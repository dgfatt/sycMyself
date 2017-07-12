package cn.syc.bean;

public class PictureLog {

	private String title;
	private String url;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public PictureLog(String t, String u) {
		this.title = t;
		this.url = u;
	}

}