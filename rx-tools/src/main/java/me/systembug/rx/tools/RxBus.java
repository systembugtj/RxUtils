package me.systembug.rx.tools;


import me.systembug.rx.tools.event.AsyncEvent;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by systembug on 4/1/16.
 */
public final class RxBus {
    private final PublishSubject<AsyncEvent> bus = PublishSubject.create();

    public void send(final AsyncEvent event) {
        bus.onNext(event);
    }

    public Observable<AsyncEvent> toObservable() {
        return bus;
    }

    public boolean hasObservers() {
        return bus.hasObservers();
    }
}

