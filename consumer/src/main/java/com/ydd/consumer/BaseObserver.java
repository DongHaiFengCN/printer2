package com.ydd.consumer;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author dong
 * @param <T>
 */

public abstract class BaseObserver<T> implements Observer<T> {
    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T t) {
        try {
            onCustomNext(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Throwable e) {


        try {
            onCustomError(RxExceptionUtil.exceptionHandler(e));
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    @Override
    public void onComplete() {

    }
    protected abstract void onCustomNext(T o) throws Exception;
    protected abstract void onCustomError(String o) throws Exception;
}
