package com.seekhoney.library_gdmap.utils;

import android.Manifest;
import com.seekhoney.library_gdmap.rxError.ErrorHandleSubscriber;
import com.seekhoney.library_gdmap.rxError.RxErrorHandler;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ryan on 18/2/22.
 */

public class PermisionUtil
{
    public interface RequestPermisionResult{
        void onRequestPermissionSuccess();
        void onRequestPermissionFailure(List<String> permissions);
        void onRequestPermissionFailureNeverAsk(List<String> permissions);

    }

    public static void requestPermision(final RequestPermisionResult result , RxPermissions rxPermissions,
                                        final RxErrorHandler rxErrorHandler , String... permissions){
        if(permissions == null || permissions.length == 0) return;
        List <String> needReq = new ArrayList<>();
        for(String p : permissions){
            if(!rxPermissions.isGranted(p)){
                needReq.add(p);
            }
        }

        if(needReq.isEmpty()){
            result.onRequestPermissionSuccess();
        }else {
            rxPermissions.
                    requestEach(needReq.toArray(new String[needReq.size()])).
                    buffer(needReq.size()).
                    subscribe(new ErrorHandleSubscriber<List<Permission>>(rxErrorHandler.getHandlerFactory()) {
                        @Override
                        public void onNext(List<Permission> permissions) {
                            for(Permission permission: permissions){

                                if(!permission.granted){
                                    if(permission.shouldShowRequestPermissionRationale){
                                        result.onRequestPermissionFailure(Arrays.asList(permission.name));
                                    }else {
                                        result.onRequestPermissionFailureNeverAsk(Arrays.asList(permission.name));
                                    }
                                }


                            }

                            result.onRequestPermissionSuccess();
                        }
                    });

        }

    }


    public static void getLocation(RxPermissions rxPermissions,RxErrorHandler rxErrorHandler ,RequestPermisionResult result ){

        requestPermision(result,rxPermissions,rxErrorHandler,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION});
    }
}
