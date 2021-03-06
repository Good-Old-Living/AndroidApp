package com.paaril.app.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;

import com.paaril.app.AppExceptionHandler;
import com.paaril.app.AppHelper;
import com.paaril.app.AppServiceRepository;
import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;
import com.paaril.app.cart.ShoppingCart;
import com.paaril.app.logging.AppLogger;
import com.paaril.app.ui.UIHelper;
import com.paaril.app.ui.component.DialogBuilder;
import com.paaril.app.ui.listener.AppDialogOnClickListener;
import com.paaril.app.util.ObjectHelper;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class RazorPayPaymentActivity extends AppActivity implements PaymentResultListener {

  protected DomainEntity salesOrder;
  private DomainEntity pgTransaction;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_razorpay);

    Checkout.preload(getApplicationContext());
    salesOrder = (DomainEntity) getIntent().getSerializableExtra("salesOrder");
    pgTransaction = (DomainEntity) getIntent().getSerializableExtra("pgTransaction");
    checkPaymentMode();
    startPayment();
  }

  public void startPayment() {
    final Activity activity = this;

    final Checkout co = new Checkout();
    DomainEntity customer = AppServiceRepository.getInstance()
                                                .getWebServer()
                                                .getDomainEntity("Customer",
                                                                 salesOrder.getValue("customerId"));
    co.setKeyID("rzp_live_CxssayBNYmb52G");
    //co.setKeyID("rzp_test_SCdWZZP1AeijOH");
    try {
      JSONObject options = new JSONObject();
      options.put("name",
                  "Good Old Living");
      options.put("description",
                  pgTransaction.getValue("transactionId"));
      options.put("image",
                  "https://goodoldliving.com/st/img/logo.png");
      options.put("currency",
                  "INR");
      options.put("amount",
                  pgTransaction.getValue("amount"));
      options.put("order_id",
                  pgTransaction.getValue("paymentOrderId"));

      JSONObject preFill = new JSONObject();
      if (!ObjectHelper.isNullorEmpty(customer.getValue("email"))) {
        preFill.put("email",
                    customer.getValue("email"));
      }
      preFill.put("contact",
                  customer.getValue("mobile"));

      options.put("prefill",
                  preFill);

      co.open(activity,
              options);

    } catch (Exception e) {
      AppExceptionHandler.handle(activity,
                                 "Unable to launch RazorPay application",
                                 e);
    }
  }

  protected void checkPaymentMode() {

  }

  @Override
  public void onPaymentSuccess(String razorpayPaymentId) {

    
    AppLogger.getLogger().logMessage("info", "RazorPaymentActivity.OnPaymentSucess - "+razorpayPaymentId);
    
    try {
      salesOrder.putValue("transactionId",
                          pgTransaction.getValue("transactionId"));
      salesOrder.putValue("paymentId",
                          pgTransaction.getValue("paymentId"));
      salesOrder.putValue("paymentOrderId",
                          pgTransaction.getValue("paymentOrderId"));
      salesOrder = ShoppingCart.getShoppingCart().checkout(salesOrder);

      //    DomainEntity soPayment = new DomainEntity();
      //    soPayment.putValue("paymentId",
      //                       razorpayPaymentId);
      //    soPayment.putValue("customerId",
      //                       salesOrder.getValue("customerId"));
      //    soPayment.putValue("paymentOrderId",
      //                       salesOrder.getValue("paymentOrderId"));
      //
      //    try {
      //      AppServiceRepository.getInstance()
      //                          .getWebServer()
      //                          .postEntity("SalesOrderPayment",
      //                                      soPayment);
      //    } catch (Exception e) {
      //
      //      AppExceptionHandler.handle(this,
      //                                 "Payment Successful, but there is an issue in updating the order",
      //                                 e);
      //      return;
      //    }
      //

      showSuccessMessage();
    } catch (Exception e) {
      AppExceptionHandler.handle(this,
                                 "We received the payment, but unable to complete your order. Please submit your order with Cash On Delivery option.",
                                 e);
    }
  }

  protected void showSuccessMessage() {
    String message = "Your order " + salesOrder.getValue(("orderId")) + " has been successfully created";
    showMessage("Success",
                message);
  }

  /**
   * The name of the function has to be
   * onPaymentError
   * Wrap your code in try catch, as shown, to ensure that this method runs correctly
   */
  @Override
  public void onPaymentError(int code,
                             String response) {

    AppExceptionHandler.handle(this,
                               "Payment process did not complete");
    this.finish();

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
                                         UIHelper.startHomeActivity(activity);
                                       }
                                     });
  }
}