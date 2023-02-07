package dmitry.clock.logic.clocks;

import dmitry.clock.logic.exceptions.InvalidTimeException;
import lombok.NonNull;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Objects;

public class DatabasedAccurateClock implements DatabasedClock, ExtensionClock {

    private final ExtensionClock clock;
    private final SessionFactory sessionFactory;

    public DatabasedAccurateClock(@NonNull ExtensionClock clock, @NonNull SessionFactory sessionFactory) {
        this.clock = clock;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public ExtensionClock wrappedClock() {
        return clock;
    }

    @Override
    public long id() {
        return clock.id();
    }

    @Override
    public int hours() {
        return clock.hours();
    }

    @Override
    public int minutes() {
        return clock.minutes();
    }

    @Override
    public int seconds() {
        return clock.seconds();
    }

    @Override
    public void change(int hours, int minutes, int seconds) throws InvalidTimeException {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            clock.change(hours, minutes, seconds);

            session.saveOrUpdate(clock);
            transaction.commit();
        }
    }

    @Override
    public void change(int hours, int minutes) throws InvalidTimeException {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            clock.change(hours, minutes);

            session.saveOrUpdate(clock);
            transaction.commit();
        }
    }

    @Override
    public void reset() {
        clock.reset();
    }

    @Override
    public void sync() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            clock.sync();

            session.saveOrUpdate(clock);
            transaction.commit();
        }
    }

    @Override
    public int price() {
        return clock.price();
    }

    @Override
    public String mark() {
        return clock.mark();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatabasedAccurateClock that = (DatabasedAccurateClock) o;
        return clock.equals(that.clock);
    }


    @Override
    public int hashCode() {
        return Objects.hash(clock);
    }

    @Override
    public String toString() {
        return "DatabasedExtensionClock{" +
                "clock=" + clock +
                '}';
    }

}
