package com.paaril.app.ui.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.paaril.app.AppHelper;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class TNImageLoaderTask extends ParallelTask {

  @SuppressWarnings("unchecked")
  @Override
  protected String perform(Object... params) {

    final List<String> imageURLList = (List<String>) params[0];
    final List<ImageView> imageViewList = (List<ImageView>) params[1];

    final Map<String, Bitmap> bitmapMap = new HashMap<>();

    for (String imageURL : imageURLList) {

      Bitmap bitmap = bitmapMap.get(imageURL);
      if (bitmap == null) {
        bitmap = AppHelper.loadImage(imageURL,
                                     null);
        bitmapMap.put(imageURL,
                      bitmap);
      }

    }

    runOnUiThread(new UIThreadExecutor() {
      public void execute() {

        int i = 0;
        for (String imageURL : imageURLList) {
          ImageView imageView = imageViewList.get(i++);
          imageView.setImageBitmap(bitmapMap.get(imageURL));
        }
      }
    });

    return null;
  }

}
