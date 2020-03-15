package com.paaril.app.ui.fragment;

import java.util.Date;
import java.util.List;

import com.paaril.app.AppConstants;
import com.paaril.app.AppHelper;
import com.paaril.app.AppServiceRepository;
import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;

import com.paaril.app.ui.UIFragmentTransaction;
import com.paaril.app.ui.UIHelper;
import com.paaril.app.ui.adapter.EntityRecyclerViewAdapter;

import com.paaril.app.ui.adapter.EntityRecyclerViewHolder;
import com.paaril.app.ui.component.DialogBuilder;
import com.paaril.app.ui.listener.AppDialogOnClickListener;
import com.paaril.app.ui.listener.AppOnClickListener;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SubscriptionsFragment extends AppFragment {

  private EntityRecyclerViewAdapter<SubscriptionAddressViewHolder> viewAdapter;

  @Override
  public void onResume() {
    super.onResume();
    setTitle("My Subscriptions");
  }

  @Override
  public void createView() {
    createView(R.layout.fragment_subscriptions);
  }

  @Override
  public void executeTask() {

    final Button newButton = contentView.findViewById(R.id.button_new_subscription);

    newButton.setOnClickListener(new AppOnClickListener() {
      @Override
      public void onClickImpl(View view) {

        Bundle bundle = new Bundle();
        bundle.putString("productFilter",
                         "product.subscribable=Y");

        UIFragmentTransaction.newSubscriptionProductList(parentActivity,
                                                         bundle);

      }

    });

    String customerId = getLoggedInCustomerId();

    viewAdapter = new EntityRecyclerViewAdapter<>(getContext(), R.layout.view_subscriptions_list_item,
        SubscriptionAddressViewHolder.class, "Subscription", "customerId=" + customerId);

  }

  @Override
  public void postExecuteTask() {

    if (viewAdapter == null) {
      return;
    }

    final View noItemsView = contentView.findViewById(R.id.text_no_items);
    if (noItemsView != null) {

      if (viewAdapter.getItemCount() == 0) {
        noItemsView.setVisibility(View.VISIBLE);
      }

      else {
        noItemsView.setVisibility(View.GONE);

      }
    }

    final RecyclerView recyclerView = (RecyclerView) contentView.findViewById(R.id.view_subscription_addresses);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    recyclerView.setAdapter(viewAdapter);
  }

  public static class SubscriptionAddressViewHolder extends EntityRecyclerViewHolder {

    private View view;
    private LayoutInflater inflater;

    public SubscriptionAddressViewHolder(View view) {
      super(view);
      this.view = view;
      inflater = LayoutInflater.from(view.getContext());
    }

    @Override
    public void setEntity(final DomainEntity entity) {

      DomainEntity deliveryAddress = entity.getDomainEntity("deliveryAddress");

      TextView addressText = view.findViewById(R.id.text_address);

      final String deliveryAddressStr = AppHelper.getFullAddress(deliveryAddress);
      addressText.setText(AppHelper.getShortAddress(deliveryAddress));

      List<DomainEntity> lineItems = entity.getDomainEntities("lineItems");

      ViewGroup listGroup = view.findViewById(R.id.subscription_item_list_view);

      for (final DomainEntity lineItem : lineItems) {

        View subView = inflater.inflate(R.layout.view_subscriptions_list_subitem,
                                        (ViewGroup) listGroup,
                                        false);

        DomainEntity productLineItem = lineItem.getDomainEntity("productLineItem");
        String prdTitle = AppHelper.getProductTitle(productLineItem);

        TextView prdTitleText = subView.findViewById(R.id.text_product_title);
        prdTitleText.setText(prdTitle);

        TextView qtyText = subView.findViewById(R.id.text_quantity);
        qtyText.setText(lineItem.getValue("quantity"));

        TextView frequencyText = subView.findViewById(R.id.text_frequency);
        frequencyText.setText("Daily");

        List<DomainEntity> deviations = lineItem.getDomainEntities("deviations");

        DomainEntity currentDeviation = null;
        for (DomainEntity deviation : deviations) {
          Date startDate = AppHelper.parseDate(deviation.getValue("startDate"));
          Date endDate = AppHelper.parseDate(deviation.getValue("endDate"));

          Date currentDate = new Date();
          if (currentDate.compareTo(endDate) <= 0) {
            currentDeviation = deviation;
            break;
          }
        }

        if (currentDeviation != null) {

          String quantity = currentDeviation.getValue("quantity");
          String startDate = currentDeviation.getValue("startDate");
          String endDate = currentDeviation.getValue("endDate");

          StringBuilder strBuilder = new StringBuilder();
          strBuilder.append((quantity.equals("0")) ? "No supply" : "Quantity changed to " + quantity);
          if (startDate.compareTo(endDate) == 0) {
            strBuilder.append(" on " + startDate);
          } else {
            strBuilder.append(" from " + startDate + " to " + endDate);
          }

          TextView deviationText = subView.findViewById(R.id.text_deviation);
          deviationText.setText(strBuilder.toString());

        } else {
          subView.findViewById(R.id.layout_deviation).setVisibility(View.GONE);
        }

        TextView editText = subView.findViewById(R.id.text_edit);
        final TextView changeScheduleText = subView.findViewById(R.id.text_change_schedule);
        final TextView cancelText = subView.findViewById(R.id.text_cancel);
        final TextView resubscribeText = subView.findViewById(R.id.text_resubscribe);

        if (AppConstants.SUBSCRIPTION_STATE_ACTIVE.equals(lineItem.getValue("state.code"))) {
          resubscribeText.setVisibility(View.GONE);
        } else {
          changeScheduleText.setVisibility(View.GONE);
          cancelText.setVisibility(View.GONE);
          resubscribeText.setVisibility(View.VISIBLE);
        }

        editText.setOnClickListener(new AppOnClickListener() {

          @Override
          public void onClickImpl(View view) {

            Bundle bundle = new Bundle();
            bundle.putString(AppConstants.ARG_SUBSCRIPTION_ID,
                             entity.getId());
            bundle.putString(AppConstants.ARG_SUBSCRIPTION_LINE_ITEM_ID,
                             lineItem.getId());

            bundle.putString(AppConstants.ARG_SUBSCRIPTION_ADDRESS,
                             deliveryAddressStr);

            UIFragmentTransaction.newSubscription(parentActivity,
                                                                bundle);

          }

        });

        final DialogInterface.OnClickListener yesListener = new AppDialogOnClickListener(editText) {
          @Override
          public void onClickImpl(DialogInterface dialog,
                                  int which) {
            AppServiceRepository.getInstance().getWebServer().getDomainEntity("SubscriptionLineItem",
                                                                              lineItem.getId(),
                                                                              "cancel");
            resubscribeText.setVisibility(View.VISIBLE);
            changeScheduleText.setVisibility(View.GONE);
            cancelText.setVisibility(View.GONE);
            UIHelper.toast(view.getContext(),
                           "You will no longer receive this item");
          }
        };

        cancelText.setOnClickListener(new AppOnClickListener() {

          @Override
          public void onClickImpl(View view) {
            DialogBuilder.buildConfirmDialog(view.getContext(),
                                             "Do you really want to cancel this subscription?",
                                             yesListener);
          }

        });

        final DialogInterface.OnClickListener subcribeYesListener = new AppDialogOnClickListener(cancelText) {
          @Override
          public void onClickImpl(DialogInterface dialog,
                                  int which) {

            AppServiceRepository.getInstance().getWebServer().getDomainEntity("SubscriptionLineItem",
                                                                              lineItem.getId(),
                                                                              "resubscribe");
            resubscribeText.setVisibility(View.GONE);
            changeScheduleText.setVisibility(View.VISIBLE);
            cancelText.setVisibility(View.VISIBLE);
            UIHelper.toast(view.getContext(),
                           "You will start receiving this item as per the schedule");

          }
        };

        resubscribeText.setOnClickListener(new AppOnClickListener() {

          @Override
          public void onClickImpl(View view) {
            DialogBuilder.buildConfirmDialog(view.getContext(),
                                             "Do you want to resubscribe?",
                                             subcribeYesListener);
          }

        });

        changeScheduleText.setOnClickListener(new AppOnClickListener() {

          @Override
          public void onClickImpl(View view) {

            Bundle bundle = new Bundle();
            bundle.putString(AppConstants.ARG_SUBSCRIPTION_LINE_ITEM_ID,
                             lineItem.getId());

                          UIFragmentTransaction.subscriptionDeviation(parentActivity,
                                                                      bundle);

          }

        });

        listGroup.addView(subView);
      }

    }

  }

}
