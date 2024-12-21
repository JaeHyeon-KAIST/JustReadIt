package jri.justreadit.scenario;

import javafx.application.Platform;
import jri.justreadit.JRIApp;
import jri.justreadit.types.JRIBookCard;
import jri.justreadit.JRIScene;
import jri.justreadit.pageController.BookShelfPageController;
import jri.justreadit.utils.ServerAPI;
import x.XApp;
import x.XCmdToChangeScene;
import x.XScenario;

import java.util.List;
import java.util.Map;

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

  public void dispatchGoToHomePageButtonPress() {
    if (this.getApp().getScenarioMgr().getCurScene() == BookShelfScenario.BookShelfScene.mSingleton) {
      BookShelfScenario.BookShelfScene.mSingleton.onMoveToHomePageButtonPress();
    }
  }

  public void dispatchGoToBookDetailPage(JRIBookCard clickedBook) {
    if (this.getApp().getScenarioMgr().getCurScene() == BookShelfScenario.BookShelfScene.mSingleton) {
      BookShelfScene.mSingleton.goToBookDetailPage(clickedBook);
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

    public void onMoveToHomePageButtonPress() {
      XCmdToChangeScene.execute(this.mScenario.getApp(), HomeScenario.ReadyScene.getSingleton(), this.mReturnScene);
    }

    private BookShelfScene(XScenario scenario) {
      super(scenario);
    }

    public void goToBookDetailPage(JRIBookCard clickedBook) {
      JRIApp jri = (JRIApp) this.mScenario.getApp();
      jri.getSelectedBookAndNoteMgr().setSelectedBookCard(clickedBook);
      XCmdToChangeScene.execute(jri, BookDetailScenario.DefaultScene.getSingleton(), this);
    }

    @Override
    public void getReady() {
      new Thread(() -> {
        List<Map<String, Object>> bookList = ServerAPI.getBookList();
        System.out.println("BookShelfScene getReady() bookList: " + bookList);
        if (bookList != null) {
          Platform.runLater(() -> {
            JRIApp jri = (JRIApp) this.mScenario.getApp();
            BookShelfPageController controller = (BookShelfPageController) jri.getPageControllerMgr().getController((BookShelfPageController.PAGE_CONTROLLER_NAME));
            controller.setBookList(bookList);
          });
        }
      }).start();

      this.mScenario.getApp().getPageControllerMgr().switchTo(BookShelfPageController.PAGE_CONTROLLER_NAME);
    }

    @Override
    public void wrapUp() {

    }
  }
}
