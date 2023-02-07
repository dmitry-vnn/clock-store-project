package dmitry.clock.logic.view;

import dmitry.clock.logic.view.controllers.StoreLoadStrategyController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class ViewApplication extends Application {

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml-elements/store-load-strategy.fxml"));

        fxmlLoader.setController(new StoreLoadStrategyController(stage));

        stage.setResizable(false);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icon.png"))));
        stage.setTitle("Магазин часов");
        stage.setScene(new Scene(fxmlLoader.load()));

        stage.show();
    }


}
