package dmitry.clock.logic.clocks.serialize;

import dmitry.clock.logic.clocks.ClockStore;
import lombok.NonNull;

import java.io.*;

public class ClockStoreBinarySerializer implements StoreSerializer {

    private final ClockStore store;
    private final File file;

    public ClockStoreBinarySerializer(@NonNull ClockStore store, @NonNull File file) throws IOException {
        this.store = store;
        this.file = file;
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    public void save() throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
                objectOutputStream.writeObject(store);
            }
        }
    }
}
