package com.le.diamond.server.utils;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.le.diamond.domain.Page;


/**
 * ��ҳ������
 * 
 * @author boyan
 * @date 2010-5-6
 * @param <E>
 */
public class PaginationHelper<E> {

    /**
     * ȡ��ҳ
     * 
     * @param jt
     *            jdbcTemplate
     * @param sqlCountRows
     *            ��ѯ������SQL
     * @param sqlFetchRows
     *            ��ѯ���ݵ�sql
     * @param args
     *            ��ѯ����
     * @param pageNo
     *            ҳ��
     * @param pageSize
     *            ÿҳ��С
     * @param rowMapper
     * @return
     */
    public Page<E> fetchPage(final JdbcTemplate jt, final String sqlCountRows,
            final String sqlFetchRows, final Object args[], final int pageNo, final int pageSize,
            final ParameterizedRowMapper<E> rowMapper) {
        if (pageNo <= 0 || pageSize <= 0) {
            throw new IllegalArgumentException("pageNo and pageSize must be greater than zero");
        }

        // ��ѯ��ǰ��¼����
        final int rowCount = jt.queryForInt(sqlCountRows, args);

        // ����ҳ��
        int pageCount = rowCount / pageSize;
        if (rowCount > pageSize * pageCount) {
            pageCount++;
        }

        // ����Page����
        final Page<E> page = new Page<E>();
        page.setPageNumber(pageNo);
        page.setPagesAvailable(pageCount);
        page.setTotalCount(rowCount);

        if (pageNo > pageCount) {
            return null;
        }

        final int startRow = (pageNo - 1) * pageSize;
        // TODO ���������ܴ�ʱ�� limitЧ�ʺܵ�
        final String selectSQL = sqlFetchRows + " limit " + startRow + "," + pageSize;

        List<E> result = jt.query(selectSQL, args, rowMapper);
        for (E item : result) {
            page.getPageItems().add(item);
        }
        return page;
    }

}