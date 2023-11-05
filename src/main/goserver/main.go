package main

import (
	"github.com/gin-gonic/gin"
	"io"
	"net/http"
	"sync"
)

type AlbumResponse struct {
	AlbumKey  string `json:"albumKey"`
	ImageSize int64  `json:"imageSize"`
}

type albumInfo struct {
	Artist string `json:"artist"`
	Title  string `json:"title"`
	Year   string `json:"year"`
}

type errorMsg struct {
	msg string `json:"error message"`
}

var (
	albumData = make(map[string]albumInfo)
	mutex sync.RWMutex // A mutex to synchronize access to the albumData map.
)

func generateUniqueAlbumKey() string {
	return "123"
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
	var albumInfo albumInfo

	if err := c.Bind(&albumInfo); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error message": "Invalid album info"})
		return
	}
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
	mutex.Lock()
	albumKey := generateUniqueAlbumKey()
	albumData[albumKey] = albumInfo
	mutex.Unlock()

	// Store the album data by album key
	//albumData[albumKey] = albumInfo

	// Create an AlbumResponse object
	albumResponse := AlbumResponse{AlbumKey: albumKey, ImageSize: imageSizeBytes}

	// Send the AlbumResponse as JSON
	c.JSON(http.StatusCreated, albumResponse)
}

// getAlbumByID retrieves an album by its ID.
func getAlbumByID(c *gin.Context) {
	// Get the album ID from the URL parameter
	albumID := c.Param("id")

	// Check if the album exists in the data store
	mutex.RLock()
	albumInfo, exists := albumData[albumID]
	mutex.RUnlock()
	if !exists {
		c.JSON(http.StatusNotFound, gin.H{"error message": "Album not found"})
		return
	}

	// Return the album info as JSON
	c.JSON(http.StatusOK, albumInfo)
}
