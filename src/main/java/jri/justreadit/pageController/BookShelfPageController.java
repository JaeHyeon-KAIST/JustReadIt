package jri.justreadit.pageController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import jri.justreadit.JRIApp;
import jri.justreadit.scenario.BookShelfScenario;
import x.XPageController;

public class BookShelfPageController extends XPageController {
  public static final String PAGE_CONTROLLER_NAME = "BookShelfPageController";
  public static final String FXML_NAME = "BookShelfPage";

  public BookShelfPageController(JRIApp app, String fxmlBasePath) {
    super(PAGE_CONTROLLER_NAME, fxmlBasePath, FXML_NAME, app);
  }

  @FXML
  private Button goToBookDetailPageButton;

  @FXML
  public void initialize() {
    goToBookDetailPageButton.setOnAction(event -> {
      System.out.println("Go to book detail page button pressed");
      BookShelfScenario scenario = (BookShelfScenario) this.mApp.getScenarioMgr().getCurScene().getScenario();
      scenario.dispatchGoToBookDetailPageButtonPress();
    });
  }
}
