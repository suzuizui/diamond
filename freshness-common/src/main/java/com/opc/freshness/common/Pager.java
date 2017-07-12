package com.opc.freshness.common;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author wenwucao Date: 16/11/2 Time: 下午6:07
 * @version \$Id$
 */
public class Pager<T> {

    /** 分页数据 */
    private PageData page;

    /** list of data */
    private List<T> data;

    public Pager() {}

    public Pager(PageData page, List<T> data) {
        this.page = page;
        this.data = new ArrayList<T>();
        this.data.addAll(data);
    }

    public PageData getPage() {
        return page;
    }

    public List<T> getData() {
        return data == null ? new ArrayList<T>() : data;
    }

    public static <T> Builder<T> builder(List<T> data) {
        return new Builder<T>().data(data);
    }

    public static class Builder<T> {

        private List<T> data;

        private int curPage = 1;

        private int pageSize = 15;

        private int totalSize = 0;

        private Builder() { }

        public Builder<T> current(PageRequest.Page page) {
            this.curPage = page.getPageNo();
            this.pageSize = page.getPageSize();
            return this;
        }

        public Builder<T> total(int totalSize) {
            this.totalSize = totalSize;
            return this;
        }

        public Builder<T> data(List<T> data) {
            this.data = data;
            return this;
        }

        public Pager<T> create() {
            return new Pager<T>(new PageData(this.curPage, this.pageSize, this.totalSize), data);
        }
    }

    public static class PageData implements Serializable {

        private static final long serialVersionUID = 3599580483667456581L;

        private int curPage;

        private int pageSize;

        private int totalSize;
        /**总页数*/
        private int totalPage;

        public PageData() {
        }

        public PageData(int curPage, int pageSize, int totalSize) {
            this.curPage = curPage;
            this.pageSize = pageSize;
            this.totalSize = totalSize;
            this.totalPage = (totalSize / pageSize) + (totalSize % pageSize > 0 ? 1 : 0);
        }

        public int getCurPage() {
            return curPage;
        }

        public void setCurPage(int curPage) {
            this.curPage = curPage;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getTotalSize() {
            return totalSize;
        }

        public void setTotalSize(int totalSize) {
            this.totalSize = totalSize;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

