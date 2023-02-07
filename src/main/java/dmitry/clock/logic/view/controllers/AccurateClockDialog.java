package dmitry.clock.logic.view.controllers;


import dmitry.clock.logic.clocks.ExtensionClock;
import dmitry.clock.logic.view.ClockIconImageFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import lombok.NonNull;

import java.io.IOException;

public class AccurateClockDialog extends AbstractDialog<Boolean> {

    @FXML private ImageView iconImageView;

    @FXML private Label markLabel;
    @FXML private Label priceLabel;

    @FXML private CheckBox systemSyncCheckBox;
    @FXML private CheckBox resetCheckBox;

    @FXML private ComboBox<Integer> hourComboBox;
    @FXML private ComboBox<Integer> minuteComboBox;
    @FXML private ComboBox<Integer> secondComboBox;

    @FXML private Button okButton;
    @FXML private Button removeButton;
    @FXML private Button cancelButton;

    private final ExtensionClock clock;

    public AccurateClockDialog(@NonNull ExtensionClock clock, Window owner) throws IOException {
        super(owner, "accurate-clock-dialog", "Точные часы " + clock.mark());

        this.clock = clock;
        setup();
    }

    private void setup() {
        fillIconImageView();
        fillMarkAndPriceLabels();

        listenSystemSyncCheckBox();
        fillTimeComboBoxes();

        listenOkButton();
        listenCancelButton();
        listenRemoveButton();
    }

    @Override
    protected void initialize() {

    }

    private void listenRemoveButton() {
        removeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            result(true);
            close();
        });
    }

    private void listenCancelButton() {
        cancelButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            result(false);
            close();
        });
    }

    private void listenOkButton() {
        okButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

            if (resetCheckBox.isSelected()) {
                clock.reset();
            }

            if (systemSyncCheckBox.isSelected()) {
                clock.sync();
            } else {
                clock.change(hourComboBox.getValue(), minuteComboBox.getValue(), secondComboBox.getValue());
            }

            result(false);
            close();
        });
    }

    private void fillTimeComboBoxes() {

        hourComboBox.setConverter(ComboBoxIntTimeConverter.getInstance());
        minuteComboBox.setConverter(ComboBoxIntTimeConverter.getInstance());
        secondComboBox.setConverter(ComboBoxIntTimeConverter.getInstance());

        hourComboBox.setValue(clock.hours());
        minuteComboBox.setValue(clock.minutes());
        secondComboBox.setValue(clock.seconds());

        for (int i = 0; i < 24; i++) {
            hourComboBox.getItems().add(i);
        }

        for (int i = 0; i < 60; i++) {
            minuteComboBox.getItems().add(i);
            secondComboBox.getItems().add(i);
        }
    }

    private void listenSystemSyncCheckBox() {
        systemSyncCheckBox.selectedProperty().addListener((observable, notSelected, selected) -> {
            hourComboBox.setDisable(selected);
            minuteComboBox.setDisable(selected);
            secondComboBox.setDisable(selected);
        });
    }

    private void fillMarkAndPriceLabels() {
        markLabel.setText(clock.mark());
        priceLabel.setText(clock.price() + "₽");
    }

    private void fillIconImageView() {
        Image icon = ClockIconImageFactory.getInstance().icon(clock);
        iconImageView.setImage(icon);
    }
}
