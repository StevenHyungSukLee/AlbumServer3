import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DynamoDBHandler {
  private final DynamoDbClient dynamoDb;

  private final String AWS_ACCESS_KEY_ID = "ASIAVJXA7U4G2OI7MDFY";
  private final String AWS_SECRET_ACCESS_KEY = "ofRaUSX7RYoK70yjyfIBVAcP6VjVOoMuuDwtMjUO";
  private final String AWS_SESSION_TOKEN = "FwoGZXIvYXdzEPz//////////wEaDERTDVDiykuJhNKXcSLMAf4xfUn4sHJ2WuV9gCxgsa/E9yKFw62wFk8n1ZOgwNjTX5stNPo5l6vptQHzYrNHhbM81Jpqte2jU94eEsSeGyzQmTPggawg0Ls4DUnixpa/HYTlObVVO5CaJgTOdnlSTWs2g32oCID3P/l0QG9tranceTeztxVVxcg5KMxbxyY8djfzM9JO/4oY8CVjKdo7fVIc4t/o9gUCNVAqg+u32CoEgc9RsD2b43dKUJTNgWaaR7uvxaUrGxhK4pdn0/hh9/mZ8oIepF7A0SQTsCjH57SrBjItobt78B06GHw1u5wlCtQ/7aMPyorrMfO0Orsx6DT7r/GvJfo5OW/nEFblE9nq";

  public DynamoDBHandler() {
    this.dynamoDb = DynamoDbClient.builder()
        .region(Region.US_WEST_2)  // Adjust as per your region
        .endpointDiscoveryEnabled(false)
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsSessionCredentials.create(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY, AWS_SESSION_TOKEN)
        ))
        .build();
  }

  public void insertItem(String tableName, Map<String, AttributeValue> item) {
    PutItemRequest request = PutItemRequest.builder()
        .tableName(tableName)
        .item(item)
        .build();

    dynamoDb.putItem(request);
  }


  public void updateReviewCount(String tableName, String albumId, String reviewType) {
    // Determine the attribute to update based on the review type
    String attributeName = reviewType.equals("like") ? "likesCount" : "dislikesCount";
    String updateExpression = "SET " + attributeName + " = if_not_exists(" + attributeName + ", :start) + :inc";

    Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
    expressionAttributeValues.put(":inc", AttributeValue.builder().n("1").build()); // Increment by 1
    expressionAttributeValues.put(":start", AttributeValue.builder().n("0").build()); // Start value if the attribute does not exist

    UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
        .tableName(tableName)
        .key(Collections.singletonMap("albumId", AttributeValue.builder().s(albumId).build()))
        .updateExpression(updateExpression)
        .expressionAttributeValues(expressionAttributeValues)
        .returnValues(ReturnValue.UPDATED_NEW) // Optionally return updated values
        .build();

    dynamoDb.updateItem(updateItemRequest);
  }


  public Map<String, AttributeValue> getItem(String tableName, Map<String, AttributeValue> key) {
    GetItemRequest request = GetItemRequest.builder()
        .tableName(tableName)
        .key(key)
        .build();

    GetItemResponse response = dynamoDb.getItem(request);
    return response.item();
  }

}
