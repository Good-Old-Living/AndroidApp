package com.paaril.app.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.paaril.app.AppConstants;
import com.paaril.app.AppExceptionHandler;
import com.paaril.app.AppHelper;
import com.paaril.app.AppServiceRepository;
import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;
import com.paaril.app.cart.ShoppingCart;
import com.paaril.app.logging.AppLogger;
import com.paaril.app.payment.gpay.GPay;
import com.paaril.app.ui.UIHelper;
import com.paaril.app.ui.activity.AppActivity;
import com.paaril.app.ui.activity.RazorPayPaymentActivity;
import com.paaril.app.ui.component.DialogBuilder;
import com.paaril.app.ui.listener.AppDialogOnClickListener;
import com.paaril.app.ui.listener.AppOnClickListener;
import com.paaril.app.util.ObjectHelper;

import java.util.Random;

public class CheckoutPaymentFragment extends AppFragment {

  private DomainEntity customerAddress;
  private DomainEntity newSalesOrder;
  private DomainEntity sessionShoppingCart;

  private String gpayTransactionId;

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

    final DomainEntity salesOrder = new DomainEntity();
    salesOrder.putValue("deliveryAddress.id",
                        customerAddress.getId());
    String customerId = AppServiceRepository.getInstance().getAppSession().getLoggedInCustomerId();
    salesOrder.putValue("customerId",
                        customerId);

    final Button walletButton = contentView.findViewById(R.id.button_wallet);
    final Button codButton = contentView.findViewById(R.id.button_cod);
    final Button gpayButton = contentView.findViewById(R.id.button_gpay);
    final Button onlineButton = contentView.findViewById(R.id.button_online);

    final View gpayOfflineLayout = contentView.findViewById(R.id.layout_gpay_offline);

