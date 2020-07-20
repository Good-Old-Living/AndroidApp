package com.paaril.app.ui.fragment;

import java.util.List;
import java.util.Random;

import com.paaril.app.AppExceptionHandler;
import com.paaril.app.AppHelper;
import com.paaril.app.AppServiceRepository;
import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;
import com.paaril.app.ui.UIFragmentTransaction;
import com.paaril.app.ui.UIHelper;
import com.paaril.app.ui.adapter.EntityRecyclerViewAdapter;
import com.paaril.app.ui.adapter.EntityRecyclerViewHolder;
import com.paaril.app.ui.component.DialogBuilder;
import com.paaril.app.ui.listener.AppOnClickListener;
import com.paaril.app.util.ObjectHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WalletFragment extends AppFragment {

  private DomainEntity customerWallet;
  private DomainEntity customer;
  private EntityRecyclerViewAdapter<WalletHistoryViewHolder> viewAdapter;
  private String amountToLoad;

  @Override
  public void onResume() {
    super.onResume();
    setTitle("My Wallet");
  }

  @Override
  public void createView() {

    createView(R.layout.fragment_wallet);

  }

  @Override
  public void executeTask() {

    customer = ProfileFragment.getCustomer();

    if (customer != null) {

      List<DomainEntity> wallets = AppServiceRepository.getInstance()
                                                       .getWebServer()
                                                       .getDomainEntities("CustomerWallet",
                                                                          "customerId=" + customer.getId());

      if (!wallets.isEmpty()) {

        customerWallet = wallets.get(0);
        viewAdapter = new EntityRecyclerViewAdapter<>(this.getContext(), R.layout.view_wallet_history_item,
            WalletHistoryViewHolder.class, "CustomerWalletHistory",
            "customerId=" + customer.getId() + "&orderBy=createdOn&isDesc=true");
        viewAdapter.setActivity(parentActivity);

      }

    }

  }

  @Override
  public void postExecuteTask() {

    String amount = customerWallet.getValue("amount");

    setAmountText(customerWallet.getValue("amount"));
    final TextView text = contentView.findViewById(R.id.text_wallet_balance);
    String message = "You have Rs. " + amount + " in your wallet";
    text.setText(message);

/*    final String transDesc = "loaded by " + customer.getValue("mobile");

    final EditText amountText = contentView.findViewById(R.id.text_wallet_amount);

    Button addButton = contentView.findViewById(R.id.button_add_money);
    addButton.setOnClickListener(new AppOnClickListener() {

      @Override
      public void onClickImpl(View view) {

        amountToLoad = amountText.getText().toString();
        if (ObjectHelper.isNullorEmpty(amountToLoad)) {
          UIHelper.toast(view.getContext(),
                         "Please provide the amount to load into wallet");
          return;
        }

        try {
          int amount = Integer.parseInt(amountToLoad);
          if (amount <= 0) {
            UIHelper.toast(view.getContext(),
                           "Please provide a positive value for the amount");
            return;
          }
        } catch (NumberFormatException nfe) {
          UIHelper.toast(view.getContext(),
                         "Please provide a numeric value for the amount");
          return;
        }

        String tr = String.valueOf(new Random().nextLong());
        android.net.Uri uri = new android.net.Uri.Builder().scheme("upi")
                                                           .authority("pay")
                                                           .appendQueryParameter("pa",
                                                                                 "kirusiva28-1@okhdfcbank")
                                                           .appendQueryParameter("pn",
                                                                                 "Good Old Living")
                                                           .appendQueryParameter("mc",
                                                                                 "5411")
                                                           .appendQueryParameter("tr",
                                                                                 tr)
                                                           .appendQueryParameter("tn",
                                                                                 transDesc)
                                                           .appendQueryParameter("am",
                                                                                 amountToLoad)
                                                           .appendQueryParameter("cu",
                                                                                 "INR")
                                                           .appendQueryParameter("url",
                                                                                 "https://www.goodoldliving.com")
                                                           .build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setPackage("com.google.android.apps.nbu.paisa.user");
        startActivityForResult(intent,
                               123);
      }
    }); */

    final RecyclerView recyclerView = contentView.findViewById(R.id.view_wallet_trans);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(viewAdapter);
  }

  private void setAmountText(String amount) {
    final TextView text = contentView.findViewById(R.id.text_wallet_balance);
    String message = "You have Rs. " + amount + " in your wallet";
    text.setText(message);

  }
/*
  @Override
  public void onActivityResult(int requestCode,
                               int resultCode,
                               Intent data) {
    super.onActivityResult(requestCode,
                           resultCode,
                           data);

    if (requestCode == 123) {

      String status = data.getStringExtra("Status");
      if ("SUCCESS".equalsIgnoreCase(status)) {

        DomainEntity walletAmount = new DomainEntity();
        walletAmount.putValue("customerId",
                              customer.getId());
        walletAmount.putValue("amount",
                              amountToLoad);

        AppServiceRepository.getInstance()
                            .getWebServer()
                            .postEntity("CustomerWalletAmount",
                                        walletAmount);
        UIHelper.toast(getContext(),
                       "Rs " + amountToLoad + " has been successfully loaded into your wallet");
        UIFragmentTransaction.wallet(parentActivity);
      } else {
        DialogBuilder.buildMessageDialog(getContext(),
                                         "Payment failed",
                                         "Unable to complete the payment process");
      }
      amountToLoad = null;
    }
  }  */

  public static class WalletHistoryViewHolder extends EntityRecyclerViewHolder {

    public WalletHistoryViewHolder(View view) {
      super(view);
    }

    @Override
    public void setEntity(final DomainEntity walletHistory) {

      StringBuilder strBuilder = new StringBuilder();

      String amountAdded = walletHistory.getValue("amountAdded");
      String amountDeducted = walletHistory.getValue("amountDeducted");
      String description = walletHistory.getValue("description");
      String createdOn = walletHistory.getValue("createdOn");

      if (!ObjectHelper.isNullorEmpty(amountAdded)) {
        strBuilder.append("Loaded Rs ").append(amountAdded).append(" on ").append(createdOn).append(AppHelper.NEW_LINE);
      } else if (!ObjectHelper.isNullorEmpty(amountDeducted)) {
        strBuilder.append("Deducted Rs ")
                  .append(amountDeducted)
                  .append(" on ")
                  .append(createdOn)
                  .append(AppHelper.NEW_LINE);
      }

      if (!ObjectHelper.isNullorEmpty(description)) {
        strBuilder.append("Ref : ").append(description).append(AppHelper.NEW_LINE);
      }

      strBuilder.append("Balance : Rs ").append(walletHistory.getValue("currAmount"));

      TextView text = itemView.findViewById(R.id.text_wallet_hist_item);
      text.setText(strBuilder.toString());
    }
  }
}
