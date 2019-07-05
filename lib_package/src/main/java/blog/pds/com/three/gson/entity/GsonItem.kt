package blog.pds.com.three.gson.entity

import com.google.gson.annotations.SerializedName


/**
 * @author: pengdaosong
 * CreateTime:  2019/5/17 10:24 AM
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class GsonItem {

    @SerializedName(value = "user_phone")
    var errcode : Int = 0
    var errmsg : String = ""
    var sex : Int?  = -1

    override fun toString(): String {
        return "GsonItem(errcode=$errcode, errmsg='$errmsg', sex=$sex)"
    }
}