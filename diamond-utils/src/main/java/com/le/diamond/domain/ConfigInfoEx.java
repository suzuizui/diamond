package com.le.diamond.domain;

/**
 * ConfigInfo����չ��, ������������
 * 
 * @author leiwen.zh
 * 
 */
public class ConfigInfoEx extends ConfigInfo {

    private static final long serialVersionUID = -1L;

    // ������ѯʱ, �������ݵ�״̬��, �����״̬����Constants.java��
    private int status;
    // ������ѯʱ, �������ݵ���Ϣ
    private String message;


    public ConfigInfoEx() {
        super();
    }


    public ConfigInfoEx(String dataId, String group, String content) {
        super(dataId, group, content);
    }

    public ConfigInfoEx(String dataId, String group, String content, int status, String message){
        super(dataId, group, content);
        this.status = status;
        this.message = message;
    }


    public int getStatus() {
        return status;
    }


    public void setStatus(int status) {
        this.status = status;
    }


    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }


    @Override
    public String toString() {
        return "ConfigInfoEx [status=" + status + ", message=" + message + ", dataId=" + getDataId() + ", group()="
                + getGroup() + ", content()=" + getContent() + "]";
    }

}
