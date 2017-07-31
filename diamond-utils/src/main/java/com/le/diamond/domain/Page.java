package com.le.diamond.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * ��ҳ����
 * 
 * @author boyan
 * @date 2010-5-6
 * @param <E>
 */
public class Page<E> implements Serializable {
    static final long serialVersionUID = -1L;

    private int totalCount; // �ܼ�¼��
    private int pageNumber; // ҳ��
    private int pagesAvailable; // ��ҳ��
    private List<E> pageItems = new ArrayList<E>(); // ��ҳ����


    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }


    public void setPagesAvailable(int pagesAvailable) {
        this.pagesAvailable = pagesAvailable;
    }


    public void setPageItems(List<E> pageItems) {
        this.pageItems = pageItems;
    }


    public int getTotalCount() {
        return totalCount;
    }


    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }


    public int getPageNumber() {
        return pageNumber;
    }


    public int getPagesAvailable() {
        return pagesAvailable;
    }


    public List<E> getPageItems() {
        return pageItems;
    }
}