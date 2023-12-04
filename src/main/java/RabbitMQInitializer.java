import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class RabbitMQInitializer implements ServletContextListener {
  private RabbitMQPool rabbitMQPool;

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    try {
      int maxPoolSize = 200; // Set the max pool size according to your needs
      rabbitMQPool = new RabbitMQPool(maxPoolSize); // Initialize the pool with the max size
      sce.getServletContext().setAttribute("RABBITMQ_POOL", rabbitMQPool);

      int numConsumers = 2; // The number of consumer threads to start
      for (int i = 0; i < numConsumers; i++) {
        Thread consumerThread = new Thread(new ReviewConsumer(rabbitMQPool));
        consumerThread.start();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    try {
      if (rabbitMQPool != null) {
        rabbitMQPool.close(); // Close the pool and its resources
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static RabbitMQPool getRabbitMQPool(ServletContextEvent sce) {
    return (RabbitMQPool) sce.getServletContext().getAttribute("RABBITMQ_POOL");
  }
}