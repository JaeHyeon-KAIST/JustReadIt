package jri.justreadit.pageController;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import jri.justreadit.JRIApp;
import jri.justreadit.scenario.FirstScenario;
import x.XPageController;

import java.awt.event.MouseEvent;

public class NotePageController extends XPageController {
    public static final String PAGE_CONTROLLER_NAME = "NotePageController";
    public static final String FXML_NAME = "NotePage";

    @FXML
    private Button addBtn;

    @FXML
    private AnchorPane SIDE_NOTE;

    public NotePageController(JRIApp app, String fxmlBasePath) {
        super(PAGE_CONTROLLER_NAME, fxmlBasePath, FXML_NAME, app);
    }

    @FXML
    public void initialize() {
        SIDE_NOTE.setTranslateX(2000);

        addBtn.setOnAction(e -> {
            System.out.println("Add button clicked");
        });
    }

    @FXML
    private void hideSlide(MouseEvent event) {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(0.4));
        slide.setNode(SIDE_NOTE);
        slide.setToX(-140);
        slide.play();
        SIDE_NOTE.setTranslateX(0);
    }

    public void showSlide(javafx.scene.input.MouseEvent mouseEvent) {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(1));
        slide.setNode(SIDE_NOTE);
        slide.setToX(0);
        slide.play();
        SIDE_NOTE.setTranslateX(-140);
    }
}
