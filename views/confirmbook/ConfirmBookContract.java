package com.maktoday.views.confirmbook;

import com.maktoday.model.ApiResponse;
import com.maktoday.model.FullAddress;
import com.maktoday.model.PojoLogin;
import com.maktoday.model.PojoSearchMaid;
import com.maktoday.model.PromoResponse;

import java.util.HashMap;

/**
 * Created by cbl81 on 12/12/17.
 */

public interface ConfirmBookContract {

    interface View {
        void searchMaidSuccessBulk(PojoSearchMaid searchMaidData);

        void applyPromoSuccess(PromoResponse searchMaidData);

        void saveAddressSuccess(ApiResponse<PojoLogin> data);

        void deleteAddressSuccess(ApiResponse<PojoLogin> data);

        void setLoading(boolean isLoading);

        void sessionExpired();

        void maidNotAvailable(String failureMessage);

        void reschduleError(String failureMessage);

        void saveAddressError(String failureMessage);

        void promoError(String failureMessage);

        void saveAddressFailure(String failureMessage);
    }

    interface Presenter {
        void apiSearchBulkMaid(HashMap<String, String> map);

        void apiRescheduleBulk(HashMap<String, String> map, String accessToken, String reschduleStatus);

        void apiSaveAddress(FullAddress fullAddress);

        void apiApplyPromo(HashMap<String, String> map);

        void apiDeleteAddress(HashMap<String, String> map);

        void attachView(ConfirmBookContract.View view);

        void detachView();

    }
}
