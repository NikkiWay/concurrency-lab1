package org.labs;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;

final class Programmer implements Runnable {

    private final Lock firstSpoon;
    private final Lock secondSpoon;
    private final ExecutorService waiters;
    private final Kitchen kitchen;
    private final long fairShare;

    private long eatenPortions;

    Programmer(Lock firstSpoon, Lock secondSpoon, ExecutorService waiters, Kitchen kitchen, long fairShare) {
        this.firstSpoon = firstSpoon;
        this.secondSpoon = secondSpoon;
        this.waiters = waiters;
        this.kitchen = kitchen;
        this.fairShare = fairShare;
    }

    @Override
    public void run() {
        try {
            while (eatenPortions < fairShare) {
                if (!orderPortion()) {
                    return; // еда на кухне кончилась
                }
                eat();
            }
        } catch (InterruptedException interruption) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean orderPortion() throws InterruptedException {
        Future<Boolean> order = waiters.submit(kitchen::takePortion);
        try {
            return order.get();
        } catch (ExecutionException taskFailure) {
            throw new IllegalStateException("Waiter failed to bring a portion", taskFailure.getCause());
        }
    }

    private void eat() {
        firstSpoon.lock();
        try {
            secondSpoon.lock();
            try {
                eatenPortions++;
            } finally {
                secondSpoon.unlock();
            }
        } finally {
            firstSpoon.unlock();
        }
    }

    long eatenPortions() {
        return eatenPortions;
    }
}
