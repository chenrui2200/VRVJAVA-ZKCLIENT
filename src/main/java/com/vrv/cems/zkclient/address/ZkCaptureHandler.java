package com.vrv.cems.zkclient.address;

import com.vrv.cems.service.base.interfaces.IAddressService;
import com.vrv.cems.zkclient.ZkClient;
import com.vrv.cems.zkclient.ZkSystem;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <B>说       明</B>:服务区域变更辅助类。
 *
 * @author 作  者  名：陈  锐<br/>
 * E-mail ：chenming@vrvmail.com.cn
 * @version 版   本  号：1.0.0 <br/>
 * 创建时间 17:08
 */
public class ZkCaptureHandler {

    private static final ZkSystem _zkSystem= ZkSystem.getInstance();
    private static final  String maxCode = IAddressService.SERVICE_CODE;
    protected static final Logger LOG = Logger.getLogger(ZkCaptureHandler.class);

    public static JSONObject getResult(String key, String type , String ... args) {

        JSONObject resultObject = new JSONObject();

        ZkClient _zkClient = _zkSystem.getZkClient();
        String dataPath = "/data/"+maxCode+"/" +ZkSystem.getServerAreaId();

        //做等待，看是否能在等待时间里出现该节点
        if(_zkClient.waitUntilExists(dataPath,TimeUnit.SECONDS,10)){
            LOG.error("wait 10 seconds but znode["+dataPath+"]not exists");
            return null;
        }
        //查找子节点
        List<String> addrChild = _zkClient.getChildren(dataPath);

        if(addrChild!=null && addrChild.size()>0){

            String dateJdbcPath = dataPath + "/" + addrChild.get(0) + "/" + (key+":"+ (type==null?"--":type) );

            JSONObject jsonObject= _zkClient.waitUntilRead(dateJdbcPath,TimeUnit.SECONDS,10);
            if(jsonObject == null){
                LOG.warn("no invalid address data on path["+dateJdbcPath+"]");
                return null;
            }

            return jsonObject;
        }else{
            LOG.warn("no invalid address data node");
            return null;
        }
    }

    private static JSONArray filterTargetJSONObject(JSONArray jsonArray, Map paramMap) {
        JSONArray arr = new JSONArray();

        for(int i=0;i<jsonArray.size();i++){

            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Iterator<Map.Entry<String,String>> it = paramMap.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<String,String> entry = it.next();
                String key = entry.getKey();
                String value = entry.getValue();
                if( value.equals(jsonObject.get(key))){
                    arr.add(jsonObject);
                }
            }
        }
        LOG.warn("no invalid address data after filter method");
        return arr;
    }

}
