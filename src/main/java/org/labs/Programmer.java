package org.labs;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

final class Programmer implements Runnable {

    private final int seat;
    private final Spoon leftSpoon;
    private final Spoon rightSpoon;
    private final ExecutorService waiters;
    private final Kitchen kitchen;

    private long eatenPortions;

    Programmer(int seat, Spoon leftSpoon, Spoon rightSpoon, ExecutorService waiters, Kitchen kitchen) {
        this.seat = seat;
        this.leftSpoon = leftSpoon;
        this.rightSpoon = rightSpoon;
        this.waiters = waiters;
        this.kitchen = kitchen;
    }

    @Override
    public void run() {
        try {
            boolean kitchenHasFood = true;
            while (kitchenHasFood) {
                leftSpoon.take(seat);
                try {
                    rightSpoon.take(seat);
                    try {
                        kitchenHasFood = orderPortion();
                        if (kitchenHasFood) {
                            eatenPortions++;
                        }
                    } finally {
                        rightSpoon.putDown(seat);
                    }
                } finally {
                    leftSpoon.putDown(seat);
                }
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

    long eatenPortions() {
        return eatenPortions;
    }
}
