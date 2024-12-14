package jri.justreadit.canvas;

import jri.justreadit.JRIBookCard;
import jri.justreadit.utils.AladdinOpenAPI.AladdinBookItem;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

public class JRICanvas2D extends JPanel {
  // constants
  private static final int CARD_WIDTH = 100;
  private static final int CARD_HEIGHT = 150;
  private static final int CARD_CATEGORY_DISTANCE = 250;
  private static final Color BOOK_CARD_DEFULT_COLOR = Color.white;
  private static final Color SELECTED_CARD_COLOR = new Color(0x00B0FF);
  private static final Color BACKGROUND_COLOR = new Color(0xB3D0EDFA, true);
  private static final Color CATRGORY_BORDER_COLOR = new Color(0xCEE37D);

  private ArrayList<JRIBookCard> mBookCards; // 저장된 북 카드 리스트
  private Point mNewBookCardPosition; // 임시 북 카드
  private ArrayList<HashSet<JRIBookCard>> mCategoryGroups;

  public Point getNewBookCardPosition() {
    return mNewBookCardPosition;
  }

  private JRIBookCard mSelectedBookCard;     // 선택된 북 카드
  private Point previousMousePosition; // 이전 마우스 좌표

  public void initBookCards() {
    this.mBookCards = new ArrayList<>();
    this.mCategoryGroups = new ArrayList<>();
  }

  public JRIBookCard getSelectedBookCard() {
    return mSelectedBookCard;
  }

  public void setSelectedBookCard(JRIBookCard selectedBookCard) {
    this.mSelectedBookCard = selectedBookCard;
  }

  // 생성자
  public JRICanvas2D() {
    this.mBookCards = new ArrayList<>();
    setBackground(Color.white);
  }

  // 북 카드 추가
  public void addBookCard(AladdinBookItem bookItem) {
    this.mBookCards.add(new JRIBookCard(bookItem, mNewBookCardPosition));
    mNewBookCardPosition = null;

    //그룸 간 거리 비교 카테고리 업데이트
    updateCategoryCardGroups();
  }

  public void initializeBookCards(JRIBookCard bookCard) {
    this.mBookCards.add(bookCard);

    //그룸 간 거리 비교 카테고리 업데이트
    updateCategoryCardGroups();
  }

  public void setNewBookCardPosition(Point position) {
    this.mNewBookCardPosition = position;
  }

  public void setPreviousMousePosition(Point position) {
    this.previousMousePosition = position;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.setColor(BACKGROUND_COLOR);
    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
    g2.setColor(BACKGROUND_COLOR);
    g2.setStroke(new BasicStroke(2.0f));
    g2.drawRoundRect(2, 2, getWidth()-3, getHeight()-3, 15, 15);

    // 활성화된 카드 그리기
    this.drawBookCards(g2);

    // 그룹 카드 간 연결선 그리기
    for(HashSet<JRIBookCard> group : mCategoryGroups){
      if(group.size() > 1){
        drawCategoryBorder(g2, group);
      }
    }

    // Anti-aliasing 설정
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
  }

