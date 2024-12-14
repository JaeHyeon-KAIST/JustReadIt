package jri.justreadit.AladdinOpenAPI;

public class AladdinBookItem {

  private String itemId;
  private String title;
  private String link;
  private String author;
  private String pubDate;
  private String description;
  private String isbn;
  private String isbn13;
  private int priceSales;
  private int priceStandard;
  private String mallType;
  private String stockStatus;
  private int mileage;
  private String cover;
  private int categoryId;
  private String categoryName;
  private String publisher;
  private int salesPoint;
  private boolean adult;
  private boolean fixedPrice;
  private int customerReviewRank;

  // 기본 생성자
  public AladdinBookItem() {
  }

  // 게터와 세터 메서드

  public String getItemId() {
    return itemId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }

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

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getPubDate() {
    return pubDate;
  }

  public void setPubDate(String pubDate) {
    this.pubDate = pubDate;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getIsbn() {
    return isbn;
  }

  public void setIsbn(String isbn) {
    this.isbn = isbn;
  }

  public String getIsbn13() {
    return isbn13;
  }

  public void setIsbn13(String isbn13) {
    this.isbn13 = isbn13;
  }

  public int getPriceSales() {
    return priceSales;
  }

  public void setPriceSales(int priceSales) {
    this.priceSales = priceSales;
  }

  public int getPriceStandard() {
    return priceStandard;
  }

  public void setPriceStandard(int priceStandard) {
    this.priceStandard = priceStandard;
  }

  public String getMallType() {
    return mallType;
  }

  public void setMallType(String mallType) {
    this.mallType = mallType;
  }

  public String getStockStatus() {
    return stockStatus;
  }

  public void setStockStatus(String stockStatus) {
    this.stockStatus = stockStatus;
  }

  public int getMileage() {
    return mileage;
  }

  public void setMileage(int mileage) {
    this.mileage = mileage;
  }

  public String getCover() {
    return cover;
  }

  public void setCover(String cover) {
    this.cover = cover;
  }

  public int getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(int categoryId) {
    this.categoryId = categoryId;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(String publisher) {
    this.publisher = publisher;
  }

  public int getSalesPoint() {
    return salesPoint;
  }

  public void setSalesPoint(int salesPoint) {
    this.salesPoint = salesPoint;
  }

  public boolean isAdult() {
    return adult;
  }

  public void setAdult(boolean adult) {
    this.adult = adult;
  }

  public boolean isFixedPrice() {
    return fixedPrice;
  }

  public void setFixedPrice(boolean fixedPrice) {
    this.fixedPrice = fixedPrice;
  }

  public int getCustomerReviewRank() {
    return customerReviewRank;
  }

  public void setCustomerReviewRank(int customerReviewRank) {
    this.customerReviewRank = customerReviewRank;
  }
}
