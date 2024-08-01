package com.maktoday.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.maktoday.R;
import com.maktoday.interfaces.SelectAddress;
import com.maktoday.model.FullAddress;
import com.maktoday.utils.Log;
import com.maktoday.utils.dialog.IOSAlertDialog;
import com.maktoday.views.confirmbook.ConfirmBookFragment;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.ArrayList;

/**
 * Created by cbl81 on 9/12/17.
 */

public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.MyViewHolder> {

    public ArrayList<FullAddress> addressList=new ArrayList<>();
    private Activity context;
    private SelectAddress selectAddress;
    private int selectedPosition=-1;
    TextView tvProceedPayment;

    public AddressListAdapter(Activity context, ArrayList<FullAddress> addressList, SelectAddress selectAddress, TextView tvProceedPayment) {

        this.context=context;
        this.tvProceedPayment=tvProceedPayment;
        this.addressList=addressList;
        this.selectAddress=selectAddress;
    }

    @Override
    public AddressListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=context.getLayoutInflater();
        View view=inflater.inflate(R.layout.item_addresslist,parent,false);
        return new AddressListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AddressListAdapter.MyViewHolder holder, int position) {

        FullAddress fullAddress=addressList.get(position);
        Log.e("fulladress==",new Gson().toJson(fullAddress));

        if(fullAddress.country.equalsIgnoreCase("Bahrain")) {
            try {//-------------Exception handle building number
                if (fullAddress.buildingNumber.isEmpty()) {
                    holder.tvbulidingNo.setVisibility(View.GONE);
                } else {
                    holder.tvbulidingNo.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
                }

            }
            try {// ---------Exception handle villa name
                holder.tvVillaNo.setText(String.format("%s %s", context.getString(R.string.label_villa_2), fullAddress.villaName));
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
                }
            }
            try {//---------- Exception handle building number
                holder.tvbulidingNo.setText(String.format("%s %s", context.getString(R.string.label_building_2), fullAddress.buildingNumber));
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
                }

            }
            try {//--------------Exception handle building name
                holder.tvBlockNo.setText(String.format("%s %s", context.getString(R.string.label_block_2), fullAddress.buildingName));
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
                }
            }
            try {//-------------Exception handle street name
                holder.tvRoadNo.setText(String.format("%s %s", context.getString(R.string.label_road_2), fullAddress.streetName));
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
                }

            }
        }else {
            // check empty value
            try {//-------Exception handle building name
                if (fullAddress.buildingName.isEmpty()) {
                    holder.tvVillaNo.setVisibility(View.GONE);
                } else {
                    holder.tvVillaNo.setVisibility(View.VISIBLE);
                    holder.tvVillaNo.setText(String.format("%s %s", context.getString(R.string.label_flat), fullAddress.buildingName));
                }
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
                }

            }
            try {//------- Street name
                holder.tvbulidingNo.setText(String.format("%s %s", context.getString(R.string.label_street), fullAddress.streetName));
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
                }

            }
            try {//---------building number
                holder.tvBlockNo.setText(String.format("%s %s", context.getString(R.string.label_building_uk), fullAddress.buildingNumber));
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
                }

            }
            try {//-------- Exception handle postal code
                holder.tvRoadNo.setText(String.format("%s %s", context.getString(R.string.label_postalcode), fullAddress.postalCode));
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    FirebaseCrashlytics.getInstance().recordException(e);
                } catch (Exception fe) {
                    fe.printStackTrace();
                }

            }
        }

        try {//------ Exception handle moreDetailedaddress
            if (fullAddress.moreDetailedaddress != null && !fullAddress.moreDetailedaddress.isEmpty()) {
                holder.tvAddionalDetails.setVisibility(View.VISIBLE);
                holder.tvAddionalDetails.setText(String.format("%s %s", context.getString(R.string.label_additionaldetails), fullAddress.moreDetailedaddress));
            } else {
                holder.tvAddionalDetails.setVisibility(View.INVISIBLE);
                holder.tvAddionalDetails.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }

        }
        try {//  Exception handle city country
            holder.tvCity.setText(String.format("%s %s, %s", context.getString(R.string.label_city), fullAddress.city, fullAddress.country));
        } catch (Exception e) {
            e.printStackTrace();

            try {
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
        }
        try {//----Exception handle----selectedPosition
            if (selectedPosition == position) {
                holder.ivCheck.setImageResource(R.drawable.ic_tick_selected_round);
                //holder.rlParent.setBackground(ContextCompat.getDrawable(context,R.drawable.rect_border_sky_white));
            } else {
                holder.ivCheck.setImageResource(R.drawable.ic_unselected_round);
                //   holder.rlParent.setBackground(ContextCompat.getDrawable(context,R.drawable.rect_white));
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception fe) {
                fe.printStackTrace();
            }

        }
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvVillaNo,tvBlockNo,tvAddionalDetails,tvRoadNo,tvCity,tvDelete,tvbulidingNo;
        private RelativeLayout rlParent;
        private ImageView ivCheck;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvVillaNo=itemView.findViewById(R.id.tvVillaNo);
            tvBlockNo=itemView.findViewById(R.id.tvBlockNo);
            tvRoadNo=itemView.findViewById(R.id.tvRoadNo);
            tvCity=itemView.findViewById(R.id.tvCity);
            tvbulidingNo=itemView.findViewById(R.id.tvbulidingNo);
            tvAddionalDetails=itemView.findViewById(R.id.tvAdditionalDetails);
            ivCheck=itemView.findViewById(R.id.ivCheck);
            tvDelete=itemView.findViewById(R.id.tvDelete);
            rlParent=itemView.findViewById(R.id.rlParent);

            tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {//-------------------Exception handle deleteCard Method
                        deleteCard();
                    }catch (Exception e){
                        e.printStackTrace();
                        try {
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {//--------- Exception handle selected position
//                        if (selectedPosition == getAdapterPosition()) {
                        if (selectedPosition == getBindingAdapterPosition()) {
                            if (ConfirmBookFragment.latLng != null) {
                            } else {
                                tvProceedPayment.setVisibility(View.GONE);
                            }
                            selectedPosition = -1;
                            notifyDataSetChanged();
                            selectAddress.getAddress(null);
                        } else {
                            tvProceedPayment.setVisibility(View.VISIBLE);
//                            selectedPosition = getAdapterPosition();
                            selectedPosition = getBindingAdapterPosition();
                            notifyDataSetChanged();
                            selectAddress.getAddress(addressList.get(selectedPosition));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } catch (Exception fe) {
                            fe.printStackTrace();
                        }
                    }
                }
            });

        }

        public void deleteCard()
        {

            IOSAlertDialog dialog =  IOSAlertDialog.newInstance(
                    context,
                    null,
                    context.getResources().getString(R.string.address_sure),
                    context.getString(R.string.delete_text),
                    context.getString(R.string.cancel1),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int idd) {
                            try {
                                selectAddress.deleteAddress(addressList.get(getAdapterPosition()).id);
                            } catch (Exception e) {
                                try {
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                } catch (Exception fe) {
                                    fe.printStackTrace();
                                }
                                e.printStackTrace();
                            }
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
                    .setMessage(context.getResources().getString(R.string.address_sure))
                    .setCancelable(false)
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int idd) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton(R.string.yes,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int idd) {
                                    try {//---------- Exception delete address
                                        selectAddress.deleteAddress(addressList.get(getAdapterPosition()).id);
                                    } catch (Exception e) {
                                        try {
                                            FirebaseCrashlytics.getInstance().recordException(e);
                                        } catch (Exception fe) {
                                            fe.printStackTrace();
                                        }
                                        e.printStackTrace();
                                    }
                                }
                            }).show();

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.appColor));
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setAllCaps(false);
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.appColor));
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setAllCaps(false);
*/
        }
    }
}
