package jri.justreadit.scenario;

import jri.justreadit.JRIScene;
import jri.justreadit.pageController.BookShelfPageController;
import x.XApp;
import x.XCmdToChangeScene;
import x.XScenario;

public class BookShelfScenario extends XScenario {
  // singleton pattern
  private static BookShelfScenario mSingleton = null;

  public static BookShelfScenario getSingleton() {
    assert (BookShelfScenario.mSingleton != null);
    return mSingleton;
  }

  public static BookShelfScenario createSingleton(XApp app) {
    assert (BookShelfScenario.mSingleton == null);
    BookShelfScenario.mSingleton = new BookShelfScenario(app);
    return BookShelfScenario.mSingleton;
  }

  private BookShelfScenario(XApp app) {
    super(app);
  }

  @Override
  protected void addScenes() {
    this.addScene(BookShelfScene.createSingleton(this));
  }

//  public void dispatchGoToBookDetailPageButtonPress() {
//    if (this.getApp().getScenarioMgr().getCurScene() == BookShelfScene.mSingleton) {
//      BookShelfScene.mSingleton.dispatchGoToBookDetailPageButtonPress();
//    }
//  }

  public void dispatchGoToHomePageButtonPress() {
    if (this.getApp().getScenarioMgr().getCurScene() == BookShelfScenario.BookShelfScene.mSingleton) {
      BookShelfScenario.BookShelfScene.mSingleton.onMoveToHomePageButtonPress();
    }
  }

  public static class BookShelfScene extends JRIScene {
    // singleton
    private static BookShelfScene mSingleton = null;

    public static BookShelfScene getSingleton() {
      assert (BookShelfScene.mSingleton != null);
      return BookShelfScene.mSingleton;
    }

    public static BookShelfScene createSingleton(XScenario scenario) {
      assert (BookShelfScene.mSingleton == null);
      BookShelfScene.mSingleton = new BookShelfScene(scenario);
      return BookShelfScene.mSingleton;
    }

//    public void dispatchGoToBookDetailPageButtonPress() {
//      XCmdToChangeScene.execute(this.mScenario.getApp(), BookDetailScenario.BookDetailScene.getSingleton(), this.mReturnScene);
//    }

    public void onMoveToHomePageButtonPress() {
      XCmdToChangeScene.execute(this.mScenario.getApp(), FirstScenario.FirstScene.getSingleton(), this.mReturnScene);
    }

    private BookShelfScene(XScenario scenario) {
      super(scenario);
    }

    @Override
    public void getReady() {
      this.mScenario.getApp().getPageControllerMgr().switchTo(BookShelfPageController.PAGE_CONTROLLER_NAME);
    }

    @Override
    public void wrapUp() {

    }
  }
}
