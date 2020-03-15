package com.paaril.app.ui.adapter;

import java.util.List;

import com.paaril.app.AppHelper;
import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;
import com.paaril.app.ui.component.ProductQuanityComponent;
import com.paaril.app.ui.component.ProductQuanityComponent.OnProductQuantityChangeListener;
import com.paaril.app.ui.listener.AppOnClickListener;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProductLineItemAdapter extends EntityListViewAdapter {

  private OnProductQuantityChangeListener valueChangeListener;
  private boolean enableQuatityChanges = true;

  public ProductLineItemAdapter(AppCompatActivity parentActivity,
                                int resource,
                                String entity,
                                String entityFilter) {
    super(parentActivity, resource, entity, entityFilter);

  }

  @Override
  public void onLoad(List<DomainEntity> entityList) {
    for (DomainEntity entity : entityList) {
      DomainEntity productLineItem = entity.getDomainEntity("productLineItem");
      Bitmap bitmap = AppHelper.loadProductTNImage(productLineItem);
      entity.setUserObject(bitmap);
    }
  }

  public void setValueChangeListener(OnProductQuantityChangeListener valueChangeListener) {
    this.valueChangeListener = valueChangeListener;
  }

  public void disableQuanityChanges() {
    enableQuatityChanges = false;
  }

  @Override
  public void setEntity(final View view,
                        final DomainEntity shoppingCartLineItem) {

    ImageView tnImageView = view.findViewById(R.id.product_tn_image);
    TextView nameText = view.findViewById(R.id.product_name);
    TextView totalPriceText = view.findViewById(R.id.text_total_item_price);
    final TextView prdQtyText = view.findViewById(R.id.product_quantity);

    final DomainEntity productLineItem = shoppingCartLineItem.getDomainEntity("productLineItem");

    Bitmap bitmap = (Bitmap) shoppingCartLineItem.getUserObject();
    tnImageView.setImageBitmap(bitmap);
//    AppHelper.setProductTNImage(tnImageView,
//                                productLineItem);

    nameText.setText(AppHelper.getProductTitle(productLineItem));
    AppHelper.setProductPrice(view,
                              productLineItem);

    prdQtyText.setText(shoppingCartLineItem.getValue("quantity"));
    totalPriceText.setText("Rs " + shoppingCartLineItem.getValue("totalPrice"));

    TextView removeItemText = view.findViewById(R.id.text_item_remove);

    if (enableQuatityChanges) {
      ProductQuanityComponent prdQtyComponent = new ProductQuanityComponent(view,
          shoppingCartLineItem.getDomainEntity("productLineItem"), shoppingCartLineItem);
      prdQtyComponent.setValueChangeListener(valueChangeListener);

      removeItemText.setOnClickListener(new AppOnClickListener() {
        @Override
        public void onClickImpl(View view) {
          /*ee
           * AppServiceRepository.getInstance() .getWebServer()
           * .deleteDomainEntity("ShoppingCartLineItem",
           * shoppingCartLineItem.getId());
           */

          valueChangeListener.onProductQuantityChange(productLineItem,
                                                      0,
                                                      shoppingCartLineItem);
          ProductLineItemAdapter.this.remove(shoppingCartLineItem);

        }
      });

    } else {
      view.findViewById(R.id.button_quantity_minus).setEnabled(false);
      view.findViewById(R.id.button_quantity_add).setEnabled(false);
      view.findViewById(R.id.product_quantity).setEnabled(false);
      removeItemText.setVisibility(View.GONE);
    }

    tnImageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        /* UIFragmentTransaction.productDetails((AppCompatActivity) view.getContext(),
                                             productLineItem);*/
      }
    });

  }

}
