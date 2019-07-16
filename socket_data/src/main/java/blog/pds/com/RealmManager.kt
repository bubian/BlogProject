package blog.pds.com

import android.content.Context
import blog.pds.com.data.Person
import blog.pds.com.data.SocketRealm
import com.facebook.stetho.Stetho
import com.uphyca.stetho_realm.RealmInspectorModulesProvider
import io.realm.*


/**
 * @author: pengdaosong
 * CreateTime:  2019-06-28 16:45
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class RealmManager{

    fun init(context: Context){

        val sr = SocketRealm()
        sr.id = 1000
        sr.name = "pds"

        Realm.init(context)
        // Get a Realm instance for this thread
        val realm = Realm.getDefaultInstance()
        val puppies = realm.where(SocketRealm::class.java).lessThan("age", 2).findAll()

        puppies.size

        realm.beginTransaction()
        val socketRealm = realm.copyToRealm(sr)
        val person = realm.createObject(Person::class.java)
        person.dogs?.add(socketRealm)
        realm.commitTransaction()

        puppies.addChangeListener { _, changeSet -> changeSet.insertions }

        realm.executeTransactionAsync(Realm.Transaction {
            val r = it.where(SocketRealm::class.java).equalTo("id",1000L).findFirst()
            r?.sex = 1
        }, Realm.Transaction.OnSuccess {
            puppies.size
            socketRealm.name
        })

    }

    /**
     * https://realm.io/docs/java/latest/#installation
     */
    fun config(){
        val config = RealmConfiguration.Builder()
            .name("myrealm.realm")
            .encryptionKey(null)
            .schemaVersion(42)
//            .modules( MySchemaModule())
//            .migration( MyMigration())
            .build()
        val realm = Realm.getInstance(config);
    }

    /**
     * 用于浏览器查看realm数据库
     */
    private fun initStetho(context: Context){
        Stetho.initialize(
            Stetho.newInitializerBuilder(context)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
                .enableWebKitInspector(RealmInspectorModulesProvider.builder(context).build())
                .build());
    }
}