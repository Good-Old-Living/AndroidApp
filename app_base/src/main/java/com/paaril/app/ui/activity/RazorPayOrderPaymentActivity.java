package com.paaril.app.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;

import com.paaril.app.AppConstants;
import com.paaril.app.AppExceptionHandler;
import com.paaril.app.AppHelper;
import com.paaril.app.AppServiceRepository;
import com.paaril.app.DomainEntity;
import com.paaril.app.WebServer;
import com.paaril.app.base.R;
import com.paaril.app.ui.UIHelper;
import com.paaril.app.ui.component.DialogBuilder;
import com.paaril.app.ui.listener.AppDialogOnClickListener;
import com.paaril.app.util.ObjectHelper;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class RazorPayOrderPaymentActivity extends RazorPayPaymentActivity implements PaymentResultListener {

  protected void checkPaymentMode() {
    if (!String.valueOf(AppConstants.PAYMENT_MODE_ONLINE).equals(salesOrder.getValue("paymentMode.id"))) {
      WebServer webServer = AppServiceRepository.getInstance().getWebServer();
      String id = salesOrder.getId();
      try {
        salesOrder = webServer.getDomainEntity("SalesOrder",
                                               id,
                                               "generateRazorPayOrderId");
      } catch (Exception e) {
        AppExceptionHandler.handle(this,
                                   "Unable to process the payment",
                                   e);
      }
    }
  }

  protected void showSuccessMessage() {
    String message = "We received the payment for the order " + salesOrder.getValue(("orderId")) + "";

    showMessage("Sucess",
                message);
  }

  @Override
  public void onPaymentSuccess(String razorpayPaymentId) {

    DomainEntity soPayment = new DomainEntity();
    soPayment.putValue("salesOrderId",
                       salesOrder.getId());
    soPayment.putValue("paymentModeId",
                       String.valueOf(AppConstants.PAYMENT_MODE_ONLINE));
    soPayment.putValue("customerId",
                       salesOrder.getValue("customerId"));
    soPayment.putValue("paymentOrderId",
                       salesOrder.getValue("paymentOrderId"));

    try {
      AppServiceRepository.getInstance()
                          .getWebServer()
                          .postEntity("SalesOrderPayment",
                                      soPayment);
    } catch (Exception e) {
      e.printStackTrace();
      AppExceptionHandler.handle(this,
                                 "Payment Successful, but there is an issue in updating the order",
                                 e);
      return;
    }

    showSuccessMessage();
  }

  /**
   * The name of the function has to be
   * onPaymentError
   * Wrap your code in try catch, as shown, to ensure that this method runs correctly
   */
  @Override
  public void onPaymentError(int code,
                             String response) {

    final Activity activity = this;
    DialogBuilder.buildMessageDialog(this,
                                     "Failed",
                                     "Unable to complete the payment. " + code + ": " + response,
                                     new AppDialogOnClickListener(findViewById(R.id.layout_payment_message)) {
                                       public void onClickImpl(DialogInterface dialog,
                                                               int which) {
                                         dialog.dismiss();
                                         activity.finish();
                                       }
                                     });

  }

  private void showMessage(String title,
                           String message) {
    final AppActivity activity = this;
    DialogBuilder.buildMessageDialog(this,
                                     title,
                                     message,
                                     new AppDialogOnClickListener(findViewById(R.id.layout_payment_message)) {
                                       public void onClickImpl(DialogInterface dialog,
                                                               int which) {
                                         dialog.dismiss();
                                         UIHelper.startNextActivity(activity,
                                                                    HomeActivity.class.getName(),
                                                                    true);
                                       }
                                     });
  }
}