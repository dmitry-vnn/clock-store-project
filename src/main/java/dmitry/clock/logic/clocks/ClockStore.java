package dmitry.clock.logic.clocks;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import dmitry.clock.logic.exceptions.ClockNotFoundException;
import dmitry.clock.logic.exceptions.EmptyStoreException;
import dmitry.clock.logic.exceptions.InvalidTimeException;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.var;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class ClockStore implements Store {

    private final Set<Clock> clocks;

    public ClockStore() {
        clocks = new HashSet<>();
    }

    ClockStore(Set<Clock> initialCapacity) {
        clocks = initialCapacity;
    }

    @Override
    public void add(Clock clock) {
        clocks.add(clock);
    }

    @Override
    public Set<Clock> findAllByMark(@NonNull String mark) {
        return clocks.stream().filter(clock -> {
            String clockMark = clock.mark().toLowerCase();
            String comparableMark = mark.toLowerCase();

            return clockMark.startsWith(comparableMark) || clockMark.contains(comparableMark);

        }).collect(Collectors.toSet());
    }

    @Override
    public Set<Clock> findAllByPrice(int price) {
        return clocks.stream().filter(clock -> clock.price() == price).collect(Collectors.toSet());
    }

    @Override
    public Clock findByMarkAndPrice(@NonNull String mark, int price) throws ClockNotFoundException {
        return clocks.stream().filter(clock -> clock.mark().equalsIgnoreCase(mark) && clock.price() == price).findFirst().orElseThrow(
                ClockNotFoundException::new
        );
    }

    @Override
    public Clock findById(long id) throws ClockNotFoundException {
        return clocks.stream().filter(clock -> clock.id() == id).findFirst().orElseThrow(
                ClockNotFoundException::new
        );
    }

    @Override
    public void removeById(long id) {
        clocks.removeAll(
                clocks.stream().filter(clock -> clock.id() == id).collect(Collectors.toSet())
        );
    }

    @Override
    public void removeByMarkAndPrice(@NonNull String mark, int price) {
        clocks.removeAll(
                clocks.stream().filter(clock -> clock.mark().equalsIgnoreCase(mark) && clock.price() == price).collect(Collectors.toSet())
        );
    }

    @Override
    public Clock mostExpensive() throws EmptyStoreException {
        return clocks.stream().max(Comparator.comparingInt(Clock::price)).orElseThrow(
                EmptyStoreException::new
        );
    }

    @Override
    public void updateAll(int hours, int minutes, int seconds) throws InvalidTimeException {
        clocks.forEach(clock -> {
            if (clock instanceof ExtensionClock) {
                ((ExtensionClock) clock).change(hours, minutes, seconds);
                return;
            }
            clock.change(hours, minutes);
        });
    }

    @Override
    public void updateAll(int hours, int minutes) throws InvalidTimeException {
        clocks.forEach(clock -> {
            clock.change(hours, minutes);
        });
    }

    @Override
    public String popularMark() throws EmptyStoreException {
        Map<String, Integer> frequencyOfUse = new HashMap<>();

        clocks.forEach(clock -> {
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

        for (Clock clock : clocks) {
            set.add(clock.mark());
        }

        return set;
    }

    @Override
    public boolean empty() {
        return clocks.isEmpty();
    }

    @Override
    public int count() {
        return clocks.size();
    }

    @Override
    public Iterator<Clock> iterator() {
        return clocks.iterator();
    }

    Set<Clock> getClocks() {
        return clocks;
    }

    @Override
    public String toString() {
        return "ClockStore{" +
                "clocks=" + clocks +
                '}';
    }

    public static final class ClockStoreJsonSerializable implements JsonSerializer<ClockStore>, JsonDeserializer<ClockStore> {

        private static final ClockStoreJsonSerializable instance = new ClockStoreJsonSerializable();

        private static final Gson gson = new GsonBuilder().create();

        public static ClockStoreJsonSerializable getInstance() {
            return instance;
        }

        private ClockStoreJsonSerializable() {
        }

        @Override
        public JsonElement serialize(ClockStore store, Type type, JsonSerializationContext jsonSerializationContext) {

            JsonObject jsonObject = new JsonObject();

            JsonArray jsonArray = new JsonArray();

            for (Clock clock : store) {
                JsonObject clockAsJson = gson.toJsonTree(clock).getAsJsonObject();

                clockAsJson.addProperty("className", clock.getClass().getName());

                jsonArray.add(clockAsJson);
            }

            jsonObject.add("clocks", jsonArray);

            return jsonObject;
        }

        @SneakyThrows
        @Override
        public ClockStore deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

            JsonObject jsonObject = jsonElement.getAsJsonObject();

            JsonArray jsonArray = jsonObject.getAsJsonArray("clocks");

            Set<Clock> clocks = new HashSet<>(jsonArray.size());

            for (JsonElement element : jsonArray) {

                JsonObject clockAsJson = element.getAsJsonObject();

                String className = clockAsJson.getAsJsonPrimitive("className").getAsString();

                Class<?> clockClass = Class.forName(className);

                clockAsJson.remove("className");

                clocks.add((Clock) gson.fromJson(clockAsJson, clockClass));

            }

            return new ClockStore(clocks);
        }
    }

}
