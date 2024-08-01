package com.maktoday.stripe;

import static com.maktoday.utils.Constants.UnAuthorized;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.maktoday.Config.Config;
import com.maktoday.R;
import com.maktoday.model.ApiResponse;
import com.maktoday.model.PojoAddCard;
import com.maktoday.model.PojoCreatePayment;
import com.maktoday.utils.BaseActivity;
import com.maktoday.utils.Constants;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Log;
import com.maktoday.utils.Prefs;
import com.maktoday.utils.dialog.IOSAlertDialog;
import com.maktoday.views.confirmbook.ConfirmBookFragment;
import com.maktoday.webservices.RestClient;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StripePaymentActivity extends BaseActivity {
    private static final String TAG = "StripePaymentActivity";
    Button charge_btn;
    Stripe stripe;
    Token token = null;
    TextView title, tvTotalPriceValue;
    RelativeLayout back_layout;
    CheckBox chkSavecard;
    String serviceId = "", ischeck = "false";
    private CardMultilineWidget mCardMultilineWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_payment);

        Log.d(TAG, "onCreate: StartActivity");
        System.out.print(TAG + "onCreate: StartActivity");
        charge_btn = findViewById(R.id.pay_btn);
        mCardMultilineWidget = findViewById(R.id.mCardMultilineWidget);
        back_layout = findViewById(R.id.back_layout);
        chkSavecard = findViewById(R.id.chkSavecard);
        tvTotalPriceValue = findViewById(R.id.tvTotalPriceValue);
        serviceId = getIntent().getStringExtra("serviceId");
        Log.e(TAG, "serviceId=====" + serviceId);
        tvTotalPriceValue.setText(getIntent().getStringExtra("currency_code") + " " + String.format("%.2f", ConfirmBookFragment.totalper));


        chkSavecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (chkSavecard.isChecked()) {
                    ischeck = "true";
                } else {
                    ischeck = "false";
                }
            }
        });


        //  progressDialog.show();

        back_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                IOSAlertDialog dialog = IOSAlertDialog.newInstance(
                        StripePaymentActivity.this,
                        null,
                        getString(R.string.cancel_pay),
                        getString(R.string.ok),
                        getString(R.string.cancel1),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                dialog.cancel();
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.cancel();
                            }
                        },
                        ContextCompat.getColor(StripePaymentActivity.this, R.color.coral),
                        ContextCompat.getColor(StripePaymentActivity.this, R.color.app_color),
                        false
                );

                dialog.show(getSupportFragmentManager(), "ios_dialog");


 /*               AlertDialog dialog = new AlertDialog.Builder(StripePaymentActivity.this)
                        .setCancelable(true)
                        .setMessage(getString(R.string.cancel_pay))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                 finish();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(StripePaymentActivity.this, R.color.appColor));
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(StripePaymentActivity.this, R.color.appColor));
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setAllCaps(false);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setAllCaps(false);
*/

            }
        });

        Config.TRANSACTION_ID = "";
        Config.TAP_ID = "";
    

        stripe = new Stripe(StripePaymentActivity.this, Config.PUBLISH_KEY);

        charge_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "chargebtn onClick: " + mCardMultilineWidget.getCardParams());
                if (mCardMultilineWidget.getCardParams() == null) {
                    Toast.makeText(StripePaymentActivity.this, "Invalid Card Data", Toast.LENGTH_SHORT).show();
                } else {
                    GeneralFunction.showProgress(StripePaymentActivity.this);
                    stripe.createCardToken(mCardMultilineWidget.getCardParams(), new ApiResultCallback<Token>() {
                        @Override
                        public void onSuccess(@NonNull Token token) {
                            Log.e(TAG, "token" + "" + new Gson().toJson(token));
                            if (ischeck.equalsIgnoreCase("true")) {
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
                                hashMap.put("cardNumber", token.getCard().getLast4());
                                hashMap.put("name", "");
                                hashMap.put("cardType", token.getCard().getFunding().toString());
                                hashMap.put("cardToken", token.getId());
                                hashMap.put("validTill", token.getCard().getExpYear().toString());
                                hitapiaddcard(hashMap);
                            } else {
                                // is save card false
                                Hitapi_CreatePayment(token.getId(), "false");

                            }
                        }

                        @Override
                        public void onError(@NonNull Exception e) {
                            GeneralFunction.dismissProgress();
                            Toast.makeText(StripePaymentActivity.this,
                                    e.getLocalizedMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private void Hitapi_CreatePayment(String cardtoken, String isSave) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
        hashMap.put("amount", String.format("%.2f", ConfirmBookFragment.totalper));
        hashMap.put("saveCards", isSave);
        hashMap.put("isExtension", "false");
        hashMap.put("serviceId", serviceId);
        hashMap.put("cardToken", cardtoken);
        Log.e(TAG, "creatpaymengt param==  " + new Gson().toJson(hashMap));
        System.out.print("creatpaymengt param==  " + new Gson().toJson(hashMap));
        GeneralFunction.showProgress(StripePaymentActivity.this);

        RestClient.getModalApiService().apiCreatePayment(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), hashMap)
                .enqueue(new Callback<PojoCreatePayment>() {
                    @Override
                    public void onResponse(Call<PojoCreatePayment> call, Response<PojoCreatePayment> response) {
                        GeneralFunction.dismissProgress();
                        if (response.isSuccessful()) {
                            // createPaymentSuccess(response.body().data);
                            Log.e(TAG, "Hitapi_CreatePayment onResponse: " + new Gson().toJson(response.body()));
                            if (response.body().statusCode == 200) {
                                Config.TRANSACTION_ID = response.body().data.transactionId;
                                Log.e(TAG, "transaction_id   == " + Config.TRANSACTION_ID);
                                //Toast.makeText(StripePaymentActivity.this, "success", Toast.LENGTH_SHORT).show();
                                finish();

                            }
                        } else {
                            if (response.code() == UnAuthorized) {
                                // view.sessionExpired();
                            } else {

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<PojoCreatePayment> call, Throwable t) {
                        //  if(view!=null) {
                        GeneralFunction.dismissProgress();
                        Toast.makeText(StripePaymentActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_LONG).show();
                        //    view.failure(t.getMessage());
                        // }
                    }

                });
    }

    private void hitapiaddcard(HashMap<String, String> hashMap) {

        Log.e(TAG, "add crad param==" + new Gson().toJson(hashMap));
        GeneralFunction.showProgress(StripePaymentActivity.this);

        RestClient.getModalApiService().apiAddCard(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), hashMap)
                .enqueue(new Callback<ApiResponse<PojoAddCard>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<PojoAddCard>> call, Response<ApiResponse<PojoAddCard>> response) {
                        GeneralFunction.dismissProgress();
                        if (response.isSuccessful()) {
                            Log.d(TAG, "hitapiaddcard onResponse: " + new Gson().toJson(response.body()));

                            // createPaymentSuccess(response.body().data);
                            if (response.body().getStatusCode().equalsIgnoreCase("200")) {
                                Log.d(TAG, "hitapiaddcard onResponse: " + new Gson().toJson(response.body()));
                                //Toast.makeText(StripePaymentActivity.this, "success", Toast.LENGTH_SHORT).show();

                                // is save card true
                                Hitapi_CreatePayment(response.body().getData().cardToken, "true");
                            } else if (response.body().getStatusCode().equalsIgnoreCase("400")) {
                                Toast.makeText(StripePaymentActivity.this, "Duplicate Card Entry", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (response.code() == 400) {
                                // view.sessionExpired();
                                Toast.makeText(StripePaymentActivity.this, "Duplicate Card Entry", Toast.LENGTH_SHORT).show();

                            } else {
                                Log.d(TAG, "onResponse: response code if not == 400" + response.code());

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<PojoAddCard>> call, Throwable t) {
                        //  if(view!=null) {
                        GeneralFunction.dismissProgress();
                        Toast.makeText(StripePaymentActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_LONG).show();
                        //    view.failure(t.getMessage());
                        // }
                    }

                });
    }
}
