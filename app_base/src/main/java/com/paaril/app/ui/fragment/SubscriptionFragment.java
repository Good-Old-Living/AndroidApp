package com.paaril.app.ui.fragment;

import java.util.Date;
import java.util.List;

import com.paaril.app.AppConstants;
import com.paaril.app.AppExceptionHandler;
import com.paaril.app.AppHelper;
import com.paaril.app.AppServiceRepository;
import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;
import com.paaril.app.ui.UIHelper;
import com.paaril.app.ui.component.DateView;
import com.paaril.app.ui.listener.AppOnClickListener;
import com.paaril.app.util.ObjectHelper;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SubscriptionFragment extends AppFragment {

  private DomainEntity productLineItem;
  private DomainEntity subscriptionLineItem;
  private List<DomainEntity> addresses;
  private boolean isNew;

  @Override
  public void onResume() {
    super.onResume();
    setTitle("My Subscription");
  }

  @Override
  public void createView() {

    createView(R.layout.fragment_subscription);
  }

  @Override
  public void executeTask() {

    Bundle bundle = getArguments();

    final String subscriptionLineItemId = bundle.getString(AppConstants.ARG_SUBSCRIPTION_LINE_ITEM_ID);
    final String subscriptionId = bundle.getString(AppConstants.ARG_SUBSCRIPTION_ID);

    isNew = (subscriptionLineItemId == null);

    final EditText dateText = contentView.findViewById(R.id.text_start_date);
    final DateView dateView = new DateView(dateText);

    final TextView prdQtyText = contentView.findViewById(R.id.product_quantity);

    if (!isNew) {
      subscriptionLineItem = AppServiceRepository.getInstance().getWebServer().getDomainEntity("SubscriptionLineItem",
                                                                                               subscriptionLineItemId);
      productLineItem = subscriptionLineItem.getDomainEntity("productLineItem");

    } else {

      final String productLineItemId = bundle.getString("productLineItemId");

      productLineItem = AppServiceRepository.getInstance().getWebServer().getDomainEntity("ProductLineItem",
                                                                                          productLineItemId);

      final String customerId = getLoggedInCustomerId();

      addresses = AppServiceRepository.getInstance().getWebServer().getDomainEntities("CustomerAddress",
                                                                                      "customerId=" + customerId);

    }

    Button addButton = contentView.findViewById(R.id.button_quantity_add);
    final Button removeButton = contentView.findViewById(R.id.button_quantity_minus);

    addButton.setOnClickListener(new AppOnClickListener() {

      @Override
      public void onClickImpl(View view) {
        UIHelper.increaseCount(prdQtyText);
        if (removeButton.getVisibility() == View.INVISIBLE) {
          removeButton.setVisibility(View.VISIBLE);
        }
      }

    });

    removeButton.setOnClickListener(new AppOnClickListener() {

      @Override
      public void onClickImpl(View view) {
        if (UIHelper.decreaseCount(prdQtyText) == 0) {
          view.setVisibility(View.INVISIBLE);
        }
      }

    });

    final RadioGroup addressGroup = contentView.findViewById(R.id.radiogroup_del_address);

    final String customerId = getLoggedInCustomerId();

    Button subscribeButton = contentView.findViewById(R.id.button_subscribe);
    subscribeButton.setOnClickListener(new AppOnClickListener() {

      @Override
      public void onClickImpl(View view) {

        String startDate = dateText.getText().toString();
        if (ObjectHelper.isNullorEmpty(startDate)) {
          UIHelper.toast(getContext(),
                         "Start date must be provided");
          return;
        }

        if (isNew) {
          Date date = AppHelper.parseDate(startDate);
          if (date.before(new Date())) {
            UIHelper.toast(getContext(),
                           "Invalid start date. Please select a future date");
            return;
          }
        }

        startDate = AppHelper.convertToXMLDate(startDate);

        DomainEntity subsLineItem = new DomainEntity();

        if (subscriptionLineItemId == null) {

          int id = addressGroup.getCheckedRadioButtonId();
          if (id == -1) {
            AppExceptionHandler.handle(parentActivity,
                                       "Please select an address");
            return;
          }

          DomainEntity selectedAddress = addresses.get(id - 1);

          DomainEntity subscription = AppServiceRepository.getInstance().getWebServer().getFirstDomainEntity("Subscription",
                                                                                                             "customerId="
                                                                                                                 + customerId
                                                                                                                 + "&deliveryAddress.id="
                                                                                                                 + selectedAddress.getId());

          if (subscription == null) {
            subscription = new DomainEntity();
            subscription.putValue("customerId",
                                  customerId);
            subscription.putValue("deliveryAddress.id",
                                  selectedAddress.getId());

            subscription = AppServiceRepository.getInstance().getWebServer().postEntity("Subscription",
                                                                                        subscription);

          }

          subsLineItem.putValue("subscriptionId",
                                subscription.getId());

        } else {
          subsLineItem.putId(subscriptionLineItemId);
          subsLineItem.putValue("subscriptionId",
                                subscriptionId);
          subsLineItem.putValue("state.id",
                                subscriptionLineItem.getValue("state.id"));
        }

        subsLineItem.putValue("productLineItem.id",
                              productLineItem.getId());

        subsLineItem.putValue("startDate",
                              startDate);
        subsLineItem.putValue("quantity",
                              prdQtyText.getText().toString());
        subsLineItem.putValue("frequency.id",
                              "1");

        AppServiceRepository.getInstance().getWebServer().postEntity("SubscriptionLineItem",
                                                                     subsLineItem);

        UIHelper.toast(getActivity(),
                       "Your subscription has been created/modified successfully");
        getActivity().onBackPressed();

      }

    });

  }

  public void postExecuteTask() {
    final EditText dateText = contentView.findViewById(R.id.text_start_date);
    final DateView dateView = new DateView(dateText);

    final TextView prdQtyText = contentView.findViewById(R.id.product_quantity);

    if (!isNew) {
      prdQtyText.setText(subscriptionLineItem.getValue("quantity"));

      String date = subscriptionLineItem.getValue("startDate");
      date = AppHelper.convertToAndroidDate(date);
      dateText.setText(date);
      dateText.setEnabled(false);

    } else {
      prdQtyText.setText("1");
    }

    ImageView tnImageView = contentView.findViewById(R.id.product_tn_image);
    TextView nameText = contentView.findViewById(R.id.product_name);
    nameText.setText(AppHelper.getProductTitle(productLineItem));
    AppHelper.setProductPrice(contentView,
                              productLineItem);

    AppHelper.setProductTNImage(tnImageView,
                                productLineItem);

    final RadioGroup frequencyGroup = contentView.findViewById(R.id.radiogroup_sub_frequency);

    LinearLayout.LayoutParams freqGroupLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT);

    byte count = 0;

    RadioButton dailyRadio = new RadioButton(contentView.getContext());
    dailyRadio.setId(++count);
    dailyRadio.setText("Daily");
    dailyRadio.setPadding(10,
                          10,
                          10,
                          10);

    dailyRadio.setChecked(true);

    frequencyGroup.addView(dailyRadio,
                           freqGroupLayout);

    /*
     * RadioButton monToFriRadio = new RadioButton(contentView.getContext());
     * monToFriRadio.setId(++count); monToFriRadio.setText("Monday to Friday");
     * monToFriRadio.setPadding(10, 10, 10, 10);
     * 
     * frequencyGroup.addView(monToFriRadio, freqGroupLayout);
     */

    final TextView delAddressText = contentView.findViewById(R.id.text_del_address);
    if (addresses == null) {
      Bundle bundle = getArguments();
      String subscriptionAddress = bundle.getString(AppConstants.ARG_SUBSCRIPTION_ADDRESS);

      delAddressText.setText(subscriptionAddress);
    } else {
      delAddressText.setVisibility(View.GONE);
      final RadioGroup addressGroup = (RadioGroup) contentView.findViewById(R.id.radiogroup_del_address);

      if (addressGroup != null) {
        LinearLayout.LayoutParams addrLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);

        count = 0;
        for (final DomainEntity customerAddress : addresses) {
          RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.view_checkout_address,
                                                                   addressGroup,
                                                                   false);
          radioButton.setId(++count);
          radioButton.setText(AppHelper.getFullAddress(customerAddress));
          radioButton.setPadding(10,
                                 10,
                                 10,
                                 10);

          addressGroup.addView(radioButton,
                               addrLayout);

        }
      }
    }

  }

}
