package com.paaril.app.ui.fragment;

import java.util.List;

import com.paaril.app.AppExceptionHandler;
import com.paaril.app.AppHelper;
import com.paaril.app.AppServiceRepository;
import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;
import com.paaril.app.cart.ShoppingCart;
import com.paaril.app.ui.UIFragmentTransaction;
import com.paaril.app.ui.listener.AppOnClickListener;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class CheckoutAddressesFragment extends AppFragment {

  private List<DomainEntity> addresses;

  @Override
  public void onResume() {
    super.onResume();
    setTitle("Select Address");
  }

  @Override
  public void createView() {

    createView(R.layout.fragment_checkout_addresses);

  }

  @Override
  public void executeTask() {
    String customerId = AppServiceRepository.getInstance().getAppSession().getLoggedInCustomerId();

    addresses = AppServiceRepository.getInstance().getWebServer().getDomainEntities("CustomerAddress",
                                                                                    "customerId=" + customerId);

  }

  @Override
  public void postExecuteTask() {

    View view = contentView.findViewById(R.id.address_no_items);
    Button continueButton = contentView.findViewById(R.id.button_address_continue);

    if (addresses.isEmpty()) {

      view.setVisibility(View.VISIBLE);
      continueButton.setVisibility(View.GONE);

    } else {
      view.setVisibility(View.GONE);
      continueButton.setVisibility(View.VISIBLE);

      final RadioGroup addressGroup = contentView.findViewById(R.id.radiogroup_del_address);

      LinearLayout.LayoutParams addrLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
          LinearLayout.LayoutParams.WRAP_CONTENT);

      int count = 0;
      for (final DomainEntity customerAddress : addresses) {
        RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.view_checkout_address,
                                                                 addressGroup,
                                                                 false);
        radioButton.setId(++count);
        radioButton.setText(AppHelper.getFullAddress(customerAddress));
        radioButton.setPadding(10,
                               10,
                               10,
                               10);

        addressGroup.addView(radioButton,
                             addrLayout);

        view = inflater.inflate(R.layout.view_checkout_address_edit,
                                addressGroup,
                                false);
        addressGroup.addView(view,
                             addrLayout);

        TextView editText = view.findViewById(R.id.text_edit_address);
        editText.setOnClickListener(new AppOnClickListener() {

          @Override
          public void onClickImpl(View view) {
            UIFragmentTransaction.editCustomerAddress(parentActivity,
                                              customerAddress.getId(),
                                              R.id.checkout_fragment_container);
          }

        });

      }

      continueButton.setOnClickListener(new AppOnClickListener() {
        @Override
        public void onClickImpl(View view) {
          int id = addressGroup.getCheckedRadioButtonId();
          if (id == -1) {
            AppExceptionHandler.handle(parentActivity,
                                       "Please select an address");
            return;
          }

          DomainEntity selectedAddress = addresses.get(id - 1);

           UIFragmentTransaction.checkoutPayment(parentActivity,
                                                selectedAddress);

        }

      });

    }

    Button newAddressButton = contentView.findViewById(R.id.button_new_address);
    newAddressButton.setOnClickListener(new AppOnClickListener() {
      @Override
      public void onClickImpl(View view) {

        UIFragmentTransaction.newAddress(parentActivity,
                                         R.id.checkout_fragment_container);
      }
    });

    Button newHCAddressButton = contentView.findViewById(R.id.button_new_hc_address);
    newHCAddressButton.setOnClickListener(new AppOnClickListener() {
      @Override
      public void onClickImpl(View view) {

        UIFragmentTransaction.newHousingComplexAddress(parentActivity,
                                                       R.id.checkout_fragment_container);
      }
    });

  }

}
