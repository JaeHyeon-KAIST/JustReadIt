package jri.justreadit.scenario;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.Scene;

import jri.justreadit.JRIApp;
import jri.justreadit.JRIBookCard;
import jri.justreadit.JRIScene;
import jri.justreadit.canvas.JRICanvas2D;
import jri.justreadit.pageController.FirstPageController;
import x.XApp;
import x.XCmdToChangeScene;
import x.XScenario;

import java.awt.*;

public class FirstScenario extends XScenario {
  // singleton pattern
  private static FirstScenario mSingleton = null;

  public static FirstScenario getSingleton() {
    assert (FirstScenario.mSingleton != null);
    return mSingleton;
  }

  public static FirstScenario createSingleton(XApp app) {
    assert (FirstScenario.mSingleton == null);
    FirstScenario.mSingleton = new FirstScenario(app);
    return FirstScenario.mSingleton;
  }

  private FirstScenario(XApp app) {
    super(app);
  }

  @Override
  protected void addScenes() {
    this.addScene(FirstScene.createSingleton(this));
    this.addScene(SecondScene.createSingleton(this));
  }

  public void dispatchMoveToSecondPageButtonPress() {
    if (this.getApp().getScenarioMgr().getCurScene() == FirstScene.mSingleton) {
      FirstScene.mSingleton.onMoveToSecondPageButtonPress();
    }
  }

  public void dispatchMoveToBookNotePageButtonPress() {
    if (this.getApp().getScenarioMgr().getCurScene() == FirstScene.mSingleton) {
      FirstScene.mSingleton.onMoveToBookNotePageButtonPress();
    }
  }

  public static class FirstScene extends JRIScene {
    private static FirstScene mSingleton = null;
    // singleton
    private final EventHandler<MouseEvent> mousePressedHandler;
    private final EventHandler<MouseEvent> mouseDraggedHandler;
    private final EventHandler<KeyEvent> keyPressedHandler;
    private final EventHandler<KeyEvent> keyReleasedHandler;

    public static FirstScene getSingleton() {
      assert (FirstScene.mSingleton != null);
      return FirstScene.mSingleton;
    }

    public static FirstScene createSingleton(XScenario scenario) {
      assert (FirstScene.mSingleton == null);
      FirstScene.mSingleton = new FirstScene(scenario);
      return FirstScene.mSingleton;
    }

    public void onMoveToSecondPageButtonPress() {
      XCmdToChangeScene.execute(this.mScenario.getApp(), BookShelfScenario.BookShelfScene.getSingleton(), this);
    }

    public void onMoveToBookNotePageButtonPress() {
      BookNoteScenario.getSingleton().setCurrentBookId(10);
      XCmdToChangeScene.execute(this.mScenario.getApp(), BookNoteScenario.ReadyScene.getSingleton(), this);
    }

    private FirstScene(XScenario scenario) {
      super(scenario);
      mousePressedHandler = this::handleMousePressed;
      mouseDraggedHandler = this::handleMouseDragged;
      keyPressedHandler = this::handleKeyPressed;
      keyReleasedHandler = this::handleKeyReleased;
    }

    @Override
    public void getReady() {
      JRIApp jri = (JRIApp) this.mScenario.getApp();
      Scene scene = jri.getPrimaryStage().getScene();

      // 먼저 이벤트 필터 추가
      System.out.println("Adding event filters");
      scene.addEventFilter(MouseEvent.MOUSE_PRESSED, mousePressedHandler);
      scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseDraggedHandler);
      scene.addEventFilter(KeyEvent.KEY_PRESSED, keyPressedHandler);
      scene.addEventFilter(KeyEvent.KEY_RELEASED, keyReleasedHandler);
      Platform.runLater(() -> {
        System.out.println("Requesting focus back to the root node");
        jri.getPrimaryStage().getScene().getRoot().requestFocus();
      });


