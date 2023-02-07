package dmitry.clock.logic.clocks.serialize;

import dmitry.clock.logic.clocks.ClockStore;

import java.io.IOException;

public interface StoreSerializer {

    void save() throws IOException, ClassNotFoundException;
}
