package com.maktoday.views.chat;

import com.maktoday.model.LatestMessage;
import com.maktoday.model.PojoChatData;

import java.util.HashMap;

/**
 * Created by cbl1005 on 24/1/18.
 */

public interface ChatContract {
    interface View {
        void successChatHistory(PojoChatData data);

        void successCreateChat(LatestMessage body);

        void setLoading(boolean isLoading);

        void sessionExpired();

        void chatError(String errorFailure);

        void chatFailure(String failureMessage);
    }

    interface Presenter {
        void apiGetChatHistory(HashMap<String, String> map);

        void apiCreateChat(HashMap<String, String> map);

        void attachView(View view);

        void detachView();
    }
}
