package jri.justreadit;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import jri.justreadit.canvas.JRICanvas2D;
import x.XApp;
import x.XLogMgr;
import x.XScenarioMgr;

import javax.swing.*;

public class JRIApp extends XApp {
  // fields
  private Stage primaryStage;
  private StackPane rootPane; // 대신 CardLayout 역할 수행
  private JRIScenarioMgr scenarioMgr;
  private JRIPageControllerMgr pageControllerMgr; // 추가된 필드
  private XLogMgr mLogMgr;

  private JRICanvas2D mCanvas2D = null;

  public JRICanvas2D getJRICanvas2D() {
    return this.mCanvas2D;
  }

  // Root Pane getter
  public StackPane getRootPane() {
    return this.rootPane;
  }

  @Override
  public XLogMgr getLogMgr() {
    return this.mLogMgr;
  }

  @Override
  public XScenarioMgr getScenarioMgr() {
    return this.scenarioMgr;
  }

  @Override
  public Stage getPrimaryStage() {
    return this.primaryStage;
  }

  @Override
  public JRIPageControllerMgr getPageControllerMgr() {
    return this.pageControllerMgr;
  }

  // Constructor or Initialization Logic
  public JRIApp() {
    // Initialize components and managers
    this.rootPane = new StackPane();
    this.mLogMgr = new XLogMgr();
    this.mLogMgr.setPrintOn(true);
    this.mCanvas2D = new JRICanvas2D();

    // Initialize Page Controller Manager
    this.pageControllerMgr = new JRIPageControllerMgr(this);
    this.pageControllerMgr.initializeControllers(); // 초기화 필수
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    this.primaryStage = primaryStage;
    primaryStage.setTitle("Just Read It");

    // Configure and show the main stage
    Scene scene = new Scene(rootPane, 1728, 1117);
    primaryStage.setScene(scene);

    primaryStage.setOnCloseRequest(event -> {
      System.out.println("Closing application...");
      // Ensure proper termination of the application
      javafx.application.Platform.exit(); // Shut down JavaFX runtime
      System.exit(0); // Terminate JVM
    });

    primaryStage.show();

    // Initialize Scenario Manager
    this.scenarioMgr = new JRIScenarioMgr(this);
    this.scenarioMgr.setInitCurScene();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
