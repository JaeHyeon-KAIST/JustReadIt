package jri.justreadit.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
}
