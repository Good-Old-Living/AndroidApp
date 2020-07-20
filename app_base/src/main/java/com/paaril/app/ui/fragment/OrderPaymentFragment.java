package com.paaril.app.ui.fragment;

import com.paaril.app.AppConstants;
import com.paaril.app.AppExceptionHandler;
import com.paaril.app.AppHelper;
import com.paaril.app.AppServiceRepository;
import com.paaril.app.DomainEntity;
import com.paaril.app.WebServer;
import com.paaril.app.base.R;
import com.paaril.app.payment.gpay.GPay;
import com.paaril.app.ui.UIHelper;
import com.paaril.app.ui.activity.AppActivity;
import com.paaril.app.ui.activity.HomeActivity;
import com.paaril.app.ui.activity.RazorPayOrderPaymentActivity;
import com.paaril.app.ui.adapter.ProductLineItemAdapter;
import com.paaril.app.ui.component.DialogBuilder;
import com.paaril.app.ui.component.ProductQuanityComponent;
import com.paaril.app.ui.listener.AppDialogOnClickListener;
import com.paaril.app.ui.listener.AppOnClickListener;
import com.paaril.app.ui.listener.AppOnMenuItemClickListener;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.TextView;

public class OrderPaymentFragment extends AppFragment {

  private String orderId;
  private String orderNumber;
  private DomainEntity salesOrder;

  public static OrderPaymentFragment newInstance(DomainEntity salesOrder) {
    OrderPaymentFragment fragment = new OrderPaymentFragment();

    Bundle args = new Bundle();
    args.putString("orderId",
                   salesOrder.getId());
    args.putString("orderNumber",
                   salesOrder.getValue("orderId"));
    args.putString("orderState",
                   salesOrder.getValue("state.id"));
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle bundle = getArguments();
    this.orderId = bundle.getString("orderId");
    this.orderNumber = bundle.getString("orderNumber");
  }

  @Override
  public void onResume() {
    super.onResume();
    setTitle("Order Payment");
  }

  @Override
  public void createView() {
    createView(R.layout.fragment_order_payment);
  }

  @Override
  public void executeTask() {

    salesOrder = AppServiceRepository.getInstance()
                                     .getWebServer()
                                     .getDomainEntity("SalesOrder",
                                                      orderId);

  }

  @Override
  public void postExecuteTask() {

    TextView orderNumberText = contentView.findViewById(R.id.text_order_no);
    orderNumberText.setText(orderNumber);
    final RadioGroup paymentGroup = contentView.findViewById(R.id.radiogroup_payment_option);

    Button payButton = contentView.findViewById(R.id.button_pay);
    payButton.setOnClickListener(new AppOnClickListener() {
      @Override
      public void onClickImpl(View view) {
        int id = paymentGroup.getCheckedRadioButtonId();

        if (id == -1) {
          AppExceptionHandler.handle(parentActivity,
                                     "Please select a payment option");
          return;
        }

        Intent gpayIntent = null;
        int paymentMode = AppConstants.PAYMENT_MODE_GPAY;
        if (id == R.id.radio_gpay) {
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

        if (id == R.id.radio_gpay) {
          startGPayActivity();
        } else if (id == R.id.radio_online) {
          startRazorPayActivity();
        }

      }

    });

  }

  private void startGPayActivity() {
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

  public void startRazorPayActivity() {
    Intent intent = new Intent(parentActivity, RazorPayOrderPaymentActivity.class);
    Bundle bundle = new Bundle();
    bundle.putSerializable("salesOrder", salesOrder);
    intent.putExtras(bundle);
    parentActivity.startActivity(intent);
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

  private void createSalesOrderPayment() {
    DomainEntity soPayment = new DomainEntity();
    soPayment.putValue("salesOrderId",
                       salesOrder.getId());
    soPayment.putValue("paymentModeId",
                       AppConstants.PAYMENT_MODE_GPAY);
    AppServiceRepository.getInstance()
                        .getWebServer()
                        .postEntity("SalesOrderPayment",
                                    soPayment);

    showPaymentSuccessMessage();

    //    UIFragmentTransaction.checkoutMessage(parentActivity,
    //                                          newSalesOrder);
  }

  private void showPaymentSuccessMessage() {
    String message = "We received the payment for the order " + salesOrder.getValue(("orderId")) + "";

    showMessage("Success",
                message);
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
                                     "Payment pro++-cessing has failed",
                                     e);
        }

      } else {
        
        final AppActivity activity = (AppActivity) parentActivity;

        DialogBuilder.buildMessageDialog(activity,
                                         "Payment Failed",
                                         "Payment processing has failed");

      }

    }
  }
}
