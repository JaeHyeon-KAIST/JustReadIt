package jri.justreadit.scenario;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.Scene;

import jri.justreadit.AladdinOpenAPI.AladdinBookItem;
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
    this.addScene(AddBookScene.createSingleton(this));
    this.addScene(MoveBookScene.createSingleton(this));
  }

  public void dispatchMoveToBookShelfPageButtonPress() {
    if (this.getApp().getScenarioMgr().getCurScene() == FirstScene.mSingleton) {
      FirstScene.mSingleton.onMoveToBookShelfPageButtonPress();
    }
  }

  public void dispatchMoveToBookNotePageButtonPress() {
    if (this.getApp().getScenarioMgr().getCurScene() == FirstScene.mSingleton) {
      FirstScene.mSingleton.onMoveToBookNotePageButtonPress();
    }
  }

  public void dispatchAddNewBookCard(AladdinBookItem selectedItem) {
    if (this.getApp().getScenarioMgr().getCurScene() == AddBookScene.mSingleton) {
      AddBookScene.mSingleton.addNewBookCard(selectedItem);
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

//    public void onMoveToSecondPageButtonPress() {
//      XCmdToChangeScene.execute(this.mScenario.getApp(), BookShelfScenario.BookShelfScene.getSingleton(), this);
//    }

    public void onMoveToBookShelfPageButtonPress() {
      XCmdToChangeScene.execute(this.mScenario.getApp(), BookShelfScenario.BookShelfScene.getSingleton(), this);
    }

    public void onMoveToBookNotePageButtonPress() {
      BookNotePageScenario.getSingleton().setCurrentBookId(10);
      XCmdToChangeScene.execute(this.mScenario.getApp(), BookNotePageScenario.NoteScene.getSingleton(), this);
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
      JRIApp jri = (JRIApp) this.mScenario.getApp();
      JRICanvas2D canvas = jri.getJRICanvas2D();

      Point screenLocation = MouseInfo.getPointerInfo().getLocation();
      Point canvasLocation = canvas.getLocationOnScreen();
      int relativeX = screenLocation.x - canvasLocation.x;
      int relativeY = screenLocation.y - canvasLocation.y;

      // check selected book card
      JRIBookCard clickedCard = canvas.getClickedBookCard(new Point(relativeX, relativeY));
      if (clickedCard != null) {
        System.out.println("Clicked on book card: " + clickedCard.getBookItem().getTitle());
        canvas.setSelectedBookCard(clickedCard);
        canvas.setPreviousMousePosition(new Point(relativeX, relativeY));
        XCmdToChangeScene.execute(jri, MoveBookScene.getSingleton(), this);
      }
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

          canvas.setNewBookCardPosition(new Point(relativeX, relativeY));

//          FirstPageController.openModal();
          FirstPageController controller = (FirstPageController) jri.getPageControllerMgr().getController(FirstPageController.PAGE_CONTROLLER_NAME);
          // 모달 창 열기
          Platform.runLater(controller::openModal);

          XCmdToChangeScene.execute(jri, AddBookScene.getSingleton(), this);
          break;
      }
    }

    private void handleKeyReleased(KeyEvent e) {
      System.out.println("Key released in FirstScenario: " + e.getCode());
    }
  }

  public static class AddBookScene extends JRIScene {
    // singleton
    private static AddBookScene mSingleton = null;

    public static AddBookScene getSingleton() {
      assert (AddBookScene.mSingleton != null);
      return AddBookScene.mSingleton;
    }

    public static AddBookScene createSingleton(XScenario scenario) {
      assert (AddBookScene.mSingleton == null);
      AddBookScene.mSingleton = new AddBookScene(scenario);
      return AddBookScene.mSingleton;
    }

    public void addNewBookCard(AladdinBookItem selectedItem) {
      JRIApp jri = (JRIApp) this.mScenario.getApp();
      JRICanvas2D canvas = jri.getJRICanvas2D();
      System.out.println("Selected Item Details:");
      System.out.println("Title: " + selectedItem.getTitle());
      System.out.println("Author: " + selectedItem.getAuthor());
      System.out.println("Publisher: " + selectedItem.getPublisher());
      System.out.println("Cover: " + selectedItem.getCover());
      canvas.addBookCard(selectedItem);
      canvas.repaint();
      XCmdToChangeScene.execute(jri, this.mReturnScene, null);
    }

    private AddBookScene(XScenario scenario) {
      super(scenario);
    }

    @Override
    public void getReady() {
    }

    @Override
    public void wrapUp() {
    }
  }

  public static class MoveBookScene extends JRIScene {
    private final EventHandler<MouseEvent> mouseDraggedHandler;
    private final EventHandler<MouseEvent> mouseReleasedHandler;
    private Point lastMousePosition; // 이전 마우스 위치 저장 변수
    // singleton
    private static MoveBookScene mSingleton = null;

    public static MoveBookScene getSingleton() {
      assert (MoveBookScene.mSingleton != null);
      return MoveBookScene.mSingleton;
    }

    public static MoveBookScene createSingleton(XScenario scenario) {
      assert (MoveBookScene.mSingleton == null);
      MoveBookScene.mSingleton = new MoveBookScene(scenario);
      return MoveBookScene.mSingleton;
    }

    private MoveBookScene(XScenario scenario) {
      super(scenario);
      mouseDraggedHandler = this::handleMouseDragged;
      mouseReleasedHandler = this::handleMouseReleased;
    }

    @Override
    public void getReady() {
      JRIApp jri = (JRIApp) this.mScenario.getApp();
      Scene scene = jri.getPrimaryStage().getScene();

      // 이벤트 핸들러 추가
      scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseDraggedHandler);
      scene.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);

      lastMousePosition = null; // 초기화
    }

    @Override
    public void wrapUp() {
    }

    private void handleMouseDragged(MouseEvent e) {
      JRIApp jri = (JRIApp) this.mScenario.getApp();
      JRICanvas2D canvas = jri.getJRICanvas2D();

      Point screenLocation = MouseInfo.getPointerInfo().getLocation();

      Point canvasPoint = canvas.screenPointToCanvasPoint(screenLocation);

      System.out.println("Mouse dragged in MoveBookScene at: " + canvasPoint.x + ", " + canvasPoint.y);

      canvas.updateSelectedBookCardPosition(canvasPoint);
      canvas.repaint();
    }

    private void handleMouseReleased(MouseEvent e) {
      JRIApp jri = (JRIApp) this.mScenario.getApp();
      JRICanvas2D canvas = jri.getJRICanvas2D();

      canvas.setSelectedBookCard(null);
      canvas.setPreviousMousePosition(null);

      XCmdToChangeScene.execute(jri, this.mReturnScene, null);
    }
  }
}
