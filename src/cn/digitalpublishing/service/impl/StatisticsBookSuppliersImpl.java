package cn.digitalpublishing.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.com.daxtech.framework.exception.CcsException;
import cn.digitalpublishing.ep.po.LAccess;
import cn.digitalpublishing.ep.po.SSupplier;
import cn.digitalpublishing.service.StatisticsBookSuppliersService;
import cn.digitalpublishing.springmvc.form.product.BookSuppliersForm;

public class StatisticsBookSuppliersImpl extends BaseServiceImpl implements StatisticsBookSuppliersService {

	@Override
	public void getSyncLaccess() throws Exception {
		this.daoFacade.getlAccessDao().executeLaccess(1);
	}

	@Override
	public List<LAccess> getBookStatisticalList(Map<String, Object> condition,
			String sort, Integer pageCount, Integer page) throws Exception {
		List<LAccess>list=null;
		try {
			list=this.daoFacade.getlAccessDao().getCounterPagingList3(condition, sort, pageCount, page);
		} catch (Exception e) {
			throw new CcsException((e instanceof CcsException) ?((CcsException)e).getPrompt()	: "Laccess.getBookStatisticalList.get.error", e);//获取统计信息分页列表失败！
		}
		return list;
	}

	@Override
	public Integer getLaccessCount(Map<String, Object> condition,String group)
			throws Exception {
		Integer num=0;
		try {
			num=this.daoFacade.getlAccessDao().getCount(condition, group);
		} catch (Exception e) {
			throw new CcsException((e instanceof CcsException) ? ((CcsException)e).getPrompt()	: "Laccess.count.get.error", e);//获取统计信息总数失败！
		}
		return num;
	}

	@Override
	public List<LAccess> getDownloadBookFull(Map<String, Object> condition,
			String sort, int pageCount, int page) throws Exception {
		List<LAccess> list = null;
		try {
			list = this.daoFacade.getlAccessDao().getCounterPagingList3(condition, sort, pageCount, page);
		} catch (Exception e) {
			throw new CcsException((e instanceof CcsException) ? ((CcsException)e).getPrompt()	: "laccess.info.get.error", e);//获取日至信息失败！
		}
		return list;
	}

	@Override
	public List<LAccess> getDownloadRefuseBookJournal(
			Map<String, Object> condition, String sort, int pageCount, int page)
			throws Exception {
		List<LAccess> list = null;
		try {
			list = this.daoFacade.getlAccessDao().getCounterPagingList4(condition, sort, pageCount, page);
		} catch (Exception e) {
			throw new CcsException((e instanceof CcsException) ? ((CcsException)e).getPrompt()	: "laccess.info.get.error", e);//获取日至信息失败！
		}
		return list;
	}

	@Override
	public List<LAccess> getRefuseBookJournalList(
			Map<String, Object> condition, String sort, Integer pageCount,
			Integer page) throws Exception {
			List<LAccess>list=null;
			try {
				list=this.daoFacade.getlAccessDao().getCounterPagingList4(condition, sort, pageCount, page);
			} catch (Exception e) {
				throw new CcsException((e instanceof CcsException) ?((CcsException)e).getPrompt()	: "Laccess.getBookStatisticalList.get.error", e);//获取统计信息分页列表失败！
			}
			return list;
		
		
	}

	@Override
	public List<LAccess> getSearchesBookJournalList(
			Map<String, Object> condition, String sort, Integer pageCount,
			Integer page) throws Exception {
		List<LAccess>list=null;
		try {
			list=this.daoFacade.getlAccessDao().getCounterPagingList5(condition, sort, pageCount, page);
		} catch (Exception e) {
			throw new CcsException((e instanceof CcsException) ?((CcsException)e).getPrompt()	: "Laccess.getBookStatisticalList.get.error", e);//获取统计信息分页列表失败！
		}
		return list;
	}

	@Override
	public List<LAccess> getDownloadSearchesBookJournal(
			Map<String, Object> condition, String sort, int pageCount, int page)
			throws Exception {
		List<LAccess> list = null;
		try {
			list = this.daoFacade.getlAccessDao().getCounterPagingList5(condition, sort, pageCount, page);
		} catch (Exception e) {
			throw new CcsException((e instanceof CcsException) ? ((CcsException)e).getPrompt()	: "laccess.info.get.error", e);//获取日至信息失败！
		}
		return list;
	}

