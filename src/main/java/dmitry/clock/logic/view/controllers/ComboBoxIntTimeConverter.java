package dmitry.clock.logic.view.controllers;

import javafx.util.StringConverter;
import lombok.Getter;

class ComboBoxIntTimeConverter extends StringConverter<Integer> {

    @Getter
    private static final ComboBoxIntTimeConverter instance = new ComboBoxIntTimeConverter();

    private ComboBoxIntTimeConverter() {}

    @Override
    public String toString(Integer i) {
        if (i < 10) {
            return "0" + i;
        }
        return String.valueOf(i);
    }

    @Override
    public Integer fromString(String string) {
        return Integer.valueOf(string);
    }
}