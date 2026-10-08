package org.labs;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class KitchenTest {

    @Test
    void givesOutExactlyItsStockWhenManyWaitersTakeAtOnce() throws InterruptedException {
        int waiterCount = 8;
        Kitchen kitchen = new Kitchen(1_000_000);
        long[] takenByWaiter = new long[waiterCount];
        Thread[] waiterThreads = new Thread[waiterCount];
        for (int waiterNumber = 0; waiterNumber < waiterCount; waiterNumber++) {
            int counterIndex = waiterNumber;
            waiterThreads[waiterNumber] = Thread.ofPlatform().start(() -> {
                while (kitchen.takePortion()) {
                    takenByWaiter[counterIndex]++;
                }
            });
        }
        for (Thread waiterThread : waiterThreads) {
            waiterThread.join();
        }

        assertEquals(1_000_000, Arrays.stream(takenByWaiter).sum(), "ни одна порция не выдана дважды");
        assertFalse(kitchen.takePortion(), "пустая кухня больше ничего не выдаёт");
    }
}
