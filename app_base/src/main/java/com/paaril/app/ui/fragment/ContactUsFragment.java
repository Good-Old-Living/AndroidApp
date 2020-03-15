package com.paaril.app.ui.fragment;

import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;
import com.paaril.app.ui.activity.HomeActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ContactUsFragment extends AppFragment {

  @Override
  public void onResume() {
    super.onResume();
    setTitle("Contact Us");
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_contact_us,
                                 container,
                                 false);
    return view;
  }

}
