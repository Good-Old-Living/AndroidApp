package com.paaril.app.ui.fragment;

import java.util.List;

import com.paaril.app.AppHelper;
import com.paaril.app.AppServiceRepository;
import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;
import com.paaril.app.ui.UIFragmentTransaction;
import com.paaril.app.ui.adapter.EntityRecyclerViewAdapter;
import com.paaril.app.ui.adapter.EntityRecyclerViewHolder;
import com.paaril.app.ui.listener.AppOnClickListener;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class OrdersFragment extends AppFragment {

  private EntityRecyclerViewAdapter<OrdersViewHolder> viewAdapter;

  @Override
  public void onResume() {
    super.onResume();
    setTitle("My Orders");
  }

  @Override
  public void createView() {

    createView(R.layout.fragment_orders);
  }

  @Override
  public void executeTask() {

    String userId = AppServiceRepository.getInstance().getAppSession().getUserId();

    if (userId != null) {

      List<DomainEntity> customers = AppServiceRepository.getInstance().getWebServer().getDomainEntities("Customer",
                                                                                                         "userId="
                                                                                                             + userId);

      if (!customers.isEmpty()) {

        final DomainEntity customer = customers.get(0);
        String customerId = customer.getId();

        viewAdapter = new EntityRecyclerViewAdapter<>(this.getContext(), R.layout.view_orders_item,
            OrdersViewHolder.class, "SalesOrder", "customerId=" + customerId + "&orderBy=createdOn&isDesc=true");
        viewAdapter.setActivity(parentActivity);

      }
    }

  }

  @Override
  public void postExecuteTask() {

    View view = contentView.findViewById(R.id.no_sales_orders);
    if (viewAdapter.getItemCount() == 0) {
      view.setVisibility(View.VISIBLE);
    } else {
      view.setVisibility(View.GONE);
    }

    final RecyclerView recyclerView = contentView.findViewById(R.id.view_orders);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(viewAdapter);
  }

  public static class OrdersViewHolder extends EntityRecyclerViewHolder {

    public OrdersViewHolder(View view) {
      super(view);
    }

    @Override
    public void setEntity(final DomainEntity salesOrder) {

      StringBuilder strBuilder = new StringBuilder();
      strBuilder.append(salesOrder.getValue("orderId")).append(" placed on ").append(salesOrder.getValue("createdOn")).append(AppHelper.NEW_LINE);

      TextView orderNoText = itemView.findViewById(R.id.text_order_no);

      orderNoText.setText(strBuilder.toString());
      orderNoText.setOnClickListener(new AppOnClickListener() {

        @Override
        public void onClickImpl(View view) {
          UIFragmentTransaction.order(parentActivity,
                                      salesOrder);
        }

      });

      final TextView statusText = itemView.findViewById(R.id.text_status);
      String status = salesOrder.getValue("state.status");
      statusText.setText(status);

      View cancelButton = itemView.findViewById(R.id.button_cancel);

      if (AppHelper.isSalesOrderCancelable(salesOrder)) {

        View.OnClickListener callbackListener = new AppOnClickListener() {

          public void onClickImpl(View view) {
            statusText.setText("Cancelled By Customer");
            view.setVisibility(View.GONE);

          }
        };

        AppHelper.attachCancelSalesOrderAction(cancelButton,
                                               cancelButton,
                                               salesOrder,
                                               callbackListener);

      } else {
        cancelButton.setVisibility(View.GONE);
      }

    }

  }

}