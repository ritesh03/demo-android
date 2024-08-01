package com.maktoday.views.chatlist;

import com.maktoday.model.PojoChatList;

import java.util.List;

/**
 * Created by cbl1005 on 24/1/18.
 */

public class ChatContract {
    interface View {
        void successGetAllChat(List<PojoChatList> data);

        void setLoading(boolean isLoading);

        void sessionExpired();

        void chatError(String errorFailure);

        void chatFailure(String failureMessage);
    }

    interface Presenter {
        void apiGetAllChat();

        void attachView(ChatContract.View view);

        void detachView();
    }
}
