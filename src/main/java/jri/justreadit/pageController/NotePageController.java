package jri.justreadit.pageController;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.HTMLEditor;
import javafx.util.Duration;
import jri.justreadit.JRIApp;
import jri.justreadit.scenario.BookDetailScenario;
import jri.justreadit.scenario.FirstScenario;
import jri.justreadit.scenario.NotePageScenario;
import x.XPageController;

import java.awt.event.MouseEvent;

public class NotePageController extends XPageController {
    public static final String PAGE_CONTROLLER_NAME = "NotePageController";
    public static final String FXML_NAME = "NotePage";

    @FXML
    private Button goToBookShelfPageButton;

    @FXML
    private AnchorPane SIDE_NOTE;

    @FXML
    private HTMLEditor htmlEditor;

    public NotePageController(JRIApp app, String fxmlBasePath) {
        super(PAGE_CONTROLLER_NAME, fxmlBasePath, FXML_NAME, app);
    }

    @FXML
    public void initialize() {
        goToBookShelfPageButton.setOnAction(e -> {
            // Scenario와 Scene을 통한 동작 위임
            NotePageScenario scenario = (NotePageScenario) this.mApp.getScenarioMgr().getCurScene().getScenario();
            scenario.dispatchGoToBookShelfPageButtonPress();
        });
        SIDE_NOTE.setTranslateX(800);
    }

    public void showSlide(javafx.scene.input.MouseEvent mouseEvent) {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(0.5));
        slide.setNode(SIDE_NOTE);
        slide.setToX(100);
        slide.play();
        SIDE_NOTE.setTranslateX(0);

        htmlEditor.setMaxSize(1050,750);
    }

    public void hideSlide(javafx.scene.input.MouseEvent mouseEvent) {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(0.5));
        slide.setNode(SIDE_NOTE);
        slide.setToX(800);
        slide.play();
        SIDE_NOTE.setTranslateX(0);

        htmlEditor.setMaxSize(1400,750);
    }
}
