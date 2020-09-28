import org.junit.jupiter.api.Test;
import org.coqur.mserver.struct.RequestHeader;

public class RequestHeaderTest {
    @Test
    public void gettingTest() {
        String header = "GET /chatware/chatroom.php HTTP/1.1\r\n" +
                "Host: www.myfavoritewebsite.com:8080\r\n" +
                "Server: mserver v0.1.0\r\n\r\n";
        RequestHeader rh = new RequestHeader(header);

        assert rh.getFirstLine().equals("GET /chatware/chatroom.php HTTP/1.1\r\n");
        assert rh.getMethod().equals("GET");
        assert rh.getRequestUri().equals("/chatware/chatroom.php");
        assert rh.getHttpVersion().equals("HTTP/1.1");

        assert rh.get("Host").equals("www.myfavoritewebsite.com:8080");
        assert rh.get("Server").equals("mserver v0.1.0");
    }

}
