package com.paaril.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.paaril.app.AppExceptionHandler;
import com.paaril.app.AppHelper;
import com.paaril.app.AppServiceRepository;
import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;
import com.paaril.app.ui.UIFragmentTransaction;
import com.paaril.app.ui.UIHelper;
import com.paaril.app.ui.fragment.ProfileFragment;
import com.paaril.app.ui.listener.AppOnClickListener;
import com.paaril.app.ui.listener.AppOnMenuItemClickListener;

public class HomeActivity extends AppActivity implements NavigationView.OnNavigationItemSelectedListener {

  private TextView welcomeText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setTitle("Categories");

    if (android.os.Build.VERSION.SDK_INT > 9) {
      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
      StrictMode.setThreadPolicy(policy);
    }

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    DrawerLayout drawer = findViewById(R.id.drawer_main);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
        R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

    try {
      setupAccountButtons(navigationView);

      UIFragmentTransaction.categoryList(this);
    } catch (Exception e) {
      AppExceptionHandler.handle(this,
                                 e);
    }
  }

  @Override
  public void onBackPressed() {
    DrawerLayout drawer = findViewById(R.id.drawer_main);

    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_toolbar,
                              menu);

    /*
     * MenuItem searchViewItem = menu.findItem(R.id.action_search); Drawable
     * searchIcon = searchViewItem.getIcon(); // change 0 with 1,2 ...
     * searchIcon.mutate();
     * searchIcon.setColorFilter(getResources().getColor(R.color.icons),
     * PorterDuff.Mode.SRC_IN);
     */

    MenuItem item = menu.findItem(R.id.action_cart);
    AppOnMenuItemClickListener mennuItemListener = new AppOnMenuItemClickListener(this) {

      @Override
      public boolean onMenuItemClickImpl(MenuItem menuItem) {
        UIFragmentTransaction.cart(HomeActivity.this);
        return false;
      }

    };
    item.setOnMenuItemClickListener(mennuItemListener);

    item = menu.findItem(R.id.action_checkout);
    item.setOnMenuItemClickListener(mennuItemListener);

    return true;//super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {

    onBackPressed();

    DrawerLayout drawer = findViewById(R.id.drawer_main);

    int id = item.getItemId();

    if (id == R.id.nav_categories) {
      UIFragmentTransaction.categoryList(this);
    } else if (id == R.id.nav_orders) {
      if (!AppHelper.loginIfRequired(this)) {
        UIFragmentTransaction.myorders(this);
      }

    } else if (id == R.id.nav_profile) {

      if (!AppHelper.loginIfRequired(this)) {
        UIFragmentTransaction.profile(this);
      }
    } else if (id == R.id.nav_wallet) {

      if (!AppHelper.loginIfRequired(this)) {
        UIFragmentTransaction.wallet(this);
      }
    } else if (id == R.id.nav_addresses) {
      if (!AppHelper.loginIfRequired(this)) {
        UIFragmentTransaction.customerAddresses(this);
      }
    } else if (id == R.id.nav_subscriptions) {
      if (!AppHelper.loginIfRequired(this)) {
        Intent intent = new Intent(this, SubscriptionsActivity.class);
        startActivity(intent);
      }
    } else if (id == R.id.nav_contactus) {
      UIFragmentTransaction.contactus(this);

    }
    return true;
  }

  private void setupAccountButtons(NavigationView navigationView) {

    View headerLayout = null;

    if (navigationView.getHeaderCount() > 0) {

      headerLayout = navigationView.getHeaderView(0);
    }

    if (headerLayout == null) {
      headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
    }

    final Button loginButton = headerLayout.findViewById(R.id.button_nav_login);
    final Button logoutButton = headerLayout.findViewById(R.id.button_nav_logout);

    loginButton.setOnClickListener(new AppOnClickListener() {
      @Override
      public void onClickImpl(View view) {

        UIHelper.startLoginActivity(HomeActivity.this,
                                    HomeActivity.class);

      }
    });

    logoutButton.setOnClickListener(new AppOnClickListener() {
      @Override
      public void onClickImpl(View view) {
        try {
          AppServiceRepository.getInstance().getAccountManager().logout(HomeActivity.this);
        } catch (Exception e) {
          AppExceptionHandler.handle(HomeActivity.this,
                                     e);
        }
        AppServiceRepository.getInstance().getAppSession().onLogout();

        loginButton.setVisibility(View.VISIBLE);

        logoutButton.setVisibility(View.GONE);
        welcomeText.setText("");
        onBackPressed();
      }
    });

    if (AppServiceRepository.getInstance().getAppSession().hasUserLoggedIn()) {

      loginButton.setVisibility(View.GONE);

    } else {

      logoutButton.setVisibility(View.GONE);

    }
    
    welcomeText = headerLayout.findViewById(R.id.text_nav_cust_name);

    final DomainEntity customer = ProfileFragment.getCustomer();
    if (customer == null || customer.getValue("name") == null) {
      welcomeText.setText("");
    } else {

      welcomeText.setText("Welcome, " + customer.getValue("name"));
    }
    
    

  }

}
