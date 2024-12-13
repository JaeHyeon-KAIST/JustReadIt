package x;

import java.util.HashMap;

public abstract class XPageControllerMgr {
  protected XApp mApp;
  private final HashMap<String, XPageController> controllers;
  private String currentPageName;

  public XPageControllerMgr(XApp app) {
    this.mApp = app;
    this.controllers = new HashMap<>();
    this.currentPageName = null;
    this.initializeControllers();
  }

  // 각 서브클래스에서 컨트롤러를 초기화
  protected abstract void initializeControllers();

  // 컨트롤러 추가
  protected void addController(XPageController controller) {
    if (controller == null || controller.getName() == null) {
      throw new IllegalArgumentException("Controller or Controller name cannot be null");
    }
    controllers.put(controller.getName(), controller);
  }

  // 컨트롤러 가져오기
  public XPageController getController(String name) {
    return controllers.get(name);
  }

  // 컨트롤러 이름으로 화면 전환
  public void switchTo(String name) {
    XPageController controller = this.getController(name);
    if (controller == null) {
      throw new IllegalArgumentException("Controller not found: " + name);
    }
    this.currentPageName = name;
    controller.show();
  }

  public String getCurrentPageName() {
    return this.currentPageName;
  }
}
