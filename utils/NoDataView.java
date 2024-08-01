package com.maktoday.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maktoday.R;

/**
 * Created by cbl81 on 28/10/17.
 */

public class NoDataView extends LinearLayout {

    TextView tvNoData;
    ImageView ivRefresh;

    public NoDataView(Context context) {
        this(context, null);
    }

    public NoDataView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NoDataView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        inflate(getContext(), R.layout.item_no_data_found, this);

        //Get references to text views
        tvNoData = (TextView) findViewById(R.id.tvNoData);
        ivRefresh = (ImageView) findViewById(R.id.ivRefresh);
    }

    public void setData(String data)
    {
        tvNoData.setText(data);
    }

    public void setImage(Drawable image)
    {

        tvNoData.setCompoundDrawablesWithIntrinsicBounds(null,image, null, null);
    }
}
