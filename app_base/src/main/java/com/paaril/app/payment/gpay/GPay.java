package com.paaril.app.payment.gpay;

import android.content.Intent;

public class GPay {

  public static Intent createGPayIntent(String amount,
                                        String transactionId,
                                        String transDesc) {

    android.net.Uri uri = new android.net.Uri.Builder().scheme("upi")
                                                       .authority("pay")
                                                       .appendQueryParameter("pa",
                                                                             "kirusiva28-1@okhdfcbank")
                                                       //            .appendQueryParameter("pa",
                                                       //                   "9880960654@hdfcbank")
                                                       .appendQueryParameter("pn",
                                                                             "Good Old Living")
                                                       .appendQueryParameter("mc",
                                                                             "5411")
                                                       .appendQueryParameter("tr",
                                                                             transactionId)
                                                       .appendQueryParameter("tn",
                                                                             transDesc)
                                                       .appendQueryParameter("am",
                                                                             amount)
                                                       .appendQueryParameter("cu",
                                                                             "INR")
                                                       .appendQueryParameter("url",
                                                                             "https://www.goodoldliving.com")
                                                       .build();
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(uri);
    intent.setPackage("com.google.android.apps.nbu.paisa.user");

    return intent;
  }
}
