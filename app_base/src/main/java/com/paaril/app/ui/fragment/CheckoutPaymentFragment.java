package com.paaril.app.ui.fragment;

import com.paaril.app.AppConstants;
import com.paaril.app.AppExceptionHandler;
import com.paaril.app.AppHelper;
import com.paaril.app.AppServiceRepository;
import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;
import com.paaril.app.cart.ShoppingCart;
import com.paaril.app.payment.gpay.GPay;
import com.paaril.app.ui.UIFragmentTransaction;
import com.paaril.app.ui.UIHelper;
import com.paaril.app.ui.activity.AppActivity;
import com.paaril.app.ui.activity.HomeActivity;
import com.paaril.app.ui.activity.RazorPayPaymentActivity;
import com.paaril.app.ui.component.DialogBuilder;
import com.paaril.app.ui.listener.AppDialogOnClickListener;
import com.paaril.app.ui.listener.AppOnClickListener;
import com.paaril.app.util.ObjectHelper;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class CheckoutPaymentFragment extends AppFragment {

  private DomainEntity customerAddress;
  private DomainEntity newSalesOrder;
  private DomainEntity sessionShoppingCart;

  public static CheckoutPaymentFragment newInstance(DomainEntity customerAddress) {
    CheckoutPaymentFragment fragment = new CheckoutPaymentFragment();

    Bundle args = new Bundle();
    args.putSerializable("customerAddress",
                         customerAddress);
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void onResume() {
    super.onResume();
    setTitle("Select a payment method");
  }

  @Override
  public void createView() {
    customerAddress = (DomainEntity) getArguments().getSerializable("customerAddress");
    createView(R.layout.fragment_checkout_payment);
  }

  @Override
  public void executeTask() {
    sessionShoppingCart = AppServiceRepository.getInstance()
                                              .getWebServer()
                                              .getDomainEntity("SessionShoppingCart",
                                                               "-1");
  }

  @Override
  public void postExecuteTask() {
    final int grandTotal = Integer.parseInt(sessionShoppingCart.getValue("grandTotal"));
    
    final RadioGroup paymentGroup = contentView.findViewById(R.id.radiogroup_payment_option);
    if (grandTotal == 0) {
      paymentGroup.setVisibility(View.GONE);

      TextView textView = contentView.findViewById(R.id.payment_message);
      textView.setText("Payment for your order will be fulfilled from the wallet");
    } else {
      paymentGroup.setVisibility(View.VISIBLE);
    }
    Button placeOrderButton = contentView.findViewById(R.id.button_place_order);
    placeOrderButton.setOnClickListener(new AppOnClickListener() {
      @Override
      public void onClickImpl(View view) {

        int paymentMode = AppConstants.PAYMENT_MODE_WALLET;
        int id = 0;
        if (grandTotal > 0) {
          id = paymentGroup.getCheckedRadioButtonId();

          if (id == -1) {
            AppExceptionHandler.handle(parentActivity,
                                       "Please select a payment option");
            return;
          }

          Intent gpayIntent = null;
          paymentMode = AppConstants.PAYMENT_MODE_COD;
          if (id == R.id.radio_cod) {
            paymentMode = AppConstants.PAYMENT_MODE_COD;
          } else if (id == R.id.radio_gpay) {
            paymentMode = AppConstants.PAYMENT_MODE_GPAY;
            gpayIntent = GPay.createGPayIntent("10",
                                               "1234",
                                               "test");
            if (!UIHelper.isIntentAvailable(parentActivity,
                                            gpayIntent)) {
              AppExceptionHandler.handle(parentActivity,
                                         "GPay is not avaialble on your phone");
              return;
            }

          } else if (id == R.id.radio_online) {
            paymentMode = AppConstants.PAYMENT_MODE_ONLINE;

          }
        }
        DomainEntity salesOrder = new DomainEntity();
        salesOrder.putValue("deliveryAddress.id",
                            customerAddress.getId());
        salesOrder.putValue("paymentMode.id",
                            String.valueOf(paymentMode));

        newSalesOrder = ShoppingCart.getShoppingCart().checkout(salesOrder);

        if (id == R.id.radio_gpay) {
          startGPayActivity(newSalesOrder);
        } else if (id == R.id.radio_online) {
          startRazorPayActivity(newSalesOrder);
        } else {
          showPaymentSuccessMessage();
          //UIFragmentTransaction.checkoutMessage(parentActivity,
          //                                     newSalesOrder);

        }

      }

    });

  }

  private void startGPayActivity(DomainEntity salesOrder) {
    Intent gpayIntent = GPay.createGPayIntent(salesOrder.getValue("amount"),
                                              salesOrder.getValue("transactionId"),
                                              salesOrder.getValue("orderId"));
    if (!UIHelper.isIntentAvailable(parentActivity,
                                    gpayIntent)) {
      AppExceptionHandler.handle(parentActivity,
                                 "GPay is not available on your phone");
      return;
    }

    startActivityForResult(gpayIntent,
                           123);
  }

  public void startRazorPayActivity(DomainEntity salesOrder) {
    Intent intent = new Intent(parentActivity, RazorPayPaymentActivity.class);
    Bundle bundle = new Bundle();
    bundle.putSerializable("salesOrder",
                           salesOrder);
    intent.putExtras(bundle);
    parentActivity.startActivity(intent);
  }

  private void createSalesOrderPayment() {
    DomainEntity soPayment = new DomainEntity();
    soPayment.putValue("customerId",
                       newSalesOrder.getValue("customerId"));
    soPayment.putValue("paymentOrderId",
                       newSalesOrder.getValue("paymentOrderId"));
    AppServiceRepository.getInstance()
                        .getWebServer()
                        .postEntity("SalesOrderPayment",
                                    soPayment);

    showPaymentSuccessMessage();

    //    UIFragmentTransaction.checkoutMessage(parentActivity,
    //                                          newSalesOrder);
  }

  private void showPaymentSuccessMessage() {
    String message = "Your order " + newSalesOrder.getValue(("orderId")) + " has been successfully created.";

    showMessage("Success",
                message);
  }

  private void showMessage(String title,
                           String message) {
    final AppActivity activity = (AppActivity) parentActivity;

    DialogBuilder.buildMessageDialog(activity,
                                     title,
                                     message,
                                     new AppDialogOnClickListener(
                                         contentView.findViewById(R.id.layout_payment_message)) {
                                       public void onClickImpl(DialogInterface dialog,
                                                               int which) {
                                         UIHelper.startNextActivity(activity,
                                                                    HomeActivity.class.getName(),
                                                                    true);
                                       }
                                     });
  }

  @Override
  public void onActivityResult(int requestCode,
                               int resultCode,
                               Intent data) {
    super.onActivityResult(requestCode,
                           resultCode,
                           data);

    if (requestCode == 123) {

      String status = data.getStringExtra("Status");
      if ("SUCCESS".equalsIgnoreCase(status)) {
        try {
          createSalesOrderPayment();
        } catch (Exception e) {
          AppExceptionHandler.handle(parentActivity,
                                     AppHelper.FAILED_PAYMENT_MESSAGE,
                                     e);
        }

      } else {

        showMessage("Payment Failed",
                    AppHelper.FAILED_PAYMENT_MESSAGE);

      }

    }
  }
}