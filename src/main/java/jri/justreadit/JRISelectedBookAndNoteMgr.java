package jri.justreadit;

public class JRISelectedBookAndNoteMgr {
  private JRIBookCard mSelectedBookCard = null;

  public JRIBookCard getSelectedBookCard() {
    return this.mSelectedBookCard;
  }

  public void setSelectedBookCard(JRIBookCard selectedBookCard) {
    this.mSelectedBookCard = selectedBookCard;
  }

  public JRISelectedBookAndNoteMgr() {
    this.mSelectedBookCard = null;
  }
}
