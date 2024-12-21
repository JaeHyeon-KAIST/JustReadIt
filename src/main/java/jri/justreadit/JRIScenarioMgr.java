package jri.justreadit;

import jri.justreadit.scenario.*;
import x.XScenarioMgr;

public class JRIScenarioMgr extends XScenarioMgr {
  public JRIScenarioMgr(JRIApp jri) {
    super(jri);
  }

  @Override
  protected void addScenarios() {
    this.addScenario(HomeScenario.createSingleton(this.mApp));
    this.addScenario(BookShelfScenario.createSingleton(this.mApp));
    this.addScenario(BookDetailScenario.createSingleton(this.mApp));
    this.addScenario(BookNoteScenario.createSingleton(this.mApp));
  }

  @Override
  protected void setInitCurScene() {
    this.setCurScene(HomeScenario.ReadyScene.getSingleton());
  }
}
