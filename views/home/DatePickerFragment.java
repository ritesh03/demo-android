package com.maktoday.views.home;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import com.maktoday.utils.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.maktoday.R;
import com.squareup.timessquare.CalendarPickerView;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.maktoday.views.home.HomeFragment.selectedDatesList;

public class DatePickerFragment extends DialogFragment {
    private static final String TAG = "DatePickerFragment";
    private DatePicker datePicker;
    public interface DateDialogListener {
        void onFinishDialog(ArrayList<Date> selectedDatesList);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_date, null);
        Log.d(TAG, "onCreateDialog: StartActivity");
        final CalendarPickerView calendar = (CalendarPickerView) v.findViewById(R.id.calendar_view);
        Date today = new Date();
        Calendar nextYear = Calendar.getInstance();
       // nextYear.add(Calendar.YEAR, 1);
        nextYear.add(Calendar.MONTH, 1);

        calendar.init(today, nextYear.getTime())


                .inMode(CalendarPickerView.SelectionMode.MULTIPLE)

                /*.withSelectedDate(today)*/;

//        datePicker = (DatePicker) v.findViewById(R.id.dialog_date_date_picker);
//        datePicker.setSelected(true);
        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {

            @Override
            public void onDateUnselected(Date date) {
                for (int i=0;i<selectedDatesList.size();i++)
                {
                    if(date.equals(selectedDatesList.get(i)))
                    {
                        selectedDatesList.remove(i);
                    }

                }

                Log.e("Date:- ", selectedDatesList+"");
            }

            @Override
            public void onDateSelected(Date date) {

              //  Toast.makeText(getActivity(),  String.valueOf(date) + "", Toast.LENGTH_LONG).show();
                selectedDatesList.add(date);
                Log.e("Date:- ", selectedDatesList+"");
            }
        });

        return new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                .setView(v)
               // .setTitle("Select Dates")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dismiss();
HomeFragment  homeFragment=new HomeFragment();
homeFragment.onFinishDialog(selectedDatesList);
                            }
                        })
                .create();



    }

}
