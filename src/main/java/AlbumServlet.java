import java.util.UUID;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/albums/*")
@MultipartConfig
public class AlbumServlet extends HttpServlet {

private final Map<String, AlbumResponse> albumData = new HashMap<>();
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

    // Perform a lookup for the album information based on the albumId
    AlbumResponse albumResponse = lookupAlbumById(albumId);

    if (albumResponse == null) {
      // Album not found, return a 404 response
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    // Return the album information as JSON
    response.setContentType("application/json");
    response.getWriter().write(albumResponse.toJson());
  }
  // Implement the logic to look up album information by ID (replace with your actual data source)
  private AlbumResponse lookupAlbumById(String albumId) {
    // Look up album information in the map based on albumId
    return albumData.get(albumId);
  }


@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
  try {
    // Retrieve the image part from the multipart/form-data request
    Part imagePart = request.getPart("image");

    // Get the size of the image in bytes
    long imageSizeBytes = imagePart.getSize();

    // Generate a unique album key (you can use UUID or any other method)
    String albumKey = generateUniqueAlbumKey();

    // Create an AlbumResponse object
    AlbumResponse albumResponse = new AlbumResponse(albumKey, imageSizeBytes);

    // Store the album information in the map using the albumKey as the ID
    albumData.put(albumKey, albumResponse);

    // Send the AlbumResponse as JSON
    response.setContentType("application/json");
    response.getWriter().write(albumResponse.toJson());
  } catch (Exception ex) {
    ex.printStackTrace();
    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
  }
}
  // Implement the logic to generate a unique album key (e.g., using UUID)
  private String generateUniqueAlbumKey() {
    return UUID.randomUUID().toString();
  }
}
