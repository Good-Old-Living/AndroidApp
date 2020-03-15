package com.paaril.app.ui.task;

import com.paaril.app.DomainEntity;

import android.R;
import android.widget.GridView;

public class CategoryGridTask extends ParallelTask {

  @Override
  protected String perform(Object... params) {

    DomainEntity parentCategory = (DomainEntity) params[1];
    boolean forLeaf = params.length == 3 ? (boolean) params[2] : false;

   

    runOnUiThread(new UIThreadExecutor() {
      public void execute() {
    //    GridView gridView = (GridView) activity.findViewById(R.id.gridview);
   //     gridView.setAdapter(categorydapter);
      }
    });

    return null;
  }

}
