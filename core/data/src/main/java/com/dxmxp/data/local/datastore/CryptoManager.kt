package com.dxmxp.data.local.datastore

import android.content.Context
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoManager @Inject constructor(context: Context) {

    private val aead: Aead

    init {
        AeadConfig.register()
        aead = AndroidKeysetManager.Builder()
            .withSharedPref(context, KEYSET_NAME, PREF_FILE_NAME)
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle
            .getPrimitive(Aead::class.java)
    }

    fun encrypt(data: ByteArray): ByteArray {
        return aead.encrypt(data, null)
    }

    fun decrypt(encryptedData: ByteArray): ByteArray {
        return aead.decrypt(encryptedData, null)
    }

    companion object {
        private const val KEYSET_NAME = "master_keyset"
        private const val PREF_FILE_NAME = "master_key_preference"
        private const val MASTER_KEY_URI = "android-keystore://master_key"
    }
}
