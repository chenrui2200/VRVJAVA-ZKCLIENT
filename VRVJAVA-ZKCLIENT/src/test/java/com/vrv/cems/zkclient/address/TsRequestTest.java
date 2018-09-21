package com.vrv.cems.zkclient.address;

import com.sys.common.util.Assert;
import com.vrv.cems.service.base.ServiceInvokerTest;
import com.vrv.cems.service.base.SystemConstants;
import com.vrv.cems.service.base.interfaces.IAddressService;
import net.sf.json.JSONObject;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;

/**
 * <B>说       明</B>:thrift所有接口单元测试类。
 *
 * @author 作  者  名：陈 明<br/>
 * E-mail ：chenming@vrvmail.com.cn
 * @version 版   本  号：V1.0.<br/>
 * 创建时间：2016年3月22日 下午4:29:07
 */
public class TsRequestTest {
    private String host = "192.168.0.133";
    private int port = 8600;
    private String maxCode = IAddressService.SERVICE_CODE;
    private boolean isEncrypt = false;
    private boolean isZip = false;

    /**
     * 测试DB
     *****/
    @Test
    public void testConfigDB() {

        String jdbcKey = ZkAddressContent.POLICY_XML_PARAMS_PARAMBEAN_JDBC_KEY_VALUE;
        String type = "SERVICE";

        JSONObject dataObj = ZkCaptureHandler.getResult(jdbcKey, type, "SQLType", "MySQL");

        String driverName = dataObj.getString("driverName");
        String url = dataObj.getString("url");
        String username = dataObj.getString("username");
        String password = dataObj.getString("password");

        System.out.println("driverName->" + driverName
                + ",url->" +url
                + ",username->" + username
                + ",password->" +password
        );
        Assert.notNull(dataObj, "调用地址服务minCode=[" + jdbcKey + "]结果不能为null!!!");
    }


    /***
     * 测试cach内容
     * ***/
    @Test
    public void testCach() {

        String jdbcKey = ZkAddressContent.POLICY_XML_PARAMS_PARAMBEAN_CACHE_KEY_VALUE;
        String type = "REDIS";

        JSONObject result = ZkCaptureHandler.getResult(jdbcKey, type);
        System.out.println("result->" + result);
        Assert.notNull(result, "调用地址服务minCode=[" + jdbcKey + "]结果不能为null!!!");
    }

    /***
     * 测试ftp内容
     * ***/
    @Test
    public void testFtp() {

        String jdbcKey = ZkAddressContent.POLICY_XML_PARAMS_PARAMBEAN_FTP_KEY_VALUE;
        String type = null;

        JSONObject result = ZkCaptureHandler.getResult(jdbcKey, type);
        System.out.println("result->" + result);
        Assert.notNull(result, "调用地址服务minCode=[" + jdbcKey + "]结果不能为null!!!");

    }


    /**
     * 测试Fastdfs
     *****/
    @Test
    public void testFastdfs() {
        String jdbcKey = ZkAddressContent.POLICY_XML_PARAMS_PARAMBEAN_FASTDFS_KEY_VALUE;
        String type = null;

        JSONObject result = ZkCaptureHandler.getResult(jdbcKey, type);
        System.out.println("result->" + result);
        Assert.notNull(result, "调用地址服务minCode=[" + jdbcKey + "]结果不能为null!!!");
    }

    /**
     * 测试cemsAddress
     *****/
    @Test
    public void testCemsAddress() {
        String jdbcKey = ZkAddressContent.POLICY_XML_PARAMS_PARAMBEAN_CEMSADDRESS_KEY_VALUE;
        String type = null;

        JSONObject result = ZkCaptureHandler.getResult(jdbcKey, type);
        System.out.println("result->" + result);
        Assert.notNull(result, "调用地址服务minCode=[" + jdbcKey + "]结果不能为null!!!");
    }

    /**
     * 测试CemsupAddress
     *****/
    @Test
    public void testCemsupAddress() {

        String jdbcKey = ZkAddressContent.POLICY_XML_PARAMS_PARAMBEAN_CEMSUPADDRESS_KEY_VALUE;
        String type = null;

        JSONObject result = ZkCaptureHandler.getResult(jdbcKey, type);
        System.out.println("result->" + result);
        Assert.notNull(result, "调用地址服务minCode=[" + jdbcKey + "]结果不能为null!!!");
    }

