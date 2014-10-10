package cn.koolcloud.ipos.appstore.entity;

import java.io.Serializable;

public class News implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String type;//推送信息类型 1：系统图文消息 
	private String id;	//图文消息ID，客户端缓存标识用 
	private String title;//图文消息标题 
	private String date;//图文消息日期 
	private String imgId;//图文消息图片ID，下载图片用 
	private String description;//图文消息内容 
	
	public News() {
		super();
	}
	
	public News(String type,String id,String title, String date, String imgId, String description) {
		super();
		this.type = type;
		this.id = id;
		this.title = title;
		this.date = date;
		this.imgId = imgId;
		this.description = description;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the time
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param time the time to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	/**
	 * @return the imgUrl
	 */
	public String getImgId() {
		return imgId;
	}
	/**
	 * @param imgUrl the imgUrl to set
	 */
	public void setImgId(String imgUrl) {
		this.imgId = imgUrl;
	}
	/**
	 * @return the message
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param message the message to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	public String getPushFileIcon(){
		return imgId+"_"+title+"_.png";
	}
	
}
