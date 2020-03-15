package com.paaril.app.ui.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.paaril.app.AppException;
import com.paaril.app.AppServiceRepository;
import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class CategoryListAdapter extends BaseExpandableListAdapter {

  private Context context;

  private List<DomainEntity> categoryGroupList;
  private Map<String, List<DomainEntity>> subcategoryMap;

  public CategoryListAdapter(Context context) {
    this.context = context;

    categoryGroupList = new ArrayList<>();
    subcategoryMap = new LinkedHashMap<>();

    try {

      List<DomainEntity> categories = AppServiceRepository.getInstance().getWebServer().getDomainEntities("ProductCategory",
                                                                                                          "isActive=Y&orderBy=sortOrder");

      Map<String, DomainEntity> categoryMap = new HashMap<>(categories.size());
      for (DomainEntity category : categories) {
        categoryMap.put(category.getId(),
                        category);
      }

      for (DomainEntity category : categories) {
        DomainEntity parentEntity = categoryMap.get(category.getValue("parentId"));

        if (parentEntity != null) {
          String parentCategory = parentEntity.getValue("name");
          List<DomainEntity> subcategoryList = subcategoryMap.get(parentCategory);

          if (subcategoryList == null) {
            subcategoryList = new ArrayList<>();
            subcategoryMap.put(parentCategory,
                               subcategoryList);
          }

          subcategoryList.add(category);
        } else {
          categoryGroupList.add(category);
        }

      }

    } catch (AppException e) {
      throw e;
    } catch (Exception e) {
      throw new AppException(e.getMessage(), e);
    }

  }

  @Override
  public Object getChild(int groupPosition,
                         int childPosititon) {

    String parentCategory = categoryGroupList.get(groupPosition).getValue("name");
    return subcategoryMap.get(parentCategory).get(childPosititon);
  }

  @Override
  public long getChildId(int groupPosition,
                         int childPosition) {
    return childPosition;
  }

  @Override
  public View getChildView(int groupPosition,
                           final int childPosition,
                           boolean isLastChild,
                           View convertView,
                           ViewGroup parent) {

    final String childName = ((DomainEntity) getChild(groupPosition,
                                                      childPosition)).getValue("name");

    if (convertView == null) {
      LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = infalInflater.inflate(R.layout.view_expandable_list_item,
                                          null);
    }

    TextView txtListChild = convertView.findViewById(R.id.text_expandable_list_item);

    txtListChild.setText(childName);
    return convertView;
  }

  @Override
  public int getChildrenCount(int groupPosition) {
    String parentCategory = categoryGroupList.get(groupPosition).getValue("name");
    return subcategoryMap.get(parentCategory).size();
  }

  @Override
  public Object getGroup(int groupPosition) {
    return categoryGroupList.get(groupPosition);
  }

  @Override
  public int getGroupCount() {
    return categoryGroupList.size();
  }

  @Override
  public long getGroupId(int groupPosition) {
    return groupPosition;
  }

  @Override
  public View getGroupView(int groupPosition,
                           boolean isExpanded,
                           View convertView,
                           ViewGroup parent) {
    String headerTitle = ((DomainEntity) getGroup(groupPosition)).getValue("name");
    if (convertView == null) {
      LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = infalInflater.inflate(R.layout.view_expandable_list_group,
                                          null);
    }

    TextView titleText = convertView.findViewById(R.id.text_expandable_list_title);
    titleText.setTypeface(null,
                          Typeface.BOLD);
    titleText.setText(headerTitle);

    return convertView;
  }

  @Override
  public boolean hasStableIds() {
    return false;
  }

  @Override
  public boolean isChildSelectable(int groupPosition,
                                   int childPosition) {
    return true;
  }
}
