package org.geektimes.reactive.streams.sync;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * (Internal) Subscription Adapter with one {@link Subscriber}
 * 这个就是subscription的实现
 */
class BusinessSubscription implements Subscription {

    private final DecoratingSubscriber<?> subscriber;

    public BusinessSubscription(Subscriber<?> subscriber) {
        this.subscriber = new DecoratingSubscriber(subscriber);
    }

    @Override
    public void request(long n) {
        if (n < 1) {
            throw new IllegalArgumentException("The number of elements to requests must be more than zero!");
        }
        this.subscriber.setMaxRequest(n);
    }

    @Override
    public void cancel() {
        this.subscriber.cancel();
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public Subscriber getSourceSubscriber() {
        return subscriber.getSource();
    }
}
