package jri.justreadit.scenario;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.Scene;

import javafx.util.Duration;
import jri.justreadit.utils.AladdinOpenAPI.AladdinBookItem;
import jri.justreadit.JRIApp;
import jri.justreadit.JRIBookCard;
import jri.justreadit.JRIScene;
import jri.justreadit.canvas.JRICanvas2D;
import jri.justreadit.pageController.FirstPageController;
import jri.justreadit.utils.ServerAPI;
import x.XApp;
import x.XCmdToChangeScene;
import x.XScenario;

import java.awt.*;
import java.util.List;
import java.util.Map;

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
    AddBookScene.mSingleton.addNewBookCard(selectedItem);
  }

  public void dispatchCloseBookSearchModal() {
    AddBookScene.mSingleton.closeBookSearch();
  }

  public static class FirstScene extends JRIScene {
    private boolean isDoubleClickInProgress = false;
    private final EventHandler<MouseEvent> mousePressedHandler;
    private final EventHandler<MouseEvent> mouseDraggedHandler;
    private final EventHandler<KeyEvent> keyPressedHandler;
    private final EventHandler<KeyEvent> keyReleasedHandler;
    // singleton
    private static FirstScene mSingleton = null;

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
        // ServerAPI.getBookList() 호출 및 데이터 처리
        new Thread(() -> {
          List<Map<String, Object>> bookList = ServerAPI.getBookList();
          if (bookList != null) {
            Platform.runLater(() -> {
              System.out.println("Fetched book list from server, processing...");
              JRICanvas2D canvas = jri.getJRICanvas2D();
              canvas.initBookCards();

              for (Map<String, Object> book : bookList) {
                try {
                  System.out.println("Processing book data: " + book);

                  String id = String.valueOf(book.get("id"));
                  String title = (String) book.get("title");
                  String publisher = (String) book.get("publisher");
                  String cover = (String) book.get("cover");
                  int positionX = (int) book.get("positionX");
                  int positionY = (int) book.get("positionY");

                  AladdinBookItem bookItem = new AladdinBookItem();
                  bookItem.setItemId(id);
                  bookItem.setTitle(title);
                  bookItem.setPublisher(publisher);
                  bookItem.setCover(cover);
                  JRIBookCard bookCard = new JRIBookCard(bookItem, new Point(positionX, positionY));

                  canvas.initializeBookCards(bookCard); // 캔버스에 책 카드 추가
                } catch (Exception e) {
                  e.printStackTrace();
                  System.err.println("Error processing book data: " + book);
                }
              }

              canvas.repaint(); // 캔버스 갱신
            });
          } else {
            System.err.println("Failed to fetch book list.");
          }
        }).start();


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

      JRIBookCard clickedCard = canvas.getClickedBookCard(new Point(relativeX, relativeY));
      if (clickedCard == null) return;

      // 더블 클릭 처리 우선
      if (e.getClickCount() == 2) {
        isDoubleClickInProgress = true;
        System.out.println("Double-clicked on book card: " + clickedCard.getBookItem().getItemId());
        BookDetailScenario.getSingleton().setCurrentBook(clickedCard);
        XCmdToChangeScene.execute(jri, BookDetailScenario.BookDetailScene.getSingleton(), this);
      } else if (!isDoubleClickInProgress) {
        // 단일 클릭 처리
        Timeline singleClickTimeline = new Timeline(new KeyFrame(
          Duration.millis(300), // 더블 클릭 간격 대기
          ev -> {
            if (!isDoubleClickInProgress) { // 더블 클릭이 아닌 경우에만 실행
              System.out.println("Clicked on book card: " + clickedCard.getBookItem().getItemId());
              canvas.setSelectedBookCard(clickedCard);
              canvas.setPreviousMousePosition(new Point(relativeX, relativeY));
              XCmdToChangeScene.execute(jri, MoveBookScene.getSingleton(), this);
            }
          }
        ));
        singleClickTimeline.setCycleCount(1);
        singleClickTimeline.play();
      }

      // 300ms 후 더블 클릭 플래그 초기화
      Timeline resetFlagTimeline = new Timeline(new KeyFrame(
        Duration.millis(300),
        ev -> isDoubleClickInProgress = false
      ));
      resetFlagTimeline.setCycleCount(1);
      resetFlagTimeline.play();
    }

    private void handleMouseDragged(MouseEvent e) {
    }

    private void handleKeyPressed(KeyEvent e) {
      switch (e.getCode()) {
        case B:
          System.out.println("B key pressed");
          e.consume();
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
      Point position = canvas.getNewBookCardPosition();

      System.out.println("Selected Item Details:");
      System.out.println("Title: " + selectedItem.getTitle());
      System.out.println("Author: " + selectedItem.getAuthor());
      System.out.println("Publisher: " + selectedItem.getPublisher());
      System.out.println("Cover: " + selectedItem.getCover());

      // UI에 새 책 카드 추가
      canvas.addBookCard(selectedItem);
      canvas.repaint();

      // 서버에 책 추가 요청 (비동기)
      Task<Boolean> addBookTask = new Task<>() {
        @Override
        protected Boolean call() throws Exception {
          return ServerAPI.addBook(selectedItem, position.x, position.y);
        }
      };

      addBookTask.setOnSucceeded(event -> {
        if (addBookTask.getValue()) {
          System.out.println("Book added to the server successfully.");
        } else {
          System.err.println("Failed to add the book to the server.");
        }
      });

      addBookTask.setOnFailed(event -> {
        System.err.println("Error while adding book to the server.");
        addBookTask.getException().printStackTrace();
      });

      new Thread(addBookTask).start();

      // Scene 변경
      XCmdToChangeScene.execute(jri, this.mReturnScene, null);
    }

    public void closeBookSearch() {
      JRIApp jri = (JRIApp) this.mScenario.getApp();
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

      JRIBookCard selectedCard = canvas.getSelectedBookCard();

      if (selectedCard != null) {
        String id = selectedCard.getBookItem().getItemId();
        Point position = selectedCard.getPosition();

        System.out.println("Updating position for book ID: " + id + " to " + position);

        // 비동기로 서버에 위치 업데이트 요청
        Task<Boolean> updatePositionTask = new Task<>() {
          @Override
          protected Boolean call() throws Exception {
            return ServerAPI.updateBookPosition(id, position.x, position.y);
          }
        };

        updatePositionTask.setOnSucceeded(event -> {
          if (updatePositionTask.getValue()) {
            System.out.println("Book position updated successfully on the server.");
          } else {
            System.err.println("Failed to update book position on the server.");
          }
        });

        updatePositionTask.setOnFailed(event -> {
          System.err.println("Error while updating book position on the server.");
          updatePositionTask.getException().printStackTrace();
        });

        new Thread(updatePositionTask).start();
      } else {
        System.err.println("No book card selected for position update.");
      }

      // Reset selection and change scene
      canvas.setSelectedBookCard(null);
      canvas.setPreviousMousePosition(null);

      XCmdToChangeScene.execute(jri, this.mReturnScene, null);
    }
  }
}
