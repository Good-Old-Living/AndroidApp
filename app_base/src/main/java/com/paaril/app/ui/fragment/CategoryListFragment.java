package com.paaril.app.ui.fragment;

import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;
import com.paaril.app.ui.UIFragmentTransaction;
import com.paaril.app.ui.adapter.CategoryListAdapter;
import com.paaril.app.ui.listener.AppOnChildClickListener;

import android.view.View;
import android.widget.ExpandableListView;

public class CategoryListFragment extends AppFragment {

  private CategoryListAdapter categoryListAdapter;

  public static CategoryListFragment newInstance() {
    return new CategoryListFragment();
  }

  @Override
  public void onResume() {
    super.onResume();
    setTitle("Category");
  }

  @Override
  public void createView() {

    createView(R.layout.fragment_category_list);
  }

  public void executeTask() {

    categoryListAdapter = new CategoryListAdapter(contentView.getContext());

  }

  public void postExecuteTask() {

    ExpandableListView categoryList = contentView.findViewById(R.id.list_category);
    categoryList.setAdapter(categoryListAdapter);

    int groupCount = categoryListAdapter.getGroupCount();
    for (int i = 0; i < groupCount; i++) {
      categoryList.expandGroup(i);
    }

    categoryList.setOnChildClickListener(new AppOnChildClickListener() {

      @Override
      public boolean onChildClickImpl(ExpandableListView parent,
                                      View v,
                                      int groupPosition,
                                      int childPosition,
                                      long id) {

        DomainEntity category = (DomainEntity) categoryListAdapter.getChild(groupPosition,
                                                                            childPosition);
        UIFragmentTransaction.productList(parentActivity,
                                          category);

        return false;
      }
    });

  }

}
