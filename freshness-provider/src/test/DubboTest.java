import com.opc.freshness.Application;
import com.wormpex.api.json.JsonUtil;
import com.wormpex.cvs.product.api.bean.BeeShop;
import com.wormpex.cvs.product.api.bean.BeeShopDevice;
import com.wormpex.cvs.product.api.remote.ProductRemote;
import com.wormpex.cvs.product.api.remote.ShopRemote;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/19
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
//@WebAppConfiguration // 使用@WebIntegrationTest注解需要将@WebAppConfiguration注释掉
@WebIntegrationTest("server.port:0")// 使用0表示端口号随机，也可以具体指定如8888这样的固定端口
public class DubboTest {
    @Resource
    private ShopRemote shopRemote;
    @Resource
    private ProductRemote productRemote;

    @Test
    public void getShops() {
        List<BeeShop> list = shopRemote.queryAllShop();
        System.out.println(JsonUtil.toJson(list));
        System.out.println("end");
    }

    @Test
    public void getDeviceList() {
        List<BeeShopDevice> list = shopRemote.queryLoggedDevice(1);
        System.out.println(JsonUtil.toJson(list));
        System.out.println("end");
    }

    @Test
    public void getShopByDevice() {
        BeeShop shop = shopRemote.queryByDevice("535d8d57-81b4-3a05-85a3-0e1abb179096");
        System.out.println(JsonUtil.toJson(shop));
        System.out.println("end");
    }
}
