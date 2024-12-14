package jri.justreadit.pageController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import jri.justreadit.AladdinOpenAPI.AladdinOpenAPI;
import jri.justreadit.AladdinOpenAPI.AladdinResponse;
import jri.justreadit.AladdinOpenAPI.AladdinBookItem;
import jri.justreadit.JRIApp;
import jri.justreadit.scenario.FirstScenario;
import x.XPageController;

import javax.swing.*;

public class FirstPageController extends XPageController {
  public static final String PAGE_CONTROLLER_NAME = "FirstPageController";
  public static final String FXML_NAME = "FirstPage";

  public FirstPageController(JRIApp app, String fxmlBasePath) {
    super(PAGE_CONTROLLER_NAME, fxmlBasePath, FXML_NAME, app);
  }

  @FXML
  private SwingNode swingNode;

  @FXML
  private StackPane modalOverlay; // 모달 오버레이

  @FXML
  private TextField modalInputField; // 입력창

  @FXML
  private ListView<AladdinBookItem> searchResultsList;

  private ObservableList<AladdinBookItem> searchResultsObservable;

  @FXML
  public void initialize() {
    JRIApp jri = (JRIApp) this.mApp;

    SwingUtilities.invokeLater(() -> {
      swingNode.setContent(jri.getJRICanvas2D());
      jri.getJRICanvas2D().setPreferredSize(new java.awt.Dimension(1728, 600));
      jri.getJRICanvas2D().repaint();
    });

    // Initialize ObservableList for ListView
    searchResultsObservable = FXCollections.observableArrayList();
    searchResultsList.setItems(searchResultsObservable);

    // Custom ListCell 설정
    searchResultsList.setCellFactory(listView -> new ListCell<AladdinBookItem>() {
      @Override
      protected void updateItem(AladdinBookItem item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
        } else {
          setText(item.getTitle() + " - " + item.getAuthor());
        }
      }
    });
  }

  @FXML
  public void moveToSecondPage() {
    FirstScenario scenario = (FirstScenario) this.mApp.getScenarioMgr().getCurScene().getScenario();
    scenario.dispatchMoveToSecondPageButtonPress();
  }

  @FXML
  public void moveToBookNotePage() {
    FirstScenario scenario = (FirstScenario) this.mApp.getScenarioMgr().getCurScene().getScenario();
    scenario.dispatchMoveToBookNotePageButtonPress();
  }

  @FXML
  public void openModal() {
    modalOverlay.setVisible(true); // 모달 창 표시
    modalInputField.clear();
    searchResultsObservable.clear(); // 검색 기록 초기화
    searchResultsList.setVisible(false); // ListView 숨기기
    modalInputField.requestFocus(); // 검색창에 포커스 설정
  }

  @FXML
  public void onModalOverlayClicked() {
    System.out.println("Modal overlay clicked!");
  }

  @FXML
  public void onModalInputKeyPressed(javafx.scene.input.KeyEvent event) {
    switch (event.getCode()) {
      case ENTER:
        String inputText = modalInputField.getText();
        searchBook(inputText);
        break;
      default:
        break;
    }
  }

  private void searchBook(String keyword) {
    try {
      AladdinOpenAPI api = new AladdinOpenAPI();

      try {
        AladdinResponse response = api.searchItems(keyword, 10, 1);
        // Clear previous results
        searchResultsObservable.clear();
        // Add new results
        searchResultsObservable.addAll(response.getItems());
        // Set ListView visibility based on results
        searchResultsList.setVisible(!searchResultsObservable.isEmpty());
      } catch (Exception ex) {
        System.out.println("Exception: " + ex.getMessage());
        searchResultsList.setVisible(false); // Hide the list on error
      }
    } catch (Exception ex) {
      System.out.println("Exception: " + ex.getMessage());
      searchResultsList.setVisible(false); // Hide the list on error
    }
  }

  @FXML
  public void onSearchResultClicked(MouseEvent event) {
    if (event.getClickCount() == 2) {
      AladdinBookItem selectedItem = searchResultsList.getSelectionModel().getSelectedItem();
      if (selectedItem != null) {
        modalOverlay.setVisible(false); // 모달 창 숨김
        FirstScenario scenario = (FirstScenario) this.mApp.getScenarioMgr().getCurScene().getScenario();
        scenario.dispatchAddNewBookCard(selectedItem);
      }
    }
  }

  @FXML
  public void exitButton() {
    System.out.println("Exiting application...");
    System.exit(0);
  }
}
