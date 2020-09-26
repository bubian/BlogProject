package com.pds.web.param;


public class HybridParamForward extends HybridParam {
    public String topage; // 如果type是H5,要求topage为一个完整的url;如果是native要求是native同事告诉h5的字符串,点击就能跳转到对应native页面
    public HybridParamType type = HybridParamType.NATIVE;
    public HybridParamAnimation animate = HybridParamAnimation.PUSH;
    public boolean hasnavgation = true;
}
