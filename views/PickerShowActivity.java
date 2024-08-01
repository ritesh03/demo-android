package com.maktoday.views;

import android.os.Bundle;

import com.maktoday.utils.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.maktoday.R;
import com.maktoday.utils.BaseActivity;
import com.squareup.timessquare.CalendarPickerView;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.Calendar;
import java.util.Date;

import static com.maktoday.views.home.HomeFragment.selectedDatesList;

public class PickerShowActivity extends BaseActivity {
    RelativeLayout back_layout;
    TextView tvDone, tvTitle;
    private static final String TAG = "PickerShowActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker_show);
        Log.d(TAG, "onCreate: StartActivity");
        back_layout = findViewById(R.id.back_layout);
        tvDone = findViewById(R.id.tvDone);
        tvTitle = findViewById(R.id.tvTitle);
        back_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().hasExtra("type")) {
                } else {
                    selectedDatesList.clear();
                }
                finish();
            }
        });
        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final CalendarPickerView calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DAY_OF_WEEK,2);

        Calendar nextYear = Calendar.getInstance();

        nextYear.add(Calendar.MONTH, 3);
        if (getIntent().hasExtra("type")) {
            tvDone.setVisibility(View.GONE);
            tvTitle.setText(getString(R.string.label_bookingdate1));
            calendar.init(today.getTime(), nextYear.getTime())
                    .inMode(CalendarPickerView.SelectionMode.MULTIPLE)
                    .displayOnly()
                    .withSelectedDates(selectedDatesList);

        } else {
            tvTitle.setText(getString(R.string.select_ser_date));
            tvDone.setVisibility(View.VISIBLE);
            calendar.init(today.getTime(), nextYear.getTime())
                    .inMode(CalendarPickerView.SelectionMode.MULTIPLE)
                    .withSelectedDates(selectedDatesList);
        }
        calendar.setOnInvalidDateSelectedListener(new CalendarPickerView.OnInvalidDateSelectedListener() {
            @Override
            public void onInvalidDateSelected(Date date) {
                Log.e("sdsd", "sdsd");
            }
        });

        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {

            @Override
            public void onDateUnselected(Date date) {
                for (int i = 0; i < selectedDatesList.size(); i++) {
                    if (date.equals(selectedDatesList.get(i))) {
                        selectedDatesList.remove(i);
                    }
                }

                Log.e("Date:- ", selectedDatesList + "");
            }

            @Override
            public void onDateSelected(Date date) {
                selectedDatesList.add(date);
                Log.e("Date:- ", selectedDatesList + "");
            }
        });
    }
}
