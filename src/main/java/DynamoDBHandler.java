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

  private final String AWS_ACCESS_KEY_ID = "ASIAVJXA7U4GQHRFFXJ2";
  private final String AWS_SECRET_ACCESS_KEY = "mH/tVPdH6OFXMA54VpsEICUpJ20F6buvG4E2PGYa";
  private final String AWS_SESSION_TOKEN = "FwoGZXIvYXdzEEQaDPNzWwfS9hPR4wOrDSLMAZ73Kn3BwoOCoP3pUPpQsTFC9lFPo8qoF5ci5q6P/x5gYga5aNPp/FYU2ds2znS8m1mozgrlciC0+hIH5cWytHRi/XB9jzJ8YjcpRNV+icFGTQXUqhmL+9G+GNOK+Q900zhuFWzg5tL5d1dAGVzU2O05M/6FTcVz8IM77JRtXLTsLqN59tq/5EY6KgIEkQ8YIpreUdV/5o3m5t7zCSiN66+qXEiCPn70Fe7HuqLngIzwqDeh/8dSvh3BIzk4LAfo3TWy8T0OrtZ9vPPyfyi3gpyqBjItjeIGnEayskbXtm0qwadhjRXOlXF2RdYHrQYi3F1X4FrU2swZzuVYvdD2Rj53";

  public DynamoDBHandler() {
    this.dynamoDb = DynamoDbClient.builder()
        .region(Region.US_WEST_2)  // Adjust as per your region
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

  public Map<String, AttributeValue> getItem(String tableName, Map<String, AttributeValue> key) {
    GetItemRequest request = GetItemRequest.builder()
        .tableName(tableName)
        .key(key)
        .build();

    GetItemResponse response = dynamoDb.getItem(request);
    return response.item();
  }

}
