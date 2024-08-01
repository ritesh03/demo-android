package com.maktoday.views.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.maktoday.R;
import com.maktoday.adapter.ChatMsgAdapter;
import com.maktoday.databinding.ActivityChatBinding;
import com.maktoday.model.LatestMessage;
import com.maktoday.model.PojoChatData;
import com.maktoday.model.PojoLogin;
import com.maktoday.utils.BaseActivity;
import com.maktoday.utils.Constants;
import com.maktoday.utils.DataVariable;
import com.maktoday.utils.DialogPopup;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Log;
import com.maktoday.utils.Prefs;
import com.maktoday.views.chatmap.MapActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ChatActivity extends BaseActivity implements ChatContract.View, View.OnClickListener {
    private static final String TAG = "ChatActivity";
    private ActivityChatBinding binding;
    private ChatContract.Presenter presenter;
    private int lastMessageCount = 0;
    private List<LatestMessage> msgList = new ArrayList<>();
    private ChatMsgAdapter chatMsgAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: StartActivity");
        init();
        setData();
        setListeners();
    }

    private void init() {
        binding = DataBindingUtil.setContentView(ChatActivity.this, R.layout.activity_chat);
        presenter = new ChatPresenter();
        presenter.attachView(this);

    }

    private void setData() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_black);
        }

        PojoLogin pojoLogin = Prefs.with(ChatActivity.this).getObject(Constants.DATA, PojoLogin.class);
        Prefs.with(ChatActivity.this).save(Constants.USER_ID, pojoLogin._id);
        try {
            binding.tvName.setText(getIntent().getStringExtra(Constants.NAME));
        } catch (Exception e) {
            e.printStackTrace();
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
        }
        chatMsgAdapter = new ChatMsgAdapter(ChatActivity.this, msgList);
        binding.rvChat.setAdapter(chatMsgAdapter);
        binding.rvChat.setLayoutManager(new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false));

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                apiGetChatData();
            }
        }, 0, 5000);
    }

    private void apiGetChatData() {
        if (GeneralFunction.isNetworkConnected(ChatActivity.this, findViewById(android.R.id.content))) {
            HashMap<String, String> map = new HashMap<>();
            map.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
            map.put("userId", getIntent().getStringExtra(Constants.USER_ID));
            map.put("lastMessageCount", String.valueOf(lastMessageCount));
            map.put("userType", "USER");
            presenter.apiGetChatHistory(map);
        }

    }

    private void setListeners() {
        binding.ivSend.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                DataVariable.hideSoftKeyboard(ChatActivity.this);
                finish();
                break;
            case R.id.action_location:
                startActivityForResult(new Intent(ChatActivity.this, MapActivity.class), 10);

                break;
        }
        return true;
    }

    @Override
    public void successChatHistory(PojoChatData data) {

        if (data.latestMessage.size() == 0) {
            return;
        }
        if (chatMsgAdapter.getItemCount() < data.latestMessage.size() && lastMessageCount == 0) {
            msgList.clear();
            msgList.addAll(data.latestMessage);
        } else {
            for (LatestMessage msg : data.latestMessage) {
                if (msg.senderId.equals(getIntent().getStringExtra(Constants.USER_ID))) {
                    msgList.add(msg);
                }
            }
        }
        lastMessageCount = data.totalMsgCount;
        chatMsgAdapter.notifyDataSetChanged();
        binding.rvChat.getLayoutManager().scrollToPosition(msgList.size() - 1);
    }

    @Override
    public void successCreateChat(LatestMessage data) {
    }

    @Override
    public void setLoading(boolean isLoading) {
        if (isLoading)
            GeneralFunction.showProgress(ChatActivity.this);
        else
            GeneralFunction.dismissProgress();
    }

    @Override
    public void sessionExpired() {
        GeneralFunction.isUserBlocked(ChatActivity.this);
    }

    @Override
    public void chatError(String errorFailure) {
        new DialogPopup().alertPopup(ChatActivity.this, getResources().getString(R.string.dialog_alert), errorFailure, "").show(getSupportFragmentManager(), "ios_dialog");

    }

    @Override
    public void chatFailure(String failureMessage) {
        Log.e(TAG, "chatFailure: " + failureMessage);
        new DialogPopup().alertPopup(ChatActivity.this, getResources().getString(R.string.dialog_alert), getString(R.string.check_connection), "").show(getSupportFragmentManager(), "ios_dialog");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Prefs.with(ChatActivity.this).remove(Constants.USER_ID);
        presenter.detachView();
    }

    //    @Override
    //    public void onBackPressed() {
    //        super.onBackPressed();
    //    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivSend:
                if (binding.etMsg.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, R.string.plz_enter_msg, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (GeneralFunction.isNetworkConnected(ChatActivity.this, findViewById(android.R.id.content))) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
                    map.put("serviceId", getIntent().getStringExtra(Constants.SERVICE_ID));
                    map.put("receiverId", getIntent().getStringExtra(Constants.USER_ID));
                    map.put("message", binding.etMsg.getText().toString().trim());
                    map.put("messageType", "MESSAGE");
                    map.put("senderType", "USER");
                    presenter.apiCreateChat(map);

                    LatestMessage data = new LatestMessage();
                    data.senderType = "USER";
                    data.messageType = "MESSAGE";
                    data.message = binding.etMsg.getText().toString().trim();
                    data.timeStamp = String.valueOf(Calendar.getInstance().getTimeInMillis());
                    msgList.add(data);
                    chatMsgAdapter.notifyItemInserted(msgList.size() - 1);
                    binding.rvChat.getLayoutManager().scrollToPosition(msgList.size() - 1);
                    binding.etMsg.setText("");
                }
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (GeneralFunction.isNetworkConnected(ChatActivity.this, findViewById(android.R.id.content))) {
                HashMap<String, String> map = new HashMap<>();
                map.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
                map.put("serviceId", getIntent().getStringExtra(Constants.SERVICE_ID));
                map.put("receiverId", getIntent().getStringExtra(Constants.USER_ID));
                map.put("lat", data.getStringExtra(Constants.LATTITUDE));
                map.put("lng", data.getStringExtra(Constants.LONGITUDE));
                map.put("messageType", "LOCATION");
                map.put("senderType", "USER");
                presenter.apiCreateChat(map);

                LatestMessage data1 = new LatestMessage();
                data1.senderType = "USER";
                data1.messageType = "LOCATION";
                data1.location = new ArrayList<>();
                data1.location.add(Double.parseDouble(data.getStringExtra(Constants.LONGITUDE)));
                data1.location.add(Double.parseDouble(data.getStringExtra(Constants.LATTITUDE)));
                data1.message = binding.etMsg.getText().toString().trim();
                data1.timeStamp = String.valueOf(Calendar.getInstance().getTimeInMillis());
                msgList.add(data1);
                chatMsgAdapter.notifyItemInserted(msgList.size() - 1);
                binding.rvChat.getLayoutManager().scrollToPosition(msgList.size() - 1);
                binding.etMsg.setText("");
            }
        }
    }
}

