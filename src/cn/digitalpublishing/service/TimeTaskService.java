package cn.digitalpublishing.service;

import java.util.List;

public interface TimeTaskService {
	
	/**
	 * 系统自动进行查询提醒表（alerts），进行对新上架的产品向订阅该学科主题的用户发送新书上架提醒邮件
	 * 
	 * 1.定时器每天凌晨一点执行查询操作
	 * 2.按照提醒频率进行发送任务
	 * 		a.频率-每天 的在凌晨一点进行发送前一天的。
	 * 		b.频率-每周 的在每周一的凌晨一点进行发送前一周的。
	 * 		c.频率-每月 的在每月第一天的凌晨一点进行发送前一个月的。
	 * @throws Exception
	 */
	public void autoHandleAlerts()throws Exception;
	
	
	public List<String> deleteIndex()throws Exception;


	/**
	 * 系统自动进行查询（LUserAlertsLog），进行续订提醒
	 * 
	 * @throws Exception
	 */
	public void autoRenewalAlerts()throws Exception;
	
}
