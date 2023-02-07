package dmitry.clock.logic.clocks;

import dmitry.clock.logic.exceptions.InvalidTimeException;
import lombok.NonNull;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Objects;

public class DatabasedStandardClock implements DatabasedClock {

    private final Clock clock;
    private final SessionFactory sessionFactory;

    public DatabasedStandardClock(@NonNull Clock clock, @NonNull SessionFactory sessionFactory) {
        this.clock = clock;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Clock wrappedClock() {
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
    public void change(int hours, int minutes) throws InvalidTimeException {
        System.out.println(this);
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            clock.change(hours, minutes);

            session.saveOrUpdate(clock);
            transaction.commit();
        }
        System.out.println(this);
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
        DatabasedStandardClock that = (DatabasedStandardClock) o;
        return clock.equals(that.clock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clock);
    }

    @Override
    public String toString() {
        return "DatabasedStandardClock{" +
                "clock=" + clock +
                '}';
    }

}
