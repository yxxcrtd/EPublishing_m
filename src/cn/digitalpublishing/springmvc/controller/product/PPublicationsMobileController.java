package cn.digitalpublishing.springmvc.controller.product;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import cn.com.daxtech.framework.Internationalization.Lang;
import cn.com.daxtech.framework.exception.CcsException;
import cn.com.daxtech.framework.model.Param;
import cn.com.daxtech.framework.util.StringUtil;
import cn.digitalpublishing.ep.po.BInstitution;
import cn.digitalpublishing.ep.po.BIpRange;
import cn.digitalpublishing.ep.po.CFavourites;
import cn.digitalpublishing.ep.po.CSearchHis;
import cn.digitalpublishing.ep.po.CUser;
import cn.digitalpublishing.ep.po.LAccess;
import cn.digitalpublishing.ep.po.LComplicating;
import cn.digitalpublishing.ep.po.LLicense;
import cn.digitalpublishing.ep.po.LLicenseIp;
import cn.digitalpublishing.ep.po.OOrderDetail;
import cn.digitalpublishing.ep.po.PCcRelation;
import cn.digitalpublishing.ep.po.PContentRelation;
import cn.digitalpublishing.ep.po.PCsRelation;
import cn.digitalpublishing.ep.po.PPrice;
import cn.digitalpublishing.ep.po.PPublications;
import cn.digitalpublishing.springmvc.controller.BaseController;
import cn.digitalpublishing.springmvc.form.index.IndexForm;
import cn.digitalpublishing.springmvc.form.product.PPublicationsForm;
import cn.digitalpublishing.util.io.FileUtil;
import cn.digitalpublishing.util.io.MD5Util;
import cn.digitalpublishing.util.web.ComparatorSubject;
import cn.digitalpublishing.util.web.DateUtil;
import cn.digitalpublishing.util.web.IpUtil;
import cn.digitalpublishing.util.web.MathHelper;
import cn.digitalpublishing.util.web.SequenceUtil;
import cn.digitalpublishing.util.web.dawsonEncryption;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("mobile/pages/publications")
public class PPublicationsMobileController extends BaseController {

	/**
	 * 期刊二级页面
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/journal")
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response, PPublicationsForm form) throws Exception {
		String forwardString = "mobile/publications/journalList";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			form.setUrl(request.getRequestURL().toString());

			Map<String, Object> condition = new HashMap<String, Object>();

			String order = "order by a.updateOn desc";

			condition.put("type", 2);
			condition.put("status", 2);
			condition.put("oa", 2);

			List<PPublications> list = this.pPublicationsService.getpublicationsPagingList(condition, order.toString(), 20, 1);
			model.put("list", list);

			// 语种
			Map<String, Object> lang = new HashMap<String, Object>();
			// 分类法
			Map<String, Object> classify = new HashMap<String, Object>();
			// 出版社
			Map<String, Object> publisher = new HashMap<String, Object>();
			// 出版时间
			Map<String, Object> pubDate = new HashMap<String, Object>();

			int i = 0;
			for (PPublications p : list) {
				if (!lang.containsKey(p.getLang())) {
					lang.put(p.getLang(), "");
				}
				i++;
			}
			model.put("langCount", i);
			i = 0;
			for (PPublications p : list) {
				if (!classify.containsKey(p.getPubSubject())) {
					classify.put(p.getPubSubject(), "");
				}
			}
			model.put("classifyCount", i);
			i = 0;
			for (PPublications p : list) {
				if (!publisher.containsKey(p.getPublisherName())) {
					publisher.put(p.getPublisher().getName(), "");
				}
			}
			model.put("publisherCount", i);
			i = 0;
			for (PPublications p : list) {
				if (!pubDate.containsKey(p.getPubDate())) {
					pubDate.put(p.getPubDate(), "");
				}
				pubDate.put("size", i++);
			}
			// 获取机构ID
			BInstitution ins = (BInstitution) request.getSession().getAttribute("institution");
			model.put("insInfo", ins == null ? null : ins.getId());
			model.put("lang", lang);
			model.put("classify", classify);
			model.put("publisher", publisher);
			model.put("pubDate", pubDate);
		} catch (Exception e) {
			form.setMsg((e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		model.put("form", form);
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 期刊二级页面JSON
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/journalJson")
	public ModelAndView journalJson(HttpServletRequest request, HttpServletResponse response, PPublicationsForm form, String pubDateOrder, String shelvesDateOrder) throws Exception {
		String forwardString = "mobile/publications/journalList";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			form.setUrl(request.getRequestURL().toString());

			Map<String, Object> condition = new HashMap<String, Object>();

			String order = "order by a.updateOn desc";
			// 按出版日期排序
			if (StringUtils.isNotEmpty(pubDateOrder) && "asc".equals(pubDateOrder)) {
				order = "order by a.pubDate asc";
			} else if (StringUtils.isNotEmpty(pubDateOrder) && "desc".equals(pubDateOrder)) {
				order = "order by a.pubDate desc";
			}
			// 按上架时间
			if (StringUtils.isNotEmpty(shelvesDateOrder) && "asc".equals(shelvesDateOrder)) {
				order += " , a.createOn asc";
			} else if (StringUtils.isNotEmpty(shelvesDateOrder) && "desc".equals(shelvesDateOrder)) {
				order += " , a.createOn desc";
			}

			condition.put("type", 2);
			condition.put("status", 2);
			condition.put("oa", 2);

			List<PPublications> list = this.pPublicationsService.getpublicationsPagingList(condition, order.toString(), 20, form.getCurpage());
			model.put("list", list);

			// 把list转成json对象
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("list", list);
			map.put("curpage", form.getCurpage() + 1);
			JSONObject json = JSONObject.fromObject(map);
			response.setContentType("text/json;charset=utf-8");
			PrintWriter writer = null;
			try {
				// 获取输出流
				writer = response.getWriter();
				writer.print(json.toString());
			} catch (IOException e) {
				// e.printStackTrace();
				throw e;
			} finally {
				if (writer != null) {
					writer.close();
				}
			}

			// 获取机构ID
			BInstitution ins = (BInstitution) request.getSession().getAttribute("institution");
			model.put("insInfo", ins == null ? null : ins.getId());
		} catch (Exception e) {
			form.setMsg((e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		model.put("form", form);
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 最新资源
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "lastPubsBook")
	public ModelAndView lastPubsBook(HttpServletRequest request, HttpServletResponse response, IndexForm form, String isCn, String isJson) throws Exception {
		String forwardString = "";
		if ("true".equals(isCn)) {
			forwardString = "mobile/publications/cnBookList";
		} else {
			forwardString = "mobile/publications/enBookList";
		}
		CUser user1 = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
		String toTab = request.getSession().getAttribute("selectType").toString();
		String selectFlag = form.getSelectflag();
		if (selectFlag != null) {
			if (selectFlag == "oaFree" || selectFlag.equals("oaFree")) {
				toTab = "2";
			}
			if (selectFlag == "license" || selectFlag.equals("license")) {
				toTab = "1";
			}
			if (selectFlag == "all" || selectFlag.equals("all")) {
				toTab = "";
			}
		}
		//默认按上架时间倒序排序
		if(form.getSearchOrder()==null && form.getSortFlag()==null ){
			form.setSearchOrder("pubDate");
			form.setSortFlag("desc");
		}
		// BInstitution ins = (BInstitution)
		// request.getSession().getAttribute("institution");
		// if (null != user1 || null != ins) {
		// form.setLcense("1");
		// }
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			form.setUrl(request.getRequestURL().toString());

			/*********** 查询条件区*开始 ***************/
			Map<String, String> param = new HashMap<String, String>();
			/** 特殊机构处理 -- START */
			String specialInstitutionFlag = request.getSession().getAttribute("specialInstitutionFlag") != null ? (String) request.getSession().getAttribute("specialInstitutionFlag") : null;
			if (null != specialInstitutionFlag && specialInstitutionFlag.length() > 0) {
				form = this.specialInstitution_handle(form, specialInstitutionFlag);
			}
			/** 特殊机构处理 -- END */
			param.put("searchValue", (form.getSearchValue() == null || "".equals(form.getSearchValue())) ? null : URLDecoder.decode(form.getSearchValue(), "UTF-8"));
			param.put("keywordCondition", (form.getKeywordCondition() == null || "".equals(form.getKeywordCondition())) ? null : form.getKeywordCondition().toString());
			param.put("notKeywords", (form.getNotKeywords() == null || "".equals(form.getNotKeywords())) ? null : form.getNotKeywords());
			param.put("title", (form.getTitle() == null || "".equals(form.getTitle())) ? null : form.getTitle());
			param.put("author", (form.getAuthor() == null || "".equals(form.getAuthor())) ? null : form.getAuthor());
			param.put("code", (form.getCode() == null || "".equals(form.getCode())) ? null : form.getCode());
			param.put("taxonomy", (form.getTaxonomy() == null || "".equals(form.getTaxonomy())) ? null : "\"" + form.getTaxonomy() + "\"");
			param.put("taxonomyEn", (form.getTaxonomyEn() == null || "".equals(form.getTaxonomyEn())) ? null : "\"" + form.getTaxonomyEn() + "\"");
			param.put("pubType", "1");// 图书类型
			if ("true".equals(isCn)) {
				param.put("language", "chs");// 中文
			} else {
				param.put("language", "eng");// 外文
			}

			param.put("pubDateStart", (form.getPubDateStart() == null || "".equals(form.getPubDateStart())) ? null : form.getPubDateStart());
			param.put("pubDateEnd", (form.getPubDateEnd() == null || "".equals(form.getPubDateEnd())) ? null : form.getPubDateEnd());
			// param.put("publisher", (form.getPublisher() == null ||
			// "".equals(form.getPublisher())) ? null : "\"" +
			// form.getPublisher() + "\"");
			param.put("publisher", (form.getPublisher() == null || "".equals(form.getPublisher())) ? null : form.getPublisher());
			// param.put("type",
			// (form.getPubType()==null||"".equals(form.getPubType()))?null:form.getPubType());
			param.put("pubDate", (form.getPubDate() == null || "".equals(form.getPubDate())) ? null : form.getPubDate() + "*");
			// 首字母
			param.put("prefixWord", (form.getPrefixWord() == null || "".equals(form.getPrefixWord())) ? null : form.getPrefixWord());
			// 本地资源查找条件
			param.put("local", (form.getLocal() == null || "".equals(form.getLocal())) ? null : form.getLocal());
			// 非语言
			param.put("notLanguage", (null == form.getNotLanguage()) || "".equals(form.getNotLanguage()) ? null : form.getNotLanguage());
			// 最新资源排序检索
			param.put("sortFlag", (null == form.getSortFlag()) || "".equals(form.getSortFlag()) ? null : form.getSortFlag());
			// 判断是是否在外文电子书界面
			param.put("isCn", (null == isCn || "".equals(isCn) ? null : isCn));
			/*********** 查询条件区*结束 ***************/
			/*** 中文 ***/
			if (param.containsKey("taxonomy") && param.get("taxonomy") != null) {
				String[] taxArr = param.get("taxonomy").replace("\"", "").split(",");
				model.put("taxArr", taxArr);
			}
			/*** 英文 ***/
			if (param.containsKey("taxonomyEn") && param.get("taxonomyEn") != null) {
				String[] taxArrEn = param.get("taxonomyEn").replace("\"", "").split(",");
				model.put("taxArrEn", taxArrEn);
			}

