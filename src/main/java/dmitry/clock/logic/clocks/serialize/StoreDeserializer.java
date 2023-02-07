package dmitry.clock.logic.clocks.serialize;

import dmitry.clock.logic.clocks.ClockStore;

import java.io.IOException;

public interface StoreDeserializer {

    ClockStore load() throws IOException, ClassNotFoundException;
}
