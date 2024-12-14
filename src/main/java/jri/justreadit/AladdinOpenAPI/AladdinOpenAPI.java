package jri.justreadit.AladdinOpenAPI;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class AladdinOpenAPI {

  private static final String API_URL = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx";

  public AladdinOpenAPI() {
  }

  public AladdinResponse searchItems(String query, int maxResults, int start) throws Exception {
    Map<String, String> env = System.getenv();
    String ttbKey = env.get("ALADDIN_TTB_KEY");

    String requestUrl = API_URL +
      "?ttbkey=" + ttbKey +
      "&Query=" + URLEncoder.encode(query, StandardCharsets.UTF_8) +
      "&QueryType=Title" +
      "&MaxResults=" + maxResults +
      "&start=" + start +
      "&SearchTarget=Book" +
      "&output=xml" +
      "&Version=20131101";

    // API 호출
    URL url = new URL(requestUrl);
    InputStream is = url.openStream();

    // XML 파싱
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true); // 네임스페이스를 인식하도록 설정
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.parse(is);

    // AladdinResponse 객체 생성
    AladdinResponse response = parseResponse(doc);

    is.close();

    return response;
  }

  private AladdinResponse parseResponse(Document doc) {
    AladdinResponse response = new AladdinResponse();

    Element root = doc.getDocumentElement();

    response.setTitle(getTextContent(root, "title"));
    response.setLink(getTextContent(root, "link"));
    response.setLogo(getTextContent(root, "logo"));
    response.setPubDate(getTextContent(root, "pubDate"));
    response.setTotalResults(Integer.parseInt(getTextContent(root, "totalResults")));
    response.setStartIndex(Integer.parseInt(getTextContent(root, "startIndex")));
    response.setItemsPerPage(Integer.parseInt(getTextContent(root, "itemsPerPage")));
    response.setQuery(getTextContent(root, "query"));
    response.setVersion(getTextContent(root, "version"));
    response.setSearchCategoryId(Integer.parseInt(getTextContent(root, "searchCategoryId")));
    response.setSearchCategoryName(getTextContent(root, "searchCategoryName"));

    // item 목록 파싱
    NodeList itemNodes = root.getElementsByTagName("item");
    for (int i = 0; i < itemNodes.getLength(); i++) {
      Element itemElement = (Element) itemNodes.item(i);
      AladdinBookItem item = parseItem(itemElement);
      response.addItem(item);
    }

    return response;
  }

  private AladdinBookItem parseItem(Element itemElement) {
    AladdinBookItem item = new AladdinBookItem();

    item.setItemId(itemElement.getAttribute("itemId"));
    item.setTitle(getTextContent(itemElement, "title"));
    item.setLink(getTextContent(itemElement, "link"));
    item.setAuthor(getTextContent(itemElement, "author"));
    item.setPubDate(getTextContent(itemElement, "pubDate"));
    item.setDescription(getTextContent(itemElement, "description"));
    item.setIsbn(getTextContent(itemElement, "isbn"));
    item.setIsbn13(getTextContent(itemElement, "isbn13"));
    item.setPriceSales(parseInt(getTextContent(itemElement, "priceSales")));
    item.setPriceStandard(parseInt(getTextContent(itemElement, "priceStandard")));
    item.setMallType(getTextContent(itemElement, "mallType"));
    item.setStockStatus(getTextContent(itemElement, "stockStatus"));
    item.setMileage(parseInt(getTextContent(itemElement, "mileage")));
    item.setCover(getTextContent(itemElement, "cover"));
    item.setCategoryId(parseInt(getTextContent(itemElement, "categoryId")));
    item.setCategoryName(getTextContent(itemElement, "categoryName"));
    item.setPublisher(getTextContent(itemElement, "publisher"));
    item.setSalesPoint(parseInt(getTextContent(itemElement, "salesPoint")));
    item.setAdult(Boolean.parseBoolean(getTextContent(itemElement, "adult")));
    item.setFixedPrice(Boolean.parseBoolean(getTextContent(itemElement, "fixedPrice")));
    item.setCustomerReviewRank(parseInt(getTextContent(itemElement, "customerReviewRank")));

    return item;
  }

  private String getTextContent(Element parent, String tagName) {
    NodeList nodeList = parent.getElementsByTagName(tagName);
    if (nodeList.getLength() > 0 && nodeList.item(0).getFirstChild() != null) {
      return nodeList.item(0).getTextContent();
    } else {
      return "";
    }
  }

  private Element getChildElement(Element parent, String tagName) {
    NodeList nodeList = parent.getElementsByTagName(tagName);
    if (nodeList.getLength() > 0) {
      return (Element) nodeList.item(0);
    } else {
      return null;
    }
  }

  private int parseInt(String value) {
    if (value == null || value.isEmpty()) return 0;
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      return 0;
    }
  }
}
