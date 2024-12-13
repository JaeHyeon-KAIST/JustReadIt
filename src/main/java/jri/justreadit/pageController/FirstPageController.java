package jri.justreadit.pageController;

import javafx.embed.swing.SwingNode;
import javafx.scene.layout.Pane;
import jri.justreadit.JRIApp;
import jri.justreadit.scenario.FirstScenario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import x.XPageController;

import javax.swing.*;

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
  private SwingNode swingNode;  // @FXML 추가

  @FXML
  private Pane swingNodePane;   // @FXML 추가

  @FXML
  public void initialize() {
    JRIApp jri = (JRIApp) this.mApp;

    SwingUtilities.invokeLater(() -> {
      swingNode.setContent(jri.getJRICanvas2D());
      jri.getJRICanvas2D().setPreferredSize(new java.awt.Dimension(1728, 600)); // Swing 컴포넌트 크기 설정
      jri.getJRICanvas2D().repaint(); // 강제로 다시 그리기 요청
    });

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

  private void createSwingContent() {
    SwingUtilities.invokeLater(() -> {
      JPanel panel = new JPanel();
      panel.add(new JLabel("This is a Swing Component!"));
      panel.add(new JButton("Click Me"));
      swingNode.setContent(panel); // SwingNode에 Swing 컴포넌트를 설정
    });
  }
}
