package com.paaril.app.ui.activity;

import com.paaril.app.ui.UIFragmentTransaction;

import android.os.Bundle;

public class SubscriptionsActivity extends AppReturnableActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("Subscriptions");
    UIFragmentTransaction.subscriptions(this);
  }

}