			Map<String, Object> resultMap = new HashMap<String, Object>();
			String userId;
			if (toTab == "" || toTab.equals("")) {// 按照全部查询
				request.getSession().setAttribute("selectType", "");// selectType
																	// 用来保存全局的变量，看是全部还是在已订阅中查询
																	// 2-全部
																	// 1-已订阅、

				resultMap = this.publicationsIndexService.advancedSearchMobile(form.getCurpage(), 20, param, form.getSearchOrder() + "##" + form.getSortFlag(), isCn);
				// 限制查询结果总数----------开始-----------
				Integer maxCount = Integer.parseInt(Param.getParam("search.config").get("maxCount"));
				Integer allCount = 0;
				if (resultMap.get("count") != null) {
					allCount = Integer.valueOf(resultMap.get("count").toString());
					model.put("queryCount", allCount);// 实际查询结果数量

					// 最多显示1000条
					allCount = maxCount > allCount ? allCount : maxCount;// 分页条显示的数量
				}
				// 限制查询结果总数----------结束-----------
				if (allCount > 0) {
					List<Map<String, Object>> list = (List<Map<String, Object>>) resultMap.get("result");
					List<PPublications> resultList = new ArrayList<PPublications>();
					for (Map<String, Object> idInfo : list) {
						// 根据ID查询产品信息
						// 由于加入了标签，这里不能用get查询
						Map<String, Object> condition = new HashMap<String, Object>();
						condition.put("id", idInfo.get("id"));
						// condition.put("check","false");
						condition.put("status", null);
						condition.put("available", 3);
						List<PPublications> ppList = this.pPublicationsService.getPubList3(condition, " order by a.createOn ", (CUser) request.getSession().getAttribute("mainUser"), IpUtil.getIp(request));
						if (ppList != null && ppList.size() > 0) {
							PPublications pub = ppList.get(0);
							if (pub != null && pub.getRemark() != null && "[无简介]".equals(pub.getRemark())) {
								pub.setRemark("");
							}
							if (idInfo.containsKey("title") && idInfo.get("title") != null && !"".equals("title"))
								pub.setTitle(idInfo.get("title").toString());
							if (idInfo.containsKey("author") && idInfo.get("author") != null && !"".equals("author"))
								pub.setAuthor(idInfo.get("author").toString());
							if (idInfo.containsKey("isbn") && idInfo.get("isbn") != null && !"".equals("isbn"))
								pub.setCode(idInfo.get("isbn").toString());
							if (null != idInfo.get("copyPublisher") && idInfo.containsKey("copyPublisher") && !"".equals("copyPublisher")) {
								pub.getPublisher().setName(idInfo.get("copyPublisher").toString());
							}
							if (idInfo.containsKey("remark") && idInfo.get("remark") != null && !"".equals("remark"))
								pub.setRemark(idInfo.get("remark").toString());

							if (idInfo.containsKey("score") && idInfo.get("score") != null && !"".equals("score"))
								pub.setActivity(idInfo.get("score").toString());

							if (user1 != null) {
								Map<String, Object> con = new HashMap<String, Object>();
								con.put("publicationsid", idInfo.get("id"));
								con.put("status", 2);
								con.put("userTypeId", user1 == null ? "1" : user1.getUserType() == null ? "1" : user1.getUserType().getId());
								// con.put("userTypeId",
								// user1.getUserType().getId()==null?"":user1.getUserType().getId());
								List<PPrice> price = this.pPublicationsService.getPriceList(con);
								int isFreeUser = request.getSession().getAttribute("isFreeUser") == null ? 0 : (Integer) request.getSession().getAttribute("isFreeUser");
								if (isFreeUser != 1) {
									for (int j = 0; j < price.size(); j++) {
										PPrice pr = price.get(j);
										double endPrice = MathHelper.round(MathHelper.mul(pr.getPrice(), 1.13d));
										price.get(j).setPrice(endPrice);
									}
								}
								pub.setPriceList(price);
							}
							// 查询分类
							// Map<String,Object> con2 = new
							// HashMap<String,Object>();
							// con2.put("publicationsId", pub.getId());
							// List<PCsRelation> csList =
							// this.bSubjectService.getSubPubList(con2,
							// " order by a.subject.code ");
							// pub.setCsList(csList);
							resultList.add(pub);
						}
					}
					// ----------------------进行高亮--------------
					/*
					 * if(form.getSearchValue()!=null&&!"".equals(form.
					 * getSearchValue())){ form.setKeyMap(this.highLight(0,
					 * form.getSearchValue())); }
					 */
					// ----------------------高亮结束--------------
					// model.put("pubDateMap", pubDate);
					form.setCount(allCount);
					model.put("list", resultList);
					if ("true".equals(isJson)) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("list", resultList);
						map.put("curpage", form.getCurpage() + 1);
						JSONObject json = JSONObject.fromObject(map);
						response.setContentType("text/json;charset=utf-8");
						PrintWriter writer = null;
						try {
							// 获取输出流
							writer = response.getWriter();
							writer.print(json.toString());
						} catch (IOException e) {
							// e.printStackTrace();
							throw e;
						} finally {
							if (writer != null) {
								writer.close();
							}
						}
					}
				} else {
					form.setCount(0);
				}
			} else if (toTab == "1" || toTab.equals("1")) {
				CUser user = (CUser) request.getSession().getAttribute("mainUser");
				request.getSession().setAttribute("selectType", 1);// selectType
																	// 用来保存全局的变量，看是全部还是在已订阅中查询
																	// 2-全部
																	// 1-已订阅
				StringBuffer userIds = new StringBuffer();
				// 访问IP
				long ip = IpUtil.getLongIp(IpUtil.getIp(request));
				// 查询机构信息
				Map<String, Object> mapip = new HashMap<String, Object>();
				mapip.put("ip", ip);
				List<BIpRange> lip = this.configureService.getIpRangeList(mapip, "");
				if (lip != null && lip.size() > 0) {
					// 根据机构ID,查询用户
					for (BIpRange bIpRange : lip) {
						Map<String, Object> uc = new HashMap<String, Object>();
						// uc.put("institutionId",bIpRange.getInstitution().getId()
						// );
						if (user != null && user.getLevel() == 2) {
							uc.put("institutionId", user.getInstitution().getId());
						} else {
							uc.put("institutionId", bIpRange.getInstitution().getId());
						}
						uc.put("insStatus", 1);// 1-机构未被禁用状态
						uc.put("level", 2);
						List<CUser> lu = this.cUserService.getUserList(uc, "");
						for (CUser cUser : lu) {
							userIds.append(cUser.getId()).append(",");
						}
					}
				}
				// 查询用户ID
				if (request.getSession().getAttribute("mainUser") != null) {
					user = (CUser) request.getSession().getAttribute("mainUser");
					userIds.append(user.getId()).append(",");
				}
				// userIds.append(Param.getParam("OAFree.uid.config").get("uid")).append(",");
				if ("".equals(userIds.toString())) {
					throw new CcsException("Controller.Index.searchLicense.noLogin");// 未登录用户，无法按照“已订阅”查询

				} else {
					userId = userIds.substring(0, userIds.toString().lastIndexOf(","));
					// 在solr中查询 [搜索类型=====0-全文;1-标题;2-作者]
					Integer coverType = 1;// 区分免费开源和已订阅
					resultMap = this.licenseIndexService.advancedSearch(coverType, userId, form.getCurpage(), 20, param, form.getSearchOrder() + "##" + form.getSortFlag());
					if (resultMap.get("count") != null && Long.valueOf(resultMap.get("count").toString()) > 0) {
						// 限制查询结果总数----------开始-----------
						Integer maxCount = Integer.parseInt(Param.getParam("search.config").get("maxCount"));
						Integer allCount = 0;
						if (resultMap.get("count") != null) {
							allCount = Integer.valueOf(resultMap.get("count").toString());
							model.put("queryCount", allCount);// 实际查询结果数量
							// 最多显示1000条
							allCount = maxCount > allCount ? allCount : maxCount;// 分页条显示的数量
						}
						List<Map<String, Object>> list = (List<Map<String, Object>>) resultMap.get("result");
						List<PPublications> resultList = new ArrayList<PPublications>();
						// 这里根据LicenseId查询产品
						for (Map<String, Object> idInfo : list) {
							String lid = idInfo.get("id").toString();
							String pid = "";
							if (lid.contains("_")) {
								pid = lid.substring(lid.lastIndexOf("_") + 1, lid.length());
							} else {
								LLicense lli = this.pPublicationsService.getLicense(lid);
								if (lli != null) {
									pid = lli.getPublications().getId();
								}
							}
							if (pid != null && !"".equals(pid)) {
								// 根据ID查询产品信息
								// 由于加入了标签，这里不能用get查询
								Map<String, Object> condition = new HashMap<String, Object>();
								condition.put("id", pid);
								condition.put("status", null);
								condition.put("available", 3);
								List<PPublications> ppList = this.pPublicationsService.getPubList3(condition, " order by a.createOn ", (CUser) request.getSession().getAttribute("mainUser"), IpUtil.getIp(request));
								if (ppList != null && ppList.size() > 0) {
									PPublications pub = ppList.get(0);
									if (pub != null && pub.getRemark() != null && "[无简介]".equals(pub.getRemark())) {
										pub.setRemark("");
									}
									if (idInfo.containsKey("title") && idInfo.get("title") != null && !"".equals("title"))
										pub.setTitle(idInfo.get("title").toString());
									if (idInfo.containsKey("author") && idInfo.get("author") != null && !"".equals("author"))
										pub.setAuthor(idInfo.get("author").toString());
									if (idInfo.containsKey("isbn") && idInfo.get("isbn") != null && !"".equals("isbn"))
										pub.setCode(idInfo.get("isbn").toString());
									if (idInfo.containsKey("copyPublisher") && idInfo.get("copyPublisher") != null && !"".equals("copyPublisher"))
										pub.getPublisher().setName(idInfo.get("copyPublisher").toString());
									if (idInfo.containsKey("remark") && idInfo.get("remark") != null && !"".equals("remark"))
										pub.setRemark(idInfo.get("remark").toString());

									if (idInfo.containsKey("score") && idInfo.get("score") != null && !"".equals("score"))
										pub.setActivity(idInfo.get("score").toString());
									if (user1 != null) {
										Map<String, Object> con = new HashMap<String, Object>();
										con.put("publicationsid", idInfo.get("id"));
										con.put("status", 2);
										con.put("userTypeId", user1.getUserType().getId() == null ? "" : user1.getUserType().getId());
										List<PPrice> price = this.pPublicationsService.getPriceList(con);
										int isFreeUser = request.getSession().getAttribute("isFreeUser") == null ? 0 : (Integer) request.getSession().getAttribute("isFreeUser");
										if (isFreeUser != 1) {
											for (int j = 0; j < price.size(); j++) {
												PPrice pr = price.get(j);
												double endPrice = MathHelper.round(MathHelper.mul(pr.getPrice(), 1.13d));
												price.get(j).setPrice(endPrice);
											}
										}
										pub.setPriceList(price);
									}
									// 查询分类
									// Map<String,Object> con2 = new
									// HashMap<String,Object>();
									// con2.put("publicationsId", pub.getId());
									// List<PCsRelation> csList =
									// this.bSubjectService.getSubPubList(con2,
									// " order by a.subject.code ");
									// pub.setCsList(csList);
									resultList.add(pub);
								}
							}
						}
						// ----------------------进行高亮--------------
						/*
						 * if(form.getSearchValue()!=null&&!"".equals(form.
						 * getSearchValue())){ form.setKeyMap(this.highLight(0,
						 * form.getSearchValue())); }
						 */
						// ----------------------高亮结束--------------
						form.setCount(allCount);
						model.put("list", resultList);
						if ("true".equals(isJson)) {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("list", resultList);
							map.put("curpage", form.getCurpage() + 1);
							JSONObject json = JSONObject.fromObject(map);
							response.setContentType("text/json;charset=utf-8");
							PrintWriter writer = null;
							try {
								// 获取输出流
								writer = response.getWriter();
								writer.print(json.toString());
							} catch (IOException e) {
								// e.printStackTrace();
								throw e;
							} finally {
								if (writer != null) {
									writer.close();
								}
							}
						}
					} else {
						form.setCount(0);
					}
				}
			} else if (toTab == "2" || toTab.equals("2")) {// 开源、免费查询
				request.getSession().setAttribute("selectType", "2");// selectType
																		// 用来保存全局的变量，看是全部还是在已订阅中查询
																		// 2-全部
																		// 1-已订阅、
				String oafree = "";
				Integer coverType = 2;// 区分 免费开源和已订阅 查询
				Map<String, String> oafreeMap = new HashMap<String, String>();
				oafreeMap = Param.getParam("OAFree.uid.config");
				oafree = oafreeMap.get("uid");
				resultMap = this.licenseIndexService.advancedSearch(coverType, oafree, 0, 20, param, form.getSearchOrder() + "##" + form.getSortFlag());
				// resultMap =
				// this.publicationsIndexService.advancedSearch(oafree,form.getCurpage(),
				// form.getPageCount(),param,form.getSearchOrder());

				if (resultMap.get("count") != null && Long.valueOf(resultMap.get("count").toString()) > 0) {
					// 限制查询结果总数----------开始-----------
					Integer maxCount = Integer.parseInt(Param.getParam("search.config").get("maxCount"));
					Integer allCount = 0;
					if (resultMap.get("count") != null) {
						allCount = Integer.valueOf(resultMap.get("count").toString());
						model.put("queryCount", allCount);// 实际查询结果数量
						// 最多显示1000条
						allCount = maxCount > allCount ? allCount : maxCount;// 分页条显示的数量
					}
					// 限制查询结果总数----------结束-----------
					List<Map<String, Object>> list = (List<Map<String, Object>>) resultMap.get("result");
					List<PPublications> resultList = new ArrayList<PPublications>();
					for (Map<String, Object> idInfo : list) {
						// 根据ID查询产品信息
						// 由于加入了标签，这里不能用get查询
						Map<String, Object> condition = new HashMap<String, Object>();
						String oafreeid = "";
						if (idInfo.get("id").toString().startsWith("oafree_")) {
							oafreeid = idInfo.get("id").toString().replaceAll("oafree_", "");
						}
						condition.put("id", oafreeid);
						// condition.put("check","false");
						condition.put("status", null);
						condition.put("available", 3);
						// condition.put("oafree",2);
						List<PPublications> ppList = this.pPublicationsService.getPubList3(condition, " order by a.createOn ", (CUser) request.getSession().getAttribute("mainUser"), IpUtil.getIp(request));
						if (ppList != null && ppList.size() > 0) {
							PPublications pub = ppList.get(0);
							if (pub != null && pub.getRemark() != null && "[无简介]".equals(pub.getRemark())) {
								pub.setRemark("");
							}
							if (idInfo.containsKey("title") && idInfo.get("title") != null && !"".equals("title"))
								pub.setTitle(idInfo.get("title").toString());
							if (idInfo.containsKey("author") && idInfo.get("author") != null && !"".equals("author"))
								pub.setAuthor(idInfo.get("author").toString());
							if (idInfo.containsKey("isbn") && idInfo.get("isbn") != null && !"".equals("isbn"))
								pub.setCode(idInfo.get("isbn").toString());
							if (idInfo.containsKey("copyPublisher") && idInfo.get("copyPublisher") != null && !"".equals("copyPublisher"))
								pub.getPublisher().setName(idInfo.get("copyPublisher").toString());
							if (idInfo.containsKey("remark") && idInfo.get("remark") != null && !"".equals("remark"))
								pub.setRemark(idInfo.get("remark").toString());

							if (idInfo.containsKey("score") && idInfo.get("score") != null && !"".equals("score"))
								pub.setActivity(idInfo.get("score").toString());

							if (user1 != null) {
								Map<String, Object> con = new HashMap<String, Object>();
								con.put("publicationsid", idInfo.get("id"));
								con.put("status", 2);
								con.put("userTypeId", user1 == null ? "1" : user1.getUserType() == null ? "1" : user1.getUserType().getId());
								// con.put("userTypeId",
								// user1.getUserType().getId()==null?"":user1.getUserType().getId());
								List<PPrice> price = this.pPublicationsService.getPriceList(con);
								int isFreeUser = request.getSession().getAttribute("isFreeUser") == null ? 0 : (Integer) request.getSession().getAttribute("isFreeUser");
								if (isFreeUser != 1) {
									for (int j = 0; j < price.size(); j++) {
										PPrice pr = price.get(j);
										double endPrice = MathHelper.round(MathHelper.mul(pr.getPrice(), 1.13d));
										price.get(j).setPrice(endPrice);
									}
								}
								pub.setPriceList(price);
							}
							// 查询分类
							// Map<String,Object> con2 = new
							// HashMap<String,Object>();
							// con2.put("publicationsId", pub.getId());
							// List<PCsRelation> csList =
							// this.bSubjectService.getSubPubList(con2,
							// " order by a.subject.code ");
							// pub.setCsList(csList);
							resultList.add(pub);
						}
					}
					// ----------------------进行高亮--------------
					/*
					 * if(form.getSearchValue()!=null&&!"".equals(form.
					 * getSearchValue())){ form.setKeyMap(this.highLight(0,
					 * form.getSearchValue())); }
					 */
					// ----------------------高亮结束--------------
					// model.put("pubDateMap", pubDate);
					form.setCount(allCount);
					model.put("list", resultList);
					if ("true".equals(isJson)) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("list", resultList);
						map.put("curpage", form.getCurpage() + 1);
						JSONObject json = JSONObject.fromObject(map);
						response.setContentType("text/json;charset=utf-8");
						PrintWriter writer = null;
						try {
							// 获取输出流
							writer = response.getWriter();
							writer.print(json.toString());
						} catch (IOException e) {
							// e.printStackTrace();
							throw e;
						} finally {
							if (writer != null) {
								writer.close();
							}
						}
					}
				} else {
					form.setCount(0);
				}
			}
			List<FacetField> facetFields = (List<FacetField>) resultMap.get("facet");
			Map<String, Integer> pubDate = new HashMap<String, Integer>();
			for (FacetField fac : facetFields) {
				if (fac.getName().equals("pubDate")) {
					List<Count> counts = fac.getValues();
					for (Count count : counts) {
						if (count == null || count.getName() == null || count.getName().length() < 4) {
							continue;
						}
						int num = pubDate.get(count.getName().substring(0, 4)) == null ? 0 : Integer.valueOf(pubDate.get(count.getName().substring(0, 4)));
						if (count.getCount() > 0) {
							pubDate.put(count.getName().substring(0, 4).toString(), (num + (int) count.getCount()));
						}
					}
				}
				// 中文分类处理
				if (fac.getName().equals("taxonomy")) { // 过滤中文取相同分类 yangheqing
														// 2014-05-27
					List<Count> counts = fac.getValues();
					if (param.containsKey("taxonomy") && param.get("taxonomy") != null && !"".equals(param.get("taxonomy"))) {
						String[] taxArr = param.get("taxonomy").replace("\"", "").split(",");

						for (int i = counts.size() - 1; i >= 0; i--) {
							/*** 中文 ***/
							String subCode = taxArr[taxArr.length - 1].split(" ")[0];
							if (!counts.get(i).toString().toLowerCase().startsWith(subCode.toLowerCase())) {
								counts.remove(i);
							}
						}
					} else {
						/*
						 * for(int i=counts.size()-1;i>=0;i--){
						 *//*** 中文 ***/
						/*
						 * if(counts.get(i).toString().split(" ")[0].toString().
						 * length()>1){ counts.remove(i); } }
						 */
					}
					if (counts != null && counts.size() > 0) {
						ComparatorSubject comparator = new ComparatorSubject();
						Collections.sort(counts, comparator);
						model.put("taxonomyList", counts);
					}
				}
				// 英文分类处理
				if (fac.getName().equals("taxonomyEn")) {
					List<Count> counts = fac.getValues();
					if (param.containsKey("taxonomyEn") && param.get("taxonomyEn") != null && !"".equals(param.get("taxonomyEn"))) {
						String[] taxArr = param.get("taxonomyEn").replace("\"", "").split(",");

						for (int i = counts.size() - 1; i >= 0; i--) {
							String subCode = taxArr[taxArr.length - 1].split(" ")[0];
							if (!counts.get(i).toString().toLowerCase().startsWith(subCode.toLowerCase())) {
								counts.remove(i);
							}
						}
					} else {
						/*
						 * for(int i=counts.size()-1;i>=0;i--){
						 * if(counts.get(i). toString().split(" "
						 * )[0].toString().length()>1){ counts.remove(i); } }
						 */
					}
					if (counts != null && counts.size() > 0) {
						ComparatorSubject comparator = new ComparatorSubject();
						Collections.sort(counts, comparator);
						model.put("taxonomyEnList", counts);
					}
				}

				// 出版社处理
				if (fac.getName().equals("publisher")) {
					List<Count> counts = fac.getValues();
					if (counts != null && counts.size() > 0) {
						ComparatorSubject comparator = new ComparatorSubject();
						Collections.sort(counts, comparator);
						model.put("publisherList", counts);
					}
				}
				// 类型处理
				if (fac.getName().equals("type")) {
					List<Count> counts = fac.getValues();
					if (counts != null && counts.size() > 0) {
						int journalIndex = -1;
						int issueIndex = -1;
						for (int i = 0; i < counts.size(); i++) {
							if ("2".equals(counts.get(i).getName())) {
								journalIndex = i;
							} else if ("7".equals(counts.get(i).getName())) {
								issueIndex = i;
							}
						}
						if (issueIndex > -1) {
							long finalJournalCount = 0;
							if (journalIndex > -1) {// 如果存在期时就把期的数量加在期刊的数量上
								finalJournalCount = counts.get(journalIndex).getCount() + counts.get(issueIndex).getCount();
								counts.get(journalIndex).setCount(finalJournalCount);
								counts.remove(issueIndex);
							} else {// 如果期刊不存在，就把期的数量算上期刊上
								finalJournalCount = counts.get(issueIndex).getCount();
								counts.get(issueIndex).setName("2");
							}
						}
						model.put("typeList", counts);
					}
				}

				// 语种处理
				if (fac.getName().equals("language")) {

					List<Count> counts = fac.getValues();
					if (counts != null && counts.size() > 0) {
						ComparatorSubject comparator = new ComparatorSubject();
						Collections.sort(counts, comparator);
						model.put("languageList", counts);
					}
				}

				if (model.get("queryCount") == null) {
					model.put("queryCount", 0);
				}
				model.put("facetFields", facetFields);
				model.put("pubDateMap", SequenceUtil.MapDescToKey(pubDate));
			}
			if (form.getSearchValue() != null && !"".equals(form.getSearchValue()) && form.getCount() > 0) {
				CUser cuser = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
				if (cuser != null) {
					if (form.getCount() > 0) {
						CSearchHis obj = new CSearchHis();
						obj.setCreateOn(new Date());
						obj.setKeyword(form.getSearchValue());
						obj.setType(1);// 临时保存...下次登录的时候清空
						obj.setUser(cuser);
						obj.setKeyType(form.getSearchsType() == null ? 0 : form.getSearchsType());
						this.cUserService.addSearchHistory(obj);
					}
				}
			}
			if(null!=form.getSortFlag()&&!"".equals(form.getSortFlag())){
				if("asc".equals(form.getSortFlag().trim())){
					form.setSortFlag("asc");
				}else{
					form.setSortFlag("desc");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("prompt", Lang.getLanguage("Controller.Index.search.prompt.error", request.getSession().getAttribute("lang").toString()));// 搜索错误提示
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		if (toTab == "" || toTab.equals("")) {
			model.put("current", "search");
		} else if (toTab == "1" || toTab.equals("1")) {
			model.put("current", "searchLicense");
		} else if (toTab == "2" || toTab.equals("2")) {
			model.put("current", "searchOaFree");
		}

		// 用于回显搜索关键词
		if (null != form.getSearchValue() && !"".equals(form.getSearchValue().toString())) {
			form.setSearchValue(URLDecoder.decode(form.getSearchValue(), "UTF-8"));
		}
		model.put("form", form);
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 最新资源
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "lastPubsBookJson")
	public ModelAndView lastPubsBookJson(HttpServletRequest request, HttpServletResponse response, IndexForm form, String isCn, String pubDateOrder, String shelvesDateOrder) throws Exception {
		String forwardString = "";
		if ("true".equals(isCn)) {
			forwardString = "mobile/publications/cnBookList";
		} else {
			forwardString = "mobile/publications/enBookList";
		}
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			List<PPublications> resultList = new ArrayList<PPublications>();
			Map<String, Object> condition = new HashMap<String, Object>();
			String ins_Id = "";
			Integer num = 20;
			CUser cs = (CUser) request.getSession().getAttribute("mainUser");
			if (request.getParameter("num") != null && !"".equals(request.getParameter("num").toString())) {
				num = Integer.valueOf(request.getParameter("num").toString());
			}
			if (request.getSession().getAttribute("institution") != null) {
				if (request.getSession().getAttribute("mainUser") != null) {
					CUser user = (CUser) request.getSession().getAttribute("mainUser");
					if (user.getLevel() == 2) {// 不是图书馆管理员
						ins_Id = ((BInstitution) request.getSession().getAttribute("institution")).getId();
					}
				} else {
					ins_Id = ((BInstitution) request.getSession().getAttribute("institution")).getId();
				}
			}

			String order = "order by a.updateOn desc";
			// 按出版日期排序
			if (StringUtils.isNotEmpty(pubDateOrder) && "asc".equals(pubDateOrder)) {
				order = "order by a.pubDate asc";
			} else if (StringUtils.isNotEmpty(pubDateOrder) && "desc".equals(pubDateOrder)) {
				order = "order by a.pubDate desc";
			}
			// 按上架时间
			if (StringUtils.isNotEmpty(shelvesDateOrder) && "asc".equals(shelvesDateOrder)) {
				order += " , a.createOn asc";
			} else if (StringUtils.isNotEmpty(shelvesDateOrder) && "desc".equals(shelvesDateOrder)) {
				order += " , a.createOn desc";
			}

			// IP范围外的全部看到的是全局的
			if ("".equals(ins_Id)) {
				// condition.put("status", 2);//已上架
				// condition.put("typeArr", new Integer[]{1,2});
				// model.put("list",this.pPublicationsService.getPubSimplePageList(condition,
				// " order by a.createDate desc ",num,0));
				if (cs != null && cs.getLevel() == 2) {
					condition.put("isCn", isCn);
					condition.put("status", 1);// license有效
					ins_Id = cs.getInstitution().getId();
					// condition.put("searchType",new Integer[]{1,2});
					condition.put("institutionId", ins_Id);
					condition.put("isTrail", "0");
					condition.put("pubType", 1);
					condition.clear();
					Map<String, Object> resultMap = null;
					if ("false".equals(isCn)) {
						resultMap = publicationsIndexService.searchNewBooksEn(form.getCurpage(), num, pubDateOrder, shelvesDateOrder);
					} else {
						resultMap = publicationsIndexService.searchNewBooks(form.getCurpage(), num, pubDateOrder, shelvesDateOrder);
					}

					if (resultMap.get("count") != null && Long.valueOf(resultMap.get("count").toString()) > 0) {
						List<Map<String, Object>> list = (List<Map<String, Object>>) resultMap.get("result");
						for (Map<String, Object> idInfo : list) {
							// 根据ID查询产品信息
							// 由于加入了标签，这里不能用get查询
							PPublications pub = this.pPublicationsService.getPublications(idInfo.get("id").toString());
							if (pub != null && (pub.getAvailable() == null ? 0 : pub.getAvailable()) != 3) {
								if (pub.getRemark() != null && "[无简介]".equals(pub.getRemark())) {
									pub.setRemark("");
								}
								resultList.add(pub);
							}
						}
						model.put("list", resultList);
					}
				} else {
					Map<String, Object> resultMap = null;
					if ("false".equals(isCn)) {
						resultMap = publicationsIndexService.searchNewBooksEn(form.getCurpage(), num, pubDateOrder, shelvesDateOrder);
					} else {
						resultMap = publicationsIndexService.searchNewBooks(form.getCurpage(), num, pubDateOrder, shelvesDateOrder);
					}

					if (resultMap.get("count") != null && Long.valueOf(resultMap.get("count").toString()) > 0) {
						List<Map<String, Object>> list = (List<Map<String, Object>>) resultMap.get("result");
						for (Map<String, Object> idInfo : list) {
							// 根据ID查询产品信息
							// 由于加入了标签，这里不能用get查询
							PPublications pub = this.pPublicationsService.getPublications(idInfo.get("id").toString());
							if (pub != null && (pub.getAvailable() == null ? 0 : pub.getAvailable()) != 3) {
								if (pub.getRemark() != null && "[无简介]".equals(pub.getRemark())) {
									pub.setRemark("");
								}
								resultList.add(pub);
							}
						}
						model.put("list", resultList);
					}
				}
			} else {
				condition.put("isCn", isCn);
				condition.put("status", 1);// license有效
				// condition.put("searchType",new Integer[]{1,2});
				condition.put("institutionId", ins_Id);
				condition.put("isTrail", "0");
				condition.put("pubType", 1);

				condition.clear();
				Map<String, Object> resultMap = null;
				if ("false".equals(isCn)) {
					resultMap = publicationsIndexService.searchNewBooksEn(form.getCurpage(), num, pubDateOrder, shelvesDateOrder);
				} else {
					resultMap = publicationsIndexService.searchNewBooks(form.getCurpage(), num, pubDateOrder, shelvesDateOrder);
				}

				if (resultMap.get("count") != null && Long.valueOf(resultMap.get("count").toString()) > 0) {

					List<Map<String, Object>> list = (List<Map<String, Object>>) resultMap.get("result");
					for (Map<String, Object> idInfo : list) {
						// 根据ID查询产品信息
						// 由于加入了标签，这里不能用get查询
						PPublications pub = this.pPublicationsService.getPublications(idInfo.get("id").toString());
						if (pub != null && (pub.getAvailable() == null ? 0 : pub.getAvailable()) != 3) {
							if (pub.getRemark() != null && "[无简介]".equals(pub.getRemark())) {
								pub.setRemark("");
							}
							resultList.add(pub);
						}
					}
					model.put("list", resultList);
				}
			}
			// 把list转成json对象

			List<PPublications> res = new ArrayList<PPublications>();
			for (PPublications pub : resultList) {
				PPublications tmp = new PPublications();
				tmp.setId(pub.getId());
				tmp.setTitle(pub.getTitle());
				tmp.setFree(pub.getFree());
				tmp.setOa(pub.getOa());
				tmp.setPubDate(pub.getPubDate());
				tmp.setSubscribedIp(pub.getSubscribedIp());
				tmp.setFavorite(pub.getFavorite());
				tmp.setSubscribedUser(pub.getSubscribedUser());
				tmp.setInCollection(pub.getInCollection());
				tmp.setPriceList(pub.getPriceList());
				tmp.setBuyInDetail(pub.getBuyInDetail());
				tmp.setExLicense(pub.getExLicense());
				tmp.setRecommand(pub.getRecommand());
				tmp.setPubDate(pub.getPubDate());
				tmp.setPublisherName(pub.getPublisher().getName());
				tmp.setStartVolume(pub.getStartVolume());
				tmp.setEndVolume(pub.getEndVolume());
				tmp.setType(pub.getType());
				tmp.setAuthor(pub.getAuthor());
				tmp.setLatest(pub.getLatest());
				res.add(tmp);
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("list", res);
			map.put("curpage", form.getCurpage() + 1);
			JSONObject json = JSONObject.fromObject(map);
			response.setContentType("text/json;charset=utf-8");
			PrintWriter writer = null;
			try {
				// 获取输出流
				writer = response.getWriter();
				writer.print(json.toString());
			} catch (IOException e) {
				// e.printStackTrace();
				throw e;
			} finally {
				if (writer != null) {
					writer.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			forwardString = "mobile/error";
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		return new ModelAndView(forwardString, model);
	}

	/**
	 * AJAX查询资源统计数据--首页最新资源
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("lastPubs")
	public ModelAndView lastPubs(HttpServletRequest request, HttpServletResponse response, PPublicationsForm form) throws Exception {
		String forwardString = "mobile/index/index/pubList";
		Map<String, Object> model = new HashMap<String, Object>();
		form.setUrl(request.getRequestURL().toString());

		Map<String, Object> resultMap = publicationsIndexService.searchNewPubs(4, 10);

		if (resultMap.get("count") != null && Long.valueOf(resultMap.get("count").toString()) > 0) {
			List<Map<String, Object>> list = (List<Map<String, Object>>) resultMap.get("result");
			for (Map<String, Object> idInfo : list) {
				// 根据ID查询产品信息
				// 由于加入了标签，这里不能用get查询
				PPublications pub = this.pPublicationsService.getPublications(idInfo.get("id").toString());
				// if (pub != null && (pub.getAvailable() == null ? 0 :
				// pub.getAvailable()) != 3) {
				// // 往 Redis 中插入最新的7条记录
				// List<String> newBooksList = new ArrayList<String>();
				// newBooksList = bookDao.getList("new7");
				// if (7 > newBooksList.size()) {
				// bookDao.lpush("new7", pub.getId() + pub.getTitle());
				// }
				// // 设置 new7 的过期时间（24小时更新一次）
				// bookDao.expire("new7", 24 * 60 * 60);
				// }
			}
		}

		try {
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			Map<String, Object> condition = new HashMap<String, Object>();
			String ins_Id = "";
			Integer num = 5;
			if (request.getParameter("num") != null && !"".equals(request.getParameter("num").toString())) {
				num = Integer.valueOf(request.getParameter("num").toString());
			}
			// Ip范围内
			if (request.getSession().getAttribute("institution") != null) {
				if (request.getSession().getAttribute("mainUser") != null) {
					user = (CUser) request.getSession().getAttribute("mainUser");
					ins_Id = ((BInstitution) request.getSession().getAttribute("institution")).getId();
				} else {
					ins_Id = ((BInstitution) request.getSession().getAttribute("institution")).getId();
				}
			} else {// IP范围外，全局的。
				if (request.getSession().getAttribute("mainUser") != null) {
					user = (CUser) request.getSession().getAttribute("mainUser");
					if (user.getLevel() == 2) {// IP范围外，机构管理员登录显示最新订阅
						ins_Id = user.getInstitution().getId();
					}
				} else {
					condition.put("institutionId", ins_Id);
				}
			}
			List<PPublications> resultList = new ArrayList<PPublications>();
			// IP范围外的全部看到的是全局的
			if ("".equals(ins_Id)) {
				if (request.getParameter("news") != null && "true".equals(request.getParameter("news").toString())) {
					// resultMap=publicationsIndexService.searchNewPubs(form.getCurpage(),form.getPageCount());
					resultMap = publicationsIndexService.searchNewPubs(4, form.getPageCount());
					forwardString = "/index/lastPubList";
					Integer allCount = 0;
					if (resultMap.get("count") != null) {
						allCount = Integer.valueOf(resultMap.get("count").toString());
						allCount = 1000 > allCount ? allCount : 1000;
					}
					if (resultMap.get("count") != null && Long.valueOf(resultMap.get("count").toString()) > 0) {
						List<Map<String, Object>> list = (List<Map<String, Object>>) resultMap.get("result");
						for (Map<String, Object> idInfo : list) {
							// 根据ID查询产品信息
							// 由于加入了标签，这里不能用get查询
							PPublications pub = this.pPublicationsService.getPublications(idInfo.get("id").toString());
							if (pub != null && (pub.getAvailable() == null ? 0 : pub.getAvailable()) != 3) {
								if (pub.getRemark() != null && "[无简介]".equals(pub.getRemark())) {
									pub.setRemark("");
								}
								resultList.add(pub);
								/*
								 * if(resultList.size()>=4){ break; }
								 */
							}
						}
						form.setCount(allCount);
						model.put("list", resultList);
					} else {
						form.setCount(0);
					}

				} else {

					resultMap = publicationsIndexService.searchNewPubs(4, 10);

					if (resultMap.get("count") != null && Long.valueOf(resultMap.get("count").toString()) > 0) {
						List<Map<String, Object>> list = (List<Map<String, Object>>) resultMap.get("result");
						for (Map<String, Object> idInfo : list) {
							// 根据ID查询产品信息
							// 由于加入了标签，这里不能用get查询
							PPublications pub = this.pPublicationsService.getPublications(idInfo.get("id").toString());
							if (pub != null && (pub.getAvailable() == null ? 0 : pub.getAvailable()) != 3) {
								if (pub.getRemark() != null && "[无简介]".equals(pub.getRemark())) {
									pub.setRemark("");
								}
								resultList.add(pub);
								if (resultList.size() >= 4) {
									break;
								}
							}
						}
						model.put("list", resultList);
					}
					Map<String, Object> publisherMap = new HashMap<String, Object>();
					for (PPublications pPublications : resultList) {
						String ppName = pPublications.getPublisher().getName();
						condition.put("ppName", ppName);
						if (!publisherMap.containsKey(ppName)) {
							publisherMap.put(ppName, this.pPublicationsService.getPubCount(condition));
						}
					}
					condition.remove("ppName");
					model.put("publisherMap", publisherMap);

					Map<String, Object> pubDateMap = new TreeMap<String, Object>();
					for (PPublications pPublications : resultList) {
						String pDate = pPublications.getPubDate();
						pDate = pDate.substring(0, 4);
						condition.put("pDate", "%" + pDate + "%");
						if (!pubDateMap.containsKey(pDate)) {// Integer.parseInt(pDate)
							pubDateMap.put(pDate, this.pPublicationsService.getPubCount(condition));
						}
					}
					condition.remove("pDate");
					model.put("pubDateMap", pubDateMap);
				}
				/*
				 * if(resultMap.get("count")!=null&&Long.valueOf(resultMap.get(
				 * "count").toString())>0){ List<PPublications> resultList = new
				 * ArrayList<PPublications>(); List<Map<String,Object>> list =
				 * (List<Map<String,Object>>)resultMap.get("result");
				 * for(Map<String,Object> idInfo:list){ //根据ID查询产品信息
				 * //由于加入了标签，这里不能用get查询 PPublications
				 * pub=this.pPublicationsService
				 * .getPublications(idInfo.get("id").toString()); if(pub!=null
				 * && (pub.getAvailable()==null?0:pub.getAvailable())!=3){
				 * if(pub.getRemark()!=null && "[无简介]".equals(pub.getRemark())){
				 * pub.setRemark(""); } resultList.add(pub);
				 * if(resultList.size()>=4){ break; } } } model.put("list",
				 * resultList); }
				 */

			} else if (ins_Id != null && !ins_Id.equals("") && request.getParameter("news") != null && "true".equals(request.getParameter("news").toString())) {

				if (request.getParameter("news") != null && "true".equals(request.getParameter("news").toString())) {
					// resultMap=publicationsIndexService.searchNewPubs(form.getCurpage(),form.getPageCount());
					resultMap = publicationsIndexService.searchNewPubs(4, form.getPageCount());
					forwardString = "mobile/index/lastPubList";
					Integer allCount = 0;
					if (resultMap.get("count") != null) {
						allCount = Integer.valueOf(resultMap.get("count").toString());
						allCount = 1000 > allCount ? allCount : 1000;
					}
					if (resultMap.get("count") != null && Long.valueOf(resultMap.get("count").toString()) > 0) {
						List<Map<String, Object>> list = (List<Map<String, Object>>) resultMap.get("result");
						for (Map<String, Object> idInfo : list) {
							// 根据ID查询产品信息
							// 由于加入了标签，这里不能用get查询
							PPublications pub = this.pPublicationsService.getPublications(idInfo.get("id").toString());
							if (pub != null && (pub.getAvailable() == null ? 0 : pub.getAvailable()) != 3) {
								if (pub.getRemark() != null && "[无简介]".equals(pub.getRemark())) {
									pub.setRemark("");
								}
								resultList.add(pub);
								/*
								 * if(resultList.size()>=4){ break; }
								 */
							}
							// 左侧条件查询
							Map<String, Object> langMap = new HashMap<String, Object>();
							Map<String, Object> publisherMap = new HashMap<String, Object>();
							Map<String, Object> pubDateMap = new TreeMap<String, Object>();
							int bookCount = 0;
							int jouranlCount = 0;
							int chapterCount = 0;
							int articleCount = 0;
							for (PPublications pPublications : resultList) {
								if (pPublications.getType() == 1) {
									bookCount++;
								} else if (pPublications.getType() == 2) {
									jouranlCount++;
								} else if (pPublications.getType() == 3) {
									chapterCount++;
								} else if (pPublications.getType() == 4) {
									articleCount++;
								}
								if (pPublications.getLang() != null && !"".equals(pPublications.getLang().toString())) {
									if (langMap.size() > 0 && langMap.containsKey(pPublications.getLang())) {
										int count = Integer.parseInt(langMap.get(pPublications.getLang()).toString());
										count++;
										langMap.put(pPublications.getLang().toUpperCase(), count);
									} else {
										langMap.put(pPublications.getLang().toUpperCase(), 1);
									}
								}

								if (pPublications.getPublisher().getName() != null && !"".equals(pPublications.getPublisher().getName().toString())) {
									if (publisherMap.size() > 0 && publisherMap.containsKey(pPublications.getPublisher().getName())) {
										int count = Integer.parseInt(publisherMap.get(pPublications.getPublisher().getName()).toString());
										count++;
										publisherMap.put(pPublications.getPublisher().getName(), count);
									} else {
										publisherMap.put(pPublications.getPublisher().getName(), 1);
									}
								}

								if (pPublications.getPubDate() != null && !"".equals(pPublications.getPubDate().toString())) {
									if (pubDateMap.size() > 0 && pubDateMap.containsKey(pPublications.getPubDate().substring(0, 4))) {
										int count = Integer.parseInt(pubDateMap.get(pPublications.getPubDate().substring(0, 4)).toString());
										count++;
										pubDateMap.put(pPublications.getPubDate().substring(0, 4), count);
									} else {
										pubDateMap.put(pPublications.getPubDate().substring(0, 4), 1);
									}
								}

							}
							condition.put("type", 1);
							model.put("bookCount", Integer.toString(bookCount));
							condition.put("type", 2);
							model.put("jouranlCount", Integer.toString(jouranlCount));
							condition.put("type", 3);
							model.put("chapterCount", Integer.toString(chapterCount));
							condition.put("type", 4);
							model.put("articleCount", Integer.toString(articleCount));
							model.put("langMap", langMap);
							model.put("publisherMap", publisherMap);
							model.put("pubDateMap", pubDateMap);
						}
						form.setCount(allCount);
						model.put("list", resultList);
					} else {
						form.setCount(0);
					}

				} else {

					resultMap = publicationsIndexService.searchNewPubs(4, 10);

					if (resultMap.get("count") != null && Long.valueOf(resultMap.get("count").toString()) > 0) {
						List<Map<String, Object>> list = (List<Map<String, Object>>) resultMap.get("result");
						for (Map<String, Object> idInfo : list) {
							// 根据ID查询产品信息
							// 由于加入了标签，这里不能用get查询
							PPublications pub = this.pPublicationsService.getPublications(idInfo.get("id").toString());
							if (pub != null && (pub.getAvailable() == null ? 0 : pub.getAvailable()) != 3) {
								if (pub.getRemark() != null && "[无简介]".equals(pub.getRemark())) {
									pub.setRemark("");
								}
								resultList.add(pub);
								if (resultList.size() >= 4) {
									break;
								}
							}
						}
						model.put("list", resultList);
					}
					Map<String, Object> publisherMap = new HashMap<String, Object>();
					for (PPublications pPublications : resultList) {
						String ppName = pPublications.getPublisher().getName();
						condition.put("ppName", ppName);
						if (!publisherMap.containsKey(ppName)) {
							publisherMap.put(ppName, this.pPublicationsService.getPubCount(condition));
						}
					}
					condition.remove("ppName");
					model.put("publisherMap", publisherMap);

					Map<String, Object> pubDateMap = new TreeMap<String, Object>();
					for (PPublications pPublications : resultList) {
						String pDate = pPublications.getPubDate();
						pDate = pDate.substring(0, 4);
						condition.put("pDate", "%" + pDate + "%");
						if (!pubDateMap.containsKey(pDate)) {// Integer.parseInt(pDate)
							pubDateMap.put(pDate, this.pPublicationsService.getPubCount(condition));
						}
					}
					condition.remove("pDate");
					model.put("pubDateMap", pubDateMap);
				}
				/*
				 * if(resultMap.get("count")!=null&&Long.valueOf(resultMap.get(
				 * "count").toString())>0){ List<PPublications> resultList = new
				 * ArrayList<PPublications>(); List<Map<String,Object>> list =
				 * (List<Map<String,Object>>)resultMap.get("result");
				 * for(Map<String,Object> idInfo:list){ //根据ID查询产品信息
				 * //由于加入了标签，这里不能用get查询 PPublications
				 * pub=this.pPublicationsService
				 * .getPublications(idInfo.get("id").toString()); if(pub!=null
				 * && (pub.getAvailable()==null?0:pub.getAvailable())!=3){
				 * if(pub.getRemark()!=null && "[无简介]".equals(pub.getRemark())){
				 * pub.setRemark(""); } resultList.add(pub);
				 * if(resultList.size()>=4){ break; } } } model.put("list",
				 * resultList); }
				 */

			} else {
				condition.put("status", 1);// license有效
				condition.put("searchType", new Integer[] { 1, 2 });
				condition.put("institutionId", ins_Id);
				condition.put("isTrail", "0");
				List<LLicense> list = this.customService.getLicensePagingListForIndex(condition, " order by a.createdon desc ", 5, 0);
				if (list.size() >= 5) {
					model.put("list", list);
					forwardString = "mobile/index/index/licenseList2";
				} else {
					resultMap = publicationsIndexService.searchNewPubs(4, 10);

					if (resultMap.get("count") != null && Long.valueOf(resultMap.get("count").toString()) > 0) {
						List<Map<String, Object>> list1 = (List<Map<String, Object>>) resultMap.get("result");
						for (Map<String, Object> idInfo : list1) {
							// 根据ID查询产品信息
							// 由于加入了标签，这里不能用get查询
							PPublications pub = this.pPublicationsService.getPublications(idInfo.get("id").toString());
							if (pub != null && (pub.getAvailable() == null ? 0 : pub.getAvailable()) != 3) {
								if (pub.getRemark() != null && "[无简介]".equals(pub.getRemark())) {
									pub.setRemark("");
								}
								resultList.add(pub);
								if (resultList.size() >= 4) {
									break;
								}
							}
						}
						model.put("list", resultList);
					}

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			forwardString = "mobile/error";
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		model.put("form", form);
		return new ModelAndView(forwardString, model);
	}

	@RequestMapping(value = "/form/journaldetail/{journalId}")
	public ModelAndView jdetaillist(@PathVariable String journalId, HttpServletRequest request, HttpServletResponse response, PPublicationsForm form) throws Exception {
		String forwardString = "mobile/publications/journaldetail";
		Map<String, Object> model = new HashMap<String, Object>();

		try {

			Integer insStatus = 1;
			form.setUrl(request.getRequestURL().toString());
			CUser user = request.getSession() == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			PPublications journal = this.pPublicationsService.getPublications(journalId);
			if (journal.getRemark() != null && "[无简介]".equals(journal.getRemark())) {
				journal.setRemark("");
			}
			if (user != null) {
				Map<String, Object> usercon = new HashMap<String, Object>();
				if (user.getInstitution() != null) {// 登陆用户属于机构用户，判断类型 2-机构用户
													// 3-默认用户 5-专家用户
					// 判断所属机构是否被禁用
					usercon.put("insId", user.getInstitution().getId());
					BInstitution ins = this.configureService.getInstitution(user.getInstitution().getId());
					if (ins != null) {
						if (ins.getStatus() != null && ins.getStatus() == 2) {// 所属机构被禁用
							insStatus = ins.getStatus();

						}
					}
				}
			}
			if (journal.getType() == 2) {
				Map<String, Object> condition = new HashMap<String, Object>();
				condition.put("parentid", journal.getId());
				if (journal.getJournalType() != null && journal.getJournalType() == 2) {

					condition.put("type", 7);// 期
				} else {
					condition.put("type", 4);// 文章

				}
				condition.put("isLicense2", "true");// 上架的
				condition.put("orOnSale", "true");
				condition.put("check", "true");
				condition.put("ip", IpUtil.getIp(request));
				if (user != null) {
					condition.put("userId", user.getId());
				}
				Map<String, Object> pcr = new HashMap<String, Object>();
				// 填写的issn 相当于合刊
				pcr.put("issueCon", journalId);
				pcr.put("mark", "1");
				List<PContentRelation> issuemark1 = this.pPublicationsService.getPContentRelaionList(pcr, "");
				if (issuemark1 != null && issuemark1.size() > 0) {
					for (PContentRelation iss1 : issuemark1) {
						iss1.setMark("2");
					}
				}
				// 填写的issn,相当于分刊
				pcr = new HashMap<String, Object>();
				pcr.put("issueCon", journalId);
				pcr.put("mark", "2");
				List<PContentRelation> issuemark2 = this.pPublicationsService.getPContentRelaionList(pcr, "");
				if (issuemark2 != null && issuemark2.size() > 0) {
					for (PContentRelation iss2 : issuemark2) {
						iss2.setMark("21");
					}
				}
				// 分刊
				pcr = new HashMap<String, Object>();
				pcr.put("separateCon", journalId);
				pcr.put("mark", "1");
				List<PContentRelation> separateConmark1 = this.pPublicationsService.getPContentRelaionList(pcr, "");
				if (separateConmark1 != null && separateConmark1.size() > 0) {
					for (PContentRelation sc1 : separateConmark1) {
						sc1.setMark("12");
					}
				}
				// 合刊
				pcr = new HashMap<String, Object>();
				pcr.put("separateCon", journalId);
				pcr.put("mark", "2");
				List<PContentRelation> separateConmark2 = this.pPublicationsService.getPContentRelaionList(pcr, "");
				if (separateConmark2 != null && separateConmark2.size() > 0) {
					for (PContentRelation sc2 : separateConmark2) {
						sc2.setMark("1");
					}
				}
				// 变更列表
				pcr = new HashMap<String, Object>();
				pcr.put("separateCon", journalId);
				pcr.put("mark", "3");
				List<PContentRelation> modifyList1 = this.pPublicationsService.getPContentRelaionList(pcr, "");
				if (modifyList1 != null && modifyList1.size() > 0) {
					for (PContentRelation mo1 : modifyList1) {
						mo1.setMark("3");
					}
				}
				// 变更列表
				pcr = new HashMap<String, Object>();
				pcr.put("issueCon", journalId);
				pcr.put("mark", "3");
				List<PContentRelation> modifyList2 = this.pPublicationsService.getPContentRelaionList(pcr, "");
				if (modifyList2 != null && modifyList2.size() > 0) {
					for (PContentRelation mo1 : modifyList2) {
						mo1.setMark("31");
					}
				}
				// 分刊列表
				ArrayList<PContentRelation> pcrList = new ArrayList<PContentRelation>();// 有改变后的列表
				ArrayList<PContentRelation> pcrList1 = new ArrayList<PContentRelation>();// 改变前的列表
				// 合刊列表
				ArrayList<PContentRelation> pcrList2 = new ArrayList<PContentRelation>();// 有改变后的列表
				ArrayList<PContentRelation> pcrList3 = new ArrayList<PContentRelation>();// 改变前的列表
				// 变更列表
				ArrayList<PContentRelation> pcrList4 = new ArrayList<PContentRelation>();// 有改变后的列表
				ArrayList<PContentRelation> pcrList5 = new ArrayList<PContentRelation>();// 改变前的列表
				if (issuemark1 != null && issuemark1.size() > 0) {
					pcrList3.add(issuemark1.get(0));
				}
				if (issuemark2 != null && issuemark2.size() > 0) {
					pcrList3.add(issuemark2.get(0));
				}
				if (separateConmark1 != null && separateConmark1.size() > 0) {
					pcrList1.add(separateConmark1.get(0));
				}
				if (separateConmark2 != null && separateConmark2.size() > 0) {
					pcrList1.add(separateConmark2.get(0));
				}
				if (modifyList1 != null && modifyList1.size() > 0) {
					pcrList5.add(modifyList1.get(0));
				}
				if (modifyList2 != null && modifyList2.size() > 0) {
					pcrList5.add(modifyList2.get(0));
				}
				pcrList4.addAll(modifyList1);
				pcrList4.addAll(modifyList2);
				pcrList2.addAll(issuemark1);
				pcrList2.addAll(issuemark2);
				// pcrList2.addAll(modifyList2);
				pcrList.addAll(separateConmark1);
				pcrList.addAll(separateConmark2);
				// pcrList.addAll(modifyList1);
				// pcr.put("id", journalId);
				// List<PContentRelation> pcrList =
				// this.pPublicationsService.getPContentRelaionList(pcr,"");
				if (pcrList1 != null && pcrList1.size() > 0) {
					if (pcrList != null && pcrList.size() > 0) {
						model.put("pcrlist", pcrList);
					} else {
						model.put("pcrlist", null);
					}
					model.put("pcrlist1", pcrList1);
				} else {
					model.put("pcrlist1", null);
				}
				if (pcrList3 != null && pcrList3.size() > 0) {
					if (pcrList2 != null && pcrList2.size() > 0) {
						model.put("pcrlist2", pcrList2);
					} else {
						model.put("pcrlist2", null);
					}
					model.put("pcrlist3", pcrList3);
				} else {
					model.put("pcrlist3", null);
				}
				if (pcrList5 != null && pcrList5.size() > 0) {
					if (pcrList4 != null && pcrList4.size() > 0) {
						model.put("pcrlist4", pcrList4);
					} else {
						model.put("pcrlist4", null);
					}
					model.put("pcrlist5", pcrList5);
				} else {
					model.put("pcrlist5", null);
				}
				Map<String, Object> cond2 = new HashMap<String, Object>();
				cond2.put("isLicense2", "true");
				cond2.put("orOnSale", "true");
				cond2.put("check", "false");
				cond2.put("ip", IpUtil.getIp(request));
				if (user != null) {
					cond2.put("userId", user.getId());
				}
				if (form.getVolumeId() != null && !"".equals(form.getVolumeId())) {
					condition.put("volumeId", form.getVolumeId());
					cond2.put("id", form.getVolumeId());
				} else if (form.getIssueId() != null && !"".equals(form.getIssueId())) {
					condition.put("issueId", form.getIssueId());
					cond2.put("id", form.getIssueId());
				} else {
					cond2.put("id", journal.getId());
				}
				List<PPublications> lpub = this.pPublicationsService.getArticleList(cond2, "", user, IpUtil.getIp(request));
				if (lpub != null && lpub.size() == 1) {
					if (lpub.get(0).getRemark() != null && "[无简介]".equals(lpub.get(0).getRemark())) {
						lpub.get(0).setRemark("");
					}
					form.setObj(lpub.get(0));
				}

				// 文章列表（出版时间倒序）

				form.setCount(this.pPublicationsService.getPubCount(condition));
				List<PPublications> list = this.pPublicationsService.getArticlePagingList(condition, " order by a.pubDate desc ", form.getPageCount(), form.getCurpage(), user, IpUtil.getIp(request));
				if (list != null && list.size() > 0) {
					if (user != null) {
						Map<String, Object> pCondition = new HashMap<String, Object>();
						pCondition.put("status", 2);
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getRemark() != null && "[无简介]".equals(list.get(i).getRemark())) {
								list.get(i).setRemark("");
							}
							pCondition.put("publicationsid", list.get(i).getId());
							pCondition.put("status", 2);
							pCondition.put("userTypeId", user.getUserType().getId() == null ? "" : user.getUserType().getId());
							List<PPrice> plist = this.pPublicationsService.getPriceList(pCondition);
							list.get(i).setPriceList(plist);
						}
					}
				}

				if (user != null) {
					// 查询价格列表
					condition.clear();
					condition.put("publicationsid", form.getObj().getId());
					condition.put("status", 2);
					condition.put("userTypeId", user.getUserType().getId() == null ? "" : user.getUserType().getId());
					List<PPrice> pricelist = this.pPublicationsService.getPriceList(condition);
					List<PPrice> delList = new ArrayList<PPrice>();
					if (pricelist != null && !pricelist.isEmpty()) {
						if (form.getObj().getType() == 2) {// 期刊
							if (request.getSession().getAttribute("isFreeUser") == null || (Integer) request.getSession().getAttribute("isFreeUser") != 1) {
								for (PPrice p : pricelist) {
									Map<String, Object> detailCondition = new HashMap<String, Object>();
									detailCondition.put("priceId", p.getId());
									// 如果是机构管理员用户，则直接通过机构Id查询
									if (user.getLevel() == 2) {
										detailCondition.put("institutionId", user.getInstitution().getId());
									} else {
										// 其他用户根据用户自己的Id查询，是否可以购买
										detailCondition.put("userid", user.getId());
									}
									// 查询购物车中是否存在
									// detailCondition.put("orderNull",
									// "1");//没有生成订单的明细
									detailCondition.put("statusArry", new Integer[] { 1, 2, 4 });// 状态
																									// 1-未处理
																									// 2-已付款未开通
																									// 3-已付款已开通
																									// 4-处理中
																									// 10-未付款已开通
																									// 99-已取消
									List<OOrderDetail> odetailList = this.oOrderService.getDetailListForAddCrat(detailCondition);
									if (odetailList != null && odetailList.size() > 0) {
										delList.add(p);
									} else {
										// 在明细中没有找到
										// 查找Licesne，License中是否有有效的 1-有效 2-无效
										detailCondition.put("status", 1);
										List<LLicense> licenseList = this.oOrderService.getLicenseForAddCart(detailCondition);
										if (licenseList != null && licenseList.size() > 0) {
											delList.add(p);
										} else {
											p.setPrice(round(MathHelper.mul(p.getPrice(), 1.13d)));
										}
									}
								}
							} else {
								for (PPrice p : pricelist) {
									Map<String, Object> detailCondition = new HashMap<String, Object>();
									detailCondition.put("priceId", p.getId());
									// 如果是机构管理员用户，则直接通过机构Id查询
									if (user.getLevel() == 2) {
										detailCondition.put("institutionId", user.getInstitution().getId());
									} else {
										// 其他用户根据用户自己的Id查询，是否可以购买
										detailCondition.put("userid", user.getId());
									}
									// 查询购物车中是否存在
									// detailCondition.put("orderNull",
									// "1");//没有生成订单的明细
									detailCondition.put("statusArry", new Integer[] { 1, 2, 4 });// 状态
																									// 1-未处理
																									// 2-已付款未开通
																									// 3-已付款已开通
																									// 4-处理中
																									// 10-未付款已开通
																									// 99-已取消
									List<OOrderDetail> odetailList = this.oOrderService.getDetailListForAddCrat(detailCondition);
									if (odetailList != null && odetailList.size() > 0) {
										delList.add(p);
									} else {
										// 在明细中没有找到
										// 查找Licesne，License中是否有有效的 1-有效 2-无效
										detailCondition.put("status", 1);
										List<LLicense> licenseList = this.oOrderService.getLicenseForAddCart(detailCondition);
										if (licenseList != null && licenseList.size() > 0) {
											delList.add(p);
										} else {
											continue;
										}
									}
								}
							}
						} else {
							if (request.getSession().getAttribute("isFreeUser") == null || (Integer) request.getSession().getAttribute("isFreeUser") != 1) {
								for (PPrice p : pricelist) {
									p.setPrice(round(MathHelper.mul(p.getPrice(), 1.13d)));
								}
							}
						}
					}
					if (delList != null && delList.size() > 0) {
						pricelist.removeAll(delList);
					}
					form.getObj().setPriceList(pricelist);
					model.put("pricelist", pricelist);
				}

				// 查询分类
				Map<String, Object> conn = new HashMap<String, Object>();
				conn.put("publicationsId", form.getObj().getId());
				form.getObj().setCsList(this.bSubjectService.getSubPubList(conn, " order by a.subject.code "));

				CUser rUser = (CUser) request.getSession().getAttribute("recommendUser");
				form.setRecommendUser(rUser);
				condition.clear();
				condition.put("parentid", journal.getId());
				condition.put("isLicense2", "true");
				condition.put("orOnSale", "true");
				condition.put("check", "false");
				condition.put("ip", IpUtil.getIp(request));
				if (user != null) {
					condition.put("userId", user.getId());
				}
				condition.put("justYear", "true");
				List<PPublications> yearList = null;
				yearList = this.pPublicationsService.getSimpleList(condition, " group by a.year order by a.year desc ", 0);

				condition.clear();
				condition.put("issueList", "true");
				condition.put("parentid", journal.getId());
				condition.put("ip", IpUtil.getIp(request));
				condition.put("issueNotNull", 1);
				List<PPublications> issueList = null;
				issueList = this.pPublicationsService.getSimpleList(condition, " group by  a.year,a.volumeCode,a.month,a.issue order by a.year desc ", 0);

				Calendar c = Calendar.getInstance();
				condition.clear();
				condition.put("year", String.valueOf(c.get(Calendar.YEAR)));
				condition.put("month", (c.get(Calendar.MONTH) + 1) < 10 ? "0" + String.valueOf(c.get(Calendar.MONTH) + 1) : String.valueOf(c.get(Calendar.MONTH) + 1));
				if (journal.getJournalType() != null && journal.getJournalType() == 2) {
					condition.put("pubType", 7);// 期
				} else {

					condition.put("pubType", 4);// 文章
				}
				condition.put("pubParentId", journal.getId());
				condition.put("pubStatus", 2);
				condition.put("type", 2);
				List<LAccess> accList = this.logAOPService.getTopList(condition, 5);

				String currYear = null;
				Integer min = 0;
				if (yearList.size() > 0) {
					for (int i = 0; i < yearList.size(); i++) {
						if (Integer.parseInt(yearList.get(i).getYear()) > min)
							min = Integer.parseInt(yearList.get(i).getYear());
					}
					currYear = String.valueOf(min);
				} else {
					currYear = form.getObj().getYear();
				}
				condition.clear();
				condition.put("parentid", journal.getId());
				condition.put("isLicense2", "true");// 已上架的或已订阅的
				condition.put("orOnSale", "true");
				condition.put("check", "true");
				condition.put("ip", IpUtil.getIp(request));
				if (user != null) {
					condition.put("userId", user.getId());
				}
				condition.put("type", 6);// 卷
				Integer volCount = this.pPublicationsService.getPubCount(condition);// 期刊中的已上架的卷总数
				condition.put("type", 7);// 期
				Integer issCount = this.pPublicationsService.getPubCount(condition);// 期刊中的已上架的期总数
				condition.put("type", 4);// 文章
				Integer artCount = this.pPublicationsService.getPubCount(condition);// 期刊中的已上架的文章总数

				LLicense license = this.pPublicationsService.getVaildLicense(journal, user, IpUtil.getIp(request));
				if (journal.getOa() != 1 && journal.getFree() != 1 && license == null) {
					BInstitution institution = (BInstitution) request.getSession().getAttribute("institution");
					// 没有访问权限,计数
					this.oOrderService.addPDACounter(journal, institution, user);
				}

				int pubtype = 0;
				if (journal.getJournalType() != null && journal.getJournalType() == 2 && form.getObj().getType() == 7) {
					pubtype = form.getObj().getType();
				}
				model.put("pubtype", pubtype);
				model.put("volCount", volCount);
				model.put("issCount", issCount);
				model.put("artCount", artCount);
				model.put("currYear", currYear);
				model.put("currIssueId", issueList.size() > 0 ? issueList.get(0).getIssue().getId() : null);
				model.put("form", form);
				model.put("list", list);
				model.put("ylist", yearList);
				model.put("alist", accList);
				model.put("journal", journal);
				model.put("insStatus", insStatus);
				model.put("issueList", issueList);
			} else {
				// 输入的不是期刊ID
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		return new ModelAndView(forwardString, model);
	}

	@RequestMapping(value = "/form/article/{publicationsid}")
	public ModelAndView article(@PathVariable String publicationsid, HttpServletRequest request, HttpServletResponse response, HttpSession session, PPublicationsForm form) throws Exception {
		String forwardString = "mobile/publications/article";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			Boolean permission = false;
			Integer licenseStatus = null;

			String ins_Id = "";
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			List<PPrice> pricelist = null;
			Map<String, Object> condition = new HashMap<String, Object>();
			if (user != null) {
				condition.put("publicationsid", publicationsid);
				condition.put("status", 2);
				condition.put("userTypeId", user.getUserType().getId() == null ? "" : user.getUserType().getId());
				pricelist = this.pPublicationsService.getPriceList(condition);
			}

			PPublications pub = new PPublications();
			// 由于加入了标签，这里不能用get查询
			Map<String, Object> s = new HashMap<String, Object>();
			s.put("id", publicationsid);
			s.put("lang", request.getSession().getAttribute("lang"));
			if ("1".equals(form.getIsReady())) {
				s.put("status", null);
			} else {// 在IsReady不等于1时，要去判断这个产品的License是否存在，尽管产品已经下架，如果存在也可以浏览
				Map<String, Object> licenseCondition = new HashMap<String, Object>();
				licenseCondition.put("status", 1);// 有效
				licenseCondition.put("pubId", publicationsid);// 图书ID
				ins_Id = null;
				if (request.getSession().getAttribute("institution") != null) {
					ins_Id = ((BInstitution) request.getSession().getAttribute("institution")).getId();
				}
				licenseCondition.put("institutionId", ins_Id);
				licenseCondition.put("userId", user == null ? "" : user.getId());// 用户ID
				licenseCondition.put("level", 2);
				licenseCondition.put("isTrail", "0");
				List<LLicense> licenseList = this.pPublicationsService.getLicenseList(licenseCondition, "");
				if (licenseList != null && !licenseList.isEmpty()) {
					s.put("status", null);
					permission = true;
					licenseStatus = licenseList.get(0).getStatus();// 区分章节的购买，如果图书购买，章节免费看
				}
			}
			List<PPublications> ppList = this.pPublicationsService.getPubList(s, " order by a.createOn ", (CUser) request.getSession().getAttribute("mainUser"), IpUtil.getIp(request));
			if (ppList != null && ppList.size() > 0) {
				pub = ppList.get(0);
				// 这里是因为政治原因的问题，直接给它提示错误
				String ppp = pub.getId();
				if (ppp != null) {
					PPublications pp = this.pPublicationsService.getPublications(ppp);
					pub.setAvailable(pp.getAvailable());
					if (pp.getAvailable() != null && pp.getAvailable() == 3) {
						request.setAttribute("message", Lang.getLanguage("Controller.Publications.Prompt.Content.pp", request.getSession().getAttribute("lang").toString()));
						forwardString = "mobile/error";
						model.put("form", form);
						return new ModelAndView(forwardString, model);
					}
				}
				// 若通过搜索结果列表访问该页面，记录搜索日志 蒋凯 2013-11-15
				if (request.getParameter("fp") != null && !"".equals(request.getParameter("fp").toString())) {
					this.addLog(pub, user, ins_Id, request.getParameter("sv"), IpUtil.getIp(request), Integer.parseInt(request.getParameter("fp")));
				}

				if (pub.getType() == 4) {
					request.setAttribute("form", form);
					request.getRequestDispatcher("/mobile/pages/publications/form/journalarticle/" + publicationsid).forward(request, response);
					return new ModelAndView(forwardString, model);
				} else if (pub.getType() == 2) {
					request.setAttribute("form", form);
					request.getRequestDispatcher("/mobile/pages/publications/form/journaldetail/" + publicationsid).forward(request, response);
					return new ModelAndView(forwardString, model);
				} else if (pub.getType() == 6) {
					request.setAttribute("form", form);
					request.getRequestDispatcher("/mobile/pages/publications/form/journaldetail/" + pub.getPublications().getId() + "?volumeId=" + publicationsid).forward(request, response);
					return new ModelAndView(forwardString, model);
				} else if (pub.getType() == 7) {
					request.setAttribute("form", form);
					request.getRequestDispatcher("/mobile/pages/publications/form/journaldetail/" + pub.getPublications().getId() + "?issueId=" + publicationsid).forward(request, response);
					return new ModelAndView(forwardString, model);
				} else if (pub.getType() == 5) {
					request.setAttribute("form", form);
					request.getRequestDispatcher("/mobile/pages/publications/form/database").forward(request, response);
					return new ModelAndView(forwardString, model);

				}
				// isReady 用于 在产品下架后 仍然可以继续阅读时使用
				if ("1".equals(form.getIsReady()) || pub.getStatus() == 2 || permission) {
					if (pub.getPubDate() != null && pub.getPubDate().length() > 4) {
						pub.setPubDate(pub.getPubDate().substring(0, 4));// 页面值显示年份
					}
					String lang = pub.getLang();
					if (lang == null && pub.getType() == 3 && pub.getPublications() != null && pub.getPublications().getLang() != null) {
						lang = pub.getPublications().getPublications().getLang();
					}
					pub.setLang(lang == null ? "" : lang.toUpperCase());// 语言大写显示
					// 查询分类
					Map<String, Object> conn = new HashMap<String, Object>();
					conn.put("publicationsId", pub.getId());
					pub.setCsList(this.bSubjectService.getSubPubList(conn, " order by a.subject.code "));
					form.setObj(pub); // 产品信息
					if (pricelist != null && !pricelist.isEmpty()) {
						if (session.getAttribute("isFreeUser") == null || (Integer) session.getAttribute("isFreeUser") != 1) {
							for (PPrice p : pricelist) {
								p.setPrice(round(MathHelper.mul(p.getPrice(), 1.13)));
							}
						}
					} else {
						// 商品未标价
					}
					condition.clear();
					condition.put("statusnew", 2);
					condition.put("parentidNew", form.getObj().getId());
					List<PPublications> list = null;
					if (pub.getType() == 1) {

						// list =
						// this.pPublicationsService.getPubList(condition,
						// "order by
						// a.startPage",(CUser)request.getSession().getAttribute("mainUser"),IpUtil.getIp(request));
						list = this.pPublicationsService.getPubList(condition, " order by a.treecode,a.order,a.journalOrder ", (CUser) request.getSession().getAttribute("mainUser"), IpUtil.getIp(request));
						if (list != null && list.size() > 0) {// 显示章节层级关系
							form.setChaperShow(1);
							String lastCode = "";
							for (int i = 0; i < list.size(); i++) {
								String remark = list.get(i).getRemark() != null ? list.get(i).getRemark().replace("<![CDATA[", "").replace("]]>", "") : list.get(i).getRemark();
								list.get(i).setRemark(remark);
								if (pub.getHomepage() == null || pub.getHomepage() == 1) {// 验证是否允许章节购买跳转不同显示页面章节列表1-toc;2-正常列表

									if (list.get(i).getTreecode() != null && !"".equals(list.get(i).getTreecode())) {
										String tcode = list.get(i).getTreecode().substring(0, 3);
										if (tcode.equals(lastCode)) {
											if (i > 0) {
												// list.get(i-1).setFullText("chapt_author_100");//临时存放样式名字用chapt_p2
												list.get(i).setFullText("chapt_p2");// 临时存放样式名字用
											}
										} else {
											list.get(i).setFullText("chapt_p1");
										}
										Integer num = 30 * (list.get(i).getTreecode().length() / 3 - 1) + 10;
										list.get(i).setBrowsePrecent(num.toString());// 临时存放缩进度用
										lastCode = tcode;
									} else {
										list.get(i).setFullText("chapt_p1");
										Integer num = 30 * (3 / 3 - 1) + 10;
										list.get(i).setBrowsePrecent(num.toString());// 临时存放缩进度用
									}

								} else {
									form.setChaperShow(2);
								}
							}

						}
					}

					CUser ruser = (CUser) session.getAttribute("recommendUser");

					form.setRecommendUser(ruser);

					// 产品包列表
					condition.clear();
					condition.put("publicationsid", pub.getId());
					List<PCcRelation> pcclist = null;
					if (pub.getType() == 1) {
						pcclist = this.pPublicationsService.getPccList(condition, "", (CUser) request.getSession().getAttribute("mainUser"), IpUtil.getIp(request));
					}
					// 查询用户是否具有有效license
					LLicense license = this.pPublicationsService.getVaildLicense(pub, user, IpUtil.getIp(request));
					/*
					 * LLicense license=null;
					 * if(session.getAttribute("mainUser")!=null){ CUser
					 * mUser=(CUser)session.getAttribute("mainUser");
					 * condition.clear(); condition.put("pubId",publicationsid);
					 * condition.put("status", 1);//有效的license
					 * condition.put("userId", mUser.getId()); List<LLicense>
					 * licenselist
					 * =this.pPublicationsService.getLicenseList(condition,
					 * " order by a.accessUIdType "); if(licenselist !=null &&
					 * !licenselist.isEmpty()){ license=licenselist.get(0);
					 * }else{ condition.remove("userId");
					 * if(request.getRemoteAddr()!=null){
					 * condition.put("ip",IpUtil.getLongIp(
					 * request.getRemoteAddr().toString())); List<LLicenseIp>
					 * lliplist= this.customService.getLicenseIpList(condition,
					 * " order by b.accessUIdType "); if(lliplist!=null &&
					 * !lliplist.isEmpty()){
					 * license=lliplist.get(0).getLicense(); } } } }
					 */

					// ----------查询同分类中购买次数最多的前4个商品开始
					/*
					 * condition.clear(); condition.put("publicationsId",
					 * pub.getId()); List<PCsRelation>
					 * pcslist=this.bSubjectService.getSubPubList(condition,
					 * ""); condition.clear(); condition.put("pcslist",
					 * pcslist); condition.put("pstatus", 2); List<PCsRelation>
					 * toplist = null; if(pub.getType()==1){
					 * toplist=this.bSubjectService .getTops(condition,
					 * " order by p.buyTimes desc " ,5,0,(CUser
					 * )request.getSession().getAttribute("mainUser"),
					 * IpUtil.getIp(request)); }
					 */
					// -----------查询同分类中购买次数最多的前4个商品结束

					// //----------查询购买过此书的图书馆 开始
					List<BInstitution> subedInslist = null;
					if (session.getAttribute("mainUser") != null) {
						CUser mUser = (CUser) session.getAttribute("mainUser");
						if (mUser.getLevel() == 5 && pub.getType() == 1) {// 中图管理员可以查看
							condition.clear();
							condition.put("pubId", pub.getId());
							subedInslist = this.configureService.getInstitutionList(condition, "");
						}
					}
					// //-------------查询购买过次数的图书馆名称 结束

					condition.clear();
					Integer AllReadTimes = 0;
					Integer InsReadTimes = 0;
					condition.put("type", 2);
					condition.put("pubId", pub.getId());
					condition.put("access", 1);
					if (pub.getType() == 1) {
						AllReadTimes = this.logAOPService.getNormalCount(condition);
					}

					BInstitution institution = (BInstitution) session.getAttribute("institution");
					if (institution != null && pub.getType() == 1) {
						condition.put("institutionId", institution.getId());
						InsReadTimes = this.logAOPService.getNormalCount(condition);
					}

					if (pub.getOa() != 2 && pub.getFree() != 2 && license == null) {
						// 没有访问权限,计数
						this.oOrderService.addPDACounter(pub, institution, user);
					}
					model.put("id", pub.getId());
					model.put("InsReadTimes", InsReadTimes);
					model.put("AllReadTimes", AllReadTimes);
					model.put("subedInslist", subedInslist);
					// model.put("toplist",toplist);
					// model.put("license", license);
					model.put("pcclist", pcclist);
					model.put("pricelist", pricelist);
					model.put("list", list);
					model.put("form", form);
					model.put("isfav", form.getObj().getFavorite());
					model.put("licenseStatus", licenseStatus);// 图书是否已经购买

				} else {
					forwardString = "frame/result";
					request.setAttribute("prompt", Lang.getLanguage("Controller.Publications.Prompt.Title.NotFind", request.getSession().getAttribute("lang").toString()));
					request.setAttribute("message", Lang.getLanguage("Controller.Publications.Prompt.Content.OffSale", request.getSession().getAttribute("lang").toString()));
				}
			} else {
				forwardString = "frame/result";
				request.setAttribute("prompt", Lang.getLanguage("Controller.Publications.Prompt.Title.NotFind", request.getSession().getAttribute("lang").toString()));
				request.setAttribute("message", Lang.getLanguage("Controller.Publications.Prompt.Content.UnFind", request.getSession().getAttribute("lang").toString()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		model.put("form", form);
		request.setAttribute("id", form.getId());
		model.put("d", new Date());
		return new ModelAndView(forwardString, model);
	}

	@RequestMapping(value = "/form/journalarticle/{articleId}")
	public ModelAndView journalArticle(@PathVariable String articleId, HttpServletRequest request, HttpServletResponse response, PPublicationsForm form) throws Exception {
		String forwardString = "mobile/publications/journalarticle";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			// 由于加入了标签，这里不能用get查询
			Map<String, Object> s = new HashMap<String, Object>();
			s.put("id", articleId);
			s.put("isLicense2", "true");
			s.put("orOnSale", "true");
			s.put("check", "false");
			s.put("ip", IpUtil.getIp(request));
			if (user != null) {
				s.put("userId", user.getId());
			}
			// if("1".equals(form.getIsReady())){
			// s.put("status", null);
			// }

			List<PPublications> ppList = this.pPublicationsService.getArticleList(s, " order by a.createOn ", (CUser) request.getSession().getAttribute("mainUser"), IpUtil.getIp(request));
			// ppList=this.pPublicationsService.getPubList(s,
			// " order by a.createOn ",
			// (CUser)request.getSession().getAttribute("mainUser"),IpUtil.getIp(request));
			if (ppList != null && !ppList.isEmpty()) {
				PPublications pub = ppList.get(0);
				String prevArticleId = null;
				String nextArticleId = null;
				// 查询前一篇文章
				Map<String, Object> condition1 = new HashMap<String, Object>();
				condition1.put("issueId", pub.getIssue().getId());
				condition1.put("prevPage", pub.getStartPage());
				condition1.put("isLicense2", "true");
				condition1.put("orOnSale", "true");
				condition1.put("ip", IpUtil.getIp(request));
				if (user != null) {
					condition1.put("userId", user.getId());
				}
				condition1.put("check", "false");
				List<PPublications> prevList = this.pPublicationsService.getSimpleList(condition1, " order by a.startPage desc ", 1);
				if (prevList != null && prevList.size() == 1) {
					prevArticleId = prevList.get(0).getId();
				}
				// 查询后一篇文章
				condition1.clear();
				condition1.put("issueId", pub.getIssue().getId());
				condition1.put("nextPage", pub.getStartPage());
				condition1.put("isLicense2", "true");
				condition1.put("orOnSale", "true");
				condition1.put("check", "false");
				condition1.put("ip", IpUtil.getIp(request));
				if (user != null) {
					condition1.put("userId", user.getId());
				}
				List<PPublications> nextList = this.pPublicationsService.getSimpleList(condition1, " order by a.startPage ", 1);
				if (nextList != null && nextList.size() == 1) {
					nextArticleId = nextList.get(0).getId();
				}
				List<PPrice> pricelist = null;
				Map<String, Object> condition = new HashMap<String, Object>();
				condition.put("publicationsid", articleId);
				condition.put("status", 2);
				if (user != null) {
					condition.put("userTypeId", user.getUserType().getId() == null ? "" : user.getUserType().getId());
					pricelist = this.pPublicationsService.getPriceList(condition);
				}
				pub.setLang(pub.getLang() == null ? "ENG" : pub.getLang().toUpperCase());// 语言大写显示
				// 查询分类
				Map<String, Object> conn = new HashMap<String, Object>();
				conn.put("publicationsId", pub.getId());
				pub.setCsList(this.bSubjectService.getSubPubList(conn, " order by a.subject.code "));
				form.setObj(pub); // 产品信息
				if (pricelist != null && !pricelist.isEmpty()) {
					if (request.getSession().getAttribute("isFreeUser") == null || (Integer) request.getSession().getAttribute("isFreeUser") != 1) {
						for (PPrice p : pricelist) {
							p.setPrice(round(MathHelper.mul(p.getPrice(), 1.13d)));
						}
					}
				}

				CUser ruser = (CUser) request.getSession().getAttribute("recommendUser");
				form.setRecommendUser(ruser);

				String[] keywords = null;
				String[] reference = null;
				if (pub.getKeywords() != null && !"".equals(pub.getKeywords().trim())) {
					keywords = pub.getKeywords().split("\n");
				}
				if (pub.getReference() != null && !"".equals(pub.getReference().trim())) {
					reference = pub.getReference().split("\n");
				}

				// 查询用户的有效license
				LLicense license = this.pPublicationsService.getVaildLicense(pub, user, IpUtil.getIp(request));
				/*
				 * LLicense license=null;
				 * if(request.getSession().getAttribute("mainUser")!=null){
				 * CUser
				 * mUser=(CUser)request.getSession().getAttribute("mainUser");
				 * condition.clear(); condition.put("articleId",articleId);
				 * condition.put("status", 1);//有效的license
				 * condition.put("userId", mUser.getId()); List<LLicense>
				 * licenselist
				 * =this.pPublicationsService.getLicenseList(condition,
				 * " order by a.accessUIdType "); if(licenselist !=null &&
				 * !licenselist.isEmpty()){ license=licenselist.get(0); }else{
				 * condition.remove("userId");
				 * if(request.getRemoteAddr()!=null){
				 * condition.put("ip",IpUtil.getLongIp(
				 * request.getRemoteAddr().toString())); List<LLicenseIp>
				 * lliplist= this.customService.getLicenseIpList(condition,
				 * " order by b.accessUIdType "); if(lliplist!=null &&
				 * !lliplist.isEmpty()){ license=lliplist.get(0).getLicense(); }
				 * } } }
				 */

				// ----------查询同分类中购买次数最多的前5个商品开始
				/*
				 * condition.clear(); condition.put("publicationsId",
				 * pub.getId()); List<PCsRelation>
				 * pcslist=this.bSubjectService.getSubPubList(condition, "");
				 * List<PCsRelation> toplist=null; if(pcslist!=null &&
				 * pcslist.size()>0){ condition.clear();
				 * condition.put("pcslist", pcslist); condition.put("pstatus",
				 * 2); condition.put("unEqualPubId", pub.getId());
				 * condition.put("pTypeArr", new Integer[]{4});//只在期刊文章中查找
				 * toplist=this.bSubjectService.getTops(condition,
				 * " order by p.buyTimes desc "
				 * ,5,0,(CUser)request.getSession().getAttribute
				 * ("mainUser"),IpUtil.getIp(request)); }
				 */
				// -----------查询同分类中购买次数最多的前5个商品结束

				// 文章的相关内容：为了速度相关内容这里改为从solr中查询标题相关的同分类前5篇文章
				condition.clear();
				condition.put("publicationsId", pub.getId());
				String query = "title:" + ClientUtils.escapeQueryChars(pub.getTitle()) + " AND NOT id:\"" + pub.getId() + "\"";
				List<PCsRelation> pcslist = this.bSubjectService.getSubPubList(condition, "");
				if (pcslist != null && pcslist.size() > 0) {
					for (Integer i = 0; i < pcslist.size(); i++) {
						if (i == 0) {
							query += " AND (";
						}
						query += "taxonomy:\"" + pcslist.get(i).getSubject().getCode() + " " + pcslist.get(i).getSubject().getName() + "\"";
						// query+="taxonomyEn:\"" +
						// pcslist.get(i).getSubject().getCode()+ " " +
						// pcslist.get(i).getSubject().getNameEn() + "\"" ;
						if (i < pcslist.size() - 1) {
							query += " OR ";
						} else {
							query += ")";
						}
					}
				}
				Map<String, String> param = new HashMap<String, String>();
				param.put("type", "4");
				Map<String, Object> resultMap = publicationsIndexService.searchByQueryString(query, 0, 5, param, "");
				if (resultMap.get("count") != null && Long.valueOf(resultMap.get("count").toString()) > 0) {
					List<PPublications> resultList = new ArrayList<PPublications>();
					List<Map<String, Object>> list = (List<Map<String, Object>>) resultMap.get("result");
					for (Map<String, Object> idInfo : list) {
						// 根据ID查询产品信息
						PPublications pub1 = this.pPublicationsService.getPublications(idInfo.get("id").toString());
						// map.put("cherperid", idInfo.get("id").toString());
						// PPublications pub1 = (PPublications)
						// pPublicationsService.getPublications(map);
						if (pub1 != null) {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("pid", idInfo.get("id").toString());
							// map.put("", );
							CFavourites f = customService.getFavourite(map);
							if (null != f) {
								pub1.setFavorite(1);
							} else {
								pub1.setFavorite(0);
							}
							resultList.add(pub1);
						}
					}
					model.put("samelist", resultList);
				}

				if (pub.getOa() != 1 && pub.getFree() != 1 && license == null) {
					BInstitution institution = (BInstitution) request.getSession().getAttribute("institution");
					// 没有访问权限,计数
					this.oOrderService.addPDACounter(pub, institution, user);
				}

				model.put("keywords", keywords);
				model.put("reference", reference);
				model.put("prevId", prevArticleId);
				model.put("nextId", nextArticleId);
				// model.put("toplist",toplist);
				model.put("license", license);
				model.put("pricelist", pricelist);
				model.put("isfav", form.getObj().getFavorite());
				// TODO 以后再修改
				String remark = form.getObj().getRemark().replaceAll("\t", "").replaceAll("\n", "");
				if ("<abstract></abstract>".equals(remark)) {
					form.getObj().setRemark("");
				}
				model.put("form", form);

			} else {
				forwardString = "frame/result";
				request.setAttribute("prompt", Lang.getLanguage("Controller.Publications.Prompt.Title.NotFind", request.getSession().getAttribute("lang").toString()));
				request.setAttribute("message", Lang.getLanguage("Controller.Publications.Prompt.Content.UnFind", request.getSession().getAttribute("lang").toString()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		model.put("form", form);
		return new ModelAndView(forwardString, model);
	}

	@RequestMapping("form/cover")
	public void cover(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		String coverPath = "";
		try {
			String basePath = Param.getParam("pdf.directory.config").get("dir").replace("-", ":");
			String id = request.getParameter("id");
			String type = request.getParameter("t");
			Boolean useDefault = false;
			if (id != null && !"".equals(id)) {
				PPublications pub = this.pPublicationsService.getPublications(id);
				if (pub.getCover() != null && !"".equals(pub.getCover())) {
					coverPath = basePath + "" + pub.getCover();
				} else {
					coverPath = basePath + "/images/noimg.jpg";
					useDefault = true;
				}
				if (FileUtil.isExist(coverPath)) {
					if (!useDefault && type != null && !"".equals(type)) {// 若使用的不是默认图片并且传递了参数type
						String size = Param.getParam("publications.cover.type").get(type);
						if (size != null && !"".equals(size)) {// 参数type是一个有效的参数（在Param.properties文件中设置过）
							String ext = FileUtil.getFix(coverPath);// 获取后缀名
							String withOutExt = FileUtil.clearExt(coverPath);// 去掉后缀名
							String thumbPath = withOutExt + "_" + size + "." + ext;// 拼接缩略图路径
							if (FileUtil.isExist(thumbPath)) {// 缩略图是否存在
								coverPath = thumbPath;// 显示缩略图
							}
						}
					}
					InputStream in = new FileInputStream(coverPath);
					int i;
					response.setContentType("image/jpeg");
					OutputStream out = response.getOutputStream();
					while ((i = in.read()) != -1) {
						out.write(i);
					}
					out.close();
				}
			}
		} catch (Exception e) {
			// coverPath="D://upload/1.gif";//设置没有图片的封面写死的或者从数据库里区都行
		}
	}

	/**
	 * 保留两位小数 如:3.1415 >> 3.15
	 * 
	 * @param value
	 * @return
	 */
	private Double round(Double value) {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(2, BigDecimal.ROUND_CEILING);
		return bd.doubleValue();
	}

	public void addLog(PPublications pub, CUser user, String institutionId, String keyword, String ip, int fp) throws Exception {
		addLog(pub, user, institutionId, keyword, ip, 3, 1, fp, 0);
	}

	public void addLog(PPublications pub, CUser user, String institutionId, String keyword, String ip, int t, int a, int fp, int d) throws Exception {

		LAccess access = new LAccess();
		access.setActivity(keyword);
		access.setAccess(a);// 访问状态1-访问成功 2-访问拒绝
		access.setType(t);// 操作类型1-访问摘要 2-访问内容 3-检索
		if (d != 0) {
			access.setRefusedVisitType(d);
		} // 1-没有License,2-超出并发数
		access.setCreateOn(new Date());
		access.setIp(ip);
		access.setPlatform("CNPe");
		access.setYear(StringUtil.formatDate(access.getCreateOn(), "yyyy"));
		access.setMonth(StringUtil.formatDate(access.getCreateOn(), "MM"));
		if ("01".equals(access.getMonth()))
			access.setMonth1(1);
		if ("02".equals(access.getMonth()))
			access.setMonth2(1);
		if ("03".equals(access.getMonth()))
			access.setMonth3(1);
		if ("04".equals(access.getMonth()))
			access.setMonth4(1);
		if ("05".equals(access.getMonth()))
			access.setMonth5(1);
		if ("06".equals(access.getMonth()))
			access.setMonth6(1);
		if ("07".equals(access.getMonth()))
			access.setMonth7(1);
		if ("08".equals(access.getMonth()))
			access.setMonth8(1);
		if ("09".equals(access.getMonth()))
			access.setMonth9(1);
		if ("10".equals(access.getMonth()))
			access.setMonth10(1);
		if ("11".equals(access.getMonth()))
			access.setMonth11(1);
		if ("12".equals(access.getMonth()))
			access.setMonth12(1);
		PPublications publications = new PPublications();
		publications.setId(pub.getId());
		access.setPublications(publications);
		if (fp != 0) {
			access.setSearchType(fp);
		}

		if (institutionId == null && user == null) {
			// 机构IP范围外未登录
			this.logAOPService.addLog(access);
		} else {
			if (institutionId != null) {
				if (user != null) {
					access.setUserId(user.getId());
				}
				access.setInstitutionId(institutionId);
				this.logAOPService.addLog(access);
				if (user != null && user.getInstitution() != null && !institutionId.equals(user.getInstitution().getId())) {
					access.setUserId(user.getId());
					access.setInstitutionId(user.getInstitution().getId());
					this.logAOPService.addLog(access);
				}
			}
		}
	}

	/**
	 * AJAX查询图书列表
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/queryPubs")
	public void queryPubs(String journalId, HttpServletRequest request, HttpServletResponse response, PPublicationsForm form) throws Exception {
		try {
			Map<String, Object> model = new HashMap<String, Object>();
			Integer insStatus = 1;
			form.setUrl(request.getRequestURL().toString());
			CUser user = request.getSession() == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			PPublications journal = this.pPublicationsService.getPublications(journalId);
			if (journal.getRemark() != null && "[无简介]".equals(journal.getRemark())) {
				journal.setRemark("");
			}
			if (user != null) {
				Map<String, Object> usercon = new HashMap<String, Object>();
				if (user.getInstitution() != null) {// 登陆用户属于机构用户，判断类型 2-机构用户
													// 3-默认用户 5-专家用户
					// 判断所属机构是否被禁用
					usercon.put("insId", user.getInstitution().getId());
					BInstitution ins = this.configureService.getInstitution(user.getInstitution().getId());
					if (ins != null) {
						if (ins.getStatus() != null && ins.getStatus() == 2) {// 所属机构被禁用
							insStatus = ins.getStatus();

						}
					}
				}
			}
			if (journal.getType() == 2) {
				Map<String, Object> condition = new HashMap<String, Object>();
				condition.put("parentid", journal.getId());
				if (journal.getJournalType() != null && journal.getJournalType() == 2) {

					condition.put("type", 7);// 期
				} else {
					condition.put("type", 4);// 文章

				}
				condition.put("isLicense2", "true");// 上架的
				condition.put("orOnSale", "true");
				condition.put("check", "true");
				condition.put("ip", IpUtil.getIp(request));
				if (user != null) {
					condition.put("userId", user.getId());
				}
				Map<String, Object> pcr = new HashMap<String, Object>();
				// 填写的issn 相当于合刊
				pcr.put("issueCon", journalId);
				pcr.put("mark", "1");
				List<PContentRelation> issuemark1 = this.pPublicationsService.getPContentRelaionList(pcr, "");
				if (issuemark1 != null && issuemark1.size() > 0) {
					for (PContentRelation iss1 : issuemark1) {
						iss1.setMark("2");
					}
				}
				// 填写的issn,相当于分刊
				pcr = new HashMap<String, Object>();
				pcr.put("issueCon", journalId);
				pcr.put("mark", "2");
				List<PContentRelation> issuemark2 = this.pPublicationsService.getPContentRelaionList(pcr, "");
				if (issuemark2 != null && issuemark2.size() > 0) {
					for (PContentRelation iss2 : issuemark2) {
						iss2.setMark("21");
					}
				}
				// 分刊
				pcr = new HashMap<String, Object>();
				pcr.put("separateCon", journalId);
				pcr.put("mark", "1");
				List<PContentRelation> separateConmark1 = this.pPublicationsService.getPContentRelaionList(pcr, "");
				if (separateConmark1 != null && separateConmark1.size() > 0) {
					for (PContentRelation sc1 : separateConmark1) {
						sc1.setMark("12");
					}
				}
				// 合刊
				pcr = new HashMap<String, Object>();
				pcr.put("separateCon", journalId);
				pcr.put("mark", "2");
				List<PContentRelation> separateConmark2 = this.pPublicationsService.getPContentRelaionList(pcr, "");
				if (separateConmark2 != null && separateConmark2.size() > 0) {
					for (PContentRelation sc2 : separateConmark2) {
						sc2.setMark("1");
					}
				}
				// 变更列表
				pcr = new HashMap<String, Object>();
				pcr.put("separateCon", journalId);
				pcr.put("mark", "3");
				List<PContentRelation> modifyList1 = this.pPublicationsService.getPContentRelaionList(pcr, "");
				if (modifyList1 != null && modifyList1.size() > 0) {
					for (PContentRelation mo1 : modifyList1) {
						mo1.setMark("3");
					}
				}
				// 变更列表
				pcr = new HashMap<String, Object>();
				pcr.put("issueCon", journalId);
				pcr.put("mark", "3");
				List<PContentRelation> modifyList2 = this.pPublicationsService.getPContentRelaionList(pcr, "");
				if (modifyList2 != null && modifyList2.size() > 0) {
					for (PContentRelation mo1 : modifyList2) {
						mo1.setMark("31");
					}
				}
				// 分刊列表
				ArrayList<PContentRelation> pcrList = new ArrayList<PContentRelation>();// 有改变后的列表
				ArrayList<PContentRelation> pcrList1 = new ArrayList<PContentRelation>();// 改变前的列表
				// 合刊列表
				ArrayList<PContentRelation> pcrList2 = new ArrayList<PContentRelation>();// 有改变后的列表
				ArrayList<PContentRelation> pcrList3 = new ArrayList<PContentRelation>();// 改变前的列表
				// 变更列表
				ArrayList<PContentRelation> pcrList4 = new ArrayList<PContentRelation>();// 有改变后的列表
				ArrayList<PContentRelation> pcrList5 = new ArrayList<PContentRelation>();// 改变前的列表
				if (issuemark1 != null && issuemark1.size() > 0) {
					pcrList3.add(issuemark1.get(0));
				}
				if (issuemark2 != null && issuemark2.size() > 0) {
					pcrList3.add(issuemark2.get(0));
				}
				if (separateConmark1 != null && separateConmark1.size() > 0) {
					pcrList1.add(separateConmark1.get(0));
				}
				if (separateConmark2 != null && separateConmark2.size() > 0) {
					pcrList1.add(separateConmark2.get(0));
				}
				if (modifyList1 != null && modifyList1.size() > 0) {
					pcrList5.add(modifyList1.get(0));
				}
				if (modifyList2 != null && modifyList2.size() > 0) {
					pcrList5.add(modifyList2.get(0));
				}
				pcrList4.addAll(modifyList1);
				pcrList4.addAll(modifyList2);
				pcrList2.addAll(issuemark1);
				pcrList2.addAll(issuemark2);
				// pcrList2.addAll(modifyList2);
				pcrList.addAll(separateConmark1);
				pcrList.addAll(separateConmark2);
				// pcrList.addAll(modifyList1);
				// pcr.put("id", journalId);
				// List<PContentRelation> pcrList =
				// this.pPublicationsService.getPContentRelaionList(pcr,"");
				if (pcrList1 != null && pcrList1.size() > 0) {
					if (pcrList != null && pcrList.size() > 0) {
						model.put("pcrlist", pcrList);
					} else {
						model.put("pcrlist", null);
					}
					model.put("pcrlist1", pcrList1);
				} else {
					model.put("pcrlist1", null);
				}
				if (pcrList3 != null && pcrList3.size() > 0) {
					if (pcrList2 != null && pcrList2.size() > 0) {
						model.put("pcrlist2", pcrList2);
					} else {
						model.put("pcrlist2", null);
					}
					model.put("pcrlist3", pcrList3);
				} else {
					model.put("pcrlist3", null);
				}
				if (pcrList5 != null && pcrList5.size() > 0) {
					if (pcrList4 != null && pcrList4.size() > 0) {
						model.put("pcrlist4", pcrList4);
					} else {
						model.put("pcrlist4", null);
					}
					model.put("pcrlist5", pcrList5);
				} else {
					model.put("pcrlist5", null);
				}
				Map<String, Object> cond2 = new HashMap<String, Object>();
				cond2.put("isLicense2", "true");
				cond2.put("orOnSale", "true");
				cond2.put("check", "false");
				cond2.put("ip", IpUtil.getIp(request));
				if (user != null) {
					cond2.put("userId", user.getId());
				}
				if (form.getVolumeId() != null && !"".equals(form.getVolumeId())) {

					condition.put("volumeCode", form.getVolumeId());
					cond2.put("id", form.getVolumeId());
				}
				if (form.getIssueId() != null && !"".equals(form.getIssueId())) {

					condition.put("issueId", form.getIssueId());
					cond2.put("id", form.getIssueId());
				} else {
					cond2.put("id", journal.getId());
				}
				List<PPublications> lpub = this.pPublicationsService.getArticleList(cond2, "", user, IpUtil.getIp(request));
				if (lpub != null && lpub.size() == 1) {
					if (lpub.get(0).getRemark() != null && "[无简介]".equals(lpub.get(0).getRemark())) {
						lpub.get(0).setRemark("");
					}
					form.setObj(lpub.get(0));
				}

				// 文章列表（出版时间倒序）

				form.setCount(this.pPublicationsService.getPubCount(condition));

				condition.put("year", form.getPubYear());

				List<PPublications> list = this.pPublicationsService.getArticleList(condition, " order by a.pubDate desc ", user, IpUtil.getIp(request));
				if (list != null && list.size() > 0) {
					if (user != null) {
						Map<String, Object> pCondition = new HashMap<String, Object>();
						pCondition.put("status", 2);
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getRemark() != null && "[无简介]".equals(list.get(i).getRemark())) {
								list.get(i).setRemark("");
							}
							pCondition.put("publicationsid", list.get(i).getId());
							pCondition.put("status", 2);
							pCondition.put("userTypeId", user.getUserType().getId() == null ? "" : user.getUserType().getId());
							List<PPrice> plist = this.pPublicationsService.getPriceList(pCondition);
							list.get(i).setPriceList(plist);
						}
					}
				}

				if (user != null) {
					// 查询价格列表
					condition.clear();
					condition.put("publicationsid", form.getObj().getId());
					condition.put("status", 2);
					condition.put("userTypeId", user.getUserType().getId() == null ? "" : user.getUserType().getId());
					List<PPrice> pricelist = this.pPublicationsService.getPriceList(condition);
					List<PPrice> delList = new ArrayList<PPrice>();
					if (pricelist != null && !pricelist.isEmpty()) {
						if (form.getObj().getType() == 2) {// 期刊
							if (request.getSession().getAttribute("isFreeUser") == null || (Integer) request.getSession().getAttribute("isFreeUser") != 1) {
								for (PPrice p : pricelist) {
									Map<String, Object> detailCondition = new HashMap<String, Object>();
									detailCondition.put("priceId", p.getId());
									// 如果是机构管理员用户，则直接通过机构Id查询
									if (user.getLevel() == 2) {
										detailCondition.put("institutionId", user.getInstitution().getId());
									} else {
										// 其他用户根据用户自己的Id查询，是否可以购买
										detailCondition.put("userid", user.getId());
									}
									// 查询购物车中是否存在
									// detailCondition.put("orderNull",
									// "1");//没有生成订单的明细
									detailCondition.put("statusArry", new Integer[] { 1, 2, 4 });// 状态
																									// 1-未处理
																									// 2-已付款未开通
																									// 3-已付款已开通
																									// 4-处理中
																									// 10-未付款已开通
																									// 99-已取消
									List<OOrderDetail> odetailList = this.oOrderService.getDetailListForAddCrat(detailCondition);
									if (odetailList != null && odetailList.size() > 0) {
										delList.add(p);
									} else {
										// 在明细中没有找到
										// 查找Licesne，License中是否有有效的 1-有效 2-无效
										detailCondition.put("status", 1);
										List<LLicense> licenseList = this.oOrderService.getLicenseForAddCart(detailCondition);
										if (licenseList != null && licenseList.size() > 0) {
											delList.add(p);
										} else {
											p.setPrice(round(MathHelper.mul(p.getPrice(), 1.13d)));
										}
									}
								}
							} else {
								for (PPrice p : pricelist) {
									Map<String, Object> detailCondition = new HashMap<String, Object>();
									detailCondition.put("priceId", p.getId());
									// 如果是机构管理员用户，则直接通过机构Id查询
									if (user.getLevel() == 2) {
										detailCondition.put("institutionId", user.getInstitution().getId());
									} else {
										// 其他用户根据用户自己的Id查询，是否可以购买
										detailCondition.put("userid", user.getId());
									}
									// 查询购物车中是否存在
									// detailCondition.put("orderNull",
									// "1");//没有生成订单的明细
									detailCondition.put("statusArry", new Integer[] { 1, 2, 4 });// 状态
																									// 1-未处理
																									// 2-已付款未开通
																									// 3-已付款已开通
																									// 4-处理中
																									// 10-未付款已开通
																									// 99-已取消
									List<OOrderDetail> odetailList = this.oOrderService.getDetailListForAddCrat(detailCondition);
									if (odetailList != null && odetailList.size() > 0) {
										delList.add(p);
									} else {
										// 在明细中没有找到
										// 查找Licesne，License中是否有有效的 1-有效 2-无效
										detailCondition.put("status", 1);
										List<LLicense> licenseList = this.oOrderService.getLicenseForAddCart(detailCondition);
										if (licenseList != null && licenseList.size() > 0) {
											delList.add(p);
										} else {
											continue;
										}
									}
								}
							}
						} else {
							if (request.getSession().getAttribute("isFreeUser") == null || (Integer) request.getSession().getAttribute("isFreeUser") != 1) {
								for (PPrice p : pricelist) {
									p.setPrice(round(MathHelper.mul(p.getPrice(), 1.13d)));
								}
							}
						}
					}
					if (delList != null && delList.size() > 0) {
						pricelist.removeAll(delList);
					}
					form.getObj().setPriceList(pricelist);
					model.put("pricelist", pricelist);
				}

				// 查询分类
				Map<String, Object> conn = new HashMap<String, Object>();
				conn.put("publicationsId", form.getObj().getId());
				form.getObj().setCsList(this.bSubjectService.getSubPubList(conn, " order by a.subject.code "));

				CUser rUser = (CUser) request.getSession().getAttribute("recommendUser");
				form.setRecommendUser(rUser);
				condition.clear();

				condition.put("justYear", "true");
				condition.put("parentid", journal.getId());
				List<PPublications> yearList = this.pPublicationsService.getSimpleList(condition, " group by a.year order by a.year desc ", 0);
				condition.clear();

				if (user != null) {
					condition.put("userId", user.getId());
				}

				condition.put("parentid", journal.getId());
				condition.put("issueList11", "true");
				condition.put("pYear", form.getPubYear());
				condition.put("type", form.getType());

				List<PPublications> issueList = this.pPublicationsService.getSimpleList(condition, "  order by a.year desc ", 0);
				condition.clear();

				Calendar c = Calendar.getInstance();
				condition.clear();
				condition.put("year", String.valueOf(c.get(Calendar.YEAR)));
				condition.put("month", (c.get(Calendar.MONTH) + 1) < 10 ? "0" + String.valueOf(c.get(Calendar.MONTH) + 1) : String.valueOf(c.get(Calendar.MONTH) + 1));
				if (journal.getJournalType() != null && journal.getJournalType() == 2) {
					condition.put("pubType", 7);// 期
				} else {

					condition.put("pubType", 4);// 文章
				}
				condition.put("pubParentId", journal.getId());
				condition.put("pubStatus", 2);
				condition.put("type", 2);
				List<LAccess> accList = this.logAOPService.getTopList(condition, 5);

				String currYear = null;
				Integer min = 0;
				if (yearList.size() > 0) {
					for (int i = 0; i < yearList.size(); i++) {
						if (Integer.parseInt(yearList.get(i).getYear()) > min)
							min = Integer.parseInt(yearList.get(i).getYear());
					}
					currYear = String.valueOf(min);
				} else {
					currYear = form.getObj().getYear();
				}
				condition.clear();
				condition.put("parentid", journal.getId());
				condition.put("isLicense2", "true");// 已上架的或已订阅的
				condition.put("orOnSale", "true");
				condition.put("check", "true");
				condition.put("ip", IpUtil.getIp(request));
				if (user != null) {
					condition.put("userId", user.getId());
				}
				condition.put("type", 6);// 卷
				Integer volCount = this.pPublicationsService.getPubCount(condition);// 期刊中的已上架的卷总数
				condition.put("type", 7);// 期
				Integer issCount = this.pPublicationsService.getPubCount(condition);// 期刊中的已上架的期总数
				condition.put("type", 4);// 文章
				Integer artCount = this.pPublicationsService.getPubCount(condition);// 期刊中的已上架的文章总数

				LLicense license = this.pPublicationsService.getVaildLicense(journal, user, IpUtil.getIp(request));
				if (journal.getOa() != 1 && journal.getFree() != 1 && license == null) {
					BInstitution institution = (BInstitution) request.getSession().getAttribute("institution");
					// 没有访问权限,计数
					this.oOrderService.addPDACounter(journal, institution, user);
				}

				int pubtype = 0;
				if (journal.getJournalType() != null && journal.getJournalType() == 2 && form.getObj().getType() == 7) {
					pubtype = form.getObj().getType();
				}
				model.put("pubtype", pubtype);
				model.put("volCount", volCount);
				model.put("issCount", issCount);
				model.put("artCount", artCount);
				model.put("currYear", currYear);
				model.put("form", form);
				model.put("list", list);
				model.put("ylist", yearList);
				model.put("issueList", issueList);
				model.put("alist", accList);
				model.put("journal", journal);
				model.put("insStatus", insStatus);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("list", list);
				map.put("curPage", form.getCurpage() + 1);
				JSONObject json = JSONObject.fromObject(map);
				response.setContentType("text/json;charset=utf-8");
				PrintWriter writer = null;
				try {
					// 获取输出流
					writer = response.getWriter();
					writer.print(json.toString());
				} catch (IOException e) {
					// e.printStackTrace();
					throw e;
				} finally {
					if (writer != null) {
						writer.close();
					}
				}
			} else {
				// 输入的不是期刊ID
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
	}

	/**
	 * 获取资源
	 * 
	 * @param
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "form/getResource")
	public ModelAndView getResource(HttpServletRequest request, HttpServletResponse response, HttpSession session, PPublicationsForm form) throws Exception {
		String forwardString = "";
		Map<String, Object> model = new HashMap<String, Object>();
		boolean inLicense = false;
		String ins_Id = "";
		try {
			if (request.getParameter("pubid") != null && !"".equals(request.getParameter("pubid"))) {
				CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
				Map<String, Object> condition = form.getCondition();
				String id = request.getParameter("pubid");
				model.put("id", id);
				PPublications pub = this.pPublicationsService.getPublications(id);
				if (pub != null) {
					if (user != null) {
						if (request.getSession().getAttribute("institution") != null) {
							condition.put("status", 1);
							condition.put("pubId", pub.getId());
							if (request.getSession().getAttribute("institution") != null || user.getLevel() == 2) {
								String insId = user.getLevel() == 2 ? user.getInstitution().getId() : request.getSession().getAttribute("institution") == null ? user.getInstitution().getId() : ((BInstitution) request.getSession().getAttribute("institution")).getId();
								condition.put("institutionId", insId);
							} else {
								condition.put("userid", user.getId());
							}
							inLicense = this.customService.getLicenseResourceCount(condition) > 0;
							// if (!inLicense) {
							// condition.remove("pubId");
							// condition.put("pubCode", pub.getCode());
							// inLicense =
							// this.customService.getLicenseResourceCount(condition)
							// > 0;
							// }
							model.put("pub", pub);
							if (user.getLevel() == 4) {// 中图管理员
								// 阅读
								/*
								 * response.setContentType(
								 * "text/html; charset=gb2312");
								 * response.sendRedirect
								 * (request.getContextPath() +
								 * "/pages/view/form/view?id="+form.getId());
								 */
								// 直接跳转到阅读页
								response.setContentType("text/html; charset=gb2312");
								response.sendRedirect(request.getContextPath() + "/pages/view/form/view?id=" + form.getId());
							} else {
								// Boolean
								// isFavourite=pub.getFavorite()>0;//是否已经已经被用户收藏过
								// 是否可以访问
								/*
								 * System.out.println(pub.getSubscribedIp());
								 * System.out.println(pub.getSubscribedUser());
								 */
								// System.out.println(id);
								// System.out.println(pub.getOa());
								// System.out.println(pub.getFree());

								Boolean isLicense = inLicense || pub.getOa() == 2 || pub.getFree() == 2;
								// Boolean isLicense = pub.getOa()==2 ||
								// pub.getFree()==2;
								Boolean isRecommand = false;
								BInstitution recIns = null;
								if (!isLicense) {
									// 没有访问权限时才可能需要显示推荐按钮
									// 优先推荐给用户直接相关的机构
									recIns = user.getInstitution() != null ? user.getInstitution() : (BInstitution) request.getSession().getAttribute("institution");
									if (recIns != null) {
										isRecommand = true;
									}
								}
								if (isLicense) {
									if (pub.getType() == 2 || pub.getType() == 4) {
										model.put("pageCode", "p1");
										// 显示选择阅读或下载的对话框
										forwardString = "publications/pop1";
									} else if (pub.getType() == 1 || pub.getType() == 3) {
										/*
										 * //直接跳转到阅读页 response.setContentType(
										 * "text/html; charset=gb2312");
										 * response
										 * .sendRedirect(request.getContextPath
										 * () +
										 * "/pages/view/form/view?id="+form.
										 * getId());
										 */
										// 直接跳转到阅读页
										model.put("pageCode", "p5");
										forwardString = "publications/pop1";
									}
								} else {
									// 访问失败写据访信息
									addLog(pub, user, recIns == null ? null : recIns.getId(), null, IpUtil.getIp(request), 2, 2, 0, 1);
									if (user.getLevel() == 2) {
										model.put("pageCode", "p2");
										// 购买
										forwardString = "publications/pop1";
									} else if (user.getLevel() == 1 || user.getLevel() == 5) {
										if (pub.getType() == 1) {
											model.put("pageCode", "p3");
											// 推荐
											forwardString = "publications/pop1";
										} else if (pub.getType() == 4) {
											model.put("pageCode", "p4");
											// 推荐,添加购物车
											forwardString = "publications/pop1";
										} else if (pub.getType() == 2) {
											model.put("pageCode", "p3");
											// 推荐
											forwardString = "publications/pop1";
										}

									}
								}
							}
						} else {
							condition.put("status", 1);
							condition.put("pubId", pub.getId());
							if (request.getSession().getAttribute("institution") != null || user.getLevel() == 2) {
								String insId = user.getLevel() == 2 ? user.getInstitution().getId() : request.getSession().getAttribute("institution") == null ? user.getInstitution().getId() : ((BInstitution) request.getSession().getAttribute("institution")).getId();
								condition.put("institutionId", insId);
							} else {
								condition.put("userid", user.getId());
							}
							inLicense = this.customService.getLicenseResourceCount(condition) > 0;
							if (!inLicense) {
								// condition.remove("pubId");
								condition.put("pubCode", pub.getCode());
								inLicense = this.customService.getLicenseResourceCount(condition) > 0;
							}
							model.put("pub", pub);
							Boolean isLicense = inLicense || pub.getOa() == 2 || pub.getFree() == 2;
							// Boolean isLicense = pub.getOa()==2 ||
							// pub.getFree()==2;
							Boolean isRecommand = false;
							BInstitution recIns = null;
							if (isLicense) {
								if (pub.getType() == 2 || pub.getType() == 4) {
									model.put("pageCode", "p1");
									// 显示选择阅读或下载的对话框
									forwardString = "publications/pop1";
								} else if (pub.getType() == 1 || pub.getType() == 3) {
									/*
									 * //直接跳转到阅读页 response.setContentType(
									 * "text/html; charset=gb2312");
									 * response.sendRedirect
									 * (request.getContextPath() +
									 * "/pages/view/form/view?id="
									 * +form.getId());
									 */
									// 直接跳转到阅读页
									model.put("pageCode", "p5");
									forwardString = "publications/pop1";
								}
							} else {
								Map<String, Object> detailCondition = new HashMap<String, Object>();
								// 如果是机构管理员用户，则直接通过机构Id查询
								if (user.getLevel() == 2) {
									detailCondition.put("institutionId", user.getInstitution().getId());
								} else {
									// 其他用户根据用户自己的Id查询，是否可以购买
									detailCondition.put("userid", user.getId());
								}
								// 查询购物车中是否存在

								detailCondition.put("isPubid", pub.getId());
								detailCondition.put("statusArry", new Integer[] { 1, 2, 4 });// 状态
																								// 1-未处理
																								// 2-已付款未开通
																								// 3-已付款已开通
																								// 4-处理中
																								// 10-未付款已开通
																								// 99-已取消
								List<OOrderDetail> odetailList = this.oOrderService.getDetailListForAddCrat(detailCondition);
								if (odetailList != null && odetailList.size() > 0) {
									if (odetailList.get(0).getStatus() == 1) {
										// 资源处于已下单状态时，获取资源提示“您已下定此资源，请耐心等待授权开通
										model.put("pageCode", "s1");
										forwardString = "publications/pop1";
									} else if (odetailList.get(0).getStatus() == 4) {
										// 资源处于购物车中时，获取资源提示“该资源已存在您的购物车中，请到购物车进行结算
										model.put("pageCode", "s2");
										forwardString = "publications/pop1";
									}

								} else {

									model.put("pageCode", "p2");
									// 购买
									forwardString = "publications/pop1";
								}

							}
						}
					} else {
						// 没有用户,在IP范围内，已订阅的资源可以阅读，IP范围外提示登录
						// Ip范围内
						if (request.getSession().getAttribute("institution") != null) {
							ins_Id = ((BInstitution) request.getSession().getAttribute("institution")).getId();
							condition.put("status", 1);
							condition.put("pubId", pub.getId());
							condition.put("institutionId", ins_Id);
							inLicense = this.customService.getLicenseResourceCount(condition) > 0;
							// if (!inLicense) {
							// condition.remove("pubId");
							// condition.put("pubCode", pub.getCode());
							// inLicense =
							// this.customService.getLicenseResourceCount(condition)
							// > 0;
							// }
							model.put("pub", pub);
							Boolean isLicense = inLicense || pub.getOa() == 2 || pub.getFree() == 2;
							if (isLicense) {
								if (pub.getType() == 1 || pub.getType() == 3) {
									model.put("pageCode", "p5");
									// 阅读
									forwardString = "publications/pop1";
								} else if (pub.getType() == 4 || pub.getType() == 2) {
									model.put("pageCode", "p1");
									// 显示选择阅读或下载的对话框
									forwardString = "publications/pop1";
									// 访问失败写据访信息
									addLog(pub, user, ins_Id, null, IpUtil.getIp(request), 2, 2, 0, 1);

								}
							} else if (!isLicense) {
								model.put("pageCode", "p3");
								// 推荐
								forwardString = "publications/pop1";
							}
						} else {// IP范围外未登录，只需要判断是否为开源免费资源
							/*
							 * condition.put("status", 1);
							 * condition.put("pubId", pub.getId());
							 * condition.put("institutionId", ins_Id);
							 * condition.put("isTrial", "1"); inLicense =
							 * this.customService.getLicenseResourceCount(
							 * condition) > 0; if (!inLicense) {
							 * condition.remove("pubId");
							 * condition.put("pubCode", pub.getCode());
							 * inLicense =
							 * this.customService.getLicenseResourceCount(
							 * condition) > 0; }
							 */
							model.put("pub", pub);
							Boolean isLicense = inLicense || pub.getOa() == 2 || pub.getFree() == 2;
							if (isLicense) {
								model.put("pageCode", "p5");
								// 阅读
								forwardString = "publications/pop1";
							} else {
								// 访问失败写据访信息
								addLog(pub, user, null, null, IpUtil.getIp(request), 2, 2, 0, 1);

								forwardString = "publications/pop1";
								model.put("pageCode", "p6");
							}
						}
					}
				} else {
					// 未找到出版物
				}
			} else {
				// 没有出版物id
			}
		} catch (Exception e) {
			e.printStackTrace();
			form.setMsg((e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}

		return new ModelAndView(forwardString, model);
	}

	/**
	 * 二级页面-期刊页
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @param isJson
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/journalList")
	public ModelAndView journalList(HttpServletRequest request, HttpServletResponse response, IndexForm form, String isJson) throws Exception {
		String forwardString = "mobile/publications/journalList";
		CUser user1 = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
		String toTab = request.getSession().getAttribute("selectType").toString();
		String selectFlag = form.getSelectflag();
		if (selectFlag != null) {
			if (selectFlag == "oaFree" || selectFlag.equals("oaFree")) {
				toTab = "2";
			}
			if (selectFlag == "license" || selectFlag.equals("license")) {
				toTab = "1";
			}
			if (selectFlag == "all" || selectFlag.equals("all")) {
				toTab = "";
			}
		}
		//默认按上架时间倒序排序
		if(form.getSearchOrder()==null && form.getSortFlag()==null ){
			form.setSearchOrder("pubDate");
			form.setSortFlag("desc");
		}
		// BInstitution ins = (BInstitution)
		// request.getSession().getAttribute("institution");
		// if (null != user1 || null != ins) {
		// form.setLcense("1");
		// }
		String lcense = form.getLcense();
		String lang = (String) request.getSession().getAttribute("lang");
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			String isCn = form.getIsCn();
			form.setUrl(request.getRequestURL().toString());

			/*********** 查询条件区*开始 ***************/
			Map<String, String> param = new HashMap<String, String>();
			/** 特殊机构处理 -- START */
			String specialInstitutionFlag = request.getSession().getAttribute("specialInstitutionFlag") != null ? (String) request.getSession().getAttribute("specialInstitutionFlag") : null;
			if (null != specialInstitutionFlag && specialInstitutionFlag.length() > 0) {
				form = this.specialInstitution_handle(form, specialInstitutionFlag);
			}
			/** 特殊机构处理 -- END */
			param.put("language", (form.getLanguage() == null || "".equals(form.getLanguage())) ? null : "\"" + form.getLanguage() + "\"");
			param.put("title", (form.getTitle() == null || "".equals(form.getTitle())) ? null : form.getTitle());
			param.put("author", (form.getAuthor() == null || "".equals(form.getAuthor())) ? null : form.getAuthor());
			param.put("taxonomy", (form.getTaxonomy() == null || "".equals(form.getTaxonomy())) ? null : "\"" + form.getTaxonomy() + "\"");
			param.put("taxonomyEn", (form.getTaxonomyEn() == null || "".equals(form.getTaxonomyEn())) ? null : "\"" + form.getTaxonomyEn() + "\"");
			param.put("pubType", "2");
			// param.put("publisher", (form.getPublisher() == null ||
			// "".equals(form.getPublisher())) ? null : "\"" +
			// form.getPublisher() + "\"");
			param.put("publisher", (form.getPublisher() == null || "".equals(form.getPublisher())) ? null : form.getPublisher());
			// param.put("type",
			// (form.getPubType()==null||"".equals(form.getPubType()))?null:form.getPubType());
			param.put("pubDate", (form.getPubDate() == null || "".equals(form.getPubDate())) ? null : form.getPubDate() + "*");
			// 首字母
			// 最新资源排序检索
			param.put("sortFlag", (null == form.getSortFlag()) || "".equals(form.getSortFlag()) ? null : form.getSortFlag());
			/*********** 查询条件区*结束 ***************/
			/*** 中文 ***/
			if (param.containsKey("taxonomy") && param.get("taxonomy") != null) {
				String[] taxArr = param.get("taxonomy").replace("\"", "").split(",");
				model.put("taxArr", taxArr);
			}
			/*** 英文 ***/
			if (param.containsKey("taxonomyEn") && param.get("taxonomyEn") != null) {
				String[] taxArrEn = param.get("taxonomyEn").replace("\"", "").split(",");
				model.put("taxArrEn", taxArrEn);
			}

			Map<String, Object> resultMap = new HashMap<String, Object>();
			String userId = "";
			if (toTab == "" || toTab.equals("")) {// 按照已订阅查询
				request.getSession().setAttribute("selectType", "");// selectType
																	// 用来保存全局的变量，看是全部还是在已订阅中查询
																	// 2-全部
																	// 1-已订阅、

				resultMap = this.publicationsIndexService.advancedSearchMobile(0, 20, param, form.getSearchOrder() + "##" + form.getSortFlag(), isCn);
				// 限制查询结果总数----------开始-----------
				Integer maxCount = Integer.parseInt(Param.getParam("search.config").get("maxCount"));
				Integer allCount = 0;
				if (resultMap.get("count") != null) {
					allCount = Integer.valueOf(resultMap.get("count").toString());
					model.put("queryCount", allCount);// 实际查询结果数量

					// 最多显示1000条
					allCount = maxCount > allCount ? allCount : maxCount;// 分页条显示的数量
				}
				// 限制查询结果总数----------结束-----------
				if (allCount > 0) {
					List<Map<String, Object>> list = (List<Map<String, Object>>) resultMap.get("result");
					List<PPublications> resultList = new ArrayList<PPublications>();
					for (Map<String, Object> idInfo : list) {
						// 根据ID查询产品信息
						// 由于加入了标签，这里不能用get查询
						Map<String, Object> condition = new HashMap<String, Object>();
						condition.put("id", idInfo.get("id"));
						// condition.put("check","false");
						condition.put("status", null);
						condition.put("available", 3);
						List<PPublications> ppList = this.pPublicationsService.getPubList3(condition, " order by a.createOn ", (CUser) request.getSession().getAttribute("mainUser"), IpUtil.getIp(request));
						if (ppList != null && ppList.size() > 0) {
							PPublications pub = ppList.get(0);
							if (pub != null && pub.getRemark() != null && "[无简介]".equals(pub.getRemark())) {
								pub.setRemark("");
							}
							if (idInfo.containsKey("title") && idInfo.get("title") != null && !"".equals("title"))
								pub.setTitle(idInfo.get("title").toString());
							if (idInfo.containsKey("author") && idInfo.get("author") != null && !"".equals("author"))
								pub.setAuthor(idInfo.get("author").toString());
							if (idInfo.containsKey("isbn") && idInfo.get("isbn") != null && !"".equals("isbn"))
								pub.setCode(idInfo.get("isbn").toString());
							if (null != idInfo.get("copyPublisher") && idInfo.containsKey("copyPublisher") && !"".equals("copyPublisher")) {
								pub.getPublisher().setName(idInfo.get("copyPublisher").toString());
							}
							if (idInfo.containsKey("remark") && idInfo.get("remark") != null && !"".equals("remark"))
								pub.setRemark(idInfo.get("remark").toString());

							if (idInfo.containsKey("score") && idInfo.get("score") != null && !"".equals("score"))
								pub.setActivity(idInfo.get("score").toString());

							if (user1 != null) {
								Map<String, Object> con = new HashMap<String, Object>();
								con.put("publicationsid", idInfo.get("id"));
								con.put("status", 2);
								con.put("userTypeId", user1 == null ? "1" : user1.getUserType() == null ? "1" : user1.getUserType().getId());
								// con.put("userTypeId",
								// user1.getUserType().getId()==null?"":user1.getUserType().getId());
								List<PPrice> price = this.pPublicationsService.getPriceList(con);
								int isFreeUser = request.getSession().getAttribute("isFreeUser") == null ? 0 : (Integer) request.getSession().getAttribute("isFreeUser");
								if (isFreeUser != 1) {
									for (int j = 0; j < price.size(); j++) {
										PPrice pr = price.get(j);
										double endPrice = MathHelper.round(MathHelper.mul(pr.getPrice(), 1.13d));
										price.get(j).setPrice(endPrice);
									}
								}
								pub.setPriceList(price);
							}
							// 查询分类
							// Map<String,Object> con2 = new
							// HashMap<String,Object>();
							// con2.put("publicationsId", pub.getId());
							// List<PCsRelation> csList =
							// this.bSubjectService.getSubPubList(con2,
							// " order by a.subject.code ");
							// pub.setCsList(csList);
							resultList.add(pub);
						}
					}
					// ----------------------进行高亮--------------
					/*
					 * if(form.getSearchValue()!=null&&!"".equals(form.
					 * getSearchValue())){ form.setKeyMap(this.highLight(0,
					 * form.getSearchValue())); }
					 */
					// ----------------------高亮结束--------------
					// model.put("pubDateMap", pubDate);
					form.setCount(allCount);
					model.put("list", resultList);
				} else {
					form.setCount(0);
				}
			} else if (toTab == "1" || toTab.equals("1")) {
				CUser user = (CUser) request.getSession().getAttribute("mainUser");
				request.getSession().setAttribute("selectType", 1);// selectType
																	// 用来保存全局的变量，看是全部还是在已订阅中查询
																	// 2-全部
																	// 1-已订阅
				StringBuffer userIds = new StringBuffer();
				// 访问IP
				long ip = IpUtil.getLongIp(IpUtil.getIp(request));
				// 查询机构信息
				Map<String, Object> mapip = new HashMap<String, Object>();
				mapip.put("ip", ip);
				List<BIpRange> lip = this.configureService.getIpRangeList(mapip, "");
				if (lip != null && lip.size() > 0) {
					// 根据机构ID,查询用户
					for (BIpRange bIpRange : lip) {
						Map<String, Object> uc = new HashMap<String, Object>();
						// uc.put("institutionId",bIpRange.getInstitution().getId()
						// );
						if (user != null && user.getLevel() == 2) {
							uc.put("institutionId", user.getInstitution().getId());
						} else {
							uc.put("institutionId", bIpRange.getInstitution().getId());
						}
						uc.put("insStatus", 1);// 1-机构未被禁用状态
						uc.put("level", 2);
						List<CUser> lu = this.cUserService.getUserList(uc, "");
						for (CUser cUser : lu) {
							userIds.append(cUser.getId()).append(",");
						}
					}
				}
				// 查询用户ID
				if (request.getSession().getAttribute("mainUser") != null) {
					user = (CUser) request.getSession().getAttribute("mainUser");
					userIds.append(user.getId()).append(",");
				}
				// userIds.append(Param.getParam("OAFree.uid.config").get("uid")).append(",");
				if ("".equals(userIds.toString())) {
					throw new CcsException("Controller.Index.searchLicense.noLogin");// 未登录用户，无法按照“已订阅”查询

				} else {
					userId = userIds.substring(0, userIds.toString().lastIndexOf(","));
					// 在solr中查询 [搜索类型=====0-全文;1-标题;2-作者]
					Integer coverType = 1;// 区分免费开源和已订阅
					resultMap = this.licenseIndexService.advancedSearch(coverType, userId, 0, 20, param, form.getSearchOrder() + "##" + form.getSortFlag());
					if (resultMap.get("count") != null && Long.valueOf(resultMap.get("count").toString()) > 0) {
						// 限制查询结果总数----------开始-----------
						Integer maxCount = Integer.parseInt(Param.getParam("search.config").get("maxCount"));
						Integer allCount = 0;
						if (resultMap.get("count") != null) {
							allCount = Integer.valueOf(resultMap.get("count").toString());
							model.put("queryCount", allCount);// 实际查询结果数量
							// 最多显示1000条
							allCount = maxCount > allCount ? allCount : maxCount;// 分页条显示的数量
						}
						List<Map<String, Object>> list = (List<Map<String, Object>>) resultMap.get("result");
						List<PPublications> resultList = new ArrayList<PPublications>();
						// 这里根据LicenseId查询产品
						for (Map<String, Object> idInfo : list) {
							String lid = idInfo.get("id").toString();
							String pid = "";
							if (lid.contains("_")) {
								pid = lid.substring(lid.lastIndexOf("_") + 1, lid.length());
							} else {
								LLicense lli = this.pPublicationsService.getLicense(lid);
								if (lli != null) {
									pid = lli.getPublications().getId();
								}
							}
							if (pid != null && !"".equals(pid)) {
								// 根据ID查询产品信息
								// 由于加入了标签，这里不能用get查询
								Map<String, Object> condition = new HashMap<String, Object>();
								condition.put("id", pid);
								condition.put("status", null);
								condition.put("available", 3);
								List<PPublications> ppList = this.pPublicationsService.getPubList3(condition, " order by a.createOn ", (CUser) request.getSession().getAttribute("mainUser"), IpUtil.getIp(request));
								if (ppList != null && ppList.size() > 0) {
									PPublications pub = ppList.get(0);
									if (pub != null && pub.getRemark() != null && "[无简介]".equals(pub.getRemark())) {
										pub.setRemark("");
									}
									if (idInfo.containsKey("title") && idInfo.get("title") != null && !"".equals("title"))
										pub.setTitle(idInfo.get("title").toString());
									if (idInfo.containsKey("author") && idInfo.get("author") != null && !"".equals("author"))
										pub.setAuthor(idInfo.get("author").toString());
									if (idInfo.containsKey("isbn") && idInfo.get("isbn") != null && !"".equals("isbn"))
										pub.setCode(idInfo.get("isbn").toString());
									if (idInfo.containsKey("copyPublisher") && idInfo.get("copyPublisher") != null && !"".equals("copyPublisher"))
										pub.getPublisher().setName(idInfo.get("copyPublisher").toString());
									if (idInfo.containsKey("remark") && idInfo.get("remark") != null && !"".equals("remark"))
										pub.setRemark(idInfo.get("remark").toString());

									if (idInfo.containsKey("score") && idInfo.get("score") != null && !"".equals("score"))
										pub.setActivity(idInfo.get("score").toString());
									if (user1 != null) {
										Map<String, Object> con = new HashMap<String, Object>();
										con.put("publicationsid", idInfo.get("id"));
										con.put("status", 2);
										con.put("userTypeId", user1.getUserType().getId() == null ? "" : user1.getUserType().getId());
										List<PPrice> price = this.pPublicationsService.getPriceList(con);
										int isFreeUser = request.getSession().getAttribute("isFreeUser") == null ? 0 : (Integer) request.getSession().getAttribute("isFreeUser");
										if (isFreeUser != 1) {
											for (int j = 0; j < price.size(); j++) {
												PPrice pr = price.get(j);
												double endPrice = MathHelper.round(MathHelper.mul(pr.getPrice(), 1.13d));
												price.get(j).setPrice(endPrice);
											}
										}
										pub.setPriceList(price);
									}
									// 查询分类
									// Map<String,Object> con2 = new
									// HashMap<String,Object>();
									// con2.put("publicationsId", pub.getId());
									// List<PCsRelation> csList =
									// this.bSubjectService.getSubPubList(con2,
									// " order by a.subject.code ");
									// pub.setCsList(csList);
									resultList.add(pub);
								}
							}
						}
						// ----------------------进行高亮--------------
						/*
						 * if(form.getSearchValue()!=null&&!"".equals(form.
						 * getSearchValue())){ form.setKeyMap(this.highLight(0,
						 * form.getSearchValue())); }
						 */
						// ----------------------高亮结束--------------
						form.setCount(allCount);
						model.put("list", resultList);
					} else {
						form.setCount(0);
					}
				}
			} else if (toTab == "2" || toTab.equals("2")) {// 开源、免费查询
				request.getSession().setAttribute("selectType", "2");// selectType
																		// 用来保存全局的变量，看是全部还是在已订阅中查询
																		// 2-全部
																		// 1-已订阅、
				String oafree = "";
				Integer coverType = 2;// 区分 免费开源和已订阅 查询
				Map<String, String> oafreeMap = new HashMap<String, String>();
				oafreeMap = Param.getParam("OAFree.uid.config");
				oafree = oafreeMap.get("uid");
				resultMap = this.licenseIndexService.advancedSearch(coverType, oafree, 0, 20, param, form.getSearchOrder() + "##" + form.getSortFlag());
				// resultMap =
				// this.publicationsIndexService.advancedSearch(oafree,form.getCurpage(),
				// form.getPageCount(),param,form.getSearchOrder());

				if (resultMap.get("count") != null && Long.valueOf(resultMap.get("count").toString()) > 0) {
					// 限制查询结果总数----------开始-----------
					Integer maxCount = Integer.parseInt(Param.getParam("search.config").get("maxCount"));
					Integer allCount = 0;
					if (resultMap.get("count") != null) {
						allCount = Integer.valueOf(resultMap.get("count").toString());
						model.put("queryCount", allCount);// 实际查询结果数量
						// 最多显示1000条
						allCount = maxCount > allCount ? allCount : maxCount;// 分页条显示的数量
					}
					// 限制查询结果总数----------结束-----------
					List<Map<String, Object>> list = (List<Map<String, Object>>) resultMap.get("result");
					List<PPublications> resultList = new ArrayList<PPublications>();
					for (Map<String, Object> idInfo : list) {
						// 根据ID查询产品信息
						// 由于加入了标签，这里不能用get查询
						Map<String, Object> condition = new HashMap<String, Object>();
						String oafreeid = "";
						if (idInfo.get("id").toString().startsWith("oafree_")) {
							oafreeid = idInfo.get("id").toString().replaceAll("oafree_", "");
						}
						condition.put("id", oafreeid);
						// condition.put("check","false");
						condition.put("status", null);
						condition.put("available", 3);
						// condition.put("oafree",2);
						List<PPublications> ppList = this.pPublicationsService.getPubList3(condition, " order by a.createOn ", (CUser) request.getSession().getAttribute("mainUser"), IpUtil.getIp(request));
						if (ppList != null && ppList.size() > 0) {
							PPublications pub = ppList.get(0);
							if (pub != null && pub.getRemark() != null && "[无简介]".equals(pub.getRemark())) {
								pub.setRemark("");
							}
							if (idInfo.containsKey("title") && idInfo.get("title") != null && !"".equals("title"))
								pub.setTitle(idInfo.get("title").toString());
							if (idInfo.containsKey("author") && idInfo.get("author") != null && !"".equals("author"))
								pub.setAuthor(idInfo.get("author").toString());
							if (idInfo.containsKey("isbn") && idInfo.get("isbn") != null && !"".equals("isbn"))
								pub.setCode(idInfo.get("isbn").toString());
							if (idInfo.containsKey("copyPublisher") && idInfo.get("copyPublisher") != null && !"".equals("copyPublisher"))
								pub.getPublisher().setName(idInfo.get("copyPublisher").toString());
							if (idInfo.containsKey("remark") && idInfo.get("remark") != null && !"".equals("remark"))
								pub.setRemark(idInfo.get("remark").toString());

							if (idInfo.containsKey("score") && idInfo.get("score") != null && !"".equals("score"))
								pub.setActivity(idInfo.get("score").toString());

							if (user1 != null) {
								Map<String, Object> con = new HashMap<String, Object>();
								con.put("publicationsid", idInfo.get("id"));
								con.put("status", 2);
								con.put("userTypeId", user1 == null ? "1" : user1.getUserType() == null ? "1" : user1.getUserType().getId());
								// con.put("userTypeId",
								// user1.getUserType().getId()==null?"":user1.getUserType().getId());
								List<PPrice> price = this.pPublicationsService.getPriceList(con);
								int isFreeUser = request.getSession().getAttribute("isFreeUser") == null ? 0 : (Integer) request.getSession().getAttribute("isFreeUser");
								if (isFreeUser != 1) {
									for (int j = 0; j < price.size(); j++) {
										PPrice pr = price.get(j);
										double endPrice = MathHelper.round(MathHelper.mul(pr.getPrice(), 1.13d));
										price.get(j).setPrice(endPrice);
									}
								}
								pub.setPriceList(price);
							}
							// 查询分类
							// Map<String,Object> con2 = new
							// HashMap<String,Object>();
							// con2.put("publicationsId", pub.getId());
							// List<PCsRelation> csList =
							// this.bSubjectService.getSubPubList(con2,
							// " order by a.subject.code ");
							// pub.setCsList(csList);
							resultList.add(pub);
						}
					}
					// ----------------------进行高亮--------------
					/*
					 * if(form.getSearchValue()!=null&&!"".equals(form.
					 * getSearchValue())){ form.setKeyMap(this.highLight(0,
					 * form.getSearchValue())); }
					 */
					// ----------------------高亮结束--------------
					// model.put("pubDateMap", pubDate);
					form.setCount(allCount);
					model.put("list", resultList);
				} else {
					form.setCount(0);
				}
			}
			List<FacetField> facetFields = (List<FacetField>) resultMap.get("facet");
			Map<String, Integer> pubDate = new HashMap<String, Integer>();
			for (FacetField fac : facetFields) {
				if (fac.getName().equals("pubDate")) {
					List<Count> counts = fac.getValues();
					for (Count count : counts) {
						if (count == null || count.getName() == null || count.getName().length() < 4) {
							continue;
						}
						int num = pubDate.get(count.getName().substring(0, 4)) == null ? 0 : Integer.valueOf(pubDate.get(count.getName().substring(0, 4)));
						if (count.getCount() > 0) {
							pubDate.put(count.getName().substring(0, 4).toString(), (num + (int) count.getCount()));
						}
					}
				}
				// 中文分类处理
				if (fac.getName().equals("taxonomy")) { // 过滤中文取相同分类 yangheqing
														// 2014-05-27
					List<Count> counts = fac.getValues();
					if (param.containsKey("taxonomy") && param.get("taxonomy") != null && !"".equals(param.get("taxonomy"))) {
						String[] taxArr = param.get("taxonomy").replace("\"", "").split(",");

						for (int i = counts.size() - 1; i >= 0; i--) {
							/*** 中文 ***/
							String subCode = taxArr[taxArr.length - 1].split(" ")[0];
							if (!counts.get(i).toString().toLowerCase().startsWith(subCode.toLowerCase())) {
								counts.remove(i);
							}
						}
					} else {
						/*
						 * for(int i=counts.size()-1;i>=0;i--){
						 *//*** 中文 ***/
						/*
						 * if(counts.get(i).toString().split(" ")[0].toString().
						 * length()>1){ counts.remove(i); } }
						 */
					}
					if (counts != null && counts.size() > 0) {
						ComparatorSubject comparator = new ComparatorSubject();
						Collections.sort(counts, comparator);
						model.put("taxonomyList", counts);
					}
				}
				// 英文分类处理
				if (fac.getName().equals("taxonomyEn")) {
					List<Count> counts = fac.getValues();
					if (param.containsKey("taxonomyEn") && param.get("taxonomyEn") != null && !"".equals(param.get("taxonomyEn"))) {
						String[] taxArr = param.get("taxonomyEn").replace("\"", "").split(",");

						for (int i = counts.size() - 1; i >= 0; i--) {
							String subCode = taxArr[taxArr.length - 1].split(" ")[0];
							if (!counts.get(i).toString().toLowerCase().startsWith(subCode.toLowerCase())) {
								counts.remove(i);
							}
						}
					} else {
						/*
						 * for(int i=counts.size()-1;i>=0;i--){
						 * if(counts.get(i). toString().split(" "
						 * )[0].toString().length()>1){ counts.remove(i); } }
						 */
					}
					if (counts != null && counts.size() > 0) {
						ComparatorSubject comparator = new ComparatorSubject();
						Collections.sort(counts, comparator);
						model.put("taxonomyEnList", counts);
					}
				}

				// 出版社处理
				if (fac.getName().equals("publisher")) {
					List<Count> counts = fac.getValues();
					if (counts != null && counts.size() > 0) {
						ComparatorSubject comparator = new ComparatorSubject();
						Collections.sort(counts, comparator);
						model.put("publisherList", counts);
					}
				}
				// 类型处理
				if (fac.getName().equals("type")) {
					List<Count> counts = fac.getValues();
					if (counts != null && counts.size() > 0) {
						int journalIndex = -1;
						int issueIndex = -1;
						for (int i = 0; i < counts.size(); i++) {
							if ("2".equals(counts.get(i).getName())) {
								journalIndex = i;
							} else if ("7".equals(counts.get(i).getName())) {
								issueIndex = i;
							}
						}
						if (issueIndex > -1) {
							long finalJournalCount = 0;
							if (journalIndex > -1) {// 如果存在期时就把期的数量加在期刊的数量上
								finalJournalCount = counts.get(journalIndex).getCount() + counts.get(issueIndex).getCount();
								counts.get(journalIndex).setCount(finalJournalCount);
								counts.remove(issueIndex);
							} else {// 如果期刊不存在，就把期的数量算上期刊上
								finalJournalCount = counts.get(issueIndex).getCount();
								counts.get(issueIndex).setName("2");
							}
						}
						model.put("typeList", counts);
					}
				}

				// 语种处理
				if (fac.getName().equals("language")) {

					List<Count> counts = fac.getValues();
					if (counts != null && counts.size() > 0) {
						ComparatorSubject comparator = new ComparatorSubject();
						Collections.sort(counts, comparator);
						model.put("languageList", counts);
					}
				}

				if (model.get("queryCount") == null) {
					model.put("queryCount", 0);
				}
				model.put("facetFields", facetFields);
				model.put("pubDateMap", SequenceUtil.MapDescToKey(pubDate));
			}
			if (form.getSearchValue() != null && !"".equals(form.getSearchValue()) && form.getCount() > 0) {
				CUser cuser = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
				if (cuser != null) {
					if (form.getCount() > 0) {
						CSearchHis obj = new CSearchHis();
						obj.setCreateOn(new Date());
						obj.setKeyword(form.getSearchValue());
						obj.setType(1);// 临时保存...下次登录的时候清空
						obj.setUser(cuser);
						obj.setKeyType(form.getSearchsType() == null ? 0 : form.getSearchsType());
						this.cUserService.addSearchHistory(obj);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("prompt", Lang.getLanguage("Controller.Index.search.prompt.error", request.getSession().getAttribute("lang").toString()));// 搜索错误提示
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		if (toTab == "" || toTab.equals("")) {
			model.put("current", "search");
		} else if (toTab == "1" || toTab.equals("1")) {
			model.put("current", "searchLicense");
		} else if (toTab == "2" || toTab.equals("2")) {
			model.put("current", "searchOaFree");
		}
		// 用于回显搜索关键词
		if (null != form.getSearchValue() && !"".equals(form.getSearchValue().toString())) {
			form.setSearchValue(URLDecoder.decode(form.getSearchValue(), "UTF-8"));
		}
		model.put("form", form);
		return new ModelAndView(forwardString, model);

	}

	/**
	 * 期刊二级页面JSON
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/journalListJson")
	public ModelAndView journalListJson(HttpServletRequest request, HttpServletResponse response, IndexForm form, String isJson, String cp) throws Exception {
		String forwardString = "mobile/publications/journalList";
		JSONObject json = null;
		CUser user1 = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
		String toTab = request.getSession().getAttribute("selectType").toString();
		String selectFlag = form.getSelectflag();
		if (selectFlag != null) {
			if (selectFlag == "oaFree" || selectFlag.equals("oaFree")) {
				toTab = "2";
			}
			if (selectFlag == "license" || selectFlag.equals("license")) {
				toTab = "1";
			}
			if (selectFlag == "all" || selectFlag.equals("all")) {
				toTab = "";
			}
		}
		//默认按上架时间倒序排序
		if(form.getSearchOrder()==null && form.getSortFlag()==null ){
			form.setSearchOrder("pubDate");
			form.setSortFlag("desc");
		}
		// BInstitution ins = (BInstitution)
		// request.getSession().getAttribute("institution");
		// if (null != user1 || null != ins) {
		// form.setLcense("1");
		// }
		String lcense = form.getLcense();
		String lang = (String) request.getSession().getAttribute("lang");
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			String isCn = form.getIsCn();
			form.setUrl(request.getRequestURL().toString());

			/*********** 查询条件区*开始 ***************/
			Map<String, String> param = new HashMap<String, String>();
			/** 特殊机构处理 -- START */
			String specialInstitutionFlag = request.getSession().getAttribute("specialInstitutionFlag") != null ? (String) request.getSession().getAttribute("specialInstitutionFlag") : null;
			if (null != specialInstitutionFlag && specialInstitutionFlag.length() > 0) {
				form = this.specialInstitution_handle(form, specialInstitutionFlag);
			}
			/** 特殊机构处理 -- END */
			param.put("language", (form.getLanguage() == null || "".equals(form.getLanguage())) ? null : "\"" + form.getLanguage() + "\"");
			param.put("title", (form.getTitle() == null || "".equals(form.getTitle())) ? null : form.getTitle());
			param.put("author", (form.getAuthor() == null || "".equals(form.getAuthor())) ? null : form.getAuthor());
			param.put("taxonomy", (form.getTaxonomy() == null || "".equals(form.getTaxonomy())) ? null : "\"" + form.getTaxonomy() + "\"");
			param.put("taxonomyEn", (form.getTaxonomyEn() == null || "".equals(form.getTaxonomyEn())) ? null : "\"" + form.getTaxonomyEn() + "\"");
			param.put("pubType", "2");
			// param.put("publisher", (form.getPublisher() == null ||
			// "".equals(form.getPublisher())) ? null : "\"" +
			// form.getPublisher() + "\"");
			param.put("publisher", (form.getPublisher() == null || "".equals(form.getPublisher())) ? null : form.getPublisher());
			// param.put("type",
			// (form.getPubType()==null||"".equals(form.getPubType()))?null:form.getPubType());
			param.put("pubDate", (form.getPubDate() == null || "".equals(form.getPubDate())) ? null : form.getPubDate() + "*");
			// 首字母
			// 最新资源排序检索
			param.put("sortFlag", (null == form.getSortFlag()) || "".equals(form.getSortFlag()) ? null : form.getSortFlag());
			/*********** 查询条件区*结束 ***************/
			/*** 中文 ***/
			if (param.containsKey("taxonomy") && param.get("taxonomy") != null) {
				String[] taxArr = param.get("taxonomy").replace("\"", "").split(",");
				model.put("taxArr", taxArr);
			}
			/*** 英文 ***/
			if (param.containsKey("taxonomyEn") && param.get("taxonomyEn") != null) {
				String[] taxArrEn = param.get("taxonomyEn").replace("\"", "").split(",");
				model.put("taxArrEn", taxArrEn);
			}

			Map<String, Object> resultMap = new HashMap<String, Object>();
			String userId = "";
			if (toTab == "" || toTab.equals("")) {// 按照已订阅查询
				request.getSession().setAttribute("selectType", "");// selectType
																	// 用来保存全局的变量，看是全部还是在已订阅中查询
																	// 2-全部
																	// 1-已订阅、

				resultMap = this.publicationsIndexService.advancedSearchMobile(form.getCurpage(), 20, param, form.getSearchOrder() + "##" + form.getSortFlag(), isCn);
				// 限制查询结果总数----------开始-----------
				Integer maxCount = Integer.parseInt(Param.getParam("search.config").get("maxCount"));
				Integer allCount = 0;
				if (resultMap.get("count") != null) {
					allCount = Integer.valueOf(resultMap.get("count").toString());
					model.put("queryCount", allCount);// 实际查询结果数量

					// 最多显示1000条
					allCount = maxCount > allCount ? allCount : maxCount;// 分页条显示的数量
				}
				// 限制查询结果总数----------结束-----------
				if (allCount > 0) {
					List<Map<String, Object>> list = (List<Map<String, Object>>) resultMap.get("result");
					List<PPublications> resultList = new ArrayList<PPublications>();
					for (Map<String, Object> idInfo : list) {
						// 根据ID查询产品信息
						// 由于加入了标签，这里不能用get查询
						Map<String, Object> condition = new HashMap<String, Object>();
						condition.put("id", idInfo.get("id"));
						// condition.put("check","false");
						condition.put("status", null);
						condition.put("available", 3);
						List<PPublications> ppList = this.pPublicationsService.getPubList3(condition, " order by a.createOn ", (CUser) request.getSession().getAttribute("mainUser"), IpUtil.getIp(request));
						if (ppList != null && ppList.size() > 0) {
							PPublications pub = ppList.get(0);
							if (pub != null && pub.getRemark() != null && "[无简介]".equals(pub.getRemark())) {
								pub.setRemark("");
							}
							if (idInfo.containsKey("title") && idInfo.get("title") != null && !"".equals("title"))
								pub.setTitle(idInfo.get("title").toString());
							if (idInfo.containsKey("author") && idInfo.get("author") != null && !"".equals("author"))
								pub.setAuthor(idInfo.get("author").toString());
							if (idInfo.containsKey("isbn") && idInfo.get("isbn") != null && !"".equals("isbn"))
								pub.setCode(idInfo.get("isbn").toString());
							if (null != idInfo.get("copyPublisher") && idInfo.containsKey("copyPublisher") && !"".equals("copyPublisher")) {
								pub.getPublisher().setName(idInfo.get("copyPublisher").toString());
							}
							if (idInfo.containsKey("remark") && idInfo.get("remark") != null && !"".equals("remark"))
								pub.setRemark(idInfo.get("remark").toString());

							if (idInfo.containsKey("score") && idInfo.get("score") != null && !"".equals("score"))
								pub.setActivity(idInfo.get("score").toString());

							if (user1 != null) {
								Map<String, Object> con = new HashMap<String, Object>();
								con.put("publicationsid", idInfo.get("id"));
								con.put("status", 2);
								con.put("userTypeId", user1 == null ? "1" : user1.getUserType() == null ? "1" : user1.getUserType().getId());
								// con.put("userTypeId",
								// user1.getUserType().getId()==null?"":user1.getUserType().getId());
								List<PPrice> price = this.pPublicationsService.getPriceList(con);
								int isFreeUser = request.getSession().getAttribute("isFreeUser") == null ? 0 : (Integer) request.getSession().getAttribute("isFreeUser");
								if (isFreeUser != 1) {
									for (int j = 0; j < price.size(); j++) {
										PPrice pr = price.get(j);
										double endPrice = MathHelper.round(MathHelper.mul(pr.getPrice(), 1.13d));
										price.get(j).setPrice(endPrice);
									}
								}
								pub.setPriceList(price);
							}
							// 查询分类
							// Map<String,Object> con2 = new
							// HashMap<String,Object>();
							// con2.put("publicationsId", pub.getId());
							// List<PCsRelation> csList =
							// this.bSubjectService.getSubPubList(con2,
							// " order by a.subject.code ");
							// pub.setCsList(csList);
							resultList.add(pub);
						}
					}
					// ----------------------进行高亮--------------
					/*
					 * if(form.getSearchValue()!=null&&!"".equals(form.
					 * getSearchValue())){ form.setKeyMap(this.highLight(0,
					 * form.getSearchValue())); }
					 */
					// ----------------------高亮结束--------------
					// model.put("pubDateMap", pubDate);

					form.setCount(allCount);
					model.put("list", resultList);
					model.put("curpage", form.getCurpage() + 1);
					json = JSONObject.fromObject(model);
				} else {
					form.setCount(0);
				}
			} else if (toTab == "1" || toTab.equals("1")) {
				CUser user = (CUser) request.getSession().getAttribute("mainUser");
				request.getSession().setAttribute("selectType", 1);// selectType
																	// 用来保存全局的变量，看是全部还是在已订阅中查询
																	// 2-全部
																	// 1-已订阅
				StringBuffer userIds = new StringBuffer();
				// 访问IP
				long ip = IpUtil.getLongIp(IpUtil.getIp(request));
				// 查询机构信息
				Map<String, Object> mapip = new HashMap<String, Object>();
				mapip.put("ip", ip);
				List<BIpRange> lip = this.configureService.getIpRangeList(mapip, "");
				if (lip != null && lip.size() > 0) {
					// 根据机构ID,查询用户
					for (BIpRange bIpRange : lip) {
						Map<String, Object> uc = new HashMap<String, Object>();
						// uc.put("institutionId",bIpRange.getInstitution().getId()
						// );
						if (user != null && user.getLevel() == 2) {
							uc.put("institutionId", user.getInstitution().getId());
						} else {
							uc.put("institutionId", bIpRange.getInstitution().getId());
						}
						uc.put("insStatus", 1);// 1-机构未被禁用状态
						uc.put("level", 2);
						List<CUser> lu = this.cUserService.getUserList(uc, "");
						for (CUser cUser : lu) {
							userIds.append(cUser.getId()).append(",");
						}
					}
				}
				// 查询用户ID
				if (request.getSession().getAttribute("mainUser") != null) {
					user = (CUser) request.getSession().getAttribute("mainUser");
					userIds.append(user.getId()).append(",");
				}
				// userIds.append(Param.getParam("OAFree.uid.config").get("uid")).append(",");
				if ("".equals(userIds.toString())) {
					throw new CcsException("Controller.Index.searchLicense.noLogin");// 未登录用户，无法按照“已订阅”查询

				} else {
					userId = userIds.substring(0, userIds.toString().lastIndexOf(","));
					// 在solr中查询 [搜索类型=====0-全文;1-标题;2-作者]
					Integer coverType = 1;// 区分免费开源和已订阅
					resultMap = this.licenseIndexService.advancedSearch(coverType, userId, form.getCurpage(), 20, param, form.getSearchOrder() + "##" + form.getSortFlag());
					if (resultMap.get("count") != null && Long.valueOf(resultMap.get("count").toString()) > 0) {
						// 限制查询结果总数----------开始-----------
						Integer maxCount = Integer.parseInt(Param.getParam("search.config").get("maxCount"));
						Integer allCount = 0;
						if (resultMap.get("count") != null) {
							allCount = Integer.valueOf(resultMap.get("count").toString());
							model.put("queryCount", allCount);// 实际查询结果数量
							// 最多显示1000条
							allCount = maxCount > allCount ? allCount : maxCount;// 分页条显示的数量
						}
						List<Map<String, Object>> list = (List<Map<String, Object>>) resultMap.get("result");
						List<PPublications> resultList = new ArrayList<PPublications>();
						// 这里根据LicenseId查询产品
						for (Map<String, Object> idInfo : list) {
							String lid = idInfo.get("id").toString();
							String pid = "";
							if (lid.contains("_")) {
								pid = lid.substring(lid.lastIndexOf("_") + 1, lid.length());
							} else {
								LLicense lli = this.pPublicationsService.getLicense(lid);
								if (lli != null) {
									pid = lli.getPublications().getId();
								}
							}
							if (pid != null && !"".equals(pid)) {
								// 根据ID查询产品信息
								// 由于加入了标签，这里不能用get查询
								Map<String, Object> condition = new HashMap<String, Object>();
								condition.put("id", pid);
								condition.put("status", null);
								condition.put("available", 3);
								List<PPublications> ppList = this.pPublicationsService.getPubList3(condition, " order by a.createOn ", (CUser) request.getSession().getAttribute("mainUser"), IpUtil.getIp(request));
								if (ppList != null && ppList.size() > 0) {
									PPublications pub = ppList.get(0);
									if (pub != null && pub.getRemark() != null && "[无简介]".equals(pub.getRemark())) {
										pub.setRemark("");
									}
									if (idInfo.containsKey("title") && idInfo.get("title") != null && !"".equals("title"))
										pub.setTitle(idInfo.get("title").toString());
									if (idInfo.containsKey("author") && idInfo.get("author") != null && !"".equals("author"))
										pub.setAuthor(idInfo.get("author").toString());
									if (idInfo.containsKey("isbn") && idInfo.get("isbn") != null && !"".equals("isbn"))
										pub.setCode(idInfo.get("isbn").toString());
									if (idInfo.containsKey("copyPublisher") && idInfo.get("copyPublisher") != null && !"".equals("copyPublisher"))
										pub.getPublisher().setName(idInfo.get("copyPublisher").toString());
									if (idInfo.containsKey("remark") && idInfo.get("remark") != null && !"".equals("remark"))
										pub.setRemark(idInfo.get("remark").toString());

									if (idInfo.containsKey("score") && idInfo.get("score") != null && !"".equals("score"))
										pub.setActivity(idInfo.get("score").toString());
									if (user1 != null) {
										Map<String, Object> con = new HashMap<String, Object>();
										con.put("publicationsid", idInfo.get("id"));
										con.put("status", 2);
										con.put("userTypeId", user1.getUserType().getId() == null ? "" : user1.getUserType().getId());
										List<PPrice> price = this.pPublicationsService.getPriceList(con);
										int isFreeUser = request.getSession().getAttribute("isFreeUser") == null ? 0 : (Integer) request.getSession().getAttribute("isFreeUser");
										if (isFreeUser != 1) {
											for (int j = 0; j < price.size(); j++) {
												PPrice pr = price.get(j);
												double endPrice = MathHelper.round(MathHelper.mul(pr.getPrice(), 1.13d));
												price.get(j).setPrice(endPrice);
											}
										}
										pub.setPriceList(price);
									}
									// 查询分类
									// Map<String,Object> con2 = new
									// HashMap<String,Object>();
									// con2.put("publicationsId", pub.getId());
									// List<PCsRelation> csList =
									// this.bSubjectService.getSubPubList(con2,
									// " order by a.subject.code ");
									// pub.setCsList(csList);
									resultList.add(pub);
								}
							}
						}
						// ----------------------进行高亮--------------
						/*
						 * if(form.getSearchValue()!=null&&!"".equals(form.
						 * getSearchValue())){ form.setKeyMap(this.highLight(0,
						 * form.getSearchValue())); }
						 */
						// ----------------------高亮结束--------------
						form.setCount(allCount);
						model.put("list", resultList);
						model.put("curpage", form.getCurpage() + 1);
						json = JSONObject.fromObject(model);
					} else {
						form.setCount(0);
					}
				}
			} else if (toTab == "2" || toTab.equals("2")) {// 开源、免费查询
				request.getSession().setAttribute("selectType", "2");// selectType
																		// 用来保存全局的变量，看是全部还是在已订阅中查询
																		// 2-全部
																		// 1-已订阅、
				String oafree = "";
				Integer coverType = 2;// 区分 免费开源和已订阅 查询
				Map<String, String> oafreeMap = new HashMap<String, String>();
				oafreeMap = Param.getParam("OAFree.uid.config");
				oafree = oafreeMap.get("uid");
				resultMap = this.licenseIndexService.advancedSearch(coverType, oafree, form.getCurpage(), 20, param, form.getSearchOrder() + "##" + form.getSortFlag());
				// resultMap =
				// this.publicationsIndexService.advancedSearch(oafree,form.getCurpage(),
				// form.getPageCount(),param,form.getSearchOrder());

				if (resultMap.get("count") != null && Long.valueOf(resultMap.get("count").toString()) > 0) {
					// 限制查询结果总数----------开始-----------
					Integer maxCount = Integer.parseInt(Param.getParam("search.config").get("maxCount"));
					Integer allCount = 0;
					if (resultMap.get("count") != null) {
						allCount = Integer.valueOf(resultMap.get("count").toString());
						model.put("queryCount", allCount);// 实际查询结果数量
						// 最多显示1000条
						allCount = maxCount > allCount ? allCount : maxCount;// 分页条显示的数量
					}
					// 限制查询结果总数----------结束-----------
					List<Map<String, Object>> list = (List<Map<String, Object>>) resultMap.get("result");
					List<PPublications> resultList = new ArrayList<PPublications>();
					for (Map<String, Object> idInfo : list) {
						// 根据ID查询产品信息
						// 由于加入了标签，这里不能用get查询
						Map<String, Object> condition = new HashMap<String, Object>();
						String oafreeid = "";
						if (idInfo.get("id").toString().startsWith("oafree_")) {
							oafreeid = idInfo.get("id").toString().replaceAll("oafree_", "");
						}
						condition.put("id", oafreeid);
						// condition.put("check","false");
						condition.put("status", null);
						condition.put("available", 3);
						// condition.put("oafree",2);
						List<PPublications> ppList = this.pPublicationsService.getPubList3(condition, " order by a.createOn ", (CUser) request.getSession().getAttribute("mainUser"), IpUtil.getIp(request));
						if (ppList != null && ppList.size() > 0) {
							PPublications pub = ppList.get(0);
							if (pub != null && pub.getRemark() != null && "[无简介]".equals(pub.getRemark())) {
								pub.setRemark("");
							}
							if (idInfo.containsKey("title") && idInfo.get("title") != null && !"".equals("title"))
								pub.setTitle(idInfo.get("title").toString());
							if (idInfo.containsKey("author") && idInfo.get("author") != null && !"".equals("author"))
								pub.setAuthor(idInfo.get("author").toString());
							if (idInfo.containsKey("isbn") && idInfo.get("isbn") != null && !"".equals("isbn"))
								pub.setCode(idInfo.get("isbn").toString());
							if (idInfo.containsKey("copyPublisher") && idInfo.get("copyPublisher") != null && !"".equals("copyPublisher"))
								pub.getPublisher().setName(idInfo.get("copyPublisher").toString());
							if (idInfo.containsKey("remark") && idInfo.get("remark") != null && !"".equals("remark"))
								pub.setRemark(idInfo.get("remark").toString());

							if (idInfo.containsKey("score") && idInfo.get("score") != null && !"".equals("score"))
								pub.setActivity(idInfo.get("score").toString());

							if (user1 != null) {
								Map<String, Object> con = new HashMap<String, Object>();
								con.put("publicationsid", idInfo.get("id"));
								con.put("status", 2);
								con.put("userTypeId", user1 == null ? "1" : user1.getUserType() == null ? "1" : user1.getUserType().getId());
								// con.put("userTypeId",
								// user1.getUserType().getId()==null?"":user1.getUserType().getId());
								List<PPrice> price = this.pPublicationsService.getPriceList(con);
								int isFreeUser = request.getSession().getAttribute("isFreeUser") == null ? 0 : (Integer) request.getSession().getAttribute("isFreeUser");
								if (isFreeUser != 1) {
									for (int j = 0; j < price.size(); j++) {
										PPrice pr = price.get(j);
										double endPrice = MathHelper.round(MathHelper.mul(pr.getPrice(), 1.13d));
										price.get(j).setPrice(endPrice);
									}
								}
								pub.setPriceList(price);
							}
							// 查询分类
							// Map<String,Object> con2 = new
							// HashMap<String,Object>();
							// con2.put("publicationsId", pub.getId());
							// List<PCsRelation> csList =
							// this.bSubjectService.getSubPubList(con2,
							// " order by a.subject.code ");
							// pub.setCsList(csList);
							resultList.add(pub);
						}
					}
					// ----------------------进行高亮--------------
					/*
					 * if(form.getSearchValue()!=null&&!"".equals(form.
					 * getSearchValue())){ form.setKeyMap(this.highLight(0,
					 * form.getSearchValue())); }
					 */
					// ----------------------高亮结束--------------
					// model.put("pubDateMap", pubDate);
					form.setCount(allCount);
					model.put("list", resultList);
					model.put("curpage", form.getCurpage() + 1);
					json = JSONObject.fromObject(model);
				} else {
					form.setCount(0);
				}
			}
			List<FacetField> facetFields = (List<FacetField>) resultMap.get("facet");
			Map<String, Integer> pubDate = new HashMap<String, Integer>();
			for (FacetField fac : facetFields) {
				if (fac.getName().equals("pubDate")) {
					List<Count> counts = fac.getValues();
					for (Count count : counts) {
						if (count == null || count.getName() == null || count.getName().length() < 4) {
							continue;
						}
						int num = pubDate.get(count.getName().substring(0, 4)) == null ? 0 : Integer.valueOf(pubDate.get(count.getName().substring(0, 4)));
						if (count.getCount() > 0) {
							pubDate.put(count.getName().substring(0, 4).toString(), (num + (int) count.getCount()));
						}
					}
				}
				// 中文分类处理
				if (fac.getName().equals("taxonomy")) { // 过滤中文取相同分类 yangheqing
														// 2014-05-27
					List<Count> counts = fac.getValues();
					if (param.containsKey("taxonomy") && param.get("taxonomy") != null && !"".equals(param.get("taxonomy"))) {
						String[] taxArr = param.get("taxonomy").replace("\"", "").split(",");

						for (int i = counts.size() - 1; i >= 0; i--) {
							/*** 中文 ***/
							String subCode = taxArr[taxArr.length - 1].split(" ")[0];
							if (!counts.get(i).toString().toLowerCase().startsWith(subCode.toLowerCase())) {
								counts.remove(i);
							}
						}
					} else {
						/*
						 * for(int i=counts.size()-1;i>=0;i--){
						 *//*** 中文 ***/
						/*
						 * if(counts.get(i).toString().split(" ")[0].toString().
						 * length()>1){ counts.remove(i); } }
						 */
					}
					if (counts != null && counts.size() > 0) {
						ComparatorSubject comparator = new ComparatorSubject();
						Collections.sort(counts, comparator);
						model.put("taxonomyList", counts);
					}
				}
				// 英文分类处理
				if (fac.getName().equals("taxonomyEn")) {
					List<Count> counts = fac.getValues();
					if (param.containsKey("taxonomyEn") && param.get("taxonomyEn") != null && !"".equals(param.get("taxonomyEn"))) {
						String[] taxArr = param.get("taxonomyEn").replace("\"", "").split(",");

						for (int i = counts.size() - 1; i >= 0; i--) {
							String subCode = taxArr[taxArr.length - 1].split(" ")[0];
							if (!counts.get(i).toString().toLowerCase().startsWith(subCode.toLowerCase())) {
								counts.remove(i);
							}
						}
					} else {
						/*
						 * for(int i=counts.size()-1;i>=0;i--){
						 * if(counts.get(i). toString().split(" "
						 * )[0].toString().length()>1){ counts.remove(i); } }
						 */
					}
					if (counts != null && counts.size() > 0) {
						ComparatorSubject comparator = new ComparatorSubject();
						Collections.sort(counts, comparator);
						model.put("taxonomyEnList", counts);
					}
				}

				// 出版社处理
				if (fac.getName().equals("publisher")) {
					List<Count> counts = fac.getValues();
					if (counts != null && counts.size() > 0) {
						ComparatorSubject comparator = new ComparatorSubject();
						Collections.sort(counts, comparator);
						model.put("publisherList", counts);
					}
				}
				// 类型处理
				if (fac.getName().equals("type")) {
					List<Count> counts = fac.getValues();
					if (counts != null && counts.size() > 0) {
						int journalIndex = -1;
						int issueIndex = -1;
						for (int i = 0; i < counts.size(); i++) {
							if ("2".equals(counts.get(i).getName())) {
								journalIndex = i;
							} else if ("7".equals(counts.get(i).getName())) {
								issueIndex = i;
							}
						}
						if (issueIndex > -1) {
							long finalJournalCount = 0;
							if (journalIndex > -1) {// 如果存在期时就把期的数量加在期刊的数量上
								finalJournalCount = counts.get(journalIndex).getCount() + counts.get(issueIndex).getCount();
								counts.get(journalIndex).setCount(finalJournalCount);
								counts.remove(issueIndex);
							} else {// 如果期刊不存在，就把期的数量算上期刊上
								finalJournalCount = counts.get(issueIndex).getCount();
								counts.get(issueIndex).setName("2");
							}
						}
						model.put("typeList", counts);
					}
				}

				// 语种处理
				if (fac.getName().equals("language")) {

					List<Count> counts = fac.getValues();
					if (counts != null && counts.size() > 0) {
						ComparatorSubject comparator = new ComparatorSubject();
						Collections.sort(counts, comparator);
						model.put("languageList", counts);
					}
				}

				if (model.get("queryCount") == null) {
					model.put("queryCount", 0);
				}
				model.put("facetFields", facetFields);
				model.put("pubDateMap", SequenceUtil.MapDescToKey(pubDate));
			}
			if (form.getSearchValue() != null && !"".equals(form.getSearchValue()) && form.getCount() > 0) {
				CUser cuser = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
				if (cuser != null) {
					if (form.getCount() > 0) {
						CSearchHis obj = new CSearchHis();
						obj.setCreateOn(new Date());
						obj.setKeyword(form.getSearchValue());
						obj.setType(1);// 临时保存...下次登录的时候清空
						obj.setUser(cuser);
						obj.setKeyType(form.getSearchsType() == null ? 0 : form.getSearchsType());
						this.cUserService.addSearchHistory(obj);
					}
				}
			}

			response.setContentType("text/json;charset=utf-8");
			PrintWriter writer = null;
			try {
				// 获取输出流
				writer = response.getWriter();
				writer.print(json.toString());
			} catch (IOException e) {
				// e.printStackTrace();
				throw e;
			} finally {
				if (writer != null) {
					writer.close();
				}
			}

			// 获取机构ID
			BInstitution ins = (BInstitution) request.getSession().getAttribute("institution");
			model.put("insInfo", ins == null ? null : ins.getId());
			
		} catch (Exception e) {
			form.setMsg((e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		model.put("form", form);
		return new ModelAndView(forwardString, model);
	}

	// 特殊机构处理
	private IndexForm specialInstitution_handle(IndexForm form, String specialInstitutionFlag) {
		if (null != form) {
			form.setNotLanguage(specialInstitutionFlag);
			form.setPubType("1"); // 图书类别
			form.setLocal("2"); // 查询本地资源
		}
		return form;
	}

	/**
	 * 查找在线阅读时需要的url
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/getUrl")
	public void getUrl(HttpServletRequest request, HttpServletResponse response, HttpSession session, PPublicationsForm form) throws Exception {
		
		String result = "mobile/error;" + Lang.getLanguage("Controller.Publications.UnFind", request.getSession().getAttribute("lang").toString());
		try {
			int falg = 0;
			String md5Str = "";
			Cookie[] cookies = request.getCookies();
			if (cookies != null && cookies.length > 0) {
				for (Cookie cookie : cookies) {
					if ("readCookie".equals(cookie.getName())) {
						md5Str = cookie.getValue();
						break;
					}
				}
			}
			if ("".equals(md5Str)) {
				md5Str = MD5Util.getEncryptedPwd(session.getId());
				Cookie c = new Cookie("readCookie", md5Str);
				c.setMaxAge(60 * 60 * 24 * 365);
				c.setPath(request.getContextPath());
				response.addCookie(c);
			}
			PPublications pub = this.pPublicationsService.getPublications(form.getId());

			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("publicationId", pub.getId());
			condition.put("ip", IpUtil.getLongIp(IpUtil.getIp(request)));
			condition.put("status", 1);
			List<LLicenseIp> list = this.pPublicationsService.getLicenseIpList(condition, "");
			// 章节本身没有license的话，继续查一下其父级书是否有license
			// 为了能优先使用其本身的license避免占用书的并发数，这里分两次查询
			if ((list == null || list.isEmpty()) && pub.getType() == 3) {
				condition.clear();
				condition.put("pubParentId", pub.getPublications().getId());
				condition.put("ip", IpUtil.getLongIp(IpUtil.getIp(request)));
				condition.put("status", 1);
				list = this.pPublicationsService.getLicenseIpList(condition, "");
			}
			if (list != null && list.size() > 0) {
				LLicense license = list.get(0).getLicense();
				if (license.getComplicating() != null && license.getComplicating() != 0) {// 即有并发限制
					// 释放占用的并发
					String beatInterval = Param.getParam("system.config").get("beatInterval").trim();
					this.pPublicationsService.deleteDead(Integer.valueOf(beatInterval) + 10);
					Map<String, Object> uCon = new HashMap<String, Object>();
					uCon.put("licenseId", license.getId());
					uCon.put("sessionId", session.getId());
					uCon.put("endTimeNull", null);
					boolean b = this.pPublicationsService.isExistComplicating(uCon);
					if (!b) {
						uCon = new HashMap<String, Object>();
						uCon.put("licenseId", license.getId());
						uCon.put("endTimeNull", null);
						int count = this.pPublicationsService.getComplicatingCount(uCon);
						if (count >= license.getComplicating()) {
							falg = 1;
							result = "error;" + Lang.getLanguage("Controller.Publications.complicating.error", request.getSession().getAttribute("lang").toString());// 超出最大并发数!";
						} else {
							// 记录下新人
							LComplicating comp = new LComplicating();
							comp.setLicense(license);
							comp.setSessionId(session.getId());
							comp.setUser(license.getUser());
							comp.setMacAddr(md5Str);
							comp.setPubCode(license.getPublications().getCode());
							comp.setCreateOn(new Date());
							comp.setUpdateTime(comp.getCreateOn());// 添加更新时间
							comp.setEndTime(null);// 结束时间
							this.pPublicationsService.insertComplicating(comp);
							Map<String, Object> compMap = session.getAttribute("compMap") != null ? (Map<String, Object>) session.getAttribute("compMap") : new HashMap<String, Object>();
							compMap.put(license.getId(), license.getId());
							session.setAttribute("compMap", compMap);
						}
						// }
					}
				}
				if (falg == 0) {
					if (list.get(0).getLicense().getReadUrl().indexOf("/pages/view/form/view") > -1) {
						// 查询机构信息
						Map<String, Object> con = new HashMap<String, Object>();
						con.put("ip", IpUtil.getLongIp(IpUtil.getIp(request)));
						List<BIpRange> li = this.configureService.getIpRangeList(con, "");
						String ipAddress = list.get(0).getLicense().getReadUrl().replaceAll("http://www.cnpereading.com", Param.getParam("system.config").get("domain")).replace("-",":");
						if (li != null && li.size() > 0) {
							result = "success;" + ipAddress + "&licenseId=" + list.get(0).getLicense().getId() + "&institutionId=" + li.get(0).getInstitution().getId();
						} else {
							result = "success;" + ipAddress + "&licenseId=" + list.get(0).getLicense().getId();
						}
						if (pub.getType() == 4) {// 4-文章
							result += "&articleId=" + form.getId();
						}
					} else {
						String url = list.get(0).getLicense().getReadUrl().replaceAll("http://www.cnpereading.com", Param.getParam("system.config").get("domain")).replace("-",":");
						if (url.indexOf("dawson") > -1) {
							String code = list.get(0).getLicense().getPublications().getCode();
							String lang = request.getSession().getAttribute("lang").toString().split("_")[1].toLowerCase();
							url = dawsonEncryption.getEncryptOnlineReadUrl(code, lang);
						}
						String code = new String(Base64.encode(url.getBytes()));
						result = "success;" + code;
					}
				}
			} else {
				CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
				if (user != null) {
					Map<String, Object> map2 = new HashMap<String, Object>();
					map2.put("pubId", pub.getId());
					map2.put("status", 1);
					map2.put("userid", user.getId());
					map2.put("isTrail", "0");
					List<LLicense> list2 = this.pPublicationsService.getLicenseList(map2, "");
					// 章节本身没有license的话，继续查一下其父级书是否有license
					// 为了能优先使用其本身的license避免占用书的并发数，这里分两次查询
					if ((list == null || list.isEmpty()) && pub.getType() == 3) {
						map2.clear();
						map2.put("pubParentId", pub.getPublications().getId());
						map2.put("status", 1);
						map2.put("userid", user.getId());
						map2.put("isTrail", "0");
						list2 = this.pPublicationsService.getLicenseList(map2, "");
					}
					if (list2 != null && list2.size() > 0) {
						LLicense license = list2.get(0);
						if (license.getComplicating() != null && license.getComplicating() != 0) {// 即有并发限制
							Map<String, Object> uCon = new HashMap<String, Object>();
							uCon.put("licenseId", license.getId());
							uCon.put("sessionId", session.getId());
							uCon.put("endTimeNull", null);
							// uCon.put("publicationId", form.getId());
							boolean b = this.pPublicationsService.isExistComplicating(uCon);
							boolean b2 = true;
							if (!b) {
								// 根据Cookie查看是否存在
								Map<String, Object> uCon2 = new HashMap<String, Object>();
								uCon2.put("licenseId", license.getId());
								uCon2.put("macId", md5Str);
								uCon2.put("endTimeNull", null);
								// uCon2.put("publicationId", form.getId());
								List<LComplicating> listComp = this.pPublicationsService.getComplicatingList(uCon2, " order by a.createOn ");
								if (listComp != null && listComp.size() > 0) {
									LComplicating lcomp = listComp.get(0);
									if (DateUtil.timeDiff(lcomp.getCreateOn(), new Date()) > 30) {// 大于30分钟
										// 先释放掉，相当于session过期
										Map<String, Object> con3 = new HashMap<String, Object>();
										con3.put("macId", md5Str);
										this.pPublicationsService.deleteComplicatingByCondition(con3);
									} else {
										b2 = false;
									}
								}
								if (b2) {
									// 查看并发数量
									uCon = new HashMap<String, Object>();
									uCon.put("licenseId", license.getId());
									int count = this.pPublicationsService.getComplicatingCount(uCon);
									count = 0; // TODO 测试用
									if (count >= license.getComplicating()) {
										falg = 1;
										result = "error;" + Lang.getLanguage("Controller.Publications.complicating.error", request.getSession().getAttribute("lang").toString());// 超出最大并发数!";
									} else {
										// 记录下新人
										LComplicating comp = new LComplicating();
										comp.setLicense(license);
										comp.setSessionId(session.getId());
										comp.setUser(license.getUser());
										comp.setMacAddr(md5Str);
										comp.setPubCode(license.getPublications().getCode());
										comp.setCreateOn(new Date());
										comp.setUpdateTime(comp.getCreateOn());// 添加更新时间
										comp.setEndTime(null);// 结束时间
										this.pPublicationsService.insertComplicating(comp);
										Map<String, Object> compMap = session.getAttribute("compMap") != null ? (Map<String, Object>) session.getAttribute("compMap") : new HashMap<String, Object>();
										compMap.put(license.getId(), license.getId());
										session.setAttribute("compMap", compMap);
									}
								}
							}
						}
						if (falg == 0) {
							if (license.getReadUrl().indexOf("/pages/view/form/view") > -1) {
								// 查询机构信息
								// license.getReadUrl().indexOf("/pages/view/form/view")
								// > -1
								String institutionId = user.getInstitution() == null ? null : user.getInstitution().getId();
								String ipAddress = list2.get(0).getReadUrl().replaceAll("http://www.cnpereading.com", Param.getParam("system.config").get("domain")).replace("-",":");
								if (institutionId != null && !"".equalsIgnoreCase(institutionId)) {
									result = "success;" + ipAddress + "&licenseId=" + list2.get(0).getId() + "&institutionId=" + institutionId;
								} else {
									result = "success;" + ipAddress + "&licenseId=" + list2.get(0).getId();
								}
								if (pub.getType() == 4) {// 4-文章
									result += "&articleId=" + form.getId();
								}
							} else {
								String url = list2.get(0).getReadUrl().replaceAll("http://www.cnpereading.com", Param.getParam("system.config").get("domain")).replace("-",":");
								if (url.indexOf("dawson") > -1) {
									String code = list2.get(0).getPublications().getCode();
									String lang = request.getSession().getAttribute("lang").toString().split("_")[1].toLowerCase();
									url = dawsonEncryption.getEncryptOnlineReadUrl(code, lang);
								}
								String code = new String(Base64.encode(url.getBytes()));
								result = "success;" + code;
							}

						}
					}
				}
			}
			if (form.getNextPage() != null && form.getNextPage() > 0) {
				result += "&nextPage=" + request.getParameter("nextPage").toString();
			}
			// 需要屏蔽
			// result = result.replaceFirst("http://.+?\\.com(/EPublishing)?", "");
		} catch (Exception e) {
			e.printStackTrace();
			result = "mobile/error;" + ((e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		try {
			response.setContentType("text/html;charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print(result);
			out.flush();
			out.close();
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

}
