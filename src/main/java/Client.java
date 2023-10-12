import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;

public class Client {

  public static void main(String[] args) {
    if (args.length != 4) {
      System.out.println("Usage: Client threadGroupSize numThreadGroups delay IPAddr");
      return;
    }

    int threadGroupSize = Integer.parseInt(args[0]);
    int numThreadGroups = Integer.parseInt(args[1]);
    int delay = Integer.parseInt(args[2]);
    String serverUri = args[3];

    long startTime, endTime;
    HttpClient httpClient = HttpClients.createDefault();

    try {
      startTime = System.currentTimeMillis();

      for (int group = 1; group <= numThreadGroups; group++) {
        Thread[] threads = new Thread[threadGroupSize];
        for (int i = 0; i < threadGroupSize; i++) {
          threads[i] = new Thread(() -> {
            for (int j = 0; j < 100; j++) {
              performPostRequest(httpClient, serverUri);
              performGetRequest(httpClient, serverUri);
            }
          });
          threads[i].start();
        }

        for (Thread thread : threads) {
          thread.join();
        }

        Thread.sleep(delay * 1000); // Convert delay from seconds to milliseconds
      }

      endTime = System.currentTimeMillis();

      long wallTime = (endTime - startTime) / 1000; // Convert to seconds
      long totalRequests = numThreadGroups * threadGroupSize * 1000;
      double throughput = (double) totalRequests / wallTime;

      System.out.println("Wall Time: " + wallTime + " seconds");
      System.out.println("Throughput: " + throughput + " requests/second");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void performPostRequest(HttpClient httpClient, String serverUri) {
    HttpPost postRequest = new HttpPost(serverUri + "/albums");
    // Configure the request if needed
    try {
      HttpResponse response = httpClient.execute(postRequest);
      // Handle the response
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void performGetRequest(HttpClient httpClient, String serverUri) {
    HttpGet getRequest = new HttpGet(serverUri + "/albums/:id");
    // Configure the request if needed
    try {
      HttpResponse response = httpClient.execute(getRequest);
      // Handle the response
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

