package com.multiplatform.app.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.multiplatform.app.common.createDataStore
import com.multiplatform.app.common.dataStoreFileName
import com.multiplatform.app.data.local.datastore.DataStoreProvider
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
class DataStoreProviderImpl : DataStoreProvider {
    override val dataStore: DataStore<Preferences> = createDataStore(
        coroutineScope = CoroutineScope(Job() + Dispatchers.IO),
        producePath = {
            val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null,
            )
            requireNotNull(documentDirectory).path + "/$dataStoreFileName"
        },
    )
}