      String currentPage = jri.getPageControllerMgr().getCurrentPageName();
      if (!FirstPageController.PAGE_CONTROLLER_NAME.equals(currentPage)) {
        System.out.println("Switching to FirstPageController");
        Platform.runLater(() -> {
          jri.getPageControllerMgr().switchTo(FirstPageController.PAGE_CONTROLLER_NAME);
        });
      }
    }

    @Override
    public void wrapUp() {
      JRIApp jri = (JRIApp) this.mScenario.getApp();
      Scene scene = jri.getPrimaryStage().getScene();
      scene.removeEventFilter(MouseEvent.MOUSE_PRESSED, mousePressedHandler);
      scene.removeEventFilter(MouseEvent.MOUSE_DRAGGED, mouseDraggedHandler);
      scene.removeEventFilter(KeyEvent.KEY_PRESSED, keyPressedHandler);
      scene.removeEventFilter(KeyEvent.KEY_RELEASED, keyReleasedHandler);
    }

    private void handleMousePressed(MouseEvent e) {
      System.out.println("Mouse pressed in FirstScenario at: " + e.getSceneX() + ", " + e.getSceneY());
    }

    private void handleMouseDragged(MouseEvent e) {
      System.out.println("Mouse dragged in FirstScenario at: " + e.getSceneX() + ", " + e.getSceneY());
    }

    private void handleKeyPressed(KeyEvent e) {
      switch (e.getCode()) {
        case B:
          System.out.println("B key pressed");
          JRIApp jri = (JRIApp) this.mScenario.getApp();
          JRICanvas2D canvas = jri.getJRICanvas2D();

          Point screenLocation = MouseInfo.getPointerInfo().getLocation();
          Point canvasLocation = canvas.getLocationOnScreen();
          int relativeX = screenLocation.x - canvasLocation.x;
          int relativeY = screenLocation.y - canvasLocation.y;

          Platform.runLater(() -> {
            System.out.println("Requesting focus back to the root node");
            jri.getPrimaryStage().getScene().getRoot().requestFocus();
          });


          canvas.createTempBookCard(new Point(relativeX, relativeY));
          XCmdToChangeScene.execute(jri, FirstScenario.SecondScene.getSingleton(), this);
          break;
      }
    }

    private void handleKeyReleased(KeyEvent e) {
      System.out.println("Key released in FirstScenario: " + e.getCode());
    }
  }

  public static class SecondScene extends JRIScene {
    private final EventHandler<KeyEvent> keyReleasedHandler;
    // singleton
    private static SecondScene mSingleton = null;

    public static SecondScene getSingleton() {
      assert (SecondScene.mSingleton != null);
      return SecondScene.mSingleton;
    }

    public static SecondScene createSingleton(XScenario scenario) {
      assert (SecondScene.mSingleton == null);
      SecondScene.mSingleton = new SecondScene(scenario);
      return SecondScene.mSingleton;
    }

    private SecondScene(XScenario scenario) {
      super(scenario);
      keyReleasedHandler = this::handleKeyReleased;
    }

    @Override
    public void getReady() {
      JRIApp jri = (JRIApp) this.mScenario.getApp();
      Scene scene = jri.getPrimaryStage().getScene();
      scene.addEventFilter(KeyEvent.KEY_RELEASED, keyReleasedHandler);
    }

    @Override
    public void wrapUp() {
      JRIApp jri = (JRIApp) this.mScenario.getApp();
      Scene scene = jri.getPrimaryStage().getScene();
      scene.removeEventFilter(KeyEvent.KEY_RELEASED, keyReleasedHandler);
    }

    private void handleKeyReleased(KeyEvent e) {
      switch (e.getCode()) {
        case B:
          JRIApp jri = (JRIApp) this.mScenario.getApp();
          JRICanvas2D canvas = jri.getJRICanvas2D();

          canvas.addTempBookCard();
          canvas.repaint();
          XCmdToChangeScene.execute(jri, this.mReturnScene, null);
          break;
      }
    }
  }
}
