package com.maktoday.views.favourite;

import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import com.maktoday.utils.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.maktoday.R;
import com.maktoday.adapter.FavouriteAdapter;
import com.maktoday.databinding.FragmentFavouriteBinding;
import com.maktoday.interfaces.OpenMaid;
import com.maktoday.interfaces.UpdateFavourite;
import com.maktoday.model.MaidData;
import com.maktoday.utils.Constants;
import com.maktoday.utils.EndlessRecyclerOnScrollListener;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.dialog.IOSAlertDialog;
import com.maktoday.views.home.HomeFragment;
import com.maktoday.views.maidprofile.MaidProfileFragment;
import com.maktoday.views.main.Main2Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by cbl1005 on 8/2/18.
 */

public class FavouriteFragment extends Fragment implements OpenMaid, FavouriteContract.View, UpdateFavourite {
    private static final String TAG = "FavouriteFragment";
    private final int LIMIT = 10;
    private FragmentFavouriteBinding binding;
    private FavouriteContract.Presenter presenter;
    private FavouriteAdapter favouriteAdapter;
    private List<MaidData> favouriteList = new ArrayList<>();
    private int page = 1;

    public static FavouriteFragment newInstance() {

        Bundle args = new Bundle();
        FavouriteFragment fragment = new FavouriteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: StartActivity");
        //setHasOptionsMenu(true);
        if (HomeFragment.noticount>0){
            Main2Activity.redCircle.setVisibility(View.VISIBLE);
            Main2Activity.countTextView.setText(String.valueOf(HomeFragment.noticount));
        }

        init();
        setData();
        setListeners();

    }

    private void init() {
        presenter = new FavouritePresenter();
        presenter.attachView(this);
        page = 1;
    }

    private void setData() {
        binding.viewFlipper.setDisplayedChild(2);
        binding.swipeRefresh.setRefreshing(false);
        //  binding.title.setText(R.string.chat);

        favouriteAdapter = new FavouriteAdapter(getActivity(), favouriteList, this, this);
        binding.rvFavorite.setAdapter(favouriteAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvFavorite.setLayoutManager(linearLayoutManager);
       // DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), VERTICAL);
       // binding.rvFavorite.addItemDecoration(decoration);

        EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                page = currentPage;
                callAllFavouriteApi();
            }
        };
        endlessRecyclerOnScrollListener.setVisibleThreshold(LIMIT);
        binding.rvFavorite.addOnScrollListener(endlessRecyclerOnScrollListener);

        callAllFavouriteApi();
    }

    private void callAllFavouriteApi() {
        if (GeneralFunction.isNetworkConnected(getActivity(), getView())) {
            binding.swipeRefresh.setRefreshing(true);
            binding.viewFlipper.setDisplayedChild(0);
            HashMap<String, String> map = new HashMap<>();
            map.put("uniquieAppKey", Constants.UNIQUE_APP_KEY);
            map.put("pageNo", String.valueOf(page));
            map.put("limit", String.valueOf(LIMIT));

            presenter.apiFavouriteList(map);
        } else {
            binding.noDataView.setData(getString(R.string.cant_connect));
            binding.noDataView.setImage(ContextCompat.getDrawable(getActivity(), R.drawable.ic_no_internet));
            binding.viewFlipper.setDisplayedChild(1);

        }

    }

    private void setListeners() {
        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                binding.swipeRefresh.setRefreshing(true);
                callAllFavouriteApi();
            }
        });
    }

    @Override
    public void setLoading(boolean isLoading) {
        binding.swipeRefresh.setRefreshing(isLoading);
    }

    @Override
    public void sessionExpired() {
        GeneralFunction.isUserBlocked(getActivity());
    }

    @Override
    public void favouriteSuccess(List<MaidData> data) {
        if (page == 1) {
            favouriteList.clear();
        }
        Log.d(TAG, "favouriteSuccess: "+new Gson().toJson(data));
        favouriteList.addAll(data);
        favouriteAdapter.notifyDataSetChanged();
        binding.swipeRefresh.setRefreshing(false);
        if (favouriteAdapter.getItemCount() == 0) {
            binding.noDataView.setData(getString(R.string.no_favourite_data));
//            binding.noDataView.setImage(ContextCompat.getDrawable(getActivity(), R.drawable.ic_no_data));
            binding.viewFlipper.setDisplayedChild(1);
        } else {
            binding.viewFlipper.setDisplayedChild(2);
        }
        binding.swipeRefresh.setRefreshing(false);
    }

    @Override
    public void removeFavouriteSuccess() {

        page = 1;
        binding.swipeRefresh.setRefreshing(true);
        callAllFavouriteApi();
    }

    @Override
    public void favouriteError(String errorMessage) {
        binding.swipeRefresh.setRefreshing(false);
        binding.noDataView.setData(getString(R.string.cant_connect));
        binding.noDataView.setImage(ContextCompat.getDrawable(getActivity(), R.drawable.ic_no_internet));
        binding.viewFlipper.setDisplayedChild(1);
    }

    @Override
    public void favouriteFailure(String failureMessage) {
        binding.swipeRefresh.setRefreshing(false);
        binding.noDataView.setData(getString(R.string.cant_connect));
        binding.noDataView.setImage(ContextCompat.getDrawable(getActivity(), R.drawable.ic_no_internet));
        binding.viewFlipper.setDisplayedChild(1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
    }

    @Override
    public void removeFavourite(final String maidId) {


        IOSAlertDialog dialog =  IOSAlertDialog.newInstance(
                getContext(),
                null,
                getResources().getString(R.string.favourite_sure),

                getResources().getString(R.string.romove1),
                getResources().getString(R.string.cancel1),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Positive button click logic
                        android.util.Log.e(TAG, "onClick: postive" );
                                                if (GeneralFunction.isNetworkConnected(getActivity(), binding.rvFavorite)) {
                                    presenter.apiRemoveFavourite(maidId);
                                }

                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Negative button click logic
                        android.util.Log.e(TAG, "onClick: negative" );

                       dialog.cancel();

                    }
                },

                ContextCompat.getColor(requireContext(), R.color.coral),
                ContextCompat.getColor(requireActivity(), R.color.app_color),
                false
                );




        dialog.show(requireActivity().getSupportFragmentManager(), "ios_dialog");


//        AlertDialog dialog = new AlertDialog.Builder(getActivity())
//                .setMessage(getResources().getString(R.string.favourite_sure))
//                .setCancelable(false)
//                .setNegativeButton(R.string.cancel1, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int idd) {
//                        dialog.cancel();
//                    }
//                })
//                .setPositiveButton(R.string.romove1,
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int idd) {
//                                if (GeneralFunction.isNetworkConnected(getActivity(), binding.rvFavorite)) {
//                                    presenter.apiRemoveFavourite(maidId);
//                                }
//                            }
//                        }).show();
//        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.app_color));
//        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.app_color));
//

    }

    @Override
    public void openMaidProfile(MaidData maidData) {
        Log.d(TAG, "openMaidProfile: maid profile:--  "+ new Gson().toJson(maidData));
        MaidProfileFragment maidProfileFragment = MaidProfileFragment.newInstance(null, maidData, null, true, "", "");
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .add(android.R.id.content, maidProfileFragment, "MaidProfileFragment").addToBackStack("MaidProfileFragment").commit();

    }
}
