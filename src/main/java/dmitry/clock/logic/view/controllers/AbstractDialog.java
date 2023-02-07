package dmitry.clock.logic.view.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractDialog<R> extends Dialog<Optional<R>> {

    @FXML
    protected abstract void initialize();

    public AbstractDialog(Window owner, String fxmlName, String title) throws IOException {

        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        setTitle(title);

        getDialogPane().getScene().getWindow().setOnCloseRequest(event -> {});

        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(
                "/fxml-elements/dialogs/" + fxmlName + ".fxml"
        )));
        fxmlLoader.setController(this);
        setDialogPane(fxmlLoader.load());
    }

    public Optional<R> open() {
        return showAndWait().orElseGet(Optional::empty);
    }

    public void result(R result) {
        setResult(Optional.ofNullable(result));
    }
}
