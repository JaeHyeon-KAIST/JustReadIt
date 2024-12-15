package jri.justreadit.scenario;

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import jri.justreadit.JRIApp;
import jri.justreadit.JRIBookCard;
import jri.justreadit.JRIBookNoteInfo;
import jri.justreadit.JRIScene;
import jri.justreadit.pageController.BookDetailPageController;
import jri.justreadit.utils.ServerAPI;
import x.XApp;
import x.XCmdToChangeScene;
import x.XScenario;

import java.util.ArrayList;

public class BookDetailScenario extends XScenario {
  // singleton pattern
  private static BookDetailScenario mSingleton = null;

  public static BookDetailScenario getSingleton() {
    assert (BookDetailScenario.mSingleton != null);
    return mSingleton;
  }

  public static BookDetailScenario createSingleton(XApp app) {
    assert (BookDetailScenario.mSingleton == null);
    BookDetailScenario.mSingleton = new BookDetailScenario(app);
    return BookDetailScenario.mSingleton;
  }

  private BookDetailScenario(XApp app) {
    super(app);
  }

  @Override
  protected void addScenes() {
    this.addScene(DefaultScene.createSingleton(this));
  }

  public void dispatchGoToBookShelfPageButtonPress() {
    if (this.getApp().getScenarioMgr().getCurScene() == DefaultScene.mSingleton) {
      DefaultScene.mSingleton.dispatchGoToBookShelfPageButtonPress();
    }
  }

  public void dispatchGoToHomePageButtonPress() {
    if (this.getApp().getScenarioMgr().getCurScene() == DefaultScene.mSingleton) {
      DefaultScene.mSingleton.dispatchGoToHomePageButtonPress();
    }
  }

  public void dispatchGoToNotePage(JRIBookNoteInfo note) {
    if (this.getApp().getScenarioMgr().getCurScene() == DefaultScene.mSingleton) {
      DefaultScene.mSingleton.goToNotePage(note);
    }
  }

  public static class DefaultScene extends JRIScene {
    private long lastClickTime = 0;
    private static final long DOUBLE_CLICK_TIME = 200;
    private Timeline singleClickTimer;
    private boolean isSceneChanging = false;

    private final EventHandler<KeyEvent> keyReleasedHandler;
    // singleton
    private static DefaultScene mSingleton = null;

    public static DefaultScene getSingleton() {
      assert (DefaultScene.mSingleton != null);
      return DefaultScene.mSingleton;
    }

    public static DefaultScene createSingleton(XScenario scenario) {
      assert (DefaultScene.mSingleton == null);
      DefaultScene.mSingleton = new DefaultScene(scenario);
      return DefaultScene.mSingleton;
    }

    public void dispatchGoToBookShelfPageButtonPress() {
      XCmdToChangeScene.execute(this.mScenario.getApp(), BookShelfScenario.BookShelfScene.getSingleton(), this);
    }

    public void dispatchGoToHomePageButtonPress() {
      XCmdToChangeScene.execute(this.mScenario.getApp(), HomeScenario.FirstScene.getSingleton(), this);
    }

    public void goToNotePage(JRIBookNoteInfo note) {
      JRIApp jri = (JRIApp) this.mScenario.getApp();
      jri.getSelectedBookAndNoteMgr().setSelectedBookNote(note);
      XCmdToChangeScene.execute(this.mScenario.getApp(), BookNotePageScenario.WritingScene.getSingleton(), this);
    }

    private DefaultScene(XScenario scenario) {
      super(scenario);
      keyReleasedHandler = this::handleKeyReleased;
    }

    @Override
    public void getReady() {
      JRIApp jri = (JRIApp) this.mScenario.getApp();
      Scene scene = jri.getPrimaryStage().getScene();

      scene.addEventFilter(KeyEvent.KEY_RELEASED, keyReleasedHandler);

      String currentPage = jri.getPageControllerMgr().getCurrentPageName();
      if (!BookDetailPageController.PAGE_CONTROLLER_NAME.equals((currentPage))) {
        new Thread(() -> {
          ArrayList<JRIBookNoteInfo> notes = ServerAPI.getBookNotes(
            jri.getSelectedBookAndNoteMgr().getSelectedBookCard().getBookItem().getItemId()
          );
          if (!notes.isEmpty()) {
            jri.getSelectedBookAndNoteMgr().setBookNotes(notes);
            for (JRIBookNoteInfo note : notes) {
              System.out.println("Loaded Note: " + note.getTitle());
            }
            Platform.runLater(() -> {
              BookDetailPageController controller = (BookDetailPageController) jri.getPageControllerMgr().getController(BookDetailPageController.PAGE_CONTROLLER_NAME);
              controller.populateNotes(notes);
            });
          } else {
            System.out.println("No notes found for this book.");
          }
        }).start();

        Platform.runLater(() -> {
          jri.getPageControllerMgr().switchTo(BookDetailPageController.PAGE_CONTROLLER_NAME);
        });
      }
    }

    @Override
    public void wrapUp() {
      JRIApp jri = (JRIApp) this.mScenario.getApp();
      Scene scene = jri.getPrimaryStage().getScene();
      scene.removeEventFilter(KeyEvent.KEY_RELEASED, keyReleasedHandler);
    }

    private void handleKeyReleased(KeyEvent e) {
      switch (e.getCode()) {
        case D:
          System.out.println("D key pressed");
          e.consume();
          createNewNote("during");
          break;
        case A:
          System.out.println("A key pressed");
          e.consume();
          createNewNote("after");
          break;
      }
    }

    private void createNewNote(String type) {
      JRIApp jri = (JRIApp) this.mScenario.getApp();
      JRIBookCard selectedBook = jri.getSelectedBookAndNoteMgr().getSelectedBookCard();

      if (selectedBook != null) {
        // 선택된 책의 ID로 새 노트 생성
        int bookId = Integer.parseInt(selectedBook.getBookItem().getItemId());
        int noteId = ServerAPI.createNote(bookId, type);

        if (noteId != -1) {
          System.out.println("Created new note with ID: " + noteId);
          // 노트 생성 성공 후 노트 씬으로 전환
          JRIBookNoteInfo newNote = new JRIBookNoteInfo(noteId, selectedBook.getBookItem().getItemId(), "Untitled", type, "");
          jri.getSelectedBookAndNoteMgr().setSelectedBookNote(newNote);
          XCmdToChangeScene.execute(this.mScenario.getApp(), BookNotePageScenario.WritingScene.getSingleton(), this);
        } else {
          System.err.println("Failed to create new note");
        }
      } else {
        System.err.println("No book selected");
      }
    }
  }
}
