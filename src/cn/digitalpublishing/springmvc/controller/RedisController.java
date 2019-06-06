package cn.digitalpublishing.springmvc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.digitalpublishing.ep.po.BInstitution;
import cn.digitalpublishing.ep.po.CUser;
import cn.digitalpublishing.redis.po.Book;

/**
 * All Redis Controller
 */
@Controller
public class RedisController extends BaseController {

	/**
	 * 帮助
	 */
	@RequestMapping("help")
	public ModelAndView help() {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("dynamic", getInfoFromRedis("help_m_zh_CN"));
		model.put("footer", getInfoFromRedis("footer_m_zh_CN"));
		return new ModelAndView("mobile/ftl/Dynamic.ftl", model);
	}

	/**
	 * 注册协议
	 */
	@RequestMapping("agreement")
	public ModelAndView agreement() {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("dynamic", getInfoFromRedis("agreement_m_zh_CN"));
		model.put("footer", getInfoFromRedis("footer_m_zh_CN"));
		return new ModelAndView("mobile/ftl/Dynamic.ftl", model);
	}

	/**
	 * 分类法
	 */
	@RequestMapping("subject")
	public ModelAndView subject() throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("subjectList", bookDao.getSet("category_zh_CN"));
		model.put("footer", getInfoFromRedis("footer_m_zh_CN"));
		return new ModelAndView("mobile/ftl/Category.ftl", model);
	}

	/**
	 * 首页热读资源
	 */
	@RequestMapping("indexHotReading")
	public ModelAndView indexHotReading(HttpServletRequest request) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		List<Book> pubList = new ArrayList<Book>();
		List<String> indexHotReading = null;
		Book book = null;

		// 获取 Session 中的用户（CUser）信息和机构（BInstitution）信息
		CUser user = (CUser) request.getSession().getAttribute("mainUser");
		BInstitution institution = (BInstitution) request.getSession().getAttribute("institution");
		institution = null == institution ? null != user ? user.getInstitution() : null : institution;
		if (null != user && null != institution) {
			indexHotReading = bookDao.getSet(institution.getId());
			if (0 == indexHotReading.size()) {
				indexHotReading = bookDao.getSet("indexHotReading");
			}
		} else {
			indexHotReading = bookDao.getSet("indexHotReading");
		}
		for (String s : indexHotReading) {
			book = new Book(s.substring(0, 32), s.substring(32, s.lastIndexOf("@@@@@author@@@@@")), s.substring(s.lastIndexOf("@@@@@author@@@@@") + 16, s.lastIndexOf("@@@@@pubdate@@@@@")), s.substring(s.lastIndexOf("@@@@@pubdate@@@@@") + 17, s.lastIndexOf("@@@@@publisher@@@@@")), s.substring(s.lastIndexOf("@@@@@publisher@@@@@") + 19, s.lastIndexOf("@@@@@type@@@@@")), Integer.valueOf(s.substring(s.lastIndexOf("@@@@@type@@@@@") + 14, s.length())));
			pubList.add(book);
		}
		model.put("pubList", pubList);
		model.put("obj", "zh_CN".equals((String) request.getSession().getAttribute("lang")) ? "图书" : "book");
		return new ModelAndView("mobile/ftl/IndexHotReading.ftl", model);
	}

	/**
	 * 首页最新资源
	 */
	@RequestMapping("indexNewest")
	public ModelAndView indexNewest(HttpServletRequest request) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		List<Book> pubList = new ArrayList<Book>();
		List<String> indexNewest = null;
		Book book = null;
		indexNewest = bookDao.getSet("new5");

		for (String s : indexNewest) {
			book = new Book(s.substring(0, 32), s.substring(32, s.lastIndexOf("@@@@@author@@@@@")), s.substring(s.lastIndexOf("@@@@@author@@@@@") + 16, s.lastIndexOf("@@@@@pubdate@@@@@")), s.substring(s.lastIndexOf("@@@@@pubdate@@@@@") + 17, s.lastIndexOf("@@@@@publisher@@@@@")), s.substring(s.lastIndexOf("@@@@@publisher@@@@@") + 19, s.lastIndexOf("@@@@@type@@@@@")), Integer.valueOf(s.substring(s.lastIndexOf("@@@@@type@@@@@") + 14, s.length())));
			pubList.add(book);
		}
		model.put("pubList", pubList);
		return new ModelAndView("mobile/ftl/IndexNewest.ftl", model);
	}

	/**
	 * 根据不同的key获取不同的Value
	 */
	private String getInfoFromRedis(String obj) {
		List<String> s = bookDao.getList(obj);
		return 0 == s.size() ? "" : s.get(0);
	}

}
