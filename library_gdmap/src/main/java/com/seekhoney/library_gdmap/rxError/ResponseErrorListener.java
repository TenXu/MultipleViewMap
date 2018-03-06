package com.seekhoney.library_gdmap.rxError;

import android.content.Context;

/**
 * Created by ryan on 18/2/22.
 */

public interface ResponseErrorListener
{
    void handleResponseError(Context context, Throwable t);

    ResponseErrorListener EMPTY = new ResponseErrorListener() {
        @Override
        public void handleResponseError(Context context, Throwable t) {


        }
    };
}
