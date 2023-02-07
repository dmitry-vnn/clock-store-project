package dmitry.clock.logic.clocks;

public interface DatabasedClock extends Clock {

    Clock wrappedClock();

}
