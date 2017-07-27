package cn.syc.bean;

public class Result {
	public String resultStr;
	public int fileCount;
	public String getResultStr() {
		return resultStr;
	}
	public void setResultStr(String resultStr) {
		this.resultStr = resultStr;
	}
	public int getFileCount() {
		return fileCount;
	}
	public void setFileCount(int fileCount) {
		this.fileCount = fileCount;
	}
	
	public String toString(){
		return "Result:"+this.resultStr +"|"+ this.fileCount;
	}
	
}
