package cn.digitalpublishing.springmvc.controller.index;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.com.daxtech.framework.Internationalization.Lang;
import cn.com.daxtech.framework.exception.CcsException;
import cn.com.daxtech.framework.model.Param;
import cn.digitalpublishing.ep.po.BInstitution;
import cn.digitalpublishing.ep.po.BIpRange;
import cn.digitalpublishing.ep.po.CSearchHis;
import cn.digitalpublishing.ep.po.CUser;
import cn.digitalpublishing.ep.po.LAccess;
import cn.digitalpublishing.ep.po.LLicense;
import cn.digitalpublishing.ep.po.PPrice;
import cn.digitalpublishing.ep.po.PPublications;
import cn.digitalpublishing.springmvc.controller.BaseController;
import cn.digitalpublishing.springmvc.form.index.IndexForm;
import cn.digitalpublishing.util.CharUtil;
import cn.digitalpublishing.util.web.ComparatorSubject;
import cn.digitalpublishing.util.web.IpUtil;
import cn.digitalpublishing.util.web.MathHelper;
import cn.digitalpublishing.util.web.SequenceUtil;

/**
 * Mobile Index Controller
 */
@Controller
public class IndexMobileController extends BaseController {

	/**
	 * 首页
	 * 
	 * @param request
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("index")
	public ModelAndView index(HttpServletRequest request, IndexForm form) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		if (request.getSession().getAttribute("selectType") == null) {
			request.getSession().setAttribute("selectType", 1);
		}
		BInstitution ins = (BInstitution) request.getSession().getAttribute("institution");
		request.getSession().setAttribute("path", Param.getParam("config.website.path").get("path").replace("-", ":"));
		CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
		if (user != null && user.getLevel() == 2) {
			ins = user.getInstitution();
			map.put("insInfo", null == ins ? null != user ? user.getInstitution() : null : ins);
		}

		if (user == null && ins == null) {
			request.getSession().setAttribute("selectType", "");
		}

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("ip", IpUtil.getLongIp(IpUtil.getIp(request)));
		List<BIpRange> ipList = configureService.getIpRangeList(condition, "");
		if (ipList != null && ipList.size() > 0) {
			ins = ipList.get(0).getInstitution();
			map.put("insInfo", ins);
		}

		map.put("form", form);
		return new ModelAndView("mobile/index", map);
	}

	@RequestMapping("welcome")
	public ModelAndView welcome(HttpServletRequest request) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		Map<String, Object> condition = new HashMap<String, Object>();
		BInstitution ins = (BInstitution) request.getSession().getAttribute("institution");
		CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
		if (user != null && user.getLevel() == 2) {
			ins = user.getInstitution();
			model.put("insInfo", ins == null ? null : ins);
		}
		condition.put("ip", IpUtil.getLongIp(IpUtil.getIp(request)));
		List<BIpRange> ipList = configureService.getIpRangeList(condition, "");
		if (ipList != null && ipList.size() > 0) {
			ins = ipList.get(0).getInstitution();
			model.put("insInfo", ins);
		}
		return new ModelAndView("mobile/ftl/Welcome.ftl", model);
	}

	@RequestMapping("index/search")
	public ModelAndView search(HttpServletRequest request, HttpServletResponse response, IndexForm form,String isJson) throws Exception {
		String forwardString = "mobile/search";
		Map<String, Object> model = new HashMap<String, Object>();
		CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
		BInstitution ins = (BInstitution) request.getSession().getAttribute("institution");

		String searchValue2 = request.getParameter("searchValue2");
		searchValue2 = (null == searchValue2 || "".equals(searchValue2)) ? request.getParameter("searchValue") : searchValue2;

		try {
			request.getSession().setAttribute("selectType", "");// selectType
																// 用来保存全局的变量，看是全部还是在已订阅中查询
																// ""-全部 1-已订阅
			if (null != ins || null != user) {
				if ("".equals(form.getLcense())) {
					request.getSession().setAttribute("selectType", "");
				} else {
					request.getSession().setAttribute("selectType", "1");
				}
			}
			String keyword = URLDecoder.decode((null == form.getSearchValue() || "".equals(form.getSearchValue()) ? searchValue2 : form.getSearchValue()), "UTF-8");
			keyword = CharUtil.toSimple(keyword);
			String lang = (String) request.getSession().getAttribute("lang");
			// if(keyword!=null&&!"".equals(keyword)){
			form.setUrl(request.getRequestURL().toString());
			// form.setTaxonomy((form.getTaxonomy()==null||"".equals(form.getTaxonomy()))?null:new
			// String(form.getTaxonomy().getBytes("ISO-8859-1"),"UTF-8"));
			Map<String, String> param = new HashMap<String, String>();
			/** 特殊机构处理 -- START */
			String specialInstitutionFlag = request.getSession().getAttribute("specialInstitutionFlag") != null ? (String) request.getSession().getAttribute("specialInstitutionFlag") : null;
			if (null != specialInstitutionFlag && specialInstitutionFlag.length() > 0) {
				form = this.specialInstitution_handle(form, specialInstitutionFlag);
			}
			/** 特殊机构处理 -- END */
			param.put("language", (form.getLanguage() == null || "".equals(form.getLanguage())) ? null : "\"" + form.getLanguage() + "\"");
			param.put("publisher", (form.getPublisher() == null || "".equals(form.getPublisher())) ? null : "\"" + form.getPublisher() + "\"");
			// param.put("publisher", (form.getPublisher() == null ||
			// "".equals(form.getPublisher())) ? null : form.getPublisher());
			param.put("type", (form.getPubType() == null || "".equals(form.getPubType())) ? null : form.getPubType());
			param.put("pubDate", (form.getPubDate() == null || "".equals(form.getPubDate())) ? null : form.getPubDate() + "*");

			param.put("taxonomy", (form.getTaxonomy() == null || "".equals(form.getTaxonomy())) ? null : "\"" + URLDecoder.decode(form.getTaxonomy(), "UTF-8") + "\"");

