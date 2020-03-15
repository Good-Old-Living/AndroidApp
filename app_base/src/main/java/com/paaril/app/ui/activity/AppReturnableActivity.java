package com.paaril.app.ui.activity;

import com.paaril.app.base.R;


import android.os.Bundle;

import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;

public class AppReturnableActivity extends AppActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_returnable_activity);
    setToolbar();
  }

  protected void setToolbar() {
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    toolbar.setNavigationIcon(R.drawable.arrow_back);

    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });
  }
  
  public View attachContent(int resourceId) {
    ViewGroup layout = (ViewGroup)findViewById(R.id.main_fragment_container);
    return getLayoutInflater().inflate(resourceId,layout,true);
  }
}
