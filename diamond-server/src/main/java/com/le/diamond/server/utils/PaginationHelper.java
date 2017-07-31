package com.le.diamond.server.utils;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.le.diamond.domain.Page;


/**
 * 分页辅助类
 * 
 * @author boyan
 * @date 2010-5-6
 * @param <E>
 */
public class PaginationHelper<E> {

    /**
     * 取分页
     * 
     * @param jt
     *            jdbcTemplate
     * @param sqlCountRows
     *            查询总数的SQL
     * @param sqlFetchRows
     *            查询数据的sql
     * @param args
     *            查询参数
     * @param pageNo
     *            页数
     * @param pageSize
     *            每页大小
     * @param rowMapper
     * @return
     */
    public Page<E> fetchPage(final JdbcTemplate jt, final String sqlCountRows,
            final String sqlFetchRows, final Object args[], final int pageNo, final int pageSize,
            final ParameterizedRowMapper<E> rowMapper) {
        if (pageNo <= 0 || pageSize <= 0) {
            throw new IllegalArgumentException("pageNo and pageSize must be greater than zero");
        }

        // 查询当前记录总数
        final int rowCount = jt.queryForInt(sqlCountRows, args);

        // 计算页数
        int pageCount = rowCount / pageSize;
        if (rowCount > pageSize * pageCount) {
            pageCount++;
        }

        // 创建Page对象
        final Page<E> page = new Page<E>();
        page.setPageNumber(pageNo);
        page.setPagesAvailable(pageCount);
        page.setTotalCount(rowCount);

        if (pageNo > pageCount) {
            return null;
        }

        final int startRow = (pageNo - 1) * pageSize;
        // TODO 在数据量很大时， limit效率很低
        final String selectSQL = sqlFetchRows + " limit " + startRow + "," + pageSize;

        List<E> result = jt.query(selectSQL, args, rowMapper);
        for (E item : result) {
            page.getPageItems().add(item);
        }
        return page;
    }

}