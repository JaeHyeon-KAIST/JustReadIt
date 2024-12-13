package jri.justreadit.canvas;

import jri.justreadit.JRIBookCard;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.JPanel;

public class JRICanvas2D extends JPanel {
  //constants
  private static final float CardWidth = 50;
  private static final float CardHeight = 150;


  private ArrayList<JRIBookCard> mBookCards = null;
  private JRIBookCard mTempBookCard = null;

  public void addBookCard(JRIBookCard bookCard) {
    this.mBookCards.add(bookCard);
  }

  public void createTempBookCard(Point position) {
    this.mTempBookCard = new JRIBookCard("", position);
  }

  public void addTempBookCard() {
    this.mBookCards.add(this.mTempBookCard);
    this.mTempBookCard = null;
  }

  //cons
  public JRICanvas2D() {
    this.mBookCards = new ArrayList<JRIBookCard>();

    this.mBookCards.add(new JRIBookCard("The Great Gatsby", new Point(100, 200)));
    this.mBookCards.add(new JRIBookCard("The Catcher in the Rye", new Point(200, 300)));
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;

    this.drawBookCard(g2);
    // turn on anti-aliasing.
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
  }

  private void drawBookCard(Graphics2D g2) {
    for (JRIBookCard bookCard : this.mBookCards) {
      Point position = bookCard.getPosition();

      // 중심점 기준으로 실제 그리기 위치 계산
      int x = position.x - (int) (CardWidth / 2);
      int y = position.y - (int) (CardHeight / 2);

      // Set the card's color and draw the rectangle
      g2.setColor(Color.LIGHT_GRAY);
      g2.fillRect(x, y, (int) CardWidth, (int) CardHeight);

      // Draw the border of the card
      g2.setColor(Color.BLACK);
      g2.setStroke(new BasicStroke(2.0f));
      g2.drawRect(x, y, (int) CardWidth, (int) CardHeight);

      // Draw the book title (텍스트도 중심점 기준으로 조정)
      g2.setColor(Color.BLACK);
      g2.setFont(new Font("Arial", Font.PLAIN, 12));
      g2.drawString(bookCard.getTitle(), x + 5, y + 15);
    }
  }
}
