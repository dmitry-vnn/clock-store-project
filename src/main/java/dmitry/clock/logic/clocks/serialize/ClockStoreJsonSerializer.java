package dmitry.clock.logic.clocks.serialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import dmitry.clock.logic.clocks.ClockStore;
import dmitry.clock.logic.clocks.Store;
import lombok.NonNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class ClockStoreJsonSerializer implements StoreSerializer{

    private static final Gson gson = new GsonBuilder()
            .setLenient()
            .setPrettyPrinting()
            .registerTypeAdapter(
                    ClockStore.class, ClockStore.ClockStoreJsonSerializable.getInstance()
            )
            .create();


    private final ClockStore store;
    private final File file;

    public ClockStoreJsonSerializer(@NonNull ClockStore store, @NonNull File file) throws IOException {
        this.store = store;
        this.file = file;
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    public void save() throws IOException {
        try (FileWriter fileWriter = new FileWriter(file)) {
            try (JsonWriter jsonWriter = new JsonWriter(fileWriter)) {
                jsonWriter.setIndent("    ");
                gson.toJson(store, ClockStore.class, jsonWriter);
            }
        }
    }
}
