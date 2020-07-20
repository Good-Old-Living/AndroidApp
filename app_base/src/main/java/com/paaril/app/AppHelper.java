package com.paaril.app;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.paaril.app.base.R;
import com.paaril.app.ui.UIFragmentTransaction;
import com.paaril.app.ui.UIHelper;
import com.paaril.app.ui.activity.HomeActivity;
import com.paaril.app.ui.component.DialogBuilder;
import com.paaril.app.ui.listener.AppDialogOnClickListener;
import com.paaril.app.ui.listener.AppOnClickListener;
import com.paaril.app.ui.listener.AppOnMenuItemClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppHelper {

  public static final String NEW_LINE = System.getProperty("line.separator");
  public static String FAILED_PAYMENT_MESSAGE = "Order has been created, but payment process is not complete. You can go to Menu-> Orders and pay against the order, which is in Pending Payment state.";

  private static final String DATE_FORMAT = "dd-MM-yyyy";
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

  private static final String DEFAULT_PRODUCT_TN_IMAGE = "img/noimage.gif";
  

  public static boolean loginIfRequired(AppCompatActivity activity) {
    if (!AppServiceRepository.getInstance().getAppSession().hasUserLoggedIn()) {
      UIHelper.startLoginActivity(activity,
                                  HomeActivity.class);
      return true;
    } else {
      return false;
    }
  }

  public static String getProductTitle(DomainEntity productLineItem) {

    StringBuilder addressBuilder = new StringBuilder();
    addressBuilder.append(productLineItem.getValue("product.name")).append(" ").append(productLineItem.getValue("quantity")).append(" ").append(productLineItem.getValue("unitOfMeasure.value"));
    return addressBuilder.toString();
  }

  public static void setProductPrice(View view,
                                     DomainEntity productLineItem) {

    TextView mrpText = view.findViewById(R.id.product_mrp);
    TextView priceText = view.findViewById(R.id.product_price);
    TextView discountText = view.findViewById(R.id.product_discount);

    if (productLineItem.getValue("discount") != null) {
      mrpText.setText("Rs " + productLineItem.getValue("mrp"));
      mrpText.setPaintFlags(mrpText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
      priceText.setText("Rs " + productLineItem.getValue("price"));

      DomainEntity discountType = productLineItem.getDomainEntity("discountType");
      if (discountType == null) {
        discountText.setText("Rs " + productLineItem.getValue("discount") + " off");
      } else {
        discountText.setText(productLineItem.getValue("discount") + "% off");
      }

    } else {
      mrpText.setVisibility(View.GONE);
      discountText.setVisibility(View.GONE);
      priceText.setText("Rs " + productLineItem.getValue("mrp"));
    }

  }

  public static Bitmap loadImage(String imageURL,
                                 String defaultImageURL) {

    if (imageURL == null || imageURL.contains("null")) {
      imageURL = defaultImageURL;
    }

    Bitmap bitmap = null;
    try {
      bitmap = AppServiceRepository.getInstance().getWebServer().getImage(imageURL);


    } catch (Exception e) {
      bitmap = AppServiceRepository.getInstance().getWebServer().getImage("/img/noimage.gif");
    }

    return bitmap;

  }

  public static String getProductTNImageURL(DomainEntity productLineItem) {
    String tnImage = productLineItem.getValue("tnImage");

    if (tnImage == null) {

      tnImage = productLineItem.getValue("product.tnImage");

    }

    if (tnImage == null || tnImage.contains("null")) {
      tnImage = DEFAULT_PRODUCT_TN_IMAGE;
    }

    return tnImage;
  }

  public static Bitmap loadProductTNImage(DomainEntity productLineItem) {
    String imageURL = getProductTNImageURL(productLineItem);
    return loadImage(imageURL,
                     null);
  }

  public static void setImage(ImageView imageView,
                              String imageURL) {

    if (imageURL == null || imageURL.contains("null")) {
      imageURL = "img/noimage.gif";
    }

    try {
      Bitmap bitmap = AppServiceRepository.getInstance().getWebServer().getImage(imageURL);
      imageView.setImageBitmap(bitmap);

    } catch (Exception e) {
      AppExceptionHandler.handle(imageView.getContext(),
                                 e);
    }
  }

  public static void setProductTNImage(ImageView tnImageView,
                                       DomainEntity productLineItem) {

    String tnImage = productLineItem.getValue("tnImage");

    if (tnImage == null) {

      tnImage = productLineItem.getValue("product.tnImage");

    }

    setImage(tnImageView,
             tnImage);

  }

  public static void setProductDetails(View itemView,
                                       final DomainEntity productItem) {

    TextView nameText = itemView.findViewById(R.id.product_name);
    TextView categoryText = itemView.findViewById(R.id.product_type);
    TextView brandText = itemView.findViewById(R.id.product_brand);

    String productType = productItem.getValue("product.type.value");
    if (productType == null) {
      categoryText.setVisibility(View.GONE);
    } else {
      categoryText.setText(productType);
    }

    nameText.setText(AppHelper.getProductTitle(productItem));

    String brand = productItem.getValue("product.brand");
    if (brand == null) {
      brandText.setVisibility(View.GONE);
    } else {
      brandText.setText(brand);
    }

    AppHelper.setProductPrice(itemView,
                              productItem);

    String subscribable = productItem.getValue("product.subscribable");

    if (AppHelper.isStrictlyTrue(subscribable)) {

      TextView subscribeButton = itemView.findViewById(R.id.button_subscribe);
      subscribeButton.setVisibility(View.VISIBLE);
      subscribeButton.setOnClickListener(new AppOnClickListener() {

        @Override
        public void onClickImpl(View view) {

          Bundle bundle = new Bundle();
          bundle.putString("productLineItemId",
                           productItem.getId());
          UIFragmentTransaction.newSubscription((AppCompatActivity) view.getContext(),
                                                bundle);

        }

      });

    }

  }

  public static String getFullAddress(DomainEntity customerAddress) {

    StringBuilder addressBuilder = new StringBuilder();
    addressBuilder.append(customerAddress.getValue("name")).append(", ").append(customerAddress.getValue("mobile")).append(NEW_LINE);

    DomainEntity hcAddress = customerAddress.getDomainEntity("housingComplexAddress");
    DomainEntity addressEntity = null;

    if (hcAddress == null) {
      addressEntity = customerAddress.getDomainEntity("address");

    } else {

      addressEntity = hcAddress.getDomainEntity("housingComplex.address");
      addressBuilder.append(hcAddress.getValue("block")).append(" - ").append(hcAddress.getValue("number")).append(", ").append(hcAddress.getValue("housingComplex.name")).append(NEW_LINE);

    }

    addressBuilder.append(addressEntity.getValue("address")).append(NEW_LINE);
    String landmark = addressEntity.getValue("landmark");
    if (landmark != null) {
      addressBuilder.append(landmark).append(NEW_LINE);
    }
    addressBuilder.append(addressEntity.getValue("area.name")).append(NEW_LINE);
    addressBuilder.append(addressEntity.getValue("city.name")).append(NEW_LINE);
    addressBuilder.append(addressEntity.getValue("country.name")).append(NEW_LINE);
    addressBuilder.append(addressEntity.getValue("pinCode"));

    return addressBuilder.toString();
  }

  public static String getShortAddress(DomainEntity customerAddress) {

    StringBuilder addressBuilder = new StringBuilder();

    DomainEntity hcAddress = customerAddress.getDomainEntity("housingComplexAddress");
    DomainEntity addressEntity = null;

    if (hcAddress == null) {
      addressEntity = customerAddress.getDomainEntity("address");

    } else {

      addressEntity = hcAddress.getDomainEntity("housingComplex.address");
      addressBuilder.append(hcAddress.getValue("block")).append(" - ").append(hcAddress.getValue("number")).append(", ").append(hcAddress.getValue("housingComplex.name")).append(", ");

    }

    addressBuilder.append(addressEntity.getValue("address")).append(", ");
    addressBuilder.append(addressEntity.getValue("area.name"));

    return addressBuilder.toString();
  }

  public static boolean isStrictlyTrue(String booleanValue) {

    return "Y".equalsIgnoreCase(booleanValue);

  }

  public static boolean isNullOrTrue(String booleanValue) {

    return booleanValue == null || booleanValue.equalsIgnoreCase("Y");

  }

  public static boolean isDefault(DomainEntity domainEntity) {

    String isDefault = domainEntity.getValue("isDefault");

    return isDefault != null && isDefault.equalsIgnoreCase("Y");

  }

  public static boolean isSalesOrderCancelable(DomainEntity salesOrder) {
    return "1".equals(salesOrder.getValue("state.id"));
  }

  public static boolean isPendingPaymentState(DomainEntity salesOrder) {
    return AppConstants.STATE_PENDING_PAYMENT_CODE.equals(salesOrder.getValue("state.code"));
  }

  public static void attachCancelSalesOrderAction(final View parentView,
                                                  Object actionComponent,
                                                  final DomainEntity salesOrder,
                                                  final View.OnClickListener callbackListener) {

    final DialogInterface.OnClickListener cancelListener = new AppDialogOnClickListener(parentView) {

      @Override
      public void onClickImpl(DialogInterface dialog,
                              int which) {

        WebServer webServer = AppServiceRepository.getInstance().getWebServer();
        String id = salesOrder.getId();
        webServer.getDomainEntity("SalesOrder",
                                  id,
                                  "cancel");

        DialogBuilder.buildMessageDialog(parentView.getContext(),
                                         "Cancelled",
                                         "The order has been cancelled");

        callbackListener.onClick(parentView);

      }
    };

    final String title = "Do you really want to cancel this order?";

    if (actionComponent instanceof MenuItem) {
      ((MenuItem) actionComponent).setOnMenuItemClickListener(new AppOnMenuItemClickListener(parentView.getContext()) {

        @Override
        public boolean onMenuItemClickImpl(MenuItem menuItem) {
          DialogBuilder.buildConfirmDialog(parentView.getContext(),
                                           title,
                                           cancelListener);

          return false;
        }
      });
    } else if (actionComponent instanceof View) {

      ((View) actionComponent).setOnClickListener(new AppOnClickListener() {

        @Override
        public void onClickImpl(View view) {
          DialogBuilder.buildConfirmDialog(view.getContext(),
                                           title,
                                           cancelListener);
        }
      });

    }

  }

  public static Date parseDate(String dateStr) {

    if (dateStr != null) {
      try {
        return dateFormat.parse(dateStr);
      } catch (ParseException e) {
        throw new RuntimeException("Unable to parse date : " + dateStr, e);
      }
    }

    throw new RuntimeException("Unable to parse date : " + dateStr);
  }

  public static String toString(Date date) {

    return dateFormat.format(date);

  }

  public static String convertToXMLDate(String dateStr) {
    return dateStr.replaceAll("[/]",
                              "-");
  }

  public static String convertToAndroidDate(String dateStr) {
    return dateStr;
  }

  public static String encodeURLQuery(String urlQuery) {
    //TODO Use standard api
    return urlQuery.replaceAll("[&]",
                               "%26").replace("[",
                                              "%5B").replace("]",
                                                             "%5D");

  }

  public static String encodeSpace(String urlQuery) {

    return urlQuery.replace(" ",
                            "%20");

  }

}
