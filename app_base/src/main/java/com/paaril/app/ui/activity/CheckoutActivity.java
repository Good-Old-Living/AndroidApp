package com.paaril.app.ui.activity;

import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;
import com.paaril.app.ui.UIFragmentTransaction;
import com.paaril.app.ui.listener.EntityChangeListener;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import androidx.appcompat.widget.Toolbar;

public class CheckoutActivity extends AppActivity implements EntityChangeListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_checkout);
    
    setTitle("Select address");
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    toolbar.setNavigationIcon(R.drawable.arrow_back);

    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });
    
    
    UIFragmentTransaction.checkoutAddresses(this);

  }

  public void onEntityChange(DomainEntity entity,
                              boolean isNew) {

    Intent intent = new Intent(this,
                               CheckoutActivity.class);
    startActivity(intent);

  }
}
