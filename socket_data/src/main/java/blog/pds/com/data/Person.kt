package blog.pds.com.data

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey



/**
 * @author: pengdaosong
 * CreateTime:  2019-06-28 17:31
 * Email：pengdaosong@medlinker.com
 * Description:
 */
open class Person: RealmObject(){
    @PrimaryKey
    var id: Long = 0
    var name: String? = null
    var dogs: RealmList<SocketRealm>? = null
}