package com.jaehyeon.jri.justreadit;

import com.jaehyeon.jri.justreadit.pageController.BookNotePageController;
import com.jaehyeon.jri.justreadit.pageController.FirstPageController;
import com.jaehyeon.jri.justreadit.pageController.SecondPageController;
import x.XPageControllerMgr;

public class JRIPageControllerMgr extends XPageControllerMgr {
  public static final String BASE_FXML_PATH = "/com/jaehyeon/jri/justreadit/pageController/";

  public JRIPageControllerMgr(JRIApp app) {
    super(app);
  }

  @Override
  protected void initializeControllers() {
    this.addController(new FirstPageController((JRIApp) this.mApp, BASE_FXML_PATH));
    this.addController(new SecondPageController((JRIApp) this.mApp, BASE_FXML_PATH));
    this.addController(new BookNotePageController((JRIApp) this.mApp, BASE_FXML_PATH));
  }
}
