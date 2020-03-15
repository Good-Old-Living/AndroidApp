package com.paaril.app.ui.component;

import java.util.Calendar;

import com.paaril.app.AppHelper;
import com.paaril.app.ui.UIHelper;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;

public class DateView
    implements OnClickListener, OnFocusChangeListener, OnDateSetListener {

  private EditText editText;
  private Calendar calendar = Calendar.getInstance();

  public DateView(EditText editText) {
    this.editText = editText;
    editText.setOnFocusChangeListener(this);
    editText.setOnClickListener(this);

  }


  @Override
  public void onDateSet(DatePicker view,
                        int year,
                        int monthOfYear,
                        int dayOfMonth) {

    calendar.set(Calendar.YEAR,
                 year);
    calendar.set(Calendar.MONTH,
                 monthOfYear);
    calendar.set(Calendar.DAY_OF_MONTH,
                 dayOfMonth);

    editText.setText(AppHelper.toString(calendar.getTime()));
    editText.requestFocus();
  }

  @Override
  public void onFocusChange(View view,
                            boolean hasFocus) {
    if (hasFocus) {
      new DatePickerDialog(editText.getContext(),
                           this,
                           calendar.get(Calendar.YEAR),
                           calendar.get(Calendar.MONTH),
                           calendar.get(Calendar.DAY_OF_MONTH)).show();

    }
  }

  @Override
  public void onClick(View view) {
    onFocusChange(view,
                  true);
  }

}
