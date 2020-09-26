package com.pds.web.param;

import java.util.ArrayList;
import java.util.List;


public class HybridParamUpdateHeader extends HybridParam {
    public ArrayList<NavgationButtonParam> left;
    public ArrayList<NavgationButtonParam> right;
    public NavgationTitleParam title;
    public NavgationStyle style = new NavgationStyle();


    /**
     * 设置header callback id
     * @param id
     */
    public void setHybridParamCallbackId(int id){
        this.id = id;
        setCallbackListId(left, id);
        setCallbackListId(right, id);
        if(title != null){
            title.id = id;
        }

    }

    private void setCallbackListId(List<NavgationButtonParam> callbacks, int id) {
        if(callbacks != null){
            for(HybridParamCallback callback:callbacks){
                callback.id = id;
            }
        }
    }

    public static class NavgationButtonParam extends HybridParamCallback {
        public String icon;
        public String value;
    }

    public static class NavgationTitleParam extends HybridParamCallback {
        public String title;
        public String subtitle;
        public String lefticon;
        public String righticon;
        public String placeholder;//hint
        public boolean focus;

    }

    public static class NavgationStyle {
        public int backgroundcolor = 0xff658dfe;
    }
}
