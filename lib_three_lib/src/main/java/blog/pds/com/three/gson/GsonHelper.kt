package blog.pds.com.three.gson

import blog.pds.com.three.gson.adapter.IntTypeAdapter
import blog.pds.com.three.gson.adapter.StringTypeAdapter
import com.google.gson.GsonBuilder
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import java.io.Reader
import java.lang.reflect.Type

/**
 * @author: pengdaosong
 * CreateTime:  2019/5/21 4:45 PM
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class GsonHelper {

    companion object {
        private val gs = GsonBuilder()
            .registerTypeAdapter(Int::class.java, IntTypeAdapter())
            .registerTypeAdapter(String::class.java, StringTypeAdapter())
            .create()

        @Throws(JsonSyntaxException::class)
        fun <T> fromJson(json: String, classOfT: Class<T>): T? {
            return gs.fromJson(json, classOfT)
        }

        @Throws(JsonSyntaxException::class)
        fun <T> fromJson(json: String?, typeOfT: Type): T? {
            return gs.fromJson(json, typeOfT)
        }

        @Throws(JsonSyntaxException::class, JsonIOException::class)
        fun <T> fromJson(json: Reader, classOfT: Class<T>): T {
            return gs.fromJson(json, classOfT)
        }

        @Throws(JsonIOException::class, JsonSyntaxException::class)
        fun <T> fromJson(json: Reader, typeOfT: Type): T {
            return gs.fromJson(json, typeOfT)
        }


    }

}