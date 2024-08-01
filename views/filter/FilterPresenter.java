package com.maktoday.views.filter;

import com.google.gson.Gson;
import com.maktoday.model.PojoFilter;
import com.maktoday.model.PojoFilterAgency;
import com.maktoday.model.PojoFilterLanguage;
import com.maktoday.utils.Constants;
import com.maktoday.utils.GeneralFunction;
import com.maktoday.utils.Log;
import com.maktoday.utils.Prefs;
import com.maktoday.webservices.RestClient;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.maktoday.utils.Constants.UnAuthorized;

/**
 * Created by cbl81 on 21/11/17.
 */

public class FilterPresenter implements FilterContract.Presenter {

    private FilterContract.View view;

    @Override
    public void apiNationality(HashMap<String, String> map) {
        if (view != null) {

            view.setLoading(true);
            RestClient.getModalApiService().apiListNationality(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), map)
                    .enqueue(new Callback<PojoFilter>() {
                        @Override
                        public void onResponse(Call<PojoFilter> call, Response<PojoFilter> response) {
                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                List<PojoFilterLanguage.Data> nationalityList = new ArrayList<PojoFilterLanguage.Data>();
                                for (String dataTemp : response.body().data) {
                                    PojoFilterLanguage.Data temp = new PojoFilterLanguage.Data();
                                    temp.languageName = dataTemp;
                                    nationalityList.add(temp);
                                }
                                if (view != null) {
                                    view.nationalitySuccess(nationalityList);
                                }

                            } else {
                                if (response.code() == UnAuthorized) {
                                    if (view != null) {
                                        view.sessionExpired();
                                    }
                                } else {
                                    try {
                                        if (view != null) {
                                            view.error(new JSONObject(response.errorBody().string()).getString("message"));
                                        }
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PojoFilter> call, Throwable t) {
                            if (view != null) {
                                GeneralFunction.dismissProgress();
                                view.failure(t.getMessage());
                            }
                        }

                    });
        }
    }

    @Override
    public void apiLanguage(HashMap<String, String> map) {

        if (view != null) {
            Log.e("langauge param", new Gson().toJson(map));
            view.setLoading(true);
            RestClient.getModalApiService().apiListLanguage(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), map)
                    .enqueue(new Callback<PojoFilterLanguage>() {
                        @Override
                        public void onResponse(Call<PojoFilterLanguage> call, Response<PojoFilterLanguage> response) {
                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                view.languageSuccess(response.body().data);
                            } else {
                                if (response.code() == UnAuthorized) {
                                    view.sessionExpired();
                                } else {
                                    try {
                                        view.error(new JSONObject(response.errorBody().string()).getString("message"));
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PojoFilterLanguage> call, Throwable t) {
                            if (view != null) {
                                GeneralFunction.dismissProgress();
                                view.failure(t.getMessage());
                            }
                        }

                    });
        }
    }

    @Override
    public void apiAgency(HashMap<String, String> map) {
        if (view != null) {

            view.setLoading(true);

            RestClient.getModalApiService().apiListAgency(map)
                    .enqueue(new Callback<PojoFilterAgency>() {
                        @Override
                        public void onResponse(Call<PojoFilterAgency> call, Response<PojoFilterAgency> response) {
                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                List<PojoFilterLanguage.Data> agencyList = new ArrayList<PojoFilterLanguage.Data>();

                                for (int i = 0; i < response.body().data.size(); i++) {
                                    PojoFilterLanguage.Data temp = new PojoFilterLanguage.Data();
                                    temp._id = response.body().data.get(i).id;
                                    temp.languageName = response.body().data.get(i).agencyName;

                                    agencyList.add(temp);
                                }
                                if (view != null) {
                                    view.agencySuccess(agencyList);
                                }

                            } else {
                                if (response.code() == UnAuthorized) {
                                    if (view != null) {
                                        view.sessionExpired();
                                    }
                                } else {
                                    try {
                                        if (view != null) {
                                            view.error(new JSONObject(response.errorBody().string()).getString("message"));
                                        }
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PojoFilterAgency> call, Throwable t) {
                            if (view != null) {
                                GeneralFunction.dismissProgress();
                                view.failure(t.getMessage());
                            }
                        }

                    });
        }
    }

    @Override
    public void apiReligion(HashMap<String, String> map) {
        if (view != null) {

            view.setLoading(true);
            RestClient.getModalApiService().apiListReligion(Prefs.get().getString(Constants.ACCESS_TOKEN, ""), map)
                    .enqueue(new Callback<PojoFilter>() {
                        @Override
                        public void onResponse(Call<PojoFilter> call, Response<PojoFilter> response) {
                            GeneralFunction.dismissProgress();
                            if (response.isSuccessful()) {
                                List<PojoFilterLanguage.Data> religionList = new ArrayList<PojoFilterLanguage.Data>();
                                for (String dataTemp : response.body().data) {
                                    PojoFilterLanguage.Data temp = new PojoFilterLanguage.Data();
                                    temp.languageName = dataTemp;
                                    religionList.add(temp);
                                }
                                if (view != null) {
                                    view.religionSuccess(religionList);
                                }
                            } else {
                                if (response.code() == UnAuthorized) {
                                    view.sessionExpired();
                                } else {
                                    try {
                                        view.error(new JSONObject(response.errorBody().string()).getString("message"));
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PojoFilter> call, Throwable t) {
                            if (view != null) {
                                GeneralFunction.dismissProgress();
                                view.failure(t.getMessage());
                            }
                        }
                    });
        }
    }

    @Override
    public void attachView(FilterContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }
}
