package com.paaril.app.ui.fragment;

import com.paaril.app.AppHelper;
import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;
import com.paaril.app.ui.adapter.ProductGroupListAdapter;
import com.paaril.app.util.ObjectHelper;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProductGroupListFragment extends AppFragment {

  private static final String TITLE = "Products";

  private DomainEntity productCategory;

  private RecyclerView recyclerView;
  private ProductGroupListAdapter productListAdapter;

  public static ProductGroupListFragment newInstance(DomainEntity productCategory) {
    ProductGroupListFragment fragment = new ProductGroupListFragment();

    Bundle args = new Bundle();
    args.putSerializable("productCategory",
                         productCategory);
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    productCategory = (DomainEntity) getArguments().getSerializable("productCategory");
  }

  @Override
  public void onResume() {
    super.onResume();
    setTitle(TITLE);
  }

  @Override
  public void createView() {

    final View view = createView(R.layout.fragment_product_group_list);

    TextView noteText = view.findViewById(R.id.product_notes);

    if (productCategory != null) {

      String notes = productCategory.getValue("notes");
      if (!ObjectHelper.isNullorEmpty(notes)) {
        noteText.setText(notes);
      } else {
        noteText.setVisibility(View.GONE);
      }


    }

    recyclerView = view.findViewById(R.id.product_list_view);

    recyclerView.setHasFixedSize(true);
    //recyclerView.setNestedScrollingEnabled(false);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

  }

  public void postExecuteTask() {
    recyclerView.setAdapter(productListAdapter);
  }

  public void executeTask() {

    String filter = null;
    if (productCategory == null) {
      Bundle bundle = getArguments();
      filter = bundle.getString("productFilter");
    } else {
      String qualifiedName = productCategory.getValue("qualifiedName");
      //This must be done here to primarily replace &, [ and ]. 
      qualifiedName = AppHelper.encodeURLQuery("[like]" + qualifiedName);
      filter = "product.productCategory.qualifiedName=" + qualifiedName;
    }
        
    filter += "&product.isActive=Y&isActive=Y&orderBy=product.popularity DESC, sortOrder";
    filter = AppHelper.encodeSpace(filter);

    productListAdapter = ProductGroupListAdapter.newInstance(getActivity(), filter);

  }
}