			// param.put("taxonomy", (form.getTaxonomy() == null ||
			// "".equals(form.getTaxonomy())) ? null : "\"" + form.getTaxonomy()
			// + "\"");
			// param.put("taxonomyEn", (form.getTaxonomyEn() == null ||
			// "".equals(form.getTaxonomyEn())) ? null : "\"" +
			// form.getTaxonomyEn() + "\"");
			param.put("nochinese", (form.getNochinese() == null || "".equals(form.getNochinese())) ? null : "\"" + form.getNochinese() + "\"");
			param.put("local", (form.getLocal() == null || "".equals(form.getLocal())) ? null : form.getLocal());
			param.put("pubType", (form.getPubType() == null || "".equals(form.getPubType())) ? null : form.getPubType());
			param.put("notLanguage", (null == form.getNotLanguage()) || "".equals(form.getNotLanguage()) ? null : form.getNotLanguage());

			// 在solr中查询 [搜索类型=====0-全文;1-标题;2-作者]
			Map<String, Object> resultMap = new HashMap<String, Object>();
			// form.setSearchsType(form.getSearchsType() == null ? 0 :
			// form.getSearchsType());
			// String keyword = form.getSearchValue();

			if (form.getIsAccurate() != null && form.getIsAccurate() == 2) {// 要查询的内容
																			// 是否精确查找
																			// 1、否
																			// ；2、是
				keyword = "\"" + keyword + "\"";
			}
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

			form.setSearchsType(null == form.getSearchsType() ? Integer.valueOf(null == request.getParameter("searchsType2") || "".equals(request.getParameter("searchsType2")) ? request.getParameter("type") : request.getParameter("searchsType2")) : (null == form.getSearchsType() || "".equals(form.getSearchsType()) ? Integer.valueOf(request.getParameter("type")) : form.getSearchsType()));

