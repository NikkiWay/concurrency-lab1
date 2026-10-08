package org.labs;

import java.util.concurrent.atomic.AtomicLong;

final class Kitchen {

    private final AtomicLong remainingPortions;

    Kitchen(long portionCount) {
        remainingPortions = new AtomicLong(portionCount);
    }

    boolean takePortion() {
        while (true) {
            long remaining = remainingPortions.get();
            if (remaining == 0) {
                return false;
            }
            if (remainingPortions.compareAndSet(remaining, remaining - 1)) {
                return true;
            }
        }
    }
}
