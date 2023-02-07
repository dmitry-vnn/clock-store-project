package dmitry.clock.logic.view.controllers;

import dmitry.clock.logic.clocks.Clock;
import dmitry.clock.logic.clocks.ExtensionClock;
import dmitry.clock.logic.clocks.Store;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StoreController {

    @FXML private CheckBox setSystemTimeCheckBox;

    @FXML private ComboBox<Integer> hourComboBox;
    @FXML private ComboBox<Integer> minuteComboBox;
    @FXML private ComboBox<Integer> secondComboBox;

    @FXML private Button syncClocksButton;

    @FXML private Button addClockButton;

    @FXML private ComboBox<FilterType> filtersComboBox;
    @FXML private ComboBox<OrderType> orderComboBox;

    @FXML private TextField searchTextField;
    @FXML private Label searchFindCountLabel;
    @FXML private FlowPane clocksFlowPane;

    private final Stage stage;


    private final Store store;
    private final Map<Clock, Pane> designedClocks;

    public StoreController(Store store, Stage stage) {
        this.store = store;
        this.designedClocks = new HashMap<>(store.count());
        this.stage = stage;
    }

    @FXML
    private void initialize() throws IOException {

        fillAndListenFiltersComboBox();
        fillAndListenOrderComboBox();
        fillTimeComboBoxes();

        listenSearchTextField();
        listenSetSystemTimeCheckBox();
        listenSyncClockButton();

        listenAddClockButton();

        fillClocksFlowPane();

        fillSearchFindCountLabel();
    }

    private void listenAddClockButton() {
        addClockButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @SneakyThrows
            @Override
            public void handle(MouseEvent event) {
                AddClockDialog addClockDialog = new AddClockDialog(stage);
                Optional<Clock> optionalClock = addClockDialog.open();

                if (!optionalClock.isPresent()) {
                    return;
                }

                Clock clock = optionalClock.get();

                store.add(clock);

                addClockToFlowPane(store.findById(clock.id()), true);

                sort();
            }
        });
    }

    private void listenSyncClockButton() {
        syncClocksButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {

            designedClocks.forEach((clock, designedClock) -> {
                if (clocksFlowPane.getChildren().contains(designedClock)) {
                    if (setSystemTimeCheckBox.isSelected()) {
                        clock.sync();
                    } else {

                        int hour = hourComboBox.getValue();
                        int minute = minuteComboBox.getValue();

                        if (clock instanceof ExtensionClock) {
                            int second = secondComboBox.getValue();
                            ((ExtensionClock) clock).change(hour, minute, second);
                        } else {
                            clock.change(hour, minute);
                        }
                    }

                    clock.reset();
                }
            });
        });
    }

    private void fillSearchFindCountLabel() {
        int size = clocksFlowPane.getChildren().size();
        searchFindCountLabel.setText(size + " найдено");
    }

    private void listenSearchTextField() {
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            sort(newValue);
        });
    }

    private void listenSetSystemTimeCheckBox() {
        setSystemTimeCheckBox.selectedProperty().addListener((observable, notSelected, selected) -> {
            hourComboBox.setDisable(selected);
            minuteComboBox.setDisable(selected);
            secondComboBox.setDisable(selected);
        });
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

    private void fillAndListenOrderComboBox() {

        orderComboBox.setConverter(new OrderType.Converter());
        orderComboBox.getItems().setAll(OrderType.values());

        orderComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            sort(newValue);
        });
    }


    @AllArgsConstructor
    private enum OrderType {

        DESC("Убывание"),
        ASC("Возрастание");

        final String label;

        private static class Converter extends StringConverter<OrderType> {

            @Override
            public String toString(OrderType type) {
                return type.label;
            }

            @Override
            public OrderType fromString(String str) {
                return Arrays.stream(OrderType.values()).filter(t -> t.label.equals(str)).findFirst().orElse(OrderType.DESC);
            }
        }
    }



    private void fillAndListenFiltersComboBox() {

        filtersComboBox.setConverter(new FilterType.Converter());
        filtersComboBox.getItems().setAll(FilterType.values());

        filtersComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            sort(newValue);
        });

    }

    private void sort() {
        sort(
                orderComboBox.getValue() == null ? OrderType.DESC : orderComboBox.getValue(),
                filtersComboBox.getValue() == null ? FilterType.PRICE : filtersComboBox.getValue(),
                searchTextField.getText());
    }

    private void sort(OrderType orderType) {
        sort(
                orderType,
                filtersComboBox.getValue() == null ? FilterType.PRICE : filtersComboBox.getValue(),
                searchTextField.getText());
    }

    private void sort(FilterType filterType) {
        sort(
                orderComboBox.getValue() == null ? OrderType.DESC : orderComboBox.getValue(),
                filterType,
                searchTextField.getText());
    }

    private void sort(String query) {
        sort(
                orderComboBox.getValue() == null ? OrderType.DESC : orderComboBox.getValue(),
                filtersComboBox.getValue() == null ? FilterType.PRICE : filtersComboBox.getValue(),
                query);
    }


    private void sort(OrderType orderType, FilterType filterType, String searchQuery) {

        if (store.empty()) {
            return;
        }

        List<Pane> sortedDesignedClocks;

        switch (filterType) {
            case POP:

                String popularMark = store.popularMark();
                sortedDesignedClocks = designedClocks.entrySet().stream().filter((e1 -> e1.getKey().mark().equals(popularMark)))
                        .map(Map.Entry::getValue).collect(Collectors.toList());

                break;
            case RICH:
                sortedDesignedClocks = Collections.singletonList(designedClocks.get(store.mostExpensive()));
                break;

            default: {
                Stream<Map.Entry<Clock, Pane>> stream;

                if (!searchQuery.isEmpty()) {
                    Set<Clock> allByMark = store.findAllByMark(searchQuery);
                    Optional<Integer> optPrice = tryParse(searchQuery);
                    Set<Clock> allByPrice = optPrice.isPresent() ? store.findAllByPrice(optPrice.get()) : Collections.emptySet();
                    stream = designedClocks.entrySet().stream().filter(e -> allByMark.contains(e.getKey()) || allByPrice.contains(e.getKey()));
                } else {
                    stream = designedClocks.entrySet().stream();
                }

                if (filterType == FilterType.PRICE) {
                    sortedDesignedClocks = stream.sorted((e1, e2) -> {
                        Clock clock1 = e1.getKey();
                        Clock clock2 = e2.getKey();

                        if (orderType == OrderType.DESC) {
                            return Integer.compare(clock2.price(), clock1.price());
                        } else {
                            return Integer.compare(clock1.price(), clock2.price());
                        }
                    }).map(Map.Entry::getValue).collect(Collectors.toList());
                } else {
                    sortedDesignedClocks = stream.sorted((e1, e2) -> {
                        Clock clock1 = e1.getKey();
                        Clock clock2 = e2.getKey();

                        if (orderType == OrderType.DESC) {
                            return clock1.mark().compareTo(clock2.mark());
                        } else {
                            return clock2.mark().compareTo(clock1.mark());
                        }
                    }).map(Map.Entry::getValue).collect(Collectors.toList());
                }

                break;
            }
        }

        clocksFlowPane.getChildren().clear();
        clocksFlowPane.getChildren().addAll(sortedDesignedClocks);
        fillSearchFindCountLabel();

    }

    private Optional<Integer> tryParse(String value) {
        try {
            return Optional.of(Integer.parseInt(value));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    @AllArgsConstructor
    private enum FilterType {

        PRICE("Цена"),
        MARK("Марка"),
        POP("Популярность"),
        RICH("Дороговизна");

        final String label;

        private static class Converter extends StringConverter<FilterType> {

            @Override
            public String toString(FilterType type) {
                return type.label;
            }

            @Override
            public FilterType fromString(String str) {
                return Arrays.stream(FilterType.values()).filter(t -> t.label.equals(str)).findFirst().orElse(FilterType.PRICE);
            }
        }
    }


    private void fillClocksFlowPane() throws IOException {

        for (Clock clock : store) {

            addClockToFlowPane(clock, false);

        }

    }

    private void addClockToFlowPane(Clock clock, boolean lazy) throws IOException {
        Pane designedClock = loadDesignedClock(clock);
        designedClocks.put(clock, designedClock);

        if (!lazy) {
            clocksFlowPane.getChildren().add(designedClock);
        }
    }

    private Pane loadDesignedClock(Clock clock) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(
                "/fxml-elements/clock-from-store-element.fxml"
        )));

        fxmlLoader.setController(new ClockIconController(clock, () -> {

            store.removeByMarkAndPrice(clock.mark(), clock.price());
            designedClocks.remove(clock);
            sort();

        }, stage));
        return fxmlLoader.load();
    }

}
