package jri.justreadit.pageController;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import jri.justreadit.JRIApp;
import jri.justreadit.types.JRIBookCard;
import jri.justreadit.scenario.BookShelfScenario;
import jri.justreadit.utils.AladdinOpenAPI.AladdinBookItem;
import x.XPageController;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class BookShelfPageController extends XPageController {
  public static final String PAGE_CONTROLLER_NAME = "BookShelfPageController";
  public static final String FXML_NAME = "BookShelfPage";

  public BookShelfPageController(JRIApp app, String fxmlBasePath) {
    super(PAGE_CONTROLLER_NAME, fxmlBasePath, FXML_NAME, app);
  }

  @FXML
  private GridPane bookGrid;

  @FXML
  public void initialize() {
  }

  public void goToHomePage() {
    System.out.println("Go to BookShelfPage button pressed");
    // Scenario와 Scene을 통한 동작 위임
    BookShelfScenario scenario = (BookShelfScenario) this.mApp.getScenarioMgr().getCurScene().getScenario();
    scenario.dispatchGoToHomePageButtonPress();
  }

  public void setBookList(List<Map<String, Object>> bookList) {
    System.out.println("Populating book list...");

    bookGrid.getChildren().clear(); // 기존 데이터를 초기화
    int row = 0, col = 0;

    for (Map<String, Object> book : bookList) {
      // 데이터 가져오기
      String id = String.valueOf(book.get("id"));
      String title = (String) book.get("title");
      String author = (String) book.get("author");
      String publisher = (String) book.get("publisher"); // 출판사 정보 가져오기
      String coverUrl = (String) book.get("cover");

      // 이미지 뷰 생성
      ImageView coverImageView = new ImageView();
      coverImageView.setFitWidth(190); // 원래 FXML 설정 유지
      coverImageView.setFitHeight(220);
      coverImageView.setPreserveRatio(true);

      try {
        Image image = new Image(coverUrl, true);
        coverImageView.setImage(image);
      } catch (Exception e) {
        System.err.println("Failed to load cover image: " + e.getMessage());
        coverImageView.setImage(new Image("/image/default_cover.png")); // 기본 이미지
      }

      // VBox: 책 정보 (이미지 + 제목 + 저자 + 출판사)
      VBox bookInfoBox = new VBox(10);
      bookInfoBox.setAlignment(Pos.CENTER_LEFT);
      bookInfoBox.setPrefHeight(190);
      bookInfoBox.setPrefWidth(553);
      bookInfoBox.getChildren().addAll(
        createText(title, "Apple SD Gothic Neo Bold", 20, 546),
        createText(author, "Apple SD Gothic Neo Regular", 18, 543),
        createText(publisher, "Apple SD Gothic Neo Regular", 16, 543) // 출판사 추가
      );

      // HBox: 이미지와 책 정보 결합
      HBox bookHBox = new HBox(20);
      bookHBox.setAlignment(Pos.CENTER_LEFT);
      bookHBox.setPrefHeight(220);
      bookHBox.setPrefWidth(734);
      bookHBox.getChildren().addAll(coverImageView, bookInfoBox);

      bookHBox.setOnMouseClicked(event -> {
        System.out.println("Clicked Book:");
        System.out.println("Title: " + title);
        System.out.println("Author: " + author);
        System.out.println("Publisher: " + publisher);

        AladdinBookItem bookItem = new AladdinBookItem();
        bookItem.setItemId(id);
        bookItem.setTitle(title);
        bookItem.setAuthor(author);
        bookItem.setPublisher(publisher);
        bookItem.setCover(coverUrl);
        JRIBookCard clickedBook = new JRIBookCard(bookItem, new Point(0, 0));

        BookShelfScenario scenario = (BookShelfScenario) this.mApp.getScenarioMgr().getCurScene().getScenario();
        scenario.dispatchGoToBookDetailPage(clickedBook);
      });

      // GridPane에 추가
      bookGrid.add(bookHBox, col, row);

      col++;
      if (col == 2) { // 한 줄에 2개의 책을 표시
        col = 0;
        row++;
      }
    }
  }

  // 텍스트 스타일을 적용하는 메서드
  private Text createText(String content, String font, int size, double wrappingWidth) {
    Text text = new Text(content);
    text.setFont(new Font(font, size));
    text.setWrappingWidth(wrappingWidth); // 텍스트 줄바꿈 설정
    return text;
  }
}
