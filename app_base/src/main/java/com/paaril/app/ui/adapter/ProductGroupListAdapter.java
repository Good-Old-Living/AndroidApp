package com.paaril.app.ui.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.paaril.app.AppHelper;
import com.paaril.app.AppServiceRepository;
import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;
import com.paaril.app.cart.ShoppingCart;
import com.paaril.app.ui.UIFragmentTransaction;
import com.paaril.app.ui.UIHelper;
import com.paaril.app.ui.component.ProductQuanityComponent;
import com.paaril.app.ui.listener.AppOnClickListener;
import com.paaril.app.ui.task.ParallelTask;
import com.paaril.app.ui.task.TNImageLoaderTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class ProductGroupListAdapter extends RecyclerView.Adapter<ProductGroupViewHolder> {

  private Context context;
  private LayoutInflater layoutInflater;
  private List<String> productIdList;
  private Map<String, List<DomainEntity>> productItemGroupMap;

  private ProductGroupListAdapter(Context context,
                                  String filter) {

    this.context = context;
    setHasStableIds(true);
    layoutInflater = LayoutInflater.from(context);

    List<DomainEntity> productList = AppServiceRepository.getInstance().getWebServer().getDomainEntities("ProductLineItem",
                                                                                                         filter);
    
    productIdList = new ArrayList<>();
    productItemGroupMap = new LinkedHashMap<>();
    Map<String, Bitmap> bitmapMap = new HashMap<>();

    for (DomainEntity productLineItem : productList) {
      DomainEntity product = productLineItem.getDomainEntity("product");
      String productId = product.getId();

      List<DomainEntity> itemGroupList = productItemGroupMap.get(productId);
      if (itemGroupList == null) {
        itemGroupList = new ArrayList<>(2);

        productItemGroupMap.put(productId,
                                itemGroupList);
        productIdList.add(productId);
      }

      //      String imageURL = AppHelper.getProductTNImageURL(productLineItem);
      //      Bitmap bitmap = bitmapMap.get(imageURL);
      //      if (bitmap == null) {
      //        bitmap = AppHelper.loadImage(imageURL,
      //                                     null);
      //        bitmapMap.put(imageURL,
      //                      bitmap);
      //      }
      //
      //      productLineItem.setUserObject(bitmap);
      itemGroupList.add(productLineItem);
    }

  }
  
  public boolean isEmpty() {
    return productIdList.isEmpty();
  }

  public static ProductGroupListAdapter newInstance(Context context,
                                                    String filter) {

    return new ProductGroupListAdapter(context, filter);
  }

  @Override
  public ProductGroupViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {

    ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.view_product_group,
                                                             parent,
                                                             false);
    return new ProductGroupViewHolder(parent.getContext(), viewGroup, layoutInflater);

  }

  @Override
  public void onBindViewHolder(@NonNull ProductGroupViewHolder viewHolder,
                               final int position) {
    String productId = productIdList.get(position);

    List<DomainEntity> productItemList = productItemGroupMap.get(productId);

    viewHolder.setViewValues(productItemList);

  }

  @Override
  public int getItemCount() {
    return productIdList.size();
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public int getItemViewType(int position) {
    return position;
  }
}

