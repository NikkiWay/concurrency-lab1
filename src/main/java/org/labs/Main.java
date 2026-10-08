package org.labs;

/**
 * Аргументы по порядку: число программистов, число официантов, число порций.
 */
public class Main {
    public static void main(String[] arguments) throws InterruptedException {
        int programmerCount = arguments.length > 0 ? Integer.parseInt(arguments[0]) : 7;
        int waiterCount = arguments.length > 1 ? Integer.parseInt(arguments[1]) : 2;
        long portionCount = arguments.length > 2 ? Long.parseLong(arguments[2]) : 1_000_000;

        long[] eatenPortions = Dinner.serve(programmerCount, waiterCount, portionCount);

        long totalEaten = 0;
        for (int seat = 0; seat < eatenPortions.length; seat++) {
            System.out.println("Programmer " + seat + " ate " + eatenPortions[seat] + " portions");
            totalEaten += eatenPortions[seat];
        }
        System.out.println("Total eaten: " + totalEaten + " of " + portionCount);
    }
}
