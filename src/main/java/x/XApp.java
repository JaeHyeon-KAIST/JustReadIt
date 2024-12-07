package x;

import javafx.application.Application;
import javafx.stage.Stage;

public abstract class XApp extends Application {
  public abstract XPageControllerMgr getPageControllerMgr();

  public abstract XScenarioMgr getScenarioMgr();

  public abstract XLogMgr getLogMgr();

  public abstract Stage getPrimaryStage();

  @Override
  public void init() throws Exception {
    super.init();
  }
}
