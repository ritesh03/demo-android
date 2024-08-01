package com.maktoday.interfaces;

import android.widget.EditText;

import com.maktoday.model.PojoCardList;

/**
 * Created by cbl81 on 24/11/17.
 */

public interface CardListUpdate {
     void selectedCard(PojoCardList.Data data, EditText etCVV, String cardType);
     void deleteCard(String id);
}
