package com.vrv.cems.zkclient; /**
 * Copyright 2010 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.vrv.cems.service.base.SystemConstants;
import org.apache.log4j.Logger;
import org.apache.zookeeper.Watcher;

import java.io.*;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.Callable;

public class ZkSystem {


    protected static final Logger LOG = Logger.getLogger(ZkSystem.class);

    private static String serviceId;
    private static String serviceName;
    private static String serverAreaId;
    private static String[] zkServerConnects;

    /**zk的服务类*/
    private static volatile ZkSystem _instance;
    private static Set<ZkClient> _zkClients = new HashSet<>();

    /**在zk存入信息位置*/
    private String addrOwnDataPath;
    /**
     * 增加回调接口处理
     */
    public interface InitCallable {
        void runInit();
    }

    private static ZkSystem.InitCallable _callable;

    public static void setInitCallable(InitCallable _callable) {
        ZkSystem._callable = _callable;
    }

    private final static IZkStateListener stateListener = new IZkStateListener(){
        @Override
        public void handleStateChanged(Watcher.Event.KeeperState keeperState, ZkClient zkClient) throws Exception {
            //zk 状态发生变化,临时节点中,清除无效的客户连接
            if(keeperState == Watcher.Event.KeeperState.Disconnected
                    || keeperState == Watcher.Event.KeeperState.Expired){
                LOG.debug("close zk client state have been [Disconnected/Expired]");
                _instance.rmZkClient(zkClient);
            }
        }

        @Override
        public void handleNewSession(ZkClient zkClient) throws Exception {
            //zk 重新连接上
            LOG.debug("zk restart,create a whole session");
            boolean emptyClientFlg = ZkSystem.isAllClientOffline();
            _instance.addZkClient(zkClient);
            if(emptyClientFlg && _callable!=null){
                //如果zkSystem初始化的
                _callable.runInit();
            }

        }

        @Override
        public void handleSessionEstablishmentError(Throwable throwable) throws Exception {
            LOG.error("new zk session establish error", throwable);
        }
    };

    public ZkSystem() {
        LOG.info("~~~~~~~~~~~~~~~ starting zk system ~~~~~~~~~~~~~~~");
        InputStream resourceAsStream = null;

        try {
            resourceAsStream = new FileInputStream(new File(SystemConstants.PATH_CONFIG_PROPERTIES));
            Properties pro = new Properties();
            pro.load(resourceAsStream);

            serviceName = pro.getProperty("service.name");
            serverAreaId = pro.getProperty("service.serverAreaId");
            serviceId = pro.getProperty("service.id");
            zkServerConnects = pro.getProperty("service.zk").split(",");
            if (zkServerConnects == null) {
                LOG.error("can't find service.zk info");
                return;
            }
            for (String connectString : zkServerConnects) {
                try {
                    LOG.debug("connect string : " + connectString);
                    final ZkConnection zkConnection = new ZkConnection(connectString);
                    Long startTime = System.currentTimeMillis();
                    final ZkClient _zkClient = new ZkClient(zkConnection,15000);
                    System.err.println("consume:"+ (System.currentTimeMillis()-startTime));
                    //加上状态变化监听
                    _zkClient.subscribeStateChanges(stateListener);
                    _zkClients.add(_zkClient);
                } catch (Exception e) {
                    LOG.error("init connect [" + connectString + "] errer ", e.getCause());
                    continue;
                }
            }

            /*new Timer().schedule(new TimerTask(){
                @Override
                public void run() {
                    LOG.info("_zkClients.size----------->" + _zkClients.size());
                }
            },1000,1000);*/

        } catch (FileNotFoundException e) {
            LOG.error("can't load zk.properties file [" + SystemConstants.PATH_CONFIG_PROPERTIES + "]");
        } catch (IOException e) {
            LOG.error("io exception occure when load input stream");
        } finally {
            if (resourceAsStream != null) {
                try {
                    resourceAsStream.close();
                } catch (IOException e) {
                    LOG.error("io exception occure when stream close");
                }
            }
        }
        LOG.info("~~~~~~~~~~~~~~~ zk system started ~~~~~~~~~~~~~~~");
    }

    public void cleanupZk() {
        LOG.info("cleanup zk namespace");
        List<String> children = getZkClient().getChildren("/");
        for (String child : children) {
            if (!child.equals("zookeeper")) {
                getZkClient().deleteRecursive("/" + child);
            }
        }
        LOG.info("unsubscribing " + getZkClient().numberOfListeners() + " listeners");
        getZkClient().unsubscribeAll();
    }

    /**
     单例模式启动
     */
    public static ZkSystem getInstance() {
        if (_instance == null) {
            _instance = new ZkSystem();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    LOG.info("shutting zk down");
                    _instance.shutdownAllClient();
                }
            });
        }
        return _instance;
    }


    private void shutdownAllClient() {
        for (ZkClient _zkClient : _zkClients) {
            if (_zkClient != null) {
                _zkClient.close();
            }
        }
    }

    public static String getServiceName() {
        return serviceName;
    }

    public ZkClient getZkClient() {
        /** 无序遍历，选取随机有效客户端*/
        Iterator i$ = _zkClients.iterator();
        ZkClient _zkClient;
        do {
            if (!i$.hasNext()) {
                LOG.error("all client are unavailable");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) { }
                return getZkClient();
            }
            _zkClient = (ZkClient) i$.next();
        } while (_zkClient == null);

        return _zkClient;
    }


    /**
     * 从工作池中移除
     */
    public  void rmZkClient(ZkClient rmZkClient) {

        Iterator i$ = _zkClients.iterator();
        ZkClient _zkClient;
        while(i$.hasNext()){
            _zkClient = (ZkClient) i$.next();
            if(_zkClient == rmZkClient){
                _zkClients.remove(_zkClient);
                break;
            }
        }

    }

    /**
     *  判断是否管理的
     */
    public static boolean isAllClientOffline(){
        return _zkClients.isEmpty()?true:false;
    }

    /**
     *  向工作池中增加元素
     */
    public void addZkClient(ZkClient rmZkClient) {
        _zkClients.add(rmZkClient);
    }


    /**
     * 给每个客户端都增加一个状态监听
     * @param zkkStateListener
     */
    public  void addZkStateListener(IZkStateListener zkkStateListener) {
        //针对客户端状态进行监控
        if (zkkStateListener != null) {
            for (ZkClient _zkClient : _zkClients) {
                if (_zkClient != null) {
                    _zkClient.subscribeStateChanges(zkkStateListener);
                }
            }
        }
    }

    /**
     * 给每个客户端都增加一个数据变化监听
     */
    public  void addZkDataListener(String path,IZkDataListener dataListener) {
        //针对客户端path上的数据变化进行监听
        if (dataListener != null) {
            for (ZkClient _zkClient : _zkClients) {
                if (_zkClient != null) {
                    _zkClient.subscribeDataChanges(path,dataListener);
                }
            }
        }
    }



    /**
     * 去除所有其他监听
     */
    public  void removeZkkStateListener() {
        //针对客户端状态进行监控
        for (ZkClient _zkClient : _zkClients) {
            if (_zkClient != null) {
                _zkClient.unsubscribeAll();
            }
        }
    }


    public static String getServiceId() {
        return serviceId;
    }

    public static String getServerAreaId() {
        return serverAreaId;
    }

    public String getAddrOwnDataPath() {
        return addrOwnDataPath;
    }

    public void setAddrOwnDataPath(String addrOwnDataPath) {
        this.addrOwnDataPath = addrOwnDataPath;
    }
}
