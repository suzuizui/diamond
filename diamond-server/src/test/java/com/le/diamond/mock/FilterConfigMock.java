package com.le.diamond.mock;

import java.util.Enumeration;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;



public class FilterConfigMock implements FilterConfig {

    public FilterConfigMock(ServletContext context) {
        this.context = context;
    }
    
    
    @Override
    public String getFilterName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ServletContext getServletContext() {
        return context;
    }

    @Override
    public String getInitParameter(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Enumeration getInitParameterNames() {
        // TODO Auto-generated method stub
        return null;
    }

    final ServletContext context;
}
