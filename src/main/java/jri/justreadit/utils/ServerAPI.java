package jri.justreadit.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jri.justreadit.JRIBookCard;
import jri.justreadit.JRIBookNoteInfo;
import jri.justreadit.utils.AladdinOpenAPI.AladdinBookItem;

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
}
