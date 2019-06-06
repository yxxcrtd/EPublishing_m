package cn.digitalpublishing.springmvc.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 登录
 */
@Controller
public class LoginController extends BaseController {

	/**
	 * 显示登录页面
	 */
	@RequestMapping("login")
	public ModelAndView loginPage(HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("beforPath", request.getHeader("Referer"));
		return new ModelAndView("mobile/login", model);
	}

}
