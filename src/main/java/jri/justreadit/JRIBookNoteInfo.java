package jri.justreadit;

public class JRIBookNoteInfo {
  private int mNoteId; // 노트 ID
  private String mBookId; // 책 ID
  private String mTitle; // 노트 제목
  private String mType; // 노트 타입
  private String mContent; // 노트 내용

  // 생성자
  public JRIBookNoteInfo(int noteId, String bookId, String title, String type, String content) {
    this.mNoteId = noteId;
    this.mBookId = bookId;
    this.mTitle = title;
    this.mType = type;
    this.mContent = content;
  }

  // 게터와 세터
  public int getNoteId() {
    return this.mNoteId;
  }

  public String getBookId() {
    return this.mBookId;
  }

  public String getTitle() {
    return this.mTitle;
  }

  public String getType() {
    return this.mType;
  }

  public String getContent() {
    return this.mContent;
  }

  public void setNoteId(int noteId) {
    this.mNoteId = noteId;
  }
}
