package jri.justreadit;

public class JRIConnectionInfo {
  private final String baseBookId;
  private final String targetBookId;
  private final int count;

  public JRIConnectionInfo(String baseBookId, String targetBookId, int count) {
    this.baseBookId = baseBookId;
    this.targetBookId = targetBookId;
    this.count = count;
  }

  public String getBaseBookId() {
    return baseBookId;
  }

  public String getTargetBookId() {
    return targetBookId;
  }

  public int getCount() {
    return count;
  }
}
