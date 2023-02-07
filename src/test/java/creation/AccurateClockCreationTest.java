package creation;

import dmitry.clock.logic.clocks.AccurateClock;
import dmitry.clock.logic.exceptions.EmptyMarkException;
import dmitry.clock.logic.exceptions.InvalidPriceException;
import dmitry.clock.logic.exceptions.InvalidTimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AccurateClockCreationTest {

    @Test
    void createClock() {
        Assertions.assertDoesNotThrow(() -> new AccurateClock(10, 10, 10, "Rolex", 53));
    }

    @Test
    void createClockWithInvalidPrice() {
        Assertions.assertThrows(InvalidPriceException.class, () -> new AccurateClock(10, 10,10, "Rolex", 0));
    }

    @Test
    void createClockWithOutOfMaxHourValue() {
        Assertions.assertThrows(InvalidTimeException.class, () -> new AccurateClock(25, 10,10, "Rolex", 45));
    }

    @Test
    void createClockWithNegativeHourValue() {
        Assertions.assertThrows(InvalidTimeException.class, () -> new AccurateClock(-2, 10,10, "Rolex", 45));
    }

    @Test
    void createClockWithInvalidMinutes() {
        Assertions.assertThrows(InvalidTimeException.class, () -> new AccurateClock(2, 985,10, "Rolex", 45));
    }

    @Test
    void createClockWithInvalidSeconds() {
        Assertions.assertThrows(InvalidTimeException.class, () -> new AccurateClock(2, 1,60, "Rolex", 45));
    }

    @Test
    void createClockWithNullMark() {
        Assertions.assertThrows(NullPointerException.class, () -> new AccurateClock(2, 2,10, null, 45));
    }

    @Test
    void createClockWithEmptyMark() {
        Assertions.assertThrows(EmptyMarkException.class, () -> new AccurateClock(2, 2,10, "  ", 45));
    }


}
