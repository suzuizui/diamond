package com.le.diamond.server;

/**
 * Service层的任何异常都包装成这个Runtime异常抛出
 * 
 * @author boyan
 * @date 2010-5-5
 */
public class DiamondServiceException extends RuntimeException {
    static final long serialVersionUID = -1L;


    public DiamondServiceException() {
        super();

    }


    public DiamondServiceException(String message, Throwable cause) {
        super(message, cause);

    }


    public DiamondServiceException(String message) {
        super(message);

    }


    public DiamondServiceException(Throwable cause) {
        super(cause);

    }

}
