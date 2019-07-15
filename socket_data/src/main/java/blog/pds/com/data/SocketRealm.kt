package blog.pds.com.data

import io.realm.RealmObject

/**
 * @author: pengdaosong
 * CreateTime:  2019-06-28 17:09
 * Email：pengdaosong@medlinker.com
 * Description:
 */
open class SocketRealm : RealmObject(){

    var id: Long = 0
    var name: String?= null
    var sex: Int = 0
}