package main

import (
	"github.com/gin-gonic/gin"
	"io"
	"net/http"
	"github.com/google/uuid"
)

type AlbumResponse struct {
	AlbumKey     string `json:"albumKey"`
	ImageSize    int64  `json:"imageSize"`
}

type imageMetaData struct {
	albumID string `json:"albumID"`
	imageSize string `json:"imageSize"`
}

type albumInfo struct {
	Artist string `json:"artist"`
	Title string `json:"title"`
	Year string `json:"year"`
}

type errorMsg struct {
	msg string `json:"error message"`
}
var albumData = make(map[string]albumInfo)

func generateUniqueAlbumKey() string {
	return uuid.New().String()
}

func main() {
	router := gin.Default()
	router.GET("/albums/:id", getAlbumByID)
	router.POST("/albums", postAlbums)

	router.Run(":8080")
}

// postAlbum creates a new album with a unique ID and the provided data.
func postAlbums(c *gin.Context) {
	// Retrieve the image from the form data
	var albumInfo  albumInfo

	file, _, err := c.Request.FormFile("image")
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error message": err.Error()})
		return
	}
	defer file.Close()

	// Read the image to calculate its size
	imageSizeBytes, err := io.Copy(io.Discard, file)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error message": err.Error()})
		return
	}

	// Generate a unique album key
	albumKey := generateUniqueAlbumKey()

	//// Create an AlbumInfo object (you can customize this based on your data)
	//albumInfo := albumInfo{
	//	Artist: albumInfo .Artist,
	//	Title:  albumInfo .Title,
	//	Year:   albumInfo .Year,
	//}

	// Store the album data by album key
	albumData[albumKey] = albumInfo

	// Create an AlbumResponse object
	albumResponse := AlbumResponse{AlbumKey: albumKey, ImageSize: imageSizeBytes}

	// Send the AlbumResponse as JSON
	c.JSON(http.StatusOK, albumResponse)
}

// getAlbumByID retrieves an album by its ID.
func getAlbumByID(c *gin.Context) {
	// Get the album ID from the URL parameter
	albumID := c.Param("id")

	// Check if the album exists in the data store
	albumInfo, exists := albumData[albumID]
	if !exists {
		c.JSON(http.StatusNotFound, gin.H{"error message": "Album not found"})
		return
	}

	// Return the album info as JSON
	c.JSON(http.StatusOK, albumInfo)
}