package com.meng.hui.android.xiezuo.entity;

public class VersionCheckEntity {

	private int lastVersion;
	private String downloadUrl;
	private int length;
	public int getLastVersion() {
		return lastVersion;
	}
	public void setLastVersion(int lastVersion) {
		this.lastVersion = lastVersion;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}

}
