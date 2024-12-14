package jri.justreadit.pageController;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.input.Clipboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import jri.justreadit.JRIApp;
import jri.justreadit.scenario.BookNotePageScenario;
import netscape.javascript.JSObject;
import x.XPageController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookNotePageController extends XPageController {
  public static final String PAGE_CONTROLLER_NAME = "NotePageController";
  public static final String FXML_NAME = "BookNotePage";

  @FXML
  private AnchorPane SIDE_NOTE;

  @FXML
  private HTMLEditor htmlEditor;

  public BookNotePageController(JRIApp app, String fxmlBasePath) {
    super(PAGE_CONTROLLER_NAME, fxmlBasePath, FXML_NAME, app);
  }

  @FXML
  public void initialize() {
    int bookId = BookNotePageScenario.getSingleton().getCurrentBookId();
    System.out.println("Book ID: " + bookId);

    WebView webView = (WebView) htmlEditor.lookup(".web-view");
    if (webView != null) {
      WebEngine engine = webView.getEngine();

      engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
        if (newState == Worker.State.SUCCEEDED) {
          Platform.runLater(() -> {
            try {
              // 브릿지 설정
              JSObject window = (JSObject) engine.executeScript("window");
              window.setMember("javaLogger", new BookNotePageController.JavaLogger());

              engine.executeScript(
                // 먼저 console.log를 Java로 리다이렉션
                "console.log = function(message) { window.javaLogger.log(message); };" +

                  // 나머지 스크립트
                  "window.javaLogger.log('Script initialization started...');" +

                  "document.head.innerHTML += '<style>" +
                  "@import url(http://fonts.googleapis.com/earlyaccess/notosanskr.css);" +  // Noto Sans Korean 폰트 추가
                  "* { font-family: 'Noto Sans KR', 'Malgun Gothic', '맑은 고딕', sans-serif !important; }" +
                  "body { font-size: 14px; line-height: 1.6; }" +
                  "</style>';" +
                  "document.body.setAttribute('lang', 'ko');" +

                  // IME 이벤트 처리
                  "document.body.addEventListener('keydown', function(e) {" +
                  "    window.javaLogger.log('KeyDown - key: ' + e.key + ', isComposing: ' + e.isComposing);" +
                  "    if (e.isComposing || e.keyCode === 229) {" +
                  "        window.javaLogger.log('IME composition detected');" +
                  "        e.preventDefault();" +
                  "        return;" +
                  "    }" +
                  "});" +

                  // IME 조합 이벤트
                  "document.body.addEventListener('compositionstart', function(e) {" +
                  "    window.javaLogger.log('CompositionStart: ' + e.data);" +
                  "});" +

                  "document.body.addEventListener('compositionupdate', function(e) {" +
                  "    window.javaLogger.log('CompositionUpdate: ' + e.data);" +
                  "});" +

                  "document.body.addEventListener('compositionend', function(e) {" +
                  "    window.javaLogger.log('CompositionEnd: ' + e.data);" +
                  "});" +

                  "window.javaLogger.log('Script initialization completed.');"
              );
            } catch (Exception e) {
              System.err.println("JavaScript bridge error: " + e.getMessage());
              e.printStackTrace();
            }
          });
        }
      });
    }

    // 링크 클릭 이벤트 감지
    setupLinkClickListener();

    // 붙여넣기 이벤트 감지
    setupPasteEventListener();

    SIDE_NOTE.setTranslateX(800);
  }

  public void goToBookShelfPage() {
    System.out.println("Go to BookShelfPage button pressed");
    // Scenario와 Scene을 통한 동작 위임
    BookNotePageScenario scenario = (BookNotePageScenario) this.mApp.getScenarioMgr().getCurScene().getScenario();
    scenario.dispatchGoToBookShelfPageButtonPress();
  }

  public void goToHomePage() {
    System.out.println("Go to BookShelfPage button pressed");
    // Scenario와 Scene을 통한 동작 위임
    BookNotePageScenario scenario = (BookNotePageScenario) this.mApp.getScenarioMgr().getCurScene().getScenario();
    scenario.dispatchMoveToHomePageButtonPress();
  }

  public static class JavaLogger {
    public void log(String message) {
      System.out.println("[WebView] " + message);
    }
  }

  private void setupLinkClickListener() {
    // HTMLEditor 내부 WebView 가져오기
    WebView webView = (WebView) htmlEditor.lookup(".web-view");
    if (webView != null) {
      WebEngine webEngine = webView.getEngine();

      webView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
        Platform.runLater(() -> {
          webEngine.getLoadWorker().cancel();
          System.out.println("Link clicked: " + webEngine.getLocation());
        });
        event.consume(); // 이벤트 소비하여 기본 동작 방지
      });
    }
  }

  private void setupPasteEventListener() {
    htmlEditor.setOnKeyReleased(event -> {
      // Ctrl+V 또는 Command+V (붙여넣기 단축키)
      if (event.isControlDown() && event.getCode().getName().equalsIgnoreCase("V") ||
        event.isMetaDown() && event.getCode().getName().equalsIgnoreCase("V")) {
        handlePaste();
      }
    });
  }

  private void handlePaste() {
    // 현재 HTML 가져오기
    String currentHtml = htmlEditor.getHtmlText();

    Clipboard clipboard = Clipboard.getSystemClipboard();
    // 이미지가 있는 경우에만 처리
    if (clipboard.hasImage()) {
      try {
        Image image = clipboard.getImage();
        if (image != null) {
          String base64Image = imageToBase64(image);

          WebView webView = (WebView) htmlEditor.lookup(".web-view");
          if (webView != null) {
            WebEngine engine = webView.getEngine();

            // 현재 HTML 내용을 JavaScript를 통해 가져오기
            String script =
              "var selection = window.getSelection();" +
                "var range = selection.getRangeAt(0);" +
                "var img = document.createElement('img');" +
                "img.src = 'data:image/png;base64," + base64Image + "';" +
                "img.style.maxWidth = '100%';" +
                "range.insertNode(img);" +
                "range.collapse(false);";

            engine.executeScript(script);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      // URL 감지 정규식 (파일 확장자 포함, 이미 <a> 태그로 감싸지지 않은 것만 대상)
      Pattern urlPattern = Pattern.compile(
        "(?<!<a href=\")https?://[\\w.-]+(:\\d+)?(/[\\w~:%+#?=&/\\-]*)?(\\.[a-zA-Z]{2,4})?",
        Pattern.CASE_INSENSITIVE);

      Matcher matcher = urlPattern.matcher(currentHtml);
      StringBuffer processedHtml = new StringBuffer();

      // URL을 HTML 링크로 변환
      while (matcher.find()) {
        String url = matcher.group();
        // 링크로 변환
        String linkHtml = "<a href=\"" + escapeHtml(url) + "\">" + escapeHtml(url) + "</a>";
        matcher.appendReplacement(processedHtml, linkHtml);
      }
      matcher.appendTail(processedHtml);

      // 변환된 HTML을 HTMLEditor에 다시 설정
      htmlEditor.setHtmlText(processedHtml.toString());
    }

    // 포커스 처리 개선
    javafx.application.Platform.runLater(() -> {
      // HTMLEditor에만 포커스 설정
      htmlEditor.requestFocus();

      // 커서를 텍스트 끝으로 이동
      WebView webView = (WebView) htmlEditor.lookup(".web-view");
      if (webView != null) {
        WebEngine engine = webView.getEngine();
        engine.executeScript(
          "document.designMode = 'on';" +
            "var range = document.createRange();" +
            "range.selectNodeContents(document.body);" +
            "range.collapse(false);" +
            "var selection = window.getSelection();" +
            "selection.removeAllRanges();" +
            "selection.addRange(range);"
        );
      }
    });
  }

  private String imageToBase64(Image image) {
    try {
      int width = (int) image.getWidth();
      int height = (int) image.getHeight();

      // JavaFX WritableImage를 BufferedImage로 변환
      BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

      PixelReader pixelReader = image.getPixelReader();
      for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          bufferedImage.setRGB(x, y, pixelReader.getArgb(x, y));
        }
      }

      // PNG 형식으로 인코딩
      ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
      ImageIO.write(bufferedImage, "png", byteOutput);

      return Base64.getEncoder().encodeToString(byteOutput.toByteArray());
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
  }

  // HTML 특수 문자 escaping (URL에서 안전한 HTML로 변환)
  private String escapeHtml(String text) {
    if (text == null) return null;
    return text.replace("&", "&amp;")
      .replace("<", "&lt;")
      .replace(">", "&gt;")
      .replace("\"", "&quot;")
      .replace("'", "&#39;");
  }

  public void showSlide(MouseEvent mouseEvent) {
    // SIDE_NOTE 슬라이드 애니메이션
    TranslateTransition sideNoteSlide = new TranslateTransition();
    sideNoteSlide.setDuration(Duration.seconds(0.5));
    sideNoteSlide.setNode(SIDE_NOTE);
    sideNoteSlide.setToX(40); // 열릴 위치
    sideNoteSlide.play();

    // HTMLEditor 크기 줄이기 애니메이션
    Timeline editorResize = new Timeline();
    KeyValue widthKey = new KeyValue(htmlEditor.prefWidthProperty(), 1000); // 목표 넓이
    KeyValue heightKey = new KeyValue(htmlEditor.prefHeightProperty(), 750); // 목표 높이
    KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), widthKey, heightKey);
    editorResize.getKeyFrames().add(keyFrame);
    editorResize.play();
  }

  public void hideSlide(MouseEvent mouseEvent) {
    // SIDE_NOTE 슬라이드 애니메이션
    TranslateTransition sideNoteSlide = new TranslateTransition();
    sideNoteSlide.setDuration(Duration.seconds(0.5));
    sideNoteSlide.setNode(SIDE_NOTE);
    sideNoteSlide.setToX(800); // 닫힌 위치
    sideNoteSlide.play();

    // HTMLEditor 크기 복원 애니메이션
    Timeline editorResize = new Timeline();
    KeyValue widthKey = new KeyValue(htmlEditor.prefWidthProperty(), 1400); // 원래 넓이
    KeyValue heightKey = new KeyValue(htmlEditor.prefHeightProperty(), 750); // 원래 높이
    KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), widthKey, heightKey);
    editorResize.getKeyFrames().add(keyFrame);
    editorResize.play();
  }
}
