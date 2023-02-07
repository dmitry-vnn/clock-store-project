package dmitry.clock.logic.clocks;


import dmitry.clock.logic.exceptions.InvalidTimeException;
import lombok.NonNull;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class AccurateClock extends StandardClock implements ExtensionClock {

    @Column
    private int startSeconds;

    public AccurateClock(int startHours, int startMinutes, int startSeconds, @NonNull String mark, int price) throws InvalidTimeException {
        super(startHours, startMinutes, mark, price);

        this.startSeconds = validateSeconds(startSeconds);
    }

    /**
     * public non-param constructor for JPA
     */
    public AccurateClock() {
        
    }

    @Override
    public String toString() {
        return "AccurateClock{" +
                "startSeconds=" + startSeconds +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccurateClock)) return false;
        if (!super.equals(o)) return false;
        AccurateClock that = (AccurateClock) o;
        return startSeconds == that.startSeconds;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), startSeconds);
    }

    @Override
    public void change(int hours, int minutes, int seconds) throws InvalidTimeException {
        super.change(hours, minutes);

        this.startSeconds = validateSeconds(seconds);
    }

    protected final int validateSeconds(int value) throws InvalidTimeException {
        return validateMinutes(value);
    }

    @Override
    public int seconds() {
        long currentTimeMillis = System.currentTimeMillis();
        return (int) ((((currentTimeMillis - startTimeMillis) / (1000)) + startSeconds) % 60);
    }

    @Override
    public void sync() {
        super.sync();
        startSeconds = (int) ((System.currentTimeMillis()) / (1000) % 60);
    }
}
