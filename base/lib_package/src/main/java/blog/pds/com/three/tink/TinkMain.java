package blog.pds.com.three.tink;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import com.google.crypto.tink.*;
import com.google.crypto.tink.aead.AeadKeyTemplates;
import com.google.crypto.tink.config.TinkConfig;
import com.google.crypto.tink.integration.android.AndroidKeysetManager;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * @author: pengdaosong
 * CreateTime:  2019-07-09 16:54
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class TinkMain extends Activity {

    private static final String TAG = "TinkMain";
    private static final String PATH = Environment.getExternalStorageDirectory().getPath();
    private static final String KEY_SET_FILE_NAME = "my_keyset.txt";


    private static final String PREF_FILE_NAME = "hello_world_pref";
    private static final String TINK_KEYSET_NAME = "hello_world_keyset";
    private static final String MASTER_KEY_URI = "android-keystore://hello_world_master_key";
    public static Aead aead;

    {
//        try {
//            TinkConfig.register();
//            // Register a custom implementation of AEAD.
//            // Registry.registerKeyManager(new MyAeadKeyManager());
//        } catch (GeneralSecurityException e) {
//            e.printStackTrace();
//        }

        try {
            Config.register(TinkConfig.TINK_1_0_0);
            aead = getOrGenerateNewKeysetHandle().getPrimitive(Aead.class);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private KeysetHandle getOrGenerateNewKeysetHandle() throws IOException, GeneralSecurityException {
        return new AndroidKeysetManager.Builder()
                .withSharedPref(getApplicationContext(), TINK_KEYSET_NAME, PREF_FILE_NAME)
                .withKeyTemplate(AeadKeyTemplates.AES256_GCM)
                .withMasterKeyUri(MASTER_KEY_URI)
                .build()
                .getKeysetHandle();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storingKeysets();
        loadingExistingKeysets();
        String data = "pds";
        symmetricKeyEncryption(data.getBytes(),null);
    }

    public void storingKeysets(){
        Log.e(TAG,"path = "+ PATH);
        try {
            KeysetHandle keysetHandle = KeysetHandle.generateNew(AeadKeyTemplates.AES128_GCM);
            File file = new File(PATH ,KEY_SET_FILE_NAME);
            CleartextKeysetHandle.write(keysetHandle, JsonKeysetWriter.withFile(file));

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadingExistingKeysets(){
        try {
            KeysetHandle keysetHandle = CleartextKeysetHandle.read(JsonKeysetReader.withFile(new File(PATH,KEY_SET_FILE_NAME)));
            Log.e(TAG,"password = "+ keysetHandle.toString());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对称加密
     */
    private void symmetricKeyEncryption(byte[] data,byte[] aad){
        Log.e(TAG,"data = "+ new String(data));
        try {
            // 1. Generate the key material.
            KeysetHandle keysetHandle = KeysetHandle.generateNew(AeadKeyTemplates.AES128_GCM);
            // 2. Get the primitive.
            Aead aead = keysetHandle.getPrimitive(Aead.class);
            // 3. Use the primitive to encrypt a plaintext,
            byte[] ciphertext = aead.encrypt(data, aad);
            Log.e(TAG,"encrypt:data = "+ new String(ciphertext));
            byte[] decrypted = aead.decrypt(ciphertext, aad);
            Log.e(TAG,"decrypted:data = "+ new String(decrypted));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

    }
}
