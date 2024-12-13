package jri.justreadit.pageController;

import jri.justreadit.JRIApp;
import jri.justreadit.scenario.FirstScenario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import x.XPageController;

public class FirstPageController extends XPageController {
  public static final String PAGE_CONTROLLER_NAME = "FirstPageController";
  public static final String FXML_NAME = "FirstPage";

  public FirstPageController(JRIApp app, String fxmlBasePath) {
    super(PAGE_CONTROLLER_NAME, fxmlBasePath, FXML_NAME, app);
  }

  @FXML
  private Button secondPageButton;

  @FXML
  private Button bookNotePageButton;

  @FXML
  private Button exitButton;

  @FXML
  public void initialize() {
    // Start 버튼 클릭 이벤트 처리
    secondPageButton.setOnAction(e -> {
      // Scenario와 Scene을 통한 동작 위임
      FirstScenario scenario = (FirstScenario) this.mApp.getScenarioMgr().getCurScene().getScenario();
      scenario.dispatchMoveToSecondPageButtonPress();
    });

    bookNotePageButton.setOnAction(e -> {
      FirstScenario scenario = (FirstScenario) this.mApp.getScenarioMgr().getCurScene().getScenario();
      scenario.dispatchMoveToBookNotePageButtonPress();
    });

    // Exit 버튼 클릭 이벤트 처리
    exitButton.setOnAction(e -> {
      System.out.println("Exiting application...");
      System.exit(0);
    });
  }
}
