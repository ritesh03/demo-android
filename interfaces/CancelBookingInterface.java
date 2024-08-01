package com.maktoday.interfaces;

/**
 * Created by cbl1005 on 15/1/18.
 */

public interface CancelBookingInterface {
    void showCancelDialog(String bookingId, String timeZone);
    void showExtendDialog(String payment_mode, String referenceId, String serviceMakId, Float actualPrice, String currency, String vat);
}