  private void drawBookCards(Graphics2D g2) {
    for (JRIBookCard bookCard : this.mBookCards) {
      Point position = bookCard.getPosition();

      // 중심점 기준으로 위치 계산
      int x = position.x - (CARD_WIDTH / 2);
      int y = position.y - (CARD_HEIGHT / 2);

      BufferedImage bookCover = null;

      // 책 커버 이미지 로드
      try {
        URL url = new URL(bookCard.getBookItem().getCover());
        bookCover = ImageIO.read(url);
      } catch (IOException e) {
        System.out.println("Failed to load image for: " + bookCard.getBookItem().getTitle());
      }

      // 이미지 또는 기본 배경 그리기
      if (bookCover != null) {
        g2.drawImage(bookCover, x, y, CARD_WIDTH, CARD_HEIGHT, null);
      } else {
        g2.setColor(BOOK_CARD_DEFULT_COLOR);
        g2.fillRect(x, y, CARD_WIDTH, CARD_HEIGHT);
      }

      // 카드 테두리 색상 설정 (선택된 카드일 경우 파란색, 아니면 기본 색상)
      if (bookCard == mSelectedBookCard) {
        g2.setColor(SELECTED_CARD_COLOR); // 파란색 테두리
      } else {
        g2.setColor(BOOK_CARD_DEFULT_COLOR); // 기본 테두리 색상
      }
      g2.setStroke(new BasicStroke(5.0f));
      g2.drawRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, 10, 10);
    }
  }

  private void drawCategoryBorder(Graphics2D g2, HashSet<JRIBookCard> group) {
    if(group.isEmpty()) return;

    int minX = Integer.MAX_VALUE;
    int minY = Integer.MAX_VALUE;
    int maxX = Integer.MIN_VALUE;
    int maxY = Integer.MIN_VALUE;

    for(JRIBookCard card : group){
      Point position = card.getPosition();
      minX = Math.min(minX, position.x - CARD_WIDTH / 2);
      minY = Math.min(minY, position.y - CARD_HEIGHT / 2);
      maxX = Math.max(maxX, position.x + CARD_WIDTH / 2);
      maxY = Math.max(maxY, position.y + CARD_HEIGHT / 2);
    }

    g2.setColor(CATRGORY_BORDER_COLOR);
    g2.setStroke(new BasicStroke(5.0f));
    g2.drawRoundRect(minX - 10, minY - 10, maxX - minX + 20, maxY - minY + 20, 20, 20);
  }

  public Point screenPointToCanvasPoint(Point screenLocation) {
    Point canvasLocation = this.getLocationOnScreen();

    int relativeX = screenLocation.x - canvasLocation.x;
    int relativeY = screenLocation.y - canvasLocation.y;

    return new Point(relativeX, relativeY);
  }

  // 클릭된 카드 찾기 메서드
  public JRIBookCard getClickedBookCard(Point clickPoint) {
    for (JRIBookCard bookCard : this.mBookCards) {
      Point cardPosition = bookCard.getPosition();
      int x = cardPosition.x - (CARD_WIDTH / 2);
      int y = cardPosition.y - (CARD_HEIGHT / 2);

      // 카드의 경계를 Rectangle로 정의
      Rectangle cardBounds = new Rectangle(x, y, CARD_WIDTH, CARD_HEIGHT);

      // 클릭된 위치가 경계 안에 있는지 확인
      if (cardBounds.contains(clickPoint)) {
        return bookCard; // 클릭된 카드 반환
      }
    }
    return null; // 클릭된 카드가 없으면 null 반환
  }

  public void updateSelectedBookCardPosition(Point currentMousePosition) {
    if (mSelectedBookCard != null && previousMousePosition != null) {
      // 현재 마우스 좌표와 이전 좌표의 차이를 계산
      int deltaX = currentMousePosition.x - previousMousePosition.x;
      int deltaY = currentMousePosition.y - previousMousePosition.y;

      // 카드의 현재 위치
      Point currentCardPosition = mSelectedBookCard.getPosition();

      // 새로운 카드 위치 계산
      int newX = currentCardPosition.x + deltaX;
      int newY = currentCardPosition.y + deltaY;

      // 화면 경계 제한 적용
      int canvasWidth = this.getWidth();
      int canvasHeight = this.getHeight();

      // 카드 중심점이 화면 안에 머물도록 제한
      int limitedX = Math.max(CARD_WIDTH / 2, Math.min(newX, canvasWidth - CARD_WIDTH / 2));
      int limitedY = Math.max(CARD_HEIGHT / 2, Math.min(newY, canvasHeight - CARD_HEIGHT / 2));

      // 제한된 위치로 카드 이동
      mSelectedBookCard.setPosition(new Point(limitedX, limitedY));

      // 그룹 업데이트 로직
      updateCategoryCardGroups();

      // 이전 마우스 좌표를 현재 좌표로 갱신
      previousMousePosition = currentMousePosition;
    }
  }

  private void updateCategoryCardGroups() {
    this.mCategoryGroups.clear(); // 기존 그룹 초기화

    for (JRIBookCard card : mBookCards) {
      boolean addedToGroup = false;

      // 기존 그룹 검사
      for (HashSet<JRIBookCard> group : this.mCategoryGroups) {
        for (JRIBookCard groupCard : group) {
          if (card.getPosition().distance(groupCard.getPosition()) < CARD_CATEGORY_DISTANCE) {
            group.add(card);
            addedToGroup = true;
            break;
          }
        }
        if (addedToGroup) break;
      }

      // 그룹에 속하지 않았다면 새 그룹 생성
      if (!addedToGroup) {
        HashSet<JRIBookCard> newGroup = new HashSet<>();
        newGroup.add(card);
        this.mCategoryGroups.add(newGroup);
      }
    }
  }
}
