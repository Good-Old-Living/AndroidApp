package com.paaril.app.ui.fragment;

import java.net.URLEncoder;
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
import com.paaril.app.ui.listener.EntityChangeListener;
import com.paaril.app.util.ObjectHelper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SubscriptionDeviationFragment extends AppFragment {

  private DomainEntity productLineItem;
  private DomainEntity subscriptionLineItem;
  private DomainEntity subscriptionDeviation;
  private boolean isNew;

  @Override
  public void onResume() {
    super.onResume();
    setTitle("My Subscription Change Schedule");
  }

  @Override
  public void createView() {

    createView(R.layout.fragment_subscription_deviation);
  }

  @Override
  public void executeTask() {

    Bundle bundle = getArguments();

    final String subscriptionLineItemId = bundle.getString(AppConstants.ARG_SUBSCRIPTION_LINE_ITEM_ID);
    subscriptionLineItem = AppServiceRepository.getInstance().getWebServer().getDomainEntity("SubscriptionLineItem",
                                                                                             subscriptionLineItemId);

    productLineItem = subscriptionLineItem.getDomainEntity("productLineItem");

    String currentDate = AppHelper.toString(new Date());

    StringBuilder filterBuilder = new StringBuilder();
    filterBuilder.append("subscriptionLineItemId=").append(subscriptionLineItemId);
    
    filterBuilder.append("&endDate=").append(URLEncoder.encode("[>=]")).append(currentDate);

    subscriptionDeviation = AppServiceRepository.getInstance().getWebServer().getFirstDomainEntity("SubscriptionDeviation",
                                                                                                   filterBuilder.toString());

    isNew = subscriptionDeviation == null;

    final EditText startDateText = contentView.findViewById(R.id.text_start_date);
    final DateView startDateView = new DateView(startDateText);

    final EditText endDateText = contentView.findViewById(R.id.text_end_date);
    final DateView endDateView = new DateView(endDateText);

    final TextView prdQtyText = contentView.findViewById(R.id.product_quantity);

    Button changeScheduleButton = contentView.findViewById(R.id.button_change_schedule);
    changeScheduleButton.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {
        try {

          String quantity = prdQtyText.getText().toString();
          if (!UIHelper.isProductQuantityValid(quantity)) {
            UIHelper.toast(getContext(),
                           "Invalid product quantity : " + quantity);
            return;
          }

          String startDate = startDateText.getText().toString();
          if (ObjectHelper.isNullorEmpty(startDate)) {
            UIHelper.toast(getContext(),
                           "Start date is required");
            return;
          }

          String endDate = endDateText.getText().toString();
          if (ObjectHelper.isNullorEmpty(endDate)) {
            endDate = startDate;
          }

          Date sDate = AppHelper.parseDate(startDate);
          Date eDate = AppHelper.parseDate(endDate);
          if (eDate.before(sDate)) {
            UIHelper.toast(getContext(),
                           "Invalid start and end date");
            return;
          }

          DomainEntity subDeviation = null;

          if (subscriptionDeviation == null) {
            subDeviation = new DomainEntity();

            subDeviation.putValue("subscriptionLineItemId",
                                  subscriptionLineItemId);
          } else {
            subDeviation = subscriptionDeviation;
          }

          subDeviation.putValue("quantity",
                                quantity);
          subDeviation.putValue("startDate",
                                AppHelper.convertToXMLDate(startDate));

          subDeviation.putValue("endDate",
                  AppHelper.convertToXMLDate(endDate));

          AppServiceRepository.getInstance().getWebServer().postEntity("SubscriptionDeviation",
                                                                       subDeviation);

          UIHelper.toast(getContext(),
                         "Your subscription has been temporarily modified");
          getActivity().onBackPressed();
        } catch (Exception e) {
          AppExceptionHandler.handle(parentActivity,
                                     e);
        }
      }

    });

  }

  @Override
  public void postExecuteTask() {

    ImageView tnImageView = contentView.findViewById(R.id.product_tn_image);
    TextView nameText = contentView.findViewById(R.id.product_name);
    nameText.setText(AppHelper.getProductTitle(productLineItem));
    AppHelper.setProductPrice(contentView,
                             productLineItem);

    AppHelper.setProductTNImage(tnImageView,
                               productLineItem);

    final EditText startDateText = contentView.findViewById(R.id.text_start_date);

    final EditText endDateText = contentView.findViewById(R.id.text_end_date);

    final TextView prdQtyText = contentView.findViewById(R.id.product_quantity);

    if (!isNew) {
      prdQtyText.setText(subscriptionDeviation.getValue("quantity"));
      startDateText.setText(subscriptionDeviation.getValue("startDate"));
      endDateText.setText(subscriptionDeviation.getValue("endDate"));
    } else {
      String prdQty = subscriptionLineItem.getValue("quantity");
      prdQtyText.setText(prdQty);
    }

  }

}
