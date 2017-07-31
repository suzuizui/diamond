package com.le.diamond.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.le.diamond.server.utils.LogUtil;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;



public class WebFilter implements Filter {
    
    static private String webRootPath;
    
    static public String rootPath() {
        return webRootPath;
    }
    
    // ∑Ω±„≤‚ ‘
    static public void setWebRootPath(String path) {
        webRootPath = path;
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext ctx = filterConfig.getServletContext();
        setWebRootPath(ctx.getRealPath("/"));
        
        try {
            initLogback();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding("GBK");
        response.setContentType("text/html;charset=GBK");
        try {
            chain.doFilter(request, response);
        } catch (IOException ioe) {
            LogUtil.defaultLog.error("Filter catch exception, " + ioe.toString(), ioe);
            throw ioe;
        } catch (ServletException se) {
            LogUtil.defaultLog.error("Filter catch exception, " + se.toString(), se);
            throw se;
        }
    }

    
    @Override
    public void destroy() {
    }

    static void initLogback() throws Exception {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.reset();

        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        configurator.doConfigure(WebFilter.class.getResource("/diamond-server-logback.xml"));
    }
}