			if (keyword != null && !"".equals(keyword)) {
				form.setPageCount(20);
				switch (form.getSearchsType()) {
				case 0:
					resultMap = this.publicationsIndexService.searchByAllFullText(keyword, form.getCurpage(), form.getPageCount(), param, form.getSearchOrder() + "##" + form.getSortFlag());
					break;
				case 1:
					resultMap = this.publicationsIndexService.searchByTitle(keyword, form.getCurpage(), form.getPageCount(), param,  form.getSearchOrder() + "##" + form.getSortFlag());
					break;
				case 2:
					resultMap = this.publicationsIndexService.searchByAuthor(keyword, form.getCurpage(), form.getPageCount(), param,  form.getSearchOrder() + "##" + form.getSortFlag());
					break;
				case 3:
					resultMap = this.publicationsIndexService.searchByISBN(keyword, form.getCurpage(), form.getPageCount(), param,  form.getSearchOrder() + "##" + form.getSortFlag());
					break;
				case 4:
					resultMap = this.publicationsIndexService.searchByPublisher(keyword, form.getCurpage(), form.getPageCount(), param,  form.getSearchOrder() + "##" + form.getSortFlag());
					break;
				default:
					resultMap = this.publicationsIndexService.searchByAllFullText(keyword, form.getCurpage(), form.getPageCount(), param,  form.getSearchOrder() + "##" + form.getSortFlag());
					break;
				}
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
					/*
					 * //中文分类处理 if(fac.getName().equals("taxonomy")){
					 */

					// 中文分类处理
					if (fac.getName().equals("taxonomy")) {

						List<Count> counts = fac.getValues();
						if (counts != null && counts.size() > 0) {
							ComparatorSubject comparator = new ComparatorSubject();
							Collections.sort(counts, comparator);
							model.put("taxonomyList", counts);
						}
					}
					// 英文分类处理
					if (fac.getName().equals("taxonomyEn")) {
						List<Count> counts = fac.getValues();
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
					// 语种处理
					if (fac.getName().equals("language")) {
						List<Count> counts = fac.getValues();
						if (counts != null && counts.size() > 0) {
							ComparatorSubject comparator = new ComparatorSubject();
							Collections.sort(counts, comparator);
							model.put("languageList", counts);
						}
					}
					/* } */

					model.put("facetFields", facetFields);
					model.put("pubDateMap", SequenceUtil.MapDescToKey(pubDate));
					if (resultMap.get("count") != null && Long.valueOf(resultMap.get("count").toString()) > 0) {
						List<Map<String, Object>> list = (List<Map<String, Object>>) resultMap.get("result");
						List<PPublications> resultList = new ArrayList<PPublications>();
						for (Map<String, Object> idInfo : list) {
							// 根据ID查询产品信息
							// 由于加入了标签，这里不能用get查询
							Map<String, Object> condition = new HashMap<String, Object>();
							condition.put("id", idInfo.get("id"));
							List<PPublications> ppList = this.pPublicationsService.getPubList3(condition, " order by a.createOn ", (CUser) request.getSession().getAttribute("mainUser"), IpUtil.getIp(request));
							if (ppList != null && ppList.size() > 0) {
								PPublications pub = ppList.get(0);
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
								if (user != null) {
									Map<String, Object> con = new HashMap<String, Object>();
									con.put("publicationsid", idInfo.get("id"));
									con.put("status", 2);
									con.put("userTypeId", user.getUserType().getId() == null ? "" : user.getUserType().getId());
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
								// con2.put("publicationsId", id);
								// List<PCsRelation> csList =
								// this.bSubjectService.getSubPubList(con2,
								// " order by a.subject.code ");
								// // Set<PCsRelation> set = new
								// HashSet(Arrays.asList(csList));
								// // pub.setCsRelations(set);
								// pub.setCsList(csList);
								resultList.add(pub);
							}
						}
						model.put("pubDateMap", SequenceUtil.MapDescToKey(pubDate));
						// if(resultList!=null&&!resultList.isEmpty()){
						// String[] keywords = form.getSearchValue().split(" ");
						// HighLightHelper tool = new
						// HighLightHelper(form.getPrefixHTML(),form.getSuffixHTML());
						// for(PPublications publication : resultList){
						// publication.setAuthor(tool.getHighLightText(keywords,publication.getAuthor()));
						// publication.setTitle(tool.getHighLightText(keywords,publication.getTitle()));
						// publication.setRemark(tool.getHighLightText(keywords,publication.getRemark()));
						// }
						// }
						form.setCount(allCount);
						model.put("list", resultList);
						if("true".equals(isJson)){
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("list", resultList);
							map.put("curpage", form.getCurpage()+1);
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

			}
			boolean msg = (Boolean) (request.getAttribute("msg") == null ? true : request.getAttribute("msg"));
			if (msg) {
				// CUser user =
				// request.getSession().getAttribute("mainUser")==null?null:(CUser)request.getSession().getAttribute("mainUser");
				if (user != null) {
					if (form.getCount() > 0) {
						CSearchHis obj = new CSearchHis();
						obj.setCreateOn(new Date());
						obj.setKeyword(keyword);
						obj.setType(1);// 临时保存...下次登录的时候清空
						obj.setUser(user);
						obj.setKeyType(form.getSearchsType() == null ? 0 : form.getSearchsType());
						this.cUserService.addSearchHistory(obj);
					}
				}
			}
		} catch (Exception e) {
			if ("keywords can't be null".equals(e.getMessage())) {
				request.setAttribute("prompt", Lang.getLanguage("Controller.Index.search.prompt.error", request.getSession().getAttribute("lang").toString()));// 搜索错误提示
				request.setAttribute("message", Lang.getLanguage("Controller.Index.search.keywords.error", request.getSession().getAttribute("lang").toString()));

				forwardString = "mobile/frame/result";
			} else {
				request.setAttribute("prompt", Lang.getLanguage("Controller.Index.search.prompt.error", request.getSession().getAttribute("lang").toString()));// 搜索错误提示
				request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
				forwardString = "mobile/frame/result";
			}
		}
		if (null != searchValue2) {
			searchValue2 = searchValue2.replaceAll("\"", "");
		}
		form.setSearchValue(URLDecoder.decode(null == form.getSearchValue() || "".equals(form.getSearchValue()) ? searchValue2 : form.getSearchValue(), "UTF-8"));
		form.setSearchValue2(URLDecoder.decode(null == form.getSearchValue() || "".equals(form.getSearchValue()) ? searchValue2 : form.getSearchValue(), "UTF-8"));
		form.setSearchsType2(request.getParameter("searchsType2"));
		model.put("form", form);
		model.put("current", "search");
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
	 * 免费资源 JQuery异步加载
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "mobile/pages/index/freePub")
	public ModelAndView freePub(HttpServletRequest request, HttpServletResponse response, IndexForm form) throws Exception {
		String forwardString = "mobile/index/index/freePub";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			form.setUrl(request.getRequestURL().toString());
			String allfree = request.getParameter("allfree");
			String ins_Id = "";
			Integer num = 5;
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			List<PPublications> list = null;
			Map<String, Object> condition = new HashMap<String, Object>();

			condition.put("free", 2);// 免费状态 1-不免费 ；2-免费
			// condition.put("freeType", 4);//类型文章？
			if ("true".equals(allfree)) {
				form.setCount(this.pPublicationsService.getDatabaseCount(condition));
				list = this.pPublicationsService.getPubSimplePageList(condition, " order by a.createOn ", 20, form.getCurpage());// form.getPageCount()

				model.put("list", list);

				forwardString = "mobile/index/index/freePubList";
			} else {
				// Ip范围内
				if (request.getSession().getAttribute("institution") != null) {
					if (request.getSession().getAttribute("mainUser") != null) {
						if (user.getLevel() != 2) {// 不是图书馆管理员
							ins_Id = ((BInstitution) request.getSession().getAttribute("institution")).getId();
						}
					} else {
						ins_Id = ((BInstitution) request.getSession().getAttribute("institution")).getId();
					}
				}

				if ("".equals(ins_Id) && user == null) {
					condition.put("publisher", "null");
					list = this.pPublicationsService.getPubSimplePageList(condition, " order by a.createOn desc", num, 0);
					model.put("list", list);
				} else {
					if ("".equals(ins_Id) && user != null && user.getLevel() != 2) {
						list = this.pPublicationsService.getPubSimplePageList(condition, " order by a.createOn desc", num, 0);
						model.put("list", list);
					} else {

						condition.put("status", 1);// license有效
						if (user != null && user.getLevel() == 2) {
							ins_Id = user.getInstitution().getId();
						}
						condition.put("institutionId", ins_Id);
						condition.put("isTrail", 1);
						List<LLicense> list2 = this.customService.getLicensePagingListForIndex(condition, " order by a.createdon desc ", num, 0);
						if (list2 != null && list2.size() > 0) {
							model.put("list", list2);
							forwardString = "mobile/index/index/licenseList";
						} else {
							condition.remove("status");
							condition.remove("institutionId");
							condition.remove("isTrail");
							condition.put("publisher", "null");
							list = this.pPublicationsService.getPubSimplePageList(condition, " order by a.createOn desc", num, 0);
							model.put("list", list);
						}
					}
				}
			}
			model.put("form", form);
		} catch (Exception e) {
			e.printStackTrace();
			form.setMsg((e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		return new ModelAndView(forwardString, model);
	}

	
	/**
	 * 免费资源 JQuery异步加载
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "mobile/pages/index/freePubJson")
	public ModelAndView freePubJson(HttpServletRequest request, HttpServletResponse response, IndexForm form) throws Exception {
		String forwardString = "mobile/index/index/freePub";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			form.setUrl(request.getRequestURL().toString());
			Integer num = 20;
			List<PPublications> list = null;
			Map<String, Object> condition = new HashMap<String, Object>();

			condition.put("free", 2);// 免费状态 1-不免费 ；2-免费
			
			list = this.pPublicationsService.getPubSimplePageList(condition, " order by a.createOn ", num, form.getCurpage());// form.getPageCount()

			model.put("list", list);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("list", list);
			map.put("curpage",form.getCurpage()+1);
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

			forwardString = "mobile/index/index/freePubList";
			model.put("form", form);
		} catch (Exception e) {
			e.printStackTrace();
			form.setMsg((e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		return new ModelAndView(forwardString, model);
	}
	
	/**
	 * 首页最近阅读JQuery异步加载
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/mobile/pages/index/recentlyRead")
	public ModelAndView recentlyRead(HttpServletRequest request, HttpServletResponse response, IndexForm form) throws Exception {
		String forwardString = "/mobile/index/index/recentlyRead";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			form.setUrl(request.getRequestURL().toString());
			// 1、当用户登陆时查询
			// 2、在日志表中查询最近阅读的4本书
			Integer num = 5;
			if (request.getParameter("num") != null && !"".equals(request.getParameter("num").toString())) {
				num = Integer.valueOf(request.getParameter("num").toString());
			}
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			List<LAccess> list = null;
			if (user != null) {
				Map<String, Object> condition = new HashMap<String, Object>();
				condition.put("type", 2);// 操作类型: 1-访问摘要， 2-访问内容， 3-检索
				condition.put("pubStatus", 2);// 书籍状态：1-未上架， 2-已上架
				condition.put("userId", user.getId());
				condition.put("maxDate", "true");
				condition.put("license", "true");
				condition.put("available", 3);// 选用状态:1-不可用(中图未选用)
												// 2-可用（中图已经选用）3-政治原因 4-版权原因
												// 取前4个
				list = this.logAOPService.getLogOfRecentlyRead(condition, " order by a.createOn desc ", num, 0);
			}
			model.put("list", list);
			model.put("form", form);
		} catch (Exception e) {
			e.printStackTrace();
			form.setMsg((e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		return new ModelAndView(forwardString, model);
	}

	@RequestMapping(value = "/pages/suggest")
	public void suggest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String json = "{}";
		try {
			String q = request.getParameter("q");
			if (q != null && !"".equals(q)) {
				q = URLEncoder.encode(q, "utf-8");
				q = q.toLowerCase();
				json = this.publicationsIndexService.suggest(q);
			} else {

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setContentType("text/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
		out.close();
	}

	/**
	 * 在已订阅中查询
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/index/searchLicense")
	public ModelAndView searchLicense(HttpServletRequest request, HttpServletResponse response, IndexForm form,String isJson) throws Exception {
		String forwardString = "mobile/search";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			request.getSession().setAttribute("selectType", 1);// selectType
																// 用来保存全局的变量，看是全部还是在已订阅中查询
																// 2-全部 1-已订阅
			String searchValue2 = request.getParameter("searchValue2");
			searchValue2 = (null == searchValue2 || "".equals(searchValue2)) ? request.getParameter("searchValue") : searchValue2;
			searchValue2 = getValus(searchValue2);
			String keyword = URLDecoder.decode((null == form.getSearchValue() || "".equals(form.getSearchValue()) ? searchValue2 : form.getSearchValue()), "UTF-8");
			keyword = CharUtil.toSimple(keyword);
			CUser user1 = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			if (keyword != null && !"".equals(keyword)) {
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
						uc.put("institutionId", bIpRange.getInstitution().getId());
						uc.put("level", 2);
						List<CUser> lu = this.cUserService.getUserList(uc, "");
						for (CUser cUser : lu) {
							userIds.append(cUser.getId()).append(",");
						}
					}
				}
				// 查询用户ID
				if (request.getSession().getAttribute("mainUser") != null) {
					CUser user = (CUser) request.getSession().getAttribute("mainUser");
					userIds.append(user.getId()).append(",");
				}
				// userIds.append(Param.getParam("OAFree.uid.config").get("uid")).append(",");
				if ("".equals(userIds.toString())) {
					throw new CcsException("Controller.Index.searchLicense.noLogin");// 未登录用户，无法按照“已订阅”查询
				} else {
					String userId = userIds.substring(0, userIds.toString().lastIndexOf(","));
					form.setUrl(request.getRequestURL().toString());
					Map<String, String> param = new HashMap<String, String>();
					/** 特殊机构处理 -- START */
					String specialInstitutionFlag = request.getSession().getAttribute("specialInstitutionFlag") != null ? (String) request.getSession().getAttribute("specialInstitutionFlag") : null;
					if (null != specialInstitutionFlag && specialInstitutionFlag.length() > 0) {
						form = this.specialInstitution_handle(form, specialInstitutionFlag);
					}
					/** 特殊机构处理 -- END */
					param.put("language", (form.getLanguage() == null || "".equals(form.getLanguage())) ? null : "\"" + form.getLanguage() + "\"");
					param.put("publisher", (form.getPublisher() == null || "".equals(form.getPublisher())) ? null : "\"" + form.getPublisher() + "\"");
					param.put("type", (form.getPubType() == null || "".equals(form.getPubType())) ? null : form.getPubType());
					param.put("pubDate", (form.getPubDate() == null || "".equals(form.getPubDate())) ? null : form.getPubDate() + "*");
					param.put("taxonomy", (form.getTaxonomy() == null || "".equals(form.getTaxonomy())) ? null : "\"" + form.getTaxonomy() + "\"");
					param.put("taxonomyEn", (form.getTaxonomyEn() == null || "".equals(form.getTaxonomyEn())) ? null : "\"" + form.getTaxonomyEn() + "\"");
					param.put("nochinese", (form.getNochinese() == null || "".equals(form.getNochinese())) ? null : "\"" + form.getNochinese() + "\"");
					param.put("local", (form.getLocal() == null || "".equals(form.getLocal())) ? null : form.getLocal());
					param.put("notLanguage", (null == form.getNotLanguage()) || "".equals(form.getNotLanguage()) ? null : form.getNotLanguage());

					// 在solr中查询 [搜索类型=====0-全文;1-标题;2-作者]
					// String keyword = form.getSearchValue();

					if (form.getIsAccurate() != null && form.getIsAccurate() == 2) {// 要查询的内容
																					// 是否精确查找
																					// 1、否
																					// ；2、是
						keyword = "\"" + keyword + "\"";
					}
					Map<String, Object> resultMap = new HashMap<String, Object>();
					String searchsType2 = request.getParameter("searchsType2");
					if(searchsType2 == null ){
						searchsType2="";
					}
					searchsType2 = getValus(searchsType2);
					form.setSearchsType(form.getSearchsType() == null || "".equals(form.getSearchsType()) ? Integer.valueOf(searchsType2) : form.getSearchsType());
					switch (form.getSearchsType()) {
					case 0:
						resultMap = this.licenseIndexService.searchByAllFullText(keyword, userId, form.getCurpage(), 20, param,  form.getSearchOrder() + "##" + form.getSortFlag());
						break;
					case 1:
						resultMap = this.licenseIndexService.searchByTitle(keyword, userId, form.getCurpage(), 20, param,  form.getSearchOrder() + "##" + form.getSortFlag());
						break;
					case 2:
						resultMap = this.licenseIndexService.searchByAuthor(keyword, userId, form.getCurpage(), 20, param,  form.getSearchOrder() + "##" + form.getSortFlag());
						break;
					case 3:
						resultMap = this.licenseIndexService.searchByISBN(keyword, userId, form.getCurpage(), 20, param,  form.getSearchOrder() + "##" + form.getSortFlag());
						break;
					case 4:
						resultMap = this.licenseIndexService.searchByPublisher(keyword, userId, form.getCurpage(), 20, param,  form.getSearchOrder() + "##" + form.getSortFlag());
						break;
					default:
						resultMap = this.licenseIndexService.searchByAllFullText(keyword, userId, form.getCurpage(), 20, param,  form.getSearchOrder() + "##" + form.getSortFlag());
						break;
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
						// 中文分类处理
						if (fac.getName().equals("taxonomy")) {
							List<Count> counts = fac.getValues();
							if (counts != null && counts.size() > 0) {
								ComparatorSubject comparator = new ComparatorSubject();
								Collections.sort(counts, comparator);
								model.put("taxonomyList", counts);
							}
						}
						// 英文分类处理
						if (fac.getName().equals("taxonomyEn")) {
							List<Count> counts = fac.getValues();
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
						// 语种处理
						if (fac.getName().equals("language")) {

							List<Count> counts = fac.getValues();
							if (counts != null && counts.size() > 0) {
								ComparatorSubject comparator = new ComparatorSubject();
								Collections.sort(counts, comparator);
								model.put("languageList", counts);
							}
						}
					}
					model.put("facetFields", facetFields);
					model.put("pubDateMap", SequenceUtil.MapDescToKey(pubDate));
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
					if (resultMap.get("count") != null && Long.valueOf(resultMap.get("count").toString()) > 0) {
						List<Map<String, Object>> list = (List<Map<String, Object>>) resultMap.get("result");
						List<PPublications> resultList = new ArrayList<PPublications>();
						for (Map<String, Object> idInfo : list) {
							// 根据ID查询产品信息
							// 由于加入了标签，这里不能用get查询
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
								condition.put("check", "false");
								List<PPublications> ppList = this.pPublicationsService.getPubList3(condition, " order by a.createOn ", (CUser) request.getSession().getAttribute("mainUser"), IpUtil.getIp(request));
								if (ppList != null && ppList.size() > 0) {
									PPublications pub = ppList.get(0);
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
									// con2.put("publicationsId",
									// lli.getPublications().getId());
									// List<PCsRelation> csList =
									// this.bSubjectService.getSubPubList(con2,
									// " order by a.subject.code ");
									// // Set<PCsRelation> set = new
									// HashSet(Arrays.asList(csList));
									// // pub.setCsRelations(set);
									// pub.setCsList(csList);
									resultList.add(pub);
								}
							}
						}
						model.put("pubDateMap", SequenceUtil.MapDescToKey(pubDate));
						form.setCount(allCount);
						model.put("list", resultList);
						if("true".equals(isJson)){
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("list", resultList);
							map.put("curpage", form.getCurpage()+1);
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
					// ----------------------进行高亮--------------
					// form.setKeyMap(this.highLight(form.getSearchsType(),
					// keyword));
					// ----------------------高亮结束--------------
					// 如果查询出了结果，那么要保存搜索关键字
					boolean msg = (Boolean) (request.getAttribute("msg") == null ? true : request.getAttribute("msg"));
					if (msg) {
						CUser cuser = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
						if (cuser != null) {
							if (form.getCount() > 0) {
								CSearchHis obj = new CSearchHis();
								obj.setCreateOn(new Date());
								obj.setKeyword(keyword);
								obj.setType(1);// 临时保存...下次登录的时候清空
								obj.setUser(cuser);
								obj.setKeyType(form.getSearchsType() == null ? 0 : form.getSearchsType());
								this.cUserService.addSearchHistory(obj);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
			if ("keywords can't be null".equals(e.getMessage())) {
				request.setAttribute("prompt", Lang.getLanguage("Controller.Index.search.prompt.error", request.getSession().getAttribute("lang").toString()));// 搜索错误提示
				request.setAttribute("message", Lang.getLanguage("Controller.Index.search.keywords.error", request.getSession().getAttribute("lang").toString()));

				forwardString = "mobile/frame/result";
			} else {
				request.setAttribute("prompt", Lang.getLanguage("Controller.Index.search.prompt.error", request.getSession().getAttribute("lang").toString()));// 搜索错误提示
				request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
				forwardString = "mobile/frame/result";
			}
		}
		model.put("form", form);
		model.put("current", "searchLicense");
		return new ModelAndView(forwardString, model);
	}

	@RequestMapping(value = "mobile/pages/index/trialList")
	public ModelAndView trialList(HttpServletRequest request, HttpServletResponse response, IndexForm form) throws Exception {
		String forwardString = "mobile/index/index/trialList";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			form.setUrl(request.getRequestURL().toString());
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			Map<String, Object> condition = new HashMap<String, Object>();
			String ins_Id = "";
			if (request.getSession().getAttribute("institution") != null) {
				if (request.getSession().getAttribute("mainUser") != null) {
					user = (CUser) request.getSession().getAttribute("mainUser");
					if (user.getLevel() == 2) {// 不是图书馆管理员
						ins_Id = user.getInstitution().getId();
					}
				} else {
					ins_Id = ((BInstitution) request.getSession().getAttribute("institution")).getId();
				}
			} else {
				if (request.getSession().getAttribute("mainUser") != null) {
					user = (CUser) request.getSession().getAttribute("mainUser");
					if (user.getLevel() == 2) {// 不是图书馆管理员
						ins_Id = user.getInstitution().getId();
					}
				}
			}

			condition.put("status", 1);// license有效
			condition.put("institutionId", ins_Id);
			condition.put("myisTrial", 1);
			if (request.getParameter("freelang") != null) {
				condition.put("freelang", request.getParameter("freelang").toLowerCase());// 语种
			}
			condition.put("trialType", request.getParameter("type"));// 类型
			condition.put("trypublisherName", request.getParameter("publisher"));// 出版社
			condition.put("pDate", request.getParameter("pDate"));// 出版日期
			Integer[] tt = { 1, 2, 4 };

			condition.put("typeArr", tt);
			List<PPublications> list = this.pPublicationsService.getTrialList(condition, " order by d.createOn ", 20, form.getCurpage()); // form.getPageCount()

			model.put("list", list);
			model.put("form", form);
		} catch (Exception e) {
			e.printStackTrace();
			forwardString = "mobile/error";
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}

		return new ModelAndView(forwardString, model);
	}
	
	
	@RequestMapping(value = "mobile/pages/index/trialListJson")
	public ModelAndView trialListJson(HttpServletRequest request, HttpServletResponse response, IndexForm form) throws Exception {
		String forwardString = "mobile/index/index/trialList";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			form.setUrl(request.getRequestURL().toString());
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			Map<String, Object> condition = new HashMap<String, Object>();
			String ins_Id = "";
			if (request.getSession().getAttribute("institution") != null) {
				if (request.getSession().getAttribute("mainUser") != null) {
					user = (CUser) request.getSession().getAttribute("mainUser");
					if (user.getLevel() == 2) {// 不是图书馆管理员
						ins_Id = user.getInstitution().getId();
					}
				} else {
					ins_Id = ((BInstitution) request.getSession().getAttribute("institution")).getId();
				}
			} else {
				if (request.getSession().getAttribute("mainUser") != null) {
					user = (CUser) request.getSession().getAttribute("mainUser");
					if (user.getLevel() == 2) {// 不是图书馆管理员
						ins_Id = user.getInstitution().getId();
					}
				}
			}

			condition.put("status", 1);// license有效
			condition.put("institutionId", ins_Id);
			condition.put("myisTrial", 1);
			if (request.getParameter("freelang") != null) {
				condition.put("freelang", request.getParameter("freelang").toLowerCase());// 语种
			}
			condition.put("trialType", request.getParameter("type"));// 类型
			condition.put("trypublisherName", request.getParameter("publisher"));// 出版社
			condition.put("pDate", request.getParameter("pDate"));// 出版日期
			Integer[] tt = { 1, 2, 4 };

			condition.put("typeArr", tt);
			form.setCount(this.pPublicationsService.getTrialCount(condition));
			List<PPublications> list = this.pPublicationsService.getTrialList(condition, " order by d.createOn ", 20, form.getCurpage()); // form.getPageCount()
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("list", list);
			map.put("curpage", form.getCurpage()+1);
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

			
			model.put("form", form);
		} catch (Exception e) {
			e.printStackTrace();
			forwardString = "mobile/error";
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}

		return new ModelAndView(forwardString, model);
	}
	
	/**
	 * 高级搜索
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/mobile/index/advancedSearchSubmit")
	public ModelAndView advancedSearchSubmit(HttpServletRequest request, HttpServletResponse response, IndexForm form,String isJson) throws Exception {
		String forwardString = "mobile/index/index/lastPubList";
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
		// BInstitution ins = (BInstitution)
		// request.getSession().getAttribute("institution");
		// if (null != user1 || null != ins) {
		// form.setLcense("1");
		// }
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
			param.put("searchValue", (form.getSearchValue() == null || "".equals(form.getSearchValue())) ? null : URLDecoder.decode(form.getSearchValue(), "UTF-8"));
			param.put("keywordCondition", (form.getKeywordCondition() == null || "".equals(form.getKeywordCondition())) ? null : form.getKeywordCondition().toString());
			param.put("notKeywords", (form.getNotKeywords() == null || "".equals(form.getNotKeywords())) ? null : form.getNotKeywords());
			param.put("title", (form.getTitle() == null || "".equals(form.getTitle())) ? null : form.getTitle());
			param.put("author", (form.getAuthor() == null || "".equals(form.getAuthor())) ? null : form.getAuthor());
			param.put("code", (form.getCode() == null || "".equals(form.getCode())) ? null : form.getCode());
			param.put("taxonomy", (form.getTaxonomy() == null || "".equals(form.getTaxonomy())) ? null : "\"" + form.getTaxonomy() + "\"");
			param.put("taxonomyEn", (form.getTaxonomyEn() == null || "".equals(form.getTaxonomyEn())) ? null : "\"" + form.getTaxonomyEn() + "\"");
			param.put("pubType", (form.getPubType() == null || "".equals(form.getPubType())) ? null : form.getPubType());
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
				request.getSession().setAttribute("selectType", "");// selectType
																	// 用来保存全局的变量，看是全部还是在已订阅中查询
																	// 2-全部
																	// 1-已订阅、

				resultMap = this.publicationsIndexService.advancedSearch(form.getCurpage(),20, param, "", isCn);
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
						List<PPublications> ppList = this.pPublicationsService.getPubList3(condition, " order by  a.createOn ", (CUser) request.getSession().getAttribute("mainUser"), IpUtil.getIp(request));
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
					if("true".equals(isJson)){
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("list", resultList);
						map.put("curpage",form.getCurpage()+1);
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
			forwardString = "mobile/frame/result";
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
	 * 在免费OA中查询
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/index/searchOaFree")
	public ModelAndView searchOaFree(HttpServletRequest request, HttpServletResponse response, IndexForm form,String isJson) throws Exception {
		String forwardString = "mobile/search";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			request.getSession().setAttribute("selectType", 2);// selectType
																// 用来保存全局的变量，看是全部还是在已订阅中查询
																// 2-免费Oa 1-已订阅
			String searchValue2 = request.getParameter("searchValue2");
			searchValue2 = getValus(searchValue2);
			String keyword = URLDecoder.decode((null == form.getSearchValue() || "".equals(form.getSearchValue()) ? searchValue2 : form.getSearchValue()), "UTF-8");
			keyword = CharUtil.toSimple(keyword);
			CUser user1 = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			if (keyword != null && !"".equals(keyword)) {
				StringBuffer userIds = new StringBuffer();
				// 访问IP
				long ip = IpUtil.getLongIp(IpUtil.getIp(request));
				// 查询机构信息
				Map<String, Object> mapip = new HashMap<String, Object>();
				mapip.put("ip", ip);
				List<BIpRange> lip = this.configureService.getIpRangeList(mapip, "");
				/*
				 * if(lip!=null&&lip.size()>0){ //根据机构ID,查询用户 for (BIpRange
				 * bIpRange : lip) { Map<String,Object> uc = new
				 * HashMap<String,Object>();
				 * uc.put("institutionId",bIpRange.getInstitution().getId() );
				 * uc.put("level", 2); List<CUser> lu =
				 * this.cUserService.getUserList(uc, ""); for (CUser cUser : lu)
				 * { userIds.append(cUser.getId()).append(","); } } }
				 */
				// 查询用户ID 免费的不加 用户id 只加 userIds= oafree
				/*
				 * if(request.getSession().getAttribute("mainUser")!=null){
				 * CUser user =
				 * (CUser)request.getSession().getAttribute("mainUser");
				 * userIds.append(user.getId()).append(","); }
				 */
				userIds.append(Param.getParam("OAFree.uid.config").get("uid")).append(",");
				if ("".equals(userIds.toString())) {
					throw new CcsException("Controller.Index.searchLicense.noLogin");// 未登录用户，无法按照“已订阅”查询
				} else {
					String userId = userIds.substring(0, userIds.toString().lastIndexOf(","));
					form.setUrl(request.getRequestURL().toString());
					Map<String, String> param = new HashMap<String, String>();
					/** 特殊机构处理 -- START */
					String specialInstitutionFlag = request.getSession().getAttribute("specialInstitutionFlag") != null ? (String) request.getSession().getAttribute("specialInstitutionFlag") : null;
					if (null != specialInstitutionFlag && specialInstitutionFlag.length() > 0) {
						form = this.specialInstitution_handle(form, specialInstitutionFlag);
					}
					/** 特殊机构处理 -- END */
					param.put("language", (form.getLanguage() == null || "".equals(form.getLanguage())) ? null : "\"" + form.getLanguage() + "\"");
					param.put("publisher", (form.getPublisher() == null || "".equals(form.getPublisher())) ? null : "\"" + form.getPublisher() + "\"");
					param.put("type", (form.getPubType() == null || "".equals(form.getPubType())) ? null : form.getPubType());
					// param.put("year", (form.getPubDate() == null ||
					// "".equals(form.getPubDate())) ? null : form.getPubDate()
					// + "*");
					param.put("pubDate", (form.getPubDate() == null || "".equals(form.getPubDate())) ? null : form.getPubDate() + "*");
					param.put("taxonomy", (form.getTaxonomy() == null || "".equals(form.getTaxonomy())) ? null : "\"" + form.getTaxonomy() + "\"");
					param.put("taxonomyEn", (form.getTaxonomyEn() == null || "".equals(form.getTaxonomyEn())) ? null : "\"" + form.getTaxonomyEn() + "\"");
					param.put("nochinese", (form.getNochinese() == null || "".equals(form.getNochinese())) ? null : "\"" + form.getNochinese() + "\"");
					param.put("local", (form.getLocal() == null || "".equals(form.getLocal())) ? null : form.getLocal());
					param.put("notLanguage", (null == form.getNotLanguage()) || "".equals(form.getNotLanguage()) ? null : form.getNotLanguage());
					// 在solr中查询 [搜索类型=====0-全文;1-标题;2-作者]
					// String keyword = form.getSearchValue();

					if (form.getIsAccurate() != null && form.getIsAccurate() == 2) {// 要查询的内容
																					// 是否精确查找
																					// 1、否
																					// ；2、是
						keyword = "\"" + keyword + "\"";
					}
					Map<String, Object> resultMap = new HashMap<String, Object>();
					String searchsType2 = request.getParameter("searchsType2");
					searchsType2 = getValus(searchsType2);
					form.setSearchsType(form.getSearchsType() == null || "".equals(form.getSearchsType()) ? Integer.valueOf(searchsType2) : form.getSearchsType());
					switch (form.getSearchsType()) {
					case 0:
						resultMap = this.licenseIndexService.searchByAllFullText(keyword, userId, form.getCurpage(), form.getPageCount(), param,  form.getSearchOrder() + "##" + form.getSortFlag());
						break;
					case 1:
						resultMap = this.licenseIndexService.searchByTitle(keyword, userId, form.getCurpage(), form.getPageCount(), param,  form.getSearchOrder() + "##" + form.getSortFlag());
						break;
					case 2:
						resultMap = this.licenseIndexService.searchByAuthor(keyword, userId, form.getCurpage(), form.getPageCount(), param,  form.getSearchOrder() + "##" + form.getSortFlag());
						break;
					case 3:
						resultMap = this.licenseIndexService.searchByISBN(keyword, userId, form.getCurpage(), form.getPageCount(), param,  form.getSearchOrder() + "##" + form.getSortFlag());
						break;
					case 4:
						resultMap = this.licenseIndexService.searchByPublisher(keyword, userId, form.getCurpage(), form.getPageCount(), param,  form.getSearchOrder() + "##" + form.getSortFlag());
						break;
					default:
						resultMap = this.licenseIndexService.searchByAllFullText(keyword, userId, form.getCurpage(), form.getPageCount(), param,  form.getSearchOrder() + "##" + form.getSortFlag());
						break;
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
						/*
						 * if(fac.getName().equals("pubDate")){ List<Count>
						 * counts = fac.getValues(); for (Count count : counts)
						 * { if(count==null || count.getName()==null ||
						 * count.getName().length()<4){ continue; } int num =
						 * pubDate
						 * .get(count.getName().substring(0,4))==null?0:Integer
						 * .valueOf
						 * (pubDate.get(count.getName().substring(0,4)));
						 * if(count.getCount()>0){
						 * pubDate.put(count.getName().substring
						 * (0,4).toString(), (num+(int)count.getCount())); } } }
						 */
						// 类型
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
						// 中文分类处理
						if (fac.getName().equals("taxonomy")) {
							List<Count> counts = fac.getValues();
							if (counts != null && counts.size() > 0) {
								ComparatorSubject comparator = new ComparatorSubject();
								Collections.sort(counts, comparator);
								model.put("taxonomyList", counts);
							}
						}
						// 英文分类处理
						if (fac.getName().equals("taxonomyEn")) {
							List<Count> counts = fac.getValues();
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
						// 语种处理
						if (fac.getName().equals("language")) {
							List<Count> counts = fac.getValues();
							if (counts != null && counts.size() > 0) {
								ComparatorSubject comparator = new ComparatorSubject();
								Collections.sort(counts, comparator);
								model.put("languageList", counts);
							}
						}
					}
					model.put("facetFields", facetFields);
					model.put("pubDateMap", SequenceUtil.MapDescToKey(pubDate));
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
					if (resultMap.get("count") != null && Long.valueOf(resultMap.get("count").toString()) > 0) {
						List<Map<String, Object>> list = (List<Map<String, Object>>) resultMap.get("result");
						List<PPublications> resultList = new ArrayList<PPublications>();
						for (Map<String, Object> idInfo : list) {
							// 根据ID查询产品信息
							// 由于加入了标签，这里不能用get查询
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
								// condition.put("check", false);
								condition.put("status", null);
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
									// con2.put("publicationsId",
									// lli.getPublications().getId());
									// List<PCsRelation> csList =
									// this.bSubjectService.getSubPubList(con2,
									// " order by a.subject.code ");
									// // Set<PCsRelation> set = new
									// HashSet(Arrays.asList(csList));
									// // pub.setCsRelations(set);
									// pub.setCsList(csList);
									resultList.add(pub);
								}
							}
						}
						model.put("pubDateMap", SequenceUtil.MapDescToKey(pubDate));
						form.setCount(allCount);
						model.put("list", resultList);
						if("true".equals(isJson)){
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("list", resultList);
							map.put("curpage", form.getCurpage()+1);
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

					// ----------------------进行高亮--------------
					// form.setKeyMap(this.highLight(form.getSearchsType(),
					// keyword));
					// ----------------------高亮结束--------------
					// 如果查询出了结果，那么要保存搜索关键字
					boolean msg = (Boolean) (request.getAttribute("msg") == null ? true : request.getAttribute("msg"));
					if (msg) {
						CUser cuser = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
						if (cuser != null) {
							if (form.getCount() > 0) {
								CSearchHis obj = new CSearchHis();
								obj.setCreateOn(new Date());
								obj.setKeyword(keyword);
								obj.setType(1);// 临时保存...下次登录的时候清空
								obj.setUser(cuser);
								obj.setKeyType(form.getSearchsType() == null ? 0 : form.getSearchsType());
								this.cUserService.addSearchHistory(obj);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			if ("keywords can't be null".equals(e.getMessage())) {
				request.setAttribute("prompt", Lang.getLanguage("Controller.Index.search.prompt.error", request.getSession().getAttribute("lang").toString()));// 搜索错误提示
				request.setAttribute("message", Lang.getLanguage("Controller.Index.search.keywords.error", request.getSession().getAttribute("lang").toString()));

				forwardString = "mobile/frame/result";
			} else {
				request.setAttribute("prompt", Lang.getLanguage("Controller.Index.search.prompt.error", request.getSession().getAttribute("lang").toString()));// 搜索错误提示
				request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
				forwardString = "mobile/frame/result";
			}
		}
		model.put("form", form);
		model.put("current", "searchOaFree");
		return new ModelAndView(forwardString, model);
	}

	
	/**
	 * 处理重复的字符串工具
	 * 
	 * @param valus
	 * @return
	 */
	public static String getValus(String valus) {
		String value = "";
		String[] str = valus.split(",");
		Set set = new TreeSet();
		for (int i = 0; i < str.length; i++) {
			set.add(str[i]);
		}
		str = (String[]) set.toArray(new String[0]);
		for (int i = 0; i < str.length; i++) {
			value = str[i];
		}
		return value;
	}
}
