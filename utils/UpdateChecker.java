package com.maktoday.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.maktoday.BuildConfig;
import com.maktoday.R;
import com.maktoday.model.ApiResponse;
import com.maktoday.model.CheckVersionResponse;
import com.maktoday.webservices.RestClient;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateChecker {
    private static final int NORMAL_UPDATE = 1;
    private static final int FORCE_UPDATE = 2;
    private static final String TAG = "UpdateChecker";
    private Listener listener;

    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
    }

    public void checkForUpdate() {
        RestClient.getModalApiService()
                .checkVersion(Constants.APP_TYPE, Constants.DEVICE_TYPE, BuildConfig.VERSION_CODE)
                .enqueue(new Callback<ApiResponse<CheckVersionResponse>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<CheckVersionResponse>> call, @NonNull Response<ApiResponse<CheckVersionResponse>> response) {
                        GeneralFunction.dismissProgress();
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            int status = response.body().getData().status;
                           Log.d(TAG, "Update status success : " + status);
                            if (listener != null) {
                                listener.appUpdateStatusReceived(status);
                            }
                        } else {
                                Log.e(TAG, "Update status error  "+response.errorBody());
                            }
                        }
                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<CheckVersionResponse>> call, @NonNull Throwable t) {
                        Log.e(TAG, "Update status failed : " + t.getMessage());
                    }
                });
    }

    public void checkAppUpdateStatus(final Context context, final int status) {
        switch (status) {
            case NORMAL_UPDATE: {
                final AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle(R.string.app_update_title)
                        .setMessage(R.string.app_update_message)
                        .setPositiveButton(R.string.update_now, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openPlayStorePage(context);
                            }
                        })
                        .setNegativeButton(R.string.update_later, null)
                        .create();
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                break;
            }
            case FORCE_UPDATE: {
                final AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle(R.string.app_update_title)
                        .setMessage(R.string.app_update_message)
                        .setPositiveButton(R.string.update_now, null)
                        .create();
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openPlayStorePage(context);
                            }
                        });
            }
        }
    }

    private void openPlayStorePage(final Context context) {
        final String playStoreUrl = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUrl));
        context.startActivity(intent);
    }

    public interface Listener {
        void appUpdateStatusReceived(int status);
    }
}