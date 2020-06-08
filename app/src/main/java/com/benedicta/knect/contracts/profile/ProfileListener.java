package com.benedicta.knect.contracts.profile;

public interface ProfileListener {

    void onProfileUpdateSuccess();

    void onProfileUpdateFailure(String message);
}
