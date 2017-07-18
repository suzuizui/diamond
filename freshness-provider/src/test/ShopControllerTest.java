import com.opc.freshness.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/18
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
//@WebAppConfiguration // 使用@WebIntegrationTest注解需要将@WebAppConfiguration注释掉
@WebIntegrationTest("server.port:0")// 使用0表示端口号随机，也可以具体指定如8888这样的固定端口
public class ShopControllerTest {
    TestRestTemplate template = new TestRestTemplate();
    @Value("${local.server.port}")// 注入端口号
    private int port;

    @Test
    public void postitionByDeviceId() {
        String url = "http://localhost:" + port + "/api/shop/position/v1?deviceId=123";
        String result = template.getForObject(url, String.class);
        System.out.println(result);
    }
}
