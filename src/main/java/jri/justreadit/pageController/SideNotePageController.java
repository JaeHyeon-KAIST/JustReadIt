package jri.justreadit.pageController;

import jri.justreadit.JRIApp;
import x.XPageController;

public class SideNotePageController extends XPageController {
  public static final String PAGE_CONTROLLER_NAME = "NotePageController";
  public static final String FXML_NAME = "NotePage";

  public SideNotePageController(JRIApp app, String fxmlBasePath) {
    super(PAGE_CONTROLLER_NAME, fxmlBasePath, FXML_NAME, app);
  }
  
}
