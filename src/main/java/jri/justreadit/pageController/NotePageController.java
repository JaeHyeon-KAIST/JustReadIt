package jri.justreadit.pageController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import jri.justreadit.JRIApp;
import jri.justreadit.scenario.FirstScenario;
import x.XPageController;

public class NotePageController extends XPageController {
  public static final String PAGE_CONTROLLER_NAME = "NotePageController";
  public static final String FXML_NAME = "NotePage";

  public NotePageController(JRIApp app, String fxmlBasePath) {
    super(PAGE_CONTROLLER_NAME, fxmlBasePath, FXML_NAME, app);
  }

}
