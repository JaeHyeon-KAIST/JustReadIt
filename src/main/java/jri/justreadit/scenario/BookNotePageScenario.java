package jri.justreadit.scenario;

import jri.justreadit.JRIScene;
import jri.justreadit.pageController.BookNotePageController;
import x.XApp;
import x.XCmdToChangeScene;
import x.XScenario;

public class BookNotePageScenario extends XScenario {
  private int mCurrentBookId;

  public void setCurrentBookId(int bookId) {
    this.mCurrentBookId = bookId;
  }

  public int getCurrentBookId() {
    return this.mCurrentBookId;
  }

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
    this.addScene(NoteScene.createSingleton(this));
  }

  public void dispatchMoveToSecondPageButtonPress() {
    if (this.getApp().getScenarioMgr().getCurScene() == NoteScene.mSingleton) {
      NoteScene.mSingleton.onMoveToSecondPageButtonPress();
    }
  }

  public void dispatchMoveToBookNotePageButtonPress() {
    if (this.getApp().getScenarioMgr().getCurScene() == NoteScene.mSingleton) {
      NoteScene.mSingleton.onMoveToBookNotePageButtonPress();
    }
  }

  public void dispatchGoToBookShelfPageButtonPress() {
    if (this.getApp().getScenarioMgr().getCurScene() == NoteScene.mSingleton) {
      NoteScene.mSingleton.dispatchGoToBookShelfPageButtonPress();
    }
  }

  public static class NoteScene extends JRIScene {
    // singleton
    private static NoteScene mSingleton = null;

    public static NoteScene getSingleton() {
      assert (NoteScene.mSingleton != null);
      return NoteScene.mSingleton;
    }

    public static NoteScene createSingleton(XScenario scenario) {
      assert (NoteScene.mSingleton == null);
      NoteScene.mSingleton = new NoteScene(scenario);
      return NoteScene.mSingleton;
    }

    public void dispatchGoToBookShelfPageButtonPress() {
      XCmdToChangeScene.execute(this.mScenario.getApp(), BookShelfScenario.BookShelfScene.getSingleton(), this);
    }

    public void onMoveToSecondPageButtonPress() {
      XCmdToChangeScene.execute(this.mScenario.getApp(), BookShelfScenario.BookShelfScene.getSingleton(), this);
    }

    public void onMoveToBookNotePageButtonPress() {
//      BookNoteScenario.getSingleton().setCurrentBookId(10);
//      XCmdToChangeScene.execute(this.mScenario.getApp(), BookNoteScenario.ReadyScene.getSingleton(), this);
    }

    private NoteScene(XScenario scenario) {
      super(scenario);
    }

    @Override
    public void getReady() {
      this.mScenario.getApp().getPageControllerMgr().switchTo(BookNotePageController.PAGE_CONTROLLER_NAME);
    }

    @Override
    public void wrapUp() {

    }
  }
}