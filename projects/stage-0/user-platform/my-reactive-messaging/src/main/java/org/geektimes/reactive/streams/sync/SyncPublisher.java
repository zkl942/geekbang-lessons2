package org.geektimes.reactive.streams.sync;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.LinkedList;
import java.util.List;

/**
 * The most prominent issue is that resource consumption needs to be controlled such that
 * a fast data source does not overwhelm the stream destination.
 *
 * The main goal of Reactive Streams is to govern the exchange of stream data across an asynchronous boundary
 * while ensuring that the receiving side is not forced to buffer arbitrary amounts of data.
 *
 * 概念响应式流(Reactive Streams)是以带非阻塞背压方式处理异步数据流的标准，提供一组最小化的接口，
 * 方法和协议来描述必要的操作和实体。 要解决的问题： 系统之间高并发的大量数据流交互通常采用异步的发布-订阅模式。
 *
 * 背压策略:订阅者告诉发布者减慢速率并保持元素，直到订阅者准备好处理更多的元素。
 * 使用背压可确保更快的发布者不会压制较慢的订阅者。 使用背压可能要求发布者拥有无限制的缓冲区，如果它要一直生成和保存元素。
 * 发布者可以实现有界缓冲区来保存有限数量的元素，如果缓冲区已满，可以选择放弃它们。
 *
 * https://github.com/reactive-streams/reactive-streams-jvm/blob/v1.0.3/examples/src/main/java/org/reactivestreams/example/unicast/AsyncIterablePublisher.java
 * @param <T>
 */
public class SyncPublisher<T> implements Publisher<T> {

    private List<Subscriber> subscribers = new LinkedList<>();

    @Override
    public void subscribe(Subscriber<? super T> s) {
        BusinessSubscription subscription = new BusinessSubscription(s);
        s.onSubscribe(subscription);
        // add decorated subscriber
        subscribers.add(subscription.getSubscriber());
    }

    public void publish(T data) {
        // synchronous publish
        subscribers.forEach(subscriber -> {
            subscriber.onNext(data);
        });
    }

    public static void main(String[] args) {
        SyncPublisher publisher = new SyncPublisher();

        for (int i = 0; i < 2; i++) {
            publisher.subscribe(new SyncSubscriber(4));
        }

        /**
         * 这里模拟fast data source
         */
        for (int i = 0; i < 5; i++) {
            publisher.publish(i);
        }
    }
}
