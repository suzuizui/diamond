package com.le.diamond.server.service;

import com.le.diamond.domain.ConfigInfo;
import com.le.diamond.domain.ConfigInfoAggr;
import com.le.diamond.domain.ConfigInfoChanged;
import com.le.diamond.domain.Page;
import com.le.diamond.server.utils.LogUtil;
import com.le.diamond.server.utils.PaginationHelper;
import com.le.diamond.server.utils.event.EventDispatcher;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


/**
 * 数据库服务，提供ConfigInfo在数据库的存取<br>
 * 3.0开始增加数据版本号, 并将物理删除改为逻辑删除<br>
 * 3.0增加数据库切换功能
 * 
 * @author boyan
 * @author leiwen.zh
 * @since 1.0
 */

@Repository
public class PersistService {
    static final class ConfigInfoWrapperRowMapper implements ParameterizedRowMapper<ConfigInfoWrapper> {
        public ConfigInfoWrapper mapRow(ResultSet rs, int rowNum) throws SQLException {
            ConfigInfoWrapper info = new ConfigInfoWrapper();

            info.setDataId(rs.getString("data_id"));
            info.setGroup(rs.getString("group_id"));

            try {
                info.setContent(rs.getString("content"));
            } catch (SQLException e) {
                // ignore
            }
            try {
                info.setId(rs.getLong("ID"));
            } catch (SQLException e) {
                // ignore
            }
            try {
                info.setLastModified(rs.getTimestamp("gmt_modified").getTime());
            } catch (SQLException e) {
                // ignore
            }
            return info;
        }
    }

    static final class ConfigInfoRowMapper implements ParameterizedRowMapper<ConfigInfo> {
        public ConfigInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            ConfigInfo info = new ConfigInfo();

            info.setDataId(rs.getString("data_id"));
            info.setGroup(rs.getString("group_id"));

