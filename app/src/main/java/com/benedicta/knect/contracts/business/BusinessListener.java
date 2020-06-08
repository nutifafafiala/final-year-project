package com.benedicta.knect.contracts.business;

import com.benedicta.knect.models.Business;
import com.benedicta.knect.models.BusinessCategory;

import java.util.List;

public interface BusinessListener {

    void onAddBusinessSuccess();

    void onAddBusinessFailure(String message);

    void onLoadBusinessSuccess(List<Business> businesses);

    void onLoadBusinessCategoriesSuccess(List<BusinessCategory> categories);
}
