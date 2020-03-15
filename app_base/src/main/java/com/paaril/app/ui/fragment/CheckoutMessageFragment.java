package com.paaril.app.ui.fragment;

import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;
import com.paaril.app.ui.activity.HomeActivity;
import com.paaril.app.ui.listener.AppOnClickListener;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheckoutMessageFragment extends AppFragment {

  private DomainEntity checkoutMessage;

  public static CheckoutMessageFragment newInstance(DomainEntity checkoutMessage) {
    CheckoutMessageFragment fragment = new CheckoutMessageFragment();

    Bundle args = new Bundle();
    args.putSerializable("checkoutMessage",
                         checkoutMessage);
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void onResume() {
    super.onResume();
    setTitle("Order Confirmed");
  }

  @Override
  public void createView() {

    checkoutMessage = (DomainEntity) getArguments().getSerializable("checkoutMessage");

    View view = createView(R.layout.fragment_checkout_message);
    TextView messageText = view.findViewById(R.id.text_checkout_message);

    DomainEntity salesOrder = null;
    if (checkoutMessage.getValue("message") == null) {
      salesOrder = checkoutMessage;
    } else {
      salesOrder = checkoutMessage.getDomainEntity("entity");
    }
    String orderId = salesOrder.getValue("orderId");
    if ("OrderMerged".equals(checkoutMessage.getValue("message"))) {
      messageText.setText("Your order has been merged with an existing open order " + orderId);
    } else {
      messageText.setText("Your order " + orderId
          + " has been successfully created. We will deliver it after 5PM today, if it was placed before 5PM or as per the delivery instructions.");
    }

    Button contShoppingButton = view.findViewById(R.id.button_cont_shopping);

    contShoppingButton.setOnClickListener(new AppOnClickListener() {
      @Override
      public void onClickImpl(View view) {
        Intent intent = new Intent(parentActivity, HomeActivity.class);
        startActivity(intent);
      }

    });

  }

}
