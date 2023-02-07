package dmitry.clock.logic.clocks.serialize;

import dmitry.clock.logic.clocks.ClockStore;
import lombok.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public final class ClockStoreBinaryDeserializer implements StoreDeserializer {

    private final File file;

    public ClockStoreBinaryDeserializer(@NonNull File file) {
        this.file = file;
    }

    @Override
    public ClockStore load() throws IOException, ClassNotFoundException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                return (ClockStore) objectInputStream.readObject();
            }
        }
    }
}
