package com.opc.freshness.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.opc.freshness.api.model.dto.BatchDto;
import com.opc.freshness.api.model.dto.SkuDto;
import com.opc.freshness.api.model.dto.SkuKindDto;
import com.wormpex.api.json.JsonUtil;
import com.wormpex.inf.wmq.utils.JsonUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/18
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = Application.class)
//@WebAppConfiguration // 使用@WebIntegrationTest注解需要将@WebAppConfiguration注释掉
//@WebIntegrationTest("server.port:0")// 使用0表示端口号随机，也可以具体指定如8888这样的固定端口
public class ShopControllerTest {
    TestRestTemplate template = new TestRestTemplate();
    //    @Value("${local.server.port}")// 注入端口号
    private int port = 8080;

    @Test
    public void postitionByDeviceId() {
        String url = "http://localhost:" + port + "/api/shop/position/v1?deviceId=535d8d57-81b4-3a05-85a3-0e1abb179096";
        String result = template.getForObject(url, String.class);
        System.out.println(xml2JSON(result));
    }

    @Test
    public void getStaff() {
        String url = "http://localhost:" + port + "/api/shop/staff/{cardCode}/v1";
        String result = template.getForObject(url, String.class, "1");
        System.out.println(xml2JSON(result));
    }

    @Test
    public void getSkuList() {
        String url = "http://localhost:" + port + "/api/shop/sku/list/v1?shopId=1&categoryId=1";
        String result = template.getForObject(url, String.class);
        System.out.println(xml2JSON(result));
    }

    @Test
    public void skuByBarCode() {
        String url = "http://localhost:" + port + "/api/shop/sku/{barCode}/v1?shopId=1";
        String result = template.getForObject(url, String.class, "6914068019529");
        System.out.println(xml2JSON(result));
    }

    @Test
    public void getAbortList() {
        String url = "http://localhost:" + port + "/api/shop/expire/list/v1?shopId=1";
        String result = template.getForObject(url, String.class);
        System.out.println(xml2JSON(result));
    }

    @Test
    public void getDetailPage() {
        String url = "http://localhost:" + port + "//api/shop/log/list/v1?shopId=1&type=1&pageNo=1&pageSize=10";
        String result = template.getForObject(url, String.class);
        System.out.println(xml2JSON(result));
    }

    @Test
    public void setkind() {
        String url = "http://localhost:" + port + "/api/shop/sku/setkinds/v1";

        SkuKindDto dto = new SkuKindDto();
        dto.setShopId(1);
        dto.setSkuId(30);
        dto.setCategoryIds(Stream.of(1,2,3,4, 5, 6).collect(Collectors.toList()));

        String result = template.postForObject(url, dto, String.class);
        Assert.assertEquals( xml2JSON(result),"{\"Success\":{\"data\":[\"true\"],\"ret\":[\"true\"]}}");
    }

    @Test
    public void make() {
        String url = "http://localhost:" + port + "/api/shop/sku/option/v1";

        BatchDto dto = new BatchDto();
        dto.setShopId(1);
        dto.setOption(ShopController.OperateType.MAKE.getValue());
        dto.setCategoryId(2);
        dto.setOperator("张三");
        dto.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        dto.setUnit("个");
        dto.setDegree(10);
        dto.setTag("蓝");

        SkuDto sku = new SkuDto();
        sku.setSkuId(2);
        sku.setQuantity(10);
        dto.setSkuList(Lists.newArrayList(sku));
        System.out.println(JsonUtils.toJsonString(dto));
        String result = template.postForObject(url, dto, String.class);

        System.out.println(xml2JSON(result));
    }

    @Test
    public void loss() {
        String url = "http://localhost:" + port + "/api/shop/sku/option/v1";

        BatchDto dto = new BatchDto();
        dto.setShopId(1);
        dto.setBatchId(34);
        dto.setOption(ShopController.OperateType.LOSS.getValue());
        dto.setOperator("张三");
        dto.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        SkuDto sku = new SkuDto();
        sku.setSkuId(6);
        sku.setQuantity(2);

        dto.setSkuList(Lists.newArrayList(sku));

        String result = template.postForObject(url, dto, String.class);

        System.out.println(xml2JSON(result));
    }

    @Test
    public void abort() {
        String url = "http://localhost:" + port + "/api/shop/sku/option/v1";

        BatchDto dto = new BatchDto();
        dto.setShopId(1);
        dto.setBatchId(3);
        dto.setOption(ShopController.OperateType.ABORT.getValue());
        dto.setOperator("张三");
        dto.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        SkuDto sku = new SkuDto();
        sku.setSkuId(2);
        sku.setQuantity(5);

        dto.setSkuList(Lists.newArrayList(sku));

        String result = template.postForObject(url, dto, String.class);

        System.out.println(xml2JSON(result));
    }
    @Test
    public void sellOut() {
        String url = "http://localhost:" + port + "/api/shop/sku/option/v1";

        BatchDto dto = new BatchDto();
        dto.setShopId(1);
        dto.setBatchId(5);
        dto.setOption(ShopController.OperateType.SELLOUT.getValue());
        dto.setOperator("张三");
        dto.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        String result = template.postForObject(url, dto, String.class);

        System.out.println(xml2JSON(result));
    }
    /**
     * 转换一个xml格式的字符串到json格式
     *
     * @param xml xml格式的字符串
     * @return 成功返回json 格式的字符串;失败反回null
     */
    @SuppressWarnings("unchecked")
    private static String xml2JSON(String xml) {
        JSONObject obj = new JSONObject();
        try {
            InputStream is = new ByteArrayInputStream(xml.getBytes("utf-8"));
            SAXBuilder sb = new SAXBuilder();
            Document doc = sb.build(is);
            Element root = doc.getRootElement();
            obj.put(root.getName(), iterateElement(root));
            return obj.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 一个迭代方法
     *
     * @param element : org.jdom.Element
     * @return java.util.Map 实例
     */
    @SuppressWarnings("unchecked")
    private static Map iterateElement(Element element) {
        List jiedian = element.getChildren();
        Element et = null;
        Map obj = new HashMap();
        List list = null;
        for (int i = 0; i < jiedian.size(); i++) {
            list = new LinkedList();
            et = (Element) jiedian.get(i);
            if (et.getTextTrim().equals("")) {
                if (et.getChildren().size() == 0)
                    continue;
                if (obj.containsKey(et.getName())) {
                    list = (List) obj.get(et.getName());
                }
                list.add(iterateElement(et));
                obj.put(et.getName(), list);
            } else {
                if (obj.containsKey(et.getName())) {
                    list = (List) obj.get(et.getName());
                }
                list.add(et.getTextTrim());
                obj.put(et.getName(), list);
            }
        }
        return obj;
    }
}