	@Override
	public void getSupplier(Integer type,Integer access,Integer stype,String stimes,String year,String sourceid)throws Exception {
		List<LAccess> list=null;
		boolean flag=false;
		try {	
			Map<String, Object> condition = new HashMap<String, Object>();
			BookSuppliersForm form = new BookSuppliersForm();
				if(sourceid!=null){//机构ID
					condition.put("isInstitutionId", sourceid);
				}
				condition.put("year", year);
				if(stimes!=null){
					form.setStartMonth(stimes);
				}
				condition.put("startMonth", form.getStartMonth());
				condition.put("endMonth", form.getEndMonth());
				condition.put("type", type);
		
				switch (type) {
				case 2:
					if(stype!=null){//1-没有License,2-超出并发数
							condition.put("", stype);
					}
					condition.put("access", access);
					
					break;
				case 3:
					if(stype!=null){//1-常规检索 , 2-高级检索 , 3-分类法点击---！（标准检索 联邦检索）
						condition.put("", stype);
					}
					break;
				default:
					break;
				}
			
		
			list=this.daoFacade.getlAccessDao().getCounterPagingList10(condition, "", 0, 0);
			if(list!=null&&list.size()>0){
				flag=true;
			}
			if(flag){
				for(LAccess o :list){
					for(int i=1; i<13;i++){
						
						
						String month = String.valueOf(i);
							if (month.length() == 1) {
								month = "0" + month;
							}
						int count = 0;//每月总数
						if(i==1){
							count += o.getMonth1()==null?0:o.getMonth1();
						}
						if (i == 2) {
							count += o.getMonth2()==null?0:o.getMonth2();
						}
						if (i == 3) {
							count += o.getMonth3()==null?0:o.getMonth3();
						}
						if (i == 4) {
							count += o.getMonth4()==null?0:o.getMonth4();
						}
						if (i == 5) {
							count += o.getMonth5()==null?0:o.getMonth5();
						}
						if (i == 6) {
							count += o.getMonth6()==null?0:o.getMonth6();
						}
						if (i == 7) {
							count += o.getMonth7()==null?0:o.getMonth7();
						}
						if (i == 8) {
							count += o.getMonth8()==null?0:o.getMonth8();
						}
						if (i == 9) {
							count += o.getMonth9()==null?0:o.getMonth9();
						}
						if (i == 10) {
							count += o.getMonth10()==null?0:o.getMonth10();
						}
						if (i == 11) {
							count += o.getMonth11()==null?0:o.getMonth11();
						}
						if (i == 12) {
							count += o.getMonth12()==null?0:o.getMonth12();
						}
						
						
							
							Map<String, Object> supMap= new HashMap<String, Object>();
							supMap.put("datetime", o.getYear()+"-"+month);
							supMap.put("institutionId", o.getInstitutionId());
							if (o.getPublications().getType()!= 1 &&o.getPublications().getType() != 3){
					            String issn = o.getPublications().getCode().length() > 9 ? o.getPublications().getCode().substring(0, 9) : o.getPublications().getCode();
					            supMap.put("issn", issn);
					            supMap.put("eissn", issn);
						     }else {
						        supMap.put("pubId", o.getPublications().getId());
						     }

							List<SSupplier> supList=this.daoFacade.getSuppDao().getList(supMap, "");
							if(supList!=null&&supList.size()>0){
								SSupplier obj=supList.get(0);
							    if (o.getPublications().getType()!= 1 && o.getPublications().getType()!= 3){
					              if (type == 1) {
					            	  count +=supList.get(0).getToc()!=null&&!"".equals(supList.get(0).getToc().toString())?supList.get(0).getToc():0;
					            	  obj.setToc(count);
					              } else if (type==2 && access != null && access== 1) {
					                count += supList.get(0).getFullAccess() != null && !"".equals(supList.get(0).getFullAccess().toString()) ? supList.get(0).getFullAccess() : 0;
					                obj.setFullAccess(count);
					              } else if (type== 2 && access != null && access == 2) {
					                if ((stype != null) && stype== 1) {
					                  count += supList.get(0).getRefusedLicense() != null && !"".equals(supList.get(0).getRefusedLicense().toString()) ? supList.get(0).getRefusedLicense() : 0;
					                  obj.setRefusedLicense(count);
					                } else if (stype != null&& stype == 2) {
					                  count += supList.get(0).getRefusedConcurrent() != null && !"".equals(supList.get(0).getRefusedConcurrent().toString()) ? supList.get(0).getRefusedConcurrent() : 0;
					                  obj.setRefusedConcurrent(count);
					                } else {
					                  count += supList.get(0).getFullRefused() != null && !"".equals(supList.get(0).getFullRefused().toString()) ? supList.get(0).getFullRefused() : 0;
					                  obj.setFullRefused(count);
					                }
					              } else if (type == 3) {
					                if (stype != null && stype == 1) {
					                  count += supList.get(0).getSearchStandard() != null && !"".equals(supList.get(0).getSearchStandard().toString()) ? supList.get(0).getSearchStandard() : 0;
					                  obj.setSearchStandard(count);
					                } else if (stype != null && stype == 2) {
					                  count += supList.get(0).getSearchFederal() != null && !"".equals(supList.get(0).getSearchFederal().toString()) ? supList.get(0).getSearchFederal() : 0;
					                  obj.setSearchFederal(count);
					                } else {
					                  count += supList.get(0).getSearch() != null && !"".equals(supList.get(0).getSearch().toString()) ? supList.get(0).getSearch() : 0;
					                  obj.setSearch(count);
					                }
					              } else if (type == 4) {
					                count += supList.get(0).getDownload() != null && !"".equals(supList.get(0).getDownload().toString()) ? supList.get(0).getDownload() : 0;
					                obj.setDownload(count);
					              }

					            }else if (type == 1){
					              obj.setToc(count);
					            }else if (type == 2 && access != null && access == 1){
					              obj.setFullAccess(count);
					            }else if (type == 2 && access != null &&access == 2) {
					              if(stype != null && stype == 1){
					                obj.setRefusedLicense(count);
					              }else if (stype != null && stype== 2){
					                obj.setRefusedConcurrent(count);
					              }else{
					                obj.setFullRefused(count);
					              }
					            }else if (type== 3) {
					              if (stype != null &&stype == 1){
					                obj.setSearchStandard(count);
					              }else if (stype != null && stype == 2){
					                obj.setSearchFederal(count);
					              } else{
					                obj.setSearch(count);
					              }
					            }else if (type== 4) {
					              obj.setDownload(count);
					            }
							    obj.setLang( o.getPublications().getLang()!=null? o.getPublications().getLang():"");//语种
					            this.daoFacade.getSuppDao().update(obj, SSupplier.class.getName(), obj.getId(), null);
					          }else {
					              SSupplier obj = new SSupplier();
					              obj.setInstitutionid(o.getInstitutionId());
					              obj.setTitle(o.getPublications().getTitle() != null ? o.getPublications().getTitle() : "");
					              obj.setAuthor(o.getPublications().getAuthor() != null ? o.getPublications().getAuthor() : "");

					              if (o.getPublications().getType()!= 1 && o.getPublications().getType() != 3) {
					                  obj.setPubId(o.getPublications().getId());
					                  obj.setIssn(o.getPublications().getCode() != null ? o.getPublications().getCode() : "");
					                  obj.setEissn(o.getPublications().getEissn() != null ?o.getPublications().getEissn() : "");
					                  obj.setType(o.getPublications().getType() != null ? o.getPublications().getType() : null);
					                
					              } else {
					                obj.setPubId(o.getPublications().getId() != null ? o.getPublications().getId() : "");
					                obj.setIsbn(o.getPublications().getCode() != null ? o.getPublications().getCode() : "");
					                obj.setType(o.getPublications().getType() != null ? o.getPublications().getType() : null);
					                obj.setIssn("");
					              }

					              obj.setPubName(o.getPublications().getPublisher().getName() != null ? o.getPublications().getPublisher().getName() : "");
					              String str = o.getYear() + "-" + month;
					              obj.setSdate(str);
					              obj.setYear(Integer.parseInt(o.getYear()));
					              obj.setMonth(Integer.parseInt(month));
					              obj.setPlatform(o.getPlatform() != null ? o.getPlatform() : "");
					              //初始值赋值 0 索引查询不能有null 值 会影响查询速度 by heqing.yang 2015-01-24
					              obj.setFullAccess(0);
					              obj.setFullRefused(0);
					              obj.setRefusedLicense(0);
					              obj.setRefusedConcurrent(0);
					              obj.setSearch(0);
					              obj.setSearchStandard(0);
					              obj.setSearchFederal(0);
					              obj.setToc(0);
					              obj.setDownload(0);
					              
					              if (type == 1){
					                obj.setToc(count);
					              }else if (type == 2 && access != null && access == 1){
					                obj.setFullAccess(count);
					              }else if (type == 2 && access != null&& access == 2) {
					                if (stype != null&& stype == 1){
					                  obj.setRefusedLicense(count);
					                }else if (stype != null && stype == 2){
					                  obj.setRefusedConcurrent(count);
					                }else{
					                  obj.setFullRefused(count);
					                }
					              }else if (type== 3) {
					                if (stype != null && stype == 1){
					                  obj.setSearchStandard(count);
					                }else if (stype != null && stype == 2){
					                  obj.setSearchFederal(count);
					                }else{
					                  obj.setSearch(count);
					                }
					              }else if (type == 4) {
					                obj.setDownload(count);
					              }
					              obj.setLang( o.getPublications().getLang()!=null? o.getPublications().getLang():"");//语种
					              this.daoFacade.getSuppDao().insert(obj);
					            }
								
	
				
					}
				}
			}
	
	
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
	}

	@Override
	public List<SSupplier> getSuppList(Map<String, Object> condition,
			String sort) throws Exception {
			List<SSupplier>list=null;
			try {
				list=this.daoFacade.getSuppDao().getList(condition, sort);
			} catch (Exception e) {
				throw new CcsException((e instanceof CcsException) ?((CcsException)e).getPrompt()	: "Laccess.getBookStatisticalList.get.error", e);//获取统计信息分页列表失败！
			}
			return list;
	}

	@Override
	public Integer getSSupplierCount(Map<String, Object> condition)
			throws Exception {
		Integer count = 0;
		try {
			count = this.daoFacade.getSuppDao().getCount(condition);
		} catch (Exception e) {
			// 获取产品信息总数失败！
			e.printStackTrace();
			throw new CcsException(
					(e instanceof CcsException) ? ((CcsException) e).getPrompt()
							: "Laccess.count.get.error", e);// 获取统计总数失败！
		}
		return count;
	}

	@Override
	public List<SSupplier> getTocList(Map<String, Object> condition,
			String sort, Integer pageCount, Integer page) throws Exception {
		List<SSupplier>list=null;
		try {
			list=this.daoFacade.getSuppDao().getTocList(condition, sort, pageCount, page);
		} catch (Exception e) {
			throw new CcsException((e instanceof CcsException) ?((CcsException)e).getPrompt()	: "Laccess.getBookStatisticalList.get.error", e);//获取统计TOC信息分页列表失败！
		}
		return list;
	}

	@Override
	public List<SSupplier> getFullAccessList(Map<String, Object> condition,
			String sort, Integer pageCount, Integer page) throws Exception {
		List<SSupplier>list=null;
		try {
			list=this.daoFacade.getSuppDao().getFullAccessList(condition, sort, pageCount, page);
		} catch (Exception e) {
			throw new CcsException((e instanceof CcsException) ?((CcsException)e).getPrompt()	: "Laccess.getBookStatisticalList.get.error", e);//获取统计TOC信息分页列表失败！
		}
		return list;
	}

	@Override
	public List<SSupplier> getFullRefusedList(Map<String, Object> condition,
			String sort, Integer pageCount, Integer page) throws Exception {
		List<SSupplier>list=null;
		try {
			list=this.daoFacade.getSuppDao().getFullRefusedList(condition, sort, pageCount, page);
		} catch (Exception e) {
			throw new CcsException((e instanceof CcsException) ?((CcsException)e).getPrompt()	: "Laccess.getBookStatisticalList.get.error", e);//获取统计TOC信息分页列表失败！
		}
		return list;
	}

	@Override
	public List<SSupplier> getDownloadList(Map<String, Object> condition,
			String sort, Integer pageCount, Integer page) throws Exception {
		List<SSupplier>list=null;
		try {
			list=this.daoFacade.getSuppDao().getDownloadList(condition, sort, pageCount, page);
		} catch (Exception e) {
			throw new CcsException((e instanceof CcsException) ?((CcsException)e).getPrompt()	: "Laccess.getBookStatisticalList.get.error", e);//获取统计TOC信息分页列表失败！
		}
		return list;
	}

	@Override
	public List<SSupplier> getSearchList(Map<String, Object> condition,
			String sort, Integer pageCount, Integer page) throws Exception {
		List<SSupplier>list=null;
		try {
			list=this.daoFacade.getSuppDao().getSearchList(condition, sort, pageCount, page);
		} catch (Exception e) {
			throw new CcsException((e instanceof CcsException) ?((CcsException)e).getPrompt()	: "Laccess.getBookStatisticalList.get.error", e);//获取统计TOC信息分页列表失败！
		}
		return list;
	}

	@Override
	public Integer getSSupplierCountGroupby(Map<String, Object> condition,
			String group) throws Exception {
		Integer num=0;
		try {
			num=this.daoFacade.getSuppDao().getGroupbyCount(condition, group);
		} catch (Exception e) {
			throw new CcsException((e instanceof CcsException) ? ((CcsException)e).getPrompt()	: "Laccess.count.get.error", e);//获取统计信息总数失败！
		}
		return num;
	}

	

}
