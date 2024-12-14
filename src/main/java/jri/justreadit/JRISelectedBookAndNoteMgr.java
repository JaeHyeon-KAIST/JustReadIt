package jri.justreadit;

public class JRISelectedBookAndNoteMgr {
  private JRIBookCard mSelectedBookCard = null;
  private JRIBookNoteInfo mSelectedBookNote = null;

  public JRIBookCard getSelectedBookCard() {
    return this.mSelectedBookCard;
  }

  public void setSelectedBookCard(JRIBookCard selectedBookCard) {
    this.mSelectedBookCard = selectedBookCard;
  }

  public JRIBookNoteInfo getSelectedBookNote() {
    return this.mSelectedBookNote;
  }

  public void setSelectedBookNote(JRIBookNoteInfo selectedBookNote) {
    this.mSelectedBookNote = selectedBookNote;
  }

  public JRISelectedBookAndNoteMgr() {
    this.mSelectedBookCard = null;
    this.mSelectedBookNote = null;
  }
}
