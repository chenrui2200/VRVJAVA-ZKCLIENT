package com.vrv.cems.zkclient.address;

import net.sf.json.JSONObject;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.concurrent.Callable;

/** 
 *   <B>说       明</B>:thrift所有接口单元测试类。
 *
 * @author  作  者  名：陈 明<br/>
 *		    E-mail ：chenming@vrvmail.com.cn
 
 * @version 版   本  号：V1.0.<br/>
 *          创建时间：2016年3月22日 下午4:29:07 
 */
public class EncrptTsRequestTest {


	/**
	 * 测试DB
	 * *****/
	@Test
	public void testConfigDB() {

		ByteBuffer buf = new RequestEncrptOutput("maxCode","minCode",true,"1",
				"sessionId",0,false,
				 new Callable<JSONObject>(){

					 @Override
					 public JSONObject call() throws Exception {
						 String jdbcKey =ZkAddressContent.POLICY_XML_PARAMS_PARAMBEAN_JDBC_KEY_VALUE;
						 String type = "SERVICE";
						 return  ZkCaptureHandler.getResult(jdbcKey,type,"SQLType","MySQL");
					 }
				 }
		 ).encrpty();
		 System.out.println("buf->"+ buf);
	}
	

}
 