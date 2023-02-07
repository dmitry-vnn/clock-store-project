package process;

import dmitry.clock.logic.clocks.AccurateClock;
import dmitry.clock.logic.clocks.StandardClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccurateClockProcessionTest {

    static AccurateClock clock;

    @BeforeEach
    void setUp() {
        clock = new AccurateClock(10, 39, 59, "Rolex", 105);
    }

    @Test
    void correctProcess() {


        assertEquals(clock.hours(), 10);
        assertEquals(clock.minutes(), 39);
        assertEquals(clock.seconds(), 59);
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
    void changeTimeTest() throws InterruptedException {

        LocalDateTime localDateTime = LocalDateTime.now();
        clock.sync();


        assertEquals(clock.hours(), localDateTime.getHour());
        assertEquals(clock.minutes(), (localDateTime.getMinute() - 20) % 60);

        Thread.sleep(2000);

        assertEquals(clock.seconds(), (localDateTime.getSecond() + 2) % 60);


    }

}