            try {
                info.setContent(rs.getString("content"));
            } catch (SQLException e) {
                // ignore
            }
            try {
                info.setId(rs.getLong("ID"));
            } catch (SQLException e) {
                // ignore
            }
            return info;
        }
    }

    static final class ConfigInfoAggrRowMapper implements ParameterizedRowMapper<ConfigInfoAggr> {
        public ConfigInfoAggr mapRow(ResultSet rs, int rowNum) throws SQLException {
            ConfigInfoAggr info = new ConfigInfoAggr();
            info.setDataId(rs.getString("data_id"));
            info.setGroup(rs.getString("group_id"));
            info.setDatumId(rs.getString("datum_id"));
            info.setContent(rs.getString("content"));
            return info;
        }
    }

    static final class ConfigInfoChangedRowMapper implements ParameterizedRowMapper<ConfigInfoChanged> {
        public ConfigInfoChanged mapRow(ResultSet rs, int rowNum) throws SQLException {
            ConfigInfoChanged info = new ConfigInfoChanged();
            info.setDataId(rs.getString("data_id"));
            info.setGroup(rs.getString("group_id"));
            return info;
        }
    }


    public PersistService() throws IOException {
        jt = new JdbcTemplate();
        jt.setMaxRows(50000); // 设置最大记录数，防止内存膨胀
        jt.setQueryTimeout(QUERY_TIMEOUT);
        
        testMasterJT = new JdbcTemplate();
        testMasterJT.setQueryTimeout(QUERY_TIMEOUT);
        
        reload();
        
        TimerTaskService.scheduleWithFixedDelay(new SelectMasterTask(), 10, 10, TimeUnit.SECONDS);
    }
    
    public synchronized void reload() throws IOException {
        List<BasicDataSource> dblist = new ArrayList<BasicDataSource>();
        FileInputStream fis = null;
        try {
            File file = new File(PersistService.class.getResource("/jdbc.properties").toURI());
            Properties props = new Properties();
            props.load(fis = new FileInputStream(file));
            String val = null;

            val = props.getProperty("db.num");
            if (null == val) {
                throw new IllegalArgumentException("db.num is null");
            }
            int dbNum = Integer.parseInt(val.trim());
            
            for (int i = 0; i < dbNum; i++) {
                BasicDataSource ds = new BasicDataSource();
                ds.setDriverClassName(JDBC_DRIVER_NAME);

                val = props.getProperty("db.url." + i);
                if (null == val) {
                    LogUtil.fatalLog.error("db.url." + i + " is null");
                    throw new IllegalArgumentException();
                }
                ds.setUrl(val.trim());

                val = props.getProperty("db.user");
                if (null == val) {
                    LogUtil.fatalLog.error("db.user is null");
                    throw new IllegalArgumentException();
                }
                ds.setUsername(val.trim());

                val = props.getProperty("db.password");
                if (null == val) {
                    LogUtil.fatalLog.error("db.password is null");
                    throw new IllegalArgumentException();
                }
                ds.setPassword(val.trim());

                val = props.getProperty("db.initialSize");
                ds.setInitialSize(Integer.parseInt(defaultIfNull(val, "10")));

                val = props.getProperty("db.maxActive");
                ds.setMaxActive(Integer.parseInt(defaultIfNull(val, "20")));

                val = props.getProperty("db.maxIdle");
                ds.setMaxIdle(Integer.parseInt(defaultIfNull(val, "50")));

                ds.setMaxWait(3000L);
                ds.setPoolPreparedStatements(true);
                
                // 每10分钟检查一遍连接池
                ds.setTimeBetweenEvictionRunsMillis(TimeUnit.MINUTES.toMillis(10L));
                ds.setTestWhileIdle(true);
                ds.setValidationQuery("SELECT 1 FROM dual");


                dblist.add(ds);
            }
            
            if (dblist == null || dblist.size() == 0) {
                throw new RuntimeException("no datasource available");
            }
            
            dataSourceList = dblist;
            new SelectMasterTask().run();
        } catch (Exception e) {
            LogUtil.fatalLog.error(DB_LOAD_ERROR_MSG, e);
            throw new IOException(e);
        } finally {
            if (null != fis) {
                fis.close();
            }
        }
    }
    
    class SelectMasterTask implements Runnable {
        public void run() {
            LogUtil.defaultLog.info("check master db.");
            boolean isFound = false;
            
            for (BasicDataSource ds : dataSourceList) {
                testMasterJT.setDataSource(ds);
                testMasterJT.setQueryTimeout(QUERY_TIMEOUT);
                try {
                    testMasterJT
                            .update("delete from config_info where data_id='com.le.diamond.testMasterDB'");
                    if (jt.getDataSource() != ds) {
                        LogUtil.fatalLog.warn("[master-db] {}", ds.getUrl());
                    }
                    jt.setDataSource(ds);
                    isFound = true;
                    break;
                } catch (DataAccessException e) { // read only
                    e.printStackTrace(); // TODO remove
                }
            }
            
            if (!isFound) {
                LogUtil.fatalLog.error("[master-db] master db not found.");
            }
        }
    }
    
    
    static String defaultIfNull(String value, String defaultValue) {
        return null == value ? defaultValue : value;
    }

    /**
     * 单元测试用
     * 
     * @return
     */
    public JdbcTemplate getJdbcTemplate() {
        return this.jt;
    }


    public String getCurrentDBUrl() {
        BasicDataSource bds = (BasicDataSource) this.jt.getDataSource();
        return bds.getUrl();
    }

    // ----------------------- config_info 表 insert update delete
    /**
     * 添加普通配置信息，发布数据变更事件
     */
    public void addConfigInfo(String dataId, String group, String content, String srcIp,
                              String srcUser, Timestamp time) {
        //final Timestamp time = TimeUtils.getCurrentTime();
        try {
            jt.update(
                    "insert into config_info(data_id,group_id,content,src_ip,src_user,gmt_create,gmt_modified) values(?,?,?,?,?,?,?)",
                    dataId, group, content, srcIp, srcUser, time, time);

            EventDispatcher.fireEvent(new ConfigDataChangeEvent(dataId, group, time.getTime()));

        } catch (CannotGetJdbcConnectionException e) {
            LogUtil.fatalLog.error("[db-error] " + e.toString(), e);
            throw e;
        }
    }

    /**
     * 更新配置信息
     */
    public void updateConfigInfo(String dataId, String group, String content, String srcIp,
                                 String srcUser, Timestamp time) {
        //final Timestamp time = TimeUtils.getCurrentTime();
        try {
            jt.update(
                    "update config_info set content=?,src_ip=?,src_user=?,gmt_modified=? where data_id=? and group_id=?",
                    content, srcIp, srcUser, time, dataId, group);

            EventDispatcher.fireEvent(new ConfigDataChangeEvent(dataId, group, time.getTime()));

        } catch (CannotGetJdbcConnectionException e) {
            LogUtil.fatalLog.error("[db-error] " + e.toString(), e);
            throw e;
        }
    }

    /**
     * 写入主表，插入或更新
     */
    public void insertOrUpdate(String srcIp, String srcUser, ConfigInfo configInfo, Timestamp time) {
        try {
            addConfigInfo(configInfo.getDataId(), configInfo.getGroup(), configInfo.getContent(),
                    srcIp, srcUser, time);
        } catch (DataIntegrityViolationException ive) { // 唯一性约束冲突
            updateConfigInfo(configInfo.getDataId(), configInfo.getGroup(),
                    configInfo.getContent(), srcIp, srcUser, time);
        }
    }

    /**
     * 删除配置信息, 物理删除
     */
    public void removeConfigInfo(String dataId, String group) {
        try {
            jt.update("delete from config_info where data_id=? and group_id=?", dataId, group);
        } catch (CannotGetJdbcConnectionException e) {
            LogUtil.fatalLog.error("[db-error] " + e.toString(), e);
            throw e;
        }
    }

    // ----------------------- config_aggr_info 表 insert update delete
    /**
     * 增加聚合前数据到数据库, select -> update or insert
     */
    public boolean addAggrConfigInfo(final String dataId, final String group, final String datumId,
            final String content) {
        final Timestamp now = new Timestamp(System.currentTimeMillis());
        String select = "select content from config_info_aggr where data_id = ? and group_id = ? and datum_id = ? ";
        String insert = "insert into config_info_aggr(data_id, group_id, datum_id, content, gmt_modified) values(?,?,?,?,?) ";
        String update = "update config_info_aggr set content = ? , gmt_modified = ? where data_id = ? and group_id = ? and datum_id = ? ";

        try {
            try {
                String dbContent = jt.queryForObject(select,
                        new Object[] { dataId, group, datumId }, String.class);

                if (dbContent.equals(content)) {
                    return true;
                } else {
                    return jt.update(update, content, now, dataId, group, datumId) > 0;
                }
            } catch (EmptyResultDataAccessException ex) { // no data, insert
                try {
                    return jt.update(insert, dataId, group, datumId, content, now) > 0;
                } catch (DataIntegrityViolationException ive) {
                    return true;
                }
            }
        } catch (CannotGetJdbcConnectionException e) {
            LogUtil.fatalLog.error("[db-error] " + e.toString(), e);
            throw e;
        }
    }

    /**
     * 删除单条聚合前数据
     */
    public void removeSingleAggrConfigInfo(final String dataId, final String group, final String datumId) {
        String sql = "delete from config_info_aggr where data_id=? and group_id=? and datum_id=?";

        try {
            this.jt.update(sql, new PreparedStatementSetter() {
                public void setValues(PreparedStatement ps) throws SQLException {
                    int index = 1;
                    ps.setString(index++, dataId);
                    ps.setString(index++, group);
                    ps.setString(index++, datumId);
                }
            });
        }
        catch (CannotGetJdbcConnectionException e) {
            LogUtil.fatalLog.error("[db-error] " + e.toString(), e);
            throw e;
        }
    }


    /**
     * 删除一个dataId下面所有的聚合前数据
     */
    public void removeAggrConfigInfo(final String dataId, final String group) {
        String sql = "delete from config_info_aggr where data_id=? and group_id=?";

        try {
            this.jt.update(sql, new PreparedStatementSetter() {
                public void setValues(PreparedStatement ps) throws SQLException {
                    int index = 1;
                    ps.setString(index++, dataId);
                    ps.setString(index++, group);
                }
            });
        } catch (CannotGetJdbcConnectionException e) {
            LogUtil.fatalLog.error("[db-error] " + e.toString(), e);
            throw e;
        }
    }


    /**
     * 查找所有的dataId和group。保证不返回NULL。
     */
    public List<ConfigInfo> findAllDataIdAndGroup() {
        String sql = "select distinct data_id, group_id from config_info";

        try {
            return jt.query(sql, new Object[] {}, CONFIG_INFO_ROW_MAPPER);
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        } catch (CannotGetJdbcConnectionException e) {
            LogUtil.fatalLog.error("[db-error] " + e.toString(), e);
            throw e;
        } catch (Exception e) {
            LogUtil.fatalLog.error("[db-other-error]" + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据dataId和group查询配置信息
     */
    public ConfigInfo findConfigInfo(final String dataId, final String group) {
        try {
            return this.jt
                    .queryForObject(
                            "select ID,data_id,group_id,content from config_info where data_id=? and group_id=?",
                            new Object[] { dataId, group }, CONFIG_INFO_ROW_MAPPER);
        } catch (EmptyResultDataAccessException e) { // 表明数据不存在, 返回null
            return null;
        } catch (CannotGetJdbcConnectionException e) {
            LogUtil.fatalLog.error("[db-error] " + e.toString(), e);
            throw e;
        }
    }


    /**
     * 根据数据库主键ID查询配置信息
     * 
     * @param id
     * @return
     */
    public ConfigInfo findConfigInfo(long id) {
        try {
            return this.jt.queryForObject("select ID,data_id,group_id,content from config_info where ID=?",
                new Object[] { id }, CONFIG_INFO_ROW_MAPPER);
        }
        catch (EmptyResultDataAccessException e) { // 表明数据不存在
            return null;
        }
        catch (CannotGetJdbcConnectionException e) {
            LogUtil.fatalLog.error("[db-error] " + e.toString(), e);
            throw e;
        }
    }


    /**
     * 根据dataId查询配置信息
     * 
     * @param pageNo
     *            页码(必须大于0)
     * @param pageSize
     *            每页大小(必须大于0)
     * @param dataId
     * 
     * @return ConfigInfo对象的集合
     */
    public Page<ConfigInfo> findConfigInfoByDataId(final int pageNo, final int pageSize, final String dataId) {
        PaginationHelper<ConfigInfo> helper = new PaginationHelper<ConfigInfo>();
        try {
            return helper.fetchPage(this.jt, "select count(ID) from config_info where data_id=?",
                "select ID,data_id,group_id,content from config_info where data_id=?", new Object[] { dataId },
                pageNo, pageSize, CONFIG_INFO_ROW_MAPPER);
        }
        catch (CannotGetJdbcConnectionException e) {
            LogUtil.fatalLog.error("[db-error] " + e.toString(), e);
            throw e;
        }
    }


    /**
     * 根据group查询配置信息
     * 
     * @param pageNo
     *            页码(必须大于0)
     * @param pageSize
     *            每页大小(必须大于0)
     * 
     * @param group
     * 
     * @return ConfigInfo对象的集合
     */
    public Page<ConfigInfo> findConfigInfoByGroup(final int pageNo, final int pageSize, final String group) {
        PaginationHelper<ConfigInfo> helper = new PaginationHelper<ConfigInfo>();
        try {
            return helper.fetchPage(this.jt, "select count(ID) from config_info where group_id=?",
                "select ID,data_id,group_id,content from config_info where group_id=?", new Object[] { group },
                pageNo, pageSize, CONFIG_INFO_ROW_MAPPER);
        }
        catch (CannotGetJdbcConnectionException e) {
            LogUtil.fatalLog.error("[db-error] " + e.toString(), e);
            throw e;
        }
    }

    /**
     * 返回配置项个数
     */
    public int configInfoCount() {
        String sql = " SELECT COUNT(ID) FROM config_info ";
        return jt.queryForInt(sql);
    }

    public int aggrConfigInfoCount(String dataId, String group) {
        String sql = " SELECT COUNT(ID) FROM config_info_aggr WHERE data_id = ? and group_id = ?";
        return jt.queryForInt(sql,new Object[] { dataId, group });
    }

    /**
     * 分页查询所有的配置信息
     * 
     * @param pageNo
     *            页码(从1开始)
     * @param pageSize
     *            每页大小(必须大于0)
     * 
     * @return ConfigInfo对象的集合
     */
    public Page<ConfigInfo> findAllConfigInfo(final int pageNo, final int pageSize) {
        String select = " SELECT t.id,data_id,group_id,content "
                      + " FROM (                               "
                      + "   SELECT id FROM config_info         "
                      + "   FORCE INDEX(PRIMARY)               "
                      + "   ORDER BY id LIMIT ?, ?             "
                      + " ) g, config_info t                   "
                      + " WHERE g.id = t.id                    ";
        
        final int totalCount = configInfoCount();
        int pageCount = totalCount / pageSize;
        if (totalCount > pageSize * pageCount) {
            pageCount++;
        }
        
        if (pageNo > pageCount) {
            return null;
        }

        final Page<ConfigInfo> page = new Page<ConfigInfo>();
        page.setPageNumber(pageNo);
        page.setPagesAvailable(pageCount);
        page.setTotalCount(totalCount);

        try {
            List<ConfigInfo> result = jt.query(select, 
                    new Object[] { (pageNo - 1) * pageSize, pageSize }, 
//                    new Object[0],
                    CONFIG_INFO_ROW_MAPPER);

            for (ConfigInfo item : result) {
                page.getPageItems().add(item);
            }
            return page;
        } catch (EmptyResultDataAccessException e) {
            return page;
        } catch (CannotGetJdbcConnectionException e) {
            LogUtil.fatalLog.error("[db-error] " + e.toString(), e);
            throw e;
        }
    }

    public static class ConfigInfoWrapper extends ConfigInfo{
        private long lastModified;

        public ConfigInfoWrapper(){}

        public long getLastModified() {
            return lastModified;
        }

        public void setLastModified(long lastModified) {
            this.lastModified = lastModified;
        }
    }
    public Page<ConfigInfoWrapper> findAllConfigInfoForDumpAll(final int pageNo, final int pageSize) {
        String select = " SELECT t.id,data_id,group_id,content,gmt_modified "
                + " FROM (                               "
                + "   SELECT id FROM config_info         "
                + "   FORCE INDEX(PRIMARY)               "
                + "   ORDER BY id LIMIT ?, ?             "
                + " ) g, config_info t                   "
                + " WHERE g.id = t.id                    ";

        final int totalCount = configInfoCount();
        int pageCount = totalCount / pageSize;
        if (totalCount > pageSize * pageCount) {
            pageCount++;
        }

        if (pageNo > pageCount) {
            return null;
        }

        final Page<ConfigInfoWrapper> page = new Page<ConfigInfoWrapper>();
        page.setPageNumber(pageNo);
        page.setPagesAvailable(pageCount);
        page.setTotalCount(totalCount);

        try {
            List<ConfigInfoWrapper> result = jt.query(select,
                    new Object[] { (pageNo - 1) * pageSize, pageSize },
//                    new Object[0],
                    CONFIG_INFO_WRAPPER_ROW_MAPPER);

            for (ConfigInfoWrapper item : result) {
                page.getPageItems().add(item);
            }
            return page;
        } catch (EmptyResultDataAccessException e) {
            return page;
        } catch (CannotGetJdbcConnectionException e) {
            LogUtil.fatalLog.error("[db-error] " + e.toString(), e);
            throw e;
        }
    }

    /**
     * 通过select in方式实现db记录的批量查询； subQueryLimit指定in中条件的个数，上限20
     */
    public List<ConfigInfo> findConfigInfoByBatch(final List<String> dataIds, final String group, int subQueryLimit){
        // assert dataids group not null

        // if dataids empty return empty list
        if(CollectionUtils.isEmpty(dataIds)){
            return Collections.emptyList();
        }

        // 批量查询上限
        // in 个数控制在100内, sql语句长度越短越好
        if(subQueryLimit > 50) subQueryLimit = 50;

        List<ConfigInfo> result = new ArrayList<ConfigInfo>(dataIds.size());

        String sqlStart = "select data_id, group_id, content from config_info where group_id = ? and data_id in (";
        String sqlEnd = ")";
        StringBuilder subQuerySql = new StringBuilder();

        for(int i = 0; i < dataIds.size(); i += subQueryLimit){
            // dataids
            List<String> params = new ArrayList<String>(
                    dataIds.subList(i,  i + subQueryLimit < dataIds.size() ? i + subQueryLimit  : dataIds.size()));

            // ? placeholder
            subQuerySql = new StringBuilder();
            for(int j = 0; j < params.size(); j++){
                subQuerySql.append("?");
                if(j != params.size() - 1) subQuerySql.append(",");
            }

            // group
            params.add(0,group);

            List<ConfigInfo> r = this.jt.query(sqlStart + subQuerySql.toString() + sqlEnd, params.toArray(), CONFIG_INFO_ROW_MAPPER);

            // assert not null
            if(r != null && r.size() > 0) result.addAll(r);
        }
        return result;
    }


    /**
     * 根据dataId和group模糊查询配置信息
     * 
     * @param pageNo
     *            页码(必须大于0)
     * @param pageSize
     *            每页大小(必须大于0)
     * @param dataId
     * @param group
     * 
     * @return ConfigInfo对象的集合
     */
    public Page<ConfigInfo> findConfigInfoLike(final int pageNo, final int pageSize,
            final String dataId, final String group, final String content) {
        if (StringUtils.isBlank(dataId) && StringUtils.isBlank(group)) {
            return this.findAllConfigInfo(pageNo, pageSize);
        }

        PaginationHelper<ConfigInfo> helper = new PaginationHelper<ConfigInfo>();

        String sqlCountRows = "select count(ID) from config_info where ";
        String sqlFetchRows = "select ID,data_id,group_id,content from config_info where ";
        String where = " 1=1 ";
        List<String> params = new ArrayList<String>();
        
        if (!StringUtils.isBlank(dataId)) {
            where += " and data_id like ? ";
            params.add(generateLikeArgument(dataId));
        }
        if (!StringUtils.isBlank(group)) {
            where += " and group_id like ? ";
            params.add(generateLikeArgument(group));
        }
        if (!StringUtils.isBlank(content)) {
            where += " and content like ? ";
            params.add(generateLikeArgument(content));
        }

        try {
            return helper.fetchPage(jt, sqlCountRows + where, sqlFetchRows + where,
                    params.toArray(), pageNo, pageSize, CONFIG_INFO_ROW_MAPPER);
        } catch (CannotGetJdbcConnectionException e) {
            LogUtil.fatalLog.error("[db-error] " + e.toString(), e);
            throw e;
        }
    }


    /**
     * 查找聚合前的单条数据
     * 
     * @param dataId
     * @param group
     * @param datumId
     * @return
     */
    public ConfigInfoAggr findSingleConfigInfoAggr(String dataId, String group, String datumId) {
        String sql =
                "select id,data_id,group_id,datum_id,content from config_info_aggr where data_id=? and group_id=? and datum_id=?";

        try {
            return this.jt.queryForObject(sql, new Object[] { dataId, group, datumId }, CONFIG_INFO_AGGR_ROW_MAPPER);
        }
        catch (EmptyResultDataAccessException e) {
            // 是EmptyResultDataAccessException, 表明数据不存在, 返回null
            return null;
        }
        catch (CannotGetJdbcConnectionException e) {
            LogUtil.fatalLog.error("[db-error] " + e.toString(), e);
            throw e;
        }
        catch(Exception e) {
            LogUtil.fatalLog.error("[db-other-error]" + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    /**
     * 查找一个dataId下面的所有聚合前的数据. 保证不返回NULL.
     */
    public List<ConfigInfoAggr> findConfigInfoAggr(String dataId, String group) {
        String sql = "select data_id,group_id,datum_id,content from config_info_aggr where data_id=? and group_id=? order by datum_id";

        try {
            return this.jt.query(sql, new Object[] { dataId, group }, CONFIG_INFO_AGGR_ROW_MAPPER);
        } catch (CannotGetJdbcConnectionException e) {
            LogUtil.fatalLog.error("[db-error] " + e.toString(), e);
            throw e;
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        } catch (Exception e) {
            LogUtil.fatalLog.error("[db-other-error]" + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public Page<ConfigInfoAggr> findConfigInfoAggrByPage(String dataId, String group, final int pageNo, final int pageSize) {
        String select = "select data_id,group_id,datum_id,content from config_info_aggr where data_id=? and group_id=? order by datum_id limit ?,?";

        final int totalCount = aggrConfigInfoCount(dataId, group);
        int pageCount = totalCount / pageSize;
        if (totalCount > pageSize * pageCount) {
            pageCount++;
        }

        if (pageNo > pageCount) {
            return null;
        }

        final Page<ConfigInfoAggr> page = new Page<ConfigInfoAggr>();
        page.setPageNumber(pageNo);
        page.setPagesAvailable(pageCount);
        page.setTotalCount(totalCount);

        try {
            List<ConfigInfoAggr> result = jt.query(select, new Object[] { dataId, group, (pageNo - 1) * pageSize, pageSize }, CONFIG_INFO_AGGR_ROW_MAPPER);

            for (ConfigInfoAggr item : result) {
                page.getPageItems().add(item);
            }
            return page;
        } catch (EmptyResultDataAccessException e) {
            return page;
        } catch (CannotGetJdbcConnectionException e) {
            LogUtil.fatalLog.error("[db-error] " + e.toString(), e);
            throw e;
        }
    }




    
    /**
     * 找到所有聚合数据组。
     */
    public List<ConfigInfoChanged> findAllAggrGroup() {
        String sql = "select distinct data_id, group_id from config_info_aggr";

        try {
            return this.jt.query(sql, new Object[] {}, CONFIG_INFO_CHANGED_ROW_MAPPER);
        }
        catch (CannotGetJdbcConnectionException e) {
            LogUtil.fatalLog.error("[db-error] " + e.toString(), e);
            throw e;
        }
        catch (EmptyResultDataAccessException e) {
            return null;
        }
        catch(Exception e) {
            LogUtil.fatalLog.error("[db-other-error]" + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    // 由datum内容查找datumId
    public List<String> findDatumIdByContent(String dataId, String groupId, String content) {
        String sql = "select datum_id from config_info_aggr where data_id = ? and group_id = ? and content = ? ";

        try {
            return this.jt.queryForList(sql, new Object[] { dataId, groupId, content },
                    String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (IncorrectResultSizeDataAccessException e) {
        	return null;
		}catch (CannotGetJdbcConnectionException e) {
		    LogUtil.fatalLog.error("[db-error] " + e.toString(), e);
            throw e;
        } 
    }

    private String generateLikeArgument(String s) {
        if (s.indexOf("*") >= 0)
            return s.replaceAll("\\*", "%");
        else {
            return "%" + s + "%";
        }
    }

    // =========================

    private static final String DB_LOAD_ERROR_MSG = "[db-load-error]load jdbc.properties error";

    private static final String JDBC_DRIVER_NAME = "com.mysql.jdbc.Driver";

    // JDBC执行超时时间, 单位秒
    private static final int QUERY_TIMEOUT = 3;

    static final ConfigInfoWrapperRowMapper CONFIG_INFO_WRAPPER_ROW_MAPPER = new ConfigInfoWrapperRowMapper();

    static final ConfigInfoRowMapper CONFIG_INFO_ROW_MAPPER = new ConfigInfoRowMapper();

    static final ConfigInfoAggrRowMapper CONFIG_INFO_AGGR_ROW_MAPPER = new ConfigInfoAggrRowMapper();

    static final ConfigInfoChangedRowMapper CONFIG_INFO_CHANGED_ROW_MAPPER = new ConfigInfoChangedRowMapper();

    private List<BasicDataSource> dataSourceList = new ArrayList<BasicDataSource>();
    private JdbcTemplate jt;
    private JdbcTemplate testMasterJT;

}
