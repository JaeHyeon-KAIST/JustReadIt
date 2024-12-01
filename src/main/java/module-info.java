module com.jaehyeon.jri.justreadit {
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.web;
  requires java.desktop;

  exports com.jaehyeon.jri.justreadit;
  exports com.jaehyeon.jri.justreadit.scenario;
  exports com.jaehyeon.jri.justreadit.pageController to javafx.fxml;
  exports x;

  opens com.jaehyeon.jri.justreadit.pageController to javafx.fxml;
}
