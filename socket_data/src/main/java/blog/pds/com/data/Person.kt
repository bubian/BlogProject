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
class Person: RealmObject(){
    @PrimaryKey
    val id: Long = 0
    val name: String? = null
    val dogs: RealmList<SocketRealm>? = null
}