package org.labs;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

final class Spoon {

    private final Lock lock = new ReentrantLock();
    private final Condition handedOver = lock.newCondition();
    private final int firstHolderSeat;
    private final int secondHolderSeat;

    private int holderSeat;

    Spoon(int firstHolderSeat, int secondHolderSeat) {
        this.firstHolderSeat = firstHolderSeat;
        this.secondHolderSeat = secondHolderSeat;
        this.holderSeat = firstHolderSeat;
    }

    void take(int seat) throws InterruptedException {
        lock.lock();
        try {
            while (holderSeat != seat) {
                handedOver.await();
            }
        } finally {
            lock.unlock();
        }
    }

    void putDown(int seat) {
        lock.lock();
        try {
            holderSeat = seat == firstHolderSeat ? secondHolderSeat : firstHolderSeat;
            handedOver.signal(); // ждать эту ложку может только сосед
        } finally {
            lock.unlock();
        }
    }
}
