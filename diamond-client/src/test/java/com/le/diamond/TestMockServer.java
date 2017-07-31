package com.le.diamond;

public class TestMockServer {
	/*
	@Test
	public void testMockServer_ClientWorker��ȡ����()throws IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		MockServer.setUpMockServer();
		DiamondUnitSite.switchToLocalUnit();
		DiamondEnv env = DiamondUnitSite.getCenterUnitEnv();
		MockServer.setConfigInfo("chenwz1", "hello mockServer");
		Method mehtod = ClientWorker.class.getDeclaredMethod("getServerConfig", new Class[] {DiamondEnv.class, String.class, String.class, long.class}); 
		mehtod.setAccessible(true);
		String msg1 = (String) mehtod.invoke(null , new Object[] {env,"chenwz1", null, 100});
		assertEquals(msg1, "hello mockServer");
	}
	
	@Test
	public void testMockServer_�л����ĵ�Ԫ() throws IOException{
		MockServer.setUpMockServer();
		DiamondUnitSite.switchToLocalUnit();
		MockServer.setConfigInfo("chenwz2", "hello mockServer");
		String msg1 = Diamond.getConfig("chenwz2", null, 100);
		assertEquals(msg1, "hello mockServer");
		//�л������ĵ�Ԫ
		DiamondUnitSite.switchToCenterUnit();
		String msg2 = Diamond.getConfig("chenwz2", null, 100);
		//�жϱ��ص�Ԫ�Ƿ������ĵ�Ԫ��
		if(DiamondUnitSite.isInCenterUnit())
			assertEquals(msg2, "hello mockServer");
		else
			assertEquals(msg2, null);
	}
	
	@Test
	public void testMockServer_�����л��ر��ص�Ԫ() throws IOException{
		MockServer.setUpMockServer();
		DiamondUnitSite.switchToLocalUnit();
		
		MockServer.setConfigInfo("chenwz3", "hello mockServer");
		String msg1 = Diamond.getConfig("chenwz3", null, 100);
		assertEquals(msg1, "hello mockServer");
		//�л������ĵ�Ԫ
		DiamondUnitSite.switchToCenterUnit();
		String msg2 = Diamond.getConfig("chenwz3", null, 100);
		//�жϱ��ص�Ԫ�Ƿ������ĵ�Ԫ��
		if(DiamondUnitSite.isInCenterUnit())
			assertEquals(msg2, "hello mockServer");
		else
			assertEquals(msg2, null);
		DiamondUnitSite.switchToLocalUnit();
		msg2 = Diamond.getConfig("chenwz3", null, 100);
		assertEquals(msg2, "hello mockServer");
	}
	
	@Test
	public void TestMockServer_��ȡ���ĵ�Ԫ() throws IOException, InterruptedException{
		MockServer.setUpMockServer();
		DiamondUnitSite.switchToLocalUnit();
		//default��Ԫ��������
		Diamond.addListener("chenwz_local", null, new MockListener("local"));
		MockServer.setConfigInfo("chenwz_local", "hello mockServer");
		String msg1 = Diamond.getConfig("chenwz_local", null, 100);
		assertEquals(msg1, "hello mockServer");
		//center��Ԫ��������
		DiamondEnv center = DiamondUnitSite.getCenterUnitEnv();
		Diamond.addListener("chenwz_center", null, new MockListener("center"));
		MockServer.setConfigInfo("chenwz_center", "hello mockServer", center);
		String msg2 = center.getConfig("chenwz_center", null, 100);
		assertEquals(msg2, "hello mockServer");
		//���ĵ�Ԫ�л�ȡ���ص�Ԫ������
		msg2 = center.getConfig("chenwz_local", null, 100);
		if(DiamondUnitSite.isInCenterUnit())
			//����ڱ��ص�Ԫ�����ĵ�Ԫ
			assertEquals(msg2, "hello mockServer");
		else
			assertEquals(msg2, null);
		//�л����ص�Ԫ�����ĵ�Ԫ
		DiamondUnitSite.switchToCenterUnit();
		center = DiamondUnitSite.getCenterUnitEnv();
		msg2 = center.getConfig("chenwz_center", null, 100);
		assertEquals(msg2, "hello mockServer");
		//���ص�Ԫ�л������ĵ�Ԫ���ٴβ�������
		msg2 = center.getConfig("chenwz_local", null, 100);
		if(DiamondUnitSite.isInCenterUnit())
			//����ڱ��ص�Ԫ�����ĵ�Ԫ
			assertEquals(msg2, "hello mockServer");
		else
			assertEquals(msg2, null);
		//�л������ĵ�Ԫ��ʹ��Ĭ�ϵ�Ԫ��ȡ����
		msg2 = Diamond.getConfig("chenwz_center", null, 100);
		Thread.sleep(2000);
		Diamond.remove("chenwz_center", null);
		Diamond.remove("chenwz_local", null);
		assertEquals(msg2, "hello mockServer");
	}
	
	
	@Test
	public void testMockServer_��ȡ����()throws IOException{
		MockServer.setUpMockServer();
		DiamondUnitSite.switchToLocalUnit();
		MockServer.setConfigInfo("chenwz4", "hello mockServer");
		String msg1 = Diamond.getConfig("chenwz4", null, 100);
		assertEquals(msg1, "hello mockServer");
	}
	
	@Test
	public void testMockServer_TDDLʹ�ó���1 () throws IOException, InterruptedException{
		MockServer.setUpMockServer();
		DiamondUnitSite.switchToLocalUnit();
		DiamondEnv center = DiamondUnitSite.getCenterUnitEnv();
		MockServer.setConfigInfo("chenwz5", "hello mockServer", center);
		String msg1 = center.getConfig("chenwz5", null, 100);
		assertEquals(msg1, "hello mockServer");
		MockListener listener = new MockListener();
		Diamond.addListener("chenwz5", null, listener);
		Diamond.removeListener("chenwz5", null, listener);
		String msg2 = center.getConfig("chenwz5", null, 100);
		assertEquals(msg2, "hello mockServer");
//		Thread.sleep(100000);
	}
	
	@Test
	public void testMockServer_ɾ������()throws IOException{
		MockServer.setUpMockServer();
		DiamondUnitSite.switchToLocalUnit();
		DiamondEnv env = DiamondUnitSite.getCenterUnitEnv();
		MockServer.setConfigInfo("chenwz6", "hello mockServer", env);
		String msg1 = env.getConfig("chenwz6", null, 100);
		assertEquals(msg1, "hello mockServer");
		env.remove("chenwz6", null);
		assertEquals(null, env.getConfig("chenwz6", null, 100));
	}
	
	@Test
	public void testMockServer_TDDLʹ�ó���2() throws IOException, InterruptedException{
		MockServer.setUpMockServer();
		MockServer.setConfigInfo("com.le.tddl.rule.le.11.versions","11");
		MockServer.setConfigInfo("com.le.tddl.rule.le.11.V1","11");
		MockServer.setConfigInfo("com.le.tddl.rule.le.11.V2","11");
		
		MockListener listener1 = new MockListener("version");
		MockListener listener2 = new MockListener("v1");
		MockListener listener3 = new MockListener("v2");
		
		Diamond.addListener("com.le.tddl.rule.le.11.versions", null, listener1);
		Diamond.addListener("com.le.tddl.rule.le.11.V1", null, listener2);
		Thread.sleep(1000);
		
		Diamond.removeListener("com.le.tddl.rule.le.11.V1", null, listener2);
		Diamond.addListener("com.le.tddl.rule.le.11.V2", null, listener3);
		assertEquals(Diamond.getConfig("com.le.tddl.rule.le.11.V1", null, 100), "11");
		assertEquals(Diamond.getConfig("com.le.tddl.rule.le.11.V2", null, 100), "11");
		Thread.sleep(1000);
		
		Diamond.removeListener("com.le.tddl.rule.le.11.V2", null, listener3);
		Diamond.addListener("com.le.tddl.rule.le.11.V1", null, listener2);
		assertEquals(Diamond.getConfig("com.le.tddl.rule.le.11.V1", null, 100), "11");
		assertEquals(Diamond.getConfig("com.le.tddl.rule.le.11.V2", null, 100), "11");
		Thread.sleep(1000);
		
		Diamond.removeListener("com.le.tddl.rule.le.11.V1", null, listener2);
		Diamond.addListener("com.le.tddl.rule.le.11.V2", null, listener3);
		assertEquals(Diamond.getConfig("com.le.tddl.rule.le.11.V1", null, 100), "11");
		assertEquals(Diamond.getConfig("com.le.tddl.rule.le.11.V2", null, 100), "11");
		Thread.sleep(1000);
		

	}
	
	
	
	
	public static class MockListener implements ManagerListener {
		String dataId;
        String config;
        
        public MockListener(String dataId){
        	this.dataId = dataId;
        }
        
        public MockListener(){}
        public Executor getExecutor() {
            return null;
        }

        public void receiveConfigInfo(String configInfo) {
            config = configInfo;
            System.out.println("mocklistenr:" + dataId);
        }
    } */
}
