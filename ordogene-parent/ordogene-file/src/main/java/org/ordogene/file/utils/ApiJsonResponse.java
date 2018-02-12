package org.ordogene.file.utils;

import java.awt.image.BufferedImage;
import java.util.List;

public class ApiJsonResponse {
	private int id;
	private int cid;
	private String error;
	private List<Calculation> list;
	private BufferedImage img;
	
	public BufferedImage getImg() {
		return img;
	}

	public void setImg(BufferedImage img) {
		this.img = img;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
}
