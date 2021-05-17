package blog.pds.com.three.tink;

import android.os.Environment;

import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetWriter;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AesGcmKeyManager;
import com.google.crypto.tink.config.TinkConfig;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class TinkTest {

    public static void main(String[] args) {
        try {
            TinkConfig.register();
            // To use only implementations of the AEAD primitive:
            // AeadConfig.register();

            // Register a custom implementation of AEAD.
            // Registry.registerKeyManager(new MyAeadKeyManager());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }


    private static final String PATH = Environment.getExternalStorageDirectory().getPath();
    private static final String KEY_SET_FILE_NAME = "tink_keyset.txt";

    private static void storingKeySets(){
        try {
            KeysetHandle keysetHandle = KeysetHandle.generateNew(AesGcmKeyManager.aes128GcmTemplate());
            File file = new File(PATH ,KEY_SET_FILE_NAME);
            CleartextKeysetHandle.write(keysetHandle, JsonKeysetWriter.withFile(file));

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
