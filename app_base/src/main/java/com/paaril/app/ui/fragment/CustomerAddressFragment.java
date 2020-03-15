package com.paaril.app.ui.fragment;

import com.paaril.app.AppExceptionHandler;
import com.paaril.app.AppServiceRepository;
import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;
import com.paaril.app.ui.DropDownEntity;
import com.paaril.app.ui.UIHelper;
import com.paaril.app.ui.component.DialogBuilder;
import com.paaril.app.ui.component.DropDownList;
import com.paaril.app.ui.listener.AppOnClickListener;
import com.paaril.app.ui.listener.EntityChangeListener;
import com.paaril.app.util.ObjectHelper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class CustomerAddressFragment extends AppFragment {

  private String customerAddressId;
  private EntityChangeListener entityChangeListener;
  private boolean isHousingComplex;

  public static CustomerAddressFragment newInstance(String customerAddressId) {
    CustomerAddressFragment fragment = new CustomerAddressFragment();

    if (customerAddressId != null) {
      Bundle args = new Bundle();
      args.putString("customerAddressId",
                     customerAddressId);
      fragment.setArguments(args);
    }

    return fragment;
  }

  public static CustomerAddressFragment newInstance(boolean isHousingComplex) {
    CustomerAddressFragment instance = new CustomerAddressFragment();
    instance.isHousingComplex = isHousingComplex;
    return instance;
  }

  public void setEntityChangeListener(EntityChangeListener listener) {
    entityChangeListener = listener;
  }

  @Override
  public void onResume() {
    super.onResume();
    setTitle("My Address");
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    String customerAddressId = null;
    if (getArguments() != null) {

      customerAddressId = getArguments().getString("customerAddressId");

    }
    View view = inflater.inflate(R.layout.fragment_customer_address,
                                 container,
                                 false);

    final DomainEntity customerAddress = (customerAddressId == null) ? new DomainEntity()
        : AppServiceRepository.getInstance().getWebServer().getDomainEntity("CustomerAddress",
                                                                            customerAddressId);

    final DomainEntity hcAddress = (customerAddressId == null) ? new DomainEntity()
        : customerAddress.getDomainEntity("housingComplexAddress");

    final DomainEntity address = (customerAddressId == null) ? new DomainEntity()
        : customerAddress.getDomainEntity("address");

    View hcView = view.findViewById(R.id.layout_hc_address);
    View indView = view.findViewById(R.id.layout_ind_address);

    final TextView textName = view.findViewById(R.id.text_name);
    final TextView textAltPhone = view.findViewById(R.id.text_altphone);
    final TextView textMobile = view.findViewById(R.id.text_mobile);

    final TextView textAddress = view.findViewById(R.id.text_address);
    final TextView textLandmark = view.findViewById(R.id.text_landmark);
    final Spinner spinnerArea = view.findViewById(R.id.spinner_area);
    final Spinner spinnerCity = view.findViewById(R.id.spinner_city);
    final Spinner spinnerState = view.findViewById(R.id.spinner_state);
    final Spinner spinnerCountry = view.findViewById(R.id.spinner_country);

    final TextView textPin = view.findViewById(R.id.text_pin);

    final Spinner spinnerHousingComplex = view.findViewById(R.id.spinner_housing_complex);
    final TextView textHCBlock = view.findViewById(R.id.text_hc_block);
    final TextView textHCNumber = view.findViewById(R.id.text_hc_number);

    textName.setText(customerAddress.getValue("name"));
    textAltPhone.setText(customerAddress.getValue("altPhone"));
    textMobile.setText(customerAddress.getValue("mobile"));

    final boolean isHC = (isHousingComplex || (hcAddress != null && hcAddress.getValue("number") != null));

    if (isHC) {

      hcView.setVisibility(View.VISIBLE);
      indView.setVisibility(View.GONE);

      DomainEntity housingComplexEntity = customerAddress.getDomainEntity("housingComplexAddress.housingComplex");
      new DropDownList(spinnerHousingComplex, housingComplexEntity, "HousingComplex", null);

      textHCBlock.setText(hcAddress.getValue("block"));
      textHCNumber.setText(hcAddress.getValue("number"));

    } else {

      indView.setVisibility(View.VISIBLE);
      hcView.setVisibility(View.GONE);

      textAddress.setText(address.getValue("address"));
      textLandmark.setText(address.getValue("landmark"));
      textPin.setText(address.getValue("pinCode"));

      DomainEntity areaEntity = address.getDomainEntity("area");
      new DropDownList(spinnerArea, areaEntity, "CityArea", "cityId=1&orderBy=name");

      DomainEntity cityEntity = address.getDomainEntity("city");
      new DropDownList(spinnerCity, cityEntity, "City", null);

      DomainEntity stateEntity = address.getDomainEntity("state");
      new DropDownList(spinnerState, stateEntity, "State", null);

      DomainEntity countryEntity = address.getDomainEntity("country");
      new DropDownList(spinnerCountry, countryEntity, "Country", null);

    }

    Button buttonSave = view.findViewById(R.id.button_save);
    buttonSave.setOnClickListener(new AppOnClickListener() {
      @Override
      public void onClickImpl(View view) {

        boolean isNew = customerAddress.getId() == null;
        if (isNew) {
          String customerId = AppServiceRepository.getInstance().getAppSession().getLoggedInCustomerId();
          customerAddress.putValue("customerId",
                                   customerId);
          if (isHC) {
            customerAddress.putValue("housingComplexAddress",
                                     hcAddress);
          } else {
            customerAddress.putValue("address",
                                     address);
          }
        }

        customerAddress.putValue("name",
                                 textName.getText().toString());
        customerAddress.putValue("altPhone",
                                 textAltPhone.getText().toString());
        customerAddress.putValue("mobile",
                                 textMobile.getText().toString());

        if (isHC) {

          if (ObjectHelper.isNullorEmpty(textName.getText().toString())
              || ObjectHelper.isNullorEmpty(textMobile.getText().toString())
              || ObjectHelper.isNullorEmpty(textHCBlock.getText().toString())
              || ObjectHelper.isNullorEmpty(textHCNumber.getText().toString())) {

            AppExceptionHandler.handle(parentActivity,
                                       "Please provide values for the mandatory (*) fields");
            return;
          }

          hcAddress.putValue("housingComplex",
                             ((DropDownEntity) spinnerHousingComplex.getSelectedItem()).getDomainEntity());
          hcAddress.putValue("block",
                             textHCBlock.getText().toString());
          hcAddress.putValue("number",
                             textHCNumber.getText().toString());

        } else {
          
          if (ObjectHelper.isNullorEmpty(textName.getText().toString())
              || ObjectHelper.isNullorEmpty(textMobile.getText().toString())
              || ObjectHelper.isNullorEmpty(textAddress.getText().toString())
              || ObjectHelper.isNullorEmpty(textPin.getText().toString())) {

            AppExceptionHandler.handle(parentActivity,
                                       "Please provide values for the mandatory (*) fields");
            return;
          }
          
          address.putValue("address",
                           textAddress.getText().toString());
          address.putValue("landmark",
                           textLandmark.getText().toString());

          address.putValue("area",
                           ((DropDownEntity) spinnerArea.getSelectedItem()).getDomainEntity());
          address.putValue("city",
                           ((DropDownEntity) spinnerCity.getSelectedItem()).getDomainEntity());
          address.putValue("state",
                           ((DropDownEntity) spinnerState.getSelectedItem()).getDomainEntity());
          address.putValue("country",
                           ((DropDownEntity) spinnerCountry.getSelectedItem()).getDomainEntity());
          address.putValue("pinCode",
                           textPin.getText().toString());
        }

        AppServiceRepository.getInstance().getWebServer().postEntity("CustomerAddress",
                                                                     customerAddress);
        UIHelper.toast(view.getContext(),
                       "Your address has been saved successfully");

        if (entityChangeListener != null) {
          entityChangeListener.onEntityChange(customerAddress,
                                              isNew);
        }

        getActivity().onBackPressed();

      }
    });

    return view;
  }

}
