import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class RabbitMQPool {
  private final BlockingQueue<Channel> channelPool;
  private final Connection connection;
  private final int maxPoolSize;
  private final AtomicInteger currentPoolSize = new AtomicInteger(0);

  public RabbitMQPool(int maxPoolSize) throws Exception {
    this.maxPoolSize = maxPoolSize;
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    connection = factory.newConnection();
    channelPool = new LinkedBlockingQueue<>();
  }

  public Channel borrowChannel() throws Exception {
    synchronized (this) {
      Channel channel;
      if (!channelPool.isEmpty() || currentPoolSize.get() >= maxPoolSize) {
        channel = channelPool.take();
      } else {
        channel = createChannel();
        currentPoolSize.incrementAndGet();
      }
      return channel;
    }
  }

  public void returnChannel(Channel channel) throws Exception {
    synchronized (this) {
      if (channel != null && channel.isOpen()) {
        channelPool.put(channel);
      } else {
        currentPoolSize.decrementAndGet();
        if (currentPoolSize.get() < maxPoolSize) {
          channelPool.put(createChannel());
          currentPoolSize.incrementAndGet();
        }
      }
    }
  }

  private Channel createChannel() throws Exception {
    return connection.createChannel();
  }

  public void close() throws Exception {
    for (Channel channel : channelPool) {
      if (channel != null && channel.isOpen()) {
        channel.close();
      }
    }
    connection.close();
  }
}

