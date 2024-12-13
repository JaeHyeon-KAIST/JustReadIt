package jri.justreadit;

import java.awt.*;

public class JRIBookCard {
  private String mTitle = null;

  public String getTitle() {
    return this.mTitle;
  }

  private Point mPosition = null;

  public Point getPosition() {
    return this.mPosition;
  }

  public JRIBookCard(String title, Point position) {
    this.mTitle = title;
    this.mPosition = position;
  }
}
