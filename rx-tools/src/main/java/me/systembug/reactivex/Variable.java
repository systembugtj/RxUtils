package me.systembug.reactivex;

/**
 * Created by albert.li on 2/16/18.
 */

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class Variable<T> {
    private T value;

    private final BehaviorSubject<T> subject;

    public Variable(T value) {
        this.value = value;
        subject =  BehaviorSubject.create();
    }

    public synchronized T get() {
        return value;
    }

    public synchronized void set(T value) {
        this.value = value;
        subject.onNext(this.value);
    }

    public Observable<T> asObservable() {
        return subject.toSerialized();
    }
}