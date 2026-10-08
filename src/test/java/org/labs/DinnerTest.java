package org.labs;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DinnerTest {
    private static final Duration TIME_LIMIT = Duration.ofSeconds(60);

    @ParameterizedTest(name = "программистов {0}, официантов {1}, порций {2}")
    @CsvSource({
            "7, 2, 1000000",
            "2, 1, 10000",
            "3, 3, 10000",
            "5, 1, 100000",
            "10, 4, 100000",
            "7, 10, 1000",
            "7, 2, 3",
            "7, 2, 0"
    })
    void dinnerEndsAllFoodIsEatenAndSharedFairly(int programmerCount, int waiterCount, long portionCount) {
        long[] eatenPortions = assertTimeoutPreemptively(
                TIME_LIMIT, () -> Dinner.serve(programmerCount, waiterCount, portionCount));

        assertEquals(programmerCount, eatenPortions.length);
        assertEquals(portionCount, Arrays.stream(eatenPortions).sum(), "съеден ровно весь запас");
        long smallestShare = Arrays.stream(eatenPortions).min().orElseThrow();
        long largestShare = Arrays.stream(eatenPortions).max().orElseThrow();
        assertTrue(largestShare - smallestShare <= 1,
                "съедено от " + smallestShare + " до " + largestShare + ", а разница должна быть не больше одной порции");
    }

    @RepeatedTest(50)
    void repeatedDinnersNeverHang() {
        long[] eatenPortions = assertTimeoutPreemptively(TIME_LIMIT, () -> Dinner.serve(7, 2, 20_000));

        assertEquals(20_000, Arrays.stream(eatenPortions).sum());
    }

    @Test
    void rejectsImpossibleParameters() {
        assertThrows(IllegalArgumentException.class, () -> Dinner.serve(1, 2, 100));
        assertThrows(IllegalArgumentException.class, () -> Dinner.serve(7, 0, 100));
        assertThrows(IllegalArgumentException.class, () -> Dinner.serve(7, 2, -1));
    }
}
