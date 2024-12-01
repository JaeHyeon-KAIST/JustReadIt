package com.jaehyeon.jri.justreadit.pageController;

import com.jaehyeon.jri.justreadit.JRIApp;
import com.jaehyeon.jri.justreadit.scenario.BookNoteScenario;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import x.XPageController;

public class BookNotePageController extends XPageController {
  public static final String PAGE_CONTROLLER_NAME = "BookNotePageController";
  public static final String FXML_NAME = "BookNotePage";

  public BookNotePageController(JRIApp app, String fxmlBasePath) {
    super(PAGE_CONTROLLER_NAME, fxmlBasePath, FXML_NAME, app);
  }

  @FXML
  private WebView webView;

  @FXML
  private Button goBackButton;

  @FXML
  public void initialize() {
    // WebView 초기화
    WebEngine webEngine = webView.getEngine();
    String initialURL = "https://jaehyeon.com/";
    webEngine.load(initialURL);

    // locationProperty 변경 감지하여 링크 클릭 이벤트 처리
    webEngine.locationProperty().addListener(new ChangeListener<String>() {
      private boolean isFirstLoad = true;

      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (isFirstLoad) {
          // 초기 페이지 로드는 무시
          isFirstLoad = false;
        } else {
          // 링크 클릭 시 동작
          System.out.println("Link clicked: " + newValue);
          // 이동 막기
          webEngine.getLoadWorker().cancel();
        }
      }
    });

    // Go Back 버튼 이벤트 처리
    goBackButton.setOnAction(event -> {
      BookNoteScenario scenario = (BookNoteScenario) this.mApp.getScenarioMgr().getCurScene().getScenario();
      scenario.dispatchReturnButtonPress();
    });
  }
}
