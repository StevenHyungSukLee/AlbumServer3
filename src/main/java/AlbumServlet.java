import com.google.gson.Gson;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@WebServlet("/albums/*")
@MultipartConfig(fileSizeThreshold = 1024*1024*10, maxFileSize = 1024*1024*50, maxRequestSize = 1024*1024*100)

public class AlbumServlet extends HttpServlet {

  private final DynamoDBHandler dynamoDBHandler = new DynamoDBHandler();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Extract the album ID from the request URL
    String[] pathInfo = request.getPathInfo().split("/");
    if (pathInfo.length != 2) {
      // Invalid URL, return an error response
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    String albumId = pathInfo[1];

    Map<String, AttributeValue> keyToGet = Map.of("albumId", AttributeValue.builder().s(albumId).build());
    Map<String, AttributeValue> albumInfo = dynamoDBHandler.getItem("Albums", keyToGet);

    if (albumInfo == null || albumInfo.isEmpty()) {
      // Album not found, return a 404 response
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    String albumJson = convertMapToJson(albumInfo); // Implement this method based on your JSON utility library.

    response.setContentType("application/json");
    response.getWriter().write(albumJson);

  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      Part imagePart = request.getPart("image");
      long imageSizeBytes = imagePart.getSize();

      String artist = request.getParameter("artist");
      String title = request.getParameter("title");
      String year = request.getParameter("year");
      String albumId = generateUniqueAlbumKey();

      Map<String, AttributeValue> newItem = new HashMap<>();
      newItem.put("albumId", AttributeValue.builder().s(albumId).build());
      // Add other attributes for artist, title, year, etc.
      newItem.put("artist", AttributeValue.builder().s(artist).build());
      newItem.put("title", AttributeValue.builder().s(title).build());
      newItem.put("year", AttributeValue.builder().s(year).build());
      newItem.put("imageSize", AttributeValue.builder().n(String.valueOf(imageSizeBytes)).build());

      // Insert the item into DynamoDB
      dynamoDBHandler.insertItem("Albums", newItem);

      AlbumResponse albumResponse = new AlbumResponse(albumId, imageSizeBytes);
      response.setCharacterEncoding("UTF-8");
      response.setContentType("application/json");
      response.getWriter().write(albumResponse.toJson());

    } catch (Exception ex) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      ex.printStackTrace();
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
  private String generateUniqueAlbumKey() {
    // Your implementation to generate a unique album key.
    return UUID.randomUUID().toString();
  }

  // Helper method to convert a map to JSON using Gson
  private String convertMapToJson(Map<String, AttributeValue> data) {
    Gson gson = new Gson();
    // Convert the DynamoDB Attributes to a Map of String to Object to handle different types of AttributeValues
    Map<String, Object> simpleMap = new HashMap<>();
    for (String key : data.keySet()) {
      AttributeValue value = data.get(key);
      // DynamoDB stores numbers as Strings, so we check and convert them to actual numbers
      if (value.n() != null) {
        try {
          // Try to parse as an integer first, then as a long if the integer fails, and as a double if long fails.
          simpleMap.put(key, Integer.parseInt(value.n()));
        } catch (NumberFormatException e) {
          try {
            simpleMap.put(key, Long.parseLong(value.n()));
          } catch (NumberFormatException le) {
            simpleMap.put(key, Double.parseDouble(value.n()));
          }
        }
      } else if (value.s() != null) {
        simpleMap.put(key, value.s());
      } else if (value.bool() != null) {
        simpleMap.put(key, value.bool());
      } else if (value.m() != null) {
        // This is a map type, so we would recursively convert it
        simpleMap.put(key, convertMapToJson(value.m()));
      } else if (value.l() != null) {
        // This is a list type, so we would handle it accordingly
        // Convert each AttributeValue in the list using the same logic as above
        simpleMap.put(key, value.l().stream().map(this::convertAttributeValue).collect(Collectors.toList()));
      }
      // Add additional else if blocks for other AttributeValue types as necessary
    }
    return gson.toJson(simpleMap);
  }

  // Helper method to handle AttributeValue conversion, used by convertMapToJson
  private Object convertAttributeValue(AttributeValue value) {
    // You can extend this method to handle different types of AttributeValues.
    if (value.n() != null) {
      try {
        return Integer.parseInt(value.n());
      } catch (NumberFormatException e) {
        try {
          return Long.parseLong(value.n());
        } catch (NumberFormatException le) {
          return Double.parseDouble(value.n());
        }
      }
    } else if (value.s() != null) {
      return value.s();
    } else if (value.bool() != null) {
      return value.bool();
    } else if (value.m() != null) {
      return convertMapToJson(value.m());
    } else if (value.l() != null) {
      return value.l().stream().map(this::convertAttributeValue).collect(Collectors.toList());
    }
    // Handle other types as necessary.
    return null;
  }
}