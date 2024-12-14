package jri.justreadit;

import jri.justreadit.pageController.*;
import x.XPageControllerMgr;

public class JRIPageControllerMgr extends XPageControllerMgr {
  public static final String BASE_FXML_PATH = "/jri/justreadit/pageController/";

  public JRIPageControllerMgr(JRIApp app) {
    super(app);
  }

  @Override
  protected void initializeControllers() {
    this.addController(new FirstPageController((JRIApp) this.mApp, BASE_FXML_PATH));
    this.addController(new BookShelfPageController((JRIApp) this.mApp, BASE_FXML_PATH));
    this.addController(new BookDetailPageController((JRIApp) this.mApp, BASE_FXML_PATH));
    this.addController(new BookNotePageController((JRIApp) this.mApp, BASE_FXML_PATH));
  }
}
