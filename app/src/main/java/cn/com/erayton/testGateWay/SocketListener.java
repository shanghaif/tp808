package cn.com.erayton.testGateWay;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class SocketListener implements Observer<BaseRequestBean> {

    abstract void onSuccess(BaseRequestBean bean);
    abstract void onExecption(Throwable throwable);

    Disposable disposable;
    @Override
    public void onSubscribe(Disposable disposable) {
        this.disposable = disposable;
    }

    @Override
    public void onNext(BaseRequestBean bean) {
        onSuccess(bean);
    }

    @Override
    public void onError(Throwable throwable) {
        onExecption(throwable);
    }

    @Override
    public void onComplete() {

    }

    /**
     * 取消socket 监听
     */
    public void cancleListen() {
        if (disposable!=null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
