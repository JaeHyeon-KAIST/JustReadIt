package jri.justreadit.scenario;

import jri.justreadit.JRIScene;
import x.XApp;
import x.XScenario;

public class EmptyScenario extends XScenario {
  // singleton pattern
  private static EmptyScenario mSingleton = null;

  public static EmptyScenario getSingleton() {
    assert (EmptyScenario.mSingleton != null);
    return mSingleton;
  }

  public static EmptyScenario createSingleton(XApp app) {
    assert (EmptyScenario.mSingleton == null);
    EmptyScenario.mSingleton = new EmptyScenario(app);
    return EmptyScenario.mSingleton;
  }

  private EmptyScenario(XApp app) {
    super(app);
  }

  @Override
  protected void addScenes() {
    this.addScene(ReadyScene.createSingleton(this));
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

    private ReadyScene(XScenario scenario) {
      super(scenario);
    }

    @Override
    public void getReady() {
//      this.mScenario.getApp().getPageControllerMgr().switchTo(PageController.PAGE_CONTROLLER_NAME);
    }

    @Override
    public void wrapUp() {
    }
  }
}
