package jri.justreadit;

import jri.justreadit.AladdinOpenAPI.AladdinBookItem;

import java.awt.*;

public class JRIBookCard {
  private AladdinBookItem mBookItem; // 책 정보
  private Point mPosition;          // 책 카드의 위치

  // 생성자
  public JRIBookCard(AladdinBookItem bookItem, Point position) {
    this.mBookItem = bookItem;
    this.mPosition = position;
  }

  // 게터와 세터
  public AladdinBookItem getBookItem() {
    return this.mBookItem;
  }

  public Point getPosition() {
    return this.mPosition;
  }

  public void setPosition(Point position) {
    this.mPosition = position;
  }
}
