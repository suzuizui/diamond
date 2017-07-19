import com.google.common.collect.Lists;
import com.opc.freshness.Application;
import com.opc.freshness.api.model.dto.BatchDto;
import com.opc.freshness.api.model.dto.SkuDto;
import com.opc.freshness.api.model.dto.SkuKindDto;
import com.opc.freshness.controller.ShopController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Test
    public void getStaff() {
        String url = "http://localhost:" + port + "/api/shop/staff/{cardCode}/v1";
        String result = template.getForObject(url, String.class, "111");
        System.out.println(result);
    }

    @Test
    public void getSkuList() {
        String url = "http://localhost:" + port + "/api/shop/sku/list/v1?shopId=1&categoryId=1";
        String result = template.getForObject(url, String.class);
        System.out.println(result);
    }

    @Test
    public void skuByBarCode() {
        String url = "http://localhost:" + port + "/api/shop/sku/{barCode}/v1?shopId=1";
        String result = template.getForObject(url, String.class, "11");
        System.out.println(result);
    }

    @Test
    public void getAbortList() {
        String url = "http://localhost:" + port + "/api/shop/expire/list/v1?shopId=1";
        String result = template.getForObject(url, String.class);
        System.out.println(result);
    }

    @Test
    public void getDetailPage() {
        String url = "http://localhost:" + port + "//api/shop/log/list/v1?shopId=1&type=1&pageNo=1&pageSize=10";
        String result = template.getForObject(url, String.class);
        System.out.println(result);
    }

    @Test
    public void setkind() {
        String url = "http://localhost:" + port + "/api/shop/sku/setkinds/v1";

        SkuKindDto dto = new SkuKindDto();
        dto.setShopId(1);
        dto.setSkuId(1);
        dto.setCategoryIds(Stream.of(1, 2, 3).collect(Collectors.toList()));

        String result = template.postForObject(url, dto, String.class);
        System.out.println(result);
    }

    @Test
    public void make() {
        String url = "http://localhost:" + port + "/api/shop/sku/option/v1";

        BatchDto dto = new BatchDto();
        dto.setShopId(1);
        dto.setOption(ShopController.OperateType.MAKE.getValue());
        dto.setCategoryId(1);
        dto.setOperator("张三");
        dto.setCreateTime(new Date());
        dto.setUnit("个");
        dto.setDegree(10);
        dto.setTag("蓝");

        SkuDto sku = new SkuDto();
        sku.setSkuId(1);
        sku.setQuantity(10);
        dto.setSkuList(Lists.newArrayList(sku));

        String result = template.postForObject(url, dto, String.class);

        System.out.println(result);
    }

    @Test
    public void loss() {
        String url = "http://localhost:" + port + "/api/shop/sku/option/v1";

        BatchDto dto = new BatchDto();
        dto.setShopId(1);
        dto.setBatchId(1);
        dto.setOption(ShopController.OperateType.LOSS.getValue());
        dto.setOperator("张三");
        dto.setCreateTime(new Date());

        SkuDto sku = new SkuDto();
        sku.setSkuId(1);
        sku.setQuantity(1);

        dto.setSkuList(Lists.newArrayList(sku));

        String result = template.postForObject(url, dto, String.class);

        System.out.println(result);
    }

    @Test
    public void abort() {
        String url = "http://localhost:" + port + "/api/shop/sku/option/v1";

        BatchDto dto = new BatchDto();
        dto.setShopId(1);
        dto.setBatchId(1);
        dto.setOption(ShopController.OperateType.ABORT.getValue());
        dto.setOperator("张三");
        dto.setCreateTime(new Date());

        SkuDto sku = new SkuDto();
        sku.setSkuId(1);
        sku.setQuantity(5);

        dto.setSkuList(Lists.newArrayList(sku));

        String result = template.postForObject(url, dto, String.class);

        System.out.println(result);
    }
}
