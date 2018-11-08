package com.meng.hui.android.xiezuo.entity;

public class VersionCheckEntity {

	private int lastVersion;
    private String versionName;
	private String downloadUrl;
	private int length;
	private String versionDetail;

	public int getLastVersion() {
		return lastVersion;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
    public String getVersionName() {
        return versionName;
    }
    public int getLength() {
		return length;
	}
	public String getVersionDetail() {
		return versionDetail;
	}
}
