package org.ordogene.file.utils;

import java.awt.image.BufferedImage;
import java.util.List;

import org.ordogene.file.parser.Validable;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ApiJsonResponse implements Validable{
	private String userId;
	private int cid;
	private String error;
	private List<Calculation> list;
	private BufferedImage img;
	
	public ApiJsonResponse() {
		
	}
	
	public ApiJsonResponse(String userId, int cid, String error, List<Calculation> list, BufferedImage img) {
		this.userId = userId;
		this.cid = cid;
		this.error = error;
		this.list = list;
		this.img = img;
	}

	public BufferedImage getImg() {
		return img;
	}

	public void setImg(BufferedImage img) {
		this.img = img;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public List<Calculation> getList() {
		return list;
	}

	public void setList(List<Calculation> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "ApiJsonResponse [id=" + userId + ", cid=" + cid + ", error=" + error + ", list=" + list + ", img=" + img
				+ "]";
	}

	@JsonIgnore
	@Override
	public boolean isValid() {
		return true;
	}
	
	
}
