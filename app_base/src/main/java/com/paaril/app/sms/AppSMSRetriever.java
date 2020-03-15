package com.paaril.app.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.paaril.app.ui.UIHelper;

public class AppSMSRetriever extends BroadcastReceiver {

  public void onReceive(Context context,
                        Intent intent) {
    
    if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
      Bundle extras = intent.getExtras();
      Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
      switch(status.getStatusCode()) {
        case CommonStatusCodes.SUCCESS:
          // Get SMS message contents
          String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
          Intent smsIntent = new Intent("otp");
          smsIntent.putExtra("message",
                  message);
          LocalBroadcastManager.getInstance(context).sendBroadcast(smsIntent);
          break;
        case CommonStatusCodes.TIMEOUT:
          UIHelper.toast(context, "Unable to read the sms");
          Log.e("SMS Timedout", "Unable to read the sms");
          break;
      }
    }

  }


  public static void start(final Context context) {
    SmsRetrieverClient client = SmsRetriever.getClient(context);

    // Starts SmsRetriever, which waits for ONE matching SMS message until
    // timeout
    // (5 minutes). The matching SMS message will be sent via a Broadcast Intent
    // with
    // action SmsRetriever#SMS_RETRIEVED_ACTION.
    Task<Void> task = client.startSmsRetriever();

    task.addOnSuccessListener(new OnSuccessListener<Void>() {
      @Override
      public void onSuccess(Void aVoid) {

      }
    });

    task.addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {

        UIHelper.toast(context, e.getMessage());
      }
    });

  }
}
