import javax.servlet.http.Part;
import com.google.gson.Gson;


// AlbumRequest.java
public class AlbumRequest {
  private String artist;
  private String title;
  private String year;
  private Part imageFile; // Part for handling the image file

  // Constructors, getters, and setters

  public AlbumRequest() {
    // Default constructor
  }



  public AlbumRequest(String artist, String title, String year, Part imageFile) {
    this.artist = artist;
    this.title = title;
    this.year = year;
    this.imageFile = imageFile;
  }
  public static String toJson(AlbumRequest albumRequest) {
    Gson gson = new Gson();
    return gson.toJson(albumRequest);
  }
  public static AlbumRequest fromJson(String json) {
    Gson gson = new Gson();
    return gson.fromJson(json, AlbumRequest.class);
  }
  public String getArtist() {
    return artist;
  }

  public void setArtist(String artist) {
    this.artist = artist;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public Part getImageFile() {
    return imageFile;
  }

  public void setImageFile(Part imageFile) {
    this.imageFile = imageFile;
  }
}
