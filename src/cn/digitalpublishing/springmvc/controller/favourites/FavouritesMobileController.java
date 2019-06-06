package cn.digitalpublishing.springmvc.controller.favourites;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sun.org.glassfish.external.statistics.annotations.Reset;

import cn.com.daxtech.framework.Internationalization.Lang;
import cn.com.daxtech.framework.exception.CcsException;
import cn.com.daxtech.framework.model.Param;
import cn.digitalpublishing.ep.po.CFavourites;
import cn.digitalpublishing.ep.po.CUser;
import cn.digitalpublishing.ep.po.PPublications;
import cn.digitalpublishing.springmvc.controller.BaseController;
import cn.digitalpublishing.springmvc.form.favourites.FavouritesForm;

@Controller
@RequestMapping("/mobile/pages/favourites")
public class FavouritesMobileController extends BaseController  {
	
	@RequestMapping(value="/form/commit")
	public @ResponseBody String commit(HttpServletRequest request,HttpServletResponse response, FavouritesForm form)throws Exception {
		Map<String, Object> condition = new HashMap<String, Object>();
		String result = "";
		try {
			CUser user = null == request.getSession().getAttribute("mainUser") ? null : (CUser) request.getSession().getAttribute("mainUser");
			if (null != user) {
				String publicationsId = request.getParameter("pubId");
				PPublications pub = new PPublications();
				pub.setId(publicationsId);

				condition.put("pid", publicationsId);
				if (null != customService.getFavourite(condition)) {
					condition.clear();
					condition.put("dels", publicationsId);
					condition.put("userId", user.getId());
					cUserService.deleteFavorites(condition);
					result = "del";
				} else {
					CFavourites favourite = new CFavourites();
					favourite.setUser(user);
					favourite.setPublications(pub);
					favourite.setCreateDate(new Date());
					customService.insertFavourites(favourite);
					result = "success";
				}
			} else {
				result = "nologin";
			}
		} catch (Exception e) {
			result = "error";
		}
		return result;
	}
	
	
	
	
	@RequestMapping(value="/form/batchCommit")
	public void batchCommit(HttpServletRequest request,HttpServletResponse response, FavouritesForm form)throws Exception {
		String result = "";
		try{
			CUser user = request.getSession().getAttribute("mainUser")==null?null:(CUser)request.getSession().getAttribute("mainUser");
			if(user!=null){
				String publicationsIds = request.getParameter("pubIds");
				String srcIds = request.getParameter("srcIds").replace("@",",");
				String[] pids = publicationsIds.split("@");
				String[] sids = srcIds.split(",");
				if(sids!=null&&sids.length>0){
					Map<String,Object> condition = new HashMap<String,Object>();
					condition.put("dels",srcIds);
					condition.put("userId", user.getId());
					this.cUserService.deleteFavorites(condition);
				}
				if(pids!=null&&pids.length>0){
					for(int i=0;i<pids.length;i++){
						PPublications pub = new PPublications();
						pub.setId(pids[i]);
						Map<String,Object> condition = new HashMap<String,Object>();
						condition.put("pid", pids[i]);
						condition.put("userId", user.getId());
						//已经收藏的出版物不再插入
						if(this.customService.getFavourite(condition) == null){
							CFavourites favourite = new CFavourites();
							favourite.setUser(user);
							favourite.setPublications(pub);
							favourite.setCreateDate(new Date());
							this.customService.insertFavourites(favourite);
						}
					}
					result = "success:"+Lang.getLanguage("Controller.Favourites.commit.success",request.getSession().getAttribute("lang").toString());//收藏成功！";
				}
			}else{
				result = "error:"+Lang.getLanguage("Controller.Favourites.commit.noUser.error",request.getSession().getAttribute("lang").toString());//收藏成功！";
			}
		}catch(Exception e){
			result = "false:"+((e instanceof CcsException)?Lang.getLanguage(((CcsException)e).getPrompt(),request.getSession().getAttribute("lang").toString()):e.getMessage());
		}
		try {
			response.setContentType("text/html;charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print(result);
			out.flush();
			out.close();
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	/**
	 * 用户收藏夹管理
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/favorites")
	public ModelAndView downloadfavorites(HttpServletRequest request, HttpServletResponse response, FavouritesForm form) throws Exception {
		String forwardString="mobile/favourite/favouritesList";
		Map<String,Object> model = new HashMap<String,Object>();
		
		CUser user = request.getSession().getAttribute("mainUser")==null?null:(CUser)request.getSession().getAttribute("mainUser");
		
		Map<String,Object> favcondition = new HashMap<String,Object>();
		
		favcondition.put("available", 3);
		favcondition.put("userId", user.getId());
		
		List<CFavourites> flist = this.cUserService.getFavoutitesPagingList(favcondition, "order by a.createDate desc",20,0);
		
		model.put("list", flist);
			
		return new ModelAndView(forwardString, model);
		
	}
	
	/**
	 * 用户收藏夹管理Json
	 * @param request
	 * @param response
	 * @param form
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/form/favoritesJson")
	public ModelAndView downloadfavoritesJson(HttpServletRequest request, HttpServletResponse response, FavouritesForm form) throws Exception {
		String forwardString="mobile/favourite/favouritesList";
		Map<String,Object> model = new HashMap<String,Object>();
		
		CUser user = request.getSession().getAttribute("mainUser")==null?null:(CUser)request.getSession().getAttribute("mainUser");
		
		Map<String,Object> favcondition = new HashMap<String,Object>();
		
		favcondition.put("available", 3);
		favcondition.put("userId", user.getId());
		
		List<CFavourites> flist = this.cUserService.getFavoutitesPagingList(favcondition, "order by a.createDate desc",20,form.getCurpage());
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", flist);
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
		model.put("list", flist);
			
		return new ModelAndView(forwardString, model);
		
	}
	
	@RequestMapping(value="/form/favorites/delete")
	public void favoritesDelete( HttpServletRequest request,HttpServletResponse response,HttpSession session, FavouritesForm form)throws Exception {
//		String forwardString="user/favorites";
//		Map<String,Object> model = new HashMap<String,Object>();
		String result= "error:"+Lang.getLanguage("Controller.User.deleteDir.del.error", request.getSession().getAttribute("lang").toString());
		try{
			CUser user = request.getSession().getAttribute("mainUser")==null?null:(CUser)request.getSession().getAttribute("mainUser");
//			CUser user = new CUser();
//			user.setId("086389937b1f1030a8ee2610e0325a2b");
			if(user!=null){
				if(form.getDels()!=null){
					Map<String,Object> condition = new HashMap<String,Object>();
					condition.put("userId", user.getId());
					condition.put("dels", form.getDels());
					this.cUserService.deleteFavorites(condition);
					result = "success:"+Lang.getLanguage("Controller.User.saveSearch.del.success", request.getSession().getAttribute("lang").toString());
				}else{
					result = "error:"+Lang.getLanguage("Pages.User.Favorites.Prompt.deleteNull", request.getSession().getAttribute("lang").toString());
				}
				
			}else{
				result = "error:"+Lang.getLanguage("Controller.view.login.no", request.getSession().getAttribute("lang").toString());
			}
			
		}catch(Exception e){
            result = "error:"+((e instanceof CcsException)?Lang.getLanguage(((CcsException)e).getPrompt(),request.getSession().getAttribute("lang").toString()):e.getMessage());
		}
//		return this.favorites(request, response, session, form);
		try {
			response.setContentType("text/html;charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print(result);
			out.flush();
			out.close();
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
}
