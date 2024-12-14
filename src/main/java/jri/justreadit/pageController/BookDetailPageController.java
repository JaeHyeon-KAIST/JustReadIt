package jri.justreadit.pageController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import jri.justreadit.JRIApp;
import jri.justreadit.scenario.BookDetailScenario;
import x.XPageController;


public class BookDetailPageController extends XPageController {
  public static final String PAGE_CONTROLLER_NAME = "BookDetailPageController";
  public static final String FXML_NAME = "BookDetailPage";

  public BookDetailPageController(JRIApp app, String fxmlBasePath) {
    super(PAGE_CONTROLLER_NAME, fxmlBasePath, FXML_NAME, app);
  }

  @FXML
  private Text bookTitleText;

  @FXML
  private Text bookAuthorText;

  @FXML
  private Text bookPublisherText;

  @FXML
  private Button goToBookShelfPageButton;

  @FXML
  public void initialize() {
    
    goToBookShelfPageButton.setOnAction(e -> {
      // Scenario와 Scene을 통한 동작 위임
      BookDetailScenario scenario = (BookDetailScenario) this.mApp.getScenarioMgr().getCurScene().getScenario();
      scenario.dispatchGoToBookShelfPageButtonPress();
    });
  }
}
