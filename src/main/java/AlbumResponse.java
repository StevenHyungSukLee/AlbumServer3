import com.google.gson.Gson;

public class AlbumResponse {
  private String albumKey;
  private long imageSizeBytes;

  public AlbumResponse() {
    // Default constructor
  }

  public AlbumResponse(String albumKey, long imageSizeBytes) {
    this.albumKey = albumKey;
    this.imageSizeBytes = imageSizeBytes;
  }

  public String getAlbumKey() {
    return albumKey;
  }

  public void setAlbumKey(String albumKey) {
    this.albumKey = albumKey;
  }

  public long getImageSizeBytes() {
    return imageSizeBytes;
  }

  public void setImageSizeBytes(long imageSizeBytes) {
    this.imageSizeBytes = imageSizeBytes;
  }

  // Method to serialize the object to JSON
  public String toJson() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }
}
