package org.ordogene.file.utils;

import java.util.List;

import org.ordogene.file.parser.Validable;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * POJO
 * @author darwinners team
 *
 */
public class ApiJsonResponse implements Validable {
	private String userId;
	private int cid;
	private String error;
	private List<Calculation> list;
	private String base64img;

	public ApiJsonResponse() {

	}

	public ApiJsonResponse(String userId, int cid, String error, List<Calculation> list, String base64img) {
		this.userId = userId;
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
		return "ApiJsonResponse [id=" + userId + ", cid=" + cid + ", error=" + error + ", list=" + list + ", img="
				+ base64img + "]";
	}

	@JsonIgnore
	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cid;
		result = prime * result + ((error == null) ? 0 : error.hashCode());
		result = prime * result + ((base64img == null) ? 0 : base64img.hashCode());
		result = prime * result + ((list == null) ? 0 : list.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ApiJsonResponse other = (ApiJsonResponse) obj;
		if (cid != other.cid)
			return false;
		if (error == null) {
			if (other.error != null)
				return false;
		} else if (!error.equals(other.error))
			return false;
		if (base64img == null) {
			if (other.base64img != null)
				return false;
		} else if (!base64img.equals(other.base64img))
			return false;
		if (list == null) {
			if (other.list != null)
				return false;
		} else if (!list.equals(other.list))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

}
