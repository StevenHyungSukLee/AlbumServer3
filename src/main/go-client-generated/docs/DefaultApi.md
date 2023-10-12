# {{classname}}

All URIs are relative to *https://virtserver.swaggerhub.com/IGORTON/AlbumStore/1.0.0*

Method | HTTP request | Description
------------- | ------------- | -------------
[**GetAlbumByKey**](DefaultApi.md#GetAlbumByKey) | **Get** /albums/{albumID} | get album by key
[**NewAlbum**](DefaultApi.md#NewAlbum) | **Post** /albums | Returns the new key and size of an image in bytes.

# **GetAlbumByKey**
> AlbumInfo GetAlbumByKey(ctx, albumID)
get album by key

### Required Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
  **albumID** | **string**| path  parameter is album key to retrieve | 

### Return type

[**AlbumInfo**](albumInfo.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **NewAlbum**
> ImageMetaData NewAlbum(ctx, image, profile)
Returns the new key and size of an image in bytes.

### Required Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
  **image** | ***os.File*****os.File**|  | 
  **profile** | [**AlbumsProfile**](.md)|  | 

### Return type

[**ImageMetaData**](imageMetaData.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

