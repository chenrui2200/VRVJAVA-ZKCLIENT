package com.vrv.cems.zkclient.address;

import com.sys.common.util.SystemUtils;
import com.sys.common.util.security.CRCUtils;
import com.vrv.cems.base.AsymmetricCipherHandler;
import com.vrv.cems.base.CipherHandler;
import com.vrv.cems.service.base.RedisCacheServiceAdapterHandler;
import com.vrv.cems.service.base.SystemConstants;
import com.vrv.cems.service.base.bean.ResponseBuilder;
import com.vrv.cems.service.base.interfaces.IAddressService;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.concurrent.Callable;

import static com.vrv.cems.service.base.SystemConstants.CLIENT_DEFAULT_SESSION_ID;

/**
 * <B>说       明</B>:该方法帮助对查询到的信息加密。
 *
 * @author 作  者  名：陈  锐<br/>
 * E-mail ：chenming@vrvmail.com.cn
 * @version 版   本  号：1.0.0 <br/>
 * 创建时间 14:33
 */
public class RequestEncrptOutput implements Serializable {

    protected static final Logger logger = Logger.getLogger(RequestEncrptOutput.class);

    private String maxCode;
    private String minCode;
    private Boolean encrypt;
    private String key;
    private String sessionId;
    private int flag;
    private boolean zip;
    private JSONObject result;
    private Callable<JSONObject> callable;

    public RequestEncrptOutput(String maxCode, String minCode, Boolean encrypt, String key, String sessionId, int flag, boolean zip, Callable<JSONObject> callable) {
        this.maxCode = maxCode;
        this.minCode = minCode;
        this.encrypt = encrypt;
        this.key = key;
        this.sessionId = sessionId;
        this.flag = flag;
        this.zip = zip;
        this.callable = callable;
    }

    public String toJson() {
        JSONObject obj = new JSONObject();
        obj.put("maxCode", this.maxCode);
        obj.put("minCode", this.minCode);
        obj.put("result", this.result);
        return obj.toString();
    }

    public  ByteBuffer encrpty (){

        logger.info("[@TsRequestTransferHandler] maxCode:" + maxCode + " ,minCode:" + minCode);

        //组装返回结果对象
        final ResponseBuilder.Builder responseBuilder = new ResponseBuilder.Builder(maxCode,minCode);
        responseBuilder.setEncrypt(encrypt);
        responseBuilder.setZip(zip);
        responseBuilder.setKey(key);
        responseBuilder.setFlag(flag);
        responseBuilder.setSessionId(sessionId);

        //判断服务功能号
        if(!IAddressService.SERVICE_CODE.equals(maxCode)){
            return ResponseBuilder.outputErrorEncryptResultInfo(responseBuilder, "maxCode error !!!");
        }
        //判断子功能号
        if(!AddressServiceFunctionCode.checkMinCode(minCode)){
            return ResponseBuilder.outputErrorEncryptResultInfo(responseBuilder, "minCode error !!!");
        }
        try {
            result = callable.call();
        } catch (Exception e) {
             logger.error("回调获取信息失败",e.getCause());
        }
        String data = this.toJson();
        if (logger.isDebugEnabled()) {
            logger.debug("response data: --> "+data);
        }
        if (sessionId != null) {
            if (CLIENT_DEFAULT_SESSION_ID.equals(sessionId)) {
                byte[] bytes = CipherHandler.asymmetricEncryptTC(zip, data);
                return ByteBuffer.wrap(bytes);
            } else {

                if (SystemConstants.CLIENT_DEFAULT_SESSION_ID.equals(this.sessionId)) {
                    byte[] eData =  AsymmetricCipherHandler.encryptByPublicKey(data);
                    return ByteBuffer.wrap(eData);
                } else {
                    CipherHandler.TcCryptHolder tcCryptHolder = RedisCacheServiceAdapterHandler.queryTcCryptHolder(sessionId);
                    if (tcCryptHolder != null) {
                        byte[] bytes =  CipherHandler.encryptTC(zip, data, tcCryptHolder);
                        return ByteBuffer.wrap(bytes);
                    }
                }
                logger.error("无效的客户端sessionId=[" + this.sessionId + "]");
            }
            logger.error("无效的客户端sessionId=["+sessionId+"]");
        } else if (key != null && flag != 0) {
            byte[] bytes =  CipherHandler.encryptTS(encrypt,zip,key,flag,data);
            return ByteBuffer.wrap(bytes);
        }
        logger.error("数据加密失败,加密getDataTS()接口返回数据时，需设置[isEncrypt,isZip,key,flag]等参数；加密getDataTC()接口返回数据时，需设置[isZip,sessionId]等参数!!!");
        return ByteBuffer.wrap(data.getBytes(SystemUtils.DEFAULT_CHARSET));

    }
}
