package org.ordogene.file.utils;

import java.util.List;

public class ApiJsonResponse {
	private String id;
	private int cid;
	private String error;
	private List<Calculation> list;
	private String base64img;

	public ApiJsonResponse() {

	}

	public ApiJsonResponse(String id, int cid, String error, List<Calculation> list, String base64img) {
		this.id = id;
		this.cid = cid;
		this.error = error;
		this.list = list;
		this.base64img = base64img;
	}

	public String getBase64img() {
		return base64img;
	}

	public void setBase64img(String base64img) {
		this.base64img = base64img;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
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
