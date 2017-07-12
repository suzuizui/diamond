package com.opc.freshness.common;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/*
 * Copyright (c) 2014 Qunar.com. All Rights Reserved.
 */

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author wenwucao Date: 16/11/2 Time: 下午6:07
 * @version \$Id$
 */
public class PageRequest {

    private Page page = new Page(15, 1);

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    @JsonIgnore
    public int getOffset() {
        if(page == null){
            return 0;
        }
        int tmp = (page.pageNo - 1) * page.pageSize;
        return tmp < 0 ? 0 : tmp;
    }

    @JsonIgnore
    public int getLimit() {
        if(page == null){
            return 0;
        }
        return page.pageSize;
    }

    public PageRequest() {
    }

    public static class Page implements Serializable {

        private static final long serialVersionUID = -9116229816861557536L;

        int pageSize = 15;

        int pageNo = 1;

        public Page() {
        }

        public Page(int pageSize, int pageNo) {
            this.pageSize = pageSize;
            this.pageNo = pageNo;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getPageNo() {
            return pageNo;
        }

        public void setPageNo(int pageNo) {
            this.pageNo = pageNo;
        }

    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}