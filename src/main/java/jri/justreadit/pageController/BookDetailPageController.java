package jri.justreadit.pageController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import jri.justreadit.JRIApp;
import jri.justreadit.scenario.BookDetailScenario;
import jri.justreadit.scenario.HomeScenario;
import jri.justreadit.utils.AladdinOpenAPI.AladdinBookItem;
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
  private ImageView bookCoverImageView;

  @FXML
  public void initialize() {
    System.out.println("BookDetailPageController initialized");
    JRIApp jri = (JRIApp) this.mApp;
    AladdinBookItem bookItem = jri.getSelectedBookAndNoteMgr().getSelectedBookCard().getBookItem();

    bookTitleText.setText(bookItem.getTitle());
    bookAuthorText.setText(bookItem.getAuthor());
    bookPublisherText.setText(bookItem.getPublisher());
    if (bookItem.getCover() != null && !bookItem.getCover().isEmpty()) {
      try {
        // URL로부터 Image 객체 생성
        Image coverImage = new Image(bookItem.getCover(), true); // true로 설정하면 background-loading
        bookCoverImageView.setImage(coverImage);
      } catch (Exception e) {
        System.err.println("Error loading book cover image: " + e.getMessage());
      }
    }
  }

  public void goToBookShelfPage() {
    System.out.println("Go to BookShelfPage button pressed");
    // Scenario와 Scene을 통한 동작 위임
    BookDetailScenario scenario = (BookDetailScenario) this.mApp.getScenarioMgr().getCurScene().getScenario();
    scenario.dispatchGoToBookShelfPageButtonPress();
  }

  public void goToHomePage() {
    System.out.println("Go to BookShelfPage button pressed");
    // Scenario와 Scene을 통한 동작 위임
    BookDetailScenario scenario = (BookDetailScenario) this.mApp.getScenarioMgr().getCurScene().getScenario();
    scenario.dispatchGoToHomePageButtonPress();
  }
}
