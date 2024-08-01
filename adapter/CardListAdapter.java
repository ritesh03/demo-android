package com.maktoday.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.maktoday.R;
import com.maktoday.interfaces.CardListUpdate;
import com.maktoday.model.PojoCardList;
import com.maktoday.utils.Log;
import com.maktoday.utils.Prefs;
import com.maktoday.utils.dialog.IOSAlertDialog;

import java.util.ArrayList;
import java.util.List;

import static com.maktoday.utils.Constants.USER_COUNTRY;

/**
 * Created by cbl81 on 23/11/17.
 */

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.ViewHolder> {

    private Activity context;
    private List<PojoCardList.Data> cardList=new ArrayList<>();
    private int selectedPosition=-1;
    private CardListUpdate cardUpdate;
    private boolean isExtend;
    String currency;
    public CardListAdapter(Activity context, List<PojoCardList.Data> cardList, CardListUpdate cardUpdate, boolean isExtend, String currency) {
        this.context = context;
        this.cardList=cardList;
        this.cardUpdate=cardUpdate;
        this.isExtend=isExtend;
        this.currency=currency;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(context.getLayoutInflater().inflate(R.layout.item_card_list,parent,false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (currency.equalsIgnoreCase("BHD")) {

            try {
                if (Integer.parseInt(cardList.get(position).get_id()) == 0) {
                    holder.tvCardNo.setVisibility(View.INVISIBLE);
                    holder.ivBarnd.setVisibility(View.GONE);
                    holder.ivDelete.setVisibility(View.INVISIBLE);
                    try {
                        holder.tvCardtype.setText(cardList.get(position).getCardType());
                    }catch (Exception e){
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }

                } else if (Integer.parseInt(cardList.get(position).get_id()) == 1) {
                    holder.tvCardNo.setVisibility(View.INVISIBLE);
                    holder.ivDelete.setVisibility(View.INVISIBLE);
                    holder.ivBarnd.setVisibility(View.GONE);
                    try {
                        holder.tvCardtype.setText(cardList.get(position).getCardType() + " " + context.getString(R.string.card_list));
                    }catch (Exception e){
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                try {//----------- IF FIREBASE RETURN EXCEPTION
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
                }
            }

        }else if(currency.equalsIgnoreCase("GBP")){
            try {
                if (cardList.get(position).get_id().equalsIgnoreCase("1")) {

                    holder.tvCardNo.setVisibility(View.INVISIBLE);
                    try {
                        holder.tvCardtype.setText(cardList.get(position).getCardType() + " Card");
                    }catch (Exception e){
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }
                    holder.ivDelete.setVisibility(View.INVISIBLE);
                    holder.ivBarnd.setVisibility(View.GONE);
                } else {
                    holder.tvCardNo.setVisibility(View.VISIBLE);
                    holder.ivDelete.setVisibility(View.VISIBLE);
                    holder.ivBarnd.setVisibility(View.VISIBLE);
                    try {
                        holder.tvCardNo.setText("**** **** **** " + cardList.get(position).getCardNumber());
                    }catch (Exception e){
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }
                    try {
                        String input = cardList.get(position).getCardType() + " Card";
                        String output = input.substring(0, 1).toUpperCase() + input.substring(1);
                        holder.tvCardtype.setText(output);
                    }catch (Exception e){
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                try {//----------- IF FIREBASE RETURN EXCEPTION
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
                }
            }
        }else {
            try {
                if (cardList.get(position).get_id().equalsIgnoreCase("0")) {
                    holder.tvCardNo.setVisibility(View.INVISIBLE);
                    holder.ivBarnd.setVisibility(View.GONE);
                    try {
                        holder.tvCardtype.setText(cardList.get(position).getCardType());
                    }catch (Exception e){
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }
                    holder.ivDelete.setVisibility(View.INVISIBLE);
                } else if (cardList.get(position).get_id().equalsIgnoreCase("1")) {
                    holder.tvCardNo.setVisibility(View.INVISIBLE);
                    try {
                        holder.tvCardtype.setText(cardList.get(position).getCardType() + " " + context.getString(R.string.card_list));
                    }catch (Exception e){
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }
                    holder.ivDelete.setVisibility(View.INVISIBLE);
                    holder.ivBarnd.setVisibility(View.GONE);
                } else {
                    holder.tvCardNo.setVisibility(View.VISIBLE);
                    holder.ivDelete.setVisibility(View.VISIBLE);
                    holder.ivBarnd.setVisibility(View.VISIBLE);
                    try {
                        holder.tvCardNo.setText("**** **** **** " + cardList.get(position).getCardNumber());
                    }catch (Exception e){
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }

                    try {
                        String input = cardList.get(position).getCardType() + " Card";
                        String output = input.substring(0, 1).toUpperCase() + input.substring(1);
                        holder.tvCardtype.setText(output);
                    }catch (Exception e){
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                try {//----------- IF FIREBASE RETURN EXCEPTION
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
                }
            }
        }

        //holder.ivRadioButton.setImageResource(R.drawable.ic_unselected);
       /* if(position==0)
        {
            cardUpdate.selectedCard(cardList.get(0));
        }*/
        String cardBrand=cardList.get(position).getCardBrand();

        switch (cardBrand){
            case "MasterCard":
                holder.ivBarnd.setImageResource(R.drawable.ic_mastercard);
                break;
            case "Visa":
                holder.ivBarnd.setImageResource(R.drawable.visa);
                break;
            case "American Express":
                holder.ivBarnd.setImageResource(R.drawable.american_express);
                break;
            case "Discover":
                holder.ivBarnd.setImageResource(R.drawable.discover);
                break;
            case "JCB":
                holder.ivBarnd.setImageResource(R.drawable.jcb);
                break;
            case "Diners Club":
                holder.ivBarnd.setImageResource(R.drawable.diners_club_card);
                break;
            case "UnionPay":
                holder.ivBarnd.setImageResource(R.drawable.unionpay_card);
                break;
           /* case "cash" :
                holder.ivBarnd.setImageResource(R.drawable.cash_newnw);
                break;
            case "Credit/debit" :
                holder.ivBarnd.setImageResource(R.drawable.ic_mastercard);
                break;*/
        }


        holder.rlayot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPosition=position;
                notifyDataSetChanged();
            }
        });

        if(selectedPosition==position) {
            holder.ivRadioButton.setImageResource(R.drawable.ic_baseline_radio_button_checked_24);
            holder.etCVV.setVisibility(View.GONE);
            try {
                cardUpdate.selectedCard(cardList.get(position), holder.etCVV, cardList.get(position).getCardType());
            }catch (Exception e){
                e.printStackTrace();
                try {//----------- IF FIREBASE RETURN EXCEPTION
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
                }
            }
        }
        else {
            holder.ivRadioButton.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24);
            holder.etCVV.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    private StringBuilder getCardno(String cardNo) {
        StringBuilder stringBuilder = new StringBuilder();
        int c1=1;
        for(int i=0; i<cardNo.length();i++)
        {
            if(i<(cardNo.length()-4)) {
                if(i!=0 && (i+c1)%5==0) {
                    stringBuilder.append(' ');
                    c1++;
                }
                stringBuilder.append('*');
            }
            else {
                if(i!=0 && (i+c1)%5==0) {
                    stringBuilder.append(' ');
                    c1++;
                }
                stringBuilder.append(cardNo.charAt(i));
            }

        }
        return stringBuilder;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvCardNo,tvCardtype;
        ImageView ivDelete,ivRadioButton,ivBarnd;
        EditText etCVV;
        RelativeLayout rlayot;
        public ViewHolder(View itemView) {
            super(itemView);
            tvCardNo=itemView.findViewById(R.id.tvCardNo);
            ivDelete=itemView.findViewById(R.id.ivDelete);
            ivRadioButton=itemView.findViewById(R.id.ivRadioButton);
            tvCardtype=itemView.findViewById(R.id.tvCardtype);
            ivBarnd=itemView.findViewById(R.id.ivBarnd);
            etCVV=itemView.findViewById(R.id.etCVV);
            rlayot=itemView.findViewById(R.id.rlayot);

            ivDelete.setOnClickListener(this);
            //rlayot.setOnClickListener(this);
           // ivRadioButton.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.ivDelete:

                    try {
                        deleteCard();
                    }catch (Exception e){
                        e.printStackTrace();
                        try {//----------- IF FIREBASE RETURN EXCEPTION
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }
                    break;

                case R.id.rlayot:

                case R.id.ivRadioButton:
              /*      selectedPosition=getAdapterPosition();
                    cardUpdate.selectedCard(cardList.get(getAdapterPosition()));
                    notifyDataSetChanged();*/

                    break;

            }
        }
        public void deleteCard() {
            IOSAlertDialog dialog =  IOSAlertDialog.newInstance(
                    context,
                    null,
                    context.getString(R.string.delete_sure),
                    context.getString(R.string.delete_text),
                    context.getString(R.string.cancel1),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int idd) {
                            selectedPosition = getAdapterPosition();
                            cardUpdate.deleteCard(cardList.get(getAdapterPosition()).get_id());
                            notifyDataSetChanged();
                            dialog.cancel();
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int idd) {
                            dialog.cancel();
                        }
                    },

                    ContextCompat.getColor(context, R.color.coral),
                    ContextCompat.getColor(context, R.color.app_color),
                    false
            );

            dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "ios_dialog");

         /*   AlertDialog dialog=new AlertDialog.Builder(context)
                    .setMessage(context.getString(R.string.delete_sure))
                    .setCancelable(false)
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int idd) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton(R.string.delete,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int idd) {
                                    selectedPosition=getAdapterPosition();
                                    cardUpdate.deleteCard(cardList.get(getAdapterPosition()).get_id());
                                    notifyDataSetChanged();
                                    dialog.cancel();
                                }
                            }).show();
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context,R.color.appColor));
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setAllCaps(false);
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context,R.color.appColor));
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setAllCaps(false);*/

        }
    }
}
