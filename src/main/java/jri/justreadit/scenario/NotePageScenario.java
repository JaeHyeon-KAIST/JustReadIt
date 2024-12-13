package jri.justreadit.scenario;

import jri.justreadit.JRIScene;
import jri.justreadit.pageController.FirstPageController;
import jri.justreadit.pageController.NotePageController;
import x.XApp;
import x.XCmdToChangeScene;
import x.XScenario;

public class NotePageScenario extends XScenario {
  // singleton pattern
  private static NotePageScenario mSingleton = null;

  public static NotePageScenario getSingleton() {
    assert (NotePageScenario.mSingleton != null);
    return mSingleton;
  }

  public static NotePageScenario createSingleton(XApp app) {
    assert (NotePageScenario.mSingleton == null);
    NotePageScenario.mSingleton = new NotePageScenario(app);
    return NotePageScenario.mSingleton;
  }

  private NotePageScenario(XApp app) {
    super(app);
  }

  @Override
  protected void addScenes() {
    this.addScene(FirstScene.createSingleton(this));
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

    public void onMoveToSecondPageButtonPress() {
      XCmdToChangeScene.execute(this.mScenario.getApp(), BookShelfScenario.BookShelfScene.getSingleton(), this);
    }

    public void onMoveToBookNotePageButtonPress() {
      BookNoteScenario.getSingleton().setCurrentBookId(10);
      XCmdToChangeScene.execute(this.mScenario.getApp(), BookNoteScenario.ReadyScene.getSingleton(), this);
    }

    private FirstScene(XScenario scenario) {
      super(scenario);
    }

    @Override
    public void getReady() {
      this.mScenario.getApp().getPageControllerMgr().switchTo(NotePageController.PAGE_CONTROLLER_NAME);
    }

    @Override
    public void wrapUp() {

    }
  }
}
