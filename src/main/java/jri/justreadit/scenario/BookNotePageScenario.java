package jri.justreadit.scenario;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import jri.justreadit.JRIApp;
import jri.justreadit.JRIScene;
import jri.justreadit.pageController.BookNotePageController;
import jri.justreadit.utils.ServerAPI;
import x.XApp;
import x.XCmdToChangeScene;
import x.XScenario;

import java.util.Map;

public class BookNotePageScenario extends XScenario {
  // singleton pattern
  private static BookNotePageScenario mSingleton = null;

  public static BookNotePageScenario getSingleton() {
    assert (BookNotePageScenario.mSingleton != null);
    return mSingleton;
  }

  public static BookNotePageScenario createSingleton(XApp app) {
    assert (BookNotePageScenario.mSingleton == null);
    BookNotePageScenario.mSingleton = new BookNotePageScenario(app);
    return BookNotePageScenario.mSingleton;
  }

  private BookNotePageScenario(XApp app) {
    super(app);
  }

  @Override
  protected void addScenes() {
    this.addScene(WritingScene.createSingleton(this));
    this.addScene(SearchNoteScene.createSingleton(this));
  }

  public void dispatchMoveToHomePageButtonPress() {
    if (this.getApp().getScenarioMgr().getCurScene() == WritingScene.mSingleton) {
      WritingScene.mSingleton.onMoveToHomePageButtonPress();
    }
  }

  public void dispatchGoToBookShelfPageButtonPress() {
    if (this.getApp().getScenarioMgr().getCurScene() == WritingScene.mSingleton) {
      WritingScene.mSingleton.dispatchGoToBookShelfPageButtonPress();
    }
  }

  public void dispatchCloseBookSearchModal() {
    SearchNoteScene.mSingleton.closeNoteSearch();
  }

  public void dispatchOpenLikedBookSideView(int noteId) {
    WritingScene.mSingleton.openLikedBookSideView(noteId);
  }

  public static class WritingScene extends JRIScene {
    private final EventHandler<KeyEvent> keyPressedHandler;
    // singleton
    private static WritingScene mSingleton = null;

    public static WritingScene getSingleton() {
      assert (WritingScene.mSingleton != null);
      return WritingScene.mSingleton;
    }

    public static WritingScene createSingleton(XScenario scenario) {
      assert (WritingScene.mSingleton == null);
      WritingScene.mSingleton = new WritingScene(scenario);
      return WritingScene.mSingleton;
    }

    public void dispatchGoToBookShelfPageButtonPress() {
      XCmdToChangeScene.execute(this.mScenario.getApp(), BookShelfScenario.BookShelfScene.getSingleton(), this);
    }

    public void onMoveToHomePageButtonPress() {
      XCmdToChangeScene.execute(this.mScenario.getApp(), HomeScenario.FirstScene.getSingleton(), this);
    }

    public void openLikedBookSideView(int noteId) {
      Map<String, Object> noteInfo = ServerAPI.getNoteInfo(noteId);
      if (noteInfo != null) {
        String noteTitle = (String) noteInfo.get("noteTitle");
        String text = (String) noteInfo.get("text");
        Map<String, Object> book = (Map<String, Object>) noteInfo.get("book");
        // 필요한 처리 수행
        System.out.println("noteTitle: " + noteTitle);
        System.out.println("text: " + text);
        System.out.println("book: " + book);

        BookNotePageController controller = (BookNotePageController) ((JRIApp) this.mScenario.getApp()).getPageControllerMgr().getController(BookNotePageController.PAGE_CONTROLLER_NAME);
        controller.setAndOpenLikedBookSideView(noteTitle, text, book);
      }
    }

    private WritingScene(XScenario scenario) {
      super(scenario);
      keyPressedHandler = this::handleKeyPressed;
    }

    @Override
    public void getReady() {
      JRIApp jri = (JRIApp) this.mScenario.getApp();
      Scene scene = jri.getPrimaryStage().getScene();

      scene.addEventFilter(KeyEvent.KEY_PRESSED, keyPressedHandler);

      String currentPage = jri.getPageControllerMgr().getCurrentPageName();
      if (!BookNotePageController.PAGE_CONTROLLER_NAME.equals((currentPage))) {
        System.out.println("Switching to BookNotePageController");

        Platform.runLater(() -> {
          jri.getPageControllerMgr().switchTo(BookNotePageController.PAGE_CONTROLLER_NAME);
        });
      }
    }

    @Override
    public void wrapUp() {
      JRIApp jri = (JRIApp) this.mScenario.getApp();
      Scene scene = jri.getPrimaryStage().getScene();
      scene.removeEventFilter(KeyEvent.KEY_PRESSED, keyPressedHandler);
    }

    private void handleKeyPressed(KeyEvent e) {
      switch (e.getCode()) {
        case ALT:
          JRIApp jri = (JRIApp) this.mScenario.getApp();

          BookNotePageController controller = (BookNotePageController) jri.getPageControllerMgr().getController(BookNotePageController.PAGE_CONTROLLER_NAME);
          // 검색창 열기
          Platform.runLater(controller::openModal);

          XCmdToChangeScene.execute(jri, SearchNoteScene.getSingleton(), this);
          break;
      }
    }
  }

  public static class SearchNoteScene extends JRIScene {
    // singleton
    private static SearchNoteScene mSingleton = null;

    public static SearchNoteScene getSingleton() {
      assert (SearchNoteScene.mSingleton != null);
      return SearchNoteScene.mSingleton;
    }

    public static SearchNoteScene createSingleton(XScenario scenario) {
      assert (SearchNoteScene.mSingleton == null);
      SearchNoteScene.mSingleton = new SearchNoteScene(scenario);
      return SearchNoteScene.mSingleton;
    }

    private SearchNoteScene(XScenario scenario) {
      super(scenario);
    }

    public void closeNoteSearch() {
      JRIApp jri = (JRIApp) this.mScenario.getApp();
      XCmdToChangeScene.execute(jri, this.mReturnScene, null);
    }

    @Override
    public void getReady() {
    }

    @Override
    public void wrapUp() {
      JRIApp jri = (JRIApp) this.mScenario.getApp();
      Scene scene = jri.getPrimaryStage().getScene();
    }
  }
}
