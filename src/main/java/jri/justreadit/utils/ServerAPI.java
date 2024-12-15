package jri.justreadit.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jri.justreadit.JRIBookCard;
import jri.justreadit.JRIBookNoteInfo;
import jri.justreadit.JRIConnectionInfo;
import jri.justreadit.JRIVectorResultInfo;
import jri.justreadit.utils.AladdinOpenAPI.AladdinBookItem;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerAPI {
  private static final String BASE_URL = "https://justreadit.jaehyeon.com/";

  // getBookList 함수
  public static List<Map<String, Object>> getBookList() {
    String endpoint = BASE_URL + "getBookList";
    HttpURLConnection connection = null;

    try {
      // URL 객체 생성
      URL url = new URL(endpoint);
      connection = (HttpURLConnection) url.openConnection();

      // HTTP GET 설정
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Accept", "application/json");

      // HTTP 응답 코드 확인
      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        // 응답 데이터 읽기
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
          response.append(line);
        }
        reader.close();

        // JSON 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.toString(), List.class);
      } else {
        System.err.println("HTTP GET Request failed with response code: " + responseCode);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
    return null;
  }

  // addBook 함수
  public static boolean addBook(AladdinBookItem book, int positionX, int positionY) {
    String endpoint = BASE_URL + "addBook";
    HttpURLConnection connection = null;

    try {
      // URL 객체 생성
      URL url = new URL(endpoint);
      connection = (HttpURLConnection) url.openConnection();

      // HTTP POST 설정
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setDoOutput(true);

      // JSON 데이터 생성
      Map<String, Object> bookData = new HashMap<>();
      bookData.put("id", book.getItemId());
      bookData.put("title", book.getTitle());
      bookData.put("author", book.getAuthor());
      bookData.put("publisher", book.getPublisher());
      bookData.put("cover", book.getCover());
      bookData.put("positionX", positionX);
      bookData.put("positionY", positionY);

      ObjectMapper objectMapper = new ObjectMapper();
      String jsonInputString = objectMapper.writeValueAsString(bookData);

      // POST 요청에 JSON 데이터 쓰기
      try (OutputStream os = connection.getOutputStream()) {
        byte[] input = jsonInputString.getBytes("utf-8");
        os.write(input, 0, input.length);
      }

      // HTTP 응답 코드 확인
      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
        System.out.println("Book added successfully.");
        return true;
      } else {
        System.err.println("Failed to add book. Response code: " + responseCode);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
    return false;
  }

  public static boolean updateBookPosition(String id, int positionX, int positionY) {
    String endpoint = BASE_URL + "updateBookPosition";
    HttpURLConnection connection = null;

    try {
      // URL 객체 생성
      URL url = new URL(endpoint);
      connection = (HttpURLConnection) url.openConnection();

      // HTTP POST 설정
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setDoOutput(true);

      // JSON 데이터 생성
      Map<String, Object> positionData = new HashMap<>();
      positionData.put("id", id);
      positionData.put("positionX", positionX);
      positionData.put("positionY", positionY);

      ObjectMapper objectMapper = new ObjectMapper();
      String jsonInputString = objectMapper.writeValueAsString(positionData);

      // POST 요청에 JSON 데이터 쓰기
      try (OutputStream os = connection.getOutputStream()) {
        byte[] input = jsonInputString.getBytes("utf-8");
        os.write(input, 0, input.length);
      }

      // HTTP 응답 코드 확인
      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        System.out.println("Book position updated successfully.");
        return true;
      } else {
        System.err.println("Failed to update book position. Response code: " + responseCode);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
    return false;
  }

  public static ArrayList<JRIBookNoteInfo> getBookNotes(String bookId) {
    String endpoint = BASE_URL + "getBookNotes?bookId=" + bookId;
    HttpURLConnection connection = null;

    try {
      URL url = new URL(endpoint);
      connection = (HttpURLConnection) url.openConnection();

      connection.setRequestMethod("GET");
      connection.setRequestProperty("Accept", "application/json");

      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
          response.append(line);
        }
        reader.close();

        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> rawNotes = objectMapper.readValue(response.toString(), List.class);

        ArrayList<JRIBookNoteInfo> bookNotes = new ArrayList<>();
        for (Map<String, Object> note : rawNotes) {
          // 필드 이름 수정 및 Null 체크
          Integer noteId = note.get("id") != null ? (Integer) note.get("id") : -1;
          String bookIdValue = note.get("bookId") != null ? String.valueOf(note.get("bookId")) : "Unknown";
          String title = note.get("title") != null ? (String) note.get("title") : "Untitled";
          String type = note.get("type") != null ? (String) note.get("type") : "Unknown";
          String text = note.get("text") != null ? (String) note.get("text") : ""; // "text"로 수정

          // Note 객체 생성 및 추가
          bookNotes.add(new JRIBookNoteInfo(noteId, bookIdValue, title, type, text));
        }

        return bookNotes;
      } else {
        System.err.println("Failed to get book notes. Response code: " + responseCode);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
    return new ArrayList<>();
  }

  public static int createNote(int bookId, String type) {
    String endpoint = BASE_URL + "createNote";
    HttpURLConnection connection = null;

    try {
      // URL 객체 생성
      URL url = new URL(endpoint);
      connection = (HttpURLConnection) url.openConnection();

      // HTTP POST 설정
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setDoOutput(true);

      // JSON 데이터 생성
      Map<String, Object> noteData = new HashMap<>();
      noteData.put("bookId", bookId);
      noteData.put("type", type);  // "during" 또는 "after"

      ObjectMapper objectMapper = new ObjectMapper();
      String jsonInputString = objectMapper.writeValueAsString(noteData);

      // POST 요청에 JSON 데이터 쓰기
      try (OutputStream os = connection.getOutputStream()) {
        byte[] input = jsonInputString.getBytes("utf-8");
        os.write(input, 0, input.length);
      }

      // HTTP 응답 코드 확인
      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        // 응답 읽기
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
          response.append(line);
        }
        reader.close();

        // JSON 응답 파싱하여 noteId 추출
        Map<String, Object> responseMap = objectMapper.readValue(response.toString(), Map.class);
        if (responseMap.containsKey("noteId")) {
          return ((Number) responseMap.get("noteId")).intValue();
        }
      } else {
        System.err.println("Failed to create note. Response code: " + responseCode);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
    return -1;  // 실패시 -1 반환
  }

  public static boolean saveNote(String bookId, String bookTitle, int noteId, String text) {
    String endpoint = BASE_URL + "saveNote";
    HttpURLConnection connection = null;

    try {
      // URL 객체 생성
      URL url = new URL(endpoint);
      connection = (HttpURLConnection) url.openConnection();

      // HTTP POST 설정
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setDoOutput(true);

      // JSON 데이터 생성
      Map<String, Object> noteData = new HashMap<>();
      noteData.put("bookId", bookId); // bookId 추가
      noteData.put("bookTitle", bookTitle); // bookTitle 추가
      noteData.put("noteId", noteId); // noteId 수정
      noteData.put("text", text);

      ObjectMapper objectMapper = new ObjectMapper();
      String jsonInputString = objectMapper.writeValueAsString(noteData);

      // POST 요청에 JSON 데이터 쓰기
      try (OutputStream os = connection.getOutputStream()) {
        byte[] input = jsonInputString.getBytes("utf-8");
        os.write(input, 0, input.length);
      }

      // HTTP 응답 코드 확인
      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        System.out.println("Note saved successfully.");
        return true;
      } else {
        System.err.println("Failed to save note. Response code: " + responseCode);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
    return false;
  }

  public static ArrayList<JRIVectorResultInfo> searchNoteByVector(String keyWord, int noteId) {
    String endpoint = BASE_URL + "searchNoteByVector"; // 서버 엔드포인트
    HttpURLConnection connection = null;

    try {
      // URL 객체 생성
      URL url = new URL(endpoint);
      connection = (HttpURLConnection) url.openConnection();

      // HTTP POST 설정
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setDoOutput(true);

      // JSON 데이터 생성
      Map<String, Object> searchData = new HashMap<>();
      searchData.put("searchText", keyWord);      // 검색 키워드
      searchData.put("excludeNoteId", noteId);    // 제외할 노트 ID

      ObjectMapper objectMapper = new ObjectMapper();
      String jsonInputString = objectMapper.writeValueAsString(searchData);

      // POST 요청에 JSON 데이터 쓰기
      try (OutputStream os = connection.getOutputStream()) {
        byte[] input = jsonInputString.getBytes("utf-8");
        os.write(input, 0, input.length);
      }

      // HTTP 응답 코드 확인
      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        // 응답 데이터 읽기
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
          response.append(line);
        }
        reader.close();

        // JSON 응답 파싱
        Map<String, Object> responseMap = objectMapper.readValue(response.toString(), Map.class);

        // "results" 필드를 추출하여 변환
        List<Map<String, Object>> resultsList = (List<Map<String, Object>>) responseMap.get("results");
        ArrayList<JRIVectorResultInfo> results = new ArrayList<>();

        for (Map<String, Object> result : resultsList) {
          String bookId = (String) result.get("bookId");
          String bookTitle = (String) result.get("bookTitle");
          int resultNoteId = (Integer) result.get("noteId");
          String sentence = (String) result.get("sentence");
          double similarity = (Double) result.get("similarity");

          results.add(new JRIVectorResultInfo(bookId, bookTitle, resultNoteId, sentence, similarity));
        }

        return results;
      } else {
        System.err.println("Search request failed with response code: " + responseCode);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }

    return new ArrayList<>(); // 실패 시 빈 리스트 반환
  }

  public static Map<String, Object> getNoteInfo(int noteId) {
    String endpoint = BASE_URL + "getNoteInfo";
    HttpURLConnection connection = null;

    try {
      // URL 객체 생성
      URL url = new URL(endpoint);
      connection = (HttpURLConnection) url.openConnection();

      // HTTP POST 설정
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setDoOutput(true);

      // JSON 데이터 생성
      Map<String, Object> requestData = new HashMap<>();
      requestData.put("noteId", noteId);

      ObjectMapper objectMapper = new ObjectMapper();
      String jsonInputString = objectMapper.writeValueAsString(requestData);

      // POST 요청에 JSON 데이터 쓰기
      try (OutputStream os = connection.getOutputStream()) {
        byte[] input = jsonInputString.getBytes("utf-8");
        os.write(input, 0, input.length);
      }

      // HTTP 응답 코드 확인
      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        // 응답 데이터 읽기
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
          response.append(line);
        }
        reader.close();

        // JSON 응답 파싱
        Map<String, Object> responseMap = objectMapper.readValue(response.toString(), Map.class);

        if (responseMap.get("status").equals("success")) {
          Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
          Map<String, Object> resultMap = new HashMap<>();

          resultMap.put("noteTitle", data.get("noteTitle"));
          resultMap.put("text", data.get("text"));
          resultMap.put("book", data.get("book"));

          return resultMap;
        } else {
          System.err.println("Failed to get note info: " + responseMap.get("message"));
        }
      } else {
        System.err.println("Note info request failed with response code: " + responseCode);

        // 에러 응답 읽기
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        StringBuilder errorResponse = new StringBuilder();
        String errorLine;

        while ((errorLine = errorReader.readLine()) != null) {
          errorResponse.append(errorLine);
        }
        errorReader.close();

        System.err.println("Error response: " + errorResponse.toString());
      }
    } catch (Exception e) {
      System.err.println("Exception during note info request: " + e.getMessage());
      e.printStackTrace();
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }

    return null; // 실패 시 null 반환
  }

  public static ArrayList<JRIBookCard> searchBook(String keyword) {
    String endpoint = BASE_URL + "searchBook"; // 서버의 엔드포인트
    HttpURLConnection connection = null;
    ObjectMapper objectMapper = new ObjectMapper();

    ArrayList<JRIBookCard> bookCardList = new ArrayList<>(); // 반환할 리스트

    try {
      // URL 객체 생성 및 연결 설정
      URL url = new URL(endpoint);
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setDoOutput(true);

      // 요청 바디 생성
      Map<String, Object> requestData = new HashMap<>();
      requestData.put("keyword", keyword);
      String jsonInputString = objectMapper.writeValueAsString(requestData);

      try (OutputStream os = connection.getOutputStream()) {
        os.write(jsonInputString.getBytes("utf-8"));
      }

      // 응답 확인
      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
          StringBuilder response = new StringBuilder();
          String line;
          while ((line = reader.readLine()) != null) {
            response.append(line);
          }

          // JSON 응답 파싱
          Map<String, Object> responseMap = objectMapper.readValue(response.toString(), Map.class);
          if ("success".equals(responseMap.get("status"))) {
            List<Map<String, Object>> bookList = (List<Map<String, Object>>) responseMap.get("data");

            for (Map<String, Object> book : bookList) {
              // 데이터 추출
              String id = String.valueOf(book.get("id"));
              String title = (String) book.get("title");
              String author = (String) book.get("author");
              String publisher = (String) book.get("publisher");
              String cover = (String) book.get("cover");

              // 객체 생성
              AladdinBookItem bookItem = new AladdinBookItem();
              bookItem.setItemId(id);
              bookItem.setTitle(title);
              bookItem.setAuthor(author);
              bookItem.setPublisher(publisher);
              bookItem.setCover(cover);

              // JRIBookCard에 추가 (positionX, positionY 기본값 0으로 설정)
              bookCardList.add(new JRIBookCard(bookItem, new Point(0, 0)));
            }
          } else {
            System.err.println("Error: " + responseMap.get("message"));
          }
        }
      } else {
        System.err.println("Server error. Response code: " + responseCode);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }

    return bookCardList; // ArrayList 반환
  }

  public static JRIBookCard searchBookById(String id) {
    String endpoint = BASE_URL + "searchBookById"; // 서버의 엔드포인트
    HttpURLConnection connection = null;
    ObjectMapper objectMapper = new ObjectMapper();

    try {
      // URL 객체 생성 및 연결 설정
      URL url = new URL(endpoint);
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setDoOutput(true);

      // 요청 바디 생성
      Map<String, Object> requestData = new HashMap<>();
      requestData.put("id", id);
      String jsonInputString = objectMapper.writeValueAsString(requestData);

      try (OutputStream os = connection.getOutputStream()) {
        os.write(jsonInputString.getBytes("utf-8"));
      }

      // 응답 확인
      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
          StringBuilder response = new StringBuilder();
          String line;
          while ((line = reader.readLine()) != null) {
            response.append(line);
          }

          // JSON 응답 파싱
          Map<String, Object> responseMap = objectMapper.readValue(response.toString(), Map.class);
          if ("success".equals(responseMap.get("status"))) {
            Map<String, Object> book = (Map<String, Object>) responseMap.get("data");

            // 데이터 추출
            String bookId = String.valueOf(book.get("id"));
            String title = (String) book.get("title");
            String author = (String) book.get("author");
            String publisher = (String) book.get("publisher");
            String cover = (String) book.get("cover");

            // 객체 생성 및 반환
            AladdinBookItem bookItem = new AladdinBookItem();
            bookItem.setItemId(bookId);
            bookItem.setTitle(title);
            bookItem.setAuthor(author);
            bookItem.setPublisher(publisher);
            bookItem.setCover(cover);

            return new JRIBookCard(bookItem, new Point(0, 0));
          } else {
            System.err.println("Error: " + responseMap.get("message"));
          }
        }
      } else {
        System.err.println("Server error. Response code: " + responseCode);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }

    return null; // 실패 시 null 반환
  }

  public static ArrayList<JRIConnectionInfo> getBookConnection() {
    String endpoint = BASE_URL + "getBookConnection";
    HttpURLConnection connection = null;

    try {
      // URL 객체 생성
      URL url = new URL(endpoint);
      connection = (HttpURLConnection) url.openConnection();

      // HTTP GET 설정
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Accept", "application/json");

      // HTTP 응답 코드 확인
      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        // 응답 데이터 읽기
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
          response.append(line);
        }
        reader.close();

        // JSON 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> results = objectMapper.readValue(response.toString(), List.class);

        // JRIConnectionInfo 객체로 변환
        ArrayList<JRIConnectionInfo> connections = new ArrayList<>();
        for (Map<String, Object> result : results) {
          String baseBookId = String.valueOf(result.get("baseBookId"));
          String targetBookId = String.valueOf(result.get("targetBookId"));
          int count = (Integer) result.get("count");
          connections.add(new JRIConnectionInfo(baseBookId, targetBookId, count));
        }

        return connections;
      } else {
        System.err.println("Failed to get book connections. Response code: " + responseCode);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }

    return new ArrayList<>(); // 실패 시 빈 리스트 반환
  }

  public static ArrayList<JRIBookCard> getConnectedBook(String bookId) {
    String endpoint = BASE_URL + "getConnectedBook";
    HttpURLConnection connection = null;
    ObjectMapper objectMapper = new ObjectMapper();

    ArrayList<JRIBookCard> connectedBooks = new ArrayList<>();

    try {
      URL url = new URL(endpoint);
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setDoOutput(true);

      // 요청 데이터
      Map<String, Object> requestData = new HashMap<>();
      requestData.put("bookId", bookId);
      String jsonInputString = objectMapper.writeValueAsString(requestData);

      // 데이터 전송
      try (OutputStream os = connection.getOutputStream()) {
        os.write(jsonInputString.getBytes("utf-8"));
      }

      // 응답 읽기
      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
          response.append(line);
        }
        reader.close();

        // JSON 파싱
        Map<String, Object> responseMap = objectMapper.readValue(response.toString(), Map.class);

        if ("success".equals(responseMap.get("status"))) {
          List<Map<String, Object>> dataList = (List<Map<String, Object>>) responseMap.get("data");

          for (Map<String, Object> book : dataList) {
            String id = String.valueOf(book.get("id"));
            String title = (String) book.get("title");
            String author = (String) book.get("author");
            String publisher = (String) book.get("publisher");
            String cover = (String) book.get("cover");

            // AladdinBookItem 생성
            AladdinBookItem bookItem = new AladdinBookItem();
            bookItem.setItemId(id);
            bookItem.setTitle(title);
            bookItem.setAuthor(author);
            bookItem.setPublisher(publisher);
            bookItem.setCover(cover);

            // JRIBookCard 추가
            connectedBooks.add(new JRIBookCard(bookItem, new Point(0, 0)));
          }
        }
      } else {
        System.err.println("Failed to fetch connected books. Response code: " + responseCode);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }

    return connectedBooks;
  }
}
