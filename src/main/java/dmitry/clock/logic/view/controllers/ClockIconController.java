package dmitry.clock.logic.view.controllers;

import dmitry.clock.logic.clocks.Clock;
import dmitry.clock.logic.clocks.ExtensionClock;
import dmitry.clock.logic.view.ClockIconImageFactory;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Window;
import lombok.SneakyThrows;

import java.util.Optional;

public class ClockIconController {

    @FXML private Pane rootPane;
    
    @FXML private ImageView iconImageView;
    @FXML private Label markLabel;
    @FXML private Label priceLabel;
    @FXML private Label fakePriceLabel;

    private final Clock clock;

    private final Runnable removeFunction;
    private final Window owner;

    ClockIconController(Clock clock, Runnable removeFunction, Window owner) {
        this.clock = clock;
        this.removeFunction = removeFunction;
        this.owner = owner;
    }

    @FXML
    private void initialize() {
        fillIconImageView();
        fillMarkLabel();
        fillPriceLabels();

        listenRootPane();
    }

    private void listenRootPane() {
        rootPane.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @SneakyThrows
            @Override
            public void handle(MouseEvent event) {

                AbstractDialog<Boolean> dialog;

                if (clock instanceof ExtensionClock) {
                    dialog = new AccurateClockDialog((ExtensionClock) clock, owner);
                } else {
                    dialog = new StandardClockDialog(clock, owner);
                }

                Optional<Boolean> optionalHasRemove = dialog.open();
                optionalHasRemove.ifPresent(hasRemove -> {
                    if (hasRemove) {
                        removeFunction.run();
                    }
                });
            }
        }) ;
    }

    private void fillPriceLabels() {
        int price = clock.price();

        priceLabel.setText(price + " руб.");
        fakePriceLabel.setText(((int) (price * 1.5)) + " руб.");
    }

    private void fillMarkLabel() {
        markLabel.setText("Часы " + clock.mark());
    }

    private void fillIconImageView() {
        Image icon = ClockIconImageFactory.getInstance().icon(clock);
        iconImageView.setImage(icon);
    }
}
