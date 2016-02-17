package com.opusreverie.oghma.launcher.helper;

import rx.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by keen on 16/02/16.
 */
public class ReactiveResult<T> {

    private List<T> emitted = new ArrayList<>();
    private List<Throwable> errors = new ArrayList<>();
    private AtomicBoolean completed = new AtomicBoolean(false);

    public static <T> ReactiveResult<T> of(Observable<T> stream) {
        ReactiveResult<T> result = new ReactiveResult<>();
        stream.toBlocking().subscribe(
                x -> result.getEmitted().add(x),
                ex -> result.getErrors().add(ex),
                () -> result.getCompleted().set(true));
        return result;
    }

    public void throwAny() {
        if (!errors.isEmpty()) throw new RuntimeException(errors.get(0));
    }

    public List<T> getEmitted() {
        return emitted;
    }

    public List<Throwable> getErrors() {
        return errors;
    }

    public AtomicBoolean getCompleted() {
        return completed;
    }
}
