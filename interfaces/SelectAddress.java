package com.maktoday.interfaces;

import com.maktoday.model.FullAddress;

/**
 * Created by cbl81 on 11/12/17.
 */

public interface SelectAddress {
     void getAddress(FullAddress address);
     void deleteAddress(String id);
}
