package blog.pds.com.socket.app.request.manager

import blog.pds.com.socket.app.SocketManager
import java.io.IOException
import java.math.BigInteger
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.*
import javax.security.cert.CertificateException as CertificateException1

/**
 * @author: pengdaosong
 * CreateTime:  2019-09-17 17:42
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class HttpsX509TrustManager : X509TrustManager {

    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {

    }

    @Throws(CertificateException1::class)
    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        if (chain == null) {
            throw  CertificateException1("checkServerTrusted: X509Certificate array is null")
        }
        if (chain.isEmpty()) {
            throw  CertificateException1("checkServerTrusted: X509Certificate is empty")
        }
        if (!(null != authType && authType == "ECDHE_RSA")) {
            throw CertificateException1 ("checkServerTrusted: AuthType is not ECDHE_RSA")
        }

        try {
            //检查所有证书
            val factory = TrustManagerFactory.getInstance("X509")
            factory.init(null as KeyStore)
            for (trustManager in factory.trustManagers){
                (trustManager as X509TrustManager).checkServerTrusted(chain, authType)
            }
        }catch (e : NoSuchAlgorithmException){
            e.printStackTrace()
        }catch (e : KeyStoreException){
            e.printStackTrace()
        }


        //获取本地证书中的信息
        var clientEncoded = ""
        var clientSubject = ""
        var clientIssUser = ""

        try {
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val inputStream = SocketManager.application?.assets?.open("baidu.cer")
            val clientCertificate = certificateFactory.generateCertificate(inputStream) as X509Certificate
            clientEncoded = BigInteger(1, clientCertificate.publicKey.encoded).toString(16)
            clientSubject = clientCertificate.subjectDN.name
            clientIssUser = clientCertificate.issuerDN.name
        } catch (e : IOException) {
            e.printStackTrace()
        }
        //获取网络中的证书信息
        val certificate = chain[0]
        val publicKey = certificate.publicKey
        val serverEncoded = BigInteger(1, publicKey.encoded).toString(16)

        if (clientEncoded != serverEncoded) {
            throw CertificateException1("server's PublicKey is not equals to client's PublicKey")
        }
        val subject = certificate.subjectDN.name
        if (clientSubject != subject) {
            throw CertificateException1("server's subject is not equals to client's subject")
        }
        val issuser = certificate.issuerDN.name
        if (clientIssUser != issuser) {
            throw CertificateException1("server's issuser is not equals to client's issuser")
        }
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return emptyArray()
    }

    companion object{
        @Throws()
        fun getSSLSocketFactory() : SSLSocketFactory {
            val context = SSLContext.getInstance("TLS")
            val trustManagers = arrayOf(HttpsX509TrustManager())
            context.init(null,trustManagers, SecureRandom())
            return context.socketFactory
        }
    }
}
