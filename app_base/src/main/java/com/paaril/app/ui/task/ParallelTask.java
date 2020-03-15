package com.paaril.app.ui.task;

import com.paaril.app.ui.UIProgressDialog;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class ParallelTask extends AsyncTask<Object, String, String> {

  protected Activity activity;


  ParallelTask() {

  }

  public ParallelTask(Activity activity) {
    this.activity = activity;
  }

  
  @SuppressWarnings("unchecked")
  public static <T extends ParallelTask> T create(Class<T> taskClass,
                                                  Activity activity) {

    ParallelTask task;

    try {
      task = (ParallelTask) taskClass.newInstance();
    } catch (Exception e) {
      Log.e("ParallelTask",
            e.getMessage(),
            e);
      throw new RuntimeException(e.getMessage());
    }

    task.setActivity(activity);

    return (T) task;
  }

  void setActivity(Activity activity) {
    this.activity = activity;
    //UIProgressDialog.showProgress(activity);
  }

  @Override
  protected String doInBackground(Object... params) {
    return perform(params);
  }

  protected String perform(Object... params) {

    return null;
  }

  protected void runOnUiThread(UIThreadExecutor executor) {

    activity.runOnUiThread(executor);
  }

  public static class UIThreadExecutor implements Runnable {

    @Override
    public final void run() {
      execute();
//      UIProgressDialog.dismiss();
    }

    public void execute() {

    }

  }
}
