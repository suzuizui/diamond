package com.le.diamond.client;

public class DiffEnvLoacalFileTest {

	String dataId = "pingwei.test";
	String group = "default_group";
	long timeoutMs = 3000;

//	@Test
//	public void test_默认环境() throws Exception {
//		IOUtils.cleanDirectory(new File(LocalConfigInfoProcessor.localFileRootPath));
//		Diamond.publishSingle(dataId, group, "test");
//		Thread.sleep(3000);
//		Diamond.getConfig(dataId, group, 3000);
//		File file = new File(LocalConfigInfoProcessor.localFileRootPath + File.separator + "default_diamond"
//				+ File.separator + "snapshot" + File.separator + group + File.separator + dataId);
//		Assert.assertTrue(file.exists());
//	}
//
//	@Test
//	public void test_单元环境listener() throws Exception{
//		File file = new File(LocalConfigInfoProcessor.localFileRootPath + File.separator + "dailyunit_diamond"
//				+ File.separator + "snapshot" + File.separator + group + File.separator + dataId);
//		file.delete();
//		Assert.assertFalse(file.exists());
//		DiamondEnv env = DiamondUnitSite.getDiamondUnitEnv("dailyunit");
//		env.addListeners(dataId, group, Arrays.asList(new ManagerListener() {
//
//			@Override
//			public void receiveConfigInfo(String configInfo) {
//			}
//
//			@Override
//			public Executor getExecutor() {
//				return null;
//			}
//		}));
//		Thread.sleep(3000);
//		Assert.assertTrue(file.exists());
//	}
//
//	@Test
//	public void test_默认环境测试listener() throws Exception{
//		File file = new File(LocalConfigInfoProcessor.localFileRootPath + File.separator + "default_diamond"
//				+ File.separator + "snapshot" + File.separator + group + File.separator + dataId);
//		file.delete();
//		Assert.assertFalse(file.exists());
//		final CountDownLatch l = new CountDownLatch(1);
//		Diamond.addListener(dataId, group, new ManagerListener() {
//
//			@Override
//			public void receiveConfigInfo(String configInfo) {
//				l.countDown();
//			}
//
//			@Override
//			public Executor getExecutor() {
//				return null;
//			}
//		});
//		l.await();
//		Assert.assertTrue(file.exists());
//	}
//
//
//
//	@Test
//	public void test_单元环境() throws Exception {
//		IOUtils.cleanDirectory(new File(LocalConfigInfoProcessor.localFileRootPath));
//		DiamondEnv env = DiamondUnitSite.getDiamondUnitEnv("dailyunit");
//		env.publishSingle(dataId, group, "test");
//		Thread.sleep(3000);
//		env.getConfig(dataId, group, 3000);
//		File file = new File(LocalConfigInfoProcessor.localFileRootPath + File.separator + "dailyunit_diamond"
//				+ File.separator + "snapshot" + File.separator + group + File.separator + dataId);
//		Assert.assertTrue(file.exists());
//	}
//
//
//	@Test
//	public void test_删除所有单元数据() throws Exception {
//		LocalConfigInfoProcessor.cleanAllSnapshot();
//		File file = new File(LocalConfigInfoProcessor.localFileRootPath + File.separator + "dailyunit_diamond"
//				+ File.separator + "snapshot");
//		Assert.assertFalse(file.exists());
//		File log = new File(LocalConfigInfoProcessor.localFileRootPath);
//		Assert.assertTrue(log.exists());
//	}
//
//	@Test
//	public void test_先缓存再服务器() throws Exception {
//		File file = new File(LocalConfigInfoProcessor.localFileRootPath + File.separator + "default_diamond"
//				+ File.separator + "snapshot" + File.separator + group + File.separator + dataId);
//		if(!file.getParentFile().exists()){
//			file.getParentFile().mkdirs();
//		}
//		file.createNewFile();
//		IOUtils.writeStringToFile(file, "pingwei", "GBK");
//		Diamond.publishSingle(dataId, group, "test");
//		Thread.sleep(3000);
//		String content = Diamond.getConfig(dataId, group, Constants.GETCONFIG_LOCAL_SNAPSHOT_SERVER, 3000);
//		Assert.assertEquals("pingwei", content);
//		content = Diamond.getConfig(dataId, group, timeoutMs);
//		Assert.assertEquals("test", content);
//	}
//
//	@Test
//	public void test_单元环境先缓存再服务器() throws Exception {
//		File file = new File(LocalConfigInfoProcessor.localFileRootPath + File.separator + "dailyunit_diamond"
//				+ File.separator + "snapshot" + File.separator + group + File.separator + dataId);
//		if(!file.getParentFile().exists()){
//			file.getParentFile().mkdirs();
//		}
//		file.createNewFile();
//		IOUtils.writeStringToFile(file, "pingwei", "GBK");
//		DiamondEnv env = DiamondUnitSite.getDiamondUnitEnv("dailyunit");
//		env.publishSingle(dataId, group, "test");
//		Thread.sleep(3000);
//
//		String content = env.getConfig(dataId, group, Constants.GETCONFIG_LOCAL_SNAPSHOT_SERVER, 3000);
//		Assert.assertEquals("pingwei", content);
//		content = env.getConfig(dataId, group, timeoutMs);
//		Assert.assertEquals("test", content);
//	}
	
	

}
