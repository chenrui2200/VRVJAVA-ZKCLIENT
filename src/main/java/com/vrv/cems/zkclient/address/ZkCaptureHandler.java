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
        List<String> addrChild = _zkClient.getChildren(dataPath);

        if(addrChild!=null && addrChild.size()>0){

            String dateJdbcPath = dataPath + "/" + addrChild.get(0) + "/" + (key+":"+ (type==null?"--":type) );
            String json= _zkClient.readData(dateJdbcPath,true);
            if(json == null){
                return null;
            }

            JSONArray jsonArray = JSONArray.fromObject(json);
            if(jsonArray == null){
                LOG.warn("no invalid address data on path["+dateJdbcPath+"]");
                return null;
            }
            if(args.length > 0){
                Map paramMap = new HashMap<>();
                for(int i=0;i<args.length;i+=2) {
                    String targetKey = args[i];
                    String targetValue = args[i + 1];
                    paramMap.put(targetKey,targetValue);
                }
                resultObject.put("jdata",filterTargetJSONObject(jsonArray,paramMap));
                return resultObject;

            }else{
                resultObject.put("jdata",jsonArray);
                return resultObject;

            }
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
