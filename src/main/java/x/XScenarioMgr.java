package x;

import java.util.ArrayList;

public abstract class XScenarioMgr {
  // fields
  protected XApp mApp = null;
  protected ArrayList<XScenario> mScenarios = null;
  protected XScene mCurScene = null;

  public XScene getCurScene() {
    return this.mCurScene;
  }

  public void setCurScene(XScene scene) {
    if (this.mCurScene != null) {
      System.out.println("Calling wrapUp for: " + this.mCurScene.getClass().getSimpleName());
      this.mCurScene.wrapUp();
    }
    System.out.println("Calling getReady for: " + scene.getClass().getSimpleName());
    scene.getReady();
    this.mCurScene = scene;
  }

  // constructor
  protected XScenarioMgr(XApp app) {
    this.mApp = app;
    this.mScenarios = new ArrayList<XScenario>();
    this.addScenarios();
    this.setInitCurScene();
  }

  // abstract methods
  protected abstract void addScenarios();

  protected abstract void setInitCurScene();

  // concrete methods
  public void addScenario(XScenario scenario) {
    this.mScenarios.add(scenario);
  }
}
