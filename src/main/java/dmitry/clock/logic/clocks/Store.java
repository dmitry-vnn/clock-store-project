package dmitry.clock.logic.clocks;

import dmitry.clock.logic.exceptions.ClockNotFoundException;
import dmitry.clock.logic.exceptions.EmptyStoreException;
import dmitry.clock.logic.exceptions.InvalidTimeException;
import lombok.NonNull;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

public interface Store extends Iterable<Clock>, Serializable {

    void add(Clock clock);

    Set<Clock> findAllByMark(@NonNull String mark);

    Set<Clock> findAllByPrice(int price);

    Clock findByMarkAndPrice(@NonNull String mark, int price) throws ClockNotFoundException;

    Clock findById(long id) throws ClockNotFoundException;

    void removeById(@NonNull long id);

    void removeByMarkAndPrice(@NonNull String mark, int price);

    Clock mostExpensive() throws EmptyStoreException;

    void updateAll(int hours, int minutes, int seconds) throws InvalidTimeException;

    void updateAll(int hours, int minutes) throws InvalidTimeException;

    String popularMark() throws EmptyStoreException;

    TreeSet<String> marksLexicographyOrder();

    boolean empty();

    int count();

}
