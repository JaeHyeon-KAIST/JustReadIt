package jri.justreadit.scenario;

import jri.justreadit.JRIBookCard;
import jri.justreadit.JRIScene;
import jri.justreadit.pageController.BookDetailPageController;
import x.XApp;
import x.XCmdToChangeScene;
import x.XScenario;

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
    this.addScene(BookDetailScene.createSingleton(this));
  }

  public void dispatchGoToBookShelfPageButtonPress() {
    if (this.getApp().getScenarioMgr().getCurScene() == BookDetailScene.mSingleton) {
      BookDetailScene.mSingleton.dispatchGoToBookShelfPageButtonPress();
    }
  }

  public void dispatchGoToHomePageButtonPress() {
    if (this.getApp().getScenarioMgr().getCurScene() == BookDetailScene.mSingleton) {
      BookDetailScene.mSingleton.dispatchGoToHomePageButtonPress();
    }
  }

  public static class BookDetailScene extends JRIScene {
    // singleton
    private static BookDetailScene mSingleton = null;

    public static BookDetailScene getSingleton() {
      assert (BookDetailScene.mSingleton != null);
      return BookDetailScene.mSingleton;
    }

    public static BookDetailScene createSingleton(XScenario scenario) {
      assert (BookDetailScene.mSingleton == null);
      BookDetailScene.mSingleton = new BookDetailScene(scenario);
      return BookDetailScene.mSingleton;
    }

    public void dispatchGoToBookShelfPageButtonPress() {
      XCmdToChangeScene.execute(this.mScenario.getApp(), BookShelfScenario.BookShelfScene.getSingleton(), this);
    }

    public void dispatchGoToHomePageButtonPress() {
      XCmdToChangeScene.execute(this.mScenario.getApp(), HomeScenario.FirstScene.getSingleton(), this);
    }

    private BookDetailScene(XScenario scenario) {
      super(scenario);
    }

    @Override
    public void getReady() {
      this.mScenario.getApp().getPageControllerMgr().switchTo(BookDetailPageController.PAGE_CONTROLLER_NAME);
    }

    @Override
    public void wrapUp() {

    }
  }
}
