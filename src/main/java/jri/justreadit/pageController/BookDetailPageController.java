package jri.justreadit.pageController;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import jri.justreadit.JRIApp;
import jri.justreadit.JRIBookCard;
import jri.justreadit.JRIBookNoteInfo;
import jri.justreadit.scenario.BookDetailScenario;
import jri.justreadit.scenario.HomeScenario;
import jri.justreadit.utils.AladdinOpenAPI.AladdinBookItem;
import x.XPageController;

import java.util.ArrayList;

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
  private FlowPane notesContainer;

  @FXML
  private ScrollPane notesScrollPane;

  @FXML
  private VBox connectedBooksContainer;

  @FXML
  private StackPane modalOverlay; // 모달 오버레이

  @FXML
  private TextField modalInputField; // 입력창

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

  public void openModal() {
    modalOverlay.setVisible(true); // 모달 창 표시
//    searchResultsObservable.clear(); // 검색 기록 초기화
    modalInputField.clear();
    modalInputField.requestFocus(); // 검색창에 포커스 설정
  }

  @FXML
  public void onModalOverlayClicked(MouseEvent event) {
    System.out.println("Modal overlay clicked!");
    if (event.getTarget() == modalOverlay) { // 배경 영역인지 확인
      modalOverlay.setVisible(false); // 모달 창 숨김
      BookDetailScenario scenario = (BookDetailScenario) this.mApp.getScenarioMgr().getCurScene().getScenario();
      scenario.dispatchCloseModal();
    }
  }

  @FXML
  public void onSaveButtonClicked() {
    String inputText = modalInputField.getText(); // TextField 값 가져오기
    System.out.println("Save button clicked!");
    System.out.println("Modal Input Field Value: " + inputText);
  }

  @FXML
  public void onDeleteButtonClicked() {

  }

  // 노트 데이터를 기반으로 UI 생성
  public void populateNotes(ArrayList<JRIBookNoteInfo> bookNotes) {
    notesContainer.getChildren().clear(); // 기존 노트 UI 초기화

    for (JRIBookNoteInfo note : bookNotes) {
      // VBox로 각 노트 UI 구성
      VBox noteBox = new VBox();
      noteBox.setAlignment(Pos.TOP_CENTER);
      noteBox.setPrefWidth(250);
      noteBox.setSpacing(20);

      // ImageView 추가
      ImageView noteImageView = new ImageView();
      noteImageView.setFitHeight(200);
      noteImageView.setFitWidth(150);
      noteImageView.setPreserveRatio(true);

      // 이미지 경로 설정
      String noteImagePath = note.getType().equals("after") ?
        "/image/after_book.png" :
        "/image/during_book.png";

      // 리소스 로드
      try {
        Image image = new Image(getClass().getResource(noteImagePath).toExternalForm());
        noteImageView.setImage(image);
      } catch (NullPointerException e) {
        System.err.println("Image not found: " + noteImagePath);
      }

      // Text 추가
      Text noteTitle = new Text(note.getTitle());
      noteTitle.setFont(new Font("Apple SD Gothic Neo Light", 24));
      noteTitle.setWrappingWidth(150);

      // Button 추가
      Button noteButton = new Button();
      noteButton.setMnemonicParsing(false);
      noteButton.setPrefSize(200, 300);
      noteButton.getStyleClass().add("menu_btn");
      noteButton.setGraphic(noteBox);

      // 버튼 클릭 이벤트
      noteButton.setOnAction(e -> {
        System.out.println("Selected note: " + note.getTitle());
        System.out.println("Selected note type: " + note.getType());
        System.out.println("Selected note content: " + note.getText());
        System.out.println("Selected note id: " + note.getNoteId());
        // 노트 세부 페이지로 이동 로직 추가 가능
        BookDetailScenario scenario = (BookDetailScenario) this.mApp.getScenarioMgr().getCurScene().getScenario();
        scenario.dispatchGoToNotePage(note);
      });

      // VBox에 ImageView와 Text 추가
      noteBox.getChildren().addAll(noteImageView, noteTitle);

      // notesContainer에 추가
      notesContainer.getChildren().add(noteButton);
    }
  }

  public void populateConnectedBooks(ArrayList<JRIBookCard> books) {
    connectedBooksContainer.getChildren().clear(); // 기존 더미 데이터를 삭제합니다.

    for (JRIBookCard book : books) {
      // ImageView 생성
      ImageView bookImageView = new ImageView();
      bookImageView.setFitWidth(221);
      bookImageView.setFitHeight(190);
      bookImageView.setPreserveRatio(true);

      // 표지 이미지 설정
      try {
        Image coverImage = new Image(book.getBookItem().getCover(), true);
        bookImageView.setImage(coverImage);
      } catch (Exception e) {
        System.err.println("Failed to load image: " + e.getMessage());
      }

      // 클릭 이벤트 (선택한 책의 정보 출력 또는 상세 페이지 이동)
      bookImageView.setOnMouseClicked(event -> {
        System.out.println("Clicked book: " + book.getBookItem().getTitle());
        // 상세 페이지 이동 로직 추가 가능
      });

      connectedBooksContainer.getChildren().add(bookImageView); // VBox에 추가
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

  @FXML
  public void goBack() {
    BookDetailScenario scenario = (BookDetailScenario) this.mApp.getScenarioMgr().getCurScene().getScenario();
    scenario.dispatchGoBackButtonPressed();
  }
}
