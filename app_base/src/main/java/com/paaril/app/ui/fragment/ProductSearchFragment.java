package com.paaril.app.ui.fragment;

import com.paaril.app.AppHelper;
import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;
import com.paaril.app.ui.UIHelper;
import com.paaril.app.ui.adapter.ProductGroupListAdapter;
import com.paaril.app.util.ObjectHelper;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProductSearchFragment extends AppFragment {

  private String query;
  private RecyclerView recyclerView;
  private ProductGroupListAdapter productListAdapter;
  private static ProductSearchFragment instance;

  public static ProductSearchFragment newInstance(String query, boolean forceNew) {

    if (forceNew || instance == null) {
      ProductSearchFragment fragment = new ProductSearchFragment();
      fragment.query = query;
      Bundle args = new Bundle();
      args.putSerializable("searchQuery",
                           query);
      fragment.setArguments(args);
      instance = fragment;
      return fragment;
    }
    else {
      instance.query = query;
      instance.replace();
    }


    return null;
  }
  

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    query = (String) getArguments().getSerializable("searchQuery");
  }

  @Override
  public void onResume() {
    super.onResume();
    setTitle("Products");
  }

  @Override
  public void createView() {

    final View view = createView(R.layout.fragment_product_search_list);

    TextView noteText = view.findViewById(R.id.product_search_notes);
    noteText.setText("Products matching your query '" + query + "'");

    recyclerView = view.findViewById(R.id.product_list_view);

    recyclerView.setHasFixedSize(true);
    //recyclerView.setNestedScrollingEnabled(false);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

  }

  @Override
  public void postExecuteTask() {
    recyclerView.setAdapter(productListAdapter);
    if (productListAdapter.isEmpty()) {
      UIHelper.toast(parentActivity,
                     "No products found");
    }
  }

  public void executeTask() {

    String likeQuery = AppHelper.encodeURLQuery("[like]" + query);
    String filter = "product.name=" + likeQuery;
    filter += "&product.isActive=Y&isActive=Y&orderBy=product.popularity DESC, sortOrder";
    filter = AppHelper.encodeSpace(filter);

    productListAdapter = ProductGroupListAdapter.newInstance(getActivity(),
                                                             filter);

  }
}
