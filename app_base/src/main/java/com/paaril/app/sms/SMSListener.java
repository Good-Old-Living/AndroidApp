package com.paaril.app.sms;

import com.paaril.app.AppConstants;
import com.paaril.app.AppExceptionHandler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class SMSListener extends BroadcastReceiver {

  public void onReceive(Context context,
                        Intent intent) {

    final Bundle bundle = intent.getExtras();

    if (bundle != null) {

      try {
        final Object[] pdus = (Object[]) bundle.get("pdus");

        for (Object pdu : pdus) {

          SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
          String senderId = sms.getDisplayOriginatingAddress();

          if (senderId.endsWith(AppConstants.SMS_SENDER_ID)) {

            String message = sms.getDisplayMessageBody();

            Intent smsIntent = new Intent("otp");
            smsIntent.putExtra("message",
                               message);
            LocalBroadcastManager.getInstance(context).sendBroadcast(smsIntent);
          }

        }

      } catch (Exception e) {
        AppExceptionHandler.handle(context,
                                   e);

      }

    }
  }

//  //In activity
//  
//  
//    private BroadcastReceiver receiver = new BroadcastReceiver() {
//      @Override
//      public void onReceive(Context context, Intent intent) {
//          if (intent.getAction().equalsIgnoreCase("otp")) {
//              final String message = intent.getStringExtra("message");
//               your_edittext.setText(message);
//              //Do whatever you want with the code here
//          }
//      }
//  };
//
//  
//  @Override
//  public void onResume() {
//      LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
//      super.onResume();
//  }
//   
//  @Override
//  public void onPause() {
//      super.onPause();
//      LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
//  }

//  <EditText
//  android:id="@+id/ed_otp"
//  android:layout_width="match_parent"
//  android:layout_height="wrap_content" />

}
