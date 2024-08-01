package com.maktoday.interfaces;

/**
 * Created by cbl81 on 22/11/17.
 */

public interface ApplyFilter {

    void updateNationalityList(int position,boolean status);
    void updateLanguageList(int position,boolean status);
    void updateGenderList(int position,boolean status);
    void updateMaterialList(int position,boolean status);
    void updateReligionList(int position,boolean status);
    void updateAgencyList(int position,boolean status);
}
