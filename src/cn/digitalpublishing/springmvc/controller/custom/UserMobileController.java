package cn.digitalpublishing.springmvc.controller.custom;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.orm.hibernate3.HibernateJdbcException;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import cn.ccsit.restful.tool.Converter;
import cn.com.daxtech.framework.Internationalization.Lang;
import cn.com.daxtech.framework.exception.CcsException;
import cn.com.daxtech.framework.model.Param;
import cn.com.daxtech.framework.model.ResultObject;
import cn.com.daxtech.framework.util.ObjectUtil;
import cn.digitalpublishing.ep.po.BInstitution;
import cn.digitalpublishing.ep.po.BSubject;
import cn.digitalpublishing.ep.po.BToken;
import cn.digitalpublishing.ep.po.BUrl;
import cn.digitalpublishing.ep.po.BUuRelation;
import cn.digitalpublishing.ep.po.CAccount;
import cn.digitalpublishing.ep.po.CDirectory;
import cn.digitalpublishing.ep.po.CFavourites;
import cn.digitalpublishing.ep.po.CSearchHis;
import cn.digitalpublishing.ep.po.CUser;
import cn.digitalpublishing.ep.po.CUserAlerts;
import cn.digitalpublishing.ep.po.CUserProp;
import cn.digitalpublishing.ep.po.CUserType;
import cn.digitalpublishing.ep.po.CUserTypeProp;
import cn.digitalpublishing.ep.po.LAccess;
import cn.digitalpublishing.ep.po.LLicense;
import cn.digitalpublishing.ep.po.OOrder;
import cn.digitalpublishing.ep.po.OOrderDetail;
import cn.digitalpublishing.ep.po.OTransation;
import cn.digitalpublishing.ep.po.PPrice;
import cn.digitalpublishing.ep.po.PPriceType;
import cn.digitalpublishing.ep.po.PPublications;
import cn.digitalpublishing.ep.po.RRecommend;
import cn.digitalpublishing.ep.po.RRecommendDetail;
import cn.digitalpublishing.ep.po.SSupplier;
import cn.digitalpublishing.ep.po.UPRelation;
import cn.digitalpublishing.ep.po.UPayment;
import cn.digitalpublishing.springmvc.controller.BaseController;
import cn.digitalpublishing.springmvc.controller.product.StatisticsBookSuppliersController;
import cn.digitalpublishing.springmvc.form.CAccountForm;
import cn.digitalpublishing.springmvc.form.custom.LAccessForm;
import cn.digitalpublishing.springmvc.form.custom.LLicenseForm;
import cn.digitalpublishing.springmvc.form.custom.OrgUserForm;
import cn.digitalpublishing.springmvc.form.custom.RRecommendDetailForm;
import cn.digitalpublishing.springmvc.form.custom.RRecommendForm;
import cn.digitalpublishing.springmvc.form.custom.SSupplierForm;
import cn.digitalpublishing.springmvc.form.custom.UAccessForm;
import cn.digitalpublishing.springmvc.form.custom.UserForm;
import cn.digitalpublishing.springmvc.form.custom.UserTypeForm;
import cn.digitalpublishing.springmvc.form.custom.UserTypePropForm;
import cn.digitalpublishing.springmvc.form.order.OTransationForm;
import cn.digitalpublishing.util.web.DateUtil;
import cn.digitalpublishing.util.web.MathHelper;
import cn.digitalpublishing.util.web.RandomCodeUtil;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("mobile/pages/user")
public class UserMobileController extends BaseController {
	/**
	 * 跳转到个人用户注册页面(Mobile)
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/register")
	public ModelAndView register(HttpServletRequest request, HttpServletResponse response, UserForm form) throws Exception {
		String forwardString = "mobile/user/registration";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			form.setUrl(request.getRequestURL().toString());
			Map<String, Object> condition = form.getCondition();
			// 查询“个人”类型属性数据
			condition.put("userTypeCode", "1");
			condition.put("status", 1);
			List<CUserTypeProp> list = this.customService.getUserTypePropList(condition, " order by a.must desc,a.order ");
			model.put("list", list);
			form.setCountryMap(Param.getParam("country.value", true, request.getSession().getAttribute("lang").toString()));
			form.setIdentityMap(Param.getParam("identity.value", true, request.getSession().getAttribute("lang").toString()));
			model.put("form", form);
			if (form.getPropsValue() != null) {
				form.setValues(new HashMap<String, String>());
				if (list != null && !list.isEmpty()) {
					for (int i = 0; i < list.size(); i++) {
						form.getValues().put(list.get(i).getCode(), form.getPropsValue()[i]);
					}
				}
			} else {
				form.setValues(new HashMap<String, String>());
				if (list != null && !list.isEmpty()) {
					for (int i = 0; i < list.size(); i++) {
						if ("country".equals(list.get(i).getCode())) {
							form.getValues().put(list.get(i).getCode(), "1");
						}
					}
				}
			}
		} catch (Exception e) {
			request.setAttribute("prompt", Lang.getLanguage("Controller.User.register.prompt.error", request.getSession().getAttribute("lang").toString()));// "注册失败提示");
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 登录页面(mobile)
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/login/page")
	public ModelAndView loginPage(HttpServletRequest request, HttpServletResponse response, UserForm form) throws Exception {
		String forwardString = "mobile/user/login";
		Map<String, Object> model = new HashMap<String, Object>();
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 用户中心页面 （mobile）
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/usercenter")
	public ModelAndView userCenter(HttpServletRequest request, HttpServletResponse response, UserForm form) throws Exception {
		String forwardString = "mobile/user/usercenter";
		Map<String, Object> model = new HashMap<String, Object>();
		CUser user = null == request.getSession().getAttribute("mainUser") ? null : (CUser) request.getSession().getAttribute("mainUser");
		// 用户不存在跳转到登录页面
		if (null == user) {
			forwardString = "redirect:/login";
		} else {
			form.setUrl(request.getRequestURL().toString());
			// 个人中心最近阅读查询
			// 1、当用户登陆时查询
			// 2、在日志表中查询最近阅读的5本书
			Integer num = 5;
			if (request.getParameter("num") != null && !"".equals(request.getParameter("num").toString())) {
				num = Integer.valueOf(request.getParameter("num").toString());
			}
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("type", 2);// 操作类型: 1-访问摘要， 2-访问内容， 3-检索
			condition.put("pubStatus", 2);// 书籍状态：1-未上架， 2-已上架
			condition.put("userId", user.getId());
			condition.put("maxDate", "true");
			condition.put("license", "true");
			condition.put("available", 3);// 选用状态:1-不可用(中图未选用)
											// 2-可用（中图已经选用）3-政治原因 4-版权原因
			List<LAccess> list = this.logAOPService.getLogOfRecentlyRead(condition, " order by a.createOn desc ", num, 0);
			model.put("list", list);
			// 个人中心已购资源
			Map<String, Object> llCondition = new HashMap<String, Object>();
			llCondition.put("status", 1);
			llCondition.put("ppStatus", 2);
			llCondition.put("UID", user.getId());
			List<PPublications> pubList = this.pPublicationsService.getSubPagingList(llCondition, " order by a.createdon desc ", 5, 0);
			model.put("pubList", pubList);

			// 个人中心订单查询
			Map<String, Object> orderCondition = new HashMap<String, Object>();
			orderCondition.put("userId", user.getId());
			orderCondition.put("startTime", "");
			orderCondition.put("endTime", "");
			List<OOrder> olist = this.oOrderService.getOrderPagingList(orderCondition, " order by a.createdon desc ", 5, 0);
			model.put("olist", olist);

			// 个人中心我的收藏查询
			Map<String, Object> favcondition = new HashMap<String, Object>();
			favcondition.put("available", 3);
			favcondition.put("userId", user.getId());
			List<CFavourites> flist = this.cUserService.getFavoutitesPagingList(favcondition, "order by a.createDate desc", 5, 0);
			model.put("flist", flist);

			model.put("form", form);
		}
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 已购资源
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/purchasedResource")
	public ModelAndView purchasedResource(HttpServletRequest request, HttpServletResponse response, UserForm form) throws Exception {
		String forwardString = "mobile/user/purchasedresource/list";
		Map<String, Object> model = new HashMap<String, Object>();
		CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
		if (user == null) {
			forwardString = "redirect:/mobile/pages/user/form/login/page";
		} else {
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("status", 1);
			condition.put("ppStatus", 2);
			condition.put("UID", user.getId());
			List<PPublications> pubList = this.pPublicationsService.getSubPagingList(condition, " order by a.createdon desc ", 20, 0);

			model.put("list", pubList);

			model.put("form", form);
		}
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 已购资源JSON
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/purchasedResourceJson")
	public ModelAndView purchasedResourceJson(HttpServletRequest request, HttpServletResponse response, UserForm form) throws Exception {
		String forwardString = "mobile/user/purchasedresource/list";
		Map<String, Object> model = new HashMap<String, Object>();
		CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
		if (user == null) {
			forwardString = "redirect:/mobile/pages/user/form/login/page";
		} else {
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("status", 1);
			condition.put("ppStatus", 2);
			condition.put("UID", user.getId());
			List<PPublications> pubList = this.pPublicationsService.getSubPagingList(condition, " order by a.createdon desc ", 20, form.getCurpage());

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("list", pubList);
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

			model.put("list", pubList);

			model.put("form", form);
		}
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 个人用户注册请求提交(mobile)
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/registerSubmit")
	public ModelAndView registerSubmit(HttpServletRequest request, HttpServletResponse response, UserForm form) throws Exception {
		String forwardString = "mobile/frame/result";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			boolean msg = (Boolean) (request.getAttribute("msg") == null ? true : request.getAttribute("msg"));
			if (msg) {
				form.setUrl(request.getRequestURL().toString());
				// 验证账号Uid的唯一性
				boolean uidUnique = this.customService.checkUidExist(null, form.getAccount().getUid());
				if (uidUnique) {
					form.setMsg(Lang.getLanguage("Controller.User.registerSubmit.msg.already.name", request.getSession().getAttribute("lang").toString()));// "已经存在该用户名，请重新输入！");
					return register(request, response, form);
				}
				// 验证邮箱的唯一性
				String emailValue = "";
				for (int i = 0; i < form.getTypePropIds().length; i++) {
					// 根据页面传的类型属性id获取类型属性详细信息，并存储到用户属性中。
					CUserTypeProp typeProp = this.customService.getUserTypePropById(form.getTypePropIds()[i].toString());
					if (typeProp != null && typeProp.getCode().equals("email")) {
						emailValue = form.getPropsValue()[i].toString();
						break;
					}
				}
				if (this.customService.checkEmailExist(null, emailValue)) {
					form.setMsg(Lang.getLanguage("Controller.User.registerSubmit.msg.already.email", request.getSession().getAttribute("lang").toString()));// "已经存在该邮箱，请重新输入！");
					return register(request, response, form);
				}

				// 新增用户信息
				CUser user = this.insertUser(form.getUserName(), form.getUserType());
				// 新增用户属性信息
				this.insertUserProp(form.getTypePropIds(), form.getPropsValue(), user);
				// 新增账户信息
				CAccount account = this.insertAccount(form.getAccount().getUid(), form.getAccount().getPwd(), user);
				// 给注册用户发送确认邮件
				boolean isSuccess = this.sendEmail(emailValue, user.getName(), account.getId(), null, Param.getParam("mail.template.register", true).get("register"), request.getSession().getAttribute("lang").toString());
				if (isSuccess) {
					request.setAttribute("prompt", Lang.getLanguage("Controller.User.registerSubmit.prompt.success", request.getSession().getAttribute("lang").toString()));// "注册成功提示");
					request.setAttribute("message", Lang.getLanguage("Controller.User.registerSubmit.email.success", request.getSession().getAttribute("lang").toString()));// "注册成功，请查收账号激活邮件！");
				} else {
					request.setAttribute("prompt", Lang.getLanguage("Controller.User.registerSubmit.prompt.success", request.getSession().getAttribute("lang").toString()));// "注册成功提示");
					request.setAttribute("message", Lang.getLanguage("Controller.User.registerSubmit.email.error", request.getSession().getAttribute("lang").toString()));// "注册成功，但邮件发送失败，请联系管理员！");
				}
			} else {
				request.setAttribute("prompt", Lang.getLanguage("Controller.User.registerSubmit.prompt.error", request.getSession().getAttribute("lang").toString()));// "注册失败提示");
				request.setAttribute("message", Lang.getLanguage("Conteoller.Global.prompt.info", request.getSession().getAttribute("lang").toString()));// "注册失败");
			}
		} catch (Exception e) {
			// request.setAttribute("prompt",
			// Lang.getLanguage("Controller.User.registerSubmit.prompt.error",
			// request.getSession().getAttribute("lang").toString()));//"注册失败提示");
			// request.setAttribute("message",(e instanceof
			// CcsException)?Lang.getLanguage(((CcsException)e).getPrompt(),request.getSession().getAttribute("lang").toString()):e.getMessage());
			if (e instanceof HibernateJdbcException) {

				SQLException ex = ((HibernateJdbcException) e).getSQLException();
				if (ex != null) {
					if (ex.getMessage().indexOf("value too large") > 0) {
						form.setMsg(Lang.getLanguage("Global.Prompt.String.Too.Long", request.getSession().getAttribute("lang").toString()));
						return register(request, response, form);
					}
				}
			}
			form.setMsg(Lang.getLanguage("Controller.User.registerSubmit.Err", request.getSession().getAttribute("lang").toString()));
			return register(request, response, form);
		}
		model.put("form", form);
		return new ModelAndView(forwardString, model);
	}

	@RequestMapping(value = "/form/login")
	public void login(HttpServletRequest request, HttpServletResponse response, HttpSession session, CAccountForm form) throws Exception {
		String result;
		try {
			String path = form.getCondition().get("beforPath").toString();
			String ctx = request.getContextPath();
			String domain = request.getServerName();
			form.setUrl(request.getRequestURL().toString());
			if (form.getCondition().get("uid") != null && !"".equals(form.getCondition().get("uid")) && form.getCondition().get("pwd") != null && !"".equals(form.getCondition().get("pwd"))) {
				// CAccount
				// account=this.customService.getAccount(form.getCondition());
				Boolean issucc = false;
				Map<String, Object> condition = new HashMap<String, Object>();
				condition.put("loginid", form.getCondition().get("uid"));
				CAccount account = this.customService.getAccount(condition);
				if (account != null) {
					Md5PasswordEncoder md5 = new Md5PasswordEncoder();
					String pwd = md5.encodePassword(form.getCondition().get("pwd").toString().trim(), account.getUid());
					if (pwd.equals(account.getPwd())) {
						// 帐号密码正确
						issucc = true;
					}
				}

				if (issucc) {
					if (account.getStatus() == 1) {// 帐号状态为已激活

						// 用户选择了保存帐号
						if (form.getCondition().get("rmb") != null && "checked".equals(form.getCondition().get("rmb").toString())) {
							// 写入Cookie
							Cookie cookie = new Cookie("account", "");
							cookie.setMaxAge(60 * 60 * 24 * 365);
							cookie.setPath(request.getContextPath());
							Cookie[] cookies = request.getCookies();
							Boolean ACexist = false;
							if (cookies != null && cookies.length > 0) {
								for (Cookie cook : cookies) {
									// Cookie中保存的用户ID按登录顺序排列，最后登录的账号排在队尾
									if ("account".equals(cook.getName())) {
										ACexist = true;
										if (cook.getValue() != null && !"".equals(cook.getValue().trim())) {
											if (cook.getValue().indexOf(account.getUser().getId()) < 0) {// 若该账号还未写入Cookie中，将该账号加入Cookie队尾
												cookie.setValue(cook.getValue() + "," + cook.getValue().split(",").length + ":" + account.getUser().getId());
											} else {// 账号已经登录过，将Cookie中的用户ID重新排列
												String[] userQueue = cook.getValue().split(",");
												String newCookieValue = "";
												for (int i = 0; i < userQueue.length; i++) {
													String[] userQueueId = userQueue[i].split(":");
													if (userQueueId.length == 2) {// 此处若不是2不进行处理
														if (userQueueId[1].equals(account.getUser().getId()) && (i + 1) < userQueue.length) {// 该用户ID不在已有的Cookie队列的末尾，若在末尾则无需处理
															String[] userQueueNextId = userQueue[i + 1].split(":");// 取得队列中该用户ID的下一个用户ID
															// 交换ID位置
															String tmp = userQueueId[1];
															userQueueId[1] = userQueueNextId[1];
															userQueueNextId[1] = tmp;
															// 将交换后的结果放回队列中
															userQueue[i] = userQueueId[0] + ":" + userQueueId[1];
															userQueue[i + 1] = userQueueNextId[0] + ":" + userQueueNextId[1];
														}
													}
													newCookieValue += userQueue[i];
													if ((i + 1) < userQueue.length) {
														newCookieValue += ",";
													}
												}
												cookie.setValue(newCookieValue);
											}
										}
										break;
									}
								}
							}
							if (!ACexist) {// 名为account的Cookie不存在
								cookie.setValue("0:" + account.getUser().getId());
							}
							response.addCookie(cookie);
						}

						String addresult = addSession(account, session);
						if ("ok".equals(addresult)) {

							request.getSession().setAttribute("selectType", 1);

							result = "success::" + Lang.getLanguage("user.login.prompt.success", request.getSession().getAttribute("lang").toString()) + "::" + path;// "登陆成功！";
							Map<String, CUser> users = (HashMap<String, CUser>) session.getAttribute("otherUser");
							Iterator it = users.entrySet().iterator(); // 取得键对象

							/*
							 * String org = ""; String ins = ""; String admin =
							 * ""; while (it.hasNext()) { Map.Entry pairs =
							 * (Map.Entry) it.next(); CUser user1 = (CUser)
							 * pairs.getValue(); if (1 == user1.getLevel() || 5
							 * == user1.getLevel()) { ins +=
							 * "<div class=\"loginboxChild\">" + user1.getName()
							 * + "</div>"; } else if (2 == user1.getLevel() || 3
							 * == user1.getLevel()) { org +=
							 * "<div class=\"loginboxChild\">" + user1.getName()
							 * + "</div>"; } else if (4 == user1.getLevel()) {
							 * admin += "<div class=\"loginboxChild\">" +
							 * user1.getName() + "</div>"; } } String cleardiv =
							 * "<div class=\"clear\"></div>"; if (admin != null
							 * && admin.length() > 1) { admin =
							 * "<div class=\"loginboxParent\">" +
							 * Lang.getLanguage(
							 * "Controller.User.login.label.admin",
							 * request.getSession().getAttribute("lang").
							 * toString()) + "</div>" + admin + cleardiv;//
							 * 超级管理员： } if (org != null && org.length() > 1) {
							 * org = "<div class=\"loginboxParent\">" +
							 * Lang.getLanguage(
							 * "Controller.User.login.label.insUser",
							 * request.getSession().getAttribute("lang").
							 * toString()) + "</div>" + org + cleardiv;// 机构用户：
							 * } if (ins != null && ins.length() > 1) { ins =
							 * "<div class=\"loginboxParent\">" +
							 * Lang.getLanguage(
							 * "Controller.User.login.label.person",
							 * request.getSession().getAttribute("lang").
							 * toString()) + "</div>" + ins + cleardiv;// 个人用户：
							 * }
							 */

							// 若当前在IP范围内登陆，取出机构LOGO
							BInstitution institution = (BInstitution) session.getAttribute("institution");
							String logo = "";
							if (institution != null && institution.getLogo() != null) {
								// logo ="<div><a href=\"" +
								// institution.getLogoUrl() + "\" ><img src=\""
								// + institution.getLogo() + "\" title=\""+
								// institution.getLogoNote()+"\"></a></div>" ;
								Boolean hasurl = false, hasnote = false;
								logo = "";
								if (institution.getLogoUrl() != null && !"".equals(institution.getLogoUrl().trim())) {
									hasurl = true;
									logo += "<a target=\"_blank\" href=\"" + institution.getLogoUrl() + "\" >";
								}
								if (institution.getLogoNote() != null && !"".equals(institution.getLogoNote().trim())) {
									hasnote = true;
								}
								logo += "<img style=\"width:187px;height:35px; margin-bottom:13px;\" src=\"" + (ctx != null && !"".equals(ctx) ? ctx : "http://" + domain) + institution.getLogo() + "\" title=\"" + (hasnote ? institution.getLogoNote() : "") + "\"/>";
								logo += (hasurl ? "</a>" : "");
								session.setAttribute("logoinfo", logo);
							}
							// session.setAttribute("logininfo", admin + org +
							// ins);

							// 登陆成功后清空上次未保存的搜索条件
							CUser user = session.getAttribute("mainUser") == null ? null : (CUser) session.getAttribute("mainUser");
							if (user != null) {
								// 清空未保存的搜索数据
								Map<String, Object> c = new HashMap<String, Object>();
								c.put("userId", user.getId());
								c.put("type", 1);// 临时数据
								this.cUserService.deleteSearchHisByCondition(c);
							}
							// 计算用户的购物车中的商品数量，存入session
							condition.clear();
							condition.put("status", 4);
							condition.put("userid", user.getId());
							condition.put("parentId", "0");
							condition.put("orderNull", "1");
							session.setAttribute("totalincart", this.oOrderService.getOrderDetailCount(condition));
						} else {
							result = "error::" + addresult;
						}
					} else {
						result = "error::" + Lang.getLanguage("user.login.prompt.account.unactived", request.getSession().getAttribute("lang").toString());// 账号未激活
					}
				} else {
					result = "error::" + Lang.getLanguage("user.login.prompt.account.wrong", request.getSession().getAttribute("lang").toString());// 账号或密码错误
				}
			} else {

				result = "error::" + Lang.getLanguage("Pages.login.user.pwd", request.getSession().getAttribute("lang").toString());// 请填写用户名和密码！
				// result = "error:" +
				// Lang.getLanguage("user.login.prompt.account.empty",
				// request.getSession().getAttribute("lang").toString());//
				// 非法请求，无参数访问此访问
			}
		} catch (Exception e) {
			result = "error::" + ((e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		try {
			response.setContentType("text/html;charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print(result);
			out.flush();
			out.close();
		} catch (Exception e) {
			form.setMsg((e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
	}

	private String addSession(CAccount account, HttpSession session) throws Exception {
		String result = null;
		try {
			CUser user = account.getUser();
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("userId", user.getId());
			List<CUserProp> props = this.customService.getUserPropList(condition, "");
			int flag = 0;
			if (props != null && props.size() > 0) {
				for (CUserProp prop : props) {
					if (prop.getCode() != null && "freetax".equalsIgnoreCase(prop.getCode())) {
						if ("2".equals(prop.getVal())) {// 免税用户
							flag = 1;
							break;
						}
					}
				}
			}
			session.setAttribute("isFreeUser", flag);// 当前mainUser是否免税 1免税，2非免税
			session.setAttribute("mainUserLevel", user.getLevel());
			session.setAttribute("mainUser", user);

			// -------------其他用户session处理
			if (session.getAttribute("otherUser") == null) {
				Map<String, CUser> otheruser = new HashMap<String, CUser>();
				otheruser.put(user.getId(), user);
				session.setAttribute("otherUser", otheruser);// 副登陆session中无值时直接插入
			} else {
				Map<String, CUser> users = (HashMap<String, CUser>) session.getAttribute("otherUser");
				users.put(account.getUser().getId(), account.getUser());
				session.setAttribute("otherUser", users);
			}

			// ---------------荐购用户登录session处理
			if (session.getAttribute("mainUser") != null) {// 用户在IP范围内
				session.setAttribute("recommendUser", session.getAttribute("mainUser"));
			} else if (session.getAttribute("ipUserId") != null) {
				Map<String, CUser> users = (HashMap<String, CUser>) session.getAttribute("otherUser");
				session.setAttribute("recommendUser", users.get(session.getAttribute("ipUserId")));
			} else {
				session.setAttribute("recommendUser", null);
			}

			result = "ok";
		} catch (Exception e) {
			result = (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), session.getAttribute("lang").toString()) : e.getMessage();
		}
		return result;
	}

	@RequestMapping(value = "/form/loginout")
	public void loginout(HttpServletRequest request, HttpServletResponse response, HttpSession session, CAccountForm form) throws Exception {
		String result;
		String path = "";//form.getCondition().get("beforPath").toString();
		try {
			session.removeAttribute("mainUser");
			session.removeAttribute("isFreeUser");
			session.removeAttribute("otherUser");
			session.removeAttribute("recommendUser");
			session.removeAttribute("logininfo");
			session.removeAttribute("institution");
			session.removeAttribute("mainUserLevel");
			session.removeAttribute("totalincart");
			session.removeAttribute("logoinfo");
			session.removeAttribute("selectType");
			session.setAttribute("isFristRequest", "yes");
			Cookie[] cookies = request.getCookies();// 从浏览器的缓存中取出所有cookie
			if (cookies != null) {// 如果没有cookie是null
				for (int i = 0; i < cookies.length; i++) {
					if ("account".equals(cookies[i].getName())) {
						cookies[i].setPath(request.getContextPath());
						cookies[i].setMaxAge(0);
						response.addCookie(cookies[i]);
						break;
					}
				}
			}
			result = "success::" + path;
		} catch (Exception e) {
			result = "error:" + ((e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		try {
			response.setContentType("text/html;charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print(result);
			out.flush();
			out.close();
		} catch (Exception e) {
			form.setMsg((e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
	}

	@RequestMapping(value = "/form/logoutRedirect")
	public ModelAndView logoutRedirect(HttpServletRequest request, HttpServletResponse response, UserForm form) throws Exception {
		String forwardString = "frame/result";
		Map<String, Object> model = new HashMap<String, Object>();
		request.setAttribute("prompt", Lang.getLanguage("Controller.User.logoutRedirect.prompt.success", request.getSession().getAttribute("lang").toString()));// "用户登出成功");
		request.setAttribute("message", Lang.getLanguage("Controller.User.logoutRedirect.message", request.getSession().getAttribute("lang").toString()));// "您可以继续以匿名用户的身份浏览平台，或者重新登录后使用");
		model.put("form", form);
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 个人用户注册成功后，激活账户请求
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/registerCheck")
	public ModelAndView registerCheck(HttpServletRequest request, HttpServletResponse response, UserForm form) throws Exception {
		String forwardString = "frame/result";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			String accountId = request.getParameter("accountId");
			CAccount account = this.customService.getAccountById(accountId);
			if (account != null) {
				account.setStatus(1);
				this.customService.updateAccount(account, accountId, null);
				CUser user = this.customService.getUser(account.getUser().getId());
				user.setStatus(1);
				this.customService.updateUser(user, account.getUser().getId(), null);
				request.setAttribute("prompt", Lang.getLanguage("Controller.User.registerCheck.prompt.success", request.getSession().getAttribute("lang").toString()));// "激活成功提示");
				request.setAttribute("message", Lang.getLanguage("Controller.User.registerCheck.activation.success", request.getSession().getAttribute("lang").toString()));// "账号激活成功！");
			} else {
				request.setAttribute("prompt", Lang.getLanguage("Controller.User.registerCheck.prompt.error", request.getSession().getAttribute("lang").toString()));// "重置失败提示");
				request.setAttribute("message", Lang.getLanguage("Controller.User.registerCheck.activation.error", request.getSession().getAttribute("lang").toString()));// "不存在该账户，激活失败！");
			}
		} catch (Exception e) {
			request.setAttribute("prompt", Lang.getLanguage("Controller.User.registerCheck.prompt.error", request.getSession().getAttribute("lang").toString()));// "激活失败提示");
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		model.put("form", form);
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 新增用户信息
	 * 
	 * @param name
	 * @param userTypeId
	 * @return
	 * @throws Exception
	 */
	private CUser insertUser(String name, String userTypeId) throws Exception {
		CUser user = new CUser();
		user.setCreatedon(new Date());
		user.setName(name);
		user.setStatus(2);
		user.setLevel(1);
		CUserType userType = new CUserType();
		userType.setId(userTypeId);
		user.setUserType(userType);
		this.customService.insertCUser(user);
		return user;
	}

	/**
	 * 新增账户信息
	 * 
	 * @param uid
	 * @param pwd
	 * @param user
	 * @return
	 * @throws Exception
	 */
	private CAccount insertAccount(String uid, String pwd, CUser user) throws Exception {
		CAccount account = new CAccount();
		account.setCreatedon(new Date());
		account.setUser(user);
		account.setUid(uid);
		Md5PasswordEncoder md5 = new Md5PasswordEncoder();
		pwd = md5.encodePassword(pwd, uid);
		account.setPwd(pwd);
		account.setStatus(2);
		this.customService.insertAccount(account);
		return account;
	}

	/**
	 * 新增用户属性信息，同时返回用户email，用于用户邮箱激活功能
	 * 
	 * @param propIds
	 * @param propValues
	 * @param user
	 * @return
	 * @throws Exception
	 */
	private void insertUserProp(Object[] propIds, Object[] propValues, CUser user) throws Exception {
		for (int i = 0; i < propIds.length; i++) {
			CUserProp userProp = new CUserProp();
			userProp.setUser(user);
			// 根据页面传的类型属性id获取类型属性详细信息，并存储到用户属性中。
			CUserTypeProp typeProp = this.customService.getUserTypePropById(propIds[i].toString());
			userProp.setUserTypeProp(typeProp);
			userProp.setCode(typeProp.getCode());
			userProp.setKey(typeProp.getKey());
			userProp.setVal(propValues[i].toString());
			userProp.setOrder(i);
			userProp.setDisplay(typeProp.getDisplay());
			userProp.setStype(typeProp.getStype());
			userProp.setMust(typeProp.getMust());
			userProp.setSvalue(typeProp.getSvalue());
			userProp.setCreatedon(new Date());
			this.customService.insertCUserProp(userProp);
		}
	}

	/**
	 * 用户注册成功后向用户注册时填写的邮箱发送邮件进行激活
	 * 
	 * @param email
	 * @param userName
	 * @param idORuid
	 *            账户id或账户uid（当用户自己找回密码时用账户id；当管理员重置密码时用uid）
	 * @param initialPwd
	 *            初始密码
	 * @param templateName
	 * @return
	 */
	private boolean sendEmail(String email, String userName, String idORuid, String initialPwd, String templateName, String lang) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String today = formatter.format(new Date());
		Map<String, String> body = new HashMap<String, String>();
		body.put("username", userName);
		body.put("content1", Lang.getLanguage("Controller.User.sendEmail.content1", lang));// "您好");
		String fwdTemplate = Param.getParam("mail.template.findPwd", true, lang).get("findPwd");
		if (templateName.equals(fwdTemplate)) {
			body.put("content2", Lang.getLanguage("Controller.User.sendEmail.initialPwdNull.content2", lang));// "欢迎你使用中图书苑，请点击链接找回密码!");
			body.put("tokenId", idORuid);
		} else {
			if (initialPwd == null) {
				body.put("content2", Lang.getLanguage("Controller.User.sendEmail.activationAcc.content2", lang));// "欢迎你使用中图书苑，请点击链接激活账户!");
				body.put("userId", idORuid);
			} else {
				body.put("content2", Lang.getLanguage("Controller.User.sendEmail.initialPwd.content2", lang));// "欢迎你使用中图书苑，请点及时修改初始账户密码!");
				body.put("uid", idORuid);
				body.put("pwd", initialPwd);
			}
		}

		body.put("email", email);
		body.put("date", today);
		Map<String, String> title = new HashMap<String, String>();
		title.put("username", userName);
		return this.sendMail.sendMail(title, body, "EPublishing", email, "A1B2", templateName);
	}

	/**
	 * 跳转到找回密码页面
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/findPwd")
	public ModelAndView findPwd(HttpServletRequest request, HttpServletResponse response, UserForm form) throws Exception {
		String forwardString = "mobile/user/findPwd";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			form.setUrl(request.getRequestURL().toString());
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		model.put("form", form);
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 找回密码请求提交
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/findPwdSubmit")
	public void findPwdSubmit(HttpServletRequest request, HttpServletResponse response, UserForm form) throws Exception {
		String result = Lang.getLanguage("Controller.User.findPwdSubmit.email", request.getSession().getAttribute("lang").toString());// "请查收修改密码邮件!";
		String uidPage = request.getParameter("uid");
		String emailPage = request.getParameter("email");
		Map<String, Object> condition = new HashMap<String, Object>();

		if (uidPage != "" && emailPage != "") {// 通过uid和email找回密码
			condition.put("loguid", uidPage);
			List<CAccount> accounts = this.customService.getAccountList(condition, null);
			condition.put("email", emailPage);
			condition.put("code", "email");
			List<CUserProp> userProps = this.customService.getUserPropList(condition, null);
			boolean flag = true;// 用于判断校验是否通过
			if (accounts == null || accounts.size() == 0) {
				result = Lang.getLanguage("Controller.User.findPwdSubmit.noName", request.getSession().getAttribute("lang").toString());// "不存在该用户名!";
				flag = false;
			} else if (userProps == null || userProps.size() == 0) {
				result = Lang.getLanguage("Controller.User.findPwdSubmit.noEmail", request.getSession().getAttribute("lang").toString());// "不存在该邮箱!";
				flag = false;
			}
			if (flag) {
				BToken token = this.configureService.createToken(BToken.TYPE_FINDPWD, userProps.get(0).getUser().getId());
				// 发邮件
				boolean sendSuccess = this.sendEmail(userProps.get(0).getVal(), userProps.get(0).getUser().getName(), token.getId(), null, Param.getParam("mail.template.findPwd", true).get("findPwd"), request.getSession().getAttribute("lang").toString());
			}
		} else if (uidPage != "") {// 通过uid找回密码
			condition.put("loguid", uidPage);
			List<CAccount> accounts = this.customService.getAccountList(condition, null);
			if (accounts == null || accounts.size() == 0) {
				result = Lang.getLanguage("Controller.User.findPwdSubmit.noName", request.getSession().getAttribute("lang").toString());// "不存在该用户名!";
			} else {
				// 发邮件
				CAccount account = accounts.get(0);
				condition.remove("email");
				condition.put("userId", account.getUser().getId());
				condition.put("code", "email");
				List<CUserProp> userProps = this.customService.getUserPropList(condition, null);
				CUserProp userProp = userProps.get(0);
				BToken token = this.configureService.createToken(BToken.TYPE_FINDPWD, userProps.get(0).getUser().getId());
				boolean sendSuccess = this.sendEmail(userProp.getVal(), userProp.getUser().getName(), token.getId(), null, Param.getParam("mail.template.findPwd", true, request.getSession().getAttribute("lang").toString()).get("findPwd"), request.getSession().getAttribute("lang").toString());

			}
		} else if (emailPage != "") {// 通过email找回密码
			condition.put("email", emailPage);
			condition.put("code", "email");
			List<CUserProp> userProps = this.customService.getUserPropList(condition, null);
			if (userProps == null || userProps.size() == 0) {
				result = Lang.getLanguage("Controller.User.findPwdSubmit.noEmail", request.getSession().getAttribute("lang").toString());// "不存在该邮箱!";
			} else {
				// 发邮件
				CUserProp userProp = userProps.get(0);
				condition.put("userId", userProp.getUser().getId());
				List<CAccount> accounts = this.customService.getAccountList(condition, null);
				BToken token = this.configureService.createToken(BToken.TYPE_FINDPWD, userProps.get(0).getUser().getId());
				boolean sendSuccess = this.sendEmail(userProp.getVal(), userProp.getUser().getName(), token.getId(), null, Param.getParam("mail.template.findPwd", true, request.getSession().getAttribute("lang").toString()).get("findPwd"), request.getSession().getAttribute("lang").toString());
			}
		}

		try {
			response.setContentType("text/html;charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print(result);
			out.flush();
			out.close();
		} catch (Exception e) {
			form.setMsg((e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
	}

	@RequestMapping(value = "/form/findPwdRedirect")
	public ModelAndView findPwdRedirect(HttpServletRequest request, HttpServletResponse response, UserForm form) throws Exception {
		String forwardString = "user/findPwdHint";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			String email = request.getParameter("email");
			form.setUrl(request.getRequestURL().toString());
			form.setEmail(email);
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		model.put("form", form);
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 跳转到重置密码页面
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/resetPwd")
	public ModelAndView resetPwd(HttpServletRequest request, HttpServletResponse response, HttpSession session, UserForm form) throws Exception {
		String forwardString = "/mobile/user/resetPwd";
		Map<String, Object> model = new HashMap<String, Object>();
		CAccount account = null;
		try {
			// 获取账户ID
			if (session.getAttribute("mainUser") != null) {// 用户登录后修改密码
				CUser user = (CUser) session.getAttribute("mainUser");
				Map<String, Object> condition = new HashMap<String, Object>();
				condition.put("userId", user.getId());
				account = this.customService.getAccount(condition);
			} else { // 用户未登录修改密码（找回密码）
				account = this.customService.getAccountById(request.getParameter("accountId"));
			}
			// 根据账户ID获取账户信息
			if (account != null) {
				account.setPwd("");
				form.setAccount(account);
			} else {
				request.setAttribute("prompt", Lang.getLanguage("Controller.User.resetPwd.prompt.error", request.getSession().getAttribute("lang").toString()));// "重置失败提示");
				request.setAttribute("message", Lang.getLanguage("Controller.User.resetPwd.message.error", request.getSession().getAttribute("lang").toString()));// "不存在该账户，重置失败！");
			}

		} catch (Exception e) {
			// e.printStackTrace();
			request.setAttribute("prompt", Lang.getLanguage("Controller.User.resetPwd.prompt.error", request.getSession().getAttribute("lang").toString()));// "重置失败提示");
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "frame/result";
		}
		model.put("form", form);
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 重置密码提交
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/resetPwdSubmit")
	public ModelAndView resetPwdSubmit(HttpServletRequest request, HttpServletResponse response, UserForm form) throws Exception {
		String forwardString = "mobile/frame/result";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			CAccount account = this.customService.getAccountById(form.getAccount().getId());
			if (account != null) {
				String pwd = form.getAccount().getPwd();
				Md5PasswordEncoder md5 = new Md5PasswordEncoder();
				pwd = md5.encodePassword(pwd, account.getUid());
				account.setPwd(pwd);
				this.customService.updateAccount(account, account.getId(), null);
				request.setAttribute("prompt", Lang.getLanguage("Controller.User.resetPwdSubmit.prompt.success", request.getSession().getAttribute("lang").toString()));// "重置成功提示");
				request.setAttribute("message", Lang.getLanguage("Controller.User.resetPwdSubmit.message.success", request.getSession().getAttribute("lang").toString()));// "密码重置成功！");
			} else {
				request.setAttribute("prompt", Lang.getLanguage("Controller.User.resetPwdSubmit.prompt.error", request.getSession().getAttribute("lang").toString()));// "重置失败提示");
				request.setAttribute("message", Lang.getLanguage("Controller.User.resetPwdSubmit.message.noName.error", request.getSession().getAttribute("lang").toString()));// "不存在该账户！");
			}
		} catch (Exception e) {
			request.setAttribute("prompt", Lang.getLanguage("Controller.User.resetPwdSubmit.prompt.error", request.getSession().getAttribute("lang").toString()));// "重置失败提示");
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		model.put("form", form);
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 机构下的荐购信息
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/myrecommend")
	public ModelAndView myRecommendList(HttpServletRequest request, HttpServletResponse response, HttpSession session, RRecommendForm form) throws Exception {
		String forwardString = "user/myrecommend";

		Map<String, Object> model = new HashMap<String, Object>();
		try {
			if (session.getAttribute("mainUser") != null) {
				form.setTypeMap(Param.getParam("product.type.config", true, request.getSession().getAttribute("lang").toString()));
				form.setUrl(request.getRequestURL().toString());
				CUser user = (CUser) session.getAttribute("mainUser");

				Map<String, Object> condition = form.getCondition();
				condition.put("available", 3);// 这是政治原因进行过滤
				condition.put("orderStatus", form.getIsOrder());
				if (form.getType() != null && Integer.parseInt(form.getType().toString()) == 2) {// 这里主要期刊时的时候，我们要同时查询期刊和杂志的期的东西
					condition.put("pubtypes", new Integer[] { 2, 7 });
				} else {
					condition.put("type", form.getType() == null ? "" : form.getType());
				}
				form.setOrderStatus(form.getIsOrder());
				form.setLevel(user.getLevel().toString());
				// System.out.println("XXXXXXXXXXXXXXXXX+UserLevel:"+user.getLevel().toString());
				String isCn = request.getParameter("isCn");
				if (form.getType() == null || form.getType() == 0) {
					form.setType(1);
				}
				/*
				 * if(isCn==null||"".equals(isCn)){
				 * isCn=form.getType()==1?isCn:"";//只有图书需要区分中文和外文 }
				 *//*
					 * else{
					 * forwardString="user/recommendList";//点击查询时，不刷新整页，仅刷新表格 }
					 */
				String sortBy = form.getSort() == null || form.getSort() == 0 ? " order by a.proCount desc, a.recommendDetailCount desc " : " order by a.recommendDetailCount desc, a.proCount desc ";
				if ((user.getLevel() == 1 || user.getLevel() == 5)) {// 个人用户
					condition.put("isCn", isCn);
					condition.put("userId", user.getId());
					form.setCount(this.rRecommendService.getRecommendDetailCount(condition));
					List<RRecommendDetail> list = this.rRecommendService.getRecommendDetailPagingList(condition, " order by a.createdon desc ", form.getPageCount(), form.getCurpage());
					if (list != null && list.size() > 0) {
						for (int i = 0; i < list.size(); i++) {
							condition = new HashMap<String, Object>();
							condition.put("institutionId", list.get(i).getRecommend().getInstitution().getId());
							condition.put("pubId", list.get(i).getRecommend().getPublications().getId());
							condition.put("level", user.getLevel());
							condition.put("status", 1);
							list.get(i).getRecommend().setIsOrdered(this.customService.getLicenseCount(condition));
						}
					}
					model.put("list", list);

				} else if (user.getLevel() == 2) {// 机构管理员
					condition.put("institutionid", user.getInstitution().getId());
					condition.put("isCn", isCn);
					// System.out.println("XXXXXXXXXXXXXXXXX+institutionid:"+user.getInstitution().getId());
					form.setCount(this.rRecommendService.getRecommendCount(condition));

					List<RRecommend> list = this.rRecommendService.getRecommendPagingList(condition, sortBy, form.getPageCount(), form.getCurpage());
					List<RRecommend> rlist = new ArrayList<RRecommend>();
					if (list != null && list.size() > 0) {
						if (user != null) {
							for (RRecommend rRecommend : list) {
								Map<String, Object> con = new HashMap<String, Object>();
								con.put("publicationsid", rRecommend.getPublications().getId());
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
								rRecommend.getPublications().setPriceList(price);
								rlist.add(rRecommend);
							}
						}
					}
					model.put("list", rlist);
				} else if (user.getLevel() == 4) {// 中图管理员
					form.setListInstitution(this.customService.getInstitutionList(null, " order by a.name "));
					condition.put("isCn", isCn);
					form.setCount(this.rRecommendService.getRecommendCount(condition));
					List<RRecommend> list = this.rRecommendService.getRecommendPagingList(condition, sortBy, form.getPageCount(), form.getCurpage());
					model.put("list", list);
				}
				int tabIndex = 1;
				if (form.getType() == 2 && "true".equals(isCn)) {
					tabIndex = 3;
				} else if (form.getType() == 2 && "false".equals(isCn)) {
					tabIndex = 4;
				} else if (form.getType() == 1 && "false".equals(isCn)) {
					tabIndex = 2;
				}
				if (form.getType() == 4) {
					tabIndex = 5;
				}
				model.put("isCn", isCn);
				model.put("tabIndex", tabIndex);
			}
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		model.put("form", form);
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 个人下的推荐历史
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/recommend")
	public ModelAndView recommendList(HttpServletRequest request, HttpServletResponse response, HttpSession session, RRecommendForm form) throws Exception {
		String forwardString = "user/recommend";

		Map<String, Object> model = new HashMap<String, Object>();
		try {
			if (session.getAttribute("mainUser") != null) {
				form.setTypeMap(Param.getParam("product.type.config", true, request.getSession().getAttribute("lang").toString()));
				form.setUrl(request.getRequestURL().toString());
				CUser user = (CUser) session.getAttribute("mainUser");

				String dr = request.getParameter("dateRange");
				String startTime = "";
				String endTime = "";
				if (dr == null || "0".equals(dr)) {

				} else if ("1".equals(dr)) {// 一周内
					endTime = DateUtil.getNowDate("yyyy-MM-dd");
					Date sTime = DateUtil.getDealedDate(new Date(), 0, 0, -7, 0, 0, 0);
					startTime = DateUtil.getDateStr("yyyy-MM-dd", sTime);
				} else if ("2".equals(dr)) {// 一月内
					endTime = DateUtil.getNowDate("yyyy-MM-dd");
					Date sTime = DateUtil.getDealedDate(new Date(), 0, -1, 0, 0, 0, 0);
					startTime = DateUtil.getDateStr("yyyy-MM-dd", sTime);
				} else if ("3".equals(dr)) {// 三月内
					endTime = DateUtil.getNowDate("yyyy-MM-dd");
					Date sTime = DateUtil.getDealedDate(new Date(), 0, -3, 0, 0, 0, 0);
					startTime = DateUtil.getDateStr("yyyy-MM-dd", sTime);
				}

				Map<String, Object> condition = form.getCondition();
				condition.put("available", 3);// 这是政治原因进行过滤
				condition.put("orderStatus", form.getIsOrder());
				if (form.getType() != null && Integer.parseInt(form.getType().toString()) == 2) {// 这里主要期刊时的时候，我们要同时查询期刊和杂志的期的东西
					condition.put("pubtypes", new Integer[] { 2, 7 });
				} else {
					condition.put("type", form.getType() == null ? "" : form.getType());
				}
				condition.put("startTime", startTime);
				condition.put("endTime", endTime);
				form.setOrderStatus(form.getIsOrder());
				form.setLevel(user.getLevel().toString());
				// System.out.println("XXXXXXXXXXXXXXXXX+UserLevel:"+user.getLevel().toString());
				String isCn = request.getParameter("isCn");
				if (form.getType() == null || form.getType() == 0) {
					form.setType(1);
				}
				/*
				 * if(isCn==null||"".equals(isCn)){
				 * isCn=form.getType()==1?isCn:"";//只有图书需要区分中文和外文 }
				 *//*
					 * else{
					 * forwardString="user/recommendList";//点击查询时，不刷新整页，仅刷新表格 }
					 */
				String sortBy = form.getSort() == null || form.getSort() == 0 ? " order by a.proCount desc, a.recommendDetailCount desc " : " order by a.recommendDetailCount desc, a.proCount desc ";
				if ((user.getLevel() == 1 || user.getLevel() == 5 || user.getLevel() == 2)) {// 个人用户
					condition.put("isCn", isCn);
					condition.put("userId", user.getId());
					form.setCount(this.rRecommendService.getRecommendDetailCount(condition));
					List<RRecommendDetail> list = this.rRecommendService.getRecommendDetailPagingList(condition, " order by a.createdon desc ", form.getPageCount(), form.getCurpage());
					if (list != null && list.size() > 0) {
						for (int i = 0; i < list.size(); i++) {
							condition = new HashMap<String, Object>();
							condition.put("institutionId", list.get(i).getRecommend().getInstitution().getId());
							condition.put("pubId", list.get(i).getRecommend().getPublications().getId());
							condition.put("level", user.getLevel());
							condition.put("status", 1);
							list.get(i).getRecommend().setIsOrdered(this.customService.getLicenseCount(condition));
						}
					}
					model.put("list", list);

				} else if (user.getLevel() == 4) {// 中图管理员
					form.setListInstitution(this.customService.getInstitutionList(null, " order by a.name "));
					condition.put("isCn", isCn);
					form.setCount(this.rRecommendService.getRecommendCount(condition));
					List<RRecommend> list = this.rRecommendService.getRecommendPagingList(condition, sortBy, form.getPageCount(), form.getCurpage());
					model.put("list", list);

				}
				int tabIndex = 1;
				if (form.getType() == 2 && "true".equals(isCn)) {
					tabIndex = 3;
				} else if (form.getType() == 2 && "false".equals(isCn)) {
					tabIndex = 4;
				} else if (form.getType() == 1 && "false".equals(isCn)) {
					tabIndex = 2;
				}
				if (form.getType() == 4) {
					tabIndex = 5;
				}
				model.put("isCn", isCn);
				model.put("tabIndex", tabIndex);
				model.put("dr", dr);
			}
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		model.put("form", form);

		return new ModelAndView(forwardString, model);
	}

	/**
	 * 显示针对单个出版物的荐购信息
	 * 
	 * @param recid
	 * @param request
	 * @param response
	 * @param session
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/myrecommend/detail/{recid}")
	public ModelAndView myRecommendList(@PathVariable String recid, HttpServletRequest request, HttpServletResponse response, HttpSession session, RRecommendDetailForm form) throws Exception {
		String forwardString = "user/myrecommend/detaillist";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			if (session.getAttribute("mainUser") != null) {
				form.setUrl(request.getRequestURL().toString());
				CUser user = (CUser) session.getAttribute("mainUser");
				Map<String, Object> condition = form.getCondition();
				if (user.getLevel() == 2 || user.getLevel() == 4) {// 机构管理员
					condition.put("recid", recid);
					List<RRecommendDetail> list = this.rRecommendService.getRecommendDetailPagingList(condition, " order by b.level desc, a.createdon desc ", form.getPageCount(), form.getCurpage());
					form.setCount(this.rRecommendService.getRecommendDetailCount(condition));
					model.put("list", list);
				}
			}
			model.put("form", form);
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		return new ModelAndView(forwardString, model);
	}

	@RequestMapping(value = "/form/myrecommend/statistics")
	public ModelAndView myRecommendStatistics(HttpServletRequest request, HttpServletResponse response, HttpSession session, RRecommendForm form) throws Exception {
		String forwardString = "user/myrecommend/statistics";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			if (session.getAttribute("mainUser") != null) {
				CUser user = (CUser) session.getAttribute("mainUser");
				Map<String, Object> condition = form.getCondition();
				if (user.getLevel() == 2) {// 机构管理员
					condition.put("institutionid", user.getInstitution().getId());
					List<RRecommend> list = this.rRecommendService.getRecommendPagingList(condition, "order by a.createdon desc", form.getPageCount(), form.getCurpage());
					model.put("list", list);
				}
			}
			model.put("form", form);
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 维护用户个人资料
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/userDetail")
	public ModelAndView userDetail(HttpServletRequest request, HttpServletResponse response, HttpSession session, UserForm form) throws Exception {
		String forwardString = "user/userDetail";
		Map<String, Object> model = new HashMap<String, Object>();
		List<CUserProp> list = null;
		try {
			form.setUrl(request.getRequestURL().toString());
			Map<String, Object> condition = form.getCondition();
			if (session.getAttribute("mainUser") != null) {
				CUser user = (CUser) session.getAttribute("mainUser");
				form.setUserName(user.getName());
				// 查询“个人”类型属性数据
				condition.put("userId", user.getId());
				list = this.customService.getUserPropList(condition, " order by a.must desc,a.order ");
				// 用户类型：个人用户
				form.setCountryMap(Param.getParam("country.value", true, request.getSession().getAttribute("lang").toString()));
				form.setIdentityMap(Param.getParam("identity.value", true, request.getSession().getAttribute("lang").toString()));
			}
			/**
			 * 将数据返回页面
			 */
			if (form.getPropsValue() != null) {// 修改个人资料验证不通过将form表单值返回页面
				if (list != null && !list.isEmpty()) {
					for (int i = 0; i < list.size(); i++) {
						form.getValues().put(list.get(i).getCode(), form.getPropsValue()[i]);
					}
				}
			} else { // 修改个人资料将数据库数据返回页面
				if (list != null && !list.isEmpty()) {
					for (int i = 0; i < list.size(); i++) {
						form.getValues().put(list.get(i).getCode(), list.get(i).getVal());
					}
				}
			}

			model.put("list", list);
			model.put("form", form);
		} catch (Exception e) {
			request.setAttribute("prompt", Lang.getLanguage("Controller.User.userDetail.prompt.error", request.getSession().getAttribute("lang").toString()));// "个人资料维护失败提示");
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 个人资料维护请求提交
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/detailSubmit")
	public ModelAndView detailSubmit(HttpServletRequest request, HttpServletResponse response, HttpSession session, UserForm form) throws Exception {
		String forwardString = "frame/result";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			boolean msg = (Boolean) (request.getAttribute("msg") == null ? true : request.getAttribute("msg"));
			if (msg) {
				form.setUrl(request.getRequestURL().toString());
				// 验证邮箱的唯一性
				String emailValue = "";
				for (int i = 0; i < form.getTypePropIds().length; i++) {
					// 根据页面传的类型属性id获取类型属性详细信息，并存储到用户属性中。
					CUserProp userProp = this.customService.getUserPropById(form.getTypePropIds()[i].toString());
					if (userProp != null && userProp.getCode().equals("email")) {
						emailValue = form.getPropsValue()[i].toString();
						break;
					}
				}

				if (session.getAttribute("mainUser") != null) {
					// 修改用户
					CUser user = (CUser) session.getAttribute("mainUser");
					// 验证邮箱
					if (this.customService.checkEmailExist(user.getId(), emailValue)) {
						form.setMsg(Lang.getLanguage("Controller.User.detailSubmit.msg.already.email.error", request.getSession().getAttribute("lang").toString()));// "已经存在该邮箱，请重新输入！");
						return userDetail(request, response, session, form);
					}
					user.setUpdatedon(new Date());
					user.setName(form.getUserName());
					user.setSendStatus(1);
					this.customService.updateUser(user, user.getId(), null);
					// 修改用户属性
					this.updateUserProp(form.getTypePropIds(), form.getPropsValue());
					// request.setAttribute("prompt",
					// Lang.getLanguage("Controller.User.detailSubmit.prompt.success",
					// request.getSession().getAttribute("lang").toString()));//
					// "维护个人资料成功提示");
					// request.setAttribute("message",
					// Lang.getLanguage("Controller.User.detailSubmit.message.success",
					// request.getSession().getAttribute("lang").toString()));//
					// "维护个人资料成功!");
					form.setMsg(Lang.getLanguage("Controller.User.detailSubmit.message.success", request.getSession().getAttribute("lang").toString()));
					form.setIsSuccess("true");
				}
			} else {
				// request.setAttribute("prompt",
				// Lang.getLanguage("Controller.User.detailSubmit.prompt.error",
				// request.getSession().getAttribute("lang").toString()));//
				// "维护个人资料失败提示");
				// request.setAttribute("message",
				// Lang.getLanguage("Conteoller.Global.prompt.info",
				// request.getSession().getAttribute("lang").toString()));//
				// "维护个人资料失败!");
				form.setMsg(Lang.getLanguage("Controller.User.detailSubmit.prompt.error", request.getSession().getAttribute("lang").toString()));
				form.setIsSuccess("false");
			}
		} catch (Exception e) {
			// request.setAttribute("prompt",
			// Lang.getLanguage("Controller.User.detailSubmit.prompt.error",
			// request.getSession().getAttribute("lang").toString()));//
			// "维护个人资料失败提示");
			// request.setAttribute("message", (e instanceof CcsException) ?
			// Lang.getLanguage(((CcsException) e).getPrompt(),
			// request.getSession().getAttribute("lang").toString()) :
			// e.getMessage());
			form.setMsg(Lang.getLanguage("Controller.User.detailSubmit.prompt.error", request.getSession().getAttribute("lang").toString()));
			form.setIsSuccess("false");
			return userDetail(request, response, session, form);
		}
		return userDetail(request, response, session, form);
		// model.put("form", form);
		// return new ModelAndView(forwardString, model);
	}

	/**
	 * 修改用户属性信息
	 * 
	 * @param propIds
	 * @param propValues
	 * @return
	 * @throws Exception
	 */
	private void updateUserProp(Object[] propIds, Object[] propValues) throws Exception {
		for (int i = 0; i < propIds.length; i++) {
			CUserProp userProp = new CUserProp();
			// 根据页面传的类型属性id获取类型属性详细信息，并存储到用户属性中。
			userProp.setVal(propValues[i].toString());
			userProp.setUpdatedon(new Date());
			this.customService.updateCUserProp(userProp, propIds[i].toString(), null);
		}
	}

	@RequestMapping(value = "/form/myaccount")
	public ModelAndView doJump(HttpServletRequest request, HttpServletResponse response, HttpSession session, UserForm form) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		String forwardString = "frame/result";
		try {
			if (session.getAttribute("mainUser") != null) {
				CUser user = (CUser) session.getAttribute("mainUser");
				if (user.getLevel() == 1 || user.getLevel() == 5) {
					forwardString = "user/myaccount";
				} else if (user.getLevel() == 2) {
					forwardString = "user/instaccount";
				} else if (user.getLevel() == 4) {
					forwardString = "user/superaccount";
				} else {
					request.setAttribute("prompt", Lang.getLanguage("Controller.User.doJump.prompt.error", request.getSession().getAttribute("lang").toString()));// "访问我的账户失败提示");
					request.setAttribute("message", Lang.getLanguage("Controller.User.doJump.message.noCompetence.error", request.getSession().getAttribute("lang").toString()));// "对不起，您没有权限访问此页面");
				}
			} else {
				request.setAttribute("prompt", Lang.getLanguage("Controller.User.doJump.prompt.error", request.getSession().getAttribute("lang").toString()));// "访问我的账户失败提示");
				request.setAttribute("message", Lang.getLanguage("Controller.User.doJump.message.loginTimout.error", request.getSession().getAttribute("lang").toString()));// "您的登录可能已经失效，请重新登录以后再访问本页面");
			}
		} catch (Exception e) {
			request.setAttribute("prompt", Lang.getLanguage("Controller.User.doJump.prompt.error", request.getSession().getAttribute("lang").toString()));// "访问我的账户失败提示");
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			return userDetail(request, response, session, form);
		}
		model.put("form", form);
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 个人信息管理
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/userManage")
	public ModelAndView userManage(HttpServletRequest request, HttpServletResponse response, HttpSession session, UserForm form) throws Exception {
		String forwardString = "user/userManage";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			form.setUrl(request.getRequestURL().toString());
			if (session.getAttribute("mainUser") != null) {
				CUserType userTypePerson = this.customService.getUserTypeByCode("1");// 获取对应的用户类型
				CUserType userTypeExpert = this.customService.getUserTypeByCode("5");// 获取对应的用户类型
				Object[] userTypeIds = { userTypePerson.getId(), userTypeExpert.getId() };
				Map<String, Object> condition = form.getCondition();
				// 查询“机构”类型属性数据
				condition.put("userTypeIds", userTypeIds);
				// condition.put("institutionId",user.getInstitution().getId());
				// condition.put("status", form.getStatus());
				form.setCount(this.customService.getAccountCount(condition, null));
				List<CAccount> list = this.customService.getAccountPagingList(condition, " order by a.createdon desc ", form.getPageCount(), form.getCurpage());
				model.put("list", list);
			}
		} catch (Exception e) {
			request.setAttribute("prompt", Lang.getLanguage("Controller.User.userManage.prompt.error", request.getSession().getAttribute("lang").toString()));// "机构管理失败提示");
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		model.put("form", form);
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 管理员重置账户密码
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/resetPwdByAdmin")
	public void resetPwdByAdmin(HttpServletRequest request, HttpServletResponse response, HttpSession session, UserForm form) throws Exception {
		String result = "false:" + Lang.getLanguage("Global.Lable.Prompt.ReLogin", request.getSession().getAttribute("lang").toString());
		try {
			form.setUrl(request.getRequestURL().toString());
			if (session.getAttribute("mainUser") != null) {
				CUser user = (CUser) session.getAttribute("mainUser");
				String accountId = request.getParameter("id");
				CAccount account = this.customService.getAccountById(accountId);

				Map<String, Object> condition = form.getCondition();
				condition.put("userId", account.getUser().getId());
				List<CUserProp> userPropList = this.customService.getUserPropList(condition, " order by a.order");

				String emailValue = "";
				for (CUserProp up : userPropList) {
					if (up != null && up.getCode().equals("email")) {
						emailValue = up.getVal();
						break;
					}
				}
				// 给注册用户发送确认邮件
				// 用当前日期作为重置密码
				String initialPwd = RandomCodeUtil.generateRandomCode(10);
				Md5PasswordEncoder md5 = new Md5PasswordEncoder();
				String pwd = md5.encodePassword(initialPwd, account.getUid());
				account.setPwd(pwd);
				account.setUpdatedon(new Date());
				this.customService.updateAccount(account, accountId, null);

				boolean isSuccess = this.sendEmail(emailValue, account.getUser().getName(), account.getUid(), initialPwd, Param.getParam("mail.template.orgRegister", true, request.getSession().getAttribute("lang").toString()).get("orgRegister"), request.getSession().getAttribute("lang").toString());
				if (isSuccess) {
					result = "true:" + Lang.getLanguage("Controller.User.resetPwdByAdmin.msg.email", request.getSession().getAttribute("lang").toString());// "重置成功，请查收账号密码重置邮件！");
				} else {
					result = "false:" + Lang.getLanguage("Controller.User.resetPwdByAdmin.msg.noEmail", request.getSession().getAttribute("lang").toString());// "重置成功，但邮件发送失败，请联系管理员！");
				}

				condition.remove("userId");
			}
		} catch (Exception e) {
			result = "false:" + ((e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print(result);
		out.flush();
		out.close();
		// return userManage(request, response,session, form);
	}

	/**
	 * 改变账户状态（激活/停用）
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/changeStatus")
	public ModelAndView changeStatus(HttpServletRequest request, HttpServletResponse response, HttpSession session, UserForm form) throws Exception {
		String result = "";
		try {
			form.setUrl(request.getRequestURL().toString());
			if (session.getAttribute("mainUser") != null) {
				CUser loginUser = (CUser) session.getAttribute("mainUser");

				int status = Integer.valueOf(request.getParameter("status"));
				String accountId = request.getParameter("id");
				CAccount account = this.customService.getAccountById(accountId);
				account.setStatus(status);
				account.setUpdatedon(new Date());
				account.setUpdatedby(account.getUser().getId());
				this.customService.updateAccount(account, accountId, null);

				CUser user = account.getUser();
				user.setStatus(status);
				user.setUpdatedon(new Date());
				user.setUpdatedby(loginUser.getId());
				this.customService.updateUser(user, user.getId(), null);

				if (status == 1) {
					result = "true:" + Lang.getLanguage("Controller.User.changeStatus.activation.success", request.getSession().getAttribute("lang").toString());// 账户激活成功!";
				} else {
					result = "true:" + Lang.getLanguage("Controller.User.changeStatus.stop.success", request.getSession().getAttribute("lang").toString());// 账户停用成功!";
				}

				response.setContentType("text/html;charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.print(result);
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			result = "false:" + Lang.getLanguage("Controller.User.changeStatus.oper.error", request.getSession().getAttribute("lang").toString());// 操作失败!";
			form.setMsg((e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		return userManage(request, response, session, form);
	}

	/* 跳转到mySubscriptionIndex页面 */
	@RequestMapping("/form/mySubscriptionIndex")
	public ModelAndView mySubscriptionIndex(HttpServletRequest request, HttpServletResponse response, HttpSession session, LLicenseForm form) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		String forwardString = "user/subscription_manage";
		form.setUrl(request.getRequestURL().toString());
		String t = request.getParameter("searchType");
		form.setSearchType(null == t || "".equals(t) ? 1 : Integer.valueOf(t));
		model.put("form", form);
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 显示我的订阅
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/mySubscription/{who}")
	public ModelAndView showMySubscription(@PathVariable String who, HttpServletRequest request, HttpServletResponse response, HttpSession session, LLicenseForm form) throws Exception {
		String forwardString = "user/subscription";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			form.setWho(who);
			form.setUrl(request.getRequestURL().toString());
			String isCn = request.getParameter("isCn");
			if (session.getAttribute("mainUser") != null) {
				CUser user = (CUser) session.getAttribute("mainUser");
				Map<String, Object> condition = form.getCondition();
				condition.put("status", 1);
				condition.put("available", 3);// 这是过滤图书是政治原因的条件
				if (form.getSearchType() != null && !"".equals(form.getSearchType())) {
					if (form.getSearchType() == 1) {
						condition.put("searchType", new Integer[] { 1, 3 });// 图书
																			// 、章节
					} else if (form.getSearchType() == 2) {
						condition.put("searchType", new Integer[] { 2, 4, 6, 7 });
						String parameter = request.getParameter("pType");

						if (parameter != null) {
							int pType = Integer.parseInt(parameter);
							if (pType == 1) {// 刊
								condition.put("searchType", new Integer[] { 2 });
							} else if (pType == 2) {// 文章
								condition.put("searchType", new Integer[] { 4 });
							} else if (pType == 3) {// 期
								condition.put("searchType", new Integer[] { 7 });
							}
							model.put("pType", pType);
						}
					} else if (form.getSearchType() == 3) {
						condition.put("searchType", new Integer[] { 5 });
					} else if (form.getSearchType() == 99) {
						condition.put("collections", "0");
					} /*
						 * else if (form.getSearchType() == 7) {//期刊杂志类的期
						 * condition.put("searchType", new Integer[] { 7 });
						 * model.put("pType", 7); }
						 */
				}

				model.put("type", form.getSearchType());
				List<LLicense> list = null;
				condition.put("isTrail", "0");
				if ("private".equals(who)) {
					form.setRange(1);
					condition.put("isCn", isCn);
					condition.put("userid", user.getId());
					form.setCount(this.customService.getLicenseCount(condition));
					list = this.customService.getLicensePagingList(condition, "order by a.createdon desc", form.getPageCount(), form.getCurpage());
				} else if ("public".equals(who)) {
					/*
					 * if(user.getInstitution()!=null){//查看mainUser所属的机构订阅信息
					 * condition.put("level",2); condition.put("institutionId",
					 * user.getInstitution().getId()); }else{
					 * request.setAttribute("prompt", "查看机构订阅失败提示");
					 * request.setAttribute("message",
					 * "您的账号不属于一个有效的机构，无法查看机构订阅信息");
					 * forwardString="frame/result"; }
					 */
					form.setRange(2);
					if (user.getInstitution() != null && user.getInstitution().getId() != null) {// 查看session中登录用户的机构订阅信息
						condition.put("level", 2);
						if (isCn != null) {
							condition.put("isCn", isCn);
						}
						condition.put("institutionId", user.getInstitution().getId());
						form.setCount(this.customService.getLicenseCount(condition));
						list = this.customService.getLicensePagingList(condition, "order by a.createdon desc ", form.getPageCount(), form.getCurpage());
					} else if (session.getAttribute("institution") != null) {// 查看session中保存的机构订阅信息
						condition.put("level", 2);
						condition.put("isCn", isCn);
						condition.put("institutionId", ((BInstitution) session.getAttribute("institution")).getId());
						form.setCount(this.customService.getLicenseCount(condition));
						list = this.customService.getLicensePagingList(condition, "order by a.createdon desc ", form.getPageCount(), form.getCurpage());
					} else {
						request.setAttribute("prompt", Lang.getLanguage("Controller.User.showMySubscription.prompt.error", request.getSession().getAttribute("lang").toString()));// "查看机构订阅失败提示");
						request.setAttribute("message", Lang.getLanguage("Controller.User.showMySubscription.message.error", request.getSession().getAttribute("lang").toString()));// "对不起，您不在机构IP范围内，无法查看机构订阅信息");
						forwardString = "frame/result";
					}
				} else if ("all".equals(who)) {
					form.setRange(3);
					condition.put("isCn", isCn);
					condition.put("userid", user.getId());
					if (session.getAttribute("institution") != null) {// 查看session中保存的机构订阅信息
						condition.put("level", 2);
						condition.put("institutionId", ((BInstitution) session.getAttribute("institution")).getId());
					}
					form.setCount(this.customService.getAllLicenseCount(condition, ""));
					list = this.customService.getAllLicensePagingList(condition, " order by a.createdon desc ", form.getPageCount(), form.getCurpage());
				}
				model.put("isCn", isCn);
				model.put("list", list);
			}
		} catch (Exception e) {
			e.printStackTrace();
			form.setMsg((e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		model.put("form", form);
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 产品包订阅情况
	 * 
	 * @param who
	 * @param request
	 * @param response
	 * @param session
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/mySubscription/{who}/collection")
	public ModelAndView showCollectionSubscription(@PathVariable String who, HttpServletRequest request, HttpServletResponse response, HttpSession session, LLicenseForm form) throws Exception {
		String forwardString = "user/subscription_collection";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			form.setWho(who);
			form.setUrl(request.getRequestURL().toString());
			if (session.getAttribute("mainUser") != null) {
				CUser user = (CUser) session.getAttribute("mainUser");
				Map<String, Object> condition = form.getCondition();
				condition.put("status", 3);
				condition.put("itemType", 99);
				form.setSearchType(4);// 产品包
				List<OOrderDetail> list = null;
				if ("private".equals(who)) {
					form.setRange(1);
					condition.put("userid", user.getId());
					form.setCount(this.oOrderService.getOrderDetailCount(condition));
					list = this.oOrderService.getOrderDetailPagingList(condition, "order by a.createdon desc ", form.getPageCount(), form.getCurpage());
				} else if ("public".equals(who)) {
					if (user.getInstitution() != null) {// 查看mainUser所属的机构订阅信息
						condition.put("level", 2);
						condition.put("institutionId", user.getInstitution().getId());
					} else {
						request.setAttribute("prompt", "查看机构订阅失败提示");
						request.setAttribute("message", "您的账号不属于一个有效的机构，无法查看机构订阅信息");
						forwardString = "frame/result";
					}
					form.setRange(2);
					if (session.getAttribute("institution") != null) {// 查看session中保存的机构订阅信息
						// condition.put("level",2);
						condition.put("institutionId", ((BInstitution) session.getAttribute("institution")).getId());
						form.setCount(this.oOrderService.getOrderDetailCount(condition));
						list = this.oOrderService.getOrderDetailPagingList(condition, "order by a.createdon desc ", form.getPageCount(), form.getCurpage());
					} else if (user.getInstitution() != null && user.getInstitution().getId() != null) {// 查看session中登录用户的机构订阅信息
						// condition.put("level",2);
						condition.put("institutionId", user.getInstitution().getId());
						form.setCount(this.oOrderService.getOrderDetailCount(condition));
						list = this.oOrderService.getOrderDetailPagingList(condition, "order by a.createdon desc ", form.getPageCount(), form.getCurpage());
					} else {
						request.setAttribute("prompt", Lang.getLanguage("Controller.User.showMySubscription.prompt.error", request.getSession().getAttribute("lang").toString()));// "查看机构订阅失败提示");
						request.setAttribute("message", Lang.getLanguage("Controller.User.showMySubscription.message.error", request.getSession().getAttribute("lang").toString()));// "对不起，您不在机构IP范围内，无法查看机构订阅信息");
						forwardString = "frame/result";
					}
				} else if ("all".equals(who)) {
					form.setRange(3);
					// condition.put("userid", user.getId());
					if (session.getAttribute("institution") != null) {// 查看session中保存的机构订阅信息
						// condition.put("level",2);
						condition.put("institutionId", ((BInstitution) session.getAttribute("institution")).getId());
					}
					form.setCount(this.oOrderService.getOrderDetailCount(condition));
					list = this.oOrderService.getOrderDetailPagingList(condition, "order by a.createdon desc ", form.getPageCount(), form.getCurpage());
				}
				model.put("list", list);
			}
		} catch (Exception e) {
			e.printStackTrace();
			form.setMsg((e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		model.put("form", form);
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 改变账户交税状态（交税/免税）
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/changeFaxStatus")
	public void changeFaxStatus(HttpServletRequest request, HttpServletResponse response, HttpSession session, OrgUserForm form) throws Exception {
		String result = "";
		try {
			form.setUrl(request.getRequestURL().toString());
			if (session.getAttribute("mainUser") != null) {
				String accountId = request.getParameter("id");
				CAccount account = this.customService.getAccountById(accountId);
				Map<String, Object> condition = form.getCondition();
				condition.put("userId", account.getUser().getId());
				condition.put("code", "freetax");
				List<CUserProp> list = this.customService.getUserPropList(condition, null);
				if (list != null && list.size() != 0) {
					CUserProp userProp = list.get(0);
					userProp.setVal(request.getParameter("faxStatus"));
					this.customService.updateCUserProp(userProp, userProp.getId(), null);
				}

				if (Integer.valueOf(request.getParameter("faxStatus")) == 1) {
					result = "true:" + Lang.getLanguage("Controller.User.changeFaxStatus.fax.success", request.getSession().getAttribute("lang").toString());// 缴税设置成功!";
				} else {
					result = "true:" + Lang.getLanguage("Controller.User.changeFaxStatus.free.success", request.getSession().getAttribute("lang").toString());// 免税设置成功!";
				}
			}
		} catch (Exception e) {
			result = "false:" + Lang.getLanguage("Controller.User.changeFaxStatus.oper.error", request.getSession().getAttribute("lang").toString());// 操作失败!";
			// form.setMsg((e instanceof CcsException) ?
			// Lang.getLanguage(((CcsException) e).getPrompt(),
			// request.getSession().getAttribute("lang").toString()) :
			// e.getMessage());
		}
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print(result);
		out.flush();
		out.close();
	}

	// @RequestMapping(value="/form/subjectAlerts/{subCode}")
	// public ModelAndView subjectAlerts(@PathVariable String
	// subCode,HttpServletRequest request,HttpServletResponse
	// response,HttpSession session, UserForm form)throws Exception {
	// Map<String,Object> model=new HashMap<String,Object>();
	// String forwardString="alerts/subjectAlertsList";
	// form.setUrl(request.getRequestURL().toString());
	// try{
	// if(session.getAttribute("mainUser")!=null){
	// // CUser user=(CUser)session.getAttribute("mainUser");
	// model.put("pCode", subCode);
	// Map<String,Object> condition = new HashMap<String, Object>();
	// List<BSubject> list =null;
	// if(subCode.equalsIgnoreCase("all")){
	// condition.put("treeCodeLength", 6);
	// list = this.bSubjectService.getSubList(condition, " order by a.order ");
	// }else{
	// condition.remove("treeCodeLength");
	// condition.put("code", subCode.toLowerCase());
	// list = this.bSubjectService.getSubList(condition, " ");
	// }
	//
	// if(list!=null && list.size()>0){
	// model.put("list", list);
	// }else{
	// form.setMsg("没有结果!");
	// }
	// model.put("form", form);
	// }else{
	// }
	// }catch(Exception e){
	// request.setAttribute("message",(e instanceof
	// CcsException)?Lang.getLanguage(((CcsException)e).getPrompt(),request.getSession().getAttribute("lang").toString()):e.getMessage());
	// }
	// return new ModelAndView(forwardString, model);
	// }

	@RequestMapping(value = "/form/subjectAlerts")
	public ModelAndView subjectAlerts(HttpServletRequest request, HttpServletResponse response, HttpSession session, UserForm form) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		String forwardString = "alerts/subjectAlertsList";
		form.setUrl(request.getRequestURL().toString());
		try {
			if (session.getAttribute("mainUser") != null) {
				CUser user = (CUser) session.getAttribute("mainUser");
				model.put("pCode", form.getpCode());
				Map<String, Object> condition = new HashMap<String, Object>();

				condition.put("userSubject", user.getId());
				List<BSubject> list = null;
				if (form.getpCode() == null || "".equals(form.getpCode()) || form.getpCode().equalsIgnoreCase("all")) {
					condition.put("treeCodeLength", 6);
				} else {
					// condition.remove("treeCodeLength");
					condition.put("code", form.getpCode().toLowerCase());
				}

				list = this.bSubjectService.getSubList(condition, " order by a.order ");
				if (list != null && list.size() > 0) {
					model.put("list", list);
				} else {
					form.setMsg(Lang.getLanguage("Controller.User.subjectAlerts.msg.error", request.getSession().getAttribute("lang").toString()));// "没有结果!");
				}
				model.put("form", form);
			} else {
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 提交用户订阅的学科主题提醒请求
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/alertsSubmit")
	public ModelAndView alertsSubmit(HttpServletRequest request, HttpServletResponse response, HttpSession session, UserForm form) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		// String forwardString = "frame/result";
		String forwardString = "alerts/mySubjectAlertsList";
		try {
			boolean msg = (Boolean) (request.getAttribute("msg") == null ? true : request.getAttribute("msg"));
			if (msg) {
				if (session.getAttribute("mainUser") != null) {
					CUser user = (CUser) session.getAttribute("mainUser");

					if (form.getDeals() != null && form.getFrequencys() != null) {
						for (int i = 0; i < form.getDeals().length; i++) {
							// 先删除下级学科主题的订阅提醒
							this.customService.deleteAlertsByTreeCode(form.getTreeCodes()[i], user.getId());
							BSubject subject = this.bSubjectService.getSubject(form.getDeals()[i]);
							CUserAlerts alerts = new CUserAlerts();
							alerts.setUser(user);
							alerts.setType(form.getAlertsType());
							alerts.setFrequency(form.getFrequencys()[i]);
							alerts.setCreatedon(new Date());
							alerts.setTreeCode(subject.getTreeCode());

							alerts.setSubject(subject);

							this.customService.insertUserAlerts(alerts);
						}
					}

					// request.setAttribute("prompt",
					// Lang.getLanguage("Controller.User.alertsSubmit.prompt.success",
					// request.getSession().getAttribute("lang").toString()));//
					// "用户订阅提醒成功提示");
					// request.setAttribute("message",
					// Lang.getLanguage("Controller.User.alertsSubmit.message.success",
					// request.getSession().getAttribute("lang").toString()));//
					// "用户订阅提醒成功！");
					form.setMsg(Lang.getLanguage("Controller.User.alertsSubmit.message.success", request.getSession().getAttribute("lang").toString()));
					return this.mySubjectAlerts(request, response, session, form);
				}
			} else {
				request.setAttribute("prompt", Lang.getLanguage("Controller.User.alertsSubmit.prompt.error", request.getSession().getAttribute("lang").toString()));// "用户订阅提醒失败提示");
				request.setAttribute("message", Lang.getLanguage("Conteoller.Global.prompt.info", request.getSession().getAttribute("lang").toString()));// "用户订阅提醒失败！");
			}
		} catch (Exception e) {
			request.setAttribute("prompt", Lang.getLanguage("Controller.User.alertsSubmit.prompt.error", request.getSession().getAttribute("lang").toString()));// "用户订阅提醒失败提示");
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		model.put("form", form);
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 查询用户已订阅的学科主题提醒信息
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/mySubjectAlerts")
	public ModelAndView mySubjectAlerts(HttpServletRequest request, HttpServletResponse response, HttpSession session, UserForm form) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		String forwardString = "alerts/mySubjectAlertsList";
		form.setUrl(request.getRequestURL().toString());
		try {
			if (session.getAttribute("mainUser") != null) {
				CUser user = (CUser) session.getAttribute("mainUser");
				model.put("pCode", form.getpCode());
				Map<String, Object> condition = new HashMap<String, Object>();
				List<CUserAlerts> list = null;
				condition.put("userId", user.getId());
				condition.put("type", 2);
				list = this.customService.getCUserAlertsList(condition, " order by b.order ");

				if (list != null && list.size() > 0) {
					model.put("list", list);
				} else {
					if (form.getMsg() == null) {
						form.setMsg(Lang.getLanguage("Controller.User.mySubjectAlerts.msg.error", request.getSession().getAttribute("lang").toString()));// "没有结果!");
					}
				}

			}
			model.put("form", form);
		} catch (Exception e) {
			// e.printStackTrace();
			forwardString = "frame/error";
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		return new ModelAndView(forwardString, model);
	}

	@RequestMapping(value = "/form/alertsSave")
	public void alertsSave(HttpServletRequest request, HttpServletResponse response, UserForm form) throws Exception {
		String result = "error:" + Lang.getLanguage("Controller.User.alerts.modify.failed", request.getSession().getAttribute("lang").toString());// 保存失败！";
		try {
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			if (user != null) {
				Map<String, Object> condition = new HashMap<String, Object>();
				condition.put("id", form.getId());
				condition.put("userId", user.getId());
				List<CUserAlerts> list = this.customService.getCUserAlertsList(condition, "");
				if (list != null && !list.isEmpty()) {
					list.get(0).setFrequency(form.getFrequency());
					this.customService.updateUserAlerts(list.get(0));
					result = "success:" + Lang.getLanguage("Controller.User.alerts.modify.success", request.getSession().getAttribute("lang").toString());
				}
			}
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		try {
			response.setContentType("text/html;charset=utf-8");
			PrintWriter writer = response.getWriter();
			writer.print(result);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	/**
	 * 删除用户已订阅的提醒
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/alertsDelete")
	public ModelAndView alertsDelete(HttpServletRequest request, HttpServletResponse response, HttpSession session, UserForm form) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			if (session.getAttribute("mainUser") != null) {
				CUser user = (CUser) session.getAttribute("mainUser");
				String[] dels = form.getDels().split(",");
				for (String alertsId : dels) {
					this.customService.deleteCUserAlerts(alertsId);
				}
				form.setMsg(Lang.getLanguage("Controller.User.alertsDelete.msg.delete.success", request.getSession().getAttribute("lang").toString()));// "订阅提醒信息删除成功!");
			}
			model.put("form", form);
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		return this.mySubjectAlerts(request, response, session, form);
	}

	/**
	 * 个人消费记录
	 * 
	 * @param request
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/myTranLog")
	public ModelAndView getTranLog(HttpServletRequest request, HttpServletResponse response, HttpSession session, OTransationForm form) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		String forwardString = "user/myTranLog";
		form.setUrl(request.getRequestURL().toString());
		try {
			if (session.getAttribute("mainUser") != null) {
				CUser user = (CUser) session.getAttribute("mainUser");
				Map<String, Object> condition = new HashMap<String, Object>();
				condition.put("userId", user.getId());
				form.setBalance(this.oOrderService.getUserBalance(user.getId()));
				// form.setTotalDeposits(this.oOrderService.getUserTotalDeposit(user.getId()));
				form.setTotalSpending(this.oOrderService.getUserTotalPay(user.getId()));// 消费总额合计
				// form.setTotalFreeze(this.oOrderService.getUserTotalFreeze(user.getId()));
				form.setCount(this.oOrderService.getLogCount(condition));
				List<OTransation> list = this.oOrderService.getTransationLogPagingList(condition, form.getPageCount(), form.getCurpage());
				model.put("list", list);
				model.put("form", form);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			// throw e;
			// request.setAttribute("message",(e instanceof
			// CcsException)?Lang.getLanguage(((CcsException)e).getPrompt(),request.getSession().getAttribute("lang").toString()):e.getMessage());
		}
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 机构消费记录
	 * 
	 * @param request
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/insTranLog")
	public ModelAndView getInsTranLog(HttpServletRequest request, HttpServletResponse response, HttpSession session, OTransationForm form) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		String forwardString = "user/insTranLog";
		form.setUrl(request.getRequestURL().toString());
		try {
			if (session.getAttribute("mainUser") != null) {
				CUser user = (CUser) session.getAttribute("mainUser");
				Map<String, Object> condition = new HashMap<String, Object>();
				condition.put("userId", user.getId());
				form.setBalance(this.oOrderService.getUserBalance(user.getId()));
				// form.setTotalDeposits(this.oOrderService.getUserTotalDeposit(user.getId()));
				form.setTotalSpending(this.oOrderService.getUserTotalPay(user.getId()));// 消费总额合计
				// form.setTotalFreeze(this.oOrderService.getUserTotalFreeze(user.getId()));
				form.setCount(this.oOrderService.getLogCount(condition));
				List<OTransation> list = this.oOrderService.getTransationLogPagingList(condition, form.getPageCount(), form.getCurpage());
				model.put("list", list);
				model.put("form", form);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			// throw e;
			// request.setAttribute("message",(e instanceof
			// CcsException)?Lang.getLanguage(((CcsException)e).getPrompt(),request.getSession().getAttribute("lang").toString()):e.getMessage());
		}
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 获取用户列表，未更新到DCC的
	 * 
	 * @param request
	 * @param model
	 * @throws Exception
	 *             by ruixue cheng
	 */
	@RequestMapping(value = "/userList")
	public void getUserList(HttpServletRequest request, Model model) throws Exception {
		ResultObject<CUser> result = null;
		try {
			Map<String, Object> condition = new HashMap<String, Object>();
			Object[] levels = { 1, 2, 5 };
			condition.put("sendStatus", 1);
			condition.put("status", 1);
			condition.put("levelin", levels);
			List<CUser> list = this.cUserService.getUserList(condition, null);

			if (list != null && !list.isEmpty()) {
				ObjectUtil<CUser> util = new ObjectUtil<CUser>();
				for (int i = 0; i < list.size(); i++) {
					util.setNull(list.get(i), new String[] { Set.class.getName() });
				}
			}
			result = new ResultObject<CUser>(1, list, Lang.getLanguage("Controller.User.getUserList.query.success", request.getSession().getAttribute("lang").toString()));// "获取人员列表成功！");
		} catch (Exception e) {
			result = new ResultObject<CUser>(2, (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		model.addAttribute("target", result);
	}

	@RequestMapping(value = "/setUserSendStatus", method = RequestMethod.POST)
	public void setUserSendStatus(HttpServletRequest request, Model model) {
		ResultObject<CUser> result = null;
		try {
			String objJson = request.getParameter("obj").toString();
			String operType = request.getParameter("operType").toString(); // 1-insert
																			// 2-update
			Converter<CUser> converter = new Converter<CUser>();
			CUser obj = (CUser) converter.json2Object(objJson, CUser.class.getName());
			CUser epUser = null;
			if (obj.getId() != null && !"".equals(obj.getId())) {
				epUser = this.customService.getUser(obj.getId());
			}
			if ("2".equals(operType)) {
				if (obj.getSendStatus() != null && obj.getSendStatus() > 0) {
					epUser.setBookPartial(obj.getBookPartial() != null ? obj.getBookPartial() : epUser.getBookPartial());
					epUser.setJournalPartial(obj.getJournalPartial() != null ? obj.getJournalPartial() : epUser.getJournalPartial());
					epUser.setLevel(obj.getLevel() != null && obj.getLevel() != 0 ? obj.getLevel() : epUser.getLevel());
					epUser.setUserType(obj.getUserType() != null ? obj.getUserType() : epUser.getUserType());
					epUser.setSendStatus(obj.getSendStatus());// 更新用户的发送状态
				}
				this.customService.updateUser(epUser, obj.getId(), null);
			} else if ("1".equals(operType)) {
				if (obj != null) {
					this.customService.insertCUser(obj);
				}
			} else {
				// 删除
			}
			ObjectUtil<CUser> util = new ObjectUtil<CUser>();
			obj = util.setNull(obj, new String[] { Set.class.getName(), List.class.getName() });
			if (obj.getUserType() != null) {
				ObjectUtil<CUserType> util1 = new ObjectUtil<CUserType>();
				util1.setNull(obj.getUserType(), new String[] { Set.class.getName(), List.class.getName() });
			}
			result = new ResultObject<CUser>(1, obj, Lang.getLanguage("Controller.User.setUserSendStatus.manage.success", request.getSession().getAttribute("lang").toString()));// "订单维护成功！");//"订单信息维护成功！");
		} catch (Exception e) {
			result = new ResultObject<CUser>(2, Lang.getLanguage("Controller.User.setUserSendStatus.manage.error", request.getSession().getAttribute("lang").toString()));// "订单维护失败！");//"订单信息维护失败！");
		}
		model.addAttribute("target", result);
	}

	/**
	 * 查询搜索历史--
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/searchHistory")
	public ModelAndView searchHistory(HttpServletRequest request, HttpServletResponse response, HttpSession session, UserForm form) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		String forwardString = "user/searchHistory_save";
		form.setUrl(request.getRequestURL().toString());
		try {
			CUser user = session.getAttribute("mainUser") == null ? null : (CUser) session.getAttribute("mainUser");
			if (user != null) {
				if (form.getType() == null || form.getType() == 0) {
					form.setType(1);
				}
				Map<String, Object> condition = new HashMap<String, Object>();
				condition.put("type", form.getType());
				condition.put("userId", user.getId());
				List<CSearchHis> list = this.cUserService.getSearchHistoryList(condition, " order by a.createOn ");
				// 查询文件夹列表
				List<CDirectory> dirList = this.cUserService.getDirtoryList(condition, " order by a.name ");
				model.put("list", list);
				model.put("form", form);
				model.put("dirList", dirList);
			}
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		return new ModelAndView(forwardString, model);
	}

	@RequestMapping(value = "/form/saveSearch")
	public void saveSearch(HttpServletRequest request, HttpServletResponse response, HttpSession session, UserForm form) throws Exception {
		String result = "error:" + Lang.getLanguage("Controller.User.saveSearch.save.error", request.getSession().getAttribute("lang").toString());// 保存失败！";
		try {
			CUser user = session.getAttribute("mainUser") == null ? null : (CUser) session.getAttribute("mainUser");
			if (user != null && form.getId() != null && !"".equals(form.getId())) {
				if (form.getOperType() == 2) {
					CSearchHis his = this.cUserService.getSearchHistory(form.getId());
					if (his != null) {
						his.setId(form.getId());
						his.setType(form.getOperType());
						String properties[] = null;
						if (form.getDirId() != null && !"".equals(form.getDirId()) && !"0".equals(form.getDirId())) {
							CDirectory directory = new CDirectory();
							directory.setId(form.getDirId());
							his.setDirectory(directory);
						} else {
							properties = new String[] { "directory" };
						}
						this.cUserService.updateSearchHis(his, form.getId(), properties);
						result = "success:" + Lang.getLanguage("Controller.User.saveSearch.save.success", request.getSession().getAttribute("lang").toString());// 保存成功！";
					}
				} else if (form.getOperType() == 3) {
					// 删除
					this.cUserService.deleteSearchHis(form.getId());
					result = "success:" + Lang.getLanguage("Controller.User.saveSearch.del.success", request.getSession().getAttribute("lang").toString());// 删除成功！";
				}
			}
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		try {
			response.setContentType("text/html;charset=utf-8");
			PrintWriter writer = response.getWriter();
			writer.print(result);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
	}

	/**
	 * 搜索收藏夹
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/form/dirManager")
	public ModelAndView dirManager(HttpServletRequest request, HttpServletResponse response, HttpSession session, UserForm form) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		String forwardString = "user/search_dir";
		form.setUrl(request.getRequestURL().toString());
		try {
			CUser user = session.getAttribute("mainUser") == null ? null : (CUser) session.getAttribute("mainUser");
			if (user != null) {
				Map<String, Object> condition = new HashMap<String, Object>();
				condition.put("userId", user.getId());
				form.setCount(this.cUserService.getDirtoryCount(condition));
				List<CDirectory> dirList = this.cUserService.getDirtoryPagingList(condition, " order by a.name ", form.getPageCount(), form.getCurpage());
				model.put("dirList", dirList);
			}
			form.setUrl(request.getRequestURL().toString());
			model.put("form", form);
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		return new ModelAndView(forwardString, model);
	}

	@RequestMapping("/form/searchIndex")
	public ModelAndView searchIndex(HttpServletRequest request, HttpServletResponse response, HttpSession session, UserForm form) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		String forwardString = "frame/result";
		form.setUrl(request.getRequestURL().toString());
		try {
			CUser user = session.getAttribute("mainUser") == null ? null : (CUser) session.getAttribute("mainUser");
			if (user != null) {
				forwardString = "user/search";
			}
			model.put("form", form);
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		return new ModelAndView(forwardString, model);
	}

	@RequestMapping("/form/editDir")
	public ModelAndView editDir(HttpServletRequest request, HttpServletRequest response, UserForm form) throws Exception {
		String forwardString = "user/editDir";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			if (request.getParameter("eid") != null && !"".equals(request.getParameter("eid").toString())) {
				form.setDirObj(this.cUserService.getDirtory(request.getParameter("eid").toString()));
				form.setId(request.getParameter("eid").toString());
			}
			model.put("form", form);
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		return new ModelAndView(forwardString, model);
	}

	@RequestMapping(value = "/form/editDirSubmit")
	public void editDirSubmit(HttpServletRequest request, HttpServletResponse response, UserForm form) throws Exception {
		String result = "error:" + Lang.getLanguage("Controller.User.editDirSubmit.save.error", request.getSession().getAttribute("lang").toString());// 保存失败！";
		try {
			boolean msg = (Boolean) (request.getAttribute("msg") == null ? true : request.getAttribute("msg"));
			if (msg) {
				CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
				if (user != null && form.getName() != null && !"".equals(form.getName())) {
					CDirectory dir = new CDirectory();
					dir.setId(form.getId());
					dir.setName(form.getName());
					if (form.getId() != null && !"".equals(form.getId())) {
						Map<String, Object> condition = new HashMap<String, Object>();
						condition.put("name", form.getName().trim());
						condition.put("userId1", form.getId());
						List<CDirectory> list = this.cUserService.getDirtoryList1(condition, "");
						if (list != null && list.size() > 0) {
							result = "error:" + Lang.getLanguage("Controller.User.Directory.Exist.error", request.getSession().getAttribute("lang").toString());// 存在相同的文件名！";
						} else {
							this.cUserService.updateDirectory(dir, form.getId(), null);
							result = "success:" + Lang.getLanguage("Controller.User.editDirSubmit.save.success", request.getSession().getAttribute("lang").toString());// 保存成功！";
						}
					} else {
						// 查询是否存在相同的文件名
						Map<String, Object> condition = new HashMap<String, Object>();
						condition.put("name", form.getName().trim());
						condition.put("userId", user.getId());
						int num = this.cUserService.getDirtoryCount(condition);
						if (num > 0) {
							result = "error:" + Lang.getLanguage("Controller.User.Directory.Exist.error", request.getSession().getAttribute("lang").toString());// 存在相同的文件名！";
						} else {
							dir.setUser(user);
							this.cUserService.addDirectory(dir);
							result = "success:" + Lang.getLanguage("Controller.User.editDirSubmit.save.success", request.getSession().getAttribute("lang").toString());// 保存成功！";
						}
					}
				}
			} else {
				result = "error:" + Lang.getLanguage("Conteoller.Global.prompt.info", request.getSession().getAttribute("lang").toString());// 保存失败！";
			}
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		try {
			response.setContentType("text/html;charset=utf-8");
			PrintWriter writer = response.getWriter();
			writer.print(result);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			// e.printStackTrace();
		}

	}

	/**
	 * 查询搜索历史--Ajax
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/searchHistoryAjax")
	public void searchHistoryAjax(HttpServletRequest request, HttpServletResponse response, HttpSession session, UserForm form) throws Exception {
		String result = "error^" + Lang.getLanguage("Controller.User.searchHistoryAjax.query.error", request.getSession().getAttribute("lang").toString());// 获取失败！";
		try {
			CUser user = session.getAttribute("mainUser") == null ? null : (CUser) session.getAttribute("mainUser");
			if (user != null) {
				String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
				url += "/index/search?type=0&searchValue=";
				if (form.getType() == null || form.getType() == 0) {
					form.setType(1);
				}
				Map<String, Object> condition = new HashMap<String, Object>();
				condition.put("type", form.getType());
				condition.put("userId", user.getId());
				condition.put("dirId", form.getDirId());
				List<CSearchHis> list = this.cUserService.getSearchHistoryList(condition, " order by a.createOn ");
				// 查询文件夹列表
				List<CDirectory> dirList = this.cUserService.getDirtoryList(condition, " order by a.name ");
				String dir = formatSelect(dirList, request.getSession().getAttribute("lang").toString());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				StringBuffer sb = new StringBuffer();
				// sb.append("<div class=\"weihu\">");
				if (list != null && list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						CSearchHis cs = list.get(i);

						sb.append("<div class=\"weihu_l\">");
						sb.append("<p>");
						sb.append(Lang.getLanguage("Pages.User.SearchHistorySave.Table.Label.keyWord", request.getSession().getAttribute("lang").toString())).append("：");
						sb.append("<a ");
						sb.append("onclick=\"searchByCondition('searchValue','").append(cs.getKeyword()).append("');\">").append(cs.getKeyword()).append("</a>");
						sb.append("</p>");
						sb.append("<p>");
						sb.append(Lang.getLanguage("Pages.User.SearchHistorySave.Table.Label.time", request.getSession().getAttribute("lang").toString())).append("：");
						sb.append(sdf.format(cs.getCreateOn()));
						sb.append("</p>");
						sb.append(dir.replace("CS_ID", cs.getId()));
						sb.append("<a onclick=\"save('").append(cs.getId()).append("',this.value,3);\" class=\"a_cancel\">");
						sb.append(Lang.getLanguage("Global.Button.Delete", request.getSession().getAttribute("lang").toString())).append("</a>");
						sb.append("</div>");
						// sb.append("<tr
						// id='tt_"+cs.getType()+"_"+cs.getId()+"'>");
						// String keyword = cs.getKeyword();
						//// if (cs.getKeyword().length() > 20) {
						//// keyword = keyword.substring(0, 20) + "...";
						//// }
						// sb.append("<td class=\"").append(className).append("
						// tdname\" title=\"").append(keyword).append("\">");
						// sb.append("<div><ul>");
						// sb.append("<li>");
						// sb.append(Lang.getLanguage("Pages.User.SearchHistorySave.Table.Label.keyWord",
						// request.getSession().getAttribute("lang").toString())).append("：").append("<a
						// href=\"")
						// .append(url).append(cs.getKeyword()).append("\">").append(keyword).append("</a>");
						// sb.append("</li>").append("<li>").append(Lang.getLanguage("Pages.User.SearchHistorySave.Table.Label.time",
						// request.getSession().getAttribute("lang").toString()))
						// .append(sdf.format(cs.getCreateOn())).append("</li>");
						// sb.append("</ul></div></td>");
						// sb.append("<td>");
						// sb.append(dir.replace("CS_ID", cs.getId()));
						// sb.append("&nbsp;&nbsp;<input type=\"button\"
						// onclick=\"save('").append(cs.getId()).append("',this.value,3)\"
						// class=\"bton01\" value=\"")
						// .append(Lang.getLanguage("Global.Button.Delete",
						// request.getSession().getAttribute("lang").toString())).append("\"/>");
						// sb.append("</td></tr>");
						// sb.append("<tr><td
						// colspan='2'>------------------------------</td></tr>");
					}
				}
				// sb.append("</div>");
				result = "success^" + sb.toString();
			}
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
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

	private String formatSelect(List<CDirectory> list, String lang) {
		StringBuffer result = new StringBuffer();
		result.append("<select id='change_CS_ID' name='change' onchange=\"save('CS_ID',this.value,2)\">");
		result.append("<option value=''>-------</option>");
		result.append("<option value='0'>").append(Lang.getLanguage("Pages.User.SearchHistorySave.Table.Select.label", lang)).append("</option>");
		if (list != null && list.size() > 0) {
			for (CDirectory cDirectory : list) {
				result.append("<option value='").append(cDirectory.getId()).append("'>").append(cDirectory.getName()).append("</option>");
			}
		}
		result.append("</select>");
		return result.toString();
	}

	@RequestMapping(value = "/form/deleteDir")
	public void deleteDir(HttpServletRequest request, HttpServletResponse response, UserForm form) throws Exception {
		String result = "error:" + Lang.getLanguage("Controller.User.deleteDir.del.error", request.getSession().getAttribute("lang").toString());// 删除失败！";
		try {
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			if (user != null) {
				// 先删除搜索历史表
				Map<String, Object> condition = new HashMap<String, Object>();
				condition.put("dirId", form.getId());
				this.cUserService.deleteSearchHisByCondition(condition);
				// 删除文件夹
				this.cUserService.deleteDirectory(form.getId());
				result = "success:" + Lang.getLanguage("Controller.User.deleteDir.del.success", request.getSession().getAttribute("lang").toString());// 删除成功！";
			}
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		try {
			response.setContentType("text/html;charset=utf-8");
			PrintWriter writer = response.getWriter();
			writer.print(result);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	@RequestMapping(value = "/form/saveTransation")
	public void saveTransation(HttpServletRequest request, HttpServletResponse response, UserForm form) throws Exception {
		String result = "error:" + Lang.getLanguage("Controller.User.saveTransation.save.error", request.getSession().getAttribute("lang").toString());// 删除失败！";
		try {
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			if (user != null) {
				// 先查询要预存的用户是否存在
				CUser cu = this.cUserService.getUser(form.getId());
				if (cu == null || cu.getId() == null || "".equals(cu.getId())) {
					throw new CcsException("user.info.get.error");// 未找到用户信息
				}
				// 直接插入记录
				Date date = new Date();
				OTransation obj = new OTransation();
				obj.setAmount(form.getAmount());
				obj.setCreatedby(user.getName());
				obj.setCreatedon(date);
				obj.setType(1);
				obj.setUpdatedby(user.getName());
				obj.setUpdatedon(date);
				obj.setUser(cu);
				this.oOrderService.insertTransation(obj);
				result = "success:" + Lang.getLanguage("Controller.User.saveTransation.save.success", request.getSession().getAttribute("lang").toString());// 删除成功！";
			}
		} catch (Exception e) {
			// e.printStackTrace();
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		try {
			response.setContentType("text/html;charset=utf-8");
			PrintWriter writer = response.getWriter();
			writer.print(result);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	@RequestMapping(value = "/form/getdawsonaccount")
	public void getDawsonAccount(HttpServletRequest request, HttpServletResponse response, HttpSession session, CAccountForm form) throws Exception {
		String result;
		try {
			result = "username=cnpiecguo&password=guoreader";
			response.setContentType("text/html;charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print(result);
			out.flush();
			out.close();
		} catch (Exception e) {
			form.setMsg((e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
	}

	/**
	 * 下载访问记录统计
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/downloadLog")
	public ModelAndView getLogFile(HttpServletRequest request, HttpServletResponse response, LAccessForm form) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		String forwardString = "user/downloadLog";
		// form.setUrl(request.getRequestURL().toString());
		List<LAccess> list = null;
		try {
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			if (user != null && (user.getLevel() == 2 || user.getLevel() == 3)) {// 限制为机构管理员或中图管理员可下载
				Map<String, Object> condition = form.getCondition();
				// if(condition.get("year")==null){
				// condition.put("year", StringUtil.formatDate(new
				// Date(),"yyyy"));
				// }
				// form.setYear(condition.get("year").toString());
				// request.setAttribute("year",
				// condition.get("year").toString());
				String pubType = request.getParameter("pubtype");
				if (pubType != null && "2".equals(pubType)) {
					condition.put("pubtypes", new Integer[] { 2, 4 });
				} else if (pubType != null && "1".equals(pubType)) {
					condition.put("pubtype", 1);
				}
				condition.put("year", form.getYear());
				condition.put("startMonth", form.getStartMonth());
				condition.put("endMonth", form.getEndMonth());
				condition.put("type", 1);
				condition.put("access", 1);
				if (user.getInstitution() != null && user.getLevel() != 3) {
					condition.put("institutionId2", user.getInstitution().getId());
				}
				list = this.logAOPService.getLogOfYearPaging2(condition, " group by a.publications.id ", 0, 0);
			}
			// model.put("year",form.getYear());
			// model.put("startMonth",Integer.valueOf(form.getStartMonth()));
			// model.put("endMonth",Integer.valueOf(form.getEndMonth()));
			// model.put("form",form);
			// model.put("list",list);
			// 输出的excel文件工作表名
			String worksheet = "access_log_" + form.getYear();
			// excel工作表的标题
			StringBuffer sb = new StringBuffer();
			sb.append("Title;");
			sb.append("Publisher;");
			sb.append("Platform;");
			sb.append("Book DOI;");
			sb.append("Propriet Identifier;");
			sb.append("ISBN;");
			sb.append("ISSN;");
			sb.append("Reporting Period Total;");
			Integer start = Integer.valueOf(form.getStartMonth());
			Integer end = Integer.valueOf(form.getEndMonth());
			for (int i = start; i <= end; i++) {
				String month = "";
				if (i == 1) {
					month = "Jan";
				}
				if (i == 2) {
					month = "Feb";
				}
				if (i == 3) {
					month = "Mar";
				}
				if (i == 4) {
					month = "Apr";
				}
				if (i == 5) {
					month = "May";
				}
				if (i == 6) {
					month = "Jun";
				}
				if (i == 7) {
					month = "Jul";
				}
				if (i == 8) {
					month = "Aug";
				}
				if (i == 9) {
					month = "Sep";
				}
				if (i == 10) {
					month = "Oct";
				}
				if (i == 11) {
					month = "Nov";
				}
				if (i == 12) {
					month = "Dec";
				}
				sb.append(month + "-" + form.getYear() + ";");
			}
			String title[] = sb.toString().split(";");

			WritableWorkbook workbook;
			OutputStream os = response.getOutputStream();
			response.reset();// 清空输出流
			response.setHeader("Content-disposition", "attachment; filename=" + worksheet + ".xls");// 设定输出文件头
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");// 定义输出类型

			workbook = Workbook.createWorkbook(os);

			WritableSheet sheet = workbook.createSheet(worksheet, 0);

			for (int i = 0; i < title.length; i++) {
				// Label(列号,行号 ,内容 )
				sheet.addCell(new Label(i, 0, title[i]));
			}
			int row = 1;
			for (LAccess o : list) {
				sheet.addCell(new Label(0, row, o.getPublications().getTitle()));
				sheet.addCell(new Label(1, row, o.getPublications().getPublisher().getName()));
				sheet.addCell(new Label(2, row, "CNPE"));
				sheet.addCell(new Label(3, row, ""));
				sheet.addCell(new Label(4, row, ""));
				if (!"2,4,6,7".contains(o.getPublications().getType().toString())) {
					sheet.addCell(new Label(5, row, o.getPublications().getCode()));
				} else {
					sheet.addCell(new Label(6, row, o.getPublications().getCode()));
				}
				int y = 1;
				int count = 0;
				for (int i = start; i <= end; i++) {
					if (i == 1) {
						sheet.addCell(new Label((y + 7), row, o.getMonth1() == null ? "0" : o.getMonth1().toString()));
						count += o.getMonth1() == null ? 0 : o.getMonth1();
					}
					if (i == 2) {
						sheet.addCell(new Label((y + 7), row, o.getMonth2() == null ? "0" : o.getMonth2().toString()));
						count += o.getMonth2() == null ? 0 : o.getMonth2();
					}
					if (i == 3) {
						sheet.addCell(new Label((y + 7), row, o.getMonth3() == null ? "0" : o.getMonth3().toString()));
						count += o.getMonth3() == null ? 0 : o.getMonth3();
					}
					if (i == 4) {
						sheet.addCell(new Label((y + 7), row, o.getMonth4() == null ? "0" : o.getMonth4().toString()));
						count += o.getMonth4() == null ? 0 : o.getMonth4();
					}
					if (i == 5) {
						sheet.addCell(new Label((y + 7), row, o.getMonth5() == null ? "0" : o.getMonth5().toString()));
						count += o.getMonth5() == null ? 0 : o.getMonth5();
					}
					if (i == 6) {
						sheet.addCell(new Label((y + 7), row, o.getMonth6() == null ? "0" : o.getMonth6().toString()));
						count += o.getMonth6() == null ? 0 : o.getMonth6();
					}
					if (i == 7) {
						sheet.addCell(new Label((y + 7), row, o.getMonth7() == null ? "0" : o.getMonth7().toString()));
						count += o.getMonth7() == null ? 0 : o.getMonth7();
					}
					if (i == 8) {
						sheet.addCell(new Label((y + 7), row, o.getMonth8() == null ? "0" : o.getMonth8().toString()));
						count += o.getMonth8() == null ? 0 : o.getMonth8();
					}
					if (i == 9) {
						sheet.addCell(new Label((y + 7), row, o.getMonth9() == null ? "0" : o.getMonth9().toString()));
						count += o.getMonth9() == null ? 0 : o.getMonth9();
					}
					if (i == 10) {
						sheet.addCell(new Label((y + 7), row, o.getMonth10() == null ? "0" : o.getMonth10().toString()));
						count += o.getMonth10() == null ? 0 : o.getMonth10();
					}
					if (i == 11) {
						sheet.addCell(new Label((y + 7), row, o.getMonth11() == null ? "0" : o.getMonth11().toString()));
						count += o.getMonth11() == null ? 0 : o.getMonth11();
					}
					if (i == 12) {
						sheet.addCell(new Label((y + 7), row, o.getMonth12() == null ? "0" : o.getMonth12().toString()));
						count += o.getMonth12() == null ? 0 : o.getMonth12();
					}
					y++;
				}
				sheet.addCell(new Label(7, row, String.valueOf(count)));
				row++;
			}

			workbook.write();
			workbook.close();
			os.close();
		} catch (Exception e) {
			forwardString = "mobile/error";
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			return new ModelAndView(forwardString, model);
		}

		return null;
	}

	/**
	 * 下载搜索记录统计
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/downloadLogSearch")
	public ModelAndView downloadLogSearch(HttpServletRequest request, HttpServletResponse response, LAccessForm form) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		String forwardString = "user/downloadLogSearch";
		// form.setUrl(request.getRequestURL().toString());
		List<LAccess> list = null;
		try {
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			if (user != null && (user.getLevel() == 2 || user.getLevel() == 3)) {// 限制为机构管理员或中图管理员可下载
				Map<String, Object> condition = form.getCondition();
				// if(condition.get("year")==null){
				// condition.put("year", StringUtil.formatDate(new
				// Date(),"yyyy"));
				// }
				// form.setYear(condition.get("year").toString());
				// request.setAttribute("year",
				// condition.get("year").toString());
				condition.put("year", form.getYear());
				condition.put("startMonth", form.getStartMonth());
				condition.put("endMonth", form.getEndMonth());
				condition.put("type", 3);
				condition.put("access", 1);
				if (user.getInstitution() != null && user.getLevel() != 3) {
					condition.put("institutionId2", user.getInstitution().getId());
				}
				list = this.logAOPService.getLogOfYearToSearch2(condition, " order by a.publications.id ");
			}
			// model.put("year",form.getYear());
			// model.put("startMonth",Integer.valueOf(form.getStartMonth()));
			// model.put("endMonth",Integer.valueOf(form.getEndMonth()));
			// model.put("form",form);
			// model.put("list",list);

			// 输出的excel文件工作表名
			String worksheet = "Search_Statistics_" + form.getYear();
			// excel工作表的标题
			StringBuffer sb = new StringBuffer();
			sb.append("Title;");
			sb.append("Publisher;");
			sb.append("Platform;");
			sb.append("Book DOI;");
			sb.append("Propriet Identifier;");
			sb.append("ISBN;");
			sb.append("ISSN;");
			sb.append("User activity;");
			sb.append("Reporting Period Total;");
			Integer start = Integer.valueOf(form.getStartMonth());
			Integer end = Integer.valueOf(form.getEndMonth());
			for (int i = start; i <= end; i++) {
				String month = "";
				if (i == 1) {
					month = "Jan";
				}
				if (i == 2) {
					month = "Feb";
				}
				if (i == 3) {
					month = "Mar";
				}
				if (i == 4) {
					month = "Apr";
				}
				if (i == 5) {
					month = "May";
				}
				if (i == 6) {
					month = "Jun";
				}
				if (i == 7) {
					month = "Jul";
				}
				if (i == 8) {
					month = "Aug";
				}
				if (i == 9) {
					month = "Sep";
				}
				if (i == 10) {
					month = "Oct";
				}
				if (i == 11) {
					month = "Nov";
				}
				if (i == 12) {
					month = "Dec";
				}
				sb.append(month + "-" + form.getYear() + ";");
			}
			String title[] = sb.toString().split(";");

			WritableWorkbook workbook;
			OutputStream os = response.getOutputStream();
			response.reset();// 清空输出流
			response.setHeader("Content-disposition", "attachment; filename=" + worksheet + ".xls");// 设定输出文件头
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");// 定义输出类型

			workbook = Workbook.createWorkbook(os);

			WritableSheet sheet = workbook.createSheet(worksheet, 0);

			for (int i = 0; i < title.length; i++) {
				// Label(列号,行号 ,内容 )
				sheet.addCell(new Label(i, 0, title[i]));
			}
			int row = 1;
			for (LAccess o : list) {
				sheet.addCell(new Label(0, row, o.getPublications().getTitle()));
				sheet.addCell(new Label(1, row, o.getPublications().getPublisher().getName()));
				sheet.addCell(new Label(2, row, "CNPE"));
				sheet.addCell(new Label(3, row, ""));
				sheet.addCell(new Label(4, row, ""));
				if (!"2,4,6,7".contains(o.getPublications().getType().toString())) {
					sheet.addCell(new Label(5, row, o.getPublications().getCode()));
				} else {
					sheet.addCell(new Label(6, row, o.getPublications().getCode()));
				}
				sheet.addCell(new Label(7, row, o.getActivity()));
				int y = 1;
				int count = 0;
				for (int i = start; i <= end; i++) {
					if (i == 1) {
						sheet.addCell(new Label((y + 8), row, o.getMonth1() == null ? "0" : o.getMonth1().toString()));
						count += o.getMonth1() == null ? 0 : o.getMonth1();
					}
					if (i == 2) {
						sheet.addCell(new Label((y + 8), row, o.getMonth2() == null ? "0" : o.getMonth2().toString()));
						count += o.getMonth2() == null ? 0 : o.getMonth2();
					}
					if (i == 3) {
						sheet.addCell(new Label((y + 8), row, o.getMonth3() == null ? "0" : o.getMonth3().toString()));
						count += o.getMonth3() == null ? 0 : o.getMonth3();
					}
					if (i == 4) {
						sheet.addCell(new Label((y + 8), row, o.getMonth4() == null ? "0" : o.getMonth4().toString()));
						count += o.getMonth4() == null ? 0 : o.getMonth4();
					}
					if (i == 5) {
						sheet.addCell(new Label((y + 8), row, o.getMonth5() == null ? "0" : o.getMonth5().toString()));
						count += o.getMonth5() == null ? 0 : o.getMonth5();
					}
					if (i == 6) {
						sheet.addCell(new Label((y + 8), row, o.getMonth6() == null ? "0" : o.getMonth6().toString()));
						count += o.getMonth6() == null ? 0 : o.getMonth6();
					}
					if (i == 7) {
						sheet.addCell(new Label((y + 8), row, o.getMonth7() == null ? "0" : o.getMonth7().toString()));
						count += o.getMonth7() == null ? 0 : o.getMonth7();
					}
					if (i == 8) {
						sheet.addCell(new Label((y + 8), row, o.getMonth8() == null ? "0" : o.getMonth8().toString()));
						count += o.getMonth8() == null ? 0 : o.getMonth8();
					}
					if (i == 9) {
						sheet.addCell(new Label((y + 8), row, o.getMonth9() == null ? "0" : o.getMonth9().toString()));
						count += o.getMonth9() == null ? 0 : o.getMonth9();
					}
					if (i == 10) {
						sheet.addCell(new Label((y + 8), row, o.getMonth10() == null ? "0" : o.getMonth10().toString()));
						count += o.getMonth10() == null ? 0 : o.getMonth10();
					}
					if (i == 11) {
						sheet.addCell(new Label((y + 8), row, o.getMonth11() == null ? "0" : o.getMonth11().toString()));
						count += o.getMonth11() == null ? 0 : o.getMonth11();
					}
					if (i == 12) {
						sheet.addCell(new Label((y + 8), row, o.getMonth12() == null ? "0" : o.getMonth12().toString()));
						count += o.getMonth12() == null ? 0 : o.getMonth12();
					}
					y++;
				}
				sheet.addCell(new Label(8, row, String.valueOf(count)));
				row++;
			}
			workbook.write();
			workbook.close();
			os.close();
		} catch (Exception e) {
			forwardString = "mobile/error";
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			return new ModelAndView(forwardString, model);
		}
		return null;
	}

	/**
	 * 下载内容浏览记录
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/downloadLogPage")
	public ModelAndView downloadLogPage(HttpServletRequest request, HttpServletResponse response, LAccessForm form) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		String forwardString = "user/downloadLogPage";
		// form.setUrl(request.getRequestURL().toString());
		List<LAccess> list = null;
		try {
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			if (user != null && (user.getLevel() == 2 || user.getLevel() == 3)) {// 限制为机构管理员或中图管理员可下载
				Map<String, Object> condition = form.getCondition();
				String pubType = request.getParameter("pubtype");
				if (pubType != null && "2".equals(pubType)) {
					condition.put("pubtypes", new Integer[] { 2, 4 });
				} else if (pubType != null && "1".equals(pubType)) {
					condition.put("pubtype", 1);
				}
				condition.put("year", form.getYear());
				condition.put("startMonth", form.getStartMonth());
				condition.put("endMonth", form.getEndMonth());
				condition.put("type", 2);
				condition.put("access", 1);
				if (user.getInstitution() != null && user.getLevel() != 3) {
					condition.put("institutionId2", user.getInstitution().getId());
				}
				list = this.logAOPService.getLogOfYearToPagePaging2(condition, " group by a.publications.id ", 0, 0);// 不分页
			}
			// model.put("year",form.getYear());
			// model.put("startMonth",Integer.valueOf(form.getStartMonth()));
			// model.put("endMonth",Integer.valueOf(form.getEndMonth()));
			// model.put("form",form);
			// model.put("list",list);

			// 输出的excel文件工作表名
			String worksheet = "Browse_Statistics_" + form.getYear();
			// excel工作表的标题
			StringBuffer sb = new StringBuffer();
			sb.append("Title;");
			sb.append("Publisher;");
			sb.append("Platform;");
			sb.append("Book DOI;");
			sb.append("Propriet Identifier;");
			sb.append("ISBN;");
			sb.append("ISSN;");
			sb.append("Reporting Period Total;");
			Integer start = Integer.valueOf(form.getStartMonth());
			Integer end = Integer.valueOf(form.getEndMonth());
			for (int i = start; i <= end; i++) {
				String month = "";
				if (i == 1) {
					month = "Jan";
				}
				if (i == 2) {
					month = "Feb";
				}
				if (i == 3) {
					month = "Mar";
				}
				if (i == 4) {
					month = "Apr";
				}
				if (i == 5) {
					month = "May";
				}
				if (i == 6) {
					month = "Jun";
				}
				if (i == 7) {
					month = "Jul";
				}
				if (i == 8) {
					month = "Aug";
				}
				if (i == 9) {
					month = "Sep";
				}
				if (i == 10) {
					month = "Oct";
				}
				if (i == 11) {
					month = "Nov";
				}
				if (i == 12) {
					month = "Dec";
				}
				sb.append(month + "-" + form.getYear() + ";");
			}
			String title[] = sb.toString().split(";");

			WritableWorkbook workbook;
			OutputStream os = response.getOutputStream();
			response.reset();// 清空输出流
			response.setHeader("Content-disposition", "attachment; filename=" + worksheet + ".xls");// 设定输出文件头
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");// 定义输出类型

			workbook = Workbook.createWorkbook(os);

			WritableSheet sheet = workbook.createSheet(worksheet, 0);

			for (int i = 0; i < title.length; i++) {
				// Label(列号,行号 ,内容 )
				sheet.addCell(new Label(i, 0, title[i]));
			}
			int row = 1;
			for (LAccess o : list) {
				sheet.addCell(new Label(0, row, o.getPublications().getTitle()));
				sheet.addCell(new Label(1, row, o.getPublications().getPublisher().getName()));
				sheet.addCell(new Label(2, row, "CNPE"));
				sheet.addCell(new Label(3, row, ""));
				sheet.addCell(new Label(4, row, ""));
				if (!"2,4,6,7".contains(o.getPublications().getType().toString())) {
					sheet.addCell(new Label(5, row, o.getPublications().getCode()));
				} else {
					sheet.addCell(new Label(6, row, o.getPublications().getCode()));
				}

				int y = 1;
				int count = 0;
				for (int i = start; i <= end; i++) {
					if (i == 1) {
						sheet.addCell(new Label((y + 7), row, o.getMonth1() == null ? "0" : o.getMonth1().toString()));
						count += o.getMonth1() == null ? 0 : o.getMonth1();
					}
					if (i == 2) {
						sheet.addCell(new Label((y + 7), row, o.getMonth2() == null ? "0" : o.getMonth2().toString()));
						count += o.getMonth2() == null ? 0 : o.getMonth2();
					}
					if (i == 3) {
						sheet.addCell(new Label((y + 7), row, o.getMonth3() == null ? "0" : o.getMonth3().toString()));
						count += o.getMonth3() == null ? 0 : o.getMonth3();
					}
					if (i == 4) {
						sheet.addCell(new Label((y + 7), row, o.getMonth4() == null ? "0" : o.getMonth4().toString()));
						count += o.getMonth4() == null ? 0 : o.getMonth4();
					}
					if (i == 5) {
						sheet.addCell(new Label((y + 7), row, o.getMonth5() == null ? "0" : o.getMonth5().toString()));
						count += o.getMonth5() == null ? 0 : o.getMonth5();
					}
					if (i == 6) {
						sheet.addCell(new Label((y + 7), row, o.getMonth6() == null ? "0" : o.getMonth6().toString()));
						count += o.getMonth6() == null ? 0 : o.getMonth6();
					}
					if (i == 7) {
						sheet.addCell(new Label((y + 7), row, o.getMonth7() == null ? "0" : o.getMonth7().toString()));
						count += o.getMonth7() == null ? 0 : o.getMonth7();
					}
					if (i == 8) {
						sheet.addCell(new Label((y + 7), row, o.getMonth8() == null ? "0" : o.getMonth8().toString()));
						count += o.getMonth8() == null ? 0 : o.getMonth8();
					}
					if (i == 9) {
						sheet.addCell(new Label((y + 7), row, o.getMonth9() == null ? "0" : o.getMonth9().toString()));
						count += o.getMonth9() == null ? 0 : o.getMonth9();
					}
					if (i == 10) {
						sheet.addCell(new Label((y + 7), row, o.getMonth10() == null ? "0" : o.getMonth10().toString()));
						count += o.getMonth10() == null ? 0 : o.getMonth10();
					}
					if (i == 11) {
						sheet.addCell(new Label((y + 7), row, o.getMonth11() == null ? "0" : o.getMonth11().toString()));
						count += o.getMonth11() == null ? 0 : o.getMonth11();
					}
					if (i == 12) {
						sheet.addCell(new Label((y + 7), row, o.getMonth12() == null ? "0" : o.getMonth12().toString()));
						count += o.getMonth12() == null ? 0 : o.getMonth12();
					}
					y++;
				}
				sheet.addCell(new Label(7, row, String.valueOf(count)));
				row++;
			}
			workbook.write();
			workbook.close();
			os.close();
		} catch (Exception e) {
			forwardString = "mobile/error";
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			return new ModelAndView(forwardString, model);
		}
		return null;
	}

	/**
	 * 下载tocexcel记录
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/downloadToc")
	public ModelAndView downloadToc(HttpServletRequest request, HttpServletResponse response, SSupplierForm form) throws Exception {
		try {
			List<SSupplier> list = null;
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			if (user != null && (user.getLevel() == 2 || user.getLevel() == 3)) {// 限制为机构管理员或中图管理员可下载
				if (user.getInstitution().getId() != null && !"".equals(user.getInstitution().getId())) {// 机构ID不为NULL
					form.setUrl(request.getRequestURL().toString());
					String pubType = "";// 1-图书2-期刊
					Integer languagetype = null;// 1-中文图书统计;2-外文图书统计
					if (request.getParameter("pubtype") != null && !"".equals(request.getParameter("pubtype"))) {
						pubType = request.getParameter("pubtype");// 1-图书2-期刊
						form.setPubType(pubType);
					} else {
						pubType = form.getPubType();// 1-图书2-期刊
					}
					Date date = new Date();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM");
					String dateString = formatter.format(date);// 当前年
					dateString = dateString + "-" + "01";
					String dateyear = sdf.format(date);// 当前年月
					String eyear = sdf1.format(date);// 当前年
					String staryear = form.getStartyear() != null && !"".equals(form.getStartyear()) ? form.getStartyear() : dateString;
					String endyear = form.getEndtyear() != null && !"".equals(form.getEndtyear()) ? form.getEndtyear() : eyear;

					Map<String, Object> condition = new HashMap<String, Object>();
					condition.put("institutionId", user.getInstitution().getId());// 机构ID
					condition.put("staryear", staryear);// 起始年月 2013-01
					condition.put("endyear", endyear);// 结束年月2013-12
					condition.put("tocON", 0);// 查询出toc统计数大于0
					if (!"2".equals(pubType)) {
						if (form.getLanguagetype() == null) {// 1-中文图书统计;2-外文图书统计
							languagetype = Integer.valueOf(request.getParameter("languagetype"));
						} else {
							languagetype = form.getLanguagetype();
						}
					}
					if (pubType != null && "1".equals(pubType)) {// 1-图书2-期刊
						condition.put("pubtypes", new Integer[] { 1, 3 });
					} else if (pubType != null && "2".equals(pubType)) {
						condition.put("pubtypes", new Integer[] { 2, 4 });
					}
					if (!"2".equals(pubType)) {
						if (languagetype == 1) {// 1-中文图书统计;2-外文图书统计
							condition.put("isPrecisebook", true);
						} else if (languagetype == 2) {
							condition.put("isPrecisebook", false);
						}
					}
					condition.put("lang", new String[] { "chs", "cht" });
					Map<String, Object> xMap = new LinkedHashMap<String, Object>();// X
																					// 轴坐标
																					// key-
																					// contentID
																					// value-
																					// x
					Map<String, Object> yMap = new LinkedHashMap<String, Object>();// Y
																					// 轴坐标
																					// key-
																					// 月-年
																					// value-
																					// y
					Map<String, Object> tocMap = new LinkedHashMap<String, Object>();// 结果集
																						// 总数
																						// -每月分别统计总数
					String[] str = { "", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };// 月份数组

					list = this.bookSuppliersService.getSuppList(condition, " order by a.pubId,a.year,a.month ");

					int flag = 0;// 1-图书 2-期刊 0-初始值
					int numX = 9;// 行号
					int numY = 7;// 列号
					int count = 0;
					/* 生成excel start heqing.yang 2014-11-02 */
					String time = StatisticsBookSuppliersController.getNowbatchCooe();
					// 输出的excel文件工作表名
					String worksheet = "";
					yMap.put("count", numY);
					StringBuffer sbb = new StringBuffer();/*
															 * 生成excel start
															 * heqing.yang
															 * 2014-11-02
															 */
					if (pubType != null && "2".equals(pubType)) {
						worksheet = "JR-C7.CNPEADING." + time;
						sbb.append("Journal Report 1 (R4);");
						flag = 2;
					} else if (pubType != null && "1".equals(pubType)) {
						worksheet = "BR-C7.CNPEADING." + time;
						sbb.append("Book Report 1 (R4);");
						flag = 1;
					}
					sbb.append(" ;");
					sbb.append(" ;");
					sbb.append("Period covered by Report:;");
					sbb.append("yyyy-mm-dd to yyyy-mm-dd;");
					sbb.append("Date run:;");
					sbb.append("yyyy-mm-dd;");

					StringBuffer sb = new StringBuffer();
					if (flag == 2) {
						sb.append("Journal;");
						sb.append("Publisher;");
						sb.append("Platform;");
						sb.append("Journal DOI;");
						sb.append("Proprietary Identifier;");

						sb.append("Print ISSN;");
						sb.append("Online ISSN;");
						sb.append("Reporting Period Total;");
					} else if (flag == 1) {
						sb.append(" ;");
						sb.append("Publisher;");
						sb.append("Platform;");
						sb.append("Book DOI;");
						sb.append("Proprietary Identifier;");
						sb.append("ISBN;");
						sb.append("ISSN;");
						sb.append("Reporting Period Total;");
					}

					// 2013-01 2014-09
					String[] sy = staryear.split("-");
					String[] ey = endyear.split("-");
					Integer start = Integer.parseInt(sy[0]);
					Integer end = Integer.parseInt(ey[0]);
					int sTartMonth = 1;
					int eNdMonth = 12;
					for (int j = start; j <= end; j++) {

						if (sTartMonth != Integer.parseInt(sy[1])) {
							sTartMonth = Integer.parseInt(sy[1]);
						} else {
							sTartMonth = 1;
						}
						if (j == end && eNdMonth != Integer.parseInt(ey[1])) {
							eNdMonth = Integer.parseInt(ey[1]);
						}
						for (int k = sTartMonth; k <= eNdMonth; k++) {
							String month = "";
							numY++;// Y轴
							month = str[k];// 月份一维数组
							sb.append(month + "-" + j + ";");
							String dete = String.valueOf(k);
							if (dete.length() == 1) {
								dete = "0" + dete;
							}
							yMap.put(j + "-" + dete, numY);
						}
					}

					StringBuffer sbf = new StringBuffer();
					if (flag == 1) {
						sbf.append("Total for all titles;");
					} else if (flag == 2) {
						sbf.append("Total for all journals;");
					}

					sbf.append(" ;");
					if (flag == 1) {
						sbf.append(" ;");
					} else if (flag == 2) {
						sbf.append("Platform Z ;");
					}

					sbf.append(" ;");
					sbf.append(" ;");
					sbf.append(" ;");
					sbf.append(" ;");
					sbf.append(" ;");
					sbf.append(" ;");// 总数列

					String title[] = sb.toString().split(";");
					String titles[] = sbb.toString().split(";");
					String titsbf[] = sbf.toString().split(";");
					WritableWorkbook workbook;
					OutputStream os = response.getOutputStream();
					response.reset();// 清空输出流
					response.setHeader("Content-disposition", "attachment; filename=" + worksheet + ".xls");// 设定输出文件头
					response.setContentType("application/vnd.ms-excel;charset=UTF-8");// 定义输出类型
					workbook = Workbook.createWorkbook(os);
					WritableSheet sheet = workbook.createSheet(worksheet, 0);

					int num = 0;
					for (int i = 0; i < titles.length; i++) {
						sheet.addCell(new Label(0, i, titles[i]));
						num++;
					}
					for (int i = 0; i < num; i++) {
						if (i == 0) {
							if (pubType != null && "2".equals(pubType)) {
								sheet.addCell(new Label(1, i, "Number of Successfull Full-Text Article Requests by Month and Journal"));
							} else if (pubType != null && "1".equals(pubType)) {
								sheet.addCell(new Label(1, i, "Number of Successfull T	Title Requests by Month and Title"));
							}

						} else if (i == 1) {
							sheet.addCell(new Label(1, i, " "));
						} else if (i == 2) {
							sheet.addCell(new Label(1, i, " "));
						} else if (i == 3) {
							sheet.addCell(new Label(1, i, "Cnpereading.com"));
						} else if (i == 4) {
							sheet.addCell(new Label(1, i, dateyear));// 条件值
						} else if (i == 5) {
							sheet.addCell(new Label(1, i, " "));
						} else if (i == 6) {
							String s = StatisticsBookSuppliersController.getNowbatchCooe();
							sheet.addCell(new Label(1, i, s));
						}
					}

					for (int i = 0; i < title.length; i++) {

						// Label(列号,行号 ,内容 )
						sheet.addCell(new Label(i, num, title[i]));
					}
					for (int i = 0; i < titsbf.length; i++) {

						// Label(列号,行号 ,内容 )
						sheet.addCell(new Label(i, num + 1, titsbf[i]));
					}
					int counta = 0;// 统计大总数
					int countb = 0;// 统计每月大总数
					if (list != null && list.size() > 0) {
						for (int i = 0; i < list.size(); i++) {
							if (xMap != null && xMap.containsKey(list.get(i).getPubId())) {

								count += list.get(i).getToc() != null && !"".equals(list.get(i).getToc()) ? list.get(i).getToc() : 0;
								counta = list.get(i).getToc() != null && !"".equals(list.get(i).getToc()) ? list.get(i).getToc() : 0;
								countb = list.get(i).getToc() != null && !"".equals(list.get(i).getToc()) ? list.get(i).getToc() : 0;
								if (tocMap.containsKey(list.get(i).getSdate())) {// 存在相同月分的值相加
																					// 每月一列总计
									tocMap.put(list.get(i).getSdate(), Integer.parseInt(tocMap.get(list.get(i).getSdate()).toString()) + countb);
								} else {
									tocMap.put(list.get(i).getSdate(), list.get(i).getToc() != null && !"".equals(list.get(i).getToc()) ? list.get(i).getToc() : 0);
								}
								tocMap.put("tocCount", Integer.parseInt(tocMap.get("tocCount").toString()) + counta);// 总数

								sheet.addCell(new Label(Integer.parseInt(yMap.get(list.get(i).getSdate()).toString()), Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), String.valueOf(list.get(i).getToc() != null && !"".equals(list.get(i).getToc()) ? list.get(i).getToc() : 0)));
								sheet.addCell(new Label(Integer.parseInt(yMap.get("count").toString()), Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), String.valueOf(count)));
							} else {
								count = 0;
								xMap.put(list.get(i).getPubId(), numX);// 行号
								for (int y = 1; y < yMap.size(); y++) {// 初始值默认为0

									sheet.addCell(new Label(7 + y, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), "0"));
								}
								// 生成excel
								sheet.addCell(new Label(0, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getTitle() != null ? list.get(i).getTitle() : ""));
								sheet.addCell(new Label(1, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getPubName() != null ? list.get(i).getPubName() : ""));
								sheet.addCell(new Label(2, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getPlatform() != null ? list.get(i).getPlatform() : ""));
								sheet.addCell(new Label(3, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), ""));
								sheet.addCell(new Label(4, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), ""));
								if (flag == 1) {
									sheet.addCell(new Label(5, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getIsbn() != null ? list.get(i).getIsbn() : ""));
									sheet.addCell(new Label(6, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getIssn() != null ? list.get(i).getIssn() : ""));
								} else if (flag == 2) {
									sheet.addCell(new Label(5, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getIssn() != null ? list.get(i).getIssn() : ""));
									sheet.addCell(new Label(6, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getEissn() != null ? list.get(i).getEissn() : ""));
								}
								sheet.addCell(new Label(Integer.parseInt(yMap.get(list.get(i).getSdate()).toString()), Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), String.valueOf(list.get(i).getToc() != null && !"".equals(list.get(i).getToc()) ? list.get(i).getToc() : 0)));
								count += list.get(i).getToc() != null && !"".equals(list.get(i).getToc()) ? list.get(i).getToc() : 0;
								counta = list.get(i).getToc() != null && !"".equals(list.get(i).getToc()) ? list.get(i).getToc() : 0;// 每月总数大统计
								countb = list.get(i).getToc() != null && !"".equals(list.get(i).getToc()) ? list.get(i).getToc() : 0;
								if (tocMap.containsKey("tocCount")) {
									tocMap.put("tocCount", Integer.parseInt(tocMap.get("tocCount").toString()) + counta);// 总数
								} else {
									tocMap.put("tocCount", counta);// 总数
								}

								if (tocMap.containsKey(list.get(i).getSdate())) {// 存在相同月分的值相加
									tocMap.put(list.get(i).getSdate(), Integer.parseInt(tocMap.get(list.get(i).getSdate()).toString()) + countb);
								} else {
									tocMap.put(list.get(i).getSdate(), countb);
								}
								numX++;
							}
							sheet.addCell(new Label(Integer.parseInt(yMap.get(list.get(i).getSdate()).toString()), 8, tocMap.get(list.get(i).getSdate()).toString()));// 每月大总数
							sheet.addCell(new Label(Integer.parseInt(yMap.get("count").toString()), 8, tocMap.get("tocCount").toString()));// 大总数
							sheet.addCell(new Label(Integer.parseInt(yMap.get("count").toString()), Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), String.valueOf(count)));
						}
					}
					workbook.write();
					workbook.close();
					os.close();

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return null;
	}

	/**
	 * 下载Searchexcel记录
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/downloadSearch")
	public ModelAndView downloadSearch(HttpServletRequest request, HttpServletResponse response, SSupplierForm form) throws Exception {
		try {
			List<SSupplier> list = null;
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			if (user != null && (user.getLevel() == 2 || user.getLevel() == 3)) {// 限制为机构管理员或中图管理员可下载
				if (user.getInstitution().getId() != null && !"".equals(user.getInstitution().getId())) {// 机构ID不为NULL
					form.setUrl(request.getRequestURL().toString());
					String pubType = "";// 1-图书2-期刊
					Integer languagetype = null;// 1-中文图书统计;2-外文图书统计
					if (request.getParameter("pubtype") != null && !"".equals(request.getParameter("pubtype"))) {
						pubType = request.getParameter("pubtype");// 1-图书2-期刊
						form.setPubType(pubType);
					} else {
						pubType = form.getPubType();// 1-图书2-期刊
					}
					Date date = new Date();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM");
					String dateString = formatter.format(date);// 当前年
					dateString = dateString + "-" + "01";
					String dateyear = sdf.format(date);// 当前年月
					String eyear = sdf1.format(date);// 当前年
					String staryear = form.getStartyear() != null && !"".equals(form.getStartyear()) ? form.getStartyear() : dateString;
					String endyear = form.getEndtyear() != null && !"".equals(form.getEndtyear()) ? form.getEndtyear() : eyear;

					Map<String, Object> condition = new HashMap<String, Object>();
					condition.put("institutionId", user.getInstitution().getId());// 机构ID
					condition.put("staryear", staryear);// 起始年月 2013-01
					condition.put("endyear", endyear);// 结束年月2013-12
					condition.put("searchON", 0);// 查询出search统计数大于0
					if (!"2".equals(pubType)) {
						if (form.getLanguagetype() == null) {// 1-中文图书统计;2-外文图书统计
							languagetype = Integer.valueOf(request.getParameter("languagetype"));
						} else {
							languagetype = form.getLanguagetype();
						}
					}
					if (pubType != null && "1".equals(pubType)) {// 1-图书2-期刊
						condition.put("pubtypes", new Integer[] { 1, 3 });
					} else if (pubType != null && "2".equals(pubType)) {
						condition.put("pubtypes", new Integer[] { 2, 4 });
					}
					if (!"2".equals(pubType)) {
						if (languagetype == 1) {// 1-中文图书统计;2-外文图书统计
							condition.put("isPrecisebook", true);
						} else if (languagetype == 2) {
							condition.put("isPrecisebook", false);
						}
					}
					condition.put("lang", new String[] { "chs", "cht" });
					Map<String, Object> xMap = new LinkedHashMap<String, Object>();// X
																					// 轴坐标
																					// key-
																					// contentID
																					// value-
																					// x
					Map<String, Object> yMap = new LinkedHashMap<String, Object>();// Y
																					// 轴坐标
																					// key-
																					// 月-年
																					// value-
																					// y
					Map<String, Object> searchMap = new LinkedHashMap<String, Object>();// 结果集
																						// 总数
																						// -每月分别统计总数
					String[] str = { "", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };// 月份数组

					list = this.bookSuppliersService.getSuppList(condition, " order by a.pubId,a.year,a.month ");

					int flag = 0;// 1-图书 2-期刊 0-初始值
					int numX = 9;// 行号
					int numY = 8;// 列号
					int count = 0;
					/* 生成excel start heqing.yang 2014-11-02 */
					String time = StatisticsBookSuppliersController.getNowbatchCooe();
					// 输出的excel文件工作表名
					String worksheet = "";
					yMap.put("count", numY);
					StringBuffer sbb = new StringBuffer();
					if (pubType != null && "2".equals(pubType)) {
						worksheet = "JR5.CNPEREADING" + time;
						sbb.append("Journal Report 5 (R4);");
						flag = 2;
					} else if (pubType != null && "1".equals(pubType)) {
						worksheet = "BR5.CNPEREADING" + time;
						sbb.append("Book Report 5 (R4);");
						flag = 1;
					}
					sbb.append(" ;");
					sbb.append(" ;");
					sbb.append("Period covered by Report:;");
					sbb.append("yyyy-mm-dd to yyyy-mm-dd;");
					sbb.append("Date run:;");
					sbb.append("yyyy-mm-dd;");

					StringBuffer sb = new StringBuffer();
					if (flag == 2) {
						sb.append("Journal;");
						sb.append("Publisher;");
						sb.append("Platform;");
						sb.append("Journal DOI;");
						sb.append("Proprietary Identifier;");

						sb.append("Print ISSN;");
						sb.append("Online ISSN;");
						sb.append("User activity;");
						sb.append("Reporting Period Total;");
					} else if (flag == 1) {
						sb.append(" ;");
						sb.append("Publisher;");
						sb.append("Platform;");
						sb.append("Book DOI;");
						sb.append("Proprietary Identifier;");
						sb.append("ISBN;");
						sb.append("ISSN;");
						sb.append("User activity;");
						sb.append("Reporting Period Total;");
					}
					// 2013-01 2014-09
					String[] sy = staryear.split("-");
					String[] ey = endyear.split("-");
					Integer start = Integer.parseInt(sy[0]);
					Integer end = Integer.parseInt(ey[0]);
					int sTartMonth = 1;
					int eNdMonth = 12;
					for (int j = start; j <= end; j++) {

						if (sTartMonth != Integer.parseInt(sy[1])) {
							sTartMonth = Integer.parseInt(sy[1]);
						} else {
							sTartMonth = 1;
						}
						if (j == end) {
							if (eNdMonth != Integer.parseInt(ey[1])) {
								eNdMonth = Integer.parseInt(ey[1]);
							}
						}
						for (int k = sTartMonth; k <= eNdMonth; k++) {
							String month = "";
							numY++;// Y轴
							month = str[k];// 月份一维数组
							sb.append(month + "-" + j + ";");
							String dete = String.valueOf(k);
							if (dete.length() == 1) {
								dete = "0" + dete;
							}
							yMap.put(j + "-" + dete, numY);
						}
					}

					StringBuffer sbf = new StringBuffer();
					if (flag == 1) {
						sbf.append("Total searches;");
					} else if (flag == 2) {
						sbf.append("Total searches journals;");
					}

					sbf.append(" ;");
					if (flag == 1) {
						sbf.append(" ;");
					} else if (flag == 2) {
						sbf.append("Platform Z ;");
					}

					sbf.append(" ;");
					sbf.append(" ;");
					sbf.append(" ;");
					sbf.append(" ;");
					sbf.append(" ;");
					sbf.append(" ;");// 总数列

					String title[] = sb.toString().split(";");
					String titles[] = sbb.toString().split(";");
					String titsbf[] = sbf.toString().split(";");
					WritableWorkbook workbook;
					OutputStream os = response.getOutputStream();
					response.reset();// 清空输出流
					response.setHeader("Content-disposition", "attachment; filename=" + worksheet + ".xls");// 设定输出文件头
					response.setContentType("application/vnd.ms-excel;charset=UTF-8");// 定义输出类型
					workbook = Workbook.createWorkbook(os);
					WritableSheet sheet = workbook.createSheet(worksheet, 0);

					int num = 0;
					for (int i = 0; i < titles.length; i++) {
						sheet.addCell(new Label(0, i, titles[i]));
						num++;
					}
					for (int i = 0; i < num; i++) {
						if (i == 0) {
							if (pubType != null && "2".equals(pubType)) {
								sheet.addCell(new Label(1, i, "Total Searches by Month and Title"));
							} else if (pubType != null && "1".equals(pubType)) {
								sheet.addCell(new Label(1, i, "Total Searches by Month and Title"));
							}

						} else if (i == 1) {
							sheet.addCell(new Label(1, i, " "));
						} else if (i == 2) {
							sheet.addCell(new Label(1, i, " "));
						} else if (i == 3) {
							sheet.addCell(new Label(1, i, "Cnpereading.com"));
						} else if (i == 4) {
							sheet.addCell(new Label(1, i, dateyear));// 条件值
						} else if (i == 5) {
							sheet.addCell(new Label(1, i, " "));
						} else if (i == 6) {
							String s = StatisticsBookSuppliersController.getNowbatchCooe();
							sheet.addCell(new Label(1, i, s));
						}
					}

					for (int i = 0; i < title.length; i++) {

						// Label(列号,行号 ,内容 )
						sheet.addCell(new Label(i, num, title[i]));
					}
					for (int i = 0; i < titsbf.length; i++) {

						// Label(列号,行号 ,内容 )
						sheet.addCell(new Label(i, num + 1, titsbf[i]));
					}

					int counta = 0;// 统计大总数
					int countb = 0;// 统计每月大总数
					if (list != null && list.size() > 0) {
						for (int i = 0; i < list.size(); i++) {
							if (xMap != null && xMap.containsKey(list.get(i).getPubId())) {

								count += list.get(i).getSearch() != null && !"".equals(list.get(i).getSearch()) ? list.get(i).getSearch() : 0;
								counta = list.get(i).getSearch() != null && !"".equals(list.get(i).getSearch()) ? list.get(i).getSearch() : 0;
								countb = list.get(i).getSearch() != null && !"".equals(list.get(i).getSearch()) ? list.get(i).getSearch() : 0;
								if (searchMap.containsKey(list.get(i).getSdate())) {// 存在相同月分的值相加
																					// 每月一列总计
									searchMap.put(list.get(i).getSdate(), Integer.parseInt(searchMap.get(list.get(i).getSdate()).toString()) + countb);
								} else {
									searchMap.put(list.get(i).getSdate(), list.get(i).getSearch() != null && !"".equals(list.get(i).getSearch()) ? list.get(i).getSearch() : 0);
								}
								searchMap.put("searchCount", Integer.parseInt(searchMap.get("searchCount").toString()) + counta);// 总数
								sheet.addCell(new Label(Integer.parseInt(yMap.get(list.get(i).getSdate()).toString()), Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), String.valueOf(list.get(i).getSearch() != null && !"".equals(list.get(i).getSearch()) ? list.get(i).getSearch() : 0)));
								sheet.addCell(new Label(Integer.parseInt(yMap.get("count").toString()), Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), String.valueOf(count)));
							} else {
								count = 0;
								xMap.put(list.get(i).getPubId(), numX);// 行号
								for (int y = 1; y < yMap.size(); y++) {// 初始值默认为0

									sheet.addCell(new Label(8 + y, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), "0"));
								}
								// 生成excel
								sheet.addCell(new Label(0, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getTitle() != null ? list.get(i).getTitle() : ""));
								sheet.addCell(new Label(1, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getPubName() != null ? list.get(i).getPubName() : ""));
								sheet.addCell(new Label(2, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getPlatform() != null ? list.get(i).getPlatform() : ""));
								sheet.addCell(new Label(3, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), ""));
								sheet.addCell(new Label(4, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), ""));
								if (flag == 1) {
									sheet.addCell(new Label(5, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getIsbn() != null ? list.get(i).getIsbn() : ""));
									sheet.addCell(new Label(6, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getIssn() != null ? list.get(i).getIssn() : ""));
								} else if (flag == 2) {
									sheet.addCell(new Label(5, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getIssn() != null ? list.get(i).getIssn() : ""));
									sheet.addCell(new Label(6, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getEissn() != null ? list.get(i).getEissn() : ""));
								}
								sheet.addCell(new Label(7, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), "Regular Searches"));
								sheet.addCell(new Label(Integer.parseInt(yMap.get(list.get(i).getSdate()).toString()), Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), String.valueOf(list.get(i).getSearch() != null && !"".equals(list.get(i).getSearch()) ? list.get(i).getSearch() : 0)));
								count += list.get(i).getSearch() != null && !"".equals(list.get(i).getSearch()) ? list.get(i).getSearch() : 0;
								counta = list.get(i).getSearch() != null && !"".equals(list.get(i).getSearch()) ? list.get(i).getSearch() : 0;// 每月总数大统计
								countb = list.get(i).getSearch() != null && !"".equals(list.get(i).getSearch()) ? list.get(i).getSearch() : 0;
								if (searchMap.containsKey("searchCount")) {
									searchMap.put("searchCount", Integer.parseInt(searchMap.get("searchCount").toString()) + counta);// 总数
								} else {
									searchMap.put("searchCount", counta);// 总数
								}
								if (searchMap.containsKey(list.get(i).getSdate())) {// 存在相同月分的值相加
									searchMap.put(list.get(i).getSdate(), Integer.parseInt(searchMap.get(list.get(i).getSdate()).toString()) + countb);
								} else {
									searchMap.put(list.get(i).getSdate(), countb);
								}
								numX++;
							}
							sheet.addCell(new Label(Integer.parseInt(yMap.get(list.get(i).getSdate()).toString()), 8, searchMap.get(list.get(i).getSdate()).toString()));// 每月大总数
							sheet.addCell(new Label(Integer.parseInt(yMap.get("count").toString()), 8, searchMap.get("searchCount").toString()));// 大总数
							sheet.addCell(new Label(Integer.parseInt(yMap.get("count").toString()), Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), String.valueOf(count)));
						}

					}
					workbook.write();
					workbook.close();
					os.close();

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return null;
	}

	/**
	 * 下载全文访问excel记录
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/downloadAccess")
	public ModelAndView downloadAccess(HttpServletRequest request, HttpServletResponse response, SSupplierForm form) throws Exception {
		try {
			List<SSupplier> list = null;
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			if (user != null && (user.getLevel() == 2 || user.getLevel() == 3)) {// 限制为机构管理员或中图管理员可下载
				if (user.getInstitution().getId() != null && !"".equals(user.getInstitution().getId())) {// 机构ID不为NULL
					form.setUrl(request.getRequestURL().toString());
					String pubType = "";// 1-图书2-期刊
					Integer languagetype = null;// 1-中文图书统计;2-外文图书统计
					if (request.getParameter("pubtype") != null && !"".equals(request.getParameter("pubtype"))) {
						pubType = request.getParameter("pubtype");// 1-图书2-期刊
						form.setPubType(pubType);
					} else {
						pubType = form.getPubType();// 1-图书2-期刊
					}
					Date date = new Date();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM");
					String dateString = formatter.format(date);// 当前年
					dateString = dateString + "-" + "01";
					String dateyear = sdf.format(date);// 当前年月
					String eyear = sdf1.format(date);// 当前年
					String staryear = form.getStartyear() != null && !"".equals(form.getStartyear()) ? form.getStartyear() : dateString;
					String endyear = form.getEndtyear() != null && !"".equals(form.getEndtyear()) ? form.getEndtyear() : eyear;

					Map<String, Object> condition = new HashMap<String, Object>();
					condition.put("institutionId", user.getInstitution().getId());// 机构ID
					condition.put("staryear", staryear);// 起始年月 2013-01
					condition.put("endyear", endyear);// 结束年月2013-12
					condition.put("accessON", 0);// 查询出全文统计数大于0
					if (!"2".equals(pubType)) {
						if (form.getLanguagetype() == null) {// 1-中文图书统计;2-外文图书统计
							languagetype = Integer.valueOf(request.getParameter("languagetype"));
						} else {
							languagetype = form.getLanguagetype();
						}
					}
					if (pubType != null && "1".equals(pubType)) {// 1-图书2-期刊
						condition.put("pubtypes", new Integer[] { 1, 3 });
					} else if (pubType != null && "2".equals(pubType)) {
						condition.put("pubtypes", new Integer[] { 2, 4 });
					}
					if (!"2".equals(pubType)) {
						if (languagetype == 1) {// 1-中文图书统计;2-外文图书统计
							condition.put("isPrecisebook", true);
						} else if (languagetype == 2) {
							condition.put("isPrecisebook", false);
						}
					}
					condition.put("lang", new String[] { "chs", "cht" });
					Map<String, Object> xMap = new LinkedHashMap<String, Object>();// X
																					// 轴坐标
																					// key-
																					// contentID
																					// value-
																					// x
					Map<String, Object> yMap = new LinkedHashMap<String, Object>();// Y
																					// 轴坐标
																					// key-
																					// 月-年
																					// value-
																					// y
					Map<String, Object> accessMap = new LinkedHashMap<String, Object>();// 结果集
																						// 总数
																						// -每月分别统计总数
					String[] str = { "", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };// 月份数组

					list = this.bookSuppliersService.getSuppList(condition, " order by a.pubId,a.year,a.month ");

					int flag = 0;// 1-图书 2-期刊 0-初始值
					int numX = 9;// 行号
					int numY = 7;// 列号
					int count = 0;
					/* 生成excel start heqing.yang 2014-11-02 */
					String time = StatisticsBookSuppliersController.getNowbatchCooe();
					// 输出的excel文件工作表名
					String worksheet = "";
					yMap.put("count", numY);
					StringBuffer sbb = new StringBuffer();/*
															 * 生成excel start
															 * heqing.yang
															 * 2014-11-02
															 */
					if (pubType != null && "2".equals(pubType)) {
						worksheet = "JR1.CNPEADING." + time;
						sbb.append("Journal Report 1 (R4);");
						flag = 2;
					} else if (pubType != null && "1".equals(pubType)) {
						worksheet = "BR1.CNPEADING." + time;
						sbb.append("Book Report 1 (R4);");
						flag = 1;
					}
					sbb.append(" ;");
					sbb.append(" ;");
					sbb.append("Period covered by Report:;");
					sbb.append("yyyy-mm-dd to yyyy-mm-dd;");
					sbb.append("Date run:;");
					sbb.append("yyyy-mm-dd;");

					StringBuffer sb = new StringBuffer();
					if (flag == 2) {
						numY++;
						yMap.put("Pdf", Integer.valueOf(numY));
						sb.append("Journal;");
						sb.append("Publisher;");
						sb.append("Platform;");
						sb.append("Journal DOI;");
						sb.append("Proprietary Identifier;");

						sb.append("Print ISSN;");
						sb.append("Online ISSN;");
						sb.append("Reporting Period Total;");
						sb.append("Reporting Period PDF;");
					} else if (flag == 1) {
						sb.append(" ;");
						sb.append("Publisher;");
						sb.append("Platform;");
						sb.append("Book DOI;");
						sb.append("Proprietary Identifier;");
						sb.append("ISBN;");
						sb.append("ISSN;");
						sb.append("Reporting Period Total;");
					}

					// 2013-01 2014-09
					String[] sy = staryear.split("-");
					String[] ey = endyear.split("-");
					Integer start = Integer.parseInt(sy[0]);
					Integer end = Integer.parseInt(ey[0]);
					int sTartMonth = 1;
					int eNdMonth = 12;
					for (int j = start; j <= end; j++) {

						if (sTartMonth != Integer.parseInt(sy[1])) {
							sTartMonth = Integer.parseInt(sy[1]);
						} else {
							sTartMonth = 1;
						}
						if (j == end && eNdMonth != Integer.parseInt(ey[1])) {
							eNdMonth = Integer.parseInt(ey[1]);
						}
						for (int k = sTartMonth; k <= eNdMonth; k++) {
							String month = "";
							numY++;// Y轴
							month = str[k];// 月份一维数组
							sb.append(month + "-" + j + ";");
							String dete = String.valueOf(k);
							if (dete.length() == 1) {
								dete = "0" + dete;
							}
							yMap.put(j + "-" + dete, numY);
						}

					}

					StringBuffer sbf = new StringBuffer();
					if (flag == 1) {
						sbf.append("Total for all titles;");
					} else if (flag == 2) {
						sbf.append("Total for all journals;");
					}

					sbf.append(" ;");
					if (flag == 1) {
						sbf.append(" ;");
					} else if (flag == 2) {
						sbf.append("Platform Z ;");
					}

					sbf.append(" ;");
					sbf.append(" ;");
					sbf.append(" ;");
					sbf.append(" ;");
					sbf.append(" ;");
					sbf.append(" ;");// 总数列
					if (flag == 2) {
						sbf.append(" ;");
					}

					String title[] = sb.toString().split(";");
					String titles[] = sbb.toString().split(";");
					String titsbf[] = sbf.toString().split(";");
					WritableWorkbook workbook;
					OutputStream os = response.getOutputStream();
					response.reset();// 清空输出流
					response.setHeader("Content-disposition", "attachment; filename=" + worksheet + ".xls");// 设定输出文件头
					response.setContentType("application/vnd.ms-excel;charset=UTF-8");// 定义输出类型
					workbook = Workbook.createWorkbook(os);
					WritableSheet sheet = workbook.createSheet(worksheet, 0);

					int num = 0;
					for (int i = 0; i < titles.length; i++) {
						sheet.addCell(new Label(0, i, titles[i]));
						num++;
					}
					for (int i = 0; i < num; i++) {
						if (i == 0) {
							if (pubType != null && "2".equals(pubType)) {
								sheet.addCell(new Label(1, i, "Number of Successfull Full-Text Article Requests by Month and Journal"));
							} else if (pubType != null && "1".equals(pubType)) {
								sheet.addCell(new Label(1, i, "Number of Successfull T	Title Requests by Month and Title"));
							}

						} else if (i == 1) {
							sheet.addCell(new Label(1, i, " "));
						} else if (i == 2) {
							sheet.addCell(new Label(1, i, " "));
						} else if (i == 3) {
							sheet.addCell(new Label(1, i, "Cnpereading.com"));
						} else if (i == 4) {
							sheet.addCell(new Label(1, i, dateyear));// 条件值
						} else if (i == 5) {
							sheet.addCell(new Label(1, i, " "));
						} else if (i == 6) {
							String s = StatisticsBookSuppliersController.getNowbatchCooe();
							sheet.addCell(new Label(1, i, s));
						}
					}

					for (int i = 0; i < title.length; i++) {

						// Label(列号,行号 ,内容 )
						sheet.addCell(new Label(i, num, title[i]));
					}
					for (int i = 0; i < titsbf.length; i++) {

						// Label(列号,行号 ,内容 )
						sheet.addCell(new Label(i, num + 1, titsbf[i]));
					}
					int counta = 0;// 统计大总数
					int countb = 0;// 统计每月大总数
					int countc = 0;
					int countd = 0;
					if (list != null && list.size() > 0) {
						for (int y = 1; y < yMap.size(); y++) {
							sheet.addCell(new Label(7 + y, 8, "0"));
						}
						for (int i = 0; i < list.size(); i++) {
							if (xMap != null && xMap.containsKey(list.get(i).getPubId())) {

								count += list.get(i).getFullAccess() != null && !"".equals(list.get(i).getFullAccess()) ? list.get(i).getFullAccess() : 0;
								counta = list.get(i).getFullAccess() != null && !"".equals(list.get(i).getFullAccess()) ? list.get(i).getFullAccess() : 0;
								countb = list.get(i).getFullAccess() != null && !"".equals(list.get(i).getFullAccess()) ? list.get(i).getFullAccess() : 0;
								countc += list.get(i).getDownload() != null && !"".equals(list.get(i).getDownload().toString()) ? list.get(i).getDownload() : 0;
								countd += list.get(i).getDownload() != null && !"".equals(list.get(i).getDownload().toString()) ? list.get(i).getDownload() : 0;
								if (accessMap.containsKey(list.get(i).getSdate())) {// 存在相同月分的值相加
																					// 每月一列总计
									accessMap.put(list.get(i).getSdate(), Integer.parseInt(accessMap.get(list.get(i).getSdate()).toString()) + countb);
								} else {
									accessMap.put(list.get(i).getSdate(), list.get(i).getFullAccess() != null && !"".equals(list.get(i).getFullAccess()) ? list.get(i).getFullAccess() : 0);
								}
								accessMap.put("accessCount", Integer.parseInt(accessMap.get("accessCount").toString()) + counta);// 总数

								sheet.addCell(new Label(Integer.parseInt(yMap.get(list.get(i).getSdate()).toString()), Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), String.valueOf(list.get(i).getFullAccess() != null && !"".equals(list.get(i).getFullAccess()) ? list.get(i).getFullAccess() : 0)));
								sheet.addCell(new Label(Integer.parseInt(yMap.get("count").toString()), Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), String.valueOf(count)));
							} else {
								count = 0;
								countc = 0;
								xMap.put(list.get(i).getPubId(), numX);// 行号
								for (int y = 1; y < yMap.size(); y++) {// 初始值默认为0

									sheet.addCell(new Label(7 + y, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), "0"));
								}
								// 生成excel
								sheet.addCell(new Label(0, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getTitle() != null ? list.get(i).getTitle() : ""));
								sheet.addCell(new Label(1, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getPubName() != null ? list.get(i).getPubName() : ""));
								sheet.addCell(new Label(2, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getPlatform() != null ? list.get(i).getPlatform() : ""));
								sheet.addCell(new Label(3, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), ""));
								sheet.addCell(new Label(4, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), ""));
								if (flag == 1) {
									sheet.addCell(new Label(5, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getIsbn() != null ? list.get(i).getIsbn() : ""));
									sheet.addCell(new Label(6, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getIssn() != null ? list.get(i).getIssn() : ""));
								} else if (flag == 2) {
									sheet.addCell(new Label(5, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getIssn() != null ? list.get(i).getIssn() : ""));
									sheet.addCell(new Label(6, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getEissn() != null ? list.get(i).getEissn() : ""));
								}
								sheet.addCell(new Label(Integer.parseInt(yMap.get(list.get(i).getSdate()).toString()), Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), String.valueOf(list.get(i).getFullAccess() != null && !"".equals(list.get(i).getFullAccess()) ? list.get(i).getFullAccess() : 0)));
								count += list.get(i).getFullAccess() != null && !"".equals(list.get(i).getFullAccess()) ? list.get(i).getFullAccess() : 0;
								counta = list.get(i).getFullAccess() != null && !"".equals(list.get(i).getFullAccess()) ? list.get(i).getFullAccess() : 0;// 每月总数大统计
								countb = list.get(i).getFullAccess() != null && !"".equals(list.get(i).getFullAccess()) ? list.get(i).getFullAccess() : 0;
								countc += list.get(i).getDownload() != null && !"".equals(list.get(i).getDownload()) ? list.get(i).getDownload() : 0;
								countd += list.get(i).getDownload() != null && !"".equals(list.get(i).getDownload()) ? list.get(i).getDownload() : 0;
								if (accessMap.containsKey("accessCount")) {
									accessMap.put("accessCount", Integer.parseInt(accessMap.get("accessCount").toString()) + counta);// 总数
								} else {
									accessMap.put("accessCount", counta);// 总数
								}

								if (accessMap.containsKey(list.get(i).getSdate())) {// 存在相同月分的值相加
									accessMap.put(list.get(i).getSdate(), Integer.parseInt(accessMap.get(list.get(i).getSdate()).toString()) + countb);
								} else {
									accessMap.put(list.get(i).getSdate(), countb);
								}
								numX++;

							}
							sheet.addCell(new Label(Integer.parseInt(yMap.get(list.get(i).getSdate()).toString()), 8, accessMap.get(list.get(i).getSdate()).toString()));
							if (flag == 1) {
								sheet.addCell(new Label(Integer.parseInt(yMap.get("count").toString()), 8, accessMap.get("accessCount").toString()));
								sheet.addCell(new Label(Integer.parseInt(yMap.get("count").toString()), Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), String.valueOf(count)));
							}
							if (flag == 2) {
								sheet.addCell(new Label(Integer.parseInt(yMap.get("Pdf").toString()), Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), String.valueOf(countc)));
								sheet.addCell(new Label(Integer.parseInt(yMap.get("Pdf").toString()), 8, String.valueOf(countd)));
								if (i + 1 == list.size()) {
									accessMap.put("accessCount", Integer.valueOf(Integer.parseInt(accessMap.get("accessCount").toString()) + countd));
								}
								sheet.addCell(new Label(Integer.parseInt(yMap.get("count").toString()), 8, accessMap.get("accessCount").toString()));
								sheet.addCell(new Label(Integer.parseInt(yMap.get("count").toString()), Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), String.valueOf(count + countc)));
							}
						}

					}
					workbook.write();
					workbook.close();
					os.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return null;
	}

	/**
	 * 下载访问excel记录
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/downloadDownload")
	public ModelAndView downloadDownload(HttpServletRequest request, HttpServletResponse response, SSupplierForm form) throws Exception {
		try {
			List<SSupplier> list = null;
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			if (user != null && (user.getLevel() == 2 || user.getLevel() == 3)) {// 限制为机构管理员或中图管理员可下载
				if (user.getInstitution().getId() != null && !"".equals(user.getInstitution().getId())) {// 机构ID不为NULL
					form.setUrl(request.getRequestURL().toString());
					String pubType = "";// 1-图书2-期刊
					Integer languagetype = null;// 1-中文图书统计;2-外文图书统计
					if (request.getParameter("pubtype") != null && !"".equals(request.getParameter("pubtype"))) {
						pubType = request.getParameter("pubtype");// 1-图书2-期刊
						form.setPubType(pubType);
					} else {
						pubType = form.getPubType();// 1-图书2-期刊
					}
					Date date = new Date();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM");
					String dateString = formatter.format(date);// 当前年
					dateString = dateString + "-" + "01";
					String dateyear = sdf.format(date);// 当前年月
					String eyear = sdf1.format(date);// 当前年
					String staryear = form.getStartyear() != null && !"".equals(form.getStartyear()) ? form.getStartyear() : dateString;
					String endyear = form.getEndtyear() != null && !"".equals(form.getEndtyear()) ? form.getEndtyear() : eyear;

					Map<String, Object> condition = new HashMap<String, Object>();
					condition.put("institutionId", user.getInstitution().getId());// 机构ID
					condition.put("staryear", staryear);// 起始年月 2013-01
					condition.put("endyear", endyear);// 结束年月2013-12
					condition.put("DownloadON", 0);// 查询出下载统计数大于0
					if (!"2".equals(pubType)) {
						if (form.getLanguagetype() == null) {// 1-中文图书统计;2-外文图书统计
							languagetype = Integer.valueOf(request.getParameter("languagetype"));
						} else {
							languagetype = form.getLanguagetype();
						}
					}
					if (pubType != null && "1".equals(pubType)) {// 1-图书2-期刊
						condition.put("pubtypes", new Integer[] { 1, 3 });
					} else if (pubType != null && "2".equals(pubType)) {
						condition.put("pubtypes", new Integer[] { 2, 4 });
					}
					if (!"2".equals(pubType)) {
						if (languagetype == 1) {// 1-中文图书统计;2-外文图书统计
							condition.put("isPrecisebook", true);
						} else if (languagetype == 2) {
							condition.put("isPrecisebook", false);
						}
					}
					condition.put("lang", new String[] { "chs", "cht" });
					Map<String, Object> xMap = new LinkedHashMap<String, Object>();// X
																					// 轴坐标
																					// key-
																					// contentID
																					// value-
																					// x
					Map<String, Object> yMap = new LinkedHashMap<String, Object>();// Y
																					// 轴坐标
																					// key-
																					// 月-年
																					// value-
																					// y
					Map<String, Object> downloadMap = new LinkedHashMap<String, Object>();// 结果集
																							// 总数
																							// -每月分别统计总数
					String[] str = { "", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };// 月份数组

					list = this.bookSuppliersService.getSuppList(condition, " order by a.pubId,a.year,a.month ");

					int flag = 0;// 1-图书 2-期刊 0-初始值
					int numX = 9;// 行号
					int numY = 7;// 列号
					int count = 0;
					/* 生成excel start heqing.yang 2014-11-02 */
					String time = StatisticsBookSuppliersController.getNowbatchCooe();
					// 输出的excel文件工作表名
					String worksheet = "";
					yMap.put("count", numY);
					StringBuffer sbb = new StringBuffer();/*
															 * 生成excel start
															 * heqing.yang
															 * 2014-11-02
															 */
					if (pubType != null && "2".equals(pubType)) {
						worksheet = "JR-C8.CNPEADING." + time;
						sbb.append("Journal Report 1 (R4);");
						flag = 2;
					} else if (pubType != null && "1".equals(pubType)) {
						worksheet = "BR-C8.CNPEADING." + time;
						sbb.append("Book Report 1 (R4);");
						flag = 1;
					}
					sbb.append(" ;");
					sbb.append(" ;");
					sbb.append("Period covered by Report:;");
					sbb.append("yyyy-mm-dd to yyyy-mm-dd;");
					sbb.append("Date run:;");
					sbb.append("yyyy-mm-dd;");

					StringBuffer sb = new StringBuffer();
					if (flag == 2) {
						sb.append("Journal;");
						sb.append("Publisher;");
						sb.append("Platform;");
						sb.append("Journal DOI;");
						sb.append("Proprietary Identifier;");

						sb.append("Print ISSN;");
						sb.append("Online ISSN;");
						sb.append("Reporting Period Total;");
					} else if (flag == 1) {
						sb.append(" ;");
						sb.append("Publisher;");
						sb.append("Platform;");
						sb.append("Book DOI;");
						sb.append("Proprietary Identifier;");
						sb.append("ISBN;");
						sb.append("ISSN;");
						sb.append("Reporting Period Total;");
					}

					// 2013-01 2014-09
					String[] sy = staryear.split("-");
					String[] ey = endyear.split("-");
					Integer start = Integer.parseInt(sy[0]);
					Integer end = Integer.parseInt(ey[0]);
					int sTartMonth = 1;
					int eNdMonth = 12;
					for (int j = start; j <= end; j++) {

						if (sTartMonth != Integer.parseInt(sy[1])) {
							sTartMonth = Integer.parseInt(sy[1]);
						} else {
							sTartMonth = 1;
						}
						if (j == end && eNdMonth != Integer.parseInt(ey[1])) {
							eNdMonth = Integer.parseInt(ey[1]);
						}
						for (int k = sTartMonth; k <= eNdMonth; k++) {
							String month = "";
							numY++;// Y轴
							month = str[k];// 月份一维数组
							sb.append(month + "-" + j + ";");
							String dete = String.valueOf(k);
							if (dete.length() == 1) {
								dete = "0" + dete;
							}
							yMap.put(j + "-" + dete, numY);
						}

					}

					StringBuffer sbf = new StringBuffer();
					if (flag == 1) {
						sbf.append("Total for all titles;");
					} else if (flag == 2) {
						sbf.append("Total for all journals;");
					}

					sbf.append(" ;");
					if (flag == 1) {
						sbf.append(" ;");
					} else if (flag == 2) {
						sbf.append("Platform Z ;");
					}

					sbf.append(" ;");
					sbf.append(" ;");
					sbf.append(" ;");
					sbf.append(" ;");
					sbf.append(" ;");
					sbf.append(" ;");// 总数列

					String title[] = sb.toString().split(";");
					String titles[] = sbb.toString().split(";");
					String titsbf[] = sbf.toString().split(";");
					WritableWorkbook workbook;
					OutputStream os = response.getOutputStream();
					response.reset();// 清空输出流
					response.setHeader("Content-disposition", "attachment; filename=" + worksheet + ".xls");// 设定输出文件头
					response.setContentType("application/vnd.ms-excel;charset=UTF-8");// 定义输出类型
					workbook = Workbook.createWorkbook(os);
					WritableSheet sheet = workbook.createSheet(worksheet, 0);

					int num = 0;
					for (int i = 0; i < titles.length; i++) {
						sheet.addCell(new Label(0, i, titles[i]));
						num++;
					}
					for (int i = 0; i < num; i++) {
						if (i == 0) {
							if (pubType != null && "2".equals(pubType)) {
								sheet.addCell(new Label(1, i, "Number of Successfull Full-Text Article Requests by Month and Journal"));
							} else if (pubType != null && "1".equals(pubType)) {
								sheet.addCell(new Label(1, i, "Number of Successfull T	Title Requests by Month and Title"));
							}

						} else if (i == 1) {
							sheet.addCell(new Label(1, i, " "));
						} else if (i == 2) {
							sheet.addCell(new Label(1, i, " "));
						} else if (i == 3) {
							sheet.addCell(new Label(1, i, "Cnpereading.com"));
						} else if (i == 4) {
							sheet.addCell(new Label(1, i, dateyear));// 条件值
						} else if (i == 5) {
							sheet.addCell(new Label(1, i, " "));
						} else if (i == 6) {
							String s = StatisticsBookSuppliersController.getNowbatchCooe();
							sheet.addCell(new Label(1, i, s));
						}
					}

					for (int i = 0; i < title.length; i++) {

						// Label(列号,行号 ,内容 )
						sheet.addCell(new Label(i, num, title[i]));
					}
					for (int i = 0; i < titsbf.length; i++) {

						// Label(列号,行号 ,内容 )
						sheet.addCell(new Label(i, num + 1, titsbf[i]));
					}
					int counta = 0;// 统计大总数
					int countb = 0;// 统计每月大总数
					if (list != null && list.size() > 0) {
						for (int i = 0; i < list.size(); i++) {
							if (xMap != null && xMap.containsKey(list.get(i).getPubId())) {

								count += list.get(i).getDownload() != null && !"".equals(list.get(i).getDownload()) ? list.get(i).getDownload() : 0;
								counta = list.get(i).getDownload() != null && !"".equals(list.get(i).getDownload()) ? list.get(i).getDownload() : 0;
								countb = list.get(i).getDownload() != null && !"".equals(list.get(i).getDownload()) ? list.get(i).getDownload() : 0;
								if (downloadMap.containsKey(list.get(i).getSdate())) {// 存在相同月分的值相加
																						// 每月一列总计
									downloadMap.put(list.get(i).getSdate(), Integer.parseInt(downloadMap.get(list.get(i).getSdate()).toString()) + countb);
								} else {
									downloadMap.put(list.get(i).getSdate(), list.get(i).getDownload() != null && !"".equals(list.get(i).getDownload()) ? list.get(i).getDownload() : 0);
								}
								downloadMap.put("downloadCount", Integer.parseInt(downloadMap.get("downloadCount").toString()) + counta);// 总数

								sheet.addCell(new Label(Integer.parseInt(yMap.get(list.get(i).getSdate()).toString()), Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), String.valueOf(list.get(i).getDownload() != null && !"".equals(list.get(i).getDownload()) ? list.get(i).getDownload() : 0)));
								sheet.addCell(new Label(Integer.parseInt(yMap.get("count").toString()), Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), String.valueOf(count)));
							} else {
								count = 0;
								xMap.put(list.get(i).getPubId(), numX);// 行号
								for (int y = 1; y < yMap.size(); y++) {// 初始值默认为0

									sheet.addCell(new Label(7 + y, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), "0"));
								}
								// 生成excel
								sheet.addCell(new Label(0, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getTitle() != null ? list.get(i).getTitle() : ""));
								sheet.addCell(new Label(1, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getPubName() != null ? list.get(i).getPubName() : ""));
								sheet.addCell(new Label(2, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getPlatform() != null ? list.get(i).getPlatform() : ""));
								sheet.addCell(new Label(3, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), ""));
								sheet.addCell(new Label(4, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), ""));
								if (flag == 1) {
									sheet.addCell(new Label(5, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getIsbn() != null ? list.get(i).getIsbn() : ""));
									sheet.addCell(new Label(6, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getIssn() != null ? list.get(i).getIssn() : ""));
								} else if (flag == 2) {
									sheet.addCell(new Label(5, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getIssn() != null ? list.get(i).getIssn() : ""));
									sheet.addCell(new Label(6, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getEissn() != null ? list.get(i).getEissn() : ""));
								}
								sheet.addCell(new Label(Integer.parseInt(yMap.get(list.get(i).getSdate()).toString()), Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), String.valueOf(list.get(i).getDownload() != null && !"".equals(list.get(i).getDownload()) ? list.get(i).getDownload() : 0)));
								count += list.get(i).getDownload() != null && !"".equals(list.get(i).getDownload()) ? list.get(i).getDownload() : 0;
								counta = list.get(i).getDownload() != null && !"".equals(list.get(i).getDownload()) ? list.get(i).getDownload() : 0;// 每月总数大统计
								countb = list.get(i).getDownload() != null && !"".equals(list.get(i).getDownload()) ? list.get(i).getDownload() : 0;
								if (downloadMap.containsKey("downloadCount")) {
									downloadMap.put("downloadCount", Integer.parseInt(downloadMap.get("downloadCount").toString()) + counta);// 总数
								} else {
									downloadMap.put("downloadCount", counta);// 总数
								}

								if (downloadMap.containsKey(list.get(i).getSdate())) {// 存在相同月分的值相加
									downloadMap.put(list.get(i).getSdate(), Integer.parseInt(downloadMap.get(list.get(i).getSdate()).toString()) + countb);
								} else {
									downloadMap.put(list.get(i).getSdate(), countb);
								}
								numX++;

							}
							sheet.addCell(new Label(Integer.parseInt(yMap.get(list.get(i).getSdate()).toString()), 8, downloadMap.get(list.get(i).getSdate()).toString()));// 每月大总数
							sheet.addCell(new Label(Integer.parseInt(yMap.get("count").toString()), 8, downloadMap.get("downloadCount").toString()));// 大总数
							sheet.addCell(new Label(Integer.parseInt(yMap.get("count").toString()), Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), String.valueOf(count)));
						}
					}
					workbook.write();
					workbook.close();
					os.close();

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return null;
	}

	/**
	 * 下载拒绝访问excel记录
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/downloadRefused")
	public ModelAndView downloadRefused(HttpServletRequest request, HttpServletResponse response, SSupplierForm form) throws Exception {
		try {
			List<SSupplier> list = null;
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			if (user != null && (user.getLevel() == 2 || user.getLevel() == 3)) {// 限制为机构管理员或中图管理员可下载
				if (user.getInstitution().getId() != null && !"".equals(user.getInstitution().getId())) {// 机构ID不为NULL
					form.setUrl(request.getRequestURL().toString());
					String pubType = "";// 1-图书2-期刊
					Integer languagetype = null;// 1-中文图书统计;2-外文图书统计
					if (request.getParameter("pubtype") != null && !"".equals(request.getParameter("pubtype"))) {
						pubType = request.getParameter("pubtype");// 1-图书2-期刊
						form.setPubType(pubType);
					} else {
						pubType = form.getPubType();// 1-图书2-期刊
					}
					Date date = new Date();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM");
					String dateString = formatter.format(date);// 当前年
					dateString = dateString + "-" + "01";
					String dateyear = sdf.format(date);// 当前年月
					String eyear = sdf1.format(date);// 当前年
					String staryear = form.getStartyear() != null && !"".equals(form.getStartyear()) ? form.getStartyear() : dateString;
					String endyear = form.getEndtyear() != null && !"".equals(form.getEndtyear()) ? form.getEndtyear() : eyear;

					Map<String, Object> condition = new HashMap<String, Object>();
					condition.put("institutionId", user.getInstitution().getId());// 机构ID
					condition.put("staryear", staryear);// 起始年月 2013-01
					condition.put("endyear", endyear);// 结束年月2013-12
					condition.put("refusedON", 0);// 查询出拒访统计数大于0
					if (!"2".equals(pubType)) {
						if (form.getLanguagetype() == null) {// 1-中文图书统计;2-外文图书统计
							languagetype = Integer.valueOf(request.getParameter("languagetype"));
						} else {
							languagetype = form.getLanguagetype();
						}
					}
					if (pubType != null && "1".equals(pubType)) {// 1-图书2-期刊
						condition.put("pubtypes", new Integer[] { 1, 3 });
					} else if (pubType != null && "2".equals(pubType)) {
						condition.put("pubtypes", new Integer[] { 2, 4 });
					}
					if (!"2".equals(pubType)) {
						if (languagetype == 1) {// 1-中文图书统计;2-外文图书统计
							condition.put("isPrecisebook", true);
						} else if (languagetype == 2) {
							condition.put("isPrecisebook", false);
						}
					}
					condition.put("lang", new String[] { "chs", "cht" });
					Map<String, Object> xMap = new LinkedHashMap<String, Object>();// X
																					// 轴坐标
																					// key-
																					// contentID
																					// value-
																					// x
					Map<String, Object> yMap = new LinkedHashMap<String, Object>();// Y
																					// 轴坐标
																					// key-
																					// 月-年
																					// value-
																					// y
					Map<String, Object> refusedMap = new LinkedHashMap<String, Object>();// 结果集
																							// 总数
																							// -每月分别统计总数
					String[] str = { "", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };// 月份数组

					list = this.bookSuppliersService.getSuppList(condition, " order by a.pubId,a.year,a.month ");

					int flag = 0;// 1-图书 2-期刊 0-初始值
					int numX = 9;// 行号
					int numY = 7;// 列号
					int count = 0;
					/* 生成excel start heqing.yang 2014-11-02 */
					String time = StatisticsBookSuppliersController.getNowbatchCooe();
					// 输出的excel文件工作表名
					String worksheet = "";
					yMap.put("count", numY);
					StringBuffer sbb = new StringBuffer();/*
															 * 生成excel start
															 * heqing.yang
															 * 2014-11-02
															 */
					if (pubType != null && "2".equals(pubType)) {
						worksheet = "JR2.CNPEREADING" + time;
						sbb.append("Journal Report 2 (R4);");
						flag = 2;
					} else if (pubType != null && "1".equals(pubType)) {
						worksheet = "BR2.CNPEREADING" + time;
						sbb.append("Book Report 3 (R4);");
						flag = 1;
					}
					sbb.append(" ;");
					sbb.append(" ;");
					sbb.append("Period covered by Report:;");
					sbb.append("yyyy-mm-dd to yyyy-mm-dd;");
					sbb.append("Date run:;");
					sbb.append("yyyy-mm-dd;");

					StringBuffer sb = new StringBuffer();
					if (flag == 2) {
						sb.append("Journal;");
						sb.append("Publisher;");
						sb.append("Platform;");
						sb.append("Journal DOI;");
						sb.append("Proprietary Identifier;");

						sb.append("Print ISSN;");
						sb.append("Online ISSN;");
						sb.append("Reporting Period Total;");
					} else if (flag == 1) {
						sb.append(" ;");
						sb.append("Publisher;");
						sb.append("Platform;");
						sb.append("Book DOI;");
						sb.append("Proprietary Identifier;");
						sb.append("ISBN;");
						sb.append("ISSN;");
						sb.append("Reporting Period Total;");
					}

					// 2013-01 2014-09
					String[] sy = staryear.split("-");
					String[] ey = endyear.split("-");
					Integer start = Integer.parseInt(sy[0]);
					Integer end = Integer.parseInt(ey[0]);
					int sTartMonth = 1;
					int eNdMonth = 12;
					for (int j = start; j <= end; j++) {

						if (sTartMonth != Integer.parseInt(sy[1])) {
							sTartMonth = Integer.parseInt(sy[1]);
						} else {
							sTartMonth = 1;
						}
						if (j == end && eNdMonth != Integer.parseInt(ey[1])) {
							eNdMonth = Integer.parseInt(ey[1]);
						}
						for (int k = sTartMonth; k <= eNdMonth; k++) {
							String month = "";
							numY++;// Y轴
							month = str[k];// 月份一维数组
							sb.append(month + "-" + j + ";");
							String dete = String.valueOf(k);
							if (dete.length() == 1) {
								dete = "0" + dete;
							}
							yMap.put(j + "-" + dete, numY);
						}
					}

					StringBuffer sbf = new StringBuffer();
					if (flag == 1) {
						sbf.append("Total for all titles;");
					} else if (flag == 2) {
						sbf.append("Total for all journals;");
					}

					sbf.append(" ;");
					if (flag == 1) {
						sbf.append(" ;");
					} else if (flag == 2) {
						sbf.append("Platform Z ;");
					}

					sbf.append(" ;");
					sbf.append(" ;");
					sbf.append(" ;");
					sbf.append(" ;");
					sbf.append(" ;");
					sbf.append(" ;");// 总数列

					String title[] = sb.toString().split(";");
					String titles[] = sbb.toString().split(";");
					String titsbf[] = sbf.toString().split(";");
					WritableWorkbook workbook;
					OutputStream os = response.getOutputStream();
					response.reset();// 清空输出流
					response.setHeader("Content-disposition", "attachment; filename=" + worksheet + ".xls");// 设定输出文件头
					response.setContentType("application/vnd.ms-excel;charset=UTF-8");// 定义输出类型
					workbook = Workbook.createWorkbook(os);
					WritableSheet sheet = workbook.createSheet(worksheet, 0);

					int num = 0;
					for (int i = 0; i < titles.length; i++) {
						sheet.addCell(new Label(0, i, titles[i]));
						num++;
					}
					for (int i = 0; i < num; i++) {
						if (i == 0) {
							if (pubType != null && "2".equals(pubType)) {
								sheet.addCell(new Label(1, i, "Access Denied to Full-Text Articles by Month, Journal and Category"));
							} else if (pubType != null && "1".equals(pubType)) {
								sheet.addCell(new Label(1, i, "Access Denied to Content Items by Month,Title and Category"));
							}

						} else if (i == 1) {
							sheet.addCell(new Label(1, i, " "));
						} else if (i == 2) {
							sheet.addCell(new Label(1, i, " "));
						} else if (i == 3) {
							sheet.addCell(new Label(1, i, "Cnpereading.com"));
						} else if (i == 4) {
							sheet.addCell(new Label(1, i, dateyear));// 条件值
						} else if (i == 5) {
							sheet.addCell(new Label(1, i, " "));
						} else if (i == 6) {
							String s = StatisticsBookSuppliersController.getNowbatchCooe();
							sheet.addCell(new Label(1, i, s));
						}
					}

					for (int i = 0; i < title.length; i++) {

						// Label(列号,行号 ,内容 )
						sheet.addCell(new Label(i, num, title[i]));
					}
					for (int i = 0; i < titsbf.length; i++) {

						// Label(列号,行号 ,内容 )
						sheet.addCell(new Label(i, num + 1, titsbf[i]));
					}
					int counta = 0;// 统计大总数
					int countb = 0;// 统计每月大总数
					if (list != null && list.size() > 0) {
						for (int i = 0; i < list.size(); i++) {
							if (xMap != null && xMap.containsKey(list.get(i).getPubId())) {

								count += list.get(i).getFullRefused() != null && !"".equals(list.get(i).getFullRefused()) ? list.get(i).getFullRefused() : 0;
								counta = list.get(i).getFullRefused() != null && !"".equals(list.get(i).getFullRefused()) ? list.get(i).getFullRefused() : 0;
								countb = list.get(i).getFullRefused() != null && !"".equals(list.get(i).getFullRefused()) ? list.get(i).getFullRefused() : 0;
								if (refusedMap.containsKey(list.get(i).getSdate())) {// 存在相同月分的值相加
																						// 每月一列总计
									refusedMap.put(list.get(i).getSdate(), Integer.parseInt(refusedMap.get(list.get(i).getSdate()).toString()) + countb);
								} else {
									refusedMap.put(list.get(i).getSdate(), list.get(i).getFullRefused() != null && !"".equals(list.get(i).getFullRefused()) ? list.get(i).getFullRefused() : 0);
								}
								refusedMap.put("refusedCount", Integer.parseInt(refusedMap.get("refusedCount").toString()) + counta);// 总数

								sheet.addCell(new Label(Integer.parseInt(yMap.get(list.get(i).getSdate()).toString()), Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), String.valueOf(list.get(i).getFullRefused() != null && !"".equals(list.get(i).getFullRefused()) ? list.get(i).getFullRefused() : 0)));
								sheet.addCell(new Label(Integer.parseInt(yMap.get("count").toString()), Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), String.valueOf(count)));
							} else {
								count = 0;
								xMap.put(list.get(i).getPubId(), numX);// 行号
								for (int y = 1; y < yMap.size(); y++) {// 初始值默认为0

									sheet.addCell(new Label(7 + y, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), "0"));
								}
								// 生成excel
								sheet.addCell(new Label(0, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getTitle() != null ? list.get(i).getTitle() : ""));
								sheet.addCell(new Label(1, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getPubName() != null ? list.get(i).getPubName() : ""));
								sheet.addCell(new Label(2, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getPlatform() != null ? list.get(i).getPlatform() : ""));
								sheet.addCell(new Label(3, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), ""));
								sheet.addCell(new Label(4, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), ""));
								if (flag == 1) {
									sheet.addCell(new Label(5, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getIsbn() != null ? list.get(i).getIsbn() : ""));
									sheet.addCell(new Label(6, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getIssn() != null ? list.get(i).getIssn() : ""));
								} else if (flag == 2) {
									sheet.addCell(new Label(5, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getIssn() != null ? list.get(i).getIssn() : ""));
									sheet.addCell(new Label(6, Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), list.get(i).getEissn() != null ? list.get(i).getEissn() : ""));
								}
								sheet.addCell(new Label(Integer.parseInt(yMap.get(list.get(i).getSdate()).toString()), Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), String.valueOf(list.get(i).getFullRefused() != null && !"".equals(list.get(i).getFullRefused()) ? list.get(i).getFullRefused() : 0)));
								count += list.get(i).getFullRefused() != null && !"".equals(list.get(i).getFullRefused()) ? list.get(i).getFullRefused() : 0;
								counta = list.get(i).getFullRefused() != null && !"".equals(list.get(i).getFullRefused()) ? list.get(i).getFullRefused() : 0;// 每月总数大统计
								countb = list.get(i).getFullRefused() != null && !"".equals(list.get(i).getFullRefused()) ? list.get(i).getFullRefused() : 0;
								if (refusedMap.containsKey("refusedCount")) {
									refusedMap.put("refusedCount", Integer.parseInt(refusedMap.get("refusedCount").toString()) + counta);// 总数
								} else {
									refusedMap.put("refusedCount", counta);// 总数
								}

								if (refusedMap.containsKey(list.get(i).getSdate())) {// 存在相同月分的值相加
									refusedMap.put(list.get(i).getSdate(), Integer.parseInt(refusedMap.get(list.get(i).getSdate()).toString()) + countb);
								} else {
									refusedMap.put(list.get(i).getSdate(), countb);
								}
								numX++;

							}
							sheet.addCell(new Label(Integer.parseInt(yMap.get(list.get(i).getSdate()).toString()), 8, refusedMap.get(list.get(i).getSdate()).toString()));// 每月大总数
							sheet.addCell(new Label(Integer.parseInt(yMap.get("count").toString()), 8, refusedMap.get("refusedCount").toString()));// 大总数
							sheet.addCell(new Label(Integer.parseInt(yMap.get("count").toString()), Integer.parseInt(xMap.get(list.get(i).getPubId()).toString()), String.valueOf(count)));
						}
					}
					workbook.write();
					workbook.close();
					os.close();

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return null;
	}

	/**
	 * 提供商Toc下载统计列表
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "/form/managerToc")
	public ModelAndView managerToc(HttpServletRequest request, HttpServletResponse response, SSupplierForm form) {
		String forwardString = "user/org_totalCh";
		Map<String, Object> model = new HashMap<String, Object>();
		try {// by heqing.yang 2015-01-24
			List<SSupplier> list = null;
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			if (user != null && (user.getLevel() == 2 || user.getLevel() == 3)) {// 限制为机构管理员或中图管理员可下载
				if (user.getInstitution().getId() != null && !"".equals(user.getInstitution().getId())) {// 机构ID不为NULL
					form.setUrl(request.getRequestURL().toString());
					String pubType = "";// 1-图书2-期刊
					Integer languagetype = null;// 1-中文图书统计;2-外文图书统计
					if (request.getParameter("pubtype") != null && !"".equals(request.getParameter("pubtype"))) {
						pubType = request.getParameter("pubtype");// 1-图书2-期刊
						form.setPubType(pubType);
					} else {
						pubType = form.getPubType();// 1-图书2-期刊
					}
					Map<String, Object> condition = new HashMap<String, Object>();
					condition.put("institutionId", user.getInstitution().getId());// 机构ID
					condition.put("staryear", form.getStartyear());// 起始年月
																	// 2013-01
					condition.put("endyear", form.getEndtyear());// 结束年月2013-12
					condition.put("tocON", 0);// 查询出toc统计数大于0

					if (form.getLanguagetype() == null) {// 1-中文图书统计;2-外文图书统计
						languagetype = Integer.valueOf(request.getParameter("languagetype"));
					} else {
						languagetype = form.getLanguagetype();
					}

					if (pubType != null && "1".equals(pubType)) {// 1-图书2-期刊
						condition.put("pubtypes", new Integer[] { 1, 3 });
					} else if (pubType != null && "2".equals(pubType)) {
						if (languagetype == 1) {
							condition.put("pubtypes", new Integer[] { 2, 4 });
							forwardString = "user/org_totalJour";
							condition.put("isPrecisebook", false);
						} else if (languagetype == 2) {
							condition.put("pubtypes", new Integer[] { 2, 4 });
							forwardString = "user/org_totalJourEn";
							condition.put("isPrecisebook", false);
						}
					}
					if (!"2".equals(pubType)) {
						if (languagetype == 1) {// 1-中文图书统计;2-外文图书统计
							forwardString = "user/org_totalCh";
							condition.put("isPrecisebook", true);
						} else if (languagetype == 2) {
							forwardString = "user/org_totalEn";
							condition.put("isPrecisebook", false);
						}
					}
					condition.put("lang", new String[] { "chs", "cht" });
					form.setType(Integer.parseInt(pubType));
					form.setCount(this.bookSuppliersService.getSSupplierCountGroupby(condition, " group by a.institutionid,a.pubId,a.title,a.type,a.author,a.isbn,a.issn,a.eissn,a.pubName,a.platform "));
					list = this.bookSuppliersService.getTocList(condition, " group by a.institutionid,a.pubId,a.title,a.type,a.author,a.isbn,a.issn,a.eissn,a.pubName,a.platform ", form.getPageCount(), form.getCurpage());

				}
			}
			String btn = request.getParameter("btn");
			int tabIndex = Integer.parseInt(btn);

			model.put("tabIndex", tabIndex);
			model.put("list", list);
			model.put("form", form);
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}

		return new ModelAndView(forwardString, model);
	}

	/**
	 * 提供商搜索统计列表
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "/form/managerSearch")
	public ModelAndView managerSearch(HttpServletRequest request, HttpServletResponse response, SSupplierForm form) {
		String forwardString = "user/org_totalCh";
		Map<String, Object> model = new HashMap<String, Object>();
		try {// by heqing.yang 2015-01-24
			List<SSupplier> list = null;
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			if (user != null && (user.getLevel() == 2 || user.getLevel() == 3)) {// 限制为机构管理员或中图管理员可下载
				if (user.getInstitution().getId() != null && !"".equals(user.getInstitution().getId())) {// 机构ID不为NULL
					form.setUrl(request.getRequestURL().toString());
					String pubType = "";// 1-图书2-期刊
					Integer languagetype = null;// 1-中文图书统计;2-外文图书统计
					if (request.getParameter("pubtype") != null && !"".equals(request.getParameter("pubtype"))) {
						pubType = request.getParameter("pubtype");// 1-图书2-期刊
						form.setPubType(pubType);
					} else {
						pubType = form.getPubType();// 1-图书2-期刊
					}
					Map<String, Object> condition = new HashMap<String, Object>();
					condition.put("institutionId", user.getInstitution().getId());// 机构ID
					condition.put("staryear", form.getStartyear());// 起始年月
																	// 2013-01
					condition.put("endyear", form.getEndtyear());// 结束年月2013-12
					condition.put("searchON", 0);// 查询出search统计数大于0

					if (form.getLanguagetype() == null) {// 1-中文图书统计;2-外文图书统计
						languagetype = Integer.valueOf(request.getParameter("languagetype"));
					} else {
						languagetype = form.getLanguagetype();
					}

					if (pubType != null && "1".equals(pubType)) {// 1-图书2-期刊
						condition.put("pubtypes", new Integer[] { 1, 3 });
					} else if (pubType != null && "2".equals(pubType)) {
						if (languagetype == 1) {
							condition.put("pubtypes", new Integer[] { 2, 4 });
							forwardString = "user/org_totalJour";
							condition.put("isPrecisebook", false);
						} else if (languagetype == 2) {
							condition.put("pubtypes", new Integer[] { 2, 4 });
							forwardString = "user/org_totalJourEn";
							condition.put("isPrecisebook", false);
						}
					}
					if (!"2".equals(pubType)) {
						if (languagetype == 1) {// 1-中文图书统计;2-外文图书统计
							forwardString = "user/org_totalCh";
							condition.put("isPrecisebook", true);
						} else if (languagetype == 2) {
							forwardString = "user/org_totalEn";
							condition.put("isPrecisebook", false);
						}
					}
					condition.put("lang", new String[] { "chs", "cht" });
					form.setType(Integer.parseInt(pubType));
					form.setCount(this.bookSuppliersService.getSSupplierCountGroupby(condition, " group by a.institutionid,a.pubId,a.title,a.type,a.author,a.isbn,a.issn,a.eissn,a.pubName,a.platform "));
					list = this.bookSuppliersService.getSearchList(condition, " group by a.institutionid,a.pubId,a.title,a.type,a.author,a.isbn,a.issn,a.eissn,a.pubName,a.platform ", form.getPageCount(), form.getCurpage());

				}
			}

			model.put("tabIndex", 5);
			model.put("list", list);
			model.put("form", form);
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}

		return new ModelAndView(forwardString, model);
	}

	/**
	 * 提供商全文访问统计列表
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "/form/managerAccess")
	public ModelAndView managerAccess(HttpServletRequest request, HttpServletResponse response, SSupplierForm form) {
		String forwardString = "user/org_totalCh";
		Map<String, Object> model = new HashMap<String, Object>();
		try {// by heqing.yang 2015-01-24
			List<SSupplier> list = null;
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			if (user != null && (user.getLevel() == 2 || user.getLevel() == 3)) {// 限制为机构管理员或中图管理员可下载
				if (user.getInstitution().getId() != null && !"".equals(user.getInstitution().getId())) {// 机构ID不为NULL
					form.setUrl(request.getRequestURL().toString());
					String pubType = "";// 1-图书2-期刊
					Integer languagetype = null;// 1-中文图书统计;2-外文图书统计
					if (request.getParameter("pubtype") != null && !"".equals(request.getParameter("pubtype"))) {
						pubType = request.getParameter("pubtype");// 1-图书2-期刊
						form.setPubType(pubType);
					} else {
						pubType = form.getPubType();// 1-图书2-期刊
					}
					Map<String, Object> condition = new HashMap<String, Object>();
					condition.put("institutionId", user.getInstitution().getId());// 机构ID
					condition.put("staryear", form.getStartyear());// 起始年月
																	// 2013-01
					condition.put("endyear", form.getEndtyear());// 结束年月2013-12
					condition.put("accessON", 0);// 查询出全文统计数大于0

					if (form.getLanguagetype() == null) {// 1-中文图书统计;2-外文图书统计
						languagetype = Integer.valueOf(request.getParameter("languagetype"));
					} else {
						languagetype = form.getLanguagetype();
					}

					if (pubType != null && "1".equals(pubType)) {// 1-图书2-期刊
						condition.put("pubtypes", new Integer[] { 1, 3 });
					} else if (pubType != null && "2".equals(pubType)) {
						if (languagetype == 1) {
							condition.put("pubtypes", new Integer[] { 2, 4 });
							forwardString = "user/org_totalJour";
							condition.put("isPrecisebook", false);
						} else if (languagetype == 2) {
							condition.put("pubtypes", new Integer[] { 2, 4 });
							forwardString = "user/org_totalJourEn";
							condition.put("isPrecisebook", false);
						}
					}
					if (!"2".equals(pubType)) {
						if (languagetype == 1) {// 1-中文图书统计;2-外文图书统计
							forwardString = "user/org_totalCh";
							condition.put("isPrecisebook", true);
						} else if (languagetype == 2) {
							forwardString = "user/org_totalEn";
							condition.put("isPrecisebook", false);
						}
					}
					condition.put("lang", new String[] { "chs", "cht" });
					form.setType(Integer.parseInt(pubType));
					form.setCount(this.bookSuppliersService.getSSupplierCountGroupby(condition, " group by a.institutionid,a.pubId,a.title,a.type,a.author,a.isbn,a.issn,a.eissn,a.pubName,a.platform "));
					list = this.bookSuppliersService.getFullAccessList(condition, " group by a.institutionid,a.pubId,a.title,a.type,a.author,a.isbn,a.issn,a.eissn,a.pubName,a.platform ", form.getPageCount(), form.getCurpage());

				}
			}
			model.put("tabIndex", 1);
			model.put("list", list);
			model.put("form", form);
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}

		return new ModelAndView(forwardString, model);
	}

	/**
	 * 提供商下载统计列表
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "/form/managerDownload")
	public ModelAndView managerDownload(HttpServletRequest request, HttpServletResponse response, SSupplierForm form) {
		String forwardString = "user/org_totalCh";
		Map<String, Object> model = new HashMap<String, Object>();
		try {// by heqing.yang 2015-01-24
			List<SSupplier> list = null;
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			if (user != null && (user.getLevel() == 2 || user.getLevel() == 3)) {// 限制为机构管理员或中图管理员可下载
				if (user.getInstitution().getId() != null && !"".equals(user.getInstitution().getId())) {// 机构ID不为NULL
					form.setUrl(request.getRequestURL().toString());
					String pubType = "";// 1-图书2-期刊
					Integer languagetype = null;// 1-中文图书统计;2-外文图书统计
					if (request.getParameter("pubtype") != null && !"".equals(request.getParameter("pubtype"))) {
						pubType = request.getParameter("pubtype");// 1-图书2-期刊
						form.setPubType(pubType);
					} else {
						pubType = form.getPubType();// 1-图书2-期刊
					}
					Map<String, Object> condition = new HashMap<String, Object>();
					condition.put("institutionId", user.getInstitution().getId());// 机构ID
					condition.put("staryear", form.getStartyear());// 起始年月
																	// 2013-01
					condition.put("endyear", form.getEndtyear());// 结束年月2013-12
					condition.put("DownloadON", 0);// 查询出下载统计数大于0

					if (form.getLanguagetype() == null) {// 1-中文图书统计;2-外文图书统计
						languagetype = Integer.valueOf(request.getParameter("languagetype"));
					} else {
						languagetype = form.getLanguagetype();
					}

					if (pubType != null && "1".equals(pubType)) {// 1-图书2-期刊
						condition.put("pubtypes", new Integer[] { 1, 3 });
					} else if (pubType != null && "2".equals(pubType)) {
						if (languagetype == 1) {
							condition.put("pubtypes", new Integer[] { 2, 4 });
							forwardString = "user/org_totalJour";
							condition.put("isPrecisebook", false);
						} else if (languagetype == 2) {
							condition.put("pubtypes", new Integer[] { 2, 4 });
							forwardString = "user/org_totalJourEn";
							condition.put("isPrecisebook", false);
						}
					}
					if (!"2".equals(pubType)) {
						if (languagetype == 1) {// 1-中文图书统计;2-外文图书统计
							forwardString = "user/org_totalCh";
							condition.put("isPrecisebook", true);
						} else if (languagetype == 2) {
							forwardString = "user/org_totalEn";
							condition.put("isPrecisebook", false);
						}
					}
					condition.put("lang", new String[] { "chs", "cht" });
					form.setType(Integer.parseInt(pubType));
					form.setCount(this.bookSuppliersService.getSSupplierCountGroupby(condition, " group by a.institutionid,a.pubId,a.title,a.type,a.author,a.isbn,a.issn,a.eissn,a.pubName,a.platform "));
					list = this.bookSuppliersService.getDownloadList(condition, " group by a.institutionid,a.pubId,a.title,a.type,a.author,a.isbn,a.issn,a.eissn,a.pubName,a.platform ", form.getPageCount(), form.getCurpage());

				}
			}
			// int tabIndex = Integer.parseInt(btn);

			model.put("tabIndex", 4);
			model.put("list", list);
			model.put("form", form);
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}

		return new ModelAndView(forwardString, model);
	}

	/**
	 * 提供商拒访统计列表
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "/form/managerRefused")
	public ModelAndView managerRefused(HttpServletRequest request, HttpServletResponse response, SSupplierForm form) {
		String forwardString = "user/org_totalCh";
		Map<String, Object> model = new HashMap<String, Object>();
		try {// by heqing.yang 2015-01-24
			List<SSupplier> list = null;
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			if (user != null && (user.getLevel() == 2 || user.getLevel() == 3)) {// 限制为机构管理员或中图管理员可下载
				if (user.getInstitution().getId() != null && !"".equals(user.getInstitution().getId())) {// 机构ID不为NULL
					form.setUrl(request.getRequestURL().toString());
					String pubType = "";// 1-图书2-期刊
					Integer languagetype = null;// 1-中文图书统计;2-外文图书统计
					if (request.getParameter("pubtype") != null && !"".equals(request.getParameter("pubtype"))) {
						pubType = request.getParameter("pubtype");// 1-图书2-期刊
						form.setPubType(pubType);
					} else {
						pubType = form.getPubType();// 1-图书2-期刊
					}
					Map<String, Object> condition = new HashMap<String, Object>();
					condition.put("institutionId", user.getInstitution().getId());// 机构ID
					condition.put("staryear", form.getStartyear());// 起始年月
																	// 2013-01
					condition.put("endyear", form.getEndtyear());// 结束年月2013-12
					condition.put("refusedON", 0);// 查询出拒访统计数大于0

					if (form.getLanguagetype() == null) {// 1-中文图书统计;2-外文图书统计
						languagetype = Integer.valueOf(request.getParameter("languagetype"));
					} else {
						languagetype = form.getLanguagetype();
					}

					if (pubType != null && "1".equals(pubType)) {// 1-图书2-期刊
						condition.put("pubtypes", new Integer[] { 1, 3 });
					} else if (pubType != null && "2".equals(pubType)) {
						if (languagetype == 1) {
							condition.put("pubtypes", new Integer[] { 2, 4 });
							forwardString = "user/org_totalJour";
							condition.put("isPrecisebook", false);
						} else if (languagetype == 2) {
							condition.put("pubtypes", new Integer[] { 2, 4 });
							forwardString = "user/org_totalJourEn";
							condition.put("isPrecisebook", false);
						}
					}
					if (!"2".equals(pubType)) {
						if (languagetype == 1) {// 1-中文图书统计;2-外文图书统计
							forwardString = "user/org_totalCh";
							condition.put("isPrecisebook", true);
						} else if (languagetype == 2) {
							forwardString = "user/org_totalEn";
							condition.put("isPrecisebook", false);
						}
					}
					condition.put("lang", new String[] { "chs", "cht" });
					form.setType(Integer.parseInt(pubType));
					form.setCount(this.bookSuppliersService.getSSupplierCountGroupby(condition, " group by a.institutionid,a.pubId,a.title,a.type,a.author,a.isbn,a.issn,a.eissn,a.pubName,a.platform "));
					list = this.bookSuppliersService.getFullRefusedList(condition, " group by a.institutionid,a.pubId,a.title,a.type,a.author,a.isbn,a.issn,a.eissn,a.pubName,a.platform ", form.getPageCount(), form.getCurpage());

				}
			}

			model.put("tabIndex", 3);
			model.put("list", list);
			model.put("form", form);
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}

		return new ModelAndView(forwardString, model);
	}

	@RequestMapping(value = "/form/accesslogNew")
	public ModelAndView accesslogNew(HttpServletRequest request, HttpServletResponse response, SSupplierForm form) {
		Map<String, Object> model = new HashMap<String, Object>();
		String forwardString = "user/org_totalCh";
		try {

			form.setUrl(request.getRequestURL().toString());

			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			/*
			 * if (user != null && (user.getLevel() == 2 || user.getLevel() ==
			 * 3)) {// 限制为机构管理员或中图管理员可下载 //
			 * form.setYear(StringUtil.formatDate(new Date(),"yyyy")); for (int
			 * i = form.getStartYear(); i < form.getEndYear() + 1; i++) {
			 * form.getYearList().add(String.valueOf(i)); } for (int i = 1; i <
			 * 13; i++) { String month = String.valueOf(i); if (month.length()
			 * == 1) { month = "0" + month; } form.getMonthList().add(month); }
			 * 
			 * 
			 * if ((request.getParameter("type") != null &&
			 * !"".equals(request.getParameter("type")))||form.getType()!=null)
			 * { List<LAccess> list = null; Integer pubType =null; Integer
			 * accessType=null; Integer access=null; Integer languagetype=null;
			 * if(form.getPubtype()!=null&&form.getType()!=null){
			 * pubType=form.getPubtype(); accessType=form.getType(); }else{
			 * pubType=Integer.valueOf(request.getParameter("pubtype"));
			 * accessType=Integer.valueOf(request.getParameter("type")); }
			 * if(form.getAccess()!=null){ access=form.getAccess(); }else{
			 * access=
			 * Integer.valueOf(request.getParameter("access")!=null?request.
			 * getParameter("access"):"0"); }
			 * 
			 * Map<String, Object> condition = form.getCondition();
			 * if(pubType==1){//图书分:中文-外文 if(form.getLanguagetype()==null){
			 * languagetype=Integer.valueOf(request.getParameter("languagetype")
			 * ); }else{ languagetype=form.getLanguagetype(); }
			 * if(languagetype==1){ forwardString="user/org_totalCh";
			 * condition.put("isPrecisebook", true); }else if(languagetype==2){
			 * forwardString="user/org_totalEn"; condition.put("isPrecisebook",
			 * false); } condition.put("lang", new String [] {"chs","cht"});
			 * condition.put("isPrecise", true); }
			 * 
			 * condition.put("year", form.getYear());
			 * condition.put("startMonth", form.getStartMonth());
			 * condition.put("endMonth", form.getEndMonth());
			 * condition.put("type", accessType); if(accessType==2){
			 * condition.put("access", access); }
			 * 
			 * //区分期刊和图书 if(pubType!=null && pubType==2){
			 * forwardString="user/org_totalJour"; condition.put("pubtypes", new
			 * Integer[] { 2, 4 }); }else if(pubType!=null && pubType==1){
			 * condition.put("pubtypes", new Integer[] { 1, 3 }); }
			 * 
			 * if (user.getInstitution() != null && user.getLevel() != 3) {
			 * condition.put("institutionId2", user.getInstitution().getId()); }
			 * int pageCount = 10; int curpage = 0; pageCount =
			 * request.getParameter("pageCount") == null ? 10 :
			 * Integer.valueOf(request.getParameter("pageCount").toString());
			 * curpage = request.getParameter("curpage") == null ? 0 :
			 * Integer.valueOf(request.getParameter("curpage").toString());
			 * 
			 * switch (accessType) { case 1:
			 * form.setCount(this.logAOPService.getCount(condition,
			 * " group by a.publications.id ")); //
			 * form.setCount(this.logAOPService.getCount(condition, " ")); list
			 * = this.logAOPService.getLogOfYearPaging2(condition,
			 * " group by a.publications.id ", pageCount, curpage); break; case
			 * 2: if(access==1){
			 * form.setCount(this.logAOPService.getCount(condition,
			 * " group by a.publications.id ")); list =
			 * this.logAOPService.getLogOfYearToPagePaging2(condition,
			 * " group by a.publications.id ", pageCount, curpage); }else
			 * if(access==2){
			 * form.setCount(this.logAOPService.getCount(condition,
			 * " group by a.publications.id,a.refusedVisitType ")); list =
			 * this.logAOPService.getLogOfYearToPagePaging4(condition,
			 * " group by a.publications.id ,a.refusedVisitType  ", pageCount,
			 * curpage); } break; case 3: condition.put("keywordNotNull",1);
			 * form.setCount(this.logAOPService.getCount(condition,
			 * " group by a.publications.id  ")); list =
			 * this.logAOPService.getLogOfYearToSearchPaging2(condition,
			 * " group by a.publications.id ", pageCount, curpage); break; case
			 * 4: form.setCount(this.logAOPService.getCount(condition,
			 * " group by a.publications.id ")); list =
			 * this.logAOPService.getLogOfYearPaging2(condition,
			 * " group by a.publications.id ", pageCount, curpage); break;
			 * default: break; }
			 * 
			 * model.put("year", form.getYear()); model.put("startMonth",
			 * form.getStartMonth()); model.put("endMonth", form.getEndMonth());
			 * model.put("list", list); model.put("pubType", pubType); } }
			 */

			String btn = request.getParameter("btn");
			int tabIndex = Integer.parseInt(btn);
			switch (tabIndex) {
			case 2:
				return managerAccess(request, response, form);
			case 3:
				return managerToc(request, response, form);
			case 4:
				return managerAccess(request, response, form);
			case 5:
				return managerAccess(request, response, form);
			default:
				return managerAccess(request, response, form);
			}
			// model.put("tabIndex", tabIndex);
			// model.put("form", form);

		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 跳转到统计页面
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/accesslog")
	public ModelAndView accesslog(HttpServletRequest request, HttpServletResponse response, LAccessForm form) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		String forwardString = "user/accesslog";
		try {
			form.setUrl(request.getRequestURL().toString());
			String pubType = request.getParameter("pubtype");
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			if (user != null && (user.getLevel() == 2 || user.getLevel() == 3)) {// 限制为机构管理员或中图管理员可下载
				// form.setYear(StringUtil.formatDate(new Date(),"yyyy"));
				for (int i = form.getStartYear(); i < form.getEndYear() + 1; i++) {
					form.getYearList().add(String.valueOf(i));
				}
				for (int i = 1; i < 13; i++) {
					String month = String.valueOf(i);
					if (month.length() == 1) {
						month = "0" + month;
					}
					form.getMonthList().add(month);
				}
				if (request.getParameter("type") != null && !"".equals(request.getParameter("type"))) {
					List<LAccess> list = null;
					Integer accessType = Integer.valueOf(request.getParameter("type"));

					Map<String, Object> condition = form.getCondition();
					condition.put("year", form.getYear());
					condition.put("startMonth", form.getStartMonth());
					condition.put("endMonth", form.getEndMonth());
					condition.put("type", accessType);
					condition.put("access", 1);

					// 区分期刊和图书
					if (pubType != null && "2".equals(pubType)) {
						condition.put("pubtypes", new Integer[] { 2, 4 });
					} else if (pubType != null && "1".equals(pubType)) {
						condition.put("pubtype", 1);
					}

					if (user.getInstitution() != null && user.getLevel() != 3) {
						condition.put("institutionId2", user.getInstitution().getId());
					}
					int pageCount = 10;
					int curpage = 0;
					pageCount = request.getParameter("pageCount") == null ? 10 : Integer.valueOf(request.getParameter("pageCount").toString());
					curpage = request.getParameter("curpage") == null ? 0 : Integer.valueOf(request.getParameter("curpage").toString());

					switch (accessType) {
					case 1:
						form.setCount(this.logAOPService.getCount(condition, " group by a.publications.id "));
						// form.setCount(this.logAOPService.getCount(condition,
						// " "));
						list = this.logAOPService.getLogOfYearPaging2(condition, " group by a.publications.id ", pageCount, curpage);
						break;
					case 2:
						form.setCount(this.logAOPService.getCount(condition, " group by a.publications.id "));
						// form.setCount(this.logAOPService.getNormalCount(condition));
						list = this.logAOPService.getLogOfYearToPagePaging2(condition, " group by a.publications.id ", pageCount, curpage);
						break;
					case 3:
						condition.put("keywordNotNull", 1);
						form.setCount(this.logAOPService.getCount(condition, " group by a.publications.id ,a.activity "));
						list = this.logAOPService.getLogOfYearToSearchPaging2(condition, " group by a.publications.id ,a.activity", pageCount, curpage);
						break;
					case 4:
						break;
					default:
						break;
					}

					model.put("year", form.getYear());
					model.put("startMonth", form.getStartMonth());
					model.put("endMonth", form.getEndMonth());
					model.put("list", list);
					model.put("pubType", pubType);
				}
			}
			model.put("form", form);
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 下载图书/期刊全文浏览记录
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/downloadBookFull")
	public ModelAndView downloadBookFull(HttpServletRequest request, HttpServletResponse response, LAccessForm form) throws Exception {
		try {
			List<LAccess> list = null;
			form.setUrl(request.getRequestURL().toString());
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			if (user != null && (user.getLevel() == 2 || user.getLevel() == 3)) {
				if ((request.getParameter("type") != null && !"".equals(request.getParameter("type"))) || form.getType() != null) {
					Integer pubType = null;
					Integer accessType = null;
					Integer access = null;
					Integer languagetype = null;
					if (form.getPubtype() != null && form.getType() != null) {
						pubType = form.getPubtype();
						accessType = form.getType();
					} else {
						pubType = Integer.valueOf(request.getParameter("pubtype"));
						accessType = Integer.valueOf(request.getParameter("type"));
					}
					if (form.getAccess() != null) {
						access = form.getAccess();
					} else {
						access = Integer.valueOf(request.getParameter("access") != null ? request.getParameter("access") : "0");
					}

					Map<String, Object> condition = form.getCondition();
					if (pubType == 1) {// 图书分:中文-外文
						if (form.getLanguagetype() == null) {
							languagetype = Integer.valueOf(request.getParameter("languagetype"));
						} else {
							languagetype = form.getLanguagetype();
						}
						if (languagetype == 1) {

							condition.put("isPrecisebook", true);
						} else if (languagetype == 2) {

							condition.put("isPrecisebook", false);
						}
						condition.put("lang", new String[] { "chs", "cht" });
						condition.put("isPrecise", true);
					}

					condition.put("year", form.getYear());
					condition.put("startMonth", form.getStartMonth());
					condition.put("endMonth", form.getEndMonth());
					condition.put("type", accessType);
					if (accessType == 2) {
						condition.put("access", access);
					}

					// 区分期刊和图书
					if (pubType != null && pubType == 2) {

						condition.put("pubtypes", new Integer[] { 2, 4 });
					} else if (pubType != null && pubType == 1) {
						condition.put("pubtypes", new Integer[] { 1, 3 });
					}

					if (user.getInstitution() != null && user.getLevel() != 3) {
						condition.put("institutionId2", user.getInstitution().getId());
					}

					list = this.logAOPService.getLogOfYearToPagePaging2(condition, " group by a.publications.id ", 0, 0);

					int flag = 0;
					int z = 1;

					// 输出的excel文件工作表名
					String worksheet = "BookFullSuppliers" + form.getYear();
					StringBuffer sbb = new StringBuffer();
					if (pubType != null && pubType == 2) {
						sbb.append("Journal Report 1 (R4);");
						flag = 2;
						z = 7;
					} else if (pubType != null && pubType == 1) {
						sbb.append("Book Report 1 (R4);");
						flag = 1;
						z = 7;
					}

					sbb.append(" ;");
					sbb.append(" ;");
					sbb.append("Period covered by Report:;");
					sbb.append("yyyy-mm-dd to yyyy-mm-dd;");
					sbb.append("Date run:;");
					sbb.append("yyyy-mm-dd;");

					StringBuffer sb = new StringBuffer();
					if (flag == 2) {
						sb.append("Journal;");
						sb.append("Publisher;");
						sb.append("Platform;");
						sb.append("Journal DOI;");
						sb.append("Proprietary Identifier;");

						sb.append("Print ISSN;");
						sb.append("Online ISSN;");
						sb.append("Total;");
					} else if (flag == 1) {
						sb.append(" ;");
						sb.append("Publisher;");
						sb.append("Platform;");
						sb.append("Book DOI;");
						sb.append("Proprietary Identifier;");
						sb.append("ISBN;");
						sb.append("ISSN;");
						sb.append("Total;");
					}
					Integer start = Integer.valueOf(form.getStartMonth());
					Integer end = Integer.valueOf(form.getEndMonth());
					for (int i = start; i <= end; i++) {
						String month = "";
						if (i == 1) {
							month = "Jan";
						}
						if (i == 2) {
							month = "Feb";
						}
						if (i == 3) {
							month = "Mar";
						}
						if (i == 4) {
							month = "Apr";
						}
						if (i == 5) {
							month = "May";
						}
						if (i == 6) {
							month = "Jun";
						}
						if (i == 7) {
							month = "Jul";
						}
						if (i == 8) {
							month = "Aug";
						}
						if (i == 9) {
							month = "Sep";
						}
						if (i == 10) {
							month = "Oct";
						}
						if (i == 11) {
							month = "Nov";
						}
						if (i == 12) {
							month = "Dec";
						}
						sb.append(month + "-" + form.getYear() + ";");
					}
					StringBuffer sbf = new StringBuffer();
					if (flag == 1) {
						sbf.append("Total for all titles;");
					} else if (flag == 2) {
						sbf.append("Total for all journals;");
					}

					sbf.append(" ;");
					if (flag == 1) {
						sbf.append(" ;");
					} else if (flag == 2) {
						sbf.append("Platform Z ;");
					}

					sbf.append(" ;");
					sbf.append(" ;");
					sbf.append(" ;");
					sbf.append(" ;");

					int countnum = 0;
					int count1 = 0;
					int count2 = 0;
					int count3 = 0;
					int count4 = 0;
					int count5 = 0;
					int count6 = 0;
					int count7 = 0;
					int count8 = 0;
					int count9 = 0;
					int count10 = 0;
					int count11 = 0;
					int count12 = 0;

					sbf.append(countnum + ";");
					for (int i = start; i <= end; i++) {
						if (i == 1) {

							sbf.append(count1 + ";");
						}
						if (i == 2) {
							sbf.append(count2 + ";");
						}
						if (i == 3) {
							sbf.append(count3 + ";");
						}
						if (i == 4) {
							sbf.append(count4 + ";");
						}
						if (i == 5) {
							sbf.append(count5 + ";");
						}
						if (i == 6) {
							sbf.append(count6 + ";");
						}
						if (i == 7) {
							sbf.append(count7 + ";");
						}
						if (i == 8) {
							sbf.append(count8 + ";");
						}
						if (i == 9) {
							sbf.append(count9 + ";");
						}
						if (i == 10) {
							sbf.append(count10 + ";");
						}
						if (i == 11) {
							sbf.append(count11 + ";");
						}
						if (i == 12) {
							sbf.append(count12 + ";");
						}

					}
					String title[] = sb.toString().split(";");
					String titles[] = sbb.toString().split(";");
					String titsbf[] = sbf.toString().split(";");
					WritableWorkbook workbook;
					OutputStream os = response.getOutputStream();
					response.reset();// 清空输出流
					response.setHeader("Content-disposition", "attachment; filename=" + worksheet + ".xls");// 设定输出文件头
					response.setContentType("application/vnd.ms-excel;charset=UTF-8");// 定义输出类型
					workbook = Workbook.createWorkbook(os);
					WritableSheet sheet = workbook.createSheet(worksheet, 0);
					int num = 0;
					for (int i = 0; i < titles.length; i++) {
						sheet.addCell(new Label(0, i, titles[i]));
						num++;
					}

					for (int i = 0; i < num; i++) {
						if (i == 0) {
							sheet.addCell(new Label(1, i, "Number of Successfull T	Title Requests by Month and Title"));
						} else if (i == 1) {
							sheet.addCell(new Label(1, i, " "));
						} else if (i == 2) {
							sheet.addCell(new Label(1, i, " "));
						} else if (i == 3) {
							sheet.addCell(new Label(1, i, "Cnpereading.com"));
						} else if (i == 4) {
							sheet.addCell(new Label(1, i, form.getYear() + "-" + form.getStartMonth() + "-" + form.getEndMonth()));// 条件值
						} else if (i == 5) {
							sheet.addCell(new Label(1, i, " "));
						} else if (i == 6) {
							String s = UserMobileController.getNowbatchCooe();
							sheet.addCell(new Label(1, i, s));
						}
					}

					for (int i = 0; i < title.length; i++) {

						// Label(列号,行号 ,内容 )
						sheet.addCell(new Label(i, num, title[i]));
					}
					for (int i = 0; i < titsbf.length; i++) {

						// Label(列号,行号 ,内容 )
						sheet.addCell(new Label(i, num + 1, titsbf[i]));
					}

					int row = num + 2;
					for (LAccess o : list) {
						sheet.addCell(new Label(0, row, o.getPublications().getTitle() != null ? o.getPublications().getTitle() : ""));
						sheet.addCell(new Label(1, row, o.getPublications().getPublisher().getName() != null ? o.getPublications().getPublisher().getName() : ""));
						sheet.addCell(new Label(2, row, "Platform Z"));
						sheet.addCell(new Label(3, row, ""));
						sheet.addCell(new Label(4, row, ""));
						if (flag == 1) {
							sheet.addCell(new Label(5, row, o.getPublications().getCode() != null ? o.getPublications().getCode() : ""));
							sheet.addCell(new Label(6, row, ""));
						} else if (flag == 2) {
							sheet.addCell(new Label(5, row, o.getPublications().getCode() != null ? o.getPublications().getCode() : ""));
							sheet.addCell(new Label(6, row, ""));
						}

						int y = 1;

						int count = 0;
						for (int i = start; i <= end; i++) {
							if (i == 1) {
								sheet.addCell(new Label((y + z), row, o.getMonth1() == null ? "0" : o.getMonth1().toString()));
								count += o.getMonth1() == null ? 0 : o.getMonth1();
								count1 += o.getMonth1() == null ? 0 : o.getMonth1();
							}
							if (i == 2) {
								sheet.addCell(new Label((y + z), row, o.getMonth2() == null ? "0" : o.getMonth2().toString()));
								count += o.getMonth2() == null ? 0 : o.getMonth2();
								count2 += o.getMonth2() == null ? 0 : o.getMonth2();
							}
							if (i == 3) {
								sheet.addCell(new Label((y + z), row, o.getMonth3() == null ? "0" : o.getMonth3().toString()));
								count += o.getMonth3() == null ? 0 : o.getMonth3();
								count3 += o.getMonth3() == null ? 0 : o.getMonth3();
							}
							if (i == 4) {
								sheet.addCell(new Label((y + z), row, o.getMonth4() == null ? "0" : o.getMonth4().toString()));
								count += o.getMonth4() == null ? 0 : o.getMonth4();
								count4 += o.getMonth4() == null ? 0 : o.getMonth4();
							}
							if (i == 5) {
								sheet.addCell(new Label((y + z), row, o.getMonth5() == null ? "0" : o.getMonth5().toString()));
								count += o.getMonth5() == null ? 0 : o.getMonth5();
								count5 += o.getMonth5() == null ? 0 : o.getMonth5();
							}
							if (i == 6) {
								sheet.addCell(new Label((y + z), row, o.getMonth6() == null ? "0" : o.getMonth6().toString()));
								count += o.getMonth6() == null ? 0 : o.getMonth6();
								count6 += o.getMonth6() == null ? 0 : o.getMonth6();
							}
							if (i == 7) {
								sheet.addCell(new Label((y + z), row, o.getMonth7() == null ? "0" : o.getMonth7().toString()));
								count += o.getMonth7() == null ? 0 : o.getMonth7();
								count7 += o.getMonth7() == null ? 0 : o.getMonth7();
							}
							if (i == 8) {
								sheet.addCell(new Label((y + z), row, o.getMonth8() == null ? "0" : o.getMonth8().toString()));
								count += o.getMonth8() == null ? 0 : o.getMonth8();
								count8 += o.getMonth8() == null ? 0 : o.getMonth8();
							}
							if (i == 9) {
								sheet.addCell(new Label((y + z), row, o.getMonth9() == null ? "0" : o.getMonth9().toString()));
								count += o.getMonth9() == null ? 0 : o.getMonth9();
								count9 += o.getMonth9() == null ? 0 : o.getMonth9();
							}
							if (i == 10) {
								sheet.addCell(new Label((y + z), row, o.getMonth10() == null ? "0" : o.getMonth10().toString()));
								count += o.getMonth10() == null ? 0 : o.getMonth10();
								count10 += o.getMonth10() == null ? 0 : o.getMonth10();
							}
							if (i == 11) {
								sheet.addCell(new Label((y + z), row, o.getMonth11() == null ? "0" : o.getMonth11().toString()));
								count += o.getMonth11() == null ? 0 : o.getMonth11();
								count11 += o.getMonth11() == null ? 0 : o.getMonth11();
							}
							if (i == 12) {
								sheet.addCell(new Label((y + z), row, o.getMonth12() == null ? "0" : o.getMonth12().toString()));
								count += o.getMonth12() == null ? 0 : o.getMonth12();
								count12 += o.getMonth12() == null ? 0 : o.getMonth12();
							}
							y++;
						}
						sheet.addCell(new Label(z, row, String.valueOf(count)));
						row++;
					}
					countnum = count1 + count2 + count3 + count4 + count5 + count6 + count7 + count8 + count9 + count10 + count11 + count12;
					sheet.addCell(new Label(z, z + 1, String.valueOf(countnum)));
					for (int i = start; i <= end; i++) {
						if (i == 1) {

							sheet.addCell(new Label(z + i, z + 1, String.valueOf(count1)));
						}
						if (i == 2) {
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(count2)));
						}
						if (i == 3) {
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(count3)));
						}
						if (i == 4) {
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(count4)));
						}
						if (i == 5) {
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(count5)));
						}
						if (i == 6) {
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(count6)));
						}
						if (i == 7) {
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(count7)));
						}
						if (i == 8) {
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(count8)));
						}
						if (i == 9) {
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(count9)));
						}
						if (i == 10) {
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(count10)));
						}
						if (i == 11) {
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(count11)));
						}
						if (i == 12) {
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(count12)));
						}

					}
					workbook.write();
					workbook.close();
					os.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return null;
	}

	/**
	 * 下载图书/期刊全文拒访记录
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/downloadRefuseBookJournal")
	public ModelAndView downloadRefuseBookJournal(HttpServletRequest request, HttpServletResponse response, LAccessForm form) throws Exception {
		try {
			List<LAccess> list = null;
			form.setUrl(request.getRequestURL().toString());
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			if (user != null && (user.getLevel() == 2 || user.getLevel() == 3)) {
				if ((request.getParameter("type") != null && !"".equals(request.getParameter("type"))) || form.getType() != null) {
					Integer pubType = null;
					Integer accessType = null;
					Integer access = null;
					Integer languagetype = null;
					if (form.getPubtype() != null && form.getType() != null) {
						pubType = form.getPubtype();
						accessType = form.getType();
					} else {
						pubType = Integer.valueOf(request.getParameter("pubtype"));
						accessType = Integer.valueOf(request.getParameter("type"));
					}
					if (form.getAccess() != null) {
						access = form.getAccess();
					} else {
						access = Integer.valueOf(request.getParameter("access") != null ? request.getParameter("access") : "0");
					}

					Map<String, Object> condition = form.getCondition();
					if (pubType == 1) {// 图书分:中文-外文
						if (form.getLanguagetype() == null) {
							languagetype = Integer.valueOf(request.getParameter("languagetype"));
						} else {
							languagetype = form.getLanguagetype();
						}
						if (languagetype == 1) {

							condition.put("isPrecisebook", true);
						} else if (languagetype == 2) {

							condition.put("isPrecisebook", false);
						}
						condition.put("lang", new String[] { "chs", "cht" });
						condition.put("isPrecise", true);
					}

					condition.put("year", form.getYear());
					condition.put("startMonth", form.getStartMonth());
					condition.put("endMonth", form.getEndMonth());
					condition.put("type", accessType);
					if (accessType == 2) {
						condition.put("access", access);
					}

					// 区分期刊和图书
					if (pubType != null && pubType == 2) {

						condition.put("pubtypes", new Integer[] { 2, 4 });
					} else if (pubType != null && pubType == 1) {
						condition.put("pubtypes", new Integer[] { 1, 3 });
					}

					if (user.getInstitution() != null && user.getLevel() != 3) {
						condition.put("institutionId2", user.getInstitution().getId());
					}
					list = this.logAOPService.getLogOfYearToPagePaging4(condition, " group by a.publications.id ,a.refusedVisitType  ", 0, 0);

					int flag = 0;
					int z = 1;

					// 输出的excel文件工作表名
					String worksheet = "BookFullSuppliers" + form.getYear();
					StringBuffer sbb = new StringBuffer();
					if (pubType != null && pubType == 2) {
						sbb.append("Journal Report 2 (R4);");
						flag = 2;
						z = 8;
					} else if (pubType != null && pubType == 1) {
						sbb.append("Book Report 3 (R4);");
						flag = 1;
						z = 8;
					}

					sbb.append(" ;");
					sbb.append(" ;");
					sbb.append("Period covered by Report:;");
					sbb.append("yyyy-mm-dd to yyyy-mm-dd;");
					sbb.append("Date run:;");
					sbb.append("yyyy-mm-dd;");

					StringBuffer sb = new StringBuffer();
					if (flag == 2) {
						sb.append("Journal;");
						sb.append("Publisher;");
						sb.append("Platform;");
						sb.append("Journal DOI;");
						sb.append("Proprietary Identifier;");

						sb.append("Print ISSN;");
						sb.append("Online ISSN;");
						sb.append("Access Deried Category;");
						sb.append("Total;");
					} else if (flag == 1) {
						sb.append(" ;");
						sb.append("Publisher;");
						sb.append("Platform;");
						sb.append("Book DOI;");
						sb.append("Proprietary Identifier;");
						sb.append("ISBN;");
						sb.append("ISSN;");
						sb.append("Access Deried Category;");
						sb.append("Total;");
					}

					Integer start = Integer.valueOf(form.getStartMonth());
					Integer end = Integer.valueOf(form.getEndMonth());
					for (int i = start; i <= end; i++) {
						String month = "";
						if (i == 1) {
							month = "Jan";
						}
						if (i == 2) {
							month = "Feb";
						}
						if (i == 3) {
							month = "Mar";
						}
						if (i == 4) {
							month = "Apr";
						}
						if (i == 5) {
							month = "May";
						}
						if (i == 6) {
							month = "Jun";
						}
						if (i == 7) {
							month = "Jul";
						}
						if (i == 8) {
							month = "Aug";
						}
						if (i == 9) {
							month = "Sep";
						}
						if (i == 10) {
							month = "Oct";
						}
						if (i == 11) {
							month = "Nov";
						}
						if (i == 12) {
							month = "Dec";
						}
						sb.append(month + "-" + form.getYear() + ";");
					}

					StringBuffer sbf = new StringBuffer();
					if (flag == 1) {
						sbf.append("Total for all titles;");

					} else if (flag == 2) {
						sbf.append("Total for all journals;");
					}

					sbf.append(";");
					sbf.append(";");

					sbf.append(" ;");
					sbf.append(" ;");
					sbf.append(" ;");
					sbf.append(" ;");
					sbf.append("Access denied:content item not licenced;");

					int countnum = 0;
					int count1 = 0;
					int count2 = 0;
					int count3 = 0;
					int count4 = 0;
					int count5 = 0;
					int count6 = 0;
					int count7 = 0;
					int count8 = 0;
					int count9 = 0;
					int count10 = 0;
					int count11 = 0;
					int count12 = 0;

					sbf.append(countnum + ";");
					for (int i = start; i <= end; i++) {
						if (i == 1) {

							sbf.append(count1 + ";");
						}
						if (i == 2) {
							sbf.append(count2 + ";");
						}
						if (i == 3) {
							sbf.append(count3 + ";");
						}
						if (i == 4) {
							sbf.append(count4 + ";");
						}
						if (i == 5) {
							sbf.append(count5 + ";");
						}
						if (i == 6) {
							sbf.append(count6 + ";");
						}
						if (i == 7) {
							sbf.append(count7 + ";");
						}
						if (i == 8) {
							sbf.append(count8 + ";");
						}
						if (i == 9) {
							sbf.append(count9 + ";");
						}
						if (i == 10) {
							sbf.append(count10 + ";");
						}
						if (i == 11) {
							sbf.append(count11 + ";");
						}
						if (i == 12) {
							sbf.append(count12 + ";");
						}

					}
					StringBuffer sbfb = new StringBuffer();
					if (flag == 1) {
						sbfb.append("Total for all titles;");

					} else if (flag == 2) {
						sbfb.append("Total for all journals;");
					}

					sbfb.append(";");
					sbfb.append(" ;");

					sbfb.append(" ;");
					sbfb.append(" ;");
					sbfb.append(" ;");
					sbfb.append(" ;");
					sbfb.append("Access denied: concurrent/simultaneous user licence limit exceded;");

					int concurrent = 0;
					int con1 = 0;
					int con2 = 0;
					int con3 = 0;
					int con4 = 0;
					int con5 = 0;
					int con6 = 0;
					int con7 = 0;
					int con8 = 0;
					int con9 = 0;
					int con10 = 0;
					int con11 = 0;
					int con12 = 0;

					sbfb.append(concurrent + ";");
					for (int i = start; i <= end; i++) {
						if (i == 1) {

							sbfb.append(con1 + ";");
						}
						if (i == 2) {
							sbfb.append(con2 + ";");
						}
						if (i == 3) {
							sbfb.append(con3 + ";");
						}
						if (i == 4) {
							sbfb.append(con4 + ";");
						}
						if (i == 5) {
							sbfb.append(con5 + ";");
						}
						if (i == 6) {
							sbfb.append(con6 + ";");
						}
						if (i == 7) {
							sbfb.append(con7 + ";");
						}
						if (i == 8) {
							sbfb.append(con8 + ";");
						}
						if (i == 9) {
							sbfb.append(con9 + ";");
						}
						if (i == 10) {
							sbfb.append(con10 + ";");
						}
						if (i == 11) {
							sbfb.append(con11 + ";");
						}
						if (i == 12) {
							sbfb.append(con12 + ";");
						}

					}

					String title[] = sb.toString().split(";");
					String titles[] = sbb.toString().split(";");
					String titsbf[] = sbf.toString().split(";");
					String titsbfb[] = sbfb.toString().split(";");
					WritableWorkbook workbook;
					OutputStream os = response.getOutputStream();
					response.reset();// 清空输出流
					response.setHeader("Content-disposition", "attachment; filename=" + worksheet + ".xls");// 设定输出文件头
					response.setContentType("application/vnd.ms-excel;charset=UTF-8");// 定义输出类型
					workbook = Workbook.createWorkbook(os);
					WritableSheet sheet = workbook.createSheet(worksheet, 0);
					int num = 0;
					for (int i = 0; i < titles.length; i++) {
						sheet.addCell(new Label(0, i, titles[i]));
						num++;
					}

					for (int i = 0; i < num; i++) {
						if (i == 0) {
							sheet.addCell(new Label(1, i, "Number of Successfull T	Title Requests by Month and Title"));
						} else if (i == 1) {
							sheet.addCell(new Label(1, i, " "));
						} else if (i == 2) {
							sheet.addCell(new Label(1, i, " "));
						} else if (i == 3) {
							sheet.addCell(new Label(1, i, "Cnpereading.com"));
						} else if (i == 4) {
							sheet.addCell(new Label(1, i, form.getYear() + "-" + form.getStartMonth() + "-" + form.getEndMonth()));// 条件值
						} else if (i == 5) {
							sheet.addCell(new Label(1, i, " "));
						} else if (i == 6) {
							String s = UserMobileController.getNowbatchCooe();
							sheet.addCell(new Label(1, i, s));
						}
					}

					for (int i = 0; i < title.length; i++) {

						// Label(列号,行号 ,内容 )
						sheet.addCell(new Label(i, num, title[i]));
					}
					for (int i = 0; i < titsbf.length; i++) {

						// Label(列号,行号 ,内容 )
						sheet.addCell(new Label(i, num + 1, titsbf[i]));
					}
					for (int i = 0; i < titsbfb.length; i++) {
						// Label(列号,行号 ,内容 )
						sheet.addCell(new Label(i, num + 2, titsbfb[i]));
					}
					int row = num + 3;
					for (LAccess o : list) {
						sheet.addCell(new Label(0, row, o.getPublications().getTitle() != null ? o.getPublications().getTitle() : ""));
						sheet.addCell(new Label(1, row, o.getPublications().getPublisher().getName() != null ? o.getPublications().getPublisher().getName() : ""));
						sheet.addCell(new Label(2, row, ""));
						sheet.addCell(new Label(3, row, ""));
						sheet.addCell(new Label(4, row, ""));
						if (flag == 1) {
							sheet.addCell(new Label(5, row, o.getPublications().getCode() != null ? o.getPublications().getCode() : ""));
							sheet.addCell(new Label(6, row, ""));
						} else if (flag == 2) {
							sheet.addCell(new Label(5, row, o.getPublications().getCode() != null ? o.getPublications().getCode() : ""));
							sheet.addCell(new Label(6, row, ""));
						}
						String denied = "";
						if (o.getRefusedVisitType() == 1) {
							denied = "Access denied:content item not licenced";
						} else if (o.getRefusedVisitType() == 2) {
							denied = "Access denied: concurrent/simultaneous user licence limit exceded";
						}
						sheet.addCell(new Label(7, row, denied));

						int y = 1;

						int count = 0;
						for (int i = start; i <= end; i++) {
							if (i == 1) {
								sheet.addCell(new Label((y + z), row, o.getMonth1() == null ? "0" : o.getMonth1().toString()));
								count += o.getMonth1() == null ? 0 : o.getMonth1();
								if (o.getRefusedVisitType() == 1) {
									count1 += o.getMonth1() == null ? 0 : o.getMonth1();
								} else if (o.getRefusedVisitType() == 2) {
									con1 += o.getMonth1() == null ? 0 : o.getMonth1();
								}
							}
							if (i == 2) {
								sheet.addCell(new Label((y + z), row, o.getMonth2() == null ? "0" : o.getMonth2().toString()));
								count += o.getMonth2() == null ? 0 : o.getMonth2();
								if (o.getRefusedVisitType() == 1) {
									count2 += o.getMonth2() == null ? 0 : o.getMonth2();
								} else if (o.getRefusedVisitType() == 2) {
									con2 += o.getMonth2() == null ? 0 : o.getMonth2();
								}
							}
							if (i == 3) {
								sheet.addCell(new Label((y + z), row, o.getMonth3() == null ? "0" : o.getMonth3().toString()));
								count += o.getMonth3() == null ? 0 : o.getMonth3();
								if (o.getRefusedVisitType() == 1) {
									count3 += o.getMonth3() == null ? 0 : o.getMonth3();
								} else if (o.getRefusedVisitType() == 2) {
									con3 += o.getMonth3() == null ? 0 : o.getMonth3();
								}
							}
							if (i == 4) {
								sheet.addCell(new Label((y + z), row, o.getMonth4() == null ? "0" : o.getMonth4().toString()));
								count += o.getMonth4() == null ? 0 : o.getMonth4();
								if (o.getRefusedVisitType() == 1) {
									count4 += o.getMonth4() == null ? 0 : o.getMonth4();
								} else if (o.getRefusedVisitType() == 2) {
									con4 += o.getMonth4() == null ? 0 : o.getMonth4();
								}
							}
							if (i == 5) {
								sheet.addCell(new Label((y + z), row, o.getMonth5() == null ? "0" : o.getMonth5().toString()));
								count += o.getMonth5() == null ? 0 : o.getMonth5();
								if (o.getRefusedVisitType() == 1) {
									count5 += o.getMonth5() == null ? 0 : o.getMonth5();
								} else if (o.getRefusedVisitType() == 2) {
									con5 += o.getMonth5() == null ? 0 : o.getMonth5();
								}
							}
							if (i == 6) {
								sheet.addCell(new Label((y + z), row, o.getMonth6() == null ? "0" : o.getMonth6().toString()));
								count += o.getMonth6() == null ? 0 : o.getMonth6();
								if (o.getRefusedVisitType() == 1) {
									count6 += o.getMonth6() == null ? 0 : o.getMonth6();
								} else if (o.getRefusedVisitType() == 2) {
									con6 += o.getMonth6() == null ? 0 : o.getMonth6();
								}
							}
							if (i == 7) {
								sheet.addCell(new Label((y + z), row, o.getMonth7() == null ? "0" : o.getMonth7().toString()));
								count += o.getMonth7() == null ? 0 : o.getMonth7();
								if (o.getRefusedVisitType() == 1) {
									count7 += o.getMonth7() == null ? 0 : o.getMonth7();
								} else if (o.getRefusedVisitType() == 2) {
									con7 += o.getMonth7() == null ? 0 : o.getMonth7();
								}
							}
							if (i == 8) {
								sheet.addCell(new Label((y + z), row, o.getMonth8() == null ? "0" : o.getMonth8().toString()));
								count += o.getMonth8() == null ? 0 : o.getMonth8();
								if (o.getRefusedVisitType() == 1) {
									count8 += o.getMonth8() == null ? 0 : o.getMonth8();
								} else if (o.getRefusedVisitType() == 2) {
									con8 += o.getMonth8() == null ? 0 : o.getMonth8();
								}
							}
							if (i == 9) {
								sheet.addCell(new Label((y + z), row, o.getMonth9() == null ? "0" : o.getMonth9().toString()));
								count += o.getMonth9() == null ? 0 : o.getMonth9();
								if (o.getRefusedVisitType() == 1) {
									count9 += o.getMonth9() == null ? 0 : o.getMonth9();
								} else if (o.getRefusedVisitType() == 2) {
									con9 += o.getMonth9() == null ? 0 : o.getMonth9();
								}
							}
							if (i == 10) {
								sheet.addCell(new Label((y + z), row, o.getMonth10() == null ? "0" : o.getMonth10().toString()));
								count += o.getMonth10() == null ? 0 : o.getMonth10();
								if (o.getRefusedVisitType() == 1) {
									count10 += o.getMonth10() == null ? 0 : o.getMonth10();
								} else if (o.getRefusedVisitType() == 2) {
									con10 += o.getMonth10() == null ? 0 : o.getMonth10();
								}
							}
							if (i == 11) {
								sheet.addCell(new Label((y + z), row, o.getMonth11() == null ? "0" : o.getMonth11().toString()));
								count += o.getMonth11() == null ? 0 : o.getMonth11();
								if (o.getRefusedVisitType() == 1) {
									count11 += o.getMonth11() == null ? 0 : o.getMonth11();
								} else if (o.getRefusedVisitType() == 2) {
									con11 += o.getMonth11() == null ? 0 : o.getMonth11();
								}
							}
							if (i == 12) {
								sheet.addCell(new Label((y + z), row, o.getMonth12() == null ? "0" : o.getMonth12().toString()));
								count += o.getMonth12() == null ? 0 : o.getMonth12();
								if (o.getRefusedVisitType() == 1) {
									count12 += o.getMonth12() == null ? 0 : o.getMonth12();
								} else if (o.getRefusedVisitType() == 2) {
									con12 += o.getMonth12() == null ? 0 : o.getMonth12();
								}
							}
							y++;
						}
						sheet.addCell(new Label(z, row, String.valueOf(count)));
						row++;
					}

					countnum = count1 + count2 + count3 + count4 + count5 + count6 + count7 + count8 + count9 + count10 + count11 + count12;
					concurrent = con1 + con2 + con3 + con4 + con5 + con6 + con7 + con8 + con9 + con10 + con11 + con12;
					sheet.addCell(new Label(z, z + 1, String.valueOf(concurrent)));
					sheet.addCell(new Label(z, z, String.valueOf(countnum)));
					for (int i = start; i <= end; i++) {
						if (i == 1) {
							sheet.addCell(new Label(z + i, z, String.valueOf(count1)));
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(con1)));
						}
						if (i == 2) {
							sheet.addCell(new Label(z + i, z, String.valueOf(count2)));
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(con2)));
						}
						if (i == 3) {
							sheet.addCell(new Label(z + i, z, String.valueOf(count3)));
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(con3)));
						}
						if (i == 4) {
							sheet.addCell(new Label(z + i, z, String.valueOf(count4)));
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(con4)));
						}
						if (i == 5) {
							sheet.addCell(new Label(z + i, z, String.valueOf(count5)));
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(con5)));
						}
						if (i == 6) {
							sheet.addCell(new Label(z + i, z, String.valueOf(count6)));
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(con6)));
						}
						if (i == 7) {
							sheet.addCell(new Label(z + i, z, String.valueOf(count7)));
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(con7)));
						}
						if (i == 8) {
							sheet.addCell(new Label(z + i, z, String.valueOf(count8)));
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(con7)));
						}
						if (i == 9) {
							sheet.addCell(new Label(z + i, z, String.valueOf(count9)));
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(con9)));
						}
						if (i == 10) {
							sheet.addCell(new Label(z + i, z, String.valueOf(count10)));
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(con10)));
						}
						if (i == 11) {
							sheet.addCell(new Label(z + i, z, String.valueOf(count11)));
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(con11)));
						}
						if (i == 12) {
							sheet.addCell(new Label(z + i, z, String.valueOf(count12)));
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(con12)));
						}

					}
					workbook.write();
					workbook.close();
					os.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return null;
	}

	/**
	 * 下载图书/期刊搜索记录
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/downloadSearchesBookJournal")
	public ModelAndView downloadSearchesBookJournal(HttpServletRequest request, HttpServletResponse response, LAccessForm form) throws Exception {
		try {
			List<LAccess> list = null;
			form.setUrl(request.getRequestURL().toString());
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			if (user != null && (user.getLevel() == 2 || user.getLevel() == 3)) {
				if ((request.getParameter("type") != null && !"".equals(request.getParameter("type"))) || form.getType() != null) {
					Integer pubType = null;
					Integer accessType = null;
					Integer access = null;
					Integer languagetype = null;
					if (form.getPubtype() != null && form.getType() != null) {
						pubType = form.getPubtype();
						accessType = form.getType();
					} else {
						pubType = Integer.valueOf(request.getParameter("pubtype"));
						accessType = Integer.valueOf(request.getParameter("type"));
					}
					if (form.getAccess() != null) {
						access = form.getAccess();
					} else {
						access = Integer.valueOf(request.getParameter("access") != null ? request.getParameter("access") : "0");
					}

					Map<String, Object> condition = form.getCondition();
					if (pubType == 1) {// 图书分:中文-外文
						if (form.getLanguagetype() == null) {
							languagetype = Integer.valueOf(request.getParameter("languagetype"));
						} else {
							languagetype = form.getLanguagetype();
						}
						if (languagetype == 1) {

							condition.put("isPrecisebook", true);
						} else if (languagetype == 2) {

							condition.put("isPrecisebook", false);
						}
						condition.put("lang", new String[] { "chs", "cht" });
						condition.put("isPrecise", true);
					}

					condition.put("year", form.getYear());
					condition.put("startMonth", form.getStartMonth());
					condition.put("endMonth", form.getEndMonth());
					condition.put("type", accessType);
					if (accessType == 2) {
						condition.put("access", access);
					}

					// 区分期刊和图书
					if (pubType != null && pubType == 2) {

						condition.put("pubtypes", new Integer[] { 2, 4 });
					} else if (pubType != null && pubType == 1) {
						condition.put("pubtypes", new Integer[] { 1, 3 });
					}

					if (user.getInstitution() != null && user.getLevel() != 3) {
						condition.put("institutionId2", user.getInstitution().getId());
					}
					list = this.logAOPService.getLogOfYearToSearchPaging2(condition, " group by a.publications.id ", 0, 0);

					int flag = 0;
					int z = 1;

					// 输出的excel文件工作表名
					String worksheet = "BookFullSuppliers" + form.getYear();
					StringBuffer sbb = new StringBuffer();
					if (pubType != null && pubType == 2) {
						sbb.append("Journal Report 5 (R4);");
						flag = 2;
						z = 8;
					} else if (pubType != null && pubType == 1) {
						sbb.append("Book Report 5 (R4);");
						flag = 1;
						z = 8;
					}

					sbb.append(" ;");
					sbb.append(" ;");
					sbb.append("Period covered by Report:;");
					sbb.append("yyyy-mm-dd to yyyy-mm-dd;");
					sbb.append("Date run:;");
					sbb.append("yyyy-mm-dd;");

					StringBuffer sb = new StringBuffer();
					if (flag == 2) {
						sb.append("Journal;");
						sb.append("Publisher;");
						sb.append("Platform;");
						sb.append("Journal DOI;");
						sb.append("Proprietary Identifier;");

						sb.append("Print ISSN;");
						sb.append("Online ISSN;");
						sb.append("User activty;");
						sb.append("Total;");
					} else if (flag == 1) {
						sb.append(" ;");
						sb.append("Publisher;");
						sb.append("Platform;");
						sb.append("Book DOI;");
						sb.append("Proprietary Identifier;");
						sb.append("ISBN;");
						sb.append("ISSN;");
						sb.append("User activty;");
						sb.append("Total;");
					}

					Integer start = Integer.valueOf(form.getStartMonth());
					Integer end = Integer.valueOf(form.getEndMonth());
					for (int i = start; i <= end; i++) {
						String month = "";
						if (i == 1) {
							month = "Jan";
						}
						if (i == 2) {
							month = "Feb";
						}
						if (i == 3) {
							month = "Mar";
						}
						if (i == 4) {
							month = "Apr";
						}
						if (i == 5) {
							month = "May";
						}
						if (i == 6) {
							month = "Jun";
						}
						if (i == 7) {
							month = "Jul";
						}
						if (i == 8) {
							month = "Aug";
						}
						if (i == 9) {
							month = "Sep";
						}
						if (i == 10) {
							month = "Oct";
						}
						if (i == 11) {
							month = "Nov";
						}
						if (i == 12) {
							month = "Dec";
						}
						sb.append(month + "-" + form.getYear() + ";");
					}

					StringBuffer sbf = new StringBuffer();
					if (flag == 1) {
						sbf.append("Total searches;");

					} else if (flag == 2) {
						sbf.append("Total searches journals;");
					}

					sbf.append(";");
					sbf.append(";");

					sbf.append(" ;");
					sbf.append(" ;");
					sbf.append(" ;");
					sbf.append(" ;");
					sbf.append(" ;");

					int countnum = 0;
					int count1 = 0;
					int count2 = 0;
					int count3 = 0;
					int count4 = 0;
					int count5 = 0;
					int count6 = 0;
					int count7 = 0;
					int count8 = 0;
					int count9 = 0;
					int count10 = 0;
					int count11 = 0;
					int count12 = 0;

					sbf.append(countnum + ";");
					for (int i = start; i <= end; i++) {
						if (i == 1) {

							sbf.append(count1 + ";");
						}
						if (i == 2) {
							sbf.append(count2 + ";");
						}
						if (i == 3) {
							sbf.append(count3 + ";");
						}
						if (i == 4) {
							sbf.append(count4 + ";");
						}
						if (i == 5) {
							sbf.append(count5 + ";");
						}
						if (i == 6) {
							sbf.append(count6 + ";");
						}
						if (i == 7) {
							sbf.append(count7 + ";");
						}
						if (i == 8) {
							sbf.append(count8 + ";");
						}
						if (i == 9) {
							sbf.append(count9 + ";");
						}
						if (i == 10) {
							sbf.append(count10 + ";");
						}
						if (i == 11) {
							sbf.append(count11 + ";");
						}
						if (i == 12) {
							sbf.append(count12 + ";");
						}

					}

					StringBuffer sbfb = new StringBuffer();
					if (flag == 1) {
						sbfb.append("Total searches:federated and automated;");

					} else if (flag == 2) {
						sbfb.append("Total searches:federated and automated all journals;");
					}

					sbfb.append(";");
					sbfb.append(" ;");

					sbfb.append(" ;");
					sbfb.append(" ;");
					sbfb.append(" ;");
					sbfb.append(" ;");
					sbfb.append(" ;");

					int concurrent = 0;
					int con1 = 0;
					int con2 = 0;
					int con3 = 0;
					int con4 = 0;
					int con5 = 0;
					int con6 = 0;
					int con7 = 0;
					int con8 = 0;
					int con9 = 0;
					int con10 = 0;
					int con11 = 0;
					int con12 = 0;

					sbfb.append(concurrent + ";");
					for (int i = start; i <= end; i++) {
						if (i == 1) {

							sbfb.append(con1 + ";");
						}
						if (i == 2) {
							sbfb.append(con2 + ";");
						}
						if (i == 3) {
							sbfb.append(con3 + ";");
						}
						if (i == 4) {
							sbfb.append(con4 + ";");
						}
						if (i == 5) {
							sbfb.append(con5 + ";");
						}
						if (i == 6) {
							sbfb.append(con6 + ";");
						}
						if (i == 7) {
							sbfb.append(con7 + ";");
						}
						if (i == 8) {
							sbfb.append(con8 + ";");
						}
						if (i == 9) {
							sbfb.append(con9 + ";");
						}
						if (i == 10) {
							sbfb.append(con10 + ";");
						}
						if (i == 11) {
							sbfb.append(con11 + ";");
						}
						if (i == 12) {
							sbfb.append(con12 + ";");
						}

					}

					String title[] = sb.toString().split(";");
					String titles[] = sbb.toString().split(";");
					String titsbf[] = sbf.toString().split(";");
					String titsbfb[] = sbfb.toString().split(";");
					WritableWorkbook workbook;
					OutputStream os = response.getOutputStream();
					response.reset();// 清空输出流
					response.setHeader("Content-disposition", "attachment; filename=" + worksheet + ".xls");// 设定输出文件头
					response.setContentType("application/vnd.ms-excel;charset=UTF-8");// 定义输出类型
					workbook = Workbook.createWorkbook(os);
					WritableSheet sheet = workbook.createSheet(worksheet, 0);
					int num = 0;
					for (int i = 0; i < titles.length; i++) {
						sheet.addCell(new Label(0, i, titles[i]));
						num++;
					}

					for (int i = 0; i < num; i++) {
						if (i == 0) {
							sheet.addCell(new Label(1, i, "Number of Successfull T	Title Requests by Month and Title"));
						} else if (i == 1) {
							sheet.addCell(new Label(1, i, " "));
						} else if (i == 2) {
							sheet.addCell(new Label(1, i, " "));
						} else if (i == 3) {
							sheet.addCell(new Label(1, i, "Cnpereading.com"));
						} else if (i == 4) {
							sheet.addCell(new Label(1, i, form.getYear() + "-" + form.getStartMonth() + "-" + form.getEndMonth()));// 条件值
						} else if (i == 5) {
							sheet.addCell(new Label(1, i, " "));
						} else if (i == 6) {
							String s = UserMobileController.getNowbatchCooe();
							sheet.addCell(new Label(1, i, s));
						}
					}

					for (int i = 0; i < title.length; i++) {

						// Label(列号,行号 ,内容 )
						sheet.addCell(new Label(i, num, title[i]));
					}
					for (int i = 0; i < titsbf.length; i++) {

						// Label(列号,行号 ,内容 )
						sheet.addCell(new Label(i, num + 1, titsbf[i]));
					}
					for (int i = 0; i < titsbfb.length; i++) {
						// Label(列号,行号 ,内容 )
						sheet.addCell(new Label(i, num + 2, titsbfb[i]));
					}

					int row = num + 3;
					for (LAccess o : list) {
						sheet.addCell(new Label(0, row, o.getPublications().getTitle() != null ? o.getPublications().getTitle() : ""));
						sheet.addCell(new Label(1, row, o.getPublications().getPublisher().getName() != null ? o.getPublications().getPublisher().getName() : ""));
						sheet.addCell(new Label(2, row, ""));
						sheet.addCell(new Label(3, row, ""));
						sheet.addCell(new Label(4, row, ""));
						if (flag == 1) {
							sheet.addCell(new Label(5, row, o.getPublications().getCode() != null ? o.getPublications().getCode() : ""));
							sheet.addCell(new Label(6, row, ""));
						} else if (flag == 2) {
							sheet.addCell(new Label(5, row, o.getPublications().getCode() != null ? o.getPublications().getCode() : ""));
							sheet.addCell(new Label(6, row, ""));
						}
						/*
						 * String denied=""; if(o.getRefusedVisitType()==1){
						 * denied="Access denied:content item not licenced";
						 * }else if(o.getRefusedVisitType()==2){ denied=
						 * "Access denied: concurrent/simultaneous user licence limit exceded"
						 * ; }
						 */
						sheet.addCell(new Label(7, row, "Regular Searches"));

						int y = 1;

						int count = 0;
						for (int i = start; i <= end; i++) {
							if (i == 1) {
								sheet.addCell(new Label((y + z), row, o.getMonth1() == null ? "0" : o.getMonth1().toString()));
								count += o.getMonth1() == null ? 0 : o.getMonth1();

								count1 += o.getMonth1() == null ? 0 : o.getMonth1();

							}
							if (i == 2) {
								sheet.addCell(new Label((y + z), row, o.getMonth2() == null ? "0" : o.getMonth2().toString()));
								count += o.getMonth2() == null ? 0 : o.getMonth2();

								count2 += o.getMonth2() == null ? 0 : o.getMonth2();

							}
							if (i == 3) {
								sheet.addCell(new Label((y + z), row, o.getMonth3() == null ? "0" : o.getMonth3().toString()));
								count += o.getMonth3() == null ? 0 : o.getMonth3();

								count3 += o.getMonth3() == null ? 0 : o.getMonth3();

							}
							if (i == 4) {
								sheet.addCell(new Label((y + z), row, o.getMonth4() == null ? "0" : o.getMonth4().toString()));
								count += o.getMonth4() == null ? 0 : o.getMonth4();

								count4 += o.getMonth4() == null ? 0 : o.getMonth4();

							}
							if (i == 5) {
								sheet.addCell(new Label((y + z), row, o.getMonth5() == null ? "0" : o.getMonth5().toString()));
								count += o.getMonth5() == null ? 0 : o.getMonth5();

								count5 += o.getMonth5() == null ? 0 : o.getMonth5();

							}
							if (i == 6) {
								sheet.addCell(new Label((y + z), row, o.getMonth6() == null ? "0" : o.getMonth6().toString()));
								count += o.getMonth6() == null ? 0 : o.getMonth6();

								count6 += o.getMonth6() == null ? 0 : o.getMonth6();

							}
							if (i == 7) {
								sheet.addCell(new Label((y + z), row, o.getMonth7() == null ? "0" : o.getMonth7().toString()));
								count += o.getMonth7() == null ? 0 : o.getMonth7();

								count7 += o.getMonth7() == null ? 0 : o.getMonth7();

							}
							if (i == 8) {
								sheet.addCell(new Label((y + z), row, o.getMonth8() == null ? "0" : o.getMonth8().toString()));
								count += o.getMonth8() == null ? 0 : o.getMonth8();

								count8 += o.getMonth8() == null ? 0 : o.getMonth8();

							}
							if (i == 9) {
								sheet.addCell(new Label((y + z), row, o.getMonth9() == null ? "0" : o.getMonth9().toString()));
								count += o.getMonth9() == null ? 0 : o.getMonth9();

								count9 += o.getMonth9() == null ? 0 : o.getMonth9();

							}
							if (i == 10) {
								sheet.addCell(new Label((y + z), row, o.getMonth10() == null ? "0" : o.getMonth10().toString()));
								count += o.getMonth10() == null ? 0 : o.getMonth10();

								count10 += o.getMonth10() == null ? 0 : o.getMonth10();

							}
							if (i == 11) {
								sheet.addCell(new Label((y + z), row, o.getMonth11() == null ? "0" : o.getMonth11().toString()));
								count += o.getMonth11() == null ? 0 : o.getMonth11();

								count11 += o.getMonth11() == null ? 0 : o.getMonth11();

							}
							if (i == 12) {
								sheet.addCell(new Label((y + z), row, o.getMonth12() == null ? "0" : o.getMonth12().toString()));
								count += o.getMonth12() == null ? 0 : o.getMonth12();

								count12 += o.getMonth12() == null ? 0 : o.getMonth12();

							}
							y++;
						}
						sheet.addCell(new Label(z, row, String.valueOf(count)));

						row++;

					}

					countnum = count1 + count2 + count3 + count4 + count5 + count6 + count7 + count8 + count9 + count10 + count11 + count12;
					concurrent = con1 + con2 + con3 + con4 + con5 + con6 + con7 + con8 + con9 + con10 + con11 + con12;
					sheet.addCell(new Label(z, z + 1, String.valueOf(concurrent)));
					sheet.addCell(new Label(z, z, String.valueOf(countnum)));
					for (int i = start; i <= end; i++) {
						if (i == 1) {
							sheet.addCell(new Label(z + i, z, String.valueOf(count1)));
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(con1)));
						}
						if (i == 2) {
							sheet.addCell(new Label(z + i, z, String.valueOf(count2)));
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(con2)));
						}
						if (i == 3) {
							sheet.addCell(new Label(z + i, z, String.valueOf(count3)));
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(con3)));
						}
						if (i == 4) {
							sheet.addCell(new Label(z + i, z, String.valueOf(count4)));
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(con4)));
						}
						if (i == 5) {
							sheet.addCell(new Label(z + i, z, String.valueOf(count5)));
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(con5)));
						}
						if (i == 6) {
							sheet.addCell(new Label(z + i, z, String.valueOf(count6)));
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(con6)));
						}
						if (i == 7) {
							sheet.addCell(new Label(z + i, z, String.valueOf(count7)));
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(con7)));
						}
						if (i == 8) {
							sheet.addCell(new Label(z + i, z, String.valueOf(count8)));
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(con7)));
						}
						if (i == 9) {
							sheet.addCell(new Label(z + i, z, String.valueOf(count9)));
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(con9)));
						}
						if (i == 10) {
							sheet.addCell(new Label(z + i, z, String.valueOf(count10)));
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(con10)));
						}
						if (i == 11) {
							sheet.addCell(new Label(z + i, z, String.valueOf(count11)));
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(con11)));
						}
						if (i == 12) {
							sheet.addCell(new Label(z + i, z, String.valueOf(count12)));
							sheet.addCell(new Label(z + i, z + 1, String.valueOf(con12)));
						}

					}
					workbook.write();
					workbook.close();
					os.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return null;
	}

	/**
	 * 日期的yyyyMMddhhmmss形式
	 * 
	 * @param strDate
	 * @return
	 */
	public static String getNowbatchCooe() {
		java.text.DateFormat format2 = new java.text.SimpleDateFormat("yyyy-MM-dd");
		String s = format2.format(new Date());
		return s;
	}

	@RequestMapping(value = "/getSearchLogForSource/{sourceId}/{year}/{startMonth}/{endMonth}")
	public void getSearchLogForSource(@PathVariable String sourceId, @PathVariable String year, @PathVariable String startMonth, @PathVariable String endMonth, HttpServletRequest request, Model model) {
		ResultObject<LAccess> result = null;
		try {
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("sourceId", sourceId);
			condition.put("year", year);
			condition.put("type", 3);
			condition.put("access", 1);
			condition.put("startMonth", startMonth);
			condition.put("endMonth", endMonth);
			Boolean pageing = Boolean.valueOf((request.getParameter("Paging") == null ? "false" : (!"false".equalsIgnoreCase(request.getParameter("Paging").toString()) && !"true".equalsIgnoreCase(request.getParameter("Paging").toString()) ? "false" : request.getParameter("Paging").toString())));
			List<LAccess> list = null;
			if (pageing) {
				int pageCount = 10;
				int curpage = 0;
				pageCount = request.getParameter("pageCount") == null ? 10 : Integer.valueOf(request.getParameter("pageCount").toString());
				curpage = request.getParameter("curpage") == null ? 10 : Integer.valueOf(request.getParameter("curpage").toString());
				list = this.logAOPService.getLogOfYearPagingForSource(condition, "", pageCount, curpage);
			} else {
				if (request.getParameter("Paging") != null && "count".equalsIgnoreCase(request.getParameter("Paging").toString())) {
					list = this.logAOPService.getLogOfYearCountForSource(condition);
					if (list == null || list.isEmpty()) {
						list = new ArrayList<LAccess>();
						LAccess access = new LAccess();
						access.setId("0");
						list.add(access);
					} else {
						int num = list.size();
						list = new ArrayList<LAccess>();
						LAccess access = new LAccess();
						access.setId(String.valueOf(num));
						list.add(access);
					}
				} else {
					list = this.logAOPService.getLogOfYearForSource(condition, "");
				}
			}
			if (list != null && !list.isEmpty()) {
				ObjectUtil<LAccess> util = new ObjectUtil<LAccess>();
				for (int i = 0; i < list.size(); i++) {
					util.setNull(list.get(i), new String[] { Set.class.getName(), List.class.getName() });
					if (list.get(i).getPublications() != null) {
						ObjectUtil<PPublications> putil = new ObjectUtil<PPublications>();
						putil.setNull(list.get(i).getPublications(), new String[] { Set.class.getName(), List.class.getName() });
					}
					if (list.get(i).getLicense() != null) {
						ObjectUtil<LLicense> lutil = new ObjectUtil<LLicense>();
						lutil.setNull(list.get(i).getLicense(), new String[] { Set.class.getName(), List.class.getName() });
					}
				}
			}
			result = new ResultObject<LAccess>(1, list, Lang.getLanguage("Controller.User.getLog.query.success", request.getSession().getAttribute("lang").toString()));// "获取订单详情列表成功！");
		} catch (Exception e) {
			e.printStackTrace();
			result = new ResultObject<LAccess>(2, (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		model.addAttribute("target", result);
	}

	@RequestMapping(value = "/getPublicationLogForSource/{sourceId}/{year}/{startMonth}/{endMonth}")
	public void getPublicationLogForSource(@PathVariable String sourceId, @PathVariable String year, @PathVariable String startMonth, @PathVariable String endMonth, HttpServletRequest request, Model model) {
		ResultObject<LAccess> result = null;
		try {
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("sourceId", sourceId);
			condition.put("year", year);
			condition.put("types", new Integer[] { 1, 2 });
			condition.put("access", 1);
			condition.put("startMonth", startMonth);
			condition.put("endMonth", endMonth);
			Boolean pageing = Boolean.valueOf((request.getParameter("Paging") == null ? "false" : (!"false".equalsIgnoreCase(request.getParameter("Paging").toString()) && !"true".equalsIgnoreCase(request.getParameter("Paging").toString()) ? "false" : request.getParameter("Paging").toString())));
			List<LAccess> list = null;
			if (pageing) {
				int pageCount = 10;
				int curpage = 0;
				pageCount = request.getParameter("pageCount") == null ? 10 : Integer.valueOf(request.getParameter("pageCount").toString());
				curpage = request.getParameter("curpage") == null ? 10 : Integer.valueOf(request.getParameter("curpage").toString());
				list = this.logAOPService.getLogOfPubPagingForSource(condition, "", pageCount, curpage);
			} else {
				if (request.getParameter("Paging") != null && "count".equalsIgnoreCase(request.getParameter("Paging").toString())) {
					list = this.logAOPService.getLogOfPubCountForSource(condition);
					if (list == null || list.isEmpty()) {
						list = new ArrayList<LAccess>();
						LAccess access = new LAccess();
						access.setId("0");
						list.add(access);
					} else {
						int num = list.size();
						list = new ArrayList<LAccess>();
						LAccess access = new LAccess();
						access.setId(String.valueOf(num));
						list.add(access);
					}
				} else {
					list = this.logAOPService.getLogOfPubForSource(condition, "");
				}
			}

			if (list != null && !list.isEmpty()) {
				ObjectUtil<LAccess> util = new ObjectUtil<LAccess>();
				for (int i = 0; i < list.size(); i++) {
					util.setNull(list.get(i), new String[] { Set.class.getName(), List.class.getName() });
					if (list.get(i).getPublications() != null) {
						ObjectUtil<PPublications> putil = new ObjectUtil<PPublications>();
						putil.setNull(list.get(i).getPublications(), new String[] { Set.class.getName(), List.class.getName() });
					}
					if (list.get(i).getLicense() != null) {
						ObjectUtil<LLicense> lutil = new ObjectUtil<LLicense>();
						lutil.setNull(list.get(i).getLicense(), new String[] { Set.class.getName(), List.class.getName() });
					}
				}
			}
			result = new ResultObject<LAccess>(1, list, Lang.getLanguage("Controller.User.getLog.query.success", request.getSession().getAttribute("lang").toString()));// "获取订单详情列表成功！");
		} catch (Exception e) {
			e.printStackTrace();
			result = new ResultObject<LAccess>(2, (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		model.addAttribute("target", result);
	}

	@RequestMapping(value = "/form/Log")
	public void mySubscriptionLog(HttpServletRequest request, HttpServletResponse response, HttpSession session, LLicenseForm form) {
		try {
			List<LLicense> list = null;
			CUser user = (CUser) session.getAttribute("mainUser");
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("status", 1);
			if (form.getSearchType() != null && !"".equals(form.getSearchType())) {
				if (form.getSearchType() == 1) {
					condition.put("searchType", new Integer[] { 1, 3 });// 图书
																		// 、章节
				} else if (form.getSearchType() == 2) {
					condition.put("searchType", new Integer[] { 2, 4, 6, 7 });
				} else if (form.getSearchType() == 3) {
					condition.put("searchType", new Integer[] { 5 });
				} else if (form.getSearchType() == 99) {
					condition.put("collections", "0");
				}
			}

			/**
			 * 查看范围 1-个人订阅 2-机构订阅 3-所有
			 */
			if (form.getRange() == null || form.getRange() == 3) {
				condition.put("userid", user.getId());
				// if (session.getAttribute("institution") != null) {//
				// 查看session中保存的机构订阅信息
				condition.put("level", 2);
				condition.put("institutionId", ((BInstitution) session.getAttribute("institution")).getId());
				// }
				list = this.customService.getLicenseList(condition, "order by a.createdon desc ");
			} else if (form.getRange() == 1) {
				condition.put("userid", user.getId());
				list = this.customService.getLicenseList(condition, "order by a.createdon desc ");
			} else if (form.getRange() == 2) {
				// if (session.getAttribute("institution") != null) {//
				// 查看session中保存的机构订阅信息
				condition.put("status", 1);
				condition.put("isTrail", "0");
				condition.put("level", 2);
				condition.put("institutionId", session.getAttribute("institution") != null ? ((BInstitution) session.getAttribute("institution")).getId() != null : user.getInstitution().getId());
				list = this.customService.getLicenseList(condition, "order by a.createdon desc ");
				/*
				 * } else if (user.getInstitution() != null &&
				 * user.getInstitution().getId() != null) {//
				 * 查看session中登录用户的机构订阅信息 condition.put("institutionId",
				 * user.getInstitution().getId()); list =
				 * this.customService.getLicenseList(condition,
				 * "order by a.createdon desc "); }
				 */
			}

			// 输出的excel文件工作表名
			String worksheet = "mySubscriptionLog";
			// excel工作表的标题
			String[] title = { "Title", "Author", "PubDate", "Price", "Date of purchase", "Remark", "Create Date", "Type", "Begin", "End" };

			WritableWorkbook workbook;
			OutputStream os = response.getOutputStream();
			response.reset();// 清空输出流
			response.setHeader("Content-disposition", "attachment; filename=mySubscription.xls");// 设定输出文件头
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");// 定义输出类型

			workbook = Workbook.createWorkbook(os);

			WritableSheet sheet = workbook.createSheet(worksheet, 0);

			for (int i = 0; i < title.length; i++) {
				// Label(列号,行号 ,内容 )
				sheet.addCell(new Label(i, 0, title[i]));
			}

			int row = 1;
			for (LLicense l : list) {
				if (l.getPublications() != null) {
					sheet.addCell(new Label(0, row, l.getPublications().getTitle()));
					sheet.addCell(new Label(1, row, l.getPublications().getAuthor()));
					if (form.getSearchType() != null && form.getSearchType() == 3) {
						sheet.addCell(new Label(2, row, l.getPublications().getCreateDate()));
					} else {
						sheet.addCell(new Label(2, row, l.getPublications().getPubDate()));
					}
					sheet.addCell(new Label(3, row, l.getPublications().getListPrice() == null ? "N/A" : l.getPublications().getListPrice().toString()));
					sheet.addCell(new Label(4, row, DateUtil.getDateStr("yyyy-MM-dd", l.getCreatedon())));
					sheet.addCell(new Label(5, row, l.getPublications().getRemark()));
					sheet.addCell(new Label(6, row, DateUtil.getDateStr("yyyy-MM-dd", l.getCreatedon())));
					if (l.getType() == 1) {
						sheet.addCell(new Label(7, row, Lang.getLanguage("Pages.User.Subscription.Table.Label.type1", request.getSession().getAttribute("lang").toString())));
						sheet.addCell(new Label(8, row, "N/A"));
						sheet.addCell(new Label(9, row, "N/A"));
					} else {
						sheet.addCell(new Label(7, row, Lang.getLanguage("Pages.User.Subscription.Table.Label.type2", request.getSession().getAttribute("lang").toString())));
						sheet.addCell(new Label(8, row, DateUtil.getDateStr("yyyy-MM-dd", l.getStartTime())));
						sheet.addCell(new Label(9, row, l.getEndTime() == null ? "N/A" : DateUtil.getDateStr("yyyy-MM-dd", l.getEndTime())));
					}
				} else {
					// 产品包
					sheet.addCell(new Label(0, row, l.getCollection().getName()));
					sheet.addCell(new Label(1, row, ""));
					sheet.addCell(new Label(2, row, DateUtil.getDateStr("yyyy-MM-dd", l.getCollection().getCreateOn())));
					sheet.addCell(new Label(3, row, l.getCollection().getPrice() == null ? "N/A" : l.getCollection().getPrice().toString()));
					sheet.addCell(new Label(4, row, DateUtil.getDateStr("yyyy-MM-dd", l.getCreatedon())));
					sheet.addCell(new Label(5, row, l.getCollection().getDesc()));
					sheet.addCell(new Label(6, row, DateUtil.getDateStr("yyyy-MM-dd", l.getCreatedon())));
					if (l.getType() == 1) {
						sheet.addCell(new Label(7, row, Lang.getLanguage("Pages.User.Subscription.Table.Label.type1", request.getSession().getAttribute("lang").toString())));
						sheet.addCell(new Label(8, row, "N/A"));
						sheet.addCell(new Label(9, row, "N/A"));
					} else {
						sheet.addCell(new Label(7, row, Lang.getLanguage("Pages.User.Subscription.Table.Label.type2", request.getSession().getAttribute("lang").toString())));
						sheet.addCell(new Label(8, row, DateUtil.getDateStr("yyyy-MM-dd", l.getStartTime())));
						sheet.addCell(new Label(9, row, l.getEndTime() == null ? "N/A" : DateUtil.getDateStr("yyyy-MM-dd", l.getEndTime())));
					}
				}
				row++;
			}

			workbook.write();
			workbook.close();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 下载
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param form
	 */
	@RequestMapping(value = "/form/collection/mySubscriptionLog")
	public void myCollectionSubscriptionLog(HttpServletRequest request, HttpServletResponse response, HttpSession session, LLicenseForm form) {
		try {
			List<OOrderDetail> list = null;
			CUser user = (CUser) session.getAttribute("mainUser");
			if (session.getAttribute("mainUser") != null) {
				Map<String, Object> condition = form.getCondition();
				condition.put("status", 3);
				condition.put("itemType", 99);
				form.setSearchType(4);// 产品包
				if (session.getAttribute("institution") != null) {// 查看session中保存的机构订阅信息
					condition.put("institutionId", ((BInstitution) session.getAttribute("institution")).getId());
					form.setCount(this.oOrderService.getOrderDetailCount(condition));
					list = this.oOrderService.getOrderDetailPagingList(condition, "order by a.createdon desc ", form.getPageCount(), form.getCurpage());
				} else if (user.getInstitution() != null && user.getInstitution().getId() != null) {// 查看session中登录用户的机构订阅信息
					condition.put("institutionId", user.getInstitution().getId());
					form.setCount(this.oOrderService.getOrderDetailCount(condition));
					list = this.oOrderService.getOrderDetailPagingList(condition, "order by a.createdon desc ", form.getPageCount(), form.getCurpage());
				}
			}

			// 输出的excel文件工作表名
			String worksheet = "myCollectionSubscriptionLog";
			// excel工作表的标题
			String[] title = { "Title", "Author", "CreateDate", "Price", "Date of purchase", "Remark", "Type", "Begin", "End" };

			WritableWorkbook workbook;
			OutputStream os = response.getOutputStream();
			response.reset();// 清空输出流
			response.setHeader("Content-disposition", "attachment; filename=myCollectionSubscriptionLog.xls");// 设定输出文件头
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");// 定义输出类型

			workbook = Workbook.createWorkbook(os);

			WritableSheet sheet = workbook.createSheet(worksheet, 0);

			for (int i = 0; i < title.length; i++) {
				// Label(列号,行号 ,内容 )
				sheet.addCell(new Label(i, 0, title[i]));
			}

			int row = 1;
			for (OOrderDetail l : list) {
				sheet.addCell(new Label(0, row, l.getCollection().getName()));
				sheet.addCell(new Label(1, row, null));
				sheet.addCell(new Label(2, row, DateUtil.getDateStr("yyyy-MM-dd", l.getCollection().getCreateOn())));
				sheet.addCell(new Label(3, row, l.getCollection().getPrice() == null ? "N/A" : l.getCollection().getPrice().toString()));
				sheet.addCell(new Label(4, row, l.getCollection().getCode()));
				sheet.addCell(new Label(5, row, l.getCollection().getDesc()));
				sheet.addCell(new Label(6, row, DateUtil.getDateStr("yyyy-MM-dd", l.getCreatedon())));
				sheet.addCell(new Label(7, row, Lang.getLanguage("Pages.User.Subscription.Table.Label.type1", request.getSession().getAttribute("lang").toString())));
				sheet.addCell(new Label(8, row, "N/A"));
				sheet.addCell(new Label(9, row, "N/A"));
				row++;
			}

			workbook.write();
			workbook.close();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/form/userTypeManage")
	public ModelAndView userTypeManage(HttpServletRequest request, HttpServletResponse response, UserTypeForm form) throws Exception {
		String forwardString = "user/myrecommend/userTypeManage";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			List<CUserType> list = this.configureService.getUserTypeList(form.getCondition(), "order by a.code asc");
			model.put("list", list);
			model.put("form", form);
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		return new ModelAndView(forwardString, model);
	}

	@RequestMapping(value = "/form/viewUserType")
	public ModelAndView viewUserType(HttpServletRequest request, HttpServletResponse response, UserTypePropForm form) throws Exception {
		String forwardString = "user/viewUserType";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			Map<String, Object> condition = new HashMap<String, Object>();
			if (form.getCode() != null && !"".equals(form.getCode())) {
				condition.put("code", form.getCode());
			}
			if (form.getKey() != null && !"".equals(form.getKey())) {
				condition.put("key", form.getKey());
			}
			if (form.getStatus() != null) {
				condition.put("status", form.getStatus());
			}

			// 类型ID
			if (form.getPid() != null) {
				condition.put("id", form.getPid());
			} else {
				condition.put("id", request.getParameter("pid"));
			}
			List<CUserTypeProp> list = this.configureService.getUserTypeProp(condition, "order by a.order asc");
			form.setList(list);
			form.setPid(request.getParameter("pid"));
			model.put("list", list);
			model.put("form", form);
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		return new ModelAndView(forwardString, model);
	}

	@RequestMapping(value = "/form/editUserType")
	public ModelAndView editUserType(HttpServletRequest request, HttpServletResponse response, HttpSession session, UserTypeForm form) throws Exception {
		String forwardString = "user/editUserType";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 修改
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("aid", request.getParameter("pid"));
			form.setObj(this.configureService.getUserType(condition));
			// form.setId(request.getParameter("pid").toString());
			model.put("form", form);
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		return new ModelAndView(forwardString, model);
	}

	@RequestMapping(value = "/form/editUserTypeSubmit")
	public ModelAndView editUserTypeSubmit(HttpServletRequest request, HttpServletResponse response, UserTypeForm form) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		// String forwardString="user/viewUserType";
		try {
			// 更新
			if (form.getPid() != null && !"".equals(form.getPid())) {
				this.configureService.updateUserType(form.getObj(), form.getPid(), null);
				// form.setId(form.getPid());
				model.put("form", form);
				form.setMsg(Lang.getLanguage("usertype.info.update.success", request.getSession().getAttribute("lang").toString()));//
			}
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		// return new ModelAndView(forwardString, model);
		return this.userTypeManage(request, response, form);
	}

	@RequestMapping(value = "/form/editUserTypeProp")
	public ModelAndView editUserTypeProp(HttpServletRequest request, HttpServletResponse response, UserTypePropForm form) throws Exception {
		String forwardString = "user/editUserTypeProp";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 修改
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("aid", request.getParameter("id"));
			form.setObj(this.configureService.getUserTypeProp(condition, null).get(0));
			form.setId(request.getParameter("id").toString());
			model.put("form", form);
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		return new ModelAndView(forwardString, model);
	}

	@RequestMapping(value = "/form/addUserTypeProp")
	public ModelAndView addUserTypeProp(HttpServletRequest request, HttpServletResponse response, HttpSession session, UserTypePropForm form) throws Exception {
		String forwardString = "user/editUserTypeProp";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			form.setObj(null);
			form.setId(null);
			model.put("form", form);
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		return new ModelAndView(forwardString, model);
	}

	@RequestMapping(value = "/form/deleteUserTypeProp")
	public ModelAndView deleteUserTypeProp(HttpServletRequest request, HttpServletResponse response, UserTypePropForm form) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("tid", form.getId());
			if ((this.configureService.getCUserProp(condition)).size() != 0) {
				form.setMsg(Lang.getLanguage("usertype.info.delete.error", request.getSession().getAttribute("lang").toString()));//
			} else {
				this.configureService.deleteUserTypeProp(form.getId());
				form.setMsg(Lang.getLanguage("usertype.info.delete.success", request.getSession().getAttribute("lang").toString()));//
			}
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		return this.viewUserType(request, response, form);
	}

	@RequestMapping(value = "/form/submitEditUserTypeProp")
	public ModelAndView submitEditUserTypeProp(HttpServletRequest request, HttpServletResponse response, UserTypePropForm form) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		// String forwardString="user/viewUserType";
		try {
			// 更新
			if (request.getParameter("id") != null && !"".equals(request.getParameter("id"))) {
				this.configureService.updateUserTypeProp(form.getObj(), form.getId(), null);
				form.setId(form.getPid());
				form.setMsg(Lang.getLanguage("usertypeprop.info.update.success", request.getSession().getAttribute("lang").toString()));//
			} else {
				// 添加
				CUserTypeProp obj = form.getObj();
				Map<String, Object> condition = new HashMap<String, Object>();
				condition.put("aid", form.getPid());
				obj.setUserType(this.configureService.getUserTypeList(condition, null).get(0));
				this.configureService.addUserTypeProp(obj);
				form.setId(form.getPid());
				form.setMsg(Lang.getLanguage("usertype.info.add.success", request.getSession().getAttribute("lang").toString()));//

			}
			model.put("form", form);
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		// return new ModelAndView(forwardString, model);
		return this.viewUserType(request, response, form);
	}

	@RequestMapping(value = "/form/saveAll")
	public ModelAndView saveAll(HttpServletRequest request, HttpServletResponse response, UserTypePropForm form) throws Exception {
		HashMap<String, Object> model = new HashMap<String, Object>();
		try {
			this.configureService.batchSaveRelation(form.getOrders(), form.getIds());
			form.setMsg(Lang.getLanguage("usertype.info.saveAll.success", request.getSession().getAttribute("lang").toString()));//
		} catch (Exception e) {
			form.setMsg((e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		model.put("form", form);
		return this.viewUserType(request, response, form);
	}

	@RequestMapping(value = "/form/trialManage")
	public ModelAndView trialManage(HttpServletRequest request, HttpServletResponse response, LLicenseForm form) throws Exception {
		String forwardString = "user/trialManage";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			form.setUrl(request.getRequestURL().toString());
			Map<String, Object> condition = form.getCondition();
			condition.put("isTrial", 1);// 只查询标识未试用的数据
			condition.put("pubCode", form.getCode());
			condition.put("userName", form.getUserName());
			condition.put("trialPeriod", form.getTrialPeriod());
			condition.put("status", form.getStatus());
			form.setCount(this.customService.getLicenseCount(condition));
			List<LLicense> list = this.customService.getLicensePagingList(condition, " order by a.createdon desc ", form.getPageCount(), form.getCurpage());
			model.put("list", list);
			model.put("form", form);
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		return new ModelAndView(forwardString, model);
	}

	@RequestMapping(value = "/form/trialEdit")
	public ModelAndView trialEdit(HttpServletRequest request, HttpServletResponse response, LLicenseForm form) throws Exception {
		String forwardString = "user/trialEdit";
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			if (request.getParameter("eid") != null && !"".equals(request.getParameter("eid"))) {
				Map<String, Object> condition = new HashMap<String, Object>();
				condition.put("lId", request.getParameter("eid"));
				condition.put("isTrial", 1);
				LLicense obj = this.customService.getLicenseList(condition, "").get(0);
				form.setObj(obj);
				form.setId(request.getParameter("eid").toString());
			}
			model.put("form", form);
		} catch (Exception e) {
			form.setMsg((e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		return new ModelAndView(forwardString, model);
	}

	@RequestMapping(value = "/form/queryUsers")
	public void queryUsers(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			String nameString = request.getParameter("pUserName");
			String id = request.getParameter("id");
			Integer number = (id == null || "".equals(id)) ? 10 : 1;
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("pUserName", nameString);
			condition.put("status", 1);
			List<CUser> list = this.cUserService.getUserPagingList(condition, " order by a.name ", number, 0);
			JSONArray json = JSONArray.fromObject(list);
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
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
	}

	@RequestMapping(value = "/form/editTrialSubmit")
	public ModelAndView editTrialSubmit(HttpServletRequest request, HttpServletResponse response, LLicenseForm form) throws Exception {
		HashMap<String, Object> model = new HashMap<String, Object>();
		String forwardString = "user/trialEdit";
		try {
			if (form.getSelauser() != null && form.getSelauser().length > 0 && form.getSelapub() != null && form.getSelapub().length > 0 && form.getObj().getTrialPeriod() != null && form.getObj().getTrialPeriod() > 0) {
				CUser mUser = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
				if (mUser != null && mUser.getLevel() == 4) {// 只能由中图管理员开通试用授权
					Integer period = form.getObj().getTrialPeriod();
					Integer accType = form.getObj().getAccessType();
					String accUser = form.getObj().getLicenseUId();
					String accUPwd = form.getObj().getLicensePwd();
					if (form.getId() != null && !"".equals(form.getId()) && !"0".equals(form.getId())) {
						String id = form.getId();
						String pubId = form.getSelapub()[0].toString();
						String userId = form.getSelauser()[0].toString();
						this.customService.UpdateTrials(id, pubId, userId, period, accType, accUser, accUPwd);
						form.setIsSuccess("true");
						form.setMsg(Lang.getLanguage("Trial.Info.Update.Success", request.getSession().getAttribute("lang").toString()));// 试用信息编辑成功
					} else {
						this.customService.AddTrials(form.getSelapub(), form.getSelauser(), period, mUser.getName(), accType, accUser, accUPwd);
						form.setIsSuccess("true");
						form.setMsg(Lang.getLanguage("Trial.Info.Add.Success", request.getSession().getAttribute("lang").toString()));// 试用信息添加成功
					}
				} else {
					form.setIsSuccess("false");
					form.setMsg(Lang.getLanguage("Controller.User.Trial.prompt.Must.SuperAdmin", request.getSession().getAttribute("lang").toString()));// 仅能由中图管理员操作
				}
			} else {
				form.setIsSuccess("false");
				form.setMsg(Lang.getLanguage("Controller.User.Trial.prompt.error.Missing", request.getSession().getAttribute("lang").toString()));// 表单填写不完整
			}
		} catch (Exception e) {
			form.setIsSuccess("false");
			form.setMsg((e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		model.put("form", form);
		if ("true".equals(form.getIsSuccess())) {
			forwardString = "msg";
		}
		return new ModelAndView(forwardString, model);
	}

	@RequestMapping(value = "/form/closeTrials")
	public void closeTrials(HttpServletRequest request, HttpServletResponse response, LLicenseForm form) throws Exception {
		String result = "success:" + Lang.getLanguage("Controller.User.Trial.close.success", request.getSession().getAttribute("lang").toString());// 关闭试用授权成功！";
		try {
			if (form.getSelaLicense() != null && form.getSelaLicense().length > 0) {
				this.customService.CloseTrials(form.getSelaLicense());
			}
		} catch (Exception e) {
			result = "error:" + ((e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		try {
			response.setContentType("text/html;charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print(result);
			out.flush();
			out.close();
		} catch (Exception e) {
			form.setMsg((e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
	}

	/**
	 * 数据接口
	 * 
	 * @param request
	 * @param model
	 */
	@RequestMapping(value = "/syncUserPriceType")
	public void syncUserPriceType(HttpServletRequest request, Model model) throws Exception {
		ResultObject<UPRelation> result = null;
		try {
			String objJson = request.getParameter("obj").toString();
			String operType = request.getParameter("operType").toString(); // 1-insert
																			// 2-update

			Converter<UPRelation> converter = new Converter<UPRelation>();
			UPRelation obj = (UPRelation) converter.json2Object(objJson, UPRelation.class.getName());

			this.customService.saveOrUpdateUPRelation(operType, obj);

			ObjectUtil<UPRelation> util = new ObjectUtil<UPRelation>();
			obj = util.setNull(obj, new String[] { Set.class.getName(), List.class.getName() });
			if (obj.getUserType() != null) {
				ObjectUtil<CUserType> util1 = new ObjectUtil<CUserType>();
				util1.setNull(obj.getUserType(), new String[] { Set.class.getName(), List.class.getName() });
			}
			if (obj.getPriceType() != null) {
				ObjectUtil<PPriceType> util2 = new ObjectUtil<PPriceType>();
				util2.setNull(obj.getPriceType(), new String[] { Set.class.getName(), List.class.getName() });
			}
			result = new ResultObject<UPRelation>(1, obj, Lang.getLanguage("Controller.PPrice.insert.manage.success", request.getSession().getAttribute("lang").toString()));// "价格信息维护成功！");//"出版商信息维护成功！");
		} catch (Exception e) {
			// e.printStackTrace();
			result = new ResultObject<UPRelation>(2, Lang.getLanguage("Controller.PPrice.insert.manage.error", request.getSession().getAttribute("lang").toString()));// "价格信息维护失败！");//"出版商信息维护失败！");
		}
		model.addAttribute("target", result);
	}

	@RequestMapping(value = "/queryBalance", method = RequestMethod.POST)
	public void queryBalance(HttpServletRequest request, Model model) {
		ResultObject<Double> result = null;
		try {
			String userId = request.getParameter("userId").toString();
			Double balance = this.oOrderService.getUserBalance(userId);
			result = new ResultObject<Double>(1, balance, Lang.getLanguage("Controller.User.Query.Balance.Success", request.getSession().getAttribute("lang").toString()));// 查询余额成功
		} catch (Exception e) {
			result = new ResultObject<Double>(2, Lang.getLanguage("Controller.User.Query.Balance.error", request.getSession().getAttribute("lang").toString()));// "查询余额失败
		}
		model.addAttribute("target", result);
	}

	/**
	 * 数据接口
	 * 
	 * @param request
	 * @param model
	 */
	@RequestMapping(value = "/syncUserTypeList")
	public void syncUserTypeList(HttpServletRequest request, Model model) throws Exception {
		ResultObject<CUserType> result = null;
		try {
			List<CUserType> list = this.configureService.getUserTypeList(null, null);
			if (list != null && !list.isEmpty()) {
				ObjectUtil<CUserType> util = new ObjectUtil<CUserType>();
				for (int i = 0; i < list.size(); i++) {
					util.setNull(list.get(i), new String[] { Set.class.getName() });
				}
			}
			result = new ResultObject<CUserType>(1, list, Lang.getLanguage("Controller.User.getUserTypeList.query.success", request.getSession().getAttribute("lang").toString()));// "获取人员列表成功！");
		} catch (Exception e) {
			result = new ResultObject<CUserType>(2, (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		model.addAttribute("target", result);
	}

	@RequestMapping(value = "/resetAmount", method = RequestMethod.POST)
	public void resetAmount(HttpServletRequest request, Model model) {
		ResultObject<Double> result = null;
		try {
			String objJson = request.getParameter("obj").toString();
			Converter<OOrderDetail> converter = new Converter<OOrderDetail>();
			OOrderDetail obj = (OOrderDetail) converter.json2Object(objJson, OOrderDetail.class.getName());

			String userId = obj.getId();// 用户ID
			Double oldLockAmount = obj.getSalePrice();// 上一次锁定的余额
			Double newLockAmount = obj.getSalePriceExtTax();// 新的锁定余额
			String adminName = obj.getName();// 管理员名称
			Double balance = this.oOrderService.getUserBalance(userId);// 用户当前余额
			CUser user = this.customService.getUser(userId);
			if (balance + oldLockAmount < newLockAmount) {
				// 用户余额不足
				result = new ResultObject<Double>(2, Lang.getLanguage("Controller.User.Balance.Not.Enough", request.getSession().getAttribute("lang").toString()));
			} else {
				Date date = new Date();
				OTransation obj0 = new OTransation();
				obj0.setAmount(oldLockAmount);
				obj0.setCreatedby(adminName);
				obj0.setCreatedon(date);
				obj0.setType(1);
				obj0.setUpdatedby(adminName);
				obj0.setUpdatedon(date);
				obj0.setUser(user);
				this.oOrderService.insertTransation(obj0);
				OTransation obj1 = new OTransation();
				obj1.setAmount(newLockAmount);
				obj1.setCreatedby(adminName);
				obj1.setCreatedon(date);
				obj1.setType(1);
				obj1.setUpdatedby(adminName);
				obj1.setUpdatedon(date);
				obj1.setUser(user);
				this.oOrderService.insertTransation(obj1);
				result = new ResultObject<Double>(1, balance + oldLockAmount - newLockAmount, Lang.getLanguage("Controller.User.Transation.Reset.Success", request.getSession().getAttribute("lang").toString()));// 更新冻结金额成功
			}

		} catch (Exception e) {
			result = new ResultObject<Double>(2, Lang.getLanguage("Controller.User.Transation.Reset.Failed", request.getSession().getAttribute("lang").toString()));// 更新冻结金额失败
		}
		model.addAttribute("target", result);
	}

	/**
	 * 获取用户属性列表 未更新到 dcc
	 * 
	 * @param userId
	 * @param request
	 * @param model
	 * @throws Exception
	 *             by Ma Guoqing
	 */
	@RequestMapping(value = "/userProps/{userId}")
	public void getUserProps(@PathVariable String userId, HttpServletRequest request, Model model) throws Exception {
		ResultObject<CUserProp> result = null;
		try {
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("userId", userId);
			condition.put("sendStatus", 1);
			List<CUserProp> list = this.customService.getUserPropList(condition, null);
			if (list != null && !list.isEmpty()) {
				ObjectUtil<CUserProp> util = new ObjectUtil<CUserProp>();
				for (int i = 0; i < list.size(); i++) {
					util.setNull(list.get(i), new String[] { Set.class.getName() });
					if ("institutionId".equals(list.get(i).getCode())) {
						BInstitution institution = this.configureService.getInstitution(list.get(i).getVal());
						list.get(i).setVal(institution == null ? "" : institution.getName());
					}
				}
			}
			result = new ResultObject<CUserProp>(1, list, Lang.getLanguage("Controller.User.getUserProps.query.success", request.getSession().getAttribute("lang").toString()));// "获取人员属性列表成功！");
		} catch (Exception e) {
			result = new ResultObject<CUserProp>(2, (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		model.addAttribute("target", result);
	}

	/**
	 * 获取用户登录信息 未更新到 dcc
	 * 
	 * @param userId
	 * @param request
	 * @param model
	 * @throws Exception
	 *             by Ma Guoqing
	 */
	@RequestMapping(value = "/userAccount/{userId}")
	public void getUserAccount(@PathVariable String userId, HttpServletRequest request, Model model) throws Exception {
		ResultObject<CAccount> result = null;
		try {
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("userId", userId);
			condition.put("sendStatus", 2);// 用户已经同步了之后才同步登陆信息
			List<CAccount> list = this.cAccountService.getList(condition, "");
			if (list != null && !list.isEmpty()) {
				ObjectUtil<CAccount> util = new ObjectUtil<CAccount>();
				for (int i = 0; i < list.size(); i++) {
					util.setNull(list.get(i), new String[] { Set.class.getName() });
				}
			}
			result = new ResultObject<CAccount>(1, list, Lang.getLanguage("Controller.User.getUserProps.query.success", request.getSession().getAttribute("lang").toString()));// "获取人员属性列表成功！");
		} catch (Exception e) {
			result = new ResultObject<CAccount>(2, (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		model.addAttribute("target", result);
	}

	/**
	 * 获取用户类型信息 未更新到 dcc
	 * 
	 * @param request
	 * @param model
	 * @throws Exception
	 *             by Ma Guoqing
	 */
	@RequestMapping(value = "/userType")
	public void getUserType(HttpServletRequest request, Model model) throws Exception {
		ResultObject<CUserType> result = null;
		try {
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("sendStatus", 1);
			List<CUserType> list = this.cUserTypeService.getList(condition, "");

			if (list != null && !list.isEmpty()) {
				ObjectUtil<CUserType> util = new ObjectUtil<CUserType>();
				for (int i = 0; i < list.size(); i++) {
					util.setNull(list.get(i), new String[] { Set.class.getName() });
				}
			}
			result = new ResultObject<CUserType>(1, list, Lang.getLanguage("Controller.User.getUserList.query.success", request.getSession().getAttribute("lang").toString()));// "获取人员列表成功！");
		} catch (Exception e) {
			result = new ResultObject<CUserType>(2, (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		model.addAttribute("target", result);

	}

	/**
	 * 获取用户类型属性 未更新到 dcc
	 * 
	 * @param userTypeId
	 * @param request
	 * @param model
	 * @throws Exception
	 *             by Ma Guoqing
	 */
	@RequestMapping(value = "/userTypeProp")
	public void getCUserTypeProp(HttpServletRequest request, Model model) throws Exception {
		ResultObject<CUserTypeProp> result = null;
		try {
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("sendStatus", 1);
			List<CUserTypeProp> list = this.cUserTypePropService.getList(condition, "");
			if (list != null && !list.isEmpty()) {
				ObjectUtil<CUserTypeProp> util = new ObjectUtil<CUserTypeProp>();
				for (int i = 0; i < list.size(); i++) {
					util.setNull(list.get(i), new String[] { Set.class.getName() });
					ObjectUtil<CUserType> typeUtil = new ObjectUtil<CUserType>();
					if (list.get(i).getUserType() != null) {
						typeUtil.setNull(list.get(i).getUserType(), new String[] { Set.class.getName() });
					}
				}
			}
			result = new ResultObject<CUserTypeProp>(1, list, Lang.getLanguage("Controller.User.getUserProps.query.success", request.getSession().getAttribute("lang").toString()));// "获取人员属性列表成功！");
		} catch (Exception e) {
			result = new ResultObject<CUserTypeProp>(2, (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
		}
		model.addAttribute("target", result);

	}

	/**
	 * 更改用户属性
	 * 
	 * @param request
	 * @param model
	 *            by Ma Guoqing
	 */
	@RequestMapping(value = "/setUserPropSendStatus", method = RequestMethod.POST)
	public void setUserPropSendStatus(HttpServletRequest request, Model model) {
		ResultObject<CUserProp> result = null;
		try {
			String objJson = request.getParameter("obj").toString();
			String operType = request.getParameter("operType").toString(); // 1-insert
																			// 2-update
			Converter<CUserProp> converter = new Converter<CUserProp>();
			CUserProp obj = (CUserProp) converter.json2Object(objJson, CUserProp.class.getName());
			CUserProp userProp = null;
			if (obj.getId() != null && !"".equals(obj.getId())) {
				userProp = this.cUserPropService.getCUserProp(obj.getId());
			}
			if ("2".equals(operType)) {
				if (obj.getSendStatus() != null && obj.getSendStatus() > 0) {
					userProp.setSendStatus(obj.getSendStatus());// 更新订单的发送状态
				}
				this.cUserPropService.updateCUserProp(userProp, CUserProp.class.getName(), userProp.getId(), null);
			} else if ("1".equals(operType)) {
				// 写入
				if (obj != null) {
					this.cUserPropService.insertCUserProp(obj);
				}
			} else {
				// 删除
			}
			ObjectUtil<CUserProp> util = new ObjectUtil<CUserProp>();
			obj = util.setNull(obj, new String[] { Set.class.getName() });
			result = new ResultObject<CUserProp>(1, Lang.getLanguage("Controller.CUserProp.setUserSendStatus.manage.success", request.getSession().getAttribute("lang").toString()));// "订单维护成功！");//"订单信息维护成功！");
		} catch (Exception e) {
			result = new ResultObject<CUserProp>(2, Lang.getLanguage("Controller.CUserProp.setUserSendStatus.manage.error", request.getSession().getAttribute("lang").toString()));// "订单维护失败！");//"订单信息维护失败！");
		}
		model.addAttribute("target", result);
	}

	/**
	 * 更改用户登录属性
	 * 
	 * @param request
	 * @param model
	 *            by Ma Guoqing
	 */
	@RequestMapping(value = "/setCAccountStatus", method = RequestMethod.POST)
	public void setCAccountStatus(HttpServletRequest request, Model model) {
		ResultObject<CAccount> result = null;
		try {
			String objJson = request.getParameter("obj").toString();
			String operType = request.getParameter("operType").toString(); // 1-insert
																			// 2-update
			Converter<CAccount> converter = new Converter<CAccount>();
			CAccount obj = (CAccount) converter.json2Object(objJson, CAccount.class.getName());
			if (obj.getId() != null && !"".equals(obj.getId())) {
				CAccount account = this.customService.getAccountById(obj.getId());
			}
			if ("2".equals(operType)) {

			} else if ("1".equals(operType)) {
				// 写入
				if (obj != null) {
					this.customService.insertAccount(obj);
				}
			} else {
				// 删除
			}
			ObjectUtil<CAccount> util = new ObjectUtil<CAccount>();
			obj = util.setNull(obj, new String[] { Set.class.getName() });
			result = new ResultObject<CAccount>(1, Lang.getLanguage("Controller.CUserProp.setUserSendStatus.manage.success", request.getSession().getAttribute("lang").toString()));// "订单维护成功！");//"订单信息维护成功！");
		} catch (Exception e) {
			result = new ResultObject<CAccount>(2, Lang.getLanguage("Controller.CUserProp.setUserSendStatus.manage.error", request.getSession().getAttribute("lang").toString()));// "订单维护失败！");//"订单信息维护失败！");
		}
		model.addAttribute("target", result);
	}

	/**
	 * 更新用户状态属性
	 * 
	 * @param request
	 * @param model
	 *            by Ma Guoqing
	 */
	@RequestMapping(value = "/setCUserTypePropSendStatus")
	public void setCUserTypePropSendStatus(HttpServletRequest request, Model model) {
		ResultObject<CUserTypeProp> result = null;
		try {
			String objJson = request.getParameter("obj").toString();
			String operType = request.getParameter("operType").toString(); // 1-insert
																			// 2-update
			Converter<CUserTypeProp> converter = new Converter<CUserTypeProp>();
			CUserTypeProp obj = (CUserTypeProp) converter.json2Object(objJson, CUserTypeProp.class.getName());
			CUserTypeProp userTypeProp = this.cUserTypePropService.getCUserTypeProp(obj.getId());
			if ("2".equals(operType)) {
				if (obj.getSendStatus() != null && obj.getSendStatus() > 0) {
					userTypeProp.setSendStatus(obj.getSendStatus());// 更新订单的发送状态
				}
				this.cUserTypePropService.updateCUserTypeProp(userTypeProp, CUserTypeProp.class.getName(), userTypeProp.getId(), null);
			} else if ("1".equals(operType)) {
				// 写入
			} else {
				// 删除
			}
			ObjectUtil<CUserTypeProp> util = new ObjectUtil<CUserTypeProp>();
			obj = util.setNull(obj, new String[] { Set.class.getName() });
			result = new ResultObject<CUserTypeProp>(1, Lang.getLanguage("Controller.CUserTypeProp.setUserSendStatus.manage.success", request.getSession().getAttribute("lang").toString()));// "订单维护成功！");//"订单信息维护成功！");
		} catch (Exception e) {
			result = new ResultObject<CUserTypeProp>(2, Lang.getLanguage("Controller.CUserTypeProp.setUserSendStatus.manage.error", request.getSession().getAttribute("lang").toString()));// "订单维护失败！");//"订单信息维护失败！");
		}
		model.addAttribute("target", result);
	}

	/**
	 * 数据接口
	 * 
	 * @param request
	 * @param model
	 */
	@RequestMapping(value = "/syncUserPayment")
	public void syncUserPayment(HttpServletRequest request, Model model) throws Exception {
		ResultObject<UPayment> result = null;
		try {
			String objJson = request.getParameter("obj").toString();
			String operType = request.getParameter("operType").toString(); // 1-insert
																			// 2-update

			Converter<UPayment> converter = new Converter<UPayment>();
			UPayment obj = (UPayment) converter.json2Object(objJson, UPayment.class.getName());

			this.customService.saveOrUpdateUPayment(operType, obj);

			ObjectUtil<UPayment> util = new ObjectUtil<UPayment>();
			obj = util.setNull(obj, new String[] { Set.class.getName(), List.class.getName() });
			if (obj.getUserType() != null) {
				ObjectUtil<CUserType> util1 = new ObjectUtil<CUserType>();
				util1.setNull(obj.getUserType(), new String[] { Set.class.getName(), List.class.getName() });
			}
			result = new ResultObject<UPayment>(1, obj, Lang.getLanguage("Controller.Payment.Sync.Success", request.getSession().getAttribute("lang").toString()));// 用户类型与支付方式关系维护成功);
		} catch (Exception e) {
			// e.printStackTrace();
			result = new ResultObject<UPayment>(2, Lang.getLanguage("Controller.Payment.Sync.Failed", request.getSession().getAttribute("lang").toString()));// 用户类型与支付方式关系维护失败
		}
		model.addAttribute("target", result);
	}

	/**
	 * 查询用户功能所具有的的功能路径
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/accessManage")
	public ModelAndView accessManage(HttpServletRequest request, HttpServletResponse response, UAccessForm form) throws Exception {
		String forwardString = "user/accessManage";
		Map<String, Object> model = new HashMap<String, Object>();
		try {

			if (form.getUserRols() != null && !"".equals(form.getUserRols())) {

				Map<String, Object> condition = new HashMap<String, Object>();
				condition.put("userTypeId", form.getUserRols());
				List<BUrl> list = this.cUserTypeService.getUrlPagingList(condition, " order by a.url ", form.getPageCount(), form.getCurpage());
				model.put("list", list);
				form.setUrl(request.getRequestURL().toString());
				form.setCount(this.cUserTypeService.getUrlCount(condition));
			}
			model.put("form", form);
		} catch (Exception e) {
			form.setMsg("错误:" + e.getMessage());
			forwardString = "mobile/error";
		}
		return new ModelAndView(forwardString, model);
	}

	@RequestMapping(value = "/form/saveRelation")
	public ModelAndView saveRelation(HttpServletRequest request, HttpServletResponse response, UAccessForm form) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			if (form.getUserRols() != null && !"".equals(form.getUserRols())) {
				String[] urs = form.getUrlIds();
				for (String url : urs) {
					String[] p = url.split(":");
					Map<String, Object> condition = new HashMap<String, Object>();
					condition.put("userTypeId", form.getUserRols());
					condition.put("urlId", p[1]);
					BUuRelation uur = this.cUserTypeService.getuuRelation(condition);
					Integer access = "2".equals(p[0]) ? 2 : 1;
					if (uur != null) {
						if (uur.getAccess() != access) {
							uur.setAccess(access);
							this.cUserTypeService.updateBUuRelation(uur);
						}
					} else {
						if (access == 1) {
							uur = new BUuRelation();
							BUrl bu = new BUrl();
							bu.setId(p[1]);
							uur.setUrl(bu);

							uur.setUrl(new BUrl());
							uur.getUrl().setId(p[1]);
							uur.setUserType(new CUserType());
							uur.getUserType().setId(form.getUserRols());
							uur.setAccess(access);
							this.cUserTypeService.insertBUuRelation(uur);
						}
					}
				}
			}
			model.put("form", form);
		} catch (Exception e) {
			form.setMsg("错误:" + e.getMessage());
		}
		return this.accessManage(request, response, form);
	}

	@RequestMapping(value = "/form/journalCounter")
	public ModelAndView journalCounter(HttpServletRequest request, HttpServletResponse response, LAccessForm form) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		String forwardString = "user/journalCounter";
		try {
			form.setUrl(request.getRequestURL().toString());
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			if (user != null && (user.getLevel() == 2 || user.getLevel() == 3)) {// 限制为机构管理员或中图管理员可下载

				for (int i = form.getStartYear(); i < form.getEndYear() + 1; i++) {
					form.getYearList().add(String.valueOf(i));
				}
				for (int i = 1; i < 13; i++) {
					String month = String.valueOf(i);
					if (month.length() == 1) {
						month = "0" + month;
					}
					form.getMonthList().add(month);
				}
				if (form.getType() == null || form.getType() == 0) {
					form.setType(1);
				}
				/*
				 * if(form.getYear()==null || form.getYear()==""){ Integer
				 * currYear=Calendar.getInstance().get(Calendar.YEAR);
				 * form.setYear(currYear.toString()); }
				 */
				List<LAccess> list = null;
				Integer counterType = form.getType();

				Map<String, Object> condition = form.getCondition();
				condition.put("year", form.getYear());
				condition.put("startMonth", form.getStartMonth());
				condition.put("endMonth", form.getEndMonth());
				condition.put("type", 2);// 访问全文
				condition.put("access", 1);
				condition.put("pubtype", 4);

				if (user.getInstitution() != null && user.getLevel() != 3) {
					condition.put("institutionId2", user.getInstitution().getId());
				}
				int pageCount = 10;
				int curpage = 0;
				pageCount = request.getParameter("pageCount") == null ? 10 : Integer.valueOf(request.getParameter("pageCount").toString());
				curpage = request.getParameter("curpage") == null ? 0 : Integer.valueOf(request.getParameter("curpage").toString());

				switch (counterType) {
				case 1:// Journal Report 1: Number of Successful Full-Text
						// Article Requests by Month and Journal
					form.setCount(this.logAOPService.getCount2(condition, " group by a.publications.publications.id "));
					list = this.logAOPService.getJournalReport1(condition, " group by a.publications.publications.id ", pageCount, curpage);
					break;
				case 2:// Journal Report 1 GOA: Number of Successful Gold Open
						// Access Full-text Article Requests by Month and
						// Journal
					condition.put("oaStatus", 2);// 开源
					form.setCount(this.logAOPService.getCount2(condition, " group by a.publications.publications.id "));
					list = this.logAOPService.getJournalReport1(condition, " group by a.publications.publications.id ", pageCount, curpage);
					break;
				case 4:// Access Denied to Full-Text Articles by Month, Journal
						// and Category
					condition.put("access", 2);// 访问失败
					form.setCount(this.logAOPService.getCount2(condition, " group by a.publications.publications.id "));
					list = this.logAOPService.getJournalReport1(condition, " group by a.publications.publications.id ", pageCount, curpage);
					break;
				case 6:// Journal Report 4
					condition.put("type", 3);// 搜索
					condition.put("searchTypeNotNull", 1);
					form.setCount(this.logAOPService.getCount2(condition, " group by a.publications.publications.id,a.searchType "));
					list = this.logAOPService.getJournalReport2(condition, " group by a.publications.publications.id,a.searchType ", pageCount, curpage);
					break;
				case 7:// Journal Report 5: Number of Successful Full-Text
						// Article Requests by Year-of-Publication (YOP) and
						// Journal
					condition.remove("startMonth");
					condition.remove("endMonth");
					condition.remove("year");
					Integer endYear = Integer.valueOf(form.getYear());
					Integer startYear = endYear - 11;
					condition.put("startYear", startYear);
					condition.put("endYear", endYear);
					form.setCount(this.logAOPService.getCount2(condition, " group by a.publications.publications.id "));
					list = this.logAOPService.getJournalReportYOP(condition, " group by a.publications.publications.id ", pageCount, curpage);
					model.put("startYear", startYear);
					model.put("endYear", endYear);
					break;
				default:
					break;
				}
				model.put("counterType", counterType);
				model.put("year", form.getYear());
				model.put("startMonth", form.getStartMonth());
				model.put("endMonth", form.getEndMonth());
				model.put("list", list);

			}
			model.put("form", form);
		} catch (Exception e) {
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			forwardString = "mobile/error";
		}
		return new ModelAndView(forwardString, model);
	}

	/**
	 * 下载访问记录统计
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/downloadReport")
	public ModelAndView downloadReport(HttpServletRequest request, HttpServletResponse response, LAccessForm form) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		String forwardString = "user/downloadReport";
		// form.setUrl(request.getRequestURL().toString());
		List<LAccess> list = null;
		try {
			CUser user = request.getSession().getAttribute("mainUser") == null ? null : (CUser) request.getSession().getAttribute("mainUser");
			if (user != null && (user.getLevel() == 2 || user.getLevel() == 3)) {// 限制为机构管理员或中图管理员可下载
				Map<String, Object> condition = form.getCondition();
				Integer counterType = Integer.valueOf(request.getParameter("type"));
				form.setType(counterType);
				condition.put("year", form.getYear());
				condition.put("startMonth", form.getStartMonth());
				condition.put("endMonth", form.getEndMonth());
				condition.put("type", 2);// 访问全文
				condition.put("access", 1);
				condition.put("pubtype", 4);
				if (user.getInstitution() != null && user.getLevel() != 3) {
					condition.put("institutionId2", user.getInstitution().getId());
				}
				switch (counterType) {
				case 1:// Journal Report 1: Number of Successful Full-Text
						// Article Requests by Month and Journal
					list = this.logAOPService.getJournalReport1(condition, " group by a.publications.publications.id ", 0, 0);
					genReportFile1(request, response, form, list);
					break;
				case 2:// Journal Report 1 GOA: Number of Successful Gold Open
						// Access Full-text Article Requests by Month and
						// Journal
					condition.put("oaStatus", 2);// 开源
					list = this.logAOPService.getJournalReport1(condition, " group by a.publications.publications.id ", 0, 0);
					genReportFile1(request, response, form, list);
					break;
				case 4:// Access Denied to Full-Text Articles by Month, Journal
						// and Category
					condition.put("access", 2);// 访问失败
					list = this.logAOPService.getJournalReport1(condition, " group by a.publications.publications.id ", 0, 0);
					genReportFile1(request, response, form, list);
					break;
				case 6:
					condition.put("access", 1);// 访问
					condition.put("type", 3);// 搜索
					list = this.logAOPService.getJournalReport2(condition, " group by a.publications.publications.id,a.searchType ", 0, 0);
					genReportFile1(request, response, form, list);
					break;
				case 7:// Journal Report 5: Number of Successful Full-Text
						// Article Requests by Year-of-Publication (YOP) and
						// Journal
					condition.remove("startMonth");
					condition.remove("endMonth");
					condition.remove("year");
					Integer endYear = Integer.valueOf(form.getYear());
					Integer startYear = endYear - 11;
					condition.put("startYear", startYear);
					condition.put("endYear", endYear);
					list = this.logAOPService.getJournalReportYOP(condition, " group by a.publications.publications.id ", 0, 0);
					genReportFile2(request, response, form, list);
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
			forwardString = "mobile/error";
			request.setAttribute("message", (e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
			return new ModelAndView(forwardString, model);
		}

		return null;
	}

	/**
	 * 生成期刊统计报告1
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @param list
	 * @throws Exception
	 */
	private void genReportFile1(HttpServletRequest request, HttpServletResponse response, LAccessForm form, List<LAccess> list) throws Exception {
		String worksheet = "";
		if (form.getType() == 1)
			worksheet = "journal_report1_" + form.getYear();
		if (form.getType() == 2)
			worksheet = "journal_report1_GOA_" + form.getYear();
		if (form.getType() == 4)
			worksheet = "journal_report2_" + form.getYear();
		if (form.getType() == 6)
			worksheet = "journal_report2_GOA_" + form.getYear();
		Integer monthStartCol = 0;
		// excel工作表的表头
		StringBuffer sb = new StringBuffer();
		sb.append("Journal;");
		sb.append("Publisher;");
		sb.append("Platform;");
		sb.append("Journal DOI;");
		sb.append("Propriet Identifier;");
		sb.append("Print ISSN;");
		sb.append("Online ISSN;");
		if (form.getType() == 4) {
			sb.append("Access Denied Category");
			monthStartCol = 8;
		}
		if (form.getType() == 6) {
			sb.append("User Activity;");
			monthStartCol = 8;
		}
		sb.append("Reporting Period Total;");
		if (form.getType() == 1 || form.getType() == 2) {
			sb.append("Reporting Period HTML;");
			sb.append("Reporting Period PDF;");
			monthStartCol = 9;
		}
		Integer start = Integer.valueOf(form.getStartMonth());
		Integer end = Integer.valueOf(form.getEndMonth());
		for (int i = start; i <= end; i++) {
			String month = "";
			if (i == 1) {
				month = "Jan";
			} else if (i == 2) {
				month = "Feb";
			} else if (i == 3) {
				month = "Mar";
			} else if (i == 4) {
				month = "Apr";
			} else if (i == 5) {
				month = "May";
			} else if (i == 6) {
				month = "Jun";
			} else if (i == 7) {
				month = "Jul";
			} else if (i == 8) {
				month = "Aug";
			} else if (i == 9) {
				month = "Sep";
			} else if (i == 10) {
				month = "Oct";
			} else if (i == 11) {
				month = "Nov";
			} else if (i == 12) {
				month = "Dec";
			}
			sb.append(month + "-" + form.getYear() + ";");
		}
		String title[] = sb.toString().split(";");

		WritableWorkbook workbook;
		OutputStream os = response.getOutputStream();
		response.reset();// 清空输出流
		response.setHeader("Content-disposition", "attachment; filename=" + worksheet + ".xls");// 设定输出文件头
		response.setContentType("application/vnd.ms-excel;charset=UTF-8");// 定义输出类型

		workbook = Workbook.createWorkbook(os);

		WritableSheet sheet = workbook.createSheet(worksheet, 0);

		for (int i = 0; i < title.length; i++) {
			// Label(列号,行号 ,内容 )
			sheet.addCell(new Label(i, 0, title[i]));
		}
		int row;
		if (form.getType() == 6) {
			row = 1;
		} else {
			row = 2;
		}
		int totalCount = 0;
		Integer[] monthCount = new Integer[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		for (LAccess o : list) {

			sheet.addCell(new Label(0, row, o.getPublications().getPublications().getTitle()));
			sheet.addCell(new Label(1, row, o.getPublications().getPublications().getPublisher().getName()));
			sheet.addCell(new Label(2, row, "CNPe"));
			sheet.addCell(new Label(3, row, o.getPublications().getPublications().getDoi()));
			sheet.addCell(new Label(4, row, ""));
			sheet.addCell(new Label(5, row, ""));
			sheet.addCell(new Label(6, row, o.getPublications().getPublications().getCode()));

			int y = 1;
			int count = 0;
			for (int i = start; i <= end; i++) {
				if (i == 1) {
					sheet.addCell(new Label((y + monthStartCol), row, o.getMonth1() == null ? "0" : o.getMonth1().toString()));
					monthCount[0] += o.getMonth1() == null ? 0 : o.getMonth1();
					count += o.getMonth1() == null ? 0 : o.getMonth1();
				}
				if (i == 2) {
					sheet.addCell(new Label((y + monthStartCol), row, o.getMonth2() == null ? "0" : o.getMonth2().toString()));
					monthCount[1] += o.getMonth2() == null ? 0 : o.getMonth2();
					count += o.getMonth2() == null ? 0 : o.getMonth2();
				}
				if (i == 3) {
					sheet.addCell(new Label((y + monthStartCol), row, o.getMonth3() == null ? "0" : o.getMonth3().toString()));
					monthCount[2] += o.getMonth3() == null ? 0 : o.getMonth3();
					count += o.getMonth3() == null ? 0 : o.getMonth3();
				}
				if (i == 4) {
					sheet.addCell(new Label((y + monthStartCol), row, o.getMonth4() == null ? "0" : o.getMonth4().toString()));
					monthCount[3] += o.getMonth4() == null ? 0 : o.getMonth4();
					count += o.getMonth4() == null ? 0 : o.getMonth4();
				}
				if (i == 5) {
					sheet.addCell(new Label((y + monthStartCol), row, o.getMonth5() == null ? "0" : o.getMonth5().toString()));
					monthCount[4] += o.getMonth5() == null ? 0 : o.getMonth5();
					count += o.getMonth5() == null ? 0 : o.getMonth5();
				}
				if (i == 6) {
					sheet.addCell(new Label((y + monthStartCol), row, o.getMonth6() == null ? "0" : o.getMonth6().toString()));
					monthCount[5] += o.getMonth6() == null ? 0 : o.getMonth6();
					count += o.getMonth6() == null ? 0 : o.getMonth6();
				}
				if (i == 7) {
					sheet.addCell(new Label((y + monthStartCol), row, o.getMonth7() == null ? "0" : o.getMonth7().toString()));
					monthCount[6] += o.getMonth7() == null ? 0 : o.getMonth7();
					count += o.getMonth7() == null ? 0 : o.getMonth7();
				}
				if (i == 8) {
					sheet.addCell(new Label((y + monthStartCol), row, o.getMonth8() == null ? "0" : o.getMonth8().toString()));
					monthCount[7] += o.getMonth8() == null ? 0 : o.getMonth8();
					count += o.getMonth8() == null ? 0 : o.getMonth8();
				}
				if (i == 9) {
					sheet.addCell(new Label((y + monthStartCol), row, o.getMonth9() == null ? "0" : o.getMonth9().toString()));
					monthCount[8] += o.getMonth9() == null ? 0 : o.getMonth9();
					count += o.getMonth9() == null ? 0 : o.getMonth9();
				}
				if (i == 10) {
					sheet.addCell(new Label((y + monthStartCol), row, o.getMonth10() == null ? "0" : o.getMonth10().toString()));
					monthCount[9] += o.getMonth10() == null ? 0 : o.getMonth10();
					count += o.getMonth10() == null ? 0 : o.getMonth10();
				}
				if (i == 11) {
					sheet.addCell(new Label((y + monthStartCol), row, o.getMonth11() == null ? "0" : o.getMonth11().toString()));
					monthCount[10] += o.getMonth11() == null ? 0 : o.getMonth11();
					count += o.getMonth11() == null ? 0 : o.getMonth11();
				}
				if (i == 12) {
					sheet.addCell(new Label((y + monthStartCol), row, o.getMonth12() == null ? "0" : o.getMonth12().toString()));
					monthCount[11] += o.getMonth12() == null ? 0 : o.getMonth12();
					count += o.getMonth12() == null ? 0 : o.getMonth12();
				}
				y++;
			}
			totalCount += count;
			if (form.getType() == 4) {
				sheet.addCell(new Label(7, row, "Access denied : content item not licenced"));
				sheet.addCell(new Label(8, row, String.valueOf(count)));
			}
			if (form.getType() == 6) {
				sheet.addCell(new Label(7, row, String.valueOf(o.getSearchType())));
				sheet.addCell(new Label(8, row, String.valueOf(count)));
			}
			if (form.getType() == 1 || form.getType() == 2) {
				sheet.addCell(new Label(7, row, String.valueOf(count)));
				sheet.addCell(new Label(8, row, "0"));
				sheet.addCell(new Label(9, row, String.valueOf(count)));
			}
			row++;
		}
		sheet.addCell(new Label(0, 1, "Total for all journals"));
		sheet.addCell(new Label(2, 1, "CNPe"));
		if (form.getType() == 4) {
			sheet.addCell(new Label(8, 1, String.valueOf(totalCount)));
		}
		if (form.getType() == 1 || form.getType() == 2) {
			sheet.addCell(new Label(7, 1, String.valueOf(totalCount)));
			sheet.addCell(new Label(8, 1, "0"));
			sheet.addCell(new Label(9, 1, String.valueOf(totalCount)));
		}
		int y = 1;
		for (int i = start; i <= end; i++) {// 小计
			sheet.addCell(new Label((y + monthStartCol), 1, monthCount[i - 1] == null ? "0" : monthCount[i - 1].toString()));
			y++;
		}
		workbook.write();
		workbook.close();
		os.close();
	}

	/**
	 * 生成期刊统计报告 按年统计
	 * 
	 * @param request
	 * @param response
	 * @param form
	 * @param list
	 * @throws Exception
	 */
	private void genReportFile2(HttpServletRequest request, HttpServletResponse response, LAccessForm form, List<LAccess> list) throws Exception {
		String worksheet = "journal_report5_YOP_" + form.getYear();
		Integer monthStartCol = 7;
		// excel工作表的表头
		StringBuffer sb = new StringBuffer();
		sb.append("Journal;");
		sb.append("Publisher;");
		sb.append("Platform;");
		sb.append("Journal DOI;");
		sb.append("Propriet Identifier;");
		sb.append("Print ISSN;");
		sb.append("Online ISSN;");
		sb.append("Articles in Press;");
		Integer endYear = Integer.valueOf(form.getYear());
		Integer startYear = endYear - 11;

		for (int i = endYear; i >= startYear; i--) {
			sb.append("YOP " + i + ";");
		}
		String title[] = sb.toString().split(";");

		WritableWorkbook workbook;
		OutputStream os = response.getOutputStream();
		response.reset();// 清空输出流
		response.setHeader("Content-disposition", "attachment; filename=" + worksheet + ".xls");// 设定输出文件头
		response.setContentType("application/vnd.ms-excel;charset=UTF-8");// 定义输出类型

		workbook = Workbook.createWorkbook(os);

		WritableSheet sheet = workbook.createSheet(worksheet, 0);

		for (int i = 0; i < title.length; i++) {
			// Label(列号,行号 ,内容 )
			sheet.addCell(new Label(i, 0, title[i]));
		}
		int row = 2;
		Integer totalCount = 0;
		Integer[] monthCount = new Integer[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		for (LAccess o : list) {

			sheet.addCell(new Label(0, row, o.getPublications().getPublications().getTitle()));
			sheet.addCell(new Label(1, row, o.getPublications().getPublications().getPublisher().getName()));
			sheet.addCell(new Label(2, row, "CNPe"));
			sheet.addCell(new Label(3, row, o.getPublications().getPublications().getDoi()));
			sheet.addCell(new Label(4, row, ""));
			sheet.addCell(new Label(5, row, ""));
			sheet.addCell(new Label(6, row, o.getPublications().getPublications().getCode()));
			sheet.addCell(new Label(7, row, o.getYear()));
			Integer articleCount = o.getYear() == null || o.getYear().isEmpty() ? 0 : Integer.valueOf(o.getYear());
			totalCount += articleCount;
			int y = 1;
			for (int i = 1; i <= endYear - startYear + 1; i++) {
				if (i == 1) {
					sheet.addCell(new Label((y + monthStartCol), row, o.getMonth1() == null ? "0" : o.getMonth1().toString()));
					monthCount[0] += o.getMonth1() == null ? 0 : o.getMonth1();
				}
				if (i == 2) {
					sheet.addCell(new Label((y + monthStartCol), row, o.getMonth2() == null ? "0" : o.getMonth2().toString()));
					monthCount[1] += o.getMonth2() == null ? 0 : o.getMonth2();
				}
				if (i == 3) {
					sheet.addCell(new Label((y + monthStartCol), row, o.getMonth3() == null ? "0" : o.getMonth3().toString()));
					monthCount[2] += o.getMonth3() == null ? 0 : o.getMonth3();
				}
				if (i == 4) {
					sheet.addCell(new Label((y + monthStartCol), row, o.getMonth4() == null ? "0" : o.getMonth4().toString()));
					monthCount[3] += o.getMonth4() == null ? 0 : o.getMonth4();
				}
				if (i == 5) {
					sheet.addCell(new Label((y + monthStartCol), row, o.getMonth5() == null ? "0" : o.getMonth5().toString()));
					monthCount[4] += o.getMonth5() == null ? 0 : o.getMonth5();
				}
				if (i == 6) {
					sheet.addCell(new Label((y + monthStartCol), row, o.getMonth6() == null ? "0" : o.getMonth6().toString()));
					monthCount[5] += o.getMonth6() == null ? 0 : o.getMonth6();
				}
				if (i == 7) {
					sheet.addCell(new Label((y + monthStartCol), row, o.getMonth7() == null ? "0" : o.getMonth7().toString()));
					monthCount[6] += o.getMonth7() == null ? 0 : o.getMonth7();
				}
				if (i == 8) {
					sheet.addCell(new Label((y + monthStartCol), row, o.getMonth8() == null ? "0" : o.getMonth8().toString()));
					monthCount[7] += o.getMonth8() == null ? 0 : o.getMonth8();
				}
				if (i == 9) {
					sheet.addCell(new Label((y + monthStartCol), row, o.getMonth9() == null ? "0" : o.getMonth9().toString()));
					monthCount[8] += o.getMonth9() == null ? 0 : o.getMonth9();
				}
				if (i == 10) {
					sheet.addCell(new Label((y + monthStartCol), row, o.getMonth10() == null ? "0" : o.getMonth10().toString()));
					monthCount[9] += o.getMonth10() == null ? 0 : o.getMonth10();
				}
				if (i == 11) {
					sheet.addCell(new Label((y + monthStartCol), row, o.getMonth11() == null ? "0" : o.getMonth11().toString()));
					monthCount[10] += o.getMonth11() == null ? 0 : o.getMonth11();
				}
				if (i == 12) {
					sheet.addCell(new Label((y + monthStartCol), row, o.getMonth12() == null ? "0" : o.getMonth12().toString()));
					monthCount[11] += o.getMonth12() == null ? 0 : o.getMonth12();
				}
				y++;
			}
			row++;
		}
		sheet.addCell(new Label(0, 1, "Total for all journals"));
		sheet.addCell(new Label(2, 1, "CNPe"));
		sheet.addCell(new Label(7, 1, totalCount.toString()));

		int y = 1;
		for (int i = 0; i <= endYear - startYear; i++) {// 小计
			sheet.addCell(new Label((y + monthStartCol), 1, monthCount[i] == null ? "0" : monthCount[i].toString()));
			y++;
		}
		workbook.write();
		workbook.close();
		os.close();
	}
}
