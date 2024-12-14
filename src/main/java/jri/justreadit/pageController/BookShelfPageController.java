package jri.justreadit.pageController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import jri.justreadit.JRIApp;
import jri.justreadit.scenario.BookDetailScenario;
import jri.justreadit.scenario.BookShelfScenario;
import x.XPageController;

public class BookShelfPageController extends XPageController {
  public static final String PAGE_CONTROLLER_NAME = "BookShelfPageController";
  public static final String FXML_NAME = "BookShelfPage";

  @FXML
  private Button goToHomePageButton;

  public BookShelfPageController(JRIApp app, String fxmlBasePath) {
    super(PAGE_CONTROLLER_NAME, fxmlBasePath, FXML_NAME, app);
  }

  @FXML
  public void initialize() {
  }

  public void goToHomePage() {
    System.out.println("Go to BookShelfPage button pressed");
    // Scenario와 Scene을 통한 동작 위임
    BookShelfScenario scenario = (BookShelfScenario) this.mApp.getScenarioMgr().getCurScene().getScenario();
    scenario.dispatchGoToHomePageButtonPress();
  }
}
