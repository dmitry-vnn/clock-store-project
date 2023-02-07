package dmitry.clock.logic.clocks;

import dmitry.clock.logic.exceptions.ClockException;
import dmitry.clock.logic.exceptions.EmptyMarkException;
import dmitry.clock.logic.exceptions.InvalidPriceException;
import dmitry.clock.logic.exceptions.InvalidTimeException;
import lombok.NonNull;

import javax.persistence.*;
import java.util.Objects;
import java.util.function.Supplier;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)

public class StandardClock implements Clock {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;

    @Column int startHours;
    @Column int startMinutes;

    @Column String mark;
    @Column int price;

    @Transient transient long startTimeMillis = System.currentTimeMillis();



    public StandardClock(int startHours, int startMinutes, @NonNull String mark, int price) throws InvalidTimeException, InvalidPriceException {
        this.startHours = validateHours(startHours);
        this.startMinutes = validateMinutes(startMinutes);
        this.price = validateRange(price, 1, Integer.MAX_VALUE, InvalidPriceException::new);

        this.mark = validateMark(mark);
    }


    public StandardClock() {

    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public int hours() {
        long currentTimeMillis = System.currentTimeMillis();
        return (int) ((((currentTimeMillis - startTimeMillis) / (1000 * 60 * 60)) + startHours) % 24);
    }

    @Override
    public int minutes() {
        long currentTimeMillis = System.currentTimeMillis();
        return (int) ((((currentTimeMillis - startTimeMillis) / (1000 * 60)) + startMinutes) % 60);
    }

    @Override
    public void change(int hours, int minutes) throws InvalidTimeException {

        int tempHours = validateHours(hours);
        int tempMinutes = validateMinutes(minutes);

        startHours = tempHours;
        startMinutes = tempMinutes;

    }

    @Override
    public void reset() {
        startTimeMillis = System.currentTimeMillis();
    }

    @Override
    public void sync() {
        startHours = (int) ((System.currentTimeMillis() / (1000 * 60 * 60) + 3) % 24);
        startMinutes = (int) ((System.currentTimeMillis()) / (1000 * 60) % 60);
    }

    @Override
    public int price() {
        return price;
    }

    @Override
    public String mark() {
        return mark;
    }

    private String validateMark(String mark) {
        if (mark.trim().isEmpty()) {
            throw new EmptyMarkException();
        }
        return mark;
    }

    protected final int validateMinutes(int value) throws InvalidTimeException {
        return validateRange(value, 0, 60, InvalidTimeException::new);
    }

    protected final <T extends ClockException> int validateRange(int value, int low, int high, Supplier<T> orThrow) throws T {
        if (value < low || value >= high) {
            throw orThrow.get();
        }
        return value;
    }

    protected final int validateHours(int value) throws InvalidTimeException {
        return validateRange(value, 0, 24, InvalidTimeException::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StandardClock that = (StandardClock) o;
        return id == that.id && startHours == that.startHours && startMinutes == that.startMinutes && price == that.price && mark.equals(that.mark);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startHours, startMinutes, mark, price);
    }

    @Override
    public String toString() {
        return "StandardClock{" +
                "id=" + id +
                ", startHours=" + startHours +
                ", startMinutes=" + startMinutes +
                ", mark='" + mark + '\'' +
                ", price=" + price +
                ", startTimeMillis=" + startTimeMillis +
                '}';
    }

}
