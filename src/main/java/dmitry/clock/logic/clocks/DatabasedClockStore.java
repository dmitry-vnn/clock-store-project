package dmitry.clock.logic.clocks;

import dmitry.clock.logic.exceptions.ClockNotFoundException;
import dmitry.clock.logic.exceptions.EmptyStoreException;
import dmitry.clock.logic.exceptions.InvalidTimeException;
import lombok.Getter;
import lombok.NonNull;
import lombok.var;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.*;
import java.util.stream.Collectors;

public class DatabasedClockStore implements Store {

    @Getter
    private final ClockStore wrappedClockStore;

    private SessionFactory sessionFactory;

    private final Set<DatabasedClock> databasedClocks;

    public DatabasedClockStore(@NonNull ClockStore store, @NonNull Strategy strategy) {

        this.wrappedClockStore = store;

        registerSessionFactory();

        this.databasedClocks = wrappedClockStore.getClocks().stream().map(clock -> {
            if (clock instanceof ExtensionClock) {
                return new DatabasedAccurateClock((ExtensionClock) clock, sessionFactory);
            } else  {
                return new DatabasedStandardClock(clock, sessionFactory);
            }
        }).collect(Collectors.toSet());

        saveExistsClocksToDatabase();

        if (strategy == Strategy.LOAD_PREVIOUS_DATA) {
            loadClocksFormDatabase();
        }
    }

    public enum Strategy {

        LOAD_PREVIOUS_DATA,
        NOT_LOAD_PREVIOUS_DATA;

    }



    @Override
    public void add(Clock clock)  {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.saveOrUpdate(clock);
            transaction.commit();
        }
        System.out.println(clock.id());
        if (clock instanceof ExtensionClock) {
            databasedClocks.add(new DatabasedAccurateClock((ExtensionClock) clock, sessionFactory));
        } else {
            databasedClocks.add(new DatabasedStandardClock(clock, sessionFactory));
        }
        wrappedClockStore.add(clock);

    }

    @Override
    public Set<Clock> findAllByMark(@NonNull String mark) {
        return databasedClocks.stream().filter(clock -> {
            String clockMark = clock.mark().toLowerCase();
            String comparableMark = mark.toLowerCase();

            return clockMark.startsWith(comparableMark) || clockMark.contains(comparableMark);

        }).collect(Collectors.toSet());
    }

    @Override
    public Set<Clock> findAllByPrice(int price) {
        return databasedClocks.stream().filter(clock -> clock.price() == price).collect(Collectors.toSet());
    }

    @Override
    public Clock findByMarkAndPrice(@NonNull String mark, int price) throws ClockNotFoundException {
        return databasedClocks.stream().filter(clock -> clock.mark().equalsIgnoreCase(mark) && clock.price() == price).findFirst().orElseThrow(
                ClockNotFoundException::new
        );
    }

    @Override
    public Clock findById(long id) throws ClockNotFoundException {
        return databasedClocks.stream().filter(clock -> clock.id() == id).findFirst().orElseThrow(
                ClockNotFoundException::new
        );
    }

    @Override
    public void removeById(long id) {

        List<DatabasedClock> removal = new ArrayList<>();

        for (DatabasedClock databasedClock : databasedClocks) {

            if (databasedClock.id() != id) {
                continue;
            }

            try (Session session = sessionFactory.openSession()) {
                Transaction transaction = session.beginTransaction();
                session.delete(databasedClock.wrappedClock());
                transaction.commit();
            }

            wrappedClockStore.removeById(id);

            removal.add(databasedClock);
        }

        removal.forEach(databasedClocks::remove);


    }

    @Override
    public void removeByMarkAndPrice(@NonNull String mark, int price) {

        List<DatabasedClock> removal = new ArrayList<>();

        for (DatabasedClock databasedClock : databasedClocks) {

            if (!databasedClock.mark().equalsIgnoreCase(mark) || databasedClock.price() != price) {
                continue;
            }

            try (Session session = sessionFactory.openSession()) {
                Transaction transaction = session.beginTransaction();
                session.delete(databasedClock.wrappedClock());
                transaction.commit();
            }

            wrappedClockStore.removeByMarkAndPrice(mark, price);

            removal.add(databasedClock);
        }

        removal.forEach(databasedClocks::remove);
    }

    @Override
    public Clock mostExpensive() throws EmptyStoreException {
        return databasedClocks.stream().max(Comparator.comparingInt(Clock::price)).orElseThrow(
                EmptyStoreException::new
        );
    }

    @Override
    public void updateAll(int hours, int minutes, int seconds) throws InvalidTimeException {
        databasedClocks.forEach(clock -> {
            if (clock instanceof ExtensionClock) {
                ((ExtensionClock) clock).change(hours, minutes, seconds);
                return;
            }
            clock.change(hours, minutes);
        });
    }

    @Override
    public void updateAll(int hours, int minutes) throws InvalidTimeException {
        databasedClocks.forEach(clock -> clock.change(hours, minutes));
    }

    @Override
    public String popularMark() throws EmptyStoreException {
        Map<String, Integer> frequencyOfUse = new HashMap<>();

        databasedClocks.forEach(clock -> {
            String mark = clock.mark();
            frequencyOfUse.putIfAbsent(mark, 0);
            frequencyOfUse.put(mark, frequencyOfUse.get(mark) + 1);
        });

        int maxFrequency = frequencyOfUse.values().stream().max(Integer::compareTo).orElseThrow(EmptyStoreException::new);

        for (var entry : frequencyOfUse.entrySet()) {
            if (entry.getValue() == maxFrequency) {
                return entry.getKey();
            }
        }

        throw new EmptyStoreException();
    }

    @Override
    public TreeSet<String> marksLexicographyOrder() {
        TreeSet<String> set = new TreeSet<>(String::compareTo);

        for (Clock clock : databasedClocks) {
            set.add(clock.mark());
        }

        return set;
    }

    @Override
    public boolean empty() {
        return databasedClocks.isEmpty();
    }

    @Override
    public int count() {
        return databasedClocks.size();
    }

    @Override
    public Iterator<Clock> iterator() {
        return databasedClocks.stream().map(c -> (Clock)c).iterator();
    }

    private void saveExistsClocksToDatabase() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            for (DatabasedClock databasedClock : databasedClocks) {
                session.saveOrUpdate(databasedClock.wrappedClock());
            }

            transaction.commit();
        }
    }

    private void loadClocksFormDatabase() {
        try (Session session = sessionFactory.openSession()) {

            for (StandardClock clock : listAllEntities(session, StandardClock.class)) {
                if (clock instanceof ExtensionClock) {
                    databasedClocks.add(new DatabasedAccurateClock((ExtensionClock) clock, sessionFactory));
                    continue;
                }
                databasedClocks.add(new DatabasedStandardClock(clock, sessionFactory));
            }

            wrappedClockStore.getClocks().addAll(databasedClocks.stream().map(DatabasedClock::wrappedClock).collect(Collectors.toSet()));
        }
    }

    private <T> List<T> listAllEntities(Session session, Class<T> clazz) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
        criteriaQuery.from(clazz);
        return session.createQuery(criteriaQuery).list();
    }

    private void registerSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(StandardClock.class);
        configuration.addAnnotatedClass(AccurateClock.class);
        configuration.configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
        sessionFactory = configuration.buildSessionFactory(builder.build());
    }
}
