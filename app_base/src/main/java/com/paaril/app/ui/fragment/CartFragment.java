package com.paaril.app.ui.fragment;

import com.paaril.app.AppServiceRepository;
import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;
import com.paaril.app.cart.ShoppingCart;

import com.paaril.app.ui.UIHelper;
import com.paaril.app.ui.activity.CheckoutActivity;
import com.paaril.app.ui.adapter.ProductLineItemAdapter;
import com.paaril.app.ui.component.ProductQuanityComponent;
import com.paaril.app.ui.listener.AppOnClickListener;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class CartFragment extends AppFragment implements ProductQuanityComponent.OnProductQuantityChangeListener {

  private ProductLineItemAdapter cartListAdapter;
  private DomainEntity sessionShoppingCart;

  @Override
  public void onResume() {
    super.onResume();
    setTitle("Shopping Cart");
  }

  public void executeTask() {

    String customerId = AppServiceRepository.getInstance().getAppSession().getLoggedInCustomerId();

    createProductLineItemAdapter();

    if (cartListAdapter.getCount() > 0) {

      sessionShoppingCart = AppServiceRepository.getInstance().getWebServer().getDomainEntity("SessionShoppingCart",
                                                                                              "-1");

      Button checkoutButton = contentView.findViewById(R.id.cart_checkout);
      checkoutButton.setOnClickListener(new AppOnClickListener() {
        @Override
        public void onClickImpl(View view) {

          Intent intent = null;

          if (AppServiceRepository.getInstance().getAppSession().hasUserLoggedIn()) {
            intent = new Intent(parentActivity, CheckoutActivity.class);
            startActivity(intent);
          } else {
            UIHelper.startLoginActivity(parentActivity,
                                        CheckoutActivity.class);
          }

        }
      });
    }

  }

  private void createProductLineItemAdapter() {
    cartListAdapter = new ProductLineItemAdapter(parentActivity, R.layout.view_cart_item, "ShoppingCartLineItem", null);
    cartListAdapter.setValueChangeListener(this);
  }

  @Override
  public void onProductQuantityChange(DomainEntity productLineItem,
                                      int quantity,
                                      DomainEntity userEntity) {

    if (productLineItem != null) {
      ShoppingCart.getShoppingCart().addProductLineItem(productLineItem,
                                                        quantity);

    }

    createProductLineItemAdapter();

    if (cartListAdapter.getCount() > 0) {

      sessionShoppingCart = AppServiceRepository.getInstance().getWebServer().getDomainEntity("SessionShoppingCart",
                                                                                              "-1");
    }

    postExecuteTask();
  }

  public void postExecuteTask() {

    final ListView itemListView = contentView.findViewById(R.id.cart_item_view);

    if (cartListAdapter.getCount() == 0) {
      contentView.findViewById(R.id.lo_cart_item_view).setVisibility(View.GONE);
      contentView.findViewById(R.id.cart_checkout_layout).setVisibility(View.GONE);
      contentView.findViewById(R.id.cart_no_items).setVisibility(View.VISIBLE);

    } else {
      contentView.findViewById(R.id.lo_cart_item_view).setVisibility(View.VISIBLE);
      itemListView.setAdapter(cartListAdapter);
      TextView grandTotalText = contentView.findViewById(R.id.cart_grand_total);

      StringBuilder strBuilder = new StringBuilder();
      strBuilder.append("Rs. ").append(sessionShoppingCart.getValue("grandTotal")).append(" (").append(sessionShoppingCart.getValue("itemCount")).append(")");

      grandTotalText.setText(strBuilder.toString());

    }

  }

  @Override
  public void createView() {

    createView(R.layout.fragment_cart);
  }

}
