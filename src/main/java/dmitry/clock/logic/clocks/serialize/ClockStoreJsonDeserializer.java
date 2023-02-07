package dmitry.clock.logic.clocks.serialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import dmitry.clock.logic.clocks.ClockStore;
import lombok.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public final class ClockStoreJsonDeserializer implements StoreDeserializer {

    private static final Gson gson = new GsonBuilder()
            .setLenient()
            .setPrettyPrinting()
            .registerTypeAdapter(
                    ClockStore.class, ClockStore.ClockStoreJsonSerializable.getInstance()
            )
            .create();


    private final File file;

    public ClockStoreJsonDeserializer(@NonNull File file) {
        this.file = file;
    }

    public ClockStore load() throws IOException {
        try (FileReader fileReader = new FileReader(file)) {
            try (JsonReader jsonReader = new JsonReader(fileReader)) {
                return gson.fromJson(jsonReader, ClockStore.class);
            }
        }
    }
}
