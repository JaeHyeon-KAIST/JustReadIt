module com.jaehyeon.jri.justreadit {
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.web;
  requires java.desktop;
  requires jdk.jsobject;

  exports jri.justreadit;
  exports jri.justreadit.scenario;
  exports jri.justreadit.pageController to javafx.fxml;
  exports x;

  // javafx.web도 추가
  opens jri.justreadit.pageController to javafx.fxml, javafx.web;
}
