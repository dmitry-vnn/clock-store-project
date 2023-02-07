package dmitry.clock.logic.view.controllers;

import dmitry.clock.logic.clocks.AccurateClock;
import dmitry.clock.logic.clocks.Clock;
import dmitry.clock.logic.clocks.StandardClock;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import javafx.util.StringConverter;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Stream;

public class AddClockDialog extends AbstractDialog<Clock> {

    @FXML private ComboBox<ClockType> clockTypeComboBox;

    @FXML private TextField clockMarkTextFiled;
    @FXML private TextField clockPriceTextField;

    @FXML private CheckBox systemSyncCheckBox;

    @FXML private ComboBox<Integer> hourComboBox;
    @FXML private ComboBox<Integer> minuteComboBox;
    @FXML private ComboBox<Integer> secondComboBox;

    @FXML private Label preSecondLabel;

    @FXML private Button okButton;
    @FXML private Button cancelButton;

    public AddClockDialog(Window owner) throws IOException {
        super(owner, "add-clock-dialog", "Добавить часы");
    }

    @Override
    protected void initialize() {

        fillTimeComboBoxes();

        fillAndListenClockTypeComboBox();

        fillAndListenClockMarkTextFiled();
        fillAndListenClockPriceTextFieldValidator();

        listenSystemSyncCheckBox();


        fillAndListenOkButton();
        listenCloseButton();

    }

    private void fillTimeComboBoxes() {
        LocalDateTime localDateTime = LocalDateTime.now();

        hourComboBox.setConverter(ComboBoxIntTimeConverter.getInstance());
        minuteComboBox.setConverter(ComboBoxIntTimeConverter.getInstance());
        secondComboBox.setConverter(ComboBoxIntTimeConverter.getInstance());

        hourComboBox.setValue(localDateTime.getHour());
        minuteComboBox.setValue(localDateTime.getMinute());
        secondComboBox.setValue(localDateTime.getSecond());

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
            if (selected) {
                secondComboBox.setDisable(true);
            } else if (clockTypeComboBox.getValue() == ClockType.ACCURATE) {
                secondComboBox.setDisable(false);
            }
        });
    }

    private void fillAndListenClockMarkTextFiled() {
        clockMarkTextFiled.setText("");

        clockMarkTextFiled.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.isEmpty() || clockPriceTextField.getText().isEmpty());
        });
    }

    private void fillAndListenClockPriceTextFieldValidator() {
        clockPriceTextField.setText("");

        clockPriceTextField.textProperty().addListener((observable, oldValue, newValue) -> {

            if (!newValue.matches("\\d*")) {
                clockPriceTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }

            okButton.setDisable(clockPriceTextField.getText().isEmpty() || clockMarkTextFiled.getText().isEmpty());

        });
    }

    private void fillAndListenClockTypeComboBox() {

        clockTypeComboBox.setConverter(ClockType.Converter.instance);
        clockTypeComboBox.getItems().addAll(ClockType.values());

        clockTypeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            preSecondLabel.setDisable(newValue == ClockType.STANDARD);
            if (!systemSyncCheckBox.isSelected()) {
                secondComboBox.setDisable(newValue == ClockType.STANDARD);
            }
        });

        clockTypeComboBox.getSelectionModel().select(0);
    }

    @AllArgsConstructor
    private enum ClockType {

        STANDARD("Без секундной стрелки"),
        ACCURATE("С секундной стрелкой");

        private final String description;

        private static class Converter extends StringConverter<ClockType> {

            private static final Converter instance = new Converter();

            @Override
            public String toString(ClockType type) {
                return type == ACCURATE ? ACCURATE.description : STANDARD.description;
            }

            @Override
            public ClockType fromString(String description) {
                return Stream.of(ClockType.values())
                        .filter(type -> type.description.equals(description))
                        .findFirst()
                        .orElse(STANDARD);
            }
        }

    }

    private void fillAndListenOkButton() {

        okButton.setDisable(true);

        okButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            result(clockTypeComboBox.getValue() == ClockType.STANDARD ?
                    new StandardClock(
                            hourComboBox.getValue(),
                            minuteComboBox.getValue(),
                            clockMarkTextFiled.getText(),
                            Integer.parseInt(clockPriceTextField.getText())
                    )
                            :
                    new AccurateClock(
                            hourComboBox.getValue(),
                            minuteComboBox.getValue(),
                            secondComboBox.getValue(),
                            clockMarkTextFiled.getText(),
                            Integer.parseInt(clockPriceTextField.getText())
                    )
            );
            close();
        });
    }

    private void listenCloseButton() {
        cancelButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            result(null);
            close();
        });
    }
}
