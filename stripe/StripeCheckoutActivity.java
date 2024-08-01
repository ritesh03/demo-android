package com.maktoday.stripe;

import static com.maktoday.views.home.HomeFragment.booking_type;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;

import com.github.kittinunf.fuel.core.Headers;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.maktoday.Config.Config;
import com.maktoday.databinding.ActivitySignupBinding;
import com.maktoday.utils.Log;
import com.maktoday.views.confirmbook.ConfirmBookFragment;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.Pair;

public class StripeCheckoutActivity extends ComponentActivity {

    private ActivitySignupBinding binding;
    PaymentSheet paymentSheet;
    String paymentIntentClientSecret;
    PaymentSheet.CustomerConfiguration customerConfig;

    private static final String TAG = "StripeCheckoutActivity";


    String fullName = "";
    String mobile ="";
    String address = "";
    String service ="";
    String currency_code = "";
    String serviceId = "";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
       // setContentView(binding.getRoot());


         fullName = getIntent().getStringExtra("name");
         mobile = getIntent().getStringExtra("mobile");
         address = getIntent().getStringExtra("address");
         service = getIntent().getStringExtra("service");
         currency_code = getIntent().getStringExtra("currency_code");
         serviceId = getIntent().getStringExtra("serviceId");




        paymentSheet = new PaymentSheet(StripeCheckoutActivity.this, this::onPaymentSheetResult);


       HashMap<String,String> request = new HashMap<>();
       request.put("name",fullName);
       request.put("mobile",mobile);
       request.put("email",address);
       request.put("service",service);
       //request.put("amount",String.format("%.2f", ConfirmBookFragment.totalper));
       request.put("currency_code",currency_code);
       request.put("serviceId",serviceId);



        android.util.Log.e(TAG, "onCreate: "+ new Gson().toJson(request));

        Fuel.INSTANCE.post(Config.getBaseURL() +"user/payment-sheet", null ) .header(Headers.CONTENT_TYPE, "application/json") // Set the content type if required
                .body(new Gson().toJson(request), Charset.defaultCharset())
                .responseString(new Handler<String>() {
            @Override
            public void success(String s) {
                try {
                    final JSONObject result = new JSONObject(s);
                    android.util.Log.e(TAG, "success: response : "+ result);
                    JSONObject data = result.getJSONObject("data");
                    customerConfig = new PaymentSheet.CustomerConfiguration(
                            data.getString("customer"),
                            data.getString("ephemeralKey")
                    );
                    paymentIntentClientSecret = data.getString("paymentIntent");

                    PaymentConfiguration.init(getApplicationContext(), data.getString("publishableKey"));
                    presentPaymentSheet();
                }
                catch (JSONException e) {
                    android.util.Log.e(TAG, "Fail Fuel APi   ",e );
                    /* handle error */
                }
            }

            @Override
            public void failure(@NonNull FuelError fuelError) {
                android.util.Log.e(TAG, "failure: "+ fuelError.getMessage() );
                /* handle error */ }
        });
    }

    void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult) {
        // implemented in the next steps

        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Log.d(TAG, "Canceled");
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
        
                Log.e(TAG, "Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
          
        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            // Display for example, an order confirmation screen
            Log.d(TAG, "Completed");
        }
    }

    private void presentPaymentSheet() {
        final PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("Example, Inc.")
                .customer(customerConfig)
                // Set `allowsDelayedPaymentMethods` to true if your business can handle payment methods
                // that complete payment after a delay, like SEPA Debit and Sofort.
                .allowsDelayedPaymentMethods(true)
                .build();
        paymentSheet.presentWithPaymentIntent(
                paymentIntentClientSecret,
                configuration
        );
    }


}
