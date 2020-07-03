package com.paaril.app.ui.fragment;

import java.util.List;

import com.paaril.app.AppExceptionHandler;
import com.paaril.app.AppHelper;
import com.paaril.app.AppServiceRepository;
import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;
import com.paaril.app.ui.UIHelper;
import com.paaril.app.ui.adapter.EntityRecyclerViewAdapter;
import com.paaril.app.ui.adapter.EntityRecyclerViewHolder;
import com.paaril.app.ui.listener.AppOnClickListener;
import com.paaril.app.util.ObjectHelper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WalletFragment extends AppFragment {

  private DomainEntity customerWallet;

  private EntityRecyclerViewAdapter<WalletHistoryViewHolder> viewAdapter;

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

    DomainEntity customer = ProfileFragment.getCustomer();

    if (customer != null) {

      List<DomainEntity> wallets = AppServiceRepository.getInstance().getWebServer().getDomainEntities("CustomerWallet",
                                                                                                       "customerId="
                                                                                                           + customer.getId());

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

    int amount = 0;
    if (customerWallet != null) {
      amount = Integer.parseInt(customerWallet.getValue("amount"));

    }
    final TextView text = contentView.findViewById(R.id.text_wallet_balance);
    String message = "You have Rs. " + amount + " in your wallet";
    text.setText(message);

    final RecyclerView recyclerView = contentView.findViewById(R.id.view_wallet_trans);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(viewAdapter);
  }

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
      } else if (!ObjectHelper.isNullorEmpty(amountAdded)) {
        strBuilder.append("Deducted Rs ").append(amountAdded).append(" on ").append(createdOn).append(AppHelper.NEW_LINE);
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
