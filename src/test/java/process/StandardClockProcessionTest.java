package process;

import dmitry.clock.logic.clocks.StandardClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StandardClockProcessionTest {

    static StandardClock clock;

    @BeforeEach
    void setUp() {
        clock = new StandardClock(10, 39, "Rolex", 105);
    }

    @Test
    void correctProcess() {
        assertEquals(clock.hours(), 10);
        assertEquals(clock.minutes(), 39);
        assertEquals(clock.mark(), "Rolex");
        assertEquals(clock.price(), 105);
    }


    @Test
    void checkSyncTime() {

        LocalDateTime localDateTime = LocalDateTime.now();
        clock.sync();

        assertEquals(clock.hours(), localDateTime.getHour());
        assertEquals(clock.minutes(),  localDateTime.getMinute());
    }


    @Test
    void changeTimeTest() {

        LocalDateTime localDateTime = LocalDateTime.now();
        clock.sync();

        clock.change(clock.hours(), (clock.minutes() - 20) % 60);

        assertEquals(clock.hours(), localDateTime.getHour());
        assertEquals(clock.minutes(), (localDateTime.getMinute() - 20) % 60);
    }

}
