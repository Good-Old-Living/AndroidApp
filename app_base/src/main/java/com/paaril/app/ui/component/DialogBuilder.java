package com.paaril.app.ui.component;

import com.paaril.app.base.R;
import com.paaril.app.ui.listener.AppOnDismissListener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogBuilder {

  public static AlertDialog.Builder buildDialog(Context context,
                                                String title,
                                                CharSequence message) {

    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppTheme_DialogTheme);
    builder.setTitle(title);
    builder.setCancelable(true);
    builder.setMessage(message);
    return builder;
  }

  public static void buildConfirmDialog(Context context,
                                        String message,
                                        DialogInterface.OnClickListener yesListener) {

    AlertDialog.Builder builder = buildDialog(context,
                                              "Confirm",
                                              message);
    builder.setPositiveButton("Yes",
                              yesListener);

    builder.setNegativeButton("No",
                              new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                }
                              });

    builder.show();

  }

  public static void buildMessageDialog(Context context,
                                        String title,
                                        CharSequence message) {

    AlertDialog.Builder builder = buildDialog(context,
                                              title,
                                              message);

    builder.setNegativeButton("Close",
                              new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                }
                              });

    builder.show();

  }

  public static void buildMessageDialog(Context context,
                                        String title,
                                        CharSequence message,
                                        final DialogInterface.OnClickListener closeListener) {

    AlertDialog.Builder builder = buildDialog(context,
                                              title,
                                              message);

    builder.setNegativeButton("Close",
                              closeListener);
    builder.setOnDismissListener(new AppOnDismissListener(null) {
      
      @Override
      public void onDismissImpl(DialogInterface dialog) {
        closeListener.onClick(dialog, 1);
        
      }
    });
    builder.show();

  }
}
