package com.le.diamond.server.monitor;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicLong;

public class ResponseMonitor {
	static AtomicLong[] getConfigCountDetail = new AtomicLong[8];
	static AtomicLong getConfigCount = new AtomicLong();
	static{
		refresh();
	}
	
	public static void refresh(){
		for(int i = 0; i< getConfigCountDetail.length;i++){
			getConfigCountDetail[i] = new AtomicLong();
		}
	}
	
	public static void addConfigTime(long time){
		getConfigCount.incrementAndGet();
		if(time < 50){
			getConfigCountDetail[0].incrementAndGet();
		} else if(time < 100) {
			getConfigCountDetail[1].incrementAndGet();
		} else if (time < 200){
			getConfigCountDetail[2].incrementAndGet();
		} else if(time < 500){
			getConfigCountDetail[3].incrementAndGet();
		} else if(time < 1000){
			getConfigCountDetail[4].incrementAndGet();
		} else if(time < 2000){
			getConfigCountDetail[5].incrementAndGet();
		} else if(time < 3000){
			getConfigCountDetail[6].incrementAndGet();
		} else {
			getConfigCountDetail[7].incrementAndGet();
		}
	}
	
	public static String getStringForPrint(){
		DecimalFormat df = new DecimalFormat("##.0");
		StringBuilder s = new StringBuilder("getConfig monitor:\r\n");
		s.append("0-50ms:" + df.format(getConfigCountDetail[0].getAndSet(0)*100/ getConfigCount.get())).append("%\r\n");
		s.append("100-200ms:" + df.format(getConfigCountDetail[2].getAndSet(0)*100/ getConfigCount.get())).append("%\r\n");
		s.append("200-500ms:" + df.format(getConfigCountDetail[3].getAndSet(0)*100/ getConfigCount.get())).append("%\r\n");
		s.append("500-1000ms:" + df.format(getConfigCountDetail[4].getAndSet(0)*100/ getConfigCount.get())).append("%\r\n");
		s.append("1000-2000ms:" + df.format(getConfigCountDetail[5].getAndSet(0)*100/ getConfigCount.get())).append("%\r\n");
		s.append("2000-3000ms:" + df.format(getConfigCountDetail[6].getAndSet(0)*100/ getConfigCount.get())).append("%\r\n");
		s.append("3000ÒÔÉÏms:" + df.format(getConfigCountDetail[7].getAndSet(0)*100/ getConfigCount.getAndSet(0))).append("%\r\n");
		return s.toString();
	}
	
	public static void main(String[] args) {
		ResponseMonitor.addConfigTime(10);
		ResponseMonitor.addConfigTime(10);
		ResponseMonitor.addConfigTime(10);
		ResponseMonitor.addConfigTime(10);
		ResponseMonitor.addConfigTime(100);
		ResponseMonitor.addConfigTime(150);
		ResponseMonitor.addConfigTime(250);
		ResponseMonitor.addConfigTime(350);
		ResponseMonitor.addConfigTime(750);
		ResponseMonitor.addConfigTime(15000);
		System.out.println(ResponseMonitor.getStringForPrint());
		System.out.println(ResponseMonitor.getStringForPrint());
		
	}
}