    /**
     * 测试ConfigServer
     *****/
    @Test
    public void testConfigServer() {

        String jdbcKey = ZkAddressContent.POLICY_XML_PARAMS_PARAMBEAN_CONFIGSERVER_KEY_VALUE;
        String type = null;

        JSONObject result = ZkCaptureHandler.getResult(jdbcKey, type);
        System.out.println("result->" + result);
        Assert.notNull(result, "调用地址服务minCode=[" + jdbcKey + "]结果不能为null!!!");
    }


    /**
     * 测试获取标准服务有效映射信息
     *****/
    @Test
    public void testGetStandardServiceInfo() {
        String minCode = "9";
        String scrData = "[{\"ip\":\"192.168.0.135\",\"port\":\"8200\"}]";

        ServiceInvokerTest.Builder builder = new ServiceInvokerTest.Builder(host, port, maxCode, minCode);
        builder.setData(scrData);
        builder.isEncrypt(isEncrypt);
        builder.isZip(isZip);
        //builder.setAlgorithm(RandomAlgorithm.SM4);
        builder.build();
        JSONObject result = ServiceInvokerTest.from(builder).testInvokeTS();
        System.out.println("standardServiceInfo: " + result);
        Assert.notNull(result, "调用地址服务minCode=[" + minCode + "]结果不能为null!!!");
    }

    /**
     * 测试获取中转服务有效映射信息
     *****/
    @Test
    public void testGetTransServiceInfo() {
        String minCode = "10";
        String scrData = "[{\"ip\":\"192.168.0.134\",\"port\":\"10500\"}]";

        ServiceInvokerTest.Builder builder = new ServiceInvokerTest.Builder(host, port, maxCode, minCode);
        builder.setData(scrData);
        builder.isEncrypt(isEncrypt);
        builder.isZip(isZip);
        //builder.setAlgorithm(RandomAlgorithm.SM4);
        builder.build();
        JSONObject result = ServiceInvokerTest.from(builder).testInvokeTS();
        System.out.println("transServiceInfo: " + result);
        Assert.notNull(result, "调用地址服务minCode=[" + minCode + "]结果不能为null!!!");
    }

    /**
     * 测试服务器网络映射变更通知
     *****/
    @Test
    public void testServerNetMappingChange0() {
        String minCode = "11";
        String scrData = "[{\"id\":\"6e0175c1c3874c14a48ed123b5f26e51,6e0475c1c3874c14a42ed123b5f26e51\",\"action\":\"sync\"}]";

        ServiceInvokerTest.Builder builder = new ServiceInvokerTest.Builder(host, port, maxCode, minCode);
        builder.setData(scrData);
        builder.isEncrypt(isEncrypt);
        builder.isZip(isZip);
        //builder.setAlgorithm(RandomAlgorithm.SM4);
        builder.build();
        JSONObject result = ServiceInvokerTest.from(builder).testInvokeTS();
        System.out.println("serverNetMapping: " + result);
        Assert.notNull(result, "调用地址服务minCode=[" + minCode + "]结果不能为null!!!");
    }

    /**
     * 测试服务器网络映射变更通知
     *****/
    @Test
    public void testServerNetMappingChange1() {
        String minCode = "11";
        String scrData = "[{\"id\":\"all\",\"action\":\"sync\"}]";

        ServiceInvokerTest.Builder builder = new ServiceInvokerTest.Builder(host, port, maxCode, minCode);
        builder.setData(scrData);
        builder.isEncrypt(isEncrypt);
        builder.isZip(isZip);
        //builder.setAlgorithm(RandomAlgorithm.SM4);
        builder.build();
        JSONObject result = ServiceInvokerTest.from(builder).testInvokeTS();
        System.out.println("serverNetMapping: " + result);
        Assert.notNull(result, "调用地址服务minCode=[" + minCode + "]结果不能为null!!!");
    }
}
 