class ProductGroupViewHolder extends RecyclerView.ViewHolder
    implements ProductQuanityComponent.OnProductQuantityChangeListener {

  private Context context;
  private ViewGroup layoutView;
  private LayoutInflater layoutInflater;

  public ProductGroupViewHolder(Context context,
                                ViewGroup layoutView,
                                LayoutInflater layoutInflater) {
    super(layoutView);
    this.context = context;
    this.layoutView = layoutView;
    this.layoutInflater = layoutInflater;

  }

  @Override
  public void onProductQuantityChange(DomainEntity productLineItem,
                                      int quantity,
                                      DomainEntity userEntity) {

    ShoppingCart.getShoppingCart().addProductLineItem(productLineItem,
                                                      quantity);

  }

  public void setViewValues(List<DomainEntity> productItems) {

    final List<View> itemViewList = new ArrayList<>(productItems.size());
    List<String> qtyList = new ArrayList<>(productItems.size());
    for (final DomainEntity productItem : productItems) {

      qtyList.add(getQuantityString(productItem));

    }

    ViewGroup containerView = layoutView.findViewById(R.id.product_item_container);

    List<String> imageURLList = new ArrayList<>(productItems.size());
    List<ImageView> imageViewList = new ArrayList<>(productItems.size());

    for (final DomainEntity productItem : productItems) {

      View itemView = layoutInflater.inflate(R.layout.view_product_tn,
                                             layoutView,
                                             false);

      AppHelper.setProductDetails(itemView,
                                  productItem);

      UIHelper.viewProductQuantity(itemView,
                                   productItem);

      AppOnClickListener detailViewListener = new AppOnClickListener() {
        @Override
        public void onClickImpl(View view) {

          UIFragmentTransaction.productDetail((AppCompatActivity) context,
                                              productItem);
        }
      };

      ImageView tnImageView = itemView.findViewById(R.id.product_tn_image);
      String imageURL = AppHelper.getProductTNImageURL(productItem);
      imageURLList.add(imageURL);
      imageViewList.add(tnImageView);

      //tnImageView.setImageBitmap((Bitmap) productItem.getUserObject());

      TextView nameText = itemView.findViewById(R.id.product_name);
      tnImageView.setOnClickListener(detailViewListener);
      nameText.setOnClickListener(detailViewListener);

      //if (!AppHelper.isDefault(productItem)) {
      //  itemView.setVisibility(View.GONE);
      //}

      containerView.addView(itemView);
      itemViewList.add(itemView);
    }

    addQtySpinner(productItems,
                  itemViewList);

    TNImageLoaderTask imageLoadTask = ParallelTask.create(TNImageLoaderTask.class,
                                                          (Activity) context);

    imageLoadTask.execute(imageURLList,
                          imageViewList);
  }

  private void addQtySpinner(List<DomainEntity> productItems,
                             final List<View> itemViewList) {

    List<String> qtyList = new ArrayList<>(productItems.size());
    int pos = 0;
    int defPos = 0;
    for (final DomainEntity productItem : productItems) {

      qtyList.add(getQuantityString(productItem));
      if (AppHelper.isDefault(productItem)) {
        defPos = pos;
      } 
      
      pos++;
      
    }

    final Spinner spinnerQtyList = layoutView.findViewById(R.id.spinner_item_qty_list);
    if (qtyList.size() > 1) {

      ArrayAdapter<String> itemQtyList = new ArrayAdapter<>(layoutView.getContext(), R.layout.view_dropdown_enum_entity,
          R.id.spinner_text);

      itemQtyList.setDropDownViewResource(R.layout.view_dropdown_enum_entity);
      itemQtyList.addAll(qtyList);
      spinnerQtyList.setAdapter(itemQtyList);

    } else {

      spinnerQtyList.setVisibility(View.GONE);

    }


    spinnerQtyList.setSelection(defPos);
    spinnerQtyList.setOnItemSelectedListener(new OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> adapterView,
                                 View view,
                                 int position,
                                 long arg3) {

        int size = itemViewList.size();
        for (int i = 0; i < size; i++) {
          if (i == position) {
            itemViewList.get(i).setVisibility(View.VISIBLE);
            adapterView.setSelection(i);
          } else {
            itemViewList.get(i).setVisibility(View.GONE);
          }
        }

      }

      @Override
      public void onNothingSelected(AdapterView<?> arg0) {

      }

    });


  }

  private String getQuantityString(DomainEntity productLineItem) {

    DomainEntity uom = productLineItem.getDomainEntity("unitOfMeasure");
    StringBuilder strBuilder = new StringBuilder(10);
    strBuilder.append(productLineItem.getValue("quantity")).append(" ").append(uom.getValue("value"));
    strBuilder.append(" - Rs ").append(productLineItem.getValue("price"));

    return strBuilder.toString();

  }

}
