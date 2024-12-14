package jri.justreadit.AladdinOpenAPI;

import java.util.ArrayList;
import java.util.List;

public class AladdinResponse {

  private String title;
  private String link;
  private String logo;
  private String pubDate;
  private int totalResults;
  private int startIndex;
  private int itemsPerPage;
  private String query;
  private String version;
  private int searchCategoryId;
  private String searchCategoryName;
  private List<AladdinBookItem> items = new ArrayList<>();

  // 기본 생성자
  public AladdinResponse() {
  }

  // 게터와 세터 메서드

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public String getLogo() {
    return logo;
  }

  public void setLogo(String logo) {
    this.logo = logo;
  }

  public String getPubDate() {
    return pubDate;
  }

  public void setPubDate(String pubDate) {
    this.pubDate = pubDate;
  }

  public int getTotalResults() {
    return totalResults;
  }

  public void setTotalResults(int totalResults) {
    this.totalResults = totalResults;
  }

  public int getStartIndex() {
    return startIndex;
  }

  public void setStartIndex(int startIndex) {
    this.startIndex = startIndex;
  }

  public int getItemsPerPage() {
    return itemsPerPage;
  }

  public void setItemsPerPage(int itemsPerPage) {
    this.itemsPerPage = itemsPerPage;
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public int getSearchCategoryId() {
    return searchCategoryId;
  }

  public void setSearchCategoryId(int searchCategoryId) {
    this.searchCategoryId = searchCategoryId;
  }

  public String getSearchCategoryName() {
    return searchCategoryName;
  }

  public void setSearchCategoryName(String searchCategoryName) {
    this.searchCategoryName = searchCategoryName;
  }

  public List<AladdinBookItem> getItems() {
    return items;
  }

  public void setItems(List<AladdinBookItem> items) {
    this.items = items;
  }

  public void addItem(AladdinBookItem item) {
    this.items.add(item);
  }
}
