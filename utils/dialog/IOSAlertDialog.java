package com.maktoday.utils.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.maktoday.R;

public class IOSAlertDialog extends DialogFragment {
    private Context context;
    private String title;
    private String message;
    private String positiveButtonTitle;
    private String negativeButtonTitle;
    private boolean cancelable = true;
    private int positiveButtonColor;
    private int negativeButtonColor;
    private DialogInterface.OnClickListener positiveButtonListener;
    private DialogInterface.OnClickListener negativeButtonListener;
    private String[] items;
    private int checkedItem = -1;
    private boolean showRadio = false;
    private boolean showVerticalButton = false;
    private RadioItemClickListener radioItemClickListener;
    private static final String TAG = "IOSAlertDialog";

    public interface RadioItemClickListener {
        void onRadioItemClick(String selectedItem, int selected);
    }





    public static IOSAlertDialog newInstance(Context context, String title, String message,
                                             String positiveButtonTitle, String negativeButtonTitle,
                                             DialogInterface.OnClickListener positiveButtonListener,
                                             DialogInterface.OnClickListener negativeButtonListener,
                                             int positiveButtonColor, int negativeButtonColor,
                                             boolean cancelable) {
        IOSAlertDialog dialog = new IOSAlertDialog();
        dialog.context = context;
        dialog.title = title;
        dialog.message = message;
        dialog.positiveButtonTitle = positiveButtonTitle;
        dialog.negativeButtonTitle = negativeButtonTitle;
        dialog.positiveButtonListener = positiveButtonListener;
        dialog.negativeButtonListener = negativeButtonListener;
        dialog.positiveButtonColor = positiveButtonColor;
        dialog.negativeButtonColor = negativeButtonColor;
        dialog.cancelable = cancelable;
        return dialog;
    }



    public static IOSAlertDialog newInstance(Context context, String title, String message,
                                             String positiveButtonTitle, String negativeButtonTitle,
                                             DialogInterface.OnClickListener positiveButtonListener,
                                             DialogInterface.OnClickListener negativeButtonListener,
                                             int positiveButtonColor, int negativeButtonColor,
                                             boolean cancelable, boolean showVerticalButton) {
        IOSAlertDialog dialog = new IOSAlertDialog();
        dialog.context = context;
        dialog.showVerticalButton = showVerticalButton;
        dialog.title = title;
        dialog.message = message;
        dialog.positiveButtonTitle = positiveButtonTitle;
        dialog.negativeButtonTitle = negativeButtonTitle;
        dialog.positiveButtonListener = positiveButtonListener;
        dialog.negativeButtonListener = negativeButtonListener;
        dialog.positiveButtonColor = positiveButtonColor;
        dialog.negativeButtonColor = negativeButtonColor;
        dialog.cancelable = cancelable;
        return dialog;
    }

    public static IOSAlertDialog newInstance(Context context, String title, String[] items,
                                             int checkedItem, String positiveButtonTitle,
                                             String negativeButtonTitle,
                                             DialogInterface.OnClickListener positiveClickListener,
                                             DialogInterface.OnClickListener negativeClickListener,
                                             int positiveButtonColor, int negativeButtonColor,
                                             boolean cancelable, boolean showRadio) {
        IOSAlertDialog dialog = new IOSAlertDialog();
        dialog.context = context;
        dialog.title = title;
        dialog.items = items;
        dialog.checkedItem = checkedItem;
        dialog.positiveButtonTitle = positiveButtonTitle;
        dialog.negativeButtonTitle = negativeButtonTitle;
        dialog.positiveButtonListener = positiveClickListener;
        dialog.negativeButtonListener = negativeClickListener;
        dialog.positiveButtonColor = positiveButtonColor;
        dialog.negativeButtonColor = negativeButtonColor;
        dialog.cancelable = cancelable;
        dialog.showRadio = showRadio;
        return dialog;
    }

    public void setRadioItemClickListener(RadioItemClickListener listener) {
        this.radioItemClickListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setCancelable(cancelable);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_ios_alert, container, false);

        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView messageTextView = view.findViewById(R.id.messageTextView);
        TextView positiveButton = view.findViewById(R.id.positiveButton);
        TextView negativeButton = view.findViewById(R.id.negativeButton);
        LinearLayout llButtons = view.findViewById(R.id.llButtons);

        if (showVerticalButton) {
            if (llButtons != null) {
                llButtons.setOrientation(LinearLayout.VERTICAL);

                // Set positive button width to match parent
                ViewGroup.LayoutParams positiveLayoutParams = positiveButton.getLayoutParams();
                positiveLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                positiveButton.setLayoutParams(positiveLayoutParams);

                // Set negative button width to match parent
                ViewGroup.LayoutParams negativeLayoutParams = negativeButton.getLayoutParams();
                negativeLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                negativeButton.setLayoutParams(negativeLayoutParams);

                // Adjust the weight of the buttons
                LinearLayout.LayoutParams positiveButtonParams = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
                positiveButtonParams.weight = 1;
                positiveButton.setLayoutParams(positiveButtonParams);

                LinearLayout.LayoutParams negativeButtonParams = (LinearLayout.LayoutParams) negativeButton.getLayoutParams();
                negativeButtonParams.weight = 0;
                negativeButton.setLayoutParams(negativeButtonParams);
            }
        }

        if (title == null) {
            titleTextView.setVisibility(View.GONE);
        } else {
            titleTextView.setVisibility(View.VISIBLE);
            titleTextView.setText(title);
            Log.e(TAG, "onCreateView: title :: " + title);
        }

        if (message == null) {
            messageTextView.setVisibility(View.GONE);
        } else {
            messageTextView.setVisibility(View.VISIBLE);
            messageTextView.setText(message);
        }

        if (positiveButtonTitle == null) {
            positiveButton.setVisibility(View.GONE);
        } else {
            positiveButton.setVisibility(View.VISIBLE);
            positiveButton.setText(positiveButtonTitle);
        }

        if (negativeButtonTitle == null) {
            negativeButton.setVisibility(View.GONE);
        } else {
            negativeButton.setVisibility(View.VISIBLE);
            negativeButton.setText(negativeButtonTitle);
        }

        if (positiveButtonColor != 0) {
            positiveButton.setTextColor(positiveButtonColor);
        }

        if (negativeButtonColor != 0) {
            negativeButton.setTextColor(negativeButtonColor);
        }

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positiveButtonListener != null) {
                    positiveButtonListener.onClick(getDialog(), DialogInterface.BUTTON_POSITIVE);
                }
                dismiss();
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (negativeButtonListener != null) {
                    negativeButtonListener.onClick(getDialog(), DialogInterface.BUTTON_NEGATIVE);
                }
                dismiss();
            }
        });

        if (showRadio && items != null && items.length > 0) {
            RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
            for (int i = 0; i < items.length; i++) {
                RadioButton radioButton = new RadioButton(requireContext());
                radioButton.setText(items[i]);
                radioButton.setId(i);
                radioButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                radioButton.setTypeface(Typeface.create("@font/manrope_bold.ttf", Typeface.NORMAL));
                radioButton.setPadding(0, 8, 0, 8);
                radioGroup.addView(radioButton);
                if (i == checkedItem) {
                    radioButton.setChecked(true);
                }
            }

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (radioItemClickListener != null) {
                        int selectedIndex = checkedId;
                        String selectedItem = items[selectedIndex];
                        radioItemClickListener.onRadioItemClick(selectedItem, selectedIndex);
                    }
                }
            });
        }

        return view;
    }
}
