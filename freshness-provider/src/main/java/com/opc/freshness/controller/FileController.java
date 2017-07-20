package com.opc.freshness.controller;

import com.opc.freshness.common.util.DateUtils;
import com.opc.freshness.domain.bo.SkuDetailBo;
import com.opc.freshness.domain.bo.SkuMakeBo;
import com.opc.freshness.domain.po.BatchPoExtras;
import com.opc.freshness.domain.po.KindPo;
import com.opc.freshness.service.KindService;
import com.wormpex.inf.wmq.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/18
 */
@Controller
public class FileController {
    private final static Logger logger = LoggerFactory.getLogger(FileController.class);

    private static final String DATE_FORMAT = "yyyyMMdd";
    private static final String DATE_FORMAT2 = "yyyy-MM-dd hh:mm";
    private static final String FILE_NAME = "制作统计";
    //表头
    private static final String[] HEADER = {"商品名", "商品编号", "制作数量", "报损数量", "废弃数量", "废弃率", "报损率"};
    private static final String[] DETAIL_HEADER = {"商品名", "商品编号", "制作人", "制作时间", "温度℃", "理论废弃时间", "实际废弃时间", "废弃个数", "废弃人"};
    @Resource
    private KindService kindService;

    /**
     * 导出制作统计列表
     *
     * @param shopId     卡号
     * @param date       查询日期
     * @param categoryId 品类Id
     * @return
     */
    @RequestMapping(value = "/api/export/make/v1", method = RequestMethod.GET)
    public void exportMakeExcel(HttpServletRequest request, HttpServletResponse response,
                                @RequestParam Integer shopId,
                                @RequestParam Integer categoryId,
                                @RequestParam Date date) throws IOException {
        logger.info("exportMakeExcel shopId:{} date:{}", shopId, date);
        //准备数据
        List<SkuMakeBo> boList = kindService.skuMakeInfoList(shopId, categoryId, date);
        KindPo po = kindService.selectByPrimaryKey(categoryId);
        //组装
        String codedFileName = URLEncoder.encode(po.getName() + FILE_NAME, "UTF-8") + DateUtils.format(date, DATE_FORMAT);

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("content-disposition", "attachment;filename=" + codedFileName + ".xls");
        try (
                OutputStream fout = response.getOutputStream()
        ) {
            // 产生工作簿对象
            HSSFWorkbook workbook = new HSSFWorkbook();
            //产生工作表对象
            HSSFSheet sheet = workbook.createSheet();
            //创建表头
            HSSFRow headRow = sheet.createRow(0);
            for (int i = 0; i < HEADER.length; i++) {
                headRow.createCell(i, CellType.STRING).setCellValue(HEADER[i]);
            }
            //填充数据
            for (int i = 0; i < boList.size(); i++) {
                SkuMakeBo makeBo = boList.get(0);
                HSSFRow row = sheet.createRow(i + 1);
                int j = 0;
                row.createCell(j++, CellType.STRING).setCellValue(makeBo.getSkuName());
                row.createCell(j++, CellType.NUMERIC).setCellValue(makeBo.getSkuId());
                row.createCell(j++, CellType.NUMERIC).setCellValue(makeBo.getMakeCount());
                row.createCell(j++, CellType.NUMERIC).setCellValue(makeBo.getLossCount());
                row.createCell(j++, CellType.NUMERIC).setCellValue(makeBo.getAbortCount());
                row.createCell(j++, CellType.STRING).setCellValue(makeBo.getAbortPercent());
                row.createCell(j, CellType.STRING).setCellValue(makeBo.getLossPercent());
            }
            //输出
            workbook.write(fout);

        } catch (Exception e) {
            logger.error("exportMakeExcel error..", e);
        }
    }

    /**
     * 导出明细列表
     *
     * @param shopId     卡号
     * @param date       查询日期
     * @param categoryId 品类Id
     * @return
     */
    @RequestMapping(value = "/api/export/detail/v1", method = RequestMethod.GET)
    public void exportDetailExcel(HttpServletRequest request, HttpServletResponse response,
                                  @RequestParam Integer shopId,
                                  @RequestParam Integer categoryId,
                                  @RequestParam Date date) throws IOException {
        logger.info("exportDetailExcel shopId:{} categoryId:{} date:{}", shopId, categoryId, date);
        //准备数据
        List<SkuDetailBo> boList = kindService.skuDetailInfoList(shopId, categoryId, date,null);
        KindPo po = kindService.selectByPrimaryKey(categoryId);
        //组装
        String codedFileName = URLEncoder.encode(po.getName() + FILE_NAME, "UTF-8") + DateUtils.format(date, DATE_FORMAT);

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("content-disposition", "attachment;filename=" + codedFileName + ".xls");
        try (
                OutputStream fout = response.getOutputStream()
        ) {
            // 产生工作簿对象
            HSSFWorkbook workbook = new HSSFWorkbook();
            //产生工作表对象
            HSSFSheet sheet = workbook.createSheet();
            //创建表头
            HSSFRow headRow = sheet.createRow(0);
            for (int i = 0; i < DETAIL_HEADER.length; i++) {
                headRow.createCell(i, CellType.STRING).setCellValue(HEADER[i]);
            }
            //填充数据
            for (int i = 0; i < boList.size(); i++) {
                SkuDetailBo detailBo = boList.get(0);
                HSSFRow row = sheet.createRow(i + 1);
                int j = 0;
                row.createCell(j++, CellType.STRING).setCellValue(detailBo.getSkuName());
                row.createCell(j++, CellType.NUMERIC).setCellValue(detailBo.getSkuId());
                row.createCell(j++, CellType.STRING).setCellValue(detailBo.getMaker());
                row.createCell(j++, CellType.STRING).setCellValue(DateUtils.format(detailBo.getCreateTime(), DATE_FORMAT2));
                //设置温度
                if (j++ > 0 && StringUtils.isNotBlank(detailBo.getExtras())) {
                    BatchPoExtras extras = JsonUtils.toBean(detailBo.getExtras(), BatchPoExtras.class);
                    if (extras.getDegree() != null)
                        row.createCell(j, CellType.NUMERIC).setCellValue(extras.getDegree());
                }
                //设置理论废弃时间
                row.createCell(j++, CellType.STRING).setCellValue(DateUtils.format(detailBo.getExpiredTime(), DATE_FORMAT2));
                //设置实际废弃时间
                if (j++ > 0 && detailBo.getExpiredRealTime() != null) {
                    row.createCell(j, CellType.STRING).setCellValue(DateUtils.format(detailBo.getExpiredRealTime(), DATE_FORMAT2));
                }
                //设置废弃个数
                if (j++ > 0 && detailBo.getExpiredCount() != null) {
                    row.createCell(j++, CellType.NUMERIC).setCellValue(detailBo.getExpiredCount());
                }
                //设置废弃人
                if (StringUtils.isNotBlank(detailBo.getAborter())) {
                    row.createCell(j, CellType.STRING).setCellValue(detailBo.getAborter());
                }
            }
            //输出
            workbook.write(fout);

        } catch (Exception e) {
            logger.error("exportMakeExcel error..", e);
        }
    }
}
