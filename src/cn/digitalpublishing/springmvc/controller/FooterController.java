package cn.digitalpublishing.springmvc.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 底部 Controller
 */
@Controller
public class FooterController extends BaseController {

	@RequestMapping("footer")
	public ModelAndView footer() throws Exception {
		String forwardString = "mobile/frame/footer";
		Map<String, Object> model = new HashMap<String, Object>();
		return new ModelAndView(forwardString, model);
	}

}
