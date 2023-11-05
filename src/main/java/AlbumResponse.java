import com.google.gson.Gson;

public class AlbumResponse {
  private String albumId;
  private long imageSizeBytes;

  public AlbumResponse() {
    // Default constructor
  }

  public AlbumResponse(String albumId, long imageSizeBytes) {
    this.albumId = albumId;
    this.imageSizeBytes = imageSizeBytes;
  }

  public String getAlbumKey() {
    return albumId;
  }

  public void setAlbumKey(String albumKey) {
    this.albumId = albumKey;
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
