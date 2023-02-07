package dmitry.clock.logic.clocks;

import dmitry.clock.logic.exceptions.InvalidTimeException;
import lombok.Builder;

import java.io.Serializable;

public interface Clock extends Serializable {

    long id();

    int hours();
    int minutes();

    int price();
    String mark();

    /**
     * Меняет начальное время на часах соответствующими параметрами
     * @param hours часы
     * @param minutes минуты
     */
    void change(int hours, int minutes) throws InvalidTimeException;

    /**
     * Устанавливает время часам значением по умолчанию
     */
    void reset();

    /**
     * Синхронизировать с текущим временем
     */
    void sync();





}
