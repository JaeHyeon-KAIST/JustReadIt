package jri.justreadit;

import jri.justreadit.types.JRIBookCard;
import jri.justreadit.types.JRIBookNoteInfo;

import java.util.ArrayList;

public class JRISelectedBookAndNoteMgr {
  private JRIBookCard mSelectedBookCard;
  private JRIBookNoteInfo mSelectedBookNote;
  private ArrayList<JRIBookNoteInfo> mBookNotes;
  private int mEditingNoteId;

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

  public ArrayList<JRIBookNoteInfo> getBookNotes() {
    return this.mBookNotes;
  }

  public void setBookNotes(ArrayList<JRIBookNoteInfo> bookNotes) {
    if (this.mBookNotes == null) {
      this.mBookNotes = new ArrayList<>();
    }
    this.mBookNotes.clear();
    this.mBookNotes.addAll(bookNotes);
  }

  public JRISelectedBookAndNoteMgr() {
    this.mSelectedBookCard = null;
    this.mSelectedBookNote = null;
    this.mBookNotes = new ArrayList<>();
  }

  public void setEditingNoteId(int editingNoteId) {
    this.mEditingNoteId = editingNoteId;
  }

  public int getEditingNoteId() {
    return this.mEditingNoteId;
  }
}
