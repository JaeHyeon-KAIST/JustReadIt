package jri.justreadit.pageController;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.Clipboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import jri.justreadit.*;
import jri.justreadit.scenario.BookNotePageScenario;
import jri.justreadit.utils.AladdinOpenAPI.AladdinBookItem;
import jri.justreadit.utils.ServerAPI;
import netscape.javascript.JSObject;
import x.XPageController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookNotePageController extends XPageController {
  public static final String PAGE_CONTROLLER_NAME = "NotePageController";
  public static final String FXML_NAME = "BookNotePage";

  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private final StringBuilder pendingNoteData = new StringBuilder();
  private ScheduledFuture<?> debounceTask;

  @FXML
  private AnchorPane SIDE_NOTE;

  @FXML
  private HTMLEditor htmlEditor;

  @FXML
  private Text noteTitleText;

  @FXML
  private Text bookTitleText;

  @FXML
  private Text bookAuthorText;

  @FXML
  private ImageView bookCoverImageView;

  @FXML
  private StackPane noteSearchModalOverlay; // 모달 오버레이

  @FXML
  private TextField noteSearchModalInputField; // 입력창

  @FXML
  private StackPane bookSearchModalOverlay; // 모달 오버레이

  @FXML
  private TextField bookSearchModalInputField; // 입력창

  @FXML
  private ImageView sideNoteBookCoverImageView;

  @FXML
  private Text sideNoteNoteTitleText;

  @FXML
  private Text sideNoteBooktTitleTet;

  @FXML
  private Text sideNoteAuthorText;

  @FXML
  private WebView readOnlyWebView;

  @FXML
  private ListView<JRIVectorResultInfo> noteSearchResultsList;

  @FXML
  private ListView<JRIBookCard> bookSearchResultsList;

  private ObservableList<JRIVectorResultInfo> noteSearchResultsObservable = FXCollections.observableArrayList();
  private ObservableList<JRIBookCard> bookSearchResultsObservable = FXCollections.observableArrayList();

  public BookNotePageController(JRIApp app, String fxmlBasePath) {
    super(PAGE_CONTROLLER_NAME, fxmlBasePath, FXML_NAME, app);
  }

  private boolean isContentSet = false;

  @FXML
  public void initialize() {
    isContentSet = false;
    JRIApp jri = (JRIApp) this.mApp;
    AladdinBookItem bookItem = jri.getSelectedBookAndNoteMgr().getSelectedBookCard().getBookItem();
    bookTitleText.setText(bookItem.getTitle());
    bookAuthorText.setText(bookItem.getAuthor());
    if (bookItem.getCover() != null && !bookItem.getCover().isEmpty()) {
      try {
        Image coverImage = new Image(bookItem.getCover(), true);
        bookCoverImageView.setImage(coverImage);
      } catch (Exception e) {
        System.err.println("Error loading book cover image: " + e.getMessage());
      }
    }

    JRIBookNoteInfo note = jri.getSelectedBookAndNoteMgr().getSelectedBookNote();
    noteTitleText.setText(note.getTitle());
    String noteText = note.getText();

    noteSearchResultsObservable = FXCollections.observableArrayList();
    bookSearchResultsObservable = FXCollections.observableArrayList();
    noteSearchResultsList.setItems(noteSearchResultsObservable);
    bookSearchResultsList.setItems(bookSearchResultsObservable);

    noteSearchResultsList.setCellFactory(listView -> new ListCell<JRIVectorResultInfo>() {
      @Override
      protected void updateItem(JRIVectorResultInfo item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
        } else {
          setText(item.getBookTitle() + " : " + item.getSentence());
        }
      }
    });

    bookSearchResultsList.setCellFactory(listView -> new ListCell<JRIBookCard>() {
      @Override
      protected void updateItem(JRIBookCard item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
        } else {
          setText(item.getBookItem().getTitle() + " - " + item.getBookItem().getAuthor());
        }
      }
    });


    WebView webView = (WebView) htmlEditor.lookup(".web-view");
    if (webView != null) {
      System.out.println("WebView found in HTMLEditor.");
      WebEngine engine = webView.getEngine();

      engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
        if (newState == Worker.State.SUCCEEDED && !isContentSet) {
          setHtmlContent(noteText);
        }
      });

      // 첫 진입 시 HTML 설정 (즉시 실행)
      Platform.runLater(() -> {
        if (!isContentSet) {
          setHtmlContent(noteText);
        }
      });

      // 폰트 및 CSS 설정
      engine.executeScript(
        "document.head.innerHTML += '<style>" +
          "@import url(http://fonts.googleapis.com/earlyaccess/notosanskr.css);" +
          "* { font-family: \"Noto Sans KR\", sans-serif; font-size: 14px; line-height: 1.6; }" +
          "</style>';"
      );
    } else {
      System.err.println("WebView not found in HTMLEditor. HTML content cannot be set.");
    }

    // 링크 클릭 이벤트 감지
    setupLinkClickListener();

    // 붙여넣기 이벤트 감지
    setupPasteEventListener();

    SIDE_NOTE.setTranslateX(800);
  }

  private void setHtmlContent(String noteText) {
    try {
      if (noteText != null && !noteText.isEmpty()) {
        htmlEditor.setHtmlText(noteText);
        System.out.println("HTML content successfully set in HTMLEditor.");
      } else {
        htmlEditor.setHtmlText("");
        System.out.println("Note text is empty or null.");
      }
      isContentSet = true;
    } catch (Exception e) {
      System.err.println("Error setting HTML content: " + e.getMessage());
    }
  }

  public void goToBookShelfPage() {
    System.out.println("Go to BookShelfPage button pressed");
    saveNote();
    BookNotePageScenario scenario = (BookNotePageScenario) this.mApp.getScenarioMgr().getCurScene().getScenario();
    scenario.dispatchGoToBookShelfPageButtonPress();
  }

  public void goToHomePage() {
    System.out.println("Go to HomePage button pressed");
    saveNote();
    BookNotePageScenario scenario = (BookNotePageScenario) this.mApp.getScenarioMgr().getCurScene().getScenario();
    scenario.dispatchMoveToHomePageButtonPress();
  }

  public class JavaLogger {
    private final BookNotePageController controller;
    private static final String JUSTREADIT_PREFIX = "/justreadit/";

    public JavaLogger(BookNotePageController controller) {
      this.controller = controller;
    }

    public void log(String message) {
      if (message.startsWith("Link clicked: ")) {
        String url = message.substring("Link clicked: ".length());
        handleUrl(url);
      } else {
        System.out.println("[WebView] " + message);
      }
    }

    private void handleUrl(String url) {
      if (url.startsWith(JUSTREADIT_PREFIX)) {
        String internalPath = url.substring(JUSTREADIT_PREFIX.length());

        if (internalPath.startsWith("note/") && internalPath.substring(5).matches("\\d+")) {
          String noteId = internalPath.substring(5);
          System.out.println("[WebView] Internal note link detected. Note ID: " + noteId);
          handleInternalLink("note", noteId);

        } else if (internalPath.startsWith("book/") && internalPath.substring(5).matches("\\d+")) {
          String bookId = internalPath.substring(5);
          System.out.println("[WebView] Internal book link detected. Book ID: " + bookId);
          handleInternalLink("book", bookId);

        } else {
          System.out.println("[WebView] Invalid internal link format: " + url);
        }

      } else if (url.startsWith("http://") || url.startsWith("https://")) {
        System.out.println("[WebView] External link detected: " + url);
        handleExternalLink(url);

      } else {
        System.out.println("[WebView] Unknown link type: " + url);
      }
    }

    private void handleInternalLink(String type, String id) {
      Platform.runLater(() -> {
        BookNotePageScenario scenario = (BookNotePageScenario) controller.mApp.getScenarioMgr().getCurScene().getScenario();
        if ("note".equals(type)) {
          System.out.println("[WebView] Processing note ID: " + id);
          scenario.dispatchOpenLikedBookSideView(Integer.parseInt(id));
        } else if ("book".equals(type)) {
          System.out.println("[WebView] Processing book ID: " + id);
          scenario.dispatchMoveToClickedBook(id);
        }
      });
    }

    private void handleExternalLink(String url) {
      System.out.println("[WebView] Processing external link: " + url);
      // 외부 링크 처리 로직
    }
  }

  private void setupLinkClickListener() {
    WebView webView = (WebView) htmlEditor.lookup(".web-view");
    if (webView != null) {
      WebEngine webEngine = webView.getEngine();

      webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
        if (newState == Worker.State.SUCCEEDED) {
          // 자바 객체를 JS에서 접근 가능하도록 설정
          JSObject window = (JSObject) webEngine.executeScript("window");
          window.setMember("javaLogger", new JavaLogger(this));  // this 참조 전달

          // a 태그 클릭 이벤트 리스너 설정 (JS에서)
          String script = "document.addEventListener('click', function(e) {" +
            "var target = e.target;" +
            "while (target && target.tagName !== 'A') {" +
            "  target = target.parentNode;" +
            "}" +
            "if (target && target.tagName === 'A') {" +
            "  e.preventDefault();" +
            "  var href = target.getAttribute('href');" +
            "  window.javaLogger.log('Link clicked: ' + href);" +
            "}" +
            "}, false);";

          webEngine.executeScript(script);
        }
      });
    }
  }

  private void setupPasteEventListener() {
    htmlEditor.setOnKeyReleased(event -> {
      if ((event.isControlDown() || event.isMetaDown()) && event.getCode().getName().equalsIgnoreCase("V")) {
        handlePaste();
      }
    });
  }

  private void handlePaste() {
    String currentHtml = htmlEditor.getHtmlText();
    Clipboard clipboard = Clipboard.getSystemClipboard();

    // 이미지가 있는 경우
    if (clipboard.hasImage()) {
      try {
        Image image = clipboard.getImage();
        if (image != null) {
          String base64Image = imageToBase64(image);
          WebView webView = (WebView) htmlEditor.lookup(".web-view");
          if (webView != null) {
            WebEngine engine = webView.getEngine();
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
      // URL 감지 정규식
      Pattern urlPattern = Pattern.compile(
        "(?<!<a href=\")https?://[\\w.-]+(:\\d+)?(/[\\w~:%+#?=&/\\-]*)?(\\.[a-zA-Z]{2,4})?",
        Pattern.CASE_INSENSITIVE);

      Matcher matcher = urlPattern.matcher(currentHtml);
      StringBuffer processedHtml = new StringBuffer();

      while (matcher.find()) {
        String url = matcher.group();
        String linkHtml = "<a href=\"" + escapeHtml(url) + "\">" + escapeHtml(url) + "</a>";
        matcher.appendReplacement(processedHtml, linkHtml);
      }
      matcher.appendTail(processedHtml);

      htmlEditor.setHtmlText(processedHtml.toString());
    }

    Platform.runLater(() -> {
      htmlEditor.requestFocus();
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

      BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      PixelReader pixelReader = image.getPixelReader();
      for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          bufferedImage.setRGB(x, y, pixelReader.getArgb(x, y));
        }
      }

      ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
      ImageIO.write(bufferedImage, "png", byteOutput);
      return Base64.getEncoder().encodeToString(byteOutput.toByteArray());
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
  }

  private String escapeHtml(String text) {
    if (text == null) return null;
    return text.replace("&", "&amp;")
      .replace("<", "&lt;")
      .replace(">", "&gt;")
      .replace("\"", "&quot;")
      .replace("'", "&#39;");
  }

  public void showSlide(MouseEvent mouseEvent) {
    TranslateTransition sideNoteSlide = new TranslateTransition();
    sideNoteSlide.setDuration(Duration.seconds(0.5));
    sideNoteSlide.setNode(SIDE_NOTE);
    sideNoteSlide.setToX(-20);
    sideNoteSlide.play();

    Timeline editorResize = new Timeline();
    KeyValue widthKey = new KeyValue(htmlEditor.prefWidthProperty(), 800);
    KeyValue heightKey = new KeyValue(htmlEditor.prefHeightProperty(), 730);
    KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), widthKey, heightKey);
    editorResize.getKeyFrames().add(keyFrame);
    editorResize.play();
  }

  public void hideSlide(MouseEvent mouseEvent) {
    TranslateTransition sideNoteSlide = new TranslateTransition();
    sideNoteSlide.setDuration(Duration.seconds(0.5));
    sideNoteSlide.setNode(SIDE_NOTE);
    sideNoteSlide.setToX(800);
    sideNoteSlide.play();

    Timeline editorResize = new Timeline();
    KeyValue widthKey = new KeyValue(htmlEditor.prefWidthProperty(), 1466);
    KeyValue heightKey = new KeyValue(htmlEditor.prefHeightProperty(), 730);
    KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), widthKey, heightKey);
    editorResize.getKeyFrames().add(keyFrame);
    editorResize.play();
  }

  public void saveNote() {
    String htmlContent = htmlEditor.getHtmlText();

    if (pendingNoteData.toString().equals(htmlContent)) {
      System.out.println("No changes detected. Skipping save.");
      return;
    }

    saveNoteWithDebounce(htmlContent);
  }

  public void saveNoteWithDebounce(String htmlContent) {
    synchronized (pendingNoteData) {
      pendingNoteData.setLength(0);
      pendingNoteData.append(htmlContent);
    }

    if (debounceTask != null && !debounceTask.isCancelled() && !debounceTask.isDone()) {
      debounceTask.cancel(true);
    }

    debounceTask = scheduler.schedule(() -> {
      synchronized (pendingNoteData) {
        String dataToSave = pendingNoteData.toString();
        pendingNoteData.setLength(0);
        performSaveNoteRequest(dataToSave);
      }
    }, 500, TimeUnit.MILLISECONDS);
  }

  private void performSaveNoteRequest(String htmlContent) {
    JRIApp jri = (JRIApp) this.mApp;
    JRISelectedBookAndNoteMgr selectedBookAndNoteMgr = jri.getSelectedBookAndNoteMgr();
    JRIBookNoteInfo note = selectedBookAndNoteMgr.getSelectedBookNote();
    String bookTitle = selectedBookAndNoteMgr.getSelectedBookCard().getBookItem().getTitle();

    CompletableFuture.runAsync(() -> {
      System.out.println("Saving note asynchronously: " + note.getNoteId());
      boolean success = ServerAPI.saveNote(note.getBookId(), bookTitle, note.getNoteId(), htmlContent);
      if (success) {
        System.out.println("Note saved successfully.");
      } else {
        System.err.println("Failed to save note.");
      }
    });
  }

  @FXML
  public void openNoteSearchModal() {
    noteSearchModalOverlay.setVisible(true);
    noteSearchResultsObservable.clear();
    noteSearchResultsList.setVisible(false);
    noteSearchModalInputField.clear();
    noteSearchModalInputField.requestFocus();
  }

  @FXML
  public void openBookSearchModal() {
    bookSearchModalOverlay.setVisible(true);
    bookSearchResultsObservable.clear();
    bookSearchResultsList.setVisible(false);
    bookSearchModalInputField.clear();
    bookSearchModalInputField.requestFocus();
  }

  @FXML
  public void onNoteSearchModalOverlayClicked(MouseEvent event) {
    System.out.println("Modal overlay clicked!");
    if (event.getTarget() == noteSearchModalOverlay) {
      noteSearchModalOverlay.setVisible(false);
      BookNotePageScenario scenario = (BookNotePageScenario) this.mApp.getScenarioMgr().getCurScene().getScenario();
      scenario.dispatchCloseNoteSearchModal();
    }
  }

  @FXML
  public void onBookSearchModalOverlayClicked(MouseEvent event) {
    System.out.println("Modal overlay clicked!");
    if (event.getTarget() == bookSearchModalOverlay) {
      bookSearchModalOverlay.setVisible(false);
      BookNotePageScenario scenario = (BookNotePageScenario) this.mApp.getScenarioMgr().getCurScene().getScenario();
      scenario.dispatchCloseBookSearchModal();
    }
  }

  @FXML
  public void onNoteSearchModalInputKeyPressed(javafx.scene.input.KeyEvent event) {
    switch (event.getCode()) {
      case ENTER:
        String inputText = noteSearchModalInputField.getText();
        System.out.println("Search input: " + inputText);
        searchNoteByVector(inputText);
        break;
      default:
        break;
    }
  }

  @FXML
  public void onBookSearchModalInputKeyPressed(javafx.scene.input.KeyEvent event) {
    switch (event.getCode()) {
      case ENTER:
        String inputText = bookSearchModalInputField.getText();
        System.out.println("Search input: " + inputText);
        searchBook(inputText);
        break;
      default:
        break;
    }
  }

  private void searchNoteByVector(String keyword) {
    try {
      JRIApp jri = (JRIApp) this.mApp;
      JRIBookNoteInfo note = jri.getSelectedBookAndNoteMgr().getSelectedBookNote();

      ArrayList<JRIVectorResultInfo> results = ServerAPI.searchNoteByVector(keyword, note.getNoteId());
      Platform.runLater(() -> {
        noteSearchResultsObservable.clear();
        noteSearchResultsObservable.addAll(results);
        noteSearchResultsList.setVisible(!noteSearchResultsObservable.isEmpty());
      });
    } catch (Exception ex) {
      System.out.println("Exception during book search: " + ex.getMessage());
      Platform.runLater(() -> noteSearchResultsList.setVisible(false));
    }
  }

  private void searchBook(String keyword) {
    try {
      ArrayList<JRIBookCard> books = ServerAPI.searchBook(keyword);
      for (JRIBookCard book : books) {
        System.out.println("Title: " + book.getBookItem().getTitle());
        System.out.println("Author: " + book.getBookItem().getAuthor());
      }
      Platform.runLater(() -> {
        bookSearchResultsObservable.clear();
        bookSearchResultsObservable.addAll(books);
        bookSearchResultsList.setVisible(!bookSearchResultsObservable.isEmpty());
      });
    } catch (Exception ex) {
      System.out.println("Exception during book search: " + ex.getMessage());
      Platform.runLater(() -> bookSearchResultsList.setVisible(false));
    }
  }

  @FXML
  public void onNoteSearchResultClicked(MouseEvent event) {
    if (event.getClickCount() == 2) {
      JRIVectorResultInfo selectedItem = noteSearchResultsList.getSelectionModel().getSelectedItem();
      if (selectedItem != null) {
        System.out.println("Selected item: " + selectedItem.getBookTitle() + " : " + selectedItem.getSentence());
        String noteId = String.valueOf(selectedItem.getNoteId());
        String sentence = selectedItem.getSentence();
        String linkUrl = "/justreadit/note/" + noteId;

        insertLinkIntoHtmlEditor(linkUrl, sentence);
        BookNotePageScenario scenario = (BookNotePageScenario) this.mApp.getScenarioMgr().getCurScene().getScenario();
        noteSearchModalOverlay.setVisible(false);
        scenario.dispatchCloseNoteSearchModal();
      }
    }
  }

  @FXML
  public void onBookSearchResultClicked(MouseEvent event) {
    if (event.getClickCount() == 2) {
      JRIBookCard selectedItem = bookSearchResultsList.getSelectionModel().getSelectedItem();
      if (selectedItem != null) {
        String sentence = selectedItem.getBookItem().getTitle() + " : " + selectedItem.getBookItem().getAuthor();
        String linkUrl = "/justreadit/book/" + selectedItem.getBookItem().getItemId();

        insertLinkIntoHtmlEditor(linkUrl, sentence);
        BookNotePageScenario scenario = (BookNotePageScenario) this.mApp.getScenarioMgr().getCurScene().getScenario();
        bookSearchModalOverlay.setVisible(false);
        scenario.dispatchCloseBookSearchModal();
      }
    }
  }

  private void insertLinkIntoHtmlEditor(String url, String displayText) {
    Platform.runLater(() -> {
      WebView webView = (WebView) htmlEditor.lookup(".web-view");
      if (webView != null) {
        WebEngine engine = webView.getEngine();
        String script = String.format(
          "var selection = window.getSelection();" +
            "if (selection.rangeCount > 0) {" +
            "    var range = selection.getRangeAt(0);" +
            "    var a = document.createElement('a');" +
            "    a.href = '%s';" +
            "    a.textContent = '%s';" +
            "    range.deleteContents();" +
            "    range.insertNode(a);" +
            "    range.collapse(false);" +
            "    selection.removeAllRanges();" +
            "    selection.addRange(range);" +
            "}", url, displayText
        );
        engine.executeScript(script);
      } else {
        System.err.println("WebView not found in HTMLEditor.");
      }
    });
  }

  public void setAndOpenLikedBookSideView(String noteTitle, String text, Map<String, Object> book) {
    sideNoteNoteTitleText.setText(noteTitle);
    sideNoteBooktTitleTet.setText((String) book.get("title"));
    sideNoteAuthorText.setText((String) book.get("author"));
    if (book.get("cover") != null) {
      try {
        String coverUrl = (String) book.get("cover"); // 명확하게 String으로 캐스팅
        Image coverImage = new Image(coverUrl, true);
        sideNoteBookCoverImageView.setImage(coverImage);
      } catch (Exception e) {
        System.err.println("Error loading book cover image: " + e.getMessage());
      }
    }

    // WebView에 HTML 내용을 읽기 전용으로 표시
    Platform.runLater(() -> {
      WebEngine engine = readOnlyWebView.getEngine();
      engine.loadContent(text, "text/html"); // HTML 내용 로드
    });

    showSlide(null);
  }
}
