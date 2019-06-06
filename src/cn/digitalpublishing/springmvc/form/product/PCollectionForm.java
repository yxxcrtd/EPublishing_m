package cn.digitalpublishing.springmvc.form.product;

import cn.digitalpublishing.ep.po.PCollection;
import cn.digitalpublishing.springmvc.form.BaseForm;

public class PCollectionForm extends BaseForm {
	
	private Integer marktype;
	
	private String typeValue;
	
	private String collectionId;
	/**
	 * 分类Id
	 */
	private String subId;
	/**
	 * 产品类型 1-书籍 2-期刊
	 */
	private String type;
	/**
	 * 出版商
	 */
	private String publishersId;
	private boolean inLicense=false;//是否成功购买（后台确认）
	private boolean inOrderDetail=false;//是否在购物车中
	
	/**
	 * 针对外文文章单篇购买设定的几个查询参数
	 */
	private String publisherId;
	private String subjectId;
	private String langId;
	
	
	private PCollection obj;
	
	
	public PCollection getObj() {
		return obj;
	}
	public void setObj(PCollection obj) {
		this.obj = obj;
	}
	public String getSubId() {
		return subId;
	}
	public void setSubId(String subId) {
		this.subId = subId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPublishersId() {
		return publishersId;
	}
	public void setPublishersId(String publishersId) {
		this.publishersId = publishersId;
	}
	public Integer getMarktype() {
		return marktype;
	}
	public void setMarktype(Integer marktype) {
		this.marktype = marktype;
	}
	public String getTypeValue() {
		return typeValue;
	}
	public void setTypeValue(String typeValue) {
		this.typeValue = typeValue;
	}
	public String getCollectionId() {
		return collectionId;
	}
	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}
	public boolean isInLicense() {
		return inLicense;
	}
	public void setInLicense(boolean inLicense) {
		this.inLicense = inLicense;
	}
	public boolean isInOrderDetail() {
		return inOrderDetail;
	}
	public void setInOrderDetail(boolean inOrderDetail) {
		this.inOrderDetail = inOrderDetail;
	}
	public String getPublisherId() {
		return publisherId;
	}
	public void setPublisherId(String publisherId) {
		this.publisherId = publisherId;
	}
	public String getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	public String getLangId() {
		return langId;
	}
	public void setLangId(String langId) {
		this.langId = langId;
	}
	
}
