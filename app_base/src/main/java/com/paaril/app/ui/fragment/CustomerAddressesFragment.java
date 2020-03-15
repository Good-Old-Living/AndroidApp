package com.paaril.app.ui.fragment;

import com.paaril.app.AppExceptionHandler;
import com.paaril.app.AppHelper;
import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;
import com.paaril.app.ui.UIFragmentTransaction;
import com.paaril.app.ui.adapter.EntityRecyclerViewAdapter;
import com.paaril.app.ui.adapter.EntityRecyclerViewHolder;
import com.paaril.app.ui.listener.AppOnClickListener;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class CustomerAddressesFragment extends AppFragment {

  EntityRecyclerViewAdapter<AddressViewHolder> viewAdapter;

  @Override
  public void onResume() {
    super.onResume();
    setTitle("My Addresses");
  }

  @Override
  public void createView() {

    createView(R.layout.fragment_customer_addresses);
  }

  @Override
  public void executeTask() {

    final Button newHCAddrButton = contentView.findViewById(R.id.button_new_hc_address);

    newHCAddrButton.setOnClickListener(new AppOnClickListener() {
      @Override
      public void onClickImpl(View view) {

        UIFragmentTransaction.newHousingComplexAddress(parentActivity);
      }

    });

    final Button newAddrButton = contentView.findViewById(R.id.button_new_address);

    newAddrButton.setOnClickListener(new AppOnClickListener() {
      @Override
      public void onClickImpl(View view) {

        UIFragmentTransaction.newAddress(parentActivity);

      }

    });

    String customerId = getLoggedInCustomerId();

    viewAdapter = new EntityRecyclerViewAdapter<>(this.getContext(), R.layout.view_customer_address_item,
        AddressViewHolder.class, "CustomerAddress", "customerId=" + customerId);

  }

  public void postExecuteTask() {
    createRecyclerView(R.id.view_customer_addresses,
                       viewAdapter);
  }

  public static class AddressViewHolder extends EntityRecyclerViewHolder {

    public AddressViewHolder(View view) {
      super(view);
    }

    @Override
    public void setEntity(final DomainEntity entity) {

      TextView addressText = itemView.findViewById(R.id.text_address);

      addressText.setText(AppHelper.getFullAddress(entity));
      TextView editText = (TextView) itemView.findViewById(R.id.text_edit);
      editText.setOnClickListener(new AppOnClickListener() {

        @Override
        public void onClickImpl(View view) {
          UIFragmentTransaction.editCustomerAddress(parentActivity,
                                                    entity.getId());
        }

      });
      TextView deleteText = (TextView) itemView.findViewById(R.id.text_delete);
    }

  }

}
