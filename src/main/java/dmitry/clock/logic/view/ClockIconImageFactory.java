package dmitry.clock.logic.view;

import dmitry.clock.logic.clocks.Clock;
import dmitry.clock.logic.clocks.ExtensionClock;
import javafx.scene.image.Image;
import lombok.AccessLevel;
import lombok.Getter;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ClockIconImageFactory {

    private static final ClockIconImageFactory instance = new ClockIconImageFactory();

    public static ClockIconImageFactory getInstance() {
        return instance;
    }

    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Image standardIcon = loadStandardIcon();

    private Image loadStandardIcon() {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/clock-icons/default/standard.png")));
    }

    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Image accurateIcon = loadAccurateIcon();

    private Image loadAccurateIcon() {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/clock-icons/default/accurate.png")));
    }

    private final Map<String, Image> icons;

    private ClockIconImageFactory() {
        icons = new HashMap<>();
    }

    public Image icon(Clock clock) {
        String imageName = clock.mark().trim().toLowerCase().replace(" ", "-");

        boolean isAccurate = clock instanceof ExtensionClock;

        String imagePath =
                "/images/clock-icons/" +
                (isAccurate ? "accurate" : "standard") +
                "/" + imageName + ".png";

        if (icons.containsKey(imagePath)) {
            return icons.get(imagePath);
        }


        InputStream resource = getClass().getResourceAsStream(imagePath);

        Image icon;

        if (resource == null) {

            if (isAccurate) {
                icon = getAccurateIcon();
            } else {
                icon = getStandardIcon();
            }

        } else {
            icon = new Image(resource);
        }

        icons.put(imagePath, icon);
        return icon;
    }
}
