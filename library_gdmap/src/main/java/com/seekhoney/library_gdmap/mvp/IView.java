package com.seekhoney.library_gdmap.mvp;

import android.app.Activity;
import com.tbruyelle.rxpermissions2.RxPermissions;

/**
 * Created by ryan on 18/2/21.
 */

public interface IView
{
    void showLoading();

    void hideLoading();

    Activity getActivity();

    RxPermissions getRxPermission();
}
