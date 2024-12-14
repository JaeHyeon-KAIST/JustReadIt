package jri.justreadit.scenario;

import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import jri.justreadit.JRIApp;
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

    private DefaultScene(XScenario scenario) {
      super(scenario);
      keyReleasedHandler = this::handleKeyReleased;
    }

    @Override
    public void getReady() {
      JRIApp jri = (JRIApp) this.mScenario.getApp();
      Scene scene = jri.getPrimaryStage().getScene();

      scene.addEventFilter(KeyEvent.KEY_RELEASED, keyReleasedHandler);

      this.mScenario.getApp().getPageControllerMgr().switchTo(BookDetailPageController.PAGE_CONTROLLER_NAME);
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
          XCmdToChangeScene.execute(this.mScenario.getApp(), BookNotePageScenario.NoteScene.getSingleton(), this);
          break;
        case A:
          System.out.println("A key pressed");
          e.consume();
          break;
      }
    }
  }
}
