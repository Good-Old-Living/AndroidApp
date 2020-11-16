package com.paaril.app.ui.fragment;

import com.paaril.app.AppExceptionHandler;
import com.paaril.app.AppServiceRepository;
import com.paaril.app.ui.AppAsyncTaskListener;
import com.paaril.app.ui.UIProgressDialog;
import com.paaril.app.ui.adapter.EntityRecyclerViewAdapter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AppFragment extends Fragment implements Runnable, AppAsyncTaskListener {

  protected AppCompatActivity parentActivity;
  protected View contentView;
  protected LayoutInflater inflater;
  private ViewGroup container;

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);
    parentActivity = (AppCompatActivity) context;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    this.inflater = inflater;
    this.container = container;

    createView();

    loadAsync();

    return contentView;
  }

  public void replace() {
    loadAsync();
  }
  
  protected void setTitle(String title) {

    if (getActivity() != null) {
      getActivity().setTitle(title);
    }
  }

  protected void createView() {

  }

  public void executeTask() {

  }

  public void postExecuteTask() {

  }

  final View createView(int viewId) {

    contentView = inflater.inflate(viewId,
                                   container,
                                   false);

    return contentView;

  }

  protected final void showProgress() {
    UIProgressDialog.showProgress(getActivity());
  }

  protected final void hideProgress() {
    UIProgressDialog.dismiss();
  }

  private void loadAsync() {
    showProgress();
    new AppAsyncTask(this).execute((Object[]) null);

  }

  public void executeOnUiThread() {

    if (getActivity() != null) {

      getActivity().runOnUiThread(this);

    }
  }

  public void run() {
    try {
      postExecuteTask();
      UIProgressDialog.dismiss();
    } catch (Exception e) {
      UIProgressDialog.dismiss();
      AppExceptionHandler.handle(parentActivity,
                                 e);
    }
  }

  protected String getLoggedInCustomerId() {
    return AppServiceRepository.getInstance().getAppSession().getLoggedInCustomerId();

  }
  
  protected final RecyclerView createRecyclerView(int viewId,
                                                  EntityRecyclerViewAdapter<?> viewAdapter) {
    final RecyclerView recyclerView = (RecyclerView) contentView.findViewById(viewId);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    viewAdapter.setActivity(parentActivity);
    recyclerView.setAdapter(viewAdapter);

    return recyclerView;
  }

  public static class AppAsyncTask extends AsyncTask<Object, String, String> {

    private AppAsyncTaskListener taskListener;

    AppAsyncTask(AppAsyncTaskListener taskListener) {
      this.taskListener = taskListener;
    }

    @Override
    protected String doInBackground(Object... arg0) {
      taskListener.executeTask();
      taskListener.executeOnUiThread();

      return null;
    }

  }
}
