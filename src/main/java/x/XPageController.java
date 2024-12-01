package x;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public abstract class XPageController {
  private final String name;
  protected final XApp mApp;
  private final String fxmlPath;

  public XPageController(String name, String fxmlBasePath, String fxmlName, XApp app) {
    this.name = name;
    this.fxmlPath = fxmlBasePath + fxmlName + ".fxml";
    this.mApp = app;
  }

  public String getName() {
    return name;
  }

  // FXML 경로 반환
  public String getFXMLPath() {
    return fxmlPath;
  }

  public void show() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(getFXMLPath()));

      // 컨트롤러 팩토리 설정
      loader.setControllerFactory(param -> {
        if (param.isInstance(this)) {
          return this; // 현재 컨트롤러 인스턴스를 사용
        }
        throw new IllegalStateException("Unexpected controller class: " + param);
      });

      Parent root = loader.load();

      // RootPane 교체
      StackPane rootPane = ((StackPane) mApp.getPrimaryStage().getScene().getRoot());
      rootPane.getChildren().clear();
      rootPane.getChildren().add(root);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
