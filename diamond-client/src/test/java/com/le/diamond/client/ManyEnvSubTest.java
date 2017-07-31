package com.le.diamond.client;


public class ManyEnvSubTest {

//    @Test
//    public void test() throws Exception {
//        String dataId = "MultiTargetEnvSubscribeTest";
//        String group = "group";
//        final String content = new Date().toString();
//
//        Diamond.publishSingle(dataId, group, content);
//        while (!Diamond.getConfig(dataId, group, 2000).equals(content)) {
//            Thread.sleep(500L);
//        }
//
//        final Listener_多环境订阅 listener182 = new Listener_多环境订阅();
//        DiamondEnv env182 = Diamond.getTargetEnv("10.232.102.182");
//        final Listener_多环境订阅 listener183 = new Listener_多环境订阅();
//        DiamondEnv env183 = Diamond.getTargetEnv("10.232.102.183");
//
//        env182.addListeners(dataId, group, Arrays.asList(listener182));
//        env183.addListeners(dataId, group, Arrays.asList(listener183));
//
//        Callable<Boolean> expect = new Callable<Boolean>() {
//            public Boolean call() {
//                return listener182.callCount.get() > 0 && listener183.callCount.get() > 0
//                        && listener182.content.equals(content)
//                        && listener183.content.equals(content);
//            }
//        };
////        while (!expect.call()) {
////            Thread.sleep(200L);
////        }
//    }
}


//class Listener_多环境订阅 implements ManagerListener {
//    @Override
//    public Executor getExecutor() {
//        return null;
//    }
//
//    @Override
//    public void receiveConfigInfo(String configInfo) {
//        callCount.incrementAndGet();
//        content = configInfo;
////        new Exception(this.toString() + ">>>" + configInfo).printStackTrace();
//    }
//
//    String content = null;
//    AtomicInteger callCount = new AtomicInteger(0);
//}
