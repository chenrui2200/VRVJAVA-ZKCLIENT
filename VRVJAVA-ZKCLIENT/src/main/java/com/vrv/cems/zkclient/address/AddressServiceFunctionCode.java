package com.vrv.cems.zkclient.address;

import com.sys.common.util.Assert;
import com.vrv.cems.service.base.interfaces.IAddressService;

import java.util.HashMap;
import java.util.Map;

/** 
 *   <B>说       明</B>:地址服务功能号枚举类。
 *
 * @author  作  者  名：陈 明<br/>
 *		    E-mail ：chenming@vrvmail.com.cn
 
 * @version 版   本  号：V1.0.<br/>
 *          创建时间：2016年3月21日 下午3:25:15 
 */
public enum AddressServiceFunctionCode {
	/**
	 * WEB平台更新服务策略
	 */
	ADDRESS_MINCODE_1000("1000"),
	/**
	 * 获取数据库连接地址子功能号
	 */
	ADDRESS_MINCODE_1("1"),
	/**
	 * 获取缓存地址子功能号
	 */
	ADDRESS_MINCODE_2("2"),
	/**
	 * 获取FTP地址子功能号
	 */
	ADDRESS_MINCODE_3("3"),
	/**
	 * 获取FastDFS子功能号
	 */
	ADDRESS_MINCODE_4("4"),
	/**
	 * 获取CEMS平台地址子功能号
	 */
	ADDRESS_MINCODE_5("5"),
	/**
	 * 获取CEMSUP平台地址子功能号
	 */
	ADDRESS_MINCODE_6("6"),
	/**
	 * 获取配置服务地址子功能号
	 */
	ADDRESS_MINCODE_7("7"),
	/**
	 * 获取服务信息子功能号
	 */
	ADDRESS_MINCODE_8("8"),
	/**
	 * 获取标准服务有效外网映射子功能号
	 */
	ADDRESS_MINCODE_9(IAddressService.IADDRESSSERVICE_9),
	/**
	 * 获取中转服务有效外网映射子功能号
	 */
	ADDRESS_MINCODE_10(IAddressService.IADDRESSSERVICE_10),
	/**
	 * 服务器网络映射变更通知子功能号
	 */
	ADDRESS_MINCODE_11(IAddressService.IADDRESSSERVICE_11),
	
	/**
	 * 拉取服务策略
	 */
	ADDRESS_MINCODE_12(IAddressService.IADDRESSSERVICE_12),
	/**
	 * 获取系统时间
	 */
	ADDRESS_MINCODE_13(IAddressService.IADDRESSSERVICE_13),
	/**
	 * 获取ES连接信息
	 */
	ADDRESS_MINCODE_14(IAddressService.ES),
	
	/**
	 * XXX 获取web服务通信信息
	 */
	ADDRESS_MINCODE_15("15"),
	
	/**
	 * XXX 获取mq连接信息
	 */
	ADDRESS_MINCODE_16("16");
	
	private static final Map<String, AddressServiceFunctionCode> lookupMinCode = new HashMap<String, AddressServiceFunctionCode>();
	static{
		for(AddressServiceFunctionCode asfc : values()){
			lookupMinCode.put(asfc.getMinCode(), asfc);
		}
	}
	private final String minCode;

	public static boolean checkMinCode(String minCode){
		return lookupMinCode.containsKey(minCode);
	}
	public static AddressServiceFunctionCode minCodeToServiceFunctionCode(String minCode){
		Assert.notNull(minCode);
		AddressServiceFunctionCode adderssServiceFunctionCode = lookupMinCode.get(minCode);
		if(adderssServiceFunctionCode == null){
			throw new IllegalStateException("Illegal AdderssServiceFunctionCode MinCode: "+minCode);
		}
		return adderssServiceFunctionCode;
	}
	private AddressServiceFunctionCode(String minCode) {
		this.minCode = minCode;
	}

	public String getMinCode() {
		return minCode;
	}
	
}
 