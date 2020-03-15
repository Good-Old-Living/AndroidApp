package com.paaril.app.ui.component;

import com.paaril.app.AppException;
import com.paaril.app.AppHelper;
import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;
import com.paaril.app.cart.ShoppingCart;
import com.paaril.app.ui.UIHelper;
import com.paaril.app.ui.listener.AppOnClickListener;
import com.paaril.app.util.ObjectHelper;

import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ProductQuanityComponent {

  private View parentView;

  private Button removeButton;
  private EditText prdQtyText;

  private DomainEntity productLineItem;
  private DomainEntity userEntity;

  private OnProductQuantityChangeListener valueChangeListener;
  private int prevQty;

  public ProductQuanityComponent(View parentView,
                                 DomainEntity productLineItem,
                                 DomainEntity userEntity) {

    this.parentView = parentView;
    this.productLineItem = productLineItem;
    this.userEntity = userEntity;

    Button addButton = parentView.findViewById(R.id.button_quantity_add);
    removeButton = parentView.findViewById(R.id.button_quantity_minus);
    prdQtyText = parentView.findViewById(R.id.product_quantity);

    if (ObjectHelper.isNullorEmpty(prdQtyText.getText().toString())) {
      String qty = ShoppingCart.getShoppingCart().getQuantityText(productLineItem.getId());

      if (qty != null) {
        prdQtyText.setText(qty);
      }
    }

    String orderable = productLineItem.getValue("product.orderable");
    String categoryOrderable = productLineItem.getValue("product.productCategory.orderable");

    if (AppHelper.isNullOrTrue(categoryOrderable) && AppHelper.isNullOrTrue(orderable)) {

      addButton.setOnClickListener(getAddItemClickListener());
      removeButton.setOnClickListener(getRemoveItemClickListener());

      if (ObjectHelper.isNullorEmpty(prdQtyText.getText().toString())) {
        removeButton.setVisibility(View.GONE);
      }

    } else {
      addButton.setVisibility(View.GONE);
      removeButton.setVisibility(View.GONE);
      prdQtyText.setVisibility(View.GONE);
    }

    prevQty = getQuantity();

    final String delInsts = productLineItem.getValue("product.deliveryInstructions");
    TextView delInsText = parentView.findViewById(R.id.product_delivery_insts_link);
    if (delInsts != null && delInsText != null) {
      delInsText.setVisibility(View.VISIBLE);
      delInsText.setOnClickListener(new AppOnClickListener() {

        @Override
        public void onClickImpl(View view) {
          DialogBuilder.buildMessageDialog(view.getContext(),
                                           "Delivery Instructions",
                                           UIHelper.getHtmlText(delInsts));
        }
      });

    }

  }

  public void setValueChangeListener(OnProductQuantityChangeListener valueChangeListener) {
    this.valueChangeListener = valueChangeListener;
  }

  private int getQuantity() {

    String quantity = prdQtyText.getText().toString();

    int qty = 0;

    if (!ObjectHelper.isNullorEmpty(quantity)) {

      qty = Integer.parseInt(quantity);

      if (qty < 0) {
        throw new AppException("Invalid quantity '" + quantity + "'");
      }
    }

    return qty;
  }

  private View.OnClickListener getAddItemClickListener() {

    return new AppOnClickListener() {

      @Override
      public void onClickImpl(View view) {

        int qty = getQuantity();

        if (qty == prevQty) {

          qty += 1;
        }

        prdQtyText.setText(String.valueOf(qty));
        removeButton.setVisibility(View.VISIBLE);
        /*
         * ShoppingCart.getShoppingCart() .addProductLineItem(productLineItem,
         * qty);
         */

        prevQty = qty;

        if (valueChangeListener != null) {
          valueChangeListener.onProductQuantityChange(productLineItem,
                                                      qty,
                                                      userEntity);
        }

        UIHelper.toast(parentView.getContext(),
                       "\"" + AppHelper.getProductTitle(productLineItem) + "\" has been added to your cart");

      }

    };

  }

  private View.OnClickListener getRemoveItemClickListener() {

    return new AppOnClickListener() {
      @Override
      public void onClickImpl(View view) {

        int qty = getQuantity() - 1;

        if (qty > 0) {

          prdQtyText.setText(String.valueOf(qty));

        } else {
          qty = 0;
          prdQtyText.setText("");
          view.setVisibility(View.GONE);
        }

        prevQty = qty;

        if (valueChangeListener != null) {
          valueChangeListener.onProductQuantityChange(productLineItem,
                                                      qty,
                                                      userEntity);
        }

        UIHelper.toast(parentView.getContext(),
                       "\"" + AppHelper.getProductTitle(productLineItem) + "\" has been updated in your cart");

      }

    };
  }

  public interface OnProductQuantityChangeListener {

    void onProductQuantityChange(DomainEntity productLineItem,
                                 int quantity,
                                 DomainEntity userentity);
  }

}
