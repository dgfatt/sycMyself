package cn.syc.listener;


public interface OnSelectedListener {
	// 定义接口获取选择的编号v,门牌
	void OnSelected(int v,String door);

	void OnfingerDown(float x, float y);

}