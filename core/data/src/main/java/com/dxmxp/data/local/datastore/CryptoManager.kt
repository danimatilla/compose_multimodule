package com.dxmxp.data.local.datastore

import android.content.Context
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import java.security.KeyStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoManager @Inject constructor(context: Context) {

    private val aead: Aead

    init {
        AeadConfig.register()
        aead = createAead(context)
    }

    private fun createAead(context: Context): Aead {
        return try {
            buildAead(context)
        } catch (e: Exception) {
            clearKeystoreState(context)
            buildAead(context)
        }
    }

    private fun buildAead(context: Context): Aead = AndroidKeysetManager.Builder()
        .withSharedPref(context, KEYSET_NAME, PREF_FILE_NAME)
        .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
        .withMasterKeyUri(MASTER_KEY_URI)
        .build()
        .keysetHandle
        .getPrimitive(Aead::class.java)

    private fun clearKeystoreState(context: Context) {
        val aliasesToDelete = listOf("master_key", KEYSTORE_ALIAS)
        val prefFilesToClear = listOf("master_key_preference", PREF_FILE_NAME)

        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            aliasesToDelete.forEach { alias ->
                if (keyStore.containsAlias(alias)) {
                    keyStore.deleteEntry(alias)
                }
            }
        } catch (_: Exception) {
            // The keystore may already be invalid or inaccessible; we continue by clearing the local keyset metadata.
        }

        prefFilesToClear.forEach { prefName ->
            context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply()
        }
    }

    fun encrypt(data: ByteArray): ByteArray {
        return aead.encrypt(data, null)
    }

    fun decrypt(encryptedData: ByteArray): ByteArray {
        return aead.decrypt(encryptedData, null)
    }

    companion object {
        private const val KEYSET_NAME = "seed_master_keyset"
        private const val PREF_FILE_NAME = "seed_master_key_preference"
        private const val KEYSTORE_ALIAS = "seed_master_key"
        private const val MASTER_KEY_URI = "android-keystore://$KEYSTORE_ALIAS"
    }
}
