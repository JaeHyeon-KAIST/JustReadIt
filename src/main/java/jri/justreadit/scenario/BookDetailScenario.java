package jri.justreadit.scenario;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import jri.justreadit.JRIApp;
import jri.justreadit.types.JRIBookCard;
import jri.justreadit.types.JRIBookNoteInfo;
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
    this.addScene(EditNoteScene.createSingleton(this));
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

  public void dispatchGoBackButtonPressed() {
    if (this.getApp().getScenarioMgr().getCurScene() == DefaultScene.mSingleton) {
      DefaultScene.mSingleton.goBack();
    }
  }

  public void dispatchEditNoteTitle(String title) {
    EditNoteScene.mSingleton.editNoteTitle(title);
  }

  public void dispatchDeleteNote() {
    EditNoteScene.mSingleton.deleteNote();
  }

  public void dispatchCloseModal() {
    EditNoteScene.mSingleton.closeModal();
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
      XCmdToChangeScene.execute(this.mScenario.getApp(), HomeScenario.ReadyScene.getSingleton(), this);
    }

    public void goToNotePage(JRIBookNoteInfo note) {
      long currentTime = System.currentTimeMillis();

      // 더블 클릭 판단
      if (currentTime - lastClickTime < DOUBLE_CLICK_TIME) {
        if (singleClickTimer != null) {
          singleClickTimer.stop(); // 중복 타이머 정지
        }

        // 더블 클릭 이벤트
        isSceneChanging = true;
        System.out.println("Double Clicked on Note: " + note.getTitle());
        JRIApp jri = (JRIApp) this.mScenario.getApp();
        jri.getSelectedBookAndNoteMgr().setSelectedBookNote(note);
        XCmdToChangeScene.execute(this.mScenario.getApp(), BookNoteScenario.WritingScene.getSingleton(), this);
        lastClickTime = 0;
      } else {
        // 단일 클릭 타이머 설정
        lastClickTime = currentTime;

        if (singleClickTimer != null) {
          singleClickTimer.stop(); // 기존 타이머 중복 실행 방지
        }

        singleClickTimer = new Timeline(new KeyFrame(Duration.millis(DOUBLE_CLICK_TIME), event -> {
          System.out.println("Single Clicked on Note: " + note.getTitle());
          if (!isSceneChanging && System.currentTimeMillis() - lastClickTime >= DOUBLE_CLICK_TIME) {
            isSceneChanging = true;  // 씬 전환 시작

            JRIApp jri = (JRIApp) this.mScenario.getApp();
            BookDetailPageController controller = (BookDetailPageController)
              jri.getPageControllerMgr().getController(BookDetailPageController.PAGE_CONTROLLER_NAME);

            // 모달 창 열기
            Platform.runLater(() -> {
              controller.openModal();
              isSceneChanging = false; // 리셋
            });

            jri.getSelectedBookAndNoteMgr().setEditingNoteId(note.getNoteId());

            XCmdToChangeScene.execute(this.mScenario.getApp(), EditNoteScene.getSingleton(), this.mReturnScene);
          }
        }));
        singleClickTimer.setCycleCount(1);
        singleClickTimer.play();
      }
    }

    public void goBack() {
      XCmdToChangeScene.execute(this.mScenario.getApp(), this.mReturnScene, this);
    }

    private DefaultScene(XScenario scenario) {
      super(scenario);
      keyReleasedHandler = this::handleKeyReleased;
    }

    @Override
    public void getReady() {
      JRIApp jri = (JRIApp) this.mScenario.getApp();
      Scene scene = jri.getPrimaryStage().getScene();

      this.isSceneChanging = false;
      this.lastClickTime = 0;

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

        new Thread(() -> {
          ArrayList<JRIBookCard> books = ServerAPI.getConnectedBook(jri.getSelectedBookAndNoteMgr().getSelectedBookCard().getBookItem().getItemId());
          if (!books.isEmpty()) {
            Platform.runLater(() -> {
              BookDetailPageController controller = (BookDetailPageController) jri.getPageControllerMgr().getController(BookDetailPageController.PAGE_CONTROLLER_NAME);
              controller.populateConnectedBooks(books);
            });
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
          XCmdToChangeScene.execute(this.mScenario.getApp(), BookNoteScenario.WritingScene.getSingleton(), this);
        } else {
          System.err.println("Failed to create new note");
        }
      } else {
        System.err.println("No book selected");
      }
    }
  }

  public static class EditNoteScene extends JRIScene {
    // singleton
    private static EditNoteScene mSingleton = null;

    public static EditNoteScene getSingleton() {
      assert (EditNoteScene.mSingleton != null);
      return EditNoteScene.mSingleton;
    }

    public static EditNoteScene createSingleton(XScenario scenario) {
      assert (EditNoteScene.mSingleton == null);
      EditNoteScene.mSingleton = new EditNoteScene(scenario);
      return EditNoteScene.mSingleton;
    }

    public void closeModal() {
      JRIApp jri = (JRIApp) this.mScenario.getApp();
      XCmdToChangeScene.execute(jri, DefaultScene.getSingleton(), this.mReturnScene);
    }

    public void editNoteTitle(String title) {
      JRIApp jri = (JRIApp) this.mScenario.getApp();
      int noteId = jri.getSelectedBookAndNoteMgr().getEditingNoteId();

      boolean success = ServerAPI.editNoteTitle(title, noteId);

      if (success) {
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
      }

      XCmdToChangeScene.execute(jri, DefaultScene.getSingleton(), this.mReturnScene);
    }

    public void deleteNote() {
      JRIApp jri = (JRIApp) this.mScenario.getApp();
      int noteId = jri.getSelectedBookAndNoteMgr().getEditingNoteId();

      boolean success = ServerAPI.deleteNote(noteId);

      if (success) {
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
      }

      XCmdToChangeScene.execute(jri, DefaultScene.getSingleton(), this.mReturnScene);
    }

    private EditNoteScene(XScenario scenario) {
      super(scenario);
    }

    @Override
    public void getReady() {
    }

    @Override
    public void wrapUp() {
    }
  }
}
