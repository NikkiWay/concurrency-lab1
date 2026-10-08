package org.labs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

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

        ReentrantLock[] spoons = new ReentrantLock[programmerCount];
        for (int spoonNumber = 0; spoonNumber < programmerCount; spoonNumber++) {
            spoons[spoonNumber] = new ReentrantLock(true);
        }
        Kitchen kitchen = new Kitchen(portionCount);

        long baseShare = portionCount / programmerCount;
        long extraPortions = portionCount % programmerCount;
        Programmer[] programmers = new Programmer[programmerCount];
        Thread[] programmerThreads = new Thread[programmerCount];

        try (ExecutorService waiters = Executors.newFixedThreadPool(
                waiterCount, Thread.ofPlatform().name("waiter-", 1).factory())) {
            for (int seat = 0; seat < programmerCount; seat++) {
                int leftSpoonNumber = seat;
                int rightSpoonNumber = (seat + 1) % programmerCount;
                long fairShare = seat < extraPortions ? baseShare + 1 : baseShare;
                programmers[seat] = new Programmer(
                        spoons[Math.min(leftSpoonNumber, rightSpoonNumber)],
                        spoons[Math.max(leftSpoonNumber, rightSpoonNumber)],
                        waiters,
                        kitchen,
                        fairShare);
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
