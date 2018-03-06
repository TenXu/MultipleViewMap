package com.seekhoney.library_gdmap.rxError;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by ryan on 18/2/22.
 */

public abstract class ErrorHandleSubscriber<T> implements Observer<T>
{
    private ErrorHandlerFactory factory;

    public ErrorHandleSubscriber(ErrorHandlerFactory factory){
        this.factory = factory;
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onComplete() {

    }


    @Override
    public void onError(Throwable e) {
        factory.handleError(e);
    }
}
