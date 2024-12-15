package jri.justreadit;

public class JRIVectorResultInfo {
  private String bookId;      // 책 ID
  private String bookTitle;   // 책 제목
  private int noteId;         // 노트 ID
  private String sentence;    // 실제 문장 텍스트
  private double similarity;  // 유사도 점수

  public JRIVectorResultInfo(String bookId, String bookTitle, int noteId, String sentence, double similarity) {
    this.bookId = bookId;
    this.bookTitle = bookTitle;
    this.noteId = noteId;
    this.sentence = sentence;
    this.similarity = similarity;
  }

  public String getBookId() {
    return bookId;
  }

  public String getBookTitle() {
    return bookTitle;
  }

  public int getNoteId() {
    return noteId;
  }

  public String getSentence() {
    return sentence;
  }

  public double getSimilarity() {
    return similarity;
  }
}
