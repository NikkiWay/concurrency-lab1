package org.labs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

final class Dinner {

    private Dinner() {
    }

    static long[] serve(int programmerCount, int waiterCount, long portionCount) throws InterruptedException {
        if (programmerCount < 2) {
            throw new IllegalArgumentException("At least 2 programmers are required, got " + programmerCount);
        }
        if (waiterCount < 1) {
            throw new IllegalArgumentException("At least 1 waiter is required, got " + waiterCount);
        }
        if (portionCount < 0) {
            throw new IllegalArgumentException("Portion count must not be negative, got " + portionCount);
        }

        Spoon[] spoons = new Spoon[programmerCount];
        for (int spoonNumber = 0; spoonNumber < programmerCount; spoonNumber++) {
            int previousSeat = (spoonNumber + programmerCount - 1) % programmerCount;
            int nextSeat = spoonNumber;
            spoons[spoonNumber] = nextSeat % 2 == 0
                    ? new Spoon(nextSeat, previousSeat)
                    : new Spoon(previousSeat, nextSeat);
        }
        Kitchen kitchen = new Kitchen(portionCount);

        Programmer[] programmers = new Programmer[programmerCount];
        Thread[] programmerThreads = new Thread[programmerCount];

        try (ExecutorService waiters = Executors.newFixedThreadPool(
                waiterCount, Thread.ofPlatform().name("waiter-", 1).factory())) {
            for (int seat = 0; seat < programmerCount; seat++) {
                programmers[seat] = new Programmer(
                        seat,
                        spoons[seat],
                        spoons[(seat + 1) % programmerCount],
                        waiters,
                        kitchen);
                programmerThreads[seat] = Thread.ofPlatform()
                        .name("programmer-" + seat)
                        .start(programmers[seat]);
            }
            for (Thread programmerThread : programmerThreads) {
                programmerThread.join();
            }
        }

        long[] eatenPortions = new long[programmerCount];
        for (int seat = 0; seat < programmerCount; seat++) {
            eatenPortions[seat] = programmers[seat].eatenPortions();
        }
        return eatenPortions;
    }
}
