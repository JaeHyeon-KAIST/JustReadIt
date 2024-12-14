package jri.justreadit.pageController;

import javafx.fxml.FXML;
import jri.justreadit.JRIApp;
import x.XPageController;

public class BookShelfPageController extends XPageController {
  public static final String PAGE_CONTROLLER_NAME = "BookShelfPageController";
  public static final String FXML_NAME = "BookShelfPage";

  public BookShelfPageController(JRIApp app, String fxmlBasePath) {
    super(PAGE_CONTROLLER_NAME, fxmlBasePath, FXML_NAME, app);
  }

  @FXML
  public void initialize() {
  }
}
