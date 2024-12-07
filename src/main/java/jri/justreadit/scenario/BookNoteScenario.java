package jri.justreadit.scenario;

import jri.justreadit.JRIScene;
import jri.justreadit.pageController.BookNotePageController;
import x.XApp;
import x.XCmdToChangeScene;
import x.XScenario;

public class BookNoteScenario extends XScenario {
  private int currentBookId;

  public void setCurrentBookId(int id) {
    this.currentBookId = id;
  }

  public int getCurrentBookId() {
    return this.currentBookId;
  }

  // singleton pattern
  private static BookNoteScenario mSingleton = null;

  public static BookNoteScenario getSingleton() {
    assert (BookNoteScenario.mSingleton != null);
    return mSingleton;
  }

  public static BookNoteScenario createSingleton(XApp app) {
    assert (BookNoteScenario.mSingleton == null);
    BookNoteScenario.mSingleton = new BookNoteScenario(app);
    return BookNoteScenario.mSingleton;
  }

  private BookNoteScenario(XApp app) {
    super(app);
  }

  @Override
  protected void addScenes() {
    this.addScene(ReadyScene.createSingleton(this));
  }

  public void dispatchReturnButtonPress() {
    if (this.getApp().getScenarioMgr().getCurScene() == ReadyScene.mSingleton) {
      ReadyScene.mSingleton.onReturnButtonPress();
    }
  }

  public static class ReadyScene extends JRIScene {
    //  singleton
    private static ReadyScene mSingleton = null;

    public static ReadyScene getSingleton() {
      assert (ReadyScene.mSingleton != null);
      return ReadyScene.mSingleton;
    }

    public static ReadyScene createSingleton(XScenario scenario) {
      assert (ReadyScene.mSingleton == null);
      ReadyScene.mSingleton = new ReadyScene(scenario);
      return ReadyScene.mSingleton;
    }

    public void onReturnButtonPress() {
      XCmdToChangeScene.execute(this.mScenario.getApp(), this.mReturnScene, null);
    }

    private ReadyScene(XScenario scenario) {
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
