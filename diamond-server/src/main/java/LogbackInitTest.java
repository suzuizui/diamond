import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.le.diamond.utils.AppNameUtils;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;


public class LogbackInitTest {

    private static final Logger logger = LoggerFactory.getLogger(LogbackInitTest.class);
       
    
    
    public static void main(String[] args) throws Exception {
        AppNameUtils.class.getClassLoader();
        String classpath = AppNameUtils.class.getResource("/").getPath();
        System.out.println("The classpath is " + classpath);
        
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.reset();

        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        configurator.doConfigure(LogbackInitTest.class.getResource("logback-jiuren.xml"));
        
        for (;;) {
            logger.info("hello");
            System.out.println(getLevel(logger));
            Thread.sleep(1000L);
        }
    }
    
    
    static String getLevel(Logger logger) {
        if (logger.isDebugEnabled()) {
            return "debug";
        } else if (logger.isInfoEnabled()) {
            return "info";
        } else if (logger.isWarnEnabled()) {
            return "warn";
        } else if (logger.isErrorEnabled()) {
            return "error";
        } else {
            return "unknown";
        }
    }
}
