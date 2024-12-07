package jri.justreadit.pageController;

import jri.justreadit.JRIApp;
import jri.justreadit.scenario.SecondScenario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import x.XPageController;

public class SecondPageController extends XPageController {
    public static final String PAGE_CONTROLLER_NAME = "SecondPageController";
    public static final String FXML_NAME = "SecondPage";

    public SecondPageController(JRIApp app, String fxmlBasePath) {
        super(PAGE_CONTROLLER_NAME, fxmlBasePath, FXML_NAME, app);
    }

    @FXML
    private Button returnButton;

    @FXML
    public void initialize() {
        // Start 버튼 클릭 이벤트 처리
        returnButton.setOnAction(e -> {
            SecondScenario scenario = (SecondScenario) this.mApp.getScenarioMgr().getCurScene().getScenario();
            scenario.dispatchReturnButtonPress();
        });
    }
}
