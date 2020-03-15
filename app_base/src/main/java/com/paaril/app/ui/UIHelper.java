package com.paaril.app.ui;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.paaril.app.AppExceptionHandler;
import com.paaril.app.AppHelper;
import com.paaril.app.AppServiceRepository;
import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;
import com.paaril.app.cart.ShoppingCart;
import com.paaril.app.ui.activity.HomeActivity;
import com.paaril.app.ui.activity.LoginActivity;
import com.paaril.app.ui.component.ProductQuanityComponent;
import com.paaril.app.ui.listener.AppOnClickListener;
import com.paaril.app.util.ObjectHelper;

public class UIHelper {

  private static final String PARAM_NEXT_ACTIVITY = "nextActivity";

  public static void toast(Context context,
                           String message) {
    Toast toast = Toast.makeText(context,
                                 message,
                                 Toast.LENGTH_LONG);
    toast.setGravity(Gravity.BOTTOM,
                     0,
                     0);
    //    ViewGroup group = (ViewGroup) toast.getView();
    //    TextView messageTextView = (TextView) group.getChildAt(0);
    //    messageTextView.setTextSize(14);
    // toast.getView().setBackgroundColor(R.color.activityBackground);
    toast.show();
  }

  public static AppCompatActivity getActivity(Context context) {
    while (context instanceof ContextWrapper) {
      if (context instanceof AppCompatActivity) {
        return (AppCompatActivity) context;
      }
      context = ((ContextWrapper) context).getBaseContext();
    }
    return null;
  }

  public static void startNextActivity(AppCompatActivity activity,
                                       boolean closeActivity) {

    final Bundle bundle = activity.getIntent().getExtras();
    final String nextActivity = bundle.getString(PARAM_NEXT_ACTIVITY);

    startNextActivity(activity,
                      nextActivity,
                      closeActivity);

  }

  public static void startNextActivity(AppCompatActivity activity,
                                       String nextActivity,
                                       boolean closeActivity) {

    try {

      if (closeActivity) {
        activity.finish();
      }
      Class<?> activityClass = Class.forName(nextActivity);
      activity.startActivity(new Intent(activity, activityClass));
    } catch (ClassNotFoundException e) {
      AppExceptionHandler.handle(activity,
                                 e);

    }

  }

  public static void startLoginActivity(AppCompatActivity activity,
                                        Class<?> nextActivityClass) {
    Intent intent = new Intent(activity, LoginActivity.class);
    Bundle bundle = new Bundle();
    bundle.putString(UIHelper.PARAM_NEXT_ACTIVITY,
                     nextActivityClass.getName());
    intent.putExtras(bundle);
    activity.startActivity(intent);
  }

  public static void setFragment(AppCompatActivity activity,
                                 Fragment fragment) {

    setFragment(activity,
                fragment,
                R.id.main_fragment_container);

  }

  public static void setFragment(AppCompatActivity activity,
                                 Fragment fragment,
                                 int resourceId) {

    FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
    transaction.addToBackStack(fragment.getClass().getName());
    transaction.replace(resourceId,
                        fragment).setCustomAnimations(R.anim.fade_in,
                                                      R.anim.fade_out,
                                                      R.anim.fade_in,
                                                      R.anim.fade_out).commit();
  }

  public static void setFragmentWithNoBackStack(AppCompatActivity activity,
                                                Fragment fragment,
                                                int resourceId) {

    FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
    transaction.replace(resourceId,
                        fragment).setCustomAnimations(R.anim.fade_in,
                                                      R.anim.fade_out,
                                                      R.anim.fade_in,
                                                      R.anim.fade_out).commit();
  }

  public static void setFragmentWithNoBackStack(AppCompatActivity activity,
                                                Fragment fragment) {

    setFragmentWithNoBackStack(activity,
                               fragment,
                               R.id.main_fragment_container);

  }

  public static void increaseCount(TextView view) {

    String quantity = view.getText().toString();

    int qty = 0;

    if (isProductQuantityValid(quantity)) {

      qty += Integer.parseInt(quantity) + 1;

      view.setText(String.valueOf(qty));

    } else {

      UIHelper.toast(view.getContext(),
                     "Invalid quantity '" + quantity + "'");
    }

  }

  public static int decreaseCount(TextView view) {

    int qty = Integer.valueOf(view.getText().toString());
    qty -= 1;
    if (qty > 0) {
      view.setText(String.valueOf(qty));

    }

    return qty;
  }

  public static boolean isProductQuantityValid(String quantity) {

    int qty = 0;

    if (!ObjectHelper.isNullorEmpty(quantity)) {

      try {
        qty = Integer.parseInt(quantity);
      } catch (Exception e) {
        return false;
      }

      if (qty < 0) {
        return false;
      }

      return true;

    }
    return false;
  }

  public static void viewProductQuantity(final View itemView,
                                         final DomainEntity productLineItem) {

    boolean isPrdOrderable = AppHelper.isStrictlyTrue(productLineItem.getValue("product.orderable"));
    boolean isCatOrderable = AppHelper.isStrictlyTrue(productLineItem.getValue("product.productCategory.orderable"));

    if (isPrdOrderable && isCatOrderable) {

      String qty = productLineItem.getValue("netQuantity");
      if (qty == null || Integer.parseInt(qty) > 0) {

        itemView.findViewById(R.id.lo_quantity).setVisibility(View.VISIBLE);
        itemView.findViewById(R.id.lo_out_of_stock).setVisibility(View.GONE);

        ProductQuanityComponent pqComponent = new ProductQuanityComponent(itemView, productLineItem, null);
        pqComponent.setValueChangeListener(new ProductQuanityComponent.OnProductQuantityChangeListener() {

          @Override
          public void onProductQuantityChange(DomainEntity productLineItem,
                                              int quantity,
                                              DomainEntity userEntity) {

            ShoppingCart.getShoppingCart().addProductLineItem(productLineItem,
                                                              quantity);

          }
        });

      } else {
        itemView.findViewById(R.id.lo_quantity).setVisibility(View.GONE);
        itemView.findViewById(R.id.lo_out_of_stock).setVisibility(View.VISIBLE);

        AppOnClickListener notifyListener = new AppOnClickListener() {
          @Override
          public void onClickImpl(View view) {

            if (AppServiceRepository.getInstance().getAppSession().hasUserLoggedIn()) {

              DomainEntity prdNotification = new DomainEntity();
              prdNotification.putValue("productLineItem.id",
                                       productLineItem.getId());

              AppServiceRepository.getInstance().getWebServer().postEntity("ProductNotification",
                                                                           prdNotification);
              UIHelper.toast(view.getContext(),
                             "We will notify you, when we have this product in stock");
            } else {
              UIHelper.startLoginActivity(UIHelper.getActivity(view.getContext()),
                                          HomeActivity.class);
            }

          }

        };

        itemView.findViewById(R.id.button_notifyme).setOnClickListener(notifyListener);

      }
    }
    else {
      itemView.findViewById(R.id.lo_quantity).setVisibility(View.GONE);
    }
  }

  public static final CharSequence getHtmlText(String htmlText) {

    if (htmlText != null) {

      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        return Html.fromHtml(htmlText,
                             Html.FROM_HTML_MODE_LEGACY);
      } else {
        Html.fromHtml(htmlText);
      }

    }
    
    return null;

  }

  public static final void setHtmlText(TextView textView,
                                       String text) {

    textView.setText(getHtmlText(text));
    

  }
}
