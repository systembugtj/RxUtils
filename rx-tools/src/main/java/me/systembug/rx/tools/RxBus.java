package me.systembug.rx.tools;


import me.systembug.rx.tools.event.AsyncEvent;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by systembug on 4/1/16.
 */
public class RxBus {
    private final Subject<AsyncEvent, AsyncEvent> _bus = new SerializedSubject<>(PublishSubject.create());

    public void send(AsyncEvent o) {
        _bus.onNext(o);
    }

    public Observable<AsyncEvent> toObserverable() {
        return _bus;
    }
}


