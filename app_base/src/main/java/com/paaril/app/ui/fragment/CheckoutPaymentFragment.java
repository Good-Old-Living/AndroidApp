package com.paaril.app.ui.fragment;

import com.paaril.app.AppExceptionHandler;
import com.paaril.app.AppServiceRepository;
import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;
import com.paaril.app.cart.ShoppingCart;
import com.paaril.app.ui.UIFragmentTransaction;
import com.paaril.app.ui.listener.AppOnClickListener;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

public class CheckoutPaymentFragment extends AppFragment {

  private DomainEntity customerAddress;

  public static CheckoutPaymentFragment newInstance(DomainEntity customerAddress) {
    CheckoutPaymentFragment fragment = new CheckoutPaymentFragment();

    Bundle args = new Bundle();
    args.putSerializable("customerAddress",
                         customerAddress);
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void onResume() {
    super.onResume();
    setTitle("Choose Payment");
  }

  @Override
  public void createView() {
    customerAddress = (DomainEntity) getArguments().getSerializable("customerAddress");

    View view = createView(R.layout.fragment_checkout_payment);
    final RadioGroup paymentGroup = view.findViewById(R.id.radiogroup_payment_option);
    Button codRadio = view.findViewById(R.id.radio_cod);
    codRadio.setId(1);
    codRadio.setSelected(true);
    /*
     * Button onlineTrfrRadio = (Button)
     * view.findViewById(R.id.radio_online_transfer); onlineTrfrRadio.setId(2);
     * Button upiRadio = (Button) view.findViewById(R.id.radio_upi_payment);
     * upiRadio.setId(3);
     */
    Button placeOrderButton = view.findViewById(R.id.button_place_order);
    placeOrderButton.setOnClickListener(new AppOnClickListener() {
      @Override
      public void onClickImpl(View view) {
        /* int id = paymentGroup.getCheckedRadioButtonId();
        if (id == -1) {
          AppExceptionHandler.handle(parentActivity,
                                     "Please select a payment option");
          return;
        }*/
        
        int id = 1;

        DomainEntity resultEntity = ShoppingCart.getShoppingCart().checkout(customerAddress.getId(),
                                                                            String.valueOf(id));

        UIFragmentTransaction.checkoutMessage(parentActivity,
                                              resultEntity);
        
        }

    });

  }

}
