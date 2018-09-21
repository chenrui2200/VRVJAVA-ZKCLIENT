/**
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vrv.cems.zkclient;

import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.vrv.cems.service.base.interfaces.IAddressService;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.zookeeper.Watcher;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.exceptions.base.MockitoAssertionError;

public class TestUtil {

    /**
     * This waits until the provided {@link Callable} returns an object that is equals to the given expected value or
     * the timeout has been reached. In both cases this method will return the return value of the latest
     * {@link Callable} execution.
     * 
     * @param expectedValue
     *            The expected value of the callable.
     * @param callable
     *            The callable.
     * @param <T>
     *            The return type of the callable.
     * @param timeUnit
     *            The timeout timeunit.
     * @param timeout
     *            The timeout.
     * @return the return value of the latest {@link Callable} execution.
     * @throws Exception
     * @throws InterruptedException
     */
    public static <T> T waitUntil(T expectedValue, Callable<T> callable, TimeUnit timeUnit, long timeout) throws Exception {
        long startTime = System.currentTimeMillis();
        do {
            T actual = callable.call();
            if (expectedValue.equals(actual)) {
                return actual;
            }
            if (System.currentTimeMillis() > startTime + timeUnit.toMillis(timeout)) {
                return actual;
            }
            Thread.sleep(50);
        } while (true);
    }

    /**
     * This waits until a mockito verification passed (which is provided in the runnable). This waits until the
     * virification passed or the timeout has been reached. If the timeout has been reached this method will rethrow the
     * {@link MockitoAssertionError} that comes from the mockito verification code.
     * 
     * @param runnable
     *            The runnable containing the mockito verification.
     * @param timeUnit
     *            The timeout timeunit.
     * @param timeout
     *            The timeout.
     * @throws InterruptedException
     */
    public static void waitUntilVerified(Runnable runnable, TimeUnit timeUnit, int timeout) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        do {
            MockitoAssertionError exception = null;
            try {
                runnable.run();
            } catch (MockitoAssertionError e) {
                exception = e;
            }
            if (exception == null) {
                return;
            }
            if (System.currentTimeMillis() > startTime + timeUnit.toMillis(timeout)) {
                throw exception;
            }
            Thread.sleep(50);
        } while (true);
    }

    public static ZkServer startZkServer(TemporaryFolder temporaryFolder, String ip,int port) throws IOException {
        File dataFolder = temporaryFolder.newFolder("data");
        File logFolder = temporaryFolder.newFolder("log");
        return startServer(ip,port, dataFolder.getAbsolutePath(), logFolder.getAbsolutePath());
    }

    public static ZkServer startZkServer(String testName, String ip,int port) throws IOException {
        String dataPath = "./build/test/" + testName + "/data";
        String logPath = "./build/test/" + testName + "/log";
        FileUtils.deleteDirectory(new File(dataPath));
        FileUtils.deleteDirectory(new File(logPath));
        return startServer(ip,port, dataPath, logPath);
    }

    private static ZkServer startServer(String ip,int port, String dataPath, String logPath) {
        ZkServer zkServer = new ZkServer(dataPath, logPath, mock(IDefaultNameSpace.class), ip,port, ZkServer.DEFAULT_TICK_TIME, 100);
        zkServer.start();
        return zkServer;
    }

    @Test
    public void test() throws InterruptedException {
        /*try {
            org.apache.zookeeper.server.auth.DigestAuthenticationProvider.main(new String[]{"test"} );
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }*/
        ZkClient zkClient = new ZkClient("192.168.133.63:2181", 10000);

        /*String data = zkClient.readData("/CEMS-SERVICE-ZK-REGISTER");
        System.out.println("data->"+data);


        List<String> list = zkClient.getChildren("/CEMS-SERVICE-ZK-REGISTER");
        System.out.println("list->"+list.toArray().toString());*/

        //String path = zkClient.createEphemeralSequential("/test","12312");

        //zkClient.createEphemeral("/data/CEMS-SERVICE-ZK-REGISTER/serverAreaMain/node0000000022/8","123123");

        String dateJdbcPath = "/data/00FF0600/A/node0000000000/"+IAddressService.FTP;
        JSONObject resultObj = zkClient.readData(dateJdbcPath,true);
        System.out.println("resultObj->"+resultObj);


        /*zkClient.subscribeChildChanges("/root/address/enode",new IZkChildListener(){

            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                System.out.println("parentPath->"+parentPath);
                if(currentChilds!=null){
                    System.out.println("curentChilds->"+currentChilds.toArray().toString());
                }
            }
        });*/

        /*while(true){

            Thread.sleep(1000);
        }*/

    }

}