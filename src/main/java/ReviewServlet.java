import com.rabbitmq.client.Channel;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/review/*")
public class ReviewServlet extends HttpServlet {
  private RabbitMQPool rabbitMQPool;

  @Override
  public void init() throws ServletException {
    super.init();
    // Retrieve the RabbitMQPool from the servlet context
    rabbitMQPool = (RabbitMQPool) getServletContext().getAttribute("RABBITMQ_POOL");
    if (rabbitMQPool == null) {
      throw new ServletException("RabbitMQ pool not found in servlet context.");
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String pathInfo = request.getPathInfo(); // "/review<reviewType><albumId>"
    if (pathInfo == null || pathInfo.equals("/")) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request path.");
      return;
    }

    String[] pathParts = pathInfo.split("/");
    if (pathParts.length != 3) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request path.");
      return;
    }

    String reviewType = pathParts[1];
    String albumId = pathParts[2];

    if (reviewType == null || !(reviewType.equals("like") || reviewType.equals("dislike"))) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Review type must be 'like' or 'dislike'.");
      return;
    }

    if (albumId == null || albumId.trim().isEmpty()) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Album ID is required.");
      return;
    }
    String message = reviewType + ":" + albumId;

    // Try to get the connection and publish the message
    Channel channel = null;
    try {
      channel = rabbitMQPool.borrowChannel(); // Borrow a channel from the pool
      channel.exchangeDeclare("REVIEW_EXCHANGE", "direct");
      channel.queueDeclare("reviews_queue",true,false,false,null);
      channel.queueBind("reviews_queue","REVIEW_EXCHANGE","");
      channel.basicPublish("REVIEW_EXCHANGE", "", null, message.getBytes(StandardCharsets.UTF_8));
      response.setStatus(HttpServletResponse.SC_ACCEPTED);
    } catch (Exception e) {
      e.printStackTrace();
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    } finally {
      if (channel != null && channel.isOpen()) {
        try {
            rabbitMQPool.returnChannel(channel);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

    response.setStatus(HttpServletResponse.SC_ACCEPTED);
  }

  @Override
  public void destroy() {
    super.destroy();
    try {
      rabbitMQPool.close(); // Close the pool and its resources
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}