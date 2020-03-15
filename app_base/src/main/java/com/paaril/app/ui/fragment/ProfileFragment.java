package com.paaril.app.ui.fragment;

import java.util.List;

import com.paaril.app.AppExceptionHandler;
import com.paaril.app.AppServiceRepository;
import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;
import com.paaril.app.ui.UIHelper;
import com.paaril.app.ui.listener.AppOnClickListener;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ProfileFragment extends AppFragment {

  private DomainEntity customer;

  @Override
  public void onResume() {
    super.onResume();
    setTitle("My Profile");
  }

  @Override
  public void createView() {

    createView(R.layout.fragment_profile);

  }

  public static DomainEntity getCustomer() {

    String userId = AppServiceRepository.getInstance()
                                        .getAppSession()
                                        .getUserId();

    if (userId != null) {

      List<DomainEntity> customers = AppServiceRepository.getInstance()
                                                         .getWebServer()
                                                         .getDomainEntities("Customer",
                                                                            "userId="
                                                                                + userId);

      if (!customers.isEmpty()) {

        return customers.get(0);

      }
    }

    return null;
  }

  @Override
  public void executeTask() {

    customer = getCustomer();

    if (customer != null) {

      final TextView textName = contentView.findViewById(R.id.text_name);
      final TextView textEmail = contentView.findViewById(R.id.text_email);
      final TextView textMobile = contentView.findViewById(R.id.text_mobile);

      Button buttonSave = contentView.findViewById(R.id.button_save);
      buttonSave.setOnClickListener(new AppOnClickListener() {
        @Override
        public void onClickImpl(View view) {
          customer.putValue("name",
                            textName.getText().toString());
          customer.putValue("email",
                            textEmail.getText().toString());
          customer.putValue("mobile",
                            textMobile.getText().toString());

            AppServiceRepository.getInstance()
                                .getWebServer()
                                .postEntity("Customer",
                                            customer);
            UIHelper.toast(contentView.getContext(),
                           "Your profile has been saved successfully");
        }
      });
    }

  }

  @Override
  public void postExecuteTask() {


    if (customer != null) {

      final TextView textName = contentView.findViewById(R.id.text_name);
      final TextView textEmail = contentView.findViewById(R.id.text_email);
      final TextView textMobile = contentView.findViewById(R.id.text_mobile);

      textName.setText(customer.getValue("name"));
      textEmail.setText(customer.getValue("email"));
      textMobile.setText(customer.getValue("mobile"));

    }
  }
}
