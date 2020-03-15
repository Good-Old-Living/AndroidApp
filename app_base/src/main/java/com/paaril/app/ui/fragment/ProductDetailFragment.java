package com.paaril.app.ui.fragment;

import com.paaril.app.AppHelper;
import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;
import com.paaril.app.ui.UIHelper;
import com.paaril.app.ui.adapter.ProductGroupListAdapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class ProductDetailFragment extends AppFragment {

  private DomainEntity productLineItem;

  public static ProductDetailFragment newInstance(DomainEntity productLineItem) {
    ProductDetailFragment fragment = new ProductDetailFragment();

    Bundle args = new Bundle();
    args.putSerializable("productLineItem",
                         productLineItem);
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void onResume() {
    super.onResume();
    setTitle("Product");
  }

  @Override
  public void createView() {

    productLineItem = (DomainEntity) getArguments().getSerializable("productLineItem");

    View view = createView(R.layout.fragment_product_detail);

    AppHelper.setProductDetails(view,
                                productLineItem);

    ImageView imageView = view.findViewById(R.id.product_image);
    AppHelper.setProductTNImage(imageView,
                                productLineItem);

    UIHelper.viewProductQuantity(view,
                                 productLineItem);

    TextView delInsText = view.findViewById(R.id.product_delivery_insts_link);
    delInsText.setVisibility(View.GONE);

    String delInsts = productLineItem.getValue("product.deliveryInstructions");

    if (delInsts != null) {
      delInsText = view.findViewById(R.id.product_delivery_insts);

      UIHelper.setHtmlText(delInsText,
                           delInsts);
    }

    TextView shortDescText = view.findViewById(R.id.product_shortDescription);

    DomainEntity product = productLineItem.getDomainEntity("product");
    String shortDesc = product.getValue("shortDescription");
    if (shortDesc != null) {
      shortDescText.setText(shortDesc);
    }

    TextView descText = view.findViewById(R.id.product_description);
    String desc = product.getValue("description");
    UIHelper.setHtmlText(descText,
                         desc);

  }

}