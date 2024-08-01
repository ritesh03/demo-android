package com.maktoday.adapter;

import android.app.Activity;
import android.content.Intent;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.pavlospt.roundedletterview.RoundedLetterView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.maktoday.R;
import com.maktoday.model.PojoChatList;
import com.maktoday.utils.Constants;
import com.maktoday.views.chat.ChatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cbl1005 on 24/1/18.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>{
    private Activity context;
    private List<PojoChatList> listChat=new ArrayList<>();

    public ChatAdapter(Activity context,List<PojoChatList> listChat)
    {
        this.context=context;
        this.listChat=listChat;

    }
    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=context.getLayoutInflater();
        View view=inflater.inflate(R.layout.item_rv_chat,parent,false);
        return new ChatAdapter.ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        PojoChatList chatData=listChat.get(position);

        try {
            if (chatData.userData != null && chatData.userData.firstName != null && chatData.userData.lastName != null) {
                holder.tvMaidName.setText(String.format("%s %s", chatData.userData.firstName, chatData.userData.lastName));
            }
        }catch (Exception e){
            e.printStackTrace();
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
        }
        try {
            if (chatData.messageType.equals("MESSAGE")) {
                holder.tvDescription.setText(chatData.message);
            } else {
                holder.tvDescription.setText(R.string.Location);
            }
        }catch (Exception e){
            e.printStackTrace();
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
        }

        try {
            holder.ivProfile.setTitleText("" + chatData.userData.firstName.charAt(0));
        }catch (Exception e){
            e.printStackTrace();
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
        }

        try {
            holder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    if (listChat.get(position).userData != null) {
                        intent.putExtra(Constants.USER_ID, listChat.get(position).userData.id);
                        intent.putExtra(Constants.SERVICE_ID, listChat.get(position).id);
                        intent.putExtra(Constants.NAME, listChat.get(position).userData.firstName + " " + listChat.get(position).userData.lastName);
                        context.startActivity(intent);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            try {//----------- IF FIREBASE RETURN EXCEPTION
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
        }
    }



    @Override
    public int getItemCount() {
        return listChat.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        private RoundedLetterView ivProfile;
        private TextView tvMaidName,tvDescription;
        private ConstraintLayout parent;
        public ChatViewHolder(View itemView) {
            super(itemView);
            ivProfile=itemView.findViewById(R.id.ivProfile);
            tvMaidName=itemView.findViewById(R.id.tvMaidName);
            tvDescription=itemView.findViewById(R.id.tvDescription);
            parent = itemView.findViewById(R.id.parent);

//            parent.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                }
//            });
        }
    }
}
