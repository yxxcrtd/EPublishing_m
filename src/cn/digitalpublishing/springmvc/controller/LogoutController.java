package cn.digitalpublishing.springmvc.controller;

import java.io.PrintWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.com.daxtech.framework.Internationalization.Lang;
import cn.com.daxtech.framework.exception.CcsException;
import cn.digitalpublishing.springmvc.form.CAccountForm;

/**
 * 退出
 */
@Controller
public class LogoutController extends BaseController {

	/**
	 * 退出
	 */
	@RequestMapping("logout")
	public void logout(HttpServletRequest request, HttpServletResponse response, HttpSession session, CAccountForm form) {
		String result;
		String path = request.getHeader("Referer").toString();
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
			result = "success::" + Lang.getLanguage("Controller.User.logoutRedirect.prompt.success", request.getSession().getAttribute("lang").toString()) + "::" + path;// "登陆成功！";
		} catch (Exception e) {
			result = ((e instanceof CcsException) ? Lang.getLanguage(((CcsException) e).getPrompt(), request.getSession().getAttribute("lang").toString()) : e.getMessage());
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

}