    if (grandTotal == 0) {

      TextView textView = contentView.findViewById(R.id.payment_message);
      textView.setText("Payment for your order will be fulfilled from the wallet");

      walletButton.setVisibility(View.VISIBLE);
      codButton.setVisibility(View.GONE);
      gpayButton.setVisibility(View.GONE);
      onlineButton.setVisibility(View.GONE);
      gpayOfflineLayout.setVisibility(View.GONE);

      walletButton.setOnClickListener(new AppOnClickListener() {
        @Override
        public void onClickImpl(View view) {

          createSalesOrder(AppConstants.PAYMENT_MODE_WALLET,
                           null,
                           null,
                           null);

        }
      });

    } else {

      walletButton.setVisibility(View.GONE);
      codButton.setVisibility(View.VISIBLE);
      gpayButton.setVisibility(View.VISIBLE);
      onlineButton.setVisibility(View.VISIBLE);
      gpayOfflineLayout.setVisibility(View.VISIBLE);

      codButton.setOnClickListener(new AppOnClickListener() {

        @Override
        public void onClickImpl(View view) {

          createSalesOrder(AppConstants.PAYMENT_MODE_COD,
                           null,
                           null,
                           null);

        }

      });

      gpayButton.setOnClickListener(new AppOnClickListener() {

        @Override
        public void onClickImpl(View view) {
          UIHelper.toast(parentActivity,
                         "Loadng GPay. Please wait ...");

          startGPayActivity(grandTotal);

        }
      });

      final Button gpayOfflineButton = contentView.findViewById(R.id.button_gpay_offline_submit);
      final EditText gpayTransIdText = contentView.findViewById(R.id.payment_gpay_trans_id);
      gpayOfflineButton.setOnClickListener(new AppOnClickListener() {

        @Override
        public void onClickImpl(View view) {
          String gpayTransId = gpayTransIdText.getText().toString();

          if (ObjectHelper.isNullorEmpty(gpayTransId)) {
            AppExceptionHandler.handle(parentActivity,
                                       "Please provide the last 4 digits of GPay Transaction Id");
            return;
          }

          createSalesOrder(AppConstants.PAYMENT_MODE_GPAY,
                           null,
                           gpayTransId,
                           null);

        }
      });

      onlineButton.setOnClickListener(new AppOnClickListener() {
        @Override
        public void onClickImpl(View view) {

          UIHelper.toast(view.getContext(),
                         "Loadng payment gateway. Please wait ...");

          String customerId = AppServiceRepository.getInstance().getAppSession().getLoggedInCustomerId();

          DomainEntity pgTransaction = new DomainEntity();
          pgTransaction.putValue("amount",
                                 String.valueOf(grandTotal));

          try {
            pgTransaction.putValue("sessionId",
                                   AppServiceRepository.getInstance().getAppSession().getSessionId());
            pgTransaction.putValue("customerId",
                                   AppServiceRepository.getInstance().getAppSession().getLoggedInCustomerId());
          } catch (Exception e) {
            //ignore
          }
          pgTransaction.putValue("amount",
                                 String.valueOf(grandTotal));

          pgTransaction = AppServiceRepository.getInstance()
                                              .getWebServer()
                                              .postEntity("RazorPayTransaction",
                                                          pgTransaction);

          pgTransaction.putValue("customerId",
                                 customerId);

          salesOrder.putValue("paymentMode.id",
                              String.valueOf(AppConstants.PAYMENT_MODE_ONLINE));

          startRazorPayActivity(salesOrder,
                                pgTransaction);
        }
      });
    }

  }

  private void createSalesOrder(int paymentMode,
                                String transactionId,
                                String paymentId,
                                String paymentOrderId) {
    DomainEntity salesOrder = new DomainEntity();
    salesOrder.putValue("deliveryAddress.id",
                        customerAddress.getId());
    salesOrder.putValue("paymentMode.id",
                        String.valueOf(paymentMode));
    if (transactionId != null) {
      salesOrder.putValue("transactionId",
                          transactionId);
    }

    if (paymentId != null) {
      salesOrder.putValue("paymentId",
                          paymentId);
    }

    if (paymentOrderId != null) {
      salesOrder.putValue("paymentOrderId",
                          paymentOrderId);
    }
    newSalesOrder = ShoppingCart.getShoppingCart().checkout(salesOrder);
    showPaymentSuccessMessage();
  }

  private void startGPayActivity(int amount) {

    gpayTransactionId = String.valueOf(new Random().nextInt());

    Intent gpayIntent = GPay.createGPayIntent(String.valueOf(amount),
                                              gpayTransactionId,
                                              gpayTransactionId);
    if (!UIHelper.isIntentAvailable(parentActivity,
                                    gpayIntent)) {
      AppExceptionHandler.handle(parentActivity,
                                 "GPay is not available on your phone");
      return;
    }

    AppLogger.getLogger()
             .logMessage("info",
                         "CheckoutPaymentFragnent.startGPayActivity: Transaction Id - " + gpayTransactionId
                             + ", Amount - " + amount);

    startActivityForResult(gpayIntent,
                           123);
  }

  public void startRazorPayActivity(DomainEntity salesOrder,
                                    DomainEntity pgTransaction) {
    Intent intent = new Intent(parentActivity, RazorPayPaymentActivity.class);
    Bundle bundle = new Bundle();
    bundle.putSerializable("salesOrder",
                           salesOrder);
    bundle.putSerializable("pgTransaction",
                           pgTransaction);
    intent.putExtras(bundle);
    parentActivity.startActivity(intent);
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
                                         UIHelper.startHomeActivity(activity);
                                       }
                                     });
  }

  @Override
  public void onActivityResult(int requestCode,
                               int resultCode,
                               Intent data) {
    //    super.onActivityResult(requestCode,
    //                           resultCode,
    //                           data);

    String st = null;
    if (data != null) {
      st = data.getStringExtra("Status");
    }

    AppLogger.getLogger()
             .logMessage("info",
                         "CheckoutPaymentFragnent.onActivityResult: Request Code - " + requestCode + ", Status - "
                             + st);

    if (requestCode == 123) {

      String status = data.getStringExtra("Status");
      if ("SUCCESS".equalsIgnoreCase(status)) {
        try {
          createSalesOrder(AppConstants.PAYMENT_MODE_GPAY,
                           gpayTransactionId,
                           null,
                           gpayTransactionId);
        } catch (Exception e) {
          AppExceptionHandler.handle(parentActivity,
                                     AppHelper.FAILED_PAYMENT_MESSAGE,
                                     e);
        }

      } else {

        //        AppExceptionHandler.handle(parentActivity,
        //                                   "Payment process did not complete");
      }

    }
  }
}