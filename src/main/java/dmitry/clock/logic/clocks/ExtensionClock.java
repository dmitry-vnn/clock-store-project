package dmitry.clock.logic.clocks;

import dmitry.clock.logic.exceptions.InvalidTimeException;

public interface ExtensionClock extends Clock {

    int seconds();

    /** Меняет время на часах соответствующими параметрами
     * @param hours часы
     * @param minutes минуты
     * @param seconds секунды
     */
    void change(int hours, int minutes, int seconds) throws InvalidTimeException;

}
