package com.paaril.app.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;
import com.paaril.app.ui.fragment.CartFragment;
import com.paaril.app.ui.fragment.CategoryListFragment;
import com.paaril.app.ui.fragment.CheckoutAddressesFragment;
import com.paaril.app.ui.fragment.CheckoutMessageFragment;
import com.paaril.app.ui.fragment.CheckoutPaymentFragment;
import com.paaril.app.ui.fragment.ContactUsFragment;
import com.paaril.app.ui.fragment.CustomerAddressFragment;
import com.paaril.app.ui.fragment.CustomerAddressesFragment;
import com.paaril.app.ui.fragment.OrderFragment;
import com.paaril.app.ui.fragment.OrdersFragment;
import com.paaril.app.ui.fragment.ProductDetailFragment;
import com.paaril.app.ui.fragment.ProductGroupListFragment;
import com.paaril.app.ui.fragment.ProfileFragment;
import com.paaril.app.ui.fragment.SubscriptionDeviationFragment;
import com.paaril.app.ui.fragment.SubscriptionFragment;
import com.paaril.app.ui.fragment.SubscriptionsFragment;

public class UIFragmentTransaction {

  public static void categoryList(AppCompatActivity activity) {
    CategoryListFragment categoryFragment = CategoryListFragment.newInstance();
    UIHelper.setFragmentWithNoBackStack(activity,
                                        categoryFragment);

  }

  public static void productDetail(AppCompatActivity activity,
                                   DomainEntity ProductLineItem) {
    ProductDetailFragment productFragment = ProductDetailFragment.newInstance(ProductLineItem);
    UIHelper.setFragment(activity,
                         productFragment);

  }

  public static void productList(AppCompatActivity activity,
                                 DomainEntity category) {
    ProductGroupListFragment productFragment = ProductGroupListFragment.newInstance(category);
    UIHelper.setFragment(activity,
                         productFragment);

  }

  public static void cart(AppCompatActivity activity) {
    CartFragment cartFragment = new CartFragment();
    UIHelper.setFragment(activity,
                         cartFragment);

  }

  public static void checkoutAddresses(AppCompatActivity activity) {
    CheckoutAddressesFragment addressesFragment = new CheckoutAddressesFragment();
    UIHelper.setFragmentWithNoBackStack(activity,
                                        addressesFragment,
                                        R.id.checkout_fragment_container);

  }

  public static void checkoutPayment(AppCompatActivity activity,
                                     DomainEntity customerAddress) {
    CheckoutPaymentFragment paymentFragment = CheckoutPaymentFragment.newInstance(customerAddress);
    UIHelper.setFragment(activity,
                         paymentFragment,
                         R.id.checkout_fragment_container);

  }

  public static void checkoutMessage(AppCompatActivity activity,
                                     DomainEntity salesOrder) {
    CheckoutMessageFragment messageFragment = CheckoutMessageFragment.newInstance(salesOrder);
    UIHelper.setFragment(activity,
                         messageFragment,
                         R.id.checkout_fragment_container);

  }

  public static void myorders(AppCompatActivity activity) {
    OrdersFragment ordersFragment = new OrdersFragment();
    UIHelper.setFragment(activity,
                         ordersFragment);

  }

  public static void order(AppCompatActivity activity,
                           DomainEntity salesOrder) {
    OrderFragment orderFragment = OrderFragment.newInstance(salesOrder);

    UIHelper.setFragment(activity,
                         orderFragment);

  }

  public static void profile(AppCompatActivity activity) {
    ProfileFragment profileFragment = new ProfileFragment();
    UIHelper.setFragment(activity,
                         profileFragment);

  }

  public static void customerAddresses(AppCompatActivity activity) {
    CustomerAddressesFragment addressesFragment = new CustomerAddressesFragment();
    UIHelper.setFragment(activity,
                         addressesFragment);

  }

  public static void editCustomerAddress(AppCompatActivity activity,
                                         String addressId) {
    CustomerAddressFragment addressFragment = CustomerAddressFragment.newInstance(addressId);
    UIHelper.setFragment(activity,
                         addressFragment);

  }

  public static void editCustomerAddress(AppCompatActivity activity,
                                         String addressId,
                                         int resourceId) {
    CustomerAddressFragment addressFragment = CustomerAddressFragment.newInstance(addressId);
    UIHelper.setFragment(activity,
                         addressFragment,
                         resourceId);

  }

  public static void newAddress(AppCompatActivity activity) {
    CustomerAddressFragment addressFragment = CustomerAddressFragment.newInstance(null);
    UIHelper.setFragment(activity,
                         addressFragment);

  }

  public static void newHousingComplexAddress(AppCompatActivity activity) {
    CustomerAddressFragment addressFragment = CustomerAddressFragment.newInstance(true);
    UIHelper.setFragment(activity,
                         addressFragment);

  }

  public static Fragment newAddress(AppCompatActivity activity,
                                    int resourceId) {
    CustomerAddressFragment addressFragment = CustomerAddressFragment.newInstance(false);
    UIHelper.setFragment(activity,
                         addressFragment,
                         resourceId);
    return addressFragment;
  }

  public static Fragment newHousingComplexAddress(AppCompatActivity activity,
                                                  int resourceId) {
    CustomerAddressFragment addressFragment = CustomerAddressFragment.newInstance(true);
    UIHelper.setFragment(activity,
                         addressFragment,
                         resourceId);

    return addressFragment;

  }

  public static void contactus(AppCompatActivity activity) {
    ContactUsFragment contactusFragment = new ContactUsFragment();
    UIHelper.setFragment(activity,
                         contactusFragment);

  }

  public static void subscriptions(AppCompatActivity activity) {
    SubscriptionsFragment subscriptionsFragment = new SubscriptionsFragment();
    UIHelper.setFragmentWithNoBackStack(activity,
                                        subscriptionsFragment);

  }

  public static void newSubscriptionProductList(AppCompatActivity activity,
                                                Bundle bundle) {
    ProductGroupListFragment productFragment = new ProductGroupListFragment();
    productFragment.setArguments(bundle);
    UIHelper.setFragment(activity,
                         productFragment);

  }

  public static void newSubscription(AppCompatActivity activity,
                                     Bundle bundle) {
    SubscriptionFragment subscriptionFragment = new SubscriptionFragment();
    subscriptionFragment.setArguments(bundle);
    UIHelper.setFragment(activity,
                         subscriptionFragment);

  }

  public static void subscriptionDeviation(AppCompatActivity activity,
                                           Bundle bundle) {
    SubscriptionDeviationFragment fragment = new SubscriptionDeviationFragment();
    fragment.setArguments(bundle);
    UIHelper.setFragment(activity,
                         fragment);

  }
}
