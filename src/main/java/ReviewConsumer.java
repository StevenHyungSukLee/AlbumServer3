import com.rabbitmq.client.*;

public class ReviewConsumer implements Runnable {
  private DynamoDBHandler dynamoDBHandler;
  private RabbitMQPool rabbitMQPool;

  String queueName = "reviews_queue";

  public ReviewConsumer(RabbitMQPool rabbitMQPool) {
    this.rabbitMQPool = rabbitMQPool;
    this.dynamoDBHandler = new DynamoDBHandler(); // Ensure DynamoDBHandler is initialized here or injected
  }

  @Override
  public void run() {
    Channel channel = null;
    try {
      channel = rabbitMQPool.borrowChannel(); // Borrow a channel from the pool
      String queueName = "reviews_queue";

      // The following setup should be moved to an initialization phase if not dynamic
      channel.exchangeDeclare("REVIEW_EXCHANGE", "direct", false);
      channel.queueDeclare(queueName, true, false, false, null);
      channel.queueBind(queueName, "REVIEW_EXCHANGE", "");

      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
        // Process message and update database
        String[] messageInfo = message.split(":");
        String reviewType = messageInfo[0];
        String albumId = messageInfo[1];

        dynamoDBHandler.updateReviewCount("Albums", albumId, reviewType);

      };
      channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (channel != null && channel.isOpen()) {
        try {
          rabbitMQPool.returnChannel(channel); // Return the channel to the pool
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

}
