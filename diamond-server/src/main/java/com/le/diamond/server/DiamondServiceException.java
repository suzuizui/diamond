package com.le.diamond.server;

/**
 * Service����κ��쳣����װ�����Runtime�쳣�׳�
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
