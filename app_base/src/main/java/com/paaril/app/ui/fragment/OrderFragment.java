package com.paaril.app.ui.fragment;

import com.paaril.app.AppHelper;
import com.paaril.app.AppServiceRepository;
import com.paaril.app.DomainEntity;
import com.paaril.app.WebServer;
import com.paaril.app.base.R;
import com.paaril.app.ui.adapter.ProductLineItemAdapter;
import com.paaril.app.ui.component.DialogBuilder;
import com.paaril.app.ui.component.ProductQuanityComponent;
import com.paaril.app.ui.listener.AppOnClickListener;
import com.paaril.app.ui.listener.AppOnMenuItemClickListener;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

public class OrderFragment extends AppFragment implements ProductQuanityComponent.OnProductQuantityChangeListener {

  private String orderId;
  private String orderNumber;
  private boolean enableQuantityChanges;
  private DomainEntity salesOrder;

  private ProductLineItemAdapter productLineItemListAdapter;

  private DomainEntity salesOrderSummary;

  public static OrderFragment newInstance(DomainEntity salesOrder) {
    OrderFragment fragment = new OrderFragment();

    Bundle args = new Bundle();
    args.putString("orderId",
                   salesOrder.getId());
    args.putString("orderNumber",
                   salesOrder.getValue("orderId"));
    args.putString("orderState",
                   salesOrder.getValue("state.id"));
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle bundle = getArguments();
    this.orderId = bundle.getString("orderId");
  }

  @Override
  public void onResume() {
    super.onResume();
    setTitle("My Order");
  }

  private void createProductLineItemAdapter() {

    salesOrder = AppServiceRepository.getInstance().getWebServer().getDomainEntity("SalesOrder",
                                                                                   orderId);
    this.orderNumber = salesOrder.getValue("orderId");
    String stateId = salesOrder.getValue("state.id");
    enableQuantityChanges = "1".equals(stateId);

    productLineItemListAdapter = new ProductLineItemAdapter(parentActivity, R.layout.view_cart_item,
        "SalesOrderLineItem", "salesOrderId=" + orderId);
    productLineItemListAdapter.setValueChangeListener(this);

    if (!enableQuantityChanges) {
      productLineItemListAdapter.disableQuanityChanges();
    }

    salesOrderSummary = AppServiceRepository.getInstance().getWebServer().getDomainEntity("SalesOrderSummary",
                                                                                          orderId);
  }

  @Override
  public void onProductQuantityChange(DomainEntity productLineItem,
                                      int quantity,
                                      DomainEntity userEntity) {

    userEntity.putValue("quantity",
                        String.valueOf(quantity));

    WebServer webServer = AppServiceRepository.getInstance().getWebServer();

    if (quantity == 0) {
      webServer.deleteDomainEntity("SalesOrderLineItem",
                                   userEntity.getId());
    } else {
      webServer.postEntity("SalesOrderLineItem",
                           userEntity);
    }

    refreshItems();
  }

  private void refreshItems() {
    createProductLineItemAdapter();
    postExecuteTask();
  }

  @Override
  public void createView() {
    createView(R.layout.fragment_order);
  }

  @Override
  public void executeTask() {

    createProductLineItemAdapter();

  }

  @Override
  public void postExecuteTask() {

    TextView orderNumberText = contentView.findViewById(R.id.text_order_no);
    orderNumberText.setText(orderNumber);

    final ListView itemListView = contentView.findViewById(R.id.cart_item_view);
    itemListView.setAdapter(productLineItemListAdapter);

    TextView itemCountText = contentView.findViewById(R.id.text_item_count);
    itemCountText.setText("Items : " + salesOrderSummary.getValue("itemCount"));

    TextView grandTotalText = contentView.findViewById(R.id.text_grand_total);
    
    StringBuilder strBuilder = new StringBuilder();

    strBuilder.append("Rs. ")
              .append(salesOrderSummary.getValue("grandTotal"))
              .append(" (-")
              .append(salesOrderSummary.getValue("walletAmount"))
              .append(")");
    
    grandTotalText.setText(strBuilder.toString());

    final View ellipsisTextView = parentActivity.findViewById(R.id.text_ellipsis);

    final PopupMenu popup = new PopupMenu(parentActivity, ellipsisTextView);
    popup.getMenuInflater().inflate(R.menu.fragment_order_menu,
                                    popup.getMenu());

    ellipsisTextView.setOnClickListener(new AppOnClickListener() {

      @Override
      public void onClickImpl(final View view) {
        popup.show();
      }
    });

    Menu menu = popup.getMenu();
    MenuItem delAddressMI = menu.getItem(0);

    delAddressMI.setOnMenuItemClickListener(new AppOnMenuItemClickListener(ellipsisTextView.getContext()) {

      public boolean onMenuItemClickImpl(MenuItem menuItem) {
        DomainEntity deliveryAddress = salesOrder.getDomainEntity("deliveryAddress");
        String deliveryAddressStr = AppHelper.getFullAddress(deliveryAddress);

        DialogBuilder.buildMessageDialog(getContext(),
                                         "Delivery Address",
                                         deliveryAddressStr);
        return false;
      }
    });

    MenuItem cancelMI = menu.getItem(1);

    if (AppHelper.isSalesOrderCancelable(salesOrder)) {

      View.OnClickListener callbackListener = new AppOnClickListener() {

        public void onClickImpl(View view) {
          refreshItems();
        }
      };

      AppHelper.attachCancelSalesOrderAction(ellipsisTextView,
                                             cancelMI,
                                             salesOrder,
                                             callbackListener);

    } else {
      cancelMI.setVisible(false);
    }

  }
}
