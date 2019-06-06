package cn.digitalpublishing.service;

import java.util.List;
import java.util.Map;

import cn.digitalpublishing.ep.po.CAccount;

public interface CAccountService {

	/**
	 * 获取登录信息
	 * 
	 * @param condition
	 * @param sort
	 * @return
	 */
	public List<CAccount> getList(Map<String, Object> condition, String sort) throws Exception;

}
