module com.jaehyeon.jri.justreadit {
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.swing;
  requires javafx.web;
  requires jdk.jsobject;
  requires com.fasterxml.jackson.databind;

  exports jri.justreadit;
  exports jri.justreadit.scenario;
  exports jri.justreadit.pageController to javafx.fxml;
  exports x;

  // javafx.web도 추가
  opens jri.justreadit.pageController to javafx.fxml, javafx.web;
}
