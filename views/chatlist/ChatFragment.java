package com.maktoday.views.chatlist;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.maktoday.utils.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maktoday.R;
import com.maktoday.adapter.ChatAdapter;
import com.maktoday.databinding.FragmentChatBinding;
import com.maktoday.model.PojoChatList;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.views.home.HomeFragment;
import com.maktoday.views.main.Main2Activity;

import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.VERTICAL;

/**
 * Created by cbl1005 on 24/1/18.
 */

public class ChatFragment extends Fragment implements ChatContract.View{
    private static final String TAG = "ChatFragment";
    private FragmentChatBinding binding;
    private ChatAdapter chatAdapter;
    private ChatContract.Presenter presenter;
    private List<PojoChatList> chatList = new ArrayList<>();
    public static ChatFragment newInstance() {
        Bundle args = new Bundle();
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: StartActivity");
       // setHasOptionsMenu(true);
        init();
        setData();
        setListeners();
        if (HomeFragment.noticount>0){
            Main2Activity.redCircle.setVisibility(View.VISIBLE);
            Main2Activity.countTextView.setText(String.valueOf(HomeFragment.noticount));
        }
    }

    private void init() {
        presenter = new ChatPresenter();
        presenter.attachView(this);
    }

    private void setData() {
        binding.viewFlipper.setDisplayedChild(2);
        binding.swipeRefresh.setRefreshing(false);

        chatAdapter = new ChatAdapter(getActivity(), chatList);
        binding.rvChat.setAdapter(chatAdapter);
        binding.rvChat.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), VERTICAL);
        binding.rvChat.addItemDecoration(decoration);

        callAllChatApi();
    }

    private void callAllChatApi() {
        if (GeneralFunction.isNetworkConnected(getActivity(), getView())) {
            binding.swipeRefresh.setRefreshing(true);
            binding.viewFlipper.setDisplayedChild(0);
            presenter.apiGetAllChat();
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
                callAllChatApi();
            }
        });
    }

    @Override
    public void successGetAllChat(List<PojoChatList> data) {
        if (data.size() != 0) {
            chatList.clear();
            chatList.addAll(data);
            chatAdapter.notifyDataSetChanged();
            binding.viewFlipper.setDisplayedChild(2);
        } else {
            binding.noDataView.setData(getString(R.string.no_chat_found));
         //   binding.noDataView.setImage(ContextCompat.getDrawable(getActivity(), R.drawable.ic_no_data));
            binding.viewFlipper.setDisplayedChild(1);
        }
        binding.swipeRefresh.setRefreshing(false);
    }

    @Override
    public void setLoading(boolean isLoading) {
        if (isLoading)
            GeneralFunction.showProgress(getActivity());
        else
            GeneralFunction.dismissProgress();
    }

    @Override
    public void sessionExpired() {
        GeneralFunction.isUserBlocked(getActivity());
    }

    @Override
    public void chatError(String errorFailure) {

        binding.swipeRefresh.setRefreshing(false);
        binding.noDataView.setData(getString(R.string.cant_connect));
        binding.noDataView.setImage(ContextCompat.getDrawable(getActivity(), R.drawable.ic_no_internet));
        binding.viewFlipper.setDisplayedChild(1);
    }

    @Override
    public void chatFailure(String failureMessage) {

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


}
