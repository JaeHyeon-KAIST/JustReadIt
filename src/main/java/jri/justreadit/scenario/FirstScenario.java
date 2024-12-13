package jri.justreadit.scenario;

import jri.justreadit.JRIScene;
import jri.justreadit.pageController.FirstPageController;
import x.XApp;
import x.XCmdToChangeScene;
import x.XScenario;

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
      XCmdToChangeScene.execute(this.mScenario.getApp(), NotePageScenario.NoteScene.getSingleton(), this);
    }

    private FirstScene(XScenario scenario) {
      super(scenario);
    }

    @Override
    public void getReady() {
      this.mScenario.getApp().getPageControllerMgr().switchTo(FirstPageController.PAGE_CONTROLLER_NAME);
    }

    @Override
    public void wrapUp() {

    }
  }
}
