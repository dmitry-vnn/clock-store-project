package dmitry.clock.logic.view.controllers;

import dmitry.clock.logic.clocks.*;
import dmitry.clock.logic.clocks.serialize.ClockStoreBinaryDeserializer;
import dmitry.clock.logic.clocks.serialize.ClockStoreBinarySerializer;
import dmitry.clock.logic.clocks.serialize.ClockStoreJsonDeserializer;
import dmitry.clock.logic.clocks.serialize.ClockStoreJsonSerializer;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class StoreLoadStrategyController {
    
    @FXML private ToggleGroup loadRadioButtonsToggleGroup;
    
    @FXML private RadioButton fromDbRadioButton;
    @FXML private RadioButton fromJsonRadioButton;
    @FXML private RadioButton fromBinaryRadioButton;
    @FXML private RadioButton customRadioButton;
    
    @FXML private CheckBox toDbCheckBox;
    @FXML private CheckBox toJsonCheckBox;
    @FXML private CheckBox toBinaryCheckBox;
    
    @FXML private Button okButton;
    @FXML private Button cancelButton;

    private final Stage loadStoreStrategyStage;

    public StoreLoadStrategyController(Stage stage) {
        this.loadStoreStrategyStage = stage;
    }

    @FXML
    private void initialize() {

        listenOkButton();
        listenCloseButton();

        listenRadioButtons();

    }

    private void listenRadioButtons() {
        fromDbRadioButton.selectedProperty().addListener((observable, oldValue, selected) -> {
            toDbCheckBox.setDisable(selected);
            toDbCheckBox.setSelected(selected);
        });

        loadRadioButtonsToggleGroup.selectToggle(customRadioButton);
    }

    private void listenCloseButton() {
        cancelButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            loadStoreStrategyStage.close();
        });
    }


    private void listenOkButton() {

        okButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> openStore());

    }

    @SneakyThrows
    private void openStore() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml-elements/store.fxml"));

        Stage storeStage = new Stage();

        Store store = loadStoreByStrategy();

        fxmlLoader.setController(new StoreController(store, storeStage));

        storeStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @SneakyThrows
            @Override
            public void handle(WindowEvent event) {
                saveStoreByCloseStrategy(store);

                storeStage.close();
            }
        });

        storeStage.setResizable(false);
        storeStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icon.png"))));
        storeStage.setTitle("Магазин часов");
        storeStage.setScene(new Scene(fxmlLoader.load()));

        loadStoreStrategyStage.hide();
        storeStage.show();
    }

    private void saveStoreByCloseStrategy(Store store) throws IOException {

        boolean isDatabased = store instanceof DatabasedClockStore;

        if (toJsonCheckBox.isSelected()) {
            new ClockStoreJsonSerializer(
                    isDatabased ? ((DatabasedClockStore) store).getWrappedClockStore() : (ClockStore) store,
                    new File("store.json")
            ).save();
        }

        if (toBinaryCheckBox.isSelected()) {
            new ClockStoreBinarySerializer(
                    isDatabased ? ((DatabasedClockStore) store).getWrappedClockStore() : (ClockStore) store,
                    new File("store.data")
            ).save();
        }

        if (toDbCheckBox.isSelected()) {

            if (isDatabased) {
                return;
            }

            new DatabasedClockStore((ClockStore) store, DatabasedClockStore.Strategy.NOT_LOAD_PREVIOUS_DATA);

        }

    }

    private Store loadStoreByStrategy() throws IOException, ClassNotFoundException {

        if (fromDbRadioButton.isSelected()) {
            return new DatabasedClockStore(new ClockStore(), DatabasedClockStore.Strategy.LOAD_PREVIOUS_DATA);
        }

        if (fromJsonRadioButton.isSelected()) {
            return new ClockStoreJsonDeserializer(new File("store.json")).load();
        }

        if (fromBinaryRadioButton.isSelected()) {
            return new ClockStoreBinaryDeserializer(new File("store.data")).load();
        }

        return createCustomStore();

    }

    private Store createCustomStore() {
        ClockStore store = new ClockStore();

        store.add(new StandardClock(12, 05, "Rolex", 5000));
        store.add(new AccurateClock(19, 39, 15, "Garmin", 2567));
        store.add(new StandardClock(9, 21, "Omega", 3985));
        store.add(new StandardClock(10, 50, "Versace", 8714));
        store.add(new AccurateClock(10, 50, 14, "Rolex", 7425));


        return store;
    }
}
