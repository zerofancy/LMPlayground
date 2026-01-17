package com.druk.lmplayground.storage

import android.app.Application
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.druk.lmplayground.models.ModelInfo
import com.druk.lmplayground.models.ModelInfoProvider
import com.druk.lmplayground.models.ModelWithStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Represents download progress for a model
 * @param modelName The name of the model being downloaded
 * @param progress Download progress from 0f to 1f, or -1f for indeterminate (copying to storage)
 * @param status Human readable status text
 */
data class DownloadProgress(
    val modelName: String,
    val progress: Float,
    val status: String
)

/**
 * Represents a pending migration from old storage to new storage.
 * @param oldUri Source folder URI, or null if migrating from Downloads folder
 */
data class MigrationState(
    val oldUri: Uri?,
    val newUri: Uri,
    val modelsToMigrate: List<ModelFile>,
    val isFromDownloads: Boolean = oldUri == null
)

/**
 * Represents migration progress
 */
data class MigrationProgress(
    val currentModel: String,
    val currentIndex: Int,
    val totalCount: Int
)

class StorageViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context get() = getApplication()
    private val prefs = StoragePreferences(application)
    private val repository = StorageRepository(application, prefs)

    private val _storageInfo = MutableLiveData<StorageInfo>()
    val storageInfo: LiveData<StorageInfo> = _storageInfo

    private val _downloadedModels = MutableLiveData<List<ModelFile>>()
    val downloadedModels: LiveData<List<ModelFile>> = _downloadedModels

    private val _allModels = MutableLiveData<List<ModelWithStatus>>()
    val allModels: LiveData<List<ModelWithStatus>> = _allModels

    private val _isStorageConfigured = MutableLiveData<Boolean>()
    val isStorageConfigured: LiveData<Boolean> = _isStorageConfigured
    
    private val _downloadingModels = MutableLiveData<Map<String, DownloadProgress>>(emptyMap())
    val downloadingModels: LiveData<Map<String, DownloadProgress>> = _downloadingModels
    
    // Synchronized backing field to avoid race conditions with postValue
    private val downloadingModelsLock = Any()
    private var _downloadingModelsMap = mutableMapOf<String, DownloadProgress>()
    
    private val _snackbarMessage = MutableLiveData<String?>()
    val snackbarMessage: LiveData<String?> = _snackbarMessage
    
    // Migration state
    private val _pendingMigration = MutableLiveData<MigrationState?>()
    val pendingMigration: LiveData<MigrationState?> = _pendingMigration
    
    private val _migrationProgress = MutableLiveData<MigrationProgress?>()
    val migrationProgress: LiveData<MigrationProgress?> = _migrationProgress
    
    // Download Manager
    private val downloadManager: DownloadManager by lazy {
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }
    
    // Maps download ID to ModelInfo for tracking
    private val downloadingModelIds = mutableMapOf<Long, ModelInfo>()
    
    // Progress polling job
    private var progressJob: Job? = null
    
    private val downloadReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val model = downloadingModelIds[id] ?: return
            val query = DownloadManager.Query()
            query.setFilterById(id)
            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                when (cursor.getInt(columnIndex)) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        handleDownloadComplete(model, model.filename)
                    }
                    DownloadManager.STATUS_FAILED -> {
                        val errorMessage = getDownloadErrorMessage(cursor)
                        removeDownloadProgress(model.name)
                        showSnackbar("${model.name}: $errorMessage")
                        cleanupTempFile(model)
                        loadStorageInfo()
                    }
                }
            } else {
                // Download was cancelled (no cursor entry found)
                removeDownloadProgress(model.name)
                showSnackbar("${model.name}: Download cancelled")
                cleanupTempFile(model)
            }
            cursor.close()
            downloadingModelIds.remove(id)
        }
    }
    
    init {
        // Register broadcast receiver
        ContextCompat.registerReceiver(
            context,
            downloadReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            ContextCompat.RECEIVER_EXPORTED
        )
    }
    
    override fun onCleared() {
        super.onCleared()
        try {
            context.unregisterReceiver(downloadReceiver)
        } catch (e: Exception) {
            // Already unregistered
        }
        progressJob?.cancel()
    }
    
    fun updateDownloadProgress(modelName: String, progress: Float, status: String) {
        synchronized(downloadingModelsLock) {
            _downloadingModelsMap[modelName] = DownloadProgress(modelName, progress, status)
            _downloadingModels.postValue(_downloadingModelsMap.toMap())
        }
    }
    
    fun removeDownloadProgress(modelName: String) {
        synchronized(downloadingModelsLock) {
            _downloadingModelsMap.remove(modelName)
            _downloadingModels.postValue(_downloadingModelsMap.toMap())
        }
    }
    
    /**
     * Sync download progress with DownloadManager state.
     * This replaces all current progress with the provided map.
     */
    private fun syncDownloadProgress(downloads: Map<String, DownloadProgress>) {
        synchronized(downloadingModelsLock) {
            _downloadingModelsMap.clear()
            _downloadingModelsMap.putAll(downloads)
            _downloadingModels.postValue(_downloadingModelsMap.toMap())
        }
    }
    
    fun showSnackbar(message: String) {
        _snackbarMessage.postValue(message)
    }
    
    fun clearSnackbar() {
        _snackbarMessage.postValue(null)
    }

    fun checkStorageConfigured() {
        _isStorageConfigured.postValue(repository.isStorageConfigured())
    }

    fun loadStorageInfo() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _storageInfo.postValue(repository.getStorageInfo())
                val modelFiles = repository.getModelFiles()
                _downloadedModels.postValue(modelFiles)
                val downloadedFilenames = modelFiles.map { it.name }.toSet()
                _allModels.postValue(ModelInfoProvider.getModelsWithStatus(downloadedFilenames))
            }
            // Sync download state after models are loaded
            syncDownloadStateFromManager()
            // Ensure progress polling is running if there are active downloads
            startProgressPolling()
        }
    }

    fun deleteModel(model: ModelFile) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteModel(model.name)
            }
            loadStorageInfo()
        }
    }
    
    fun deleteModel(model: ModelInfo) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteModel(model.filename)
            }
            loadStorageInfo()
        }
    }

    /**
     * Request to change storage folder. If old folder has models, shows migration dialog.
     * For first-time setup (no old folder), checks Downloads folder for existing models.
     */
    fun requestStorageFolderChange(newUri: Uri) {
        val oldUri = repository.getStorageUri()
        
        viewModelScope.launch {
            if (oldUri == null) {
                // First time setup - check Downloads folder for existing models
                val modelsInDownloads = withContext(Dispatchers.IO) {
                    getModelFilesFromDownloads()
                }
                
                if (modelsInDownloads.isEmpty()) {
                    // No models to migrate, just set folder
                    setStorageFolderInternal(newUri)
                } else {
                    // Show migration dialog for Downloads folder
                    _pendingMigration.value = MigrationState(
                        oldUri = null, // null indicates Downloads folder
                        newUri = newUri,
                        modelsToMigrate = modelsInDownloads
                    )
                }
            } else if (oldUri == newUri) {
                // Same folder, nothing to do
                return@launch
            } else {
                // Changing from one folder to another - check old folder for models
                val modelsInOldFolder = withContext(Dispatchers.IO) {
                    getModelFilesFromUri(oldUri)
                }
                
                if (modelsInOldFolder.isEmpty()) {
                    // No models to migrate, just change folder
                    setStorageFolderInternal(newUri)
                } else {
                    // Show migration dialog
                    _pendingMigration.value = MigrationState(
                        oldUri = oldUri,
                        newUri = newUri,
                        modelsToMigrate = modelsInOldFolder
                    )
                }
            }
        }
    }
    
    /**
     * User confirmed migration - copy models from old folder to new folder
     */
    fun confirmMigration() {
        val migration = _pendingMigration.value ?: return
        _pendingMigration.value = null
        
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val models = migration.modelsToMigrate
                val newDocumentFile = DocumentFile.fromTreeUri(context, migration.newUri)
                
                if (newDocumentFile == null) {
                    showSnackbar("Cannot access new folder")
                    return@withContext
                }
                
                var successCount = 0
                var failCount = 0
                
                models.forEachIndexed { index, modelFile ->
                    _migrationProgress.postValue(
                        MigrationProgress(
                            currentModel = modelFile.displayName,
                            currentIndex = index + 1,
                            totalCount = models.size
                        )
                    )
                    
                    try {
                        // Read from old location - handle both file:// and content:// URIs
                        val inputStream = if (migration.isFromDownloads) {
                            // Downloads folder uses file:// URI
                            File(modelFile.uri.path!!).inputStream()
                        } else {
                            // SAF folder uses content:// URI
                            context.contentResolver.openInputStream(modelFile.uri)
                        }
                        
                        if (inputStream == null) {
                            failCount++
                            return@forEachIndexed
                        }
                        
                        // Delete existing file in new location if any
                        newDocumentFile.findFile(modelFile.name)?.delete()
                        
                        // Create file in new location
                        val destFile = newDocumentFile.createFile("application/octet-stream", modelFile.name)
                        if (destFile == null) {
                            inputStream.close()
                            failCount++
                            return@forEachIndexed
                        }
                        
                        // Copy content
                        context.contentResolver.openOutputStream(destFile.uri)?.use { outputStream ->
                            inputStream.use { input ->
                                input.copyTo(outputStream, bufferSize = 8192)
                            }
                        }
                        
                        successCount++
                    } catch (e: Exception) {
                        failCount++
                    }
                }
                
                _migrationProgress.postValue(null)
                
                // Set new folder
                repository.setStorageFolder(migration.newUri)
                _isStorageConfigured.postValue(true)
                
                // Show result
                if (failCount == 0) {
                    showSnackbar("Migrated $successCount model(s)")
                } else {
                    showSnackbar("Migrated $successCount, failed $failCount model(s)")
                }
            }
            
            loadStorageInfo()
        }
    }
    
    /**
     * User declined migration - just change to new folder without copying
     */
    fun skipMigration() {
        val migration = _pendingMigration.value ?: return
        _pendingMigration.value = null
        setStorageFolderInternal(migration.newUri)
    }
    
    /**
     * User cancelled folder change
     */
    fun cancelMigration() {
        _pendingMigration.value = null
    }
    
    private fun setStorageFolderInternal(uri: Uri) {
        repository.setStorageFolder(uri)
        _isStorageConfigured.postValue(true)
        loadStorageInfo()
    }
    
    /**
     * Get model files from a specific URI (used for migration check)
     * Only returns files that match known model filenames.
     */
    private fun getModelFilesFromUri(uri: Uri): List<ModelFile> {
        val documentFile = DocumentFile.fromTreeUri(context, uri) ?: return emptyList()
        val knownFilenames = ModelInfoProvider.knownFilenames
        
        return documentFile.listFiles()
            .filter { it.name in knownFilenames }
            .mapNotNull { file ->
                val name = file.name ?: return@mapNotNull null
                ModelFile(
                    name = name,
                    displayName = ModelInfoProvider.getDisplayName(name),
                    sizeBytes = file.length(),
                    uri = file.uri
                )
            }
    }
    
    /**
     * Get model files from the system Downloads folder (for first-time migration)
     * Only returns files that match known model filenames.
     */
    private fun getModelFilesFromDownloads(): List<ModelFile> {
        val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(
            android.os.Environment.DIRECTORY_DOWNLOADS
        )
        
        if (!downloadsDir.exists() || !downloadsDir.canRead()) {
            return emptyList()
        }
        
        val knownFilenames = ModelInfoProvider.knownFilenames
        
        return downloadsDir.listFiles()
            ?.filter { it.isFile && it.name in knownFilenames }
            ?.map { file ->
                ModelFile(
                    name = file.name,
                    displayName = ModelInfoProvider.getDisplayName(file.name),
                    sizeBytes = file.length(),
                    uri = Uri.fromFile(file)
                )
            } ?: emptyList()
    }

    fun hasValidPermission(): Boolean {
        return repository.hasValidPermission()
    }

    fun getRepository(): StorageRepository = repository
    
    // ==================== Download Management ====================
    
    fun downloadModel(model: ModelInfo) {
        // Set initial download progress
        updateDownloadProgress(model.name, 0f, "Starting download...")
        
        val request = DownloadManager.Request(model.remoteUri)
        request.setTitle(model.filename)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        
        // Download to app's external files dir, then copy to SAF folder
        request.setDestinationInExternalFilesDir(context, null, model.filename)
        
        val downloadId = downloadManager.enqueue(request)
        downloadingModelIds[downloadId] = model
        
        // Start progress polling
        startProgressPolling()
    }
    
    fun cancelDownload(model: ModelInfo) {
        // Find download ID by model name and cancel it
        val entry = downloadingModelIds.entries.find { it.value.name == model.name }
        if (entry != null) {
            downloadManager.remove(entry.key)
            downloadingModelIds.remove(entry.key)
        }
        
        // Also try to find by URI in case it wasn't in our local tracking
        val query = DownloadManager.Query()
        val cursor = downloadManager.query(query)
        while (cursor.moveToNext()) {
            val idIndex = cursor.getColumnIndex(DownloadManager.COLUMN_ID)
            val uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_URI)
            if (idIndex != -1 && uriIndex != -1) {
                val downloadUri = cursor.getString(uriIndex)
                if (downloadUri == model.remoteUri.toString()) {
                    val downloadId = cursor.getLong(idIndex)
                    downloadManager.remove(downloadId)
                    downloadingModelIds.remove(downloadId)
                }
            }
        }
        cursor.close()
        
        // Clean up temp file
        cleanupTempFile(model)
        
        // Progress will be synced on next tick
    }
    
    private fun startProgressPolling() {
        if (progressJob?.isActive == true) return
        
        progressJob = viewModelScope.launch {
            while (isActive) {
                syncDownloadStateFromManager()
                
                // Stop polling if no active downloads
                if (_downloadingModelsMap.isEmpty()) {
                    break
                }
                delay(250)
            }
        }
    }
    
    /**
     * Query DownloadManager for all active downloads and sync UI state.
     */
    private fun syncDownloadStateFromManager() {
        val modelsWithStatus = _allModels.value ?: return
        
        // Query all running, pending, and paused downloads
        val query = DownloadManager.Query()
        query.setFilterByStatus(
            DownloadManager.STATUS_RUNNING or 
            DownloadManager.STATUS_PENDING or 
            DownloadManager.STATUS_PAUSED
        )
        
        val cursor = downloadManager.query(query)
        val activeDownloads = mutableMapOf<String, DownloadProgress>()
        val activeDownloadIds = mutableSetOf<Long>()
        
        while (cursor.moveToNext()) {
            val idIndex = cursor.getColumnIndex(DownloadManager.COLUMN_ID)
            val uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_URI)
            val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            
            if (idIndex == -1 || uriIndex == -1 || statusIndex == -1) continue
            
            val downloadId = cursor.getLong(idIndex)
            val downloadUri = cursor.getString(uriIndex)
            val status = cursor.getInt(statusIndex)
            
            // Find matching model by URI
            val modelInfo = modelsWithStatus.find { it.model.remoteUri.toString() == downloadUri }?.model ?: continue
            
            activeDownloadIds.add(downloadId)
            
            // Track this download
            if (!downloadingModelIds.containsKey(downloadId)) {
                downloadingModelIds[downloadId] = modelInfo
            }
            
            // Build progress info
            val progress: DownloadProgress = when (status) {
                DownloadManager.STATUS_RUNNING -> {
                    val bytesDownloadedIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                    val bytesTotalIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                    if (bytesDownloadedIndex != -1 && bytesTotalIndex != -1) {
                        val bytesDownloaded = cursor.getLong(bytesDownloadedIndex)
                        val bytesTotal = cursor.getLong(bytesTotalIndex)
                        val progressValue = if (bytesTotal > 0) bytesDownloaded.toFloat() / bytesTotal else 0f
                        val percent = (progressValue * 100).toInt()
                        DownloadProgress(modelInfo.name, progressValue, "Downloading $percent%")
                    } else {
                        DownloadProgress(modelInfo.name, 0f, "Downloading...")
                    }
                }
                DownloadManager.STATUS_PENDING -> {
                    DownloadProgress(modelInfo.name, 0f, "Pending...")
                }
                DownloadManager.STATUS_PAUSED -> {
                    DownloadProgress(modelInfo.name, -1f, "Paused")
                }
                else -> continue
            }
            
            activeDownloads[modelInfo.name] = progress
        }
        cursor.close()
        
        // Find downloads that were in our tracking but are no longer active (cancelled)
        val previouslyTracked = downloadingModelIds.keys.toSet()
        val cancelledIds = previouslyTracked - activeDownloadIds
        val cancelledModels = cancelledIds.mapNotNull { downloadingModelIds[it] }
        
        // Clean up cancelled downloads
        if (cancelledModels.isNotEmpty()) {
            cancelledIds.forEach { downloadingModelIds.remove(it) }
            cancelledModels.forEach { cleanupTempFile(it) }
            
            // Show snackbar for cancelled downloads
            val message = if (cancelledModels.size == 1) {
                "${cancelledModels.first().name}: Download cancelled"
            } else {
                "${cancelledModels.size} downloads cancelled"
            }
            showSnackbar(message)
        }
        
        // Update with current state
        syncDownloadProgress(activeDownloads)
    }
    
    private fun handleDownloadComplete(model: ModelInfo, filename: String) {
        viewModelScope.launch {
            // Show "Moving to storage" status
            updateDownloadProgress(model.name, -1f, "Moving to storage...")
            
            withContext(Dispatchers.IO) {
                // Get downloaded file from app's external files dir
                val tempFile = File(context.getExternalFilesDir(null), filename)
                if (!tempFile.exists()) {
                    removeDownloadProgress(model.name)
                    showSnackbar("${model.name}: Downloaded file not found")
                    return@withContext
                }
                
                // Copy to SAF folder
                val storageUri = repository.getStorageUri()
                if (storageUri == null) {
                    removeDownloadProgress(model.name)
                    showSnackbar("${model.name}: Storage not configured")
                    tempFile.delete()
                    return@withContext
                }
                
                val documentFile = DocumentFile.fromTreeUri(context, storageUri)
                if (documentFile == null) {
                    removeDownloadProgress(model.name)
                    showSnackbar("${model.name}: Cannot access storage folder")
                    tempFile.delete()
                    return@withContext
                }
                
                // Delete existing file if any
                documentFile.findFile(filename)?.delete()
                
                // Create new file in SAF folder
                val destFile = documentFile.createFile("application/octet-stream", filename)
                if (destFile == null) {
                    removeDownloadProgress(model.name)
                    showSnackbar("${model.name}: Cannot create file in storage")
                    tempFile.delete()
                    return@withContext
                }
                
                try {
                    context.contentResolver.openOutputStream(destFile.uri)?.use { outputStream ->
                        tempFile.inputStream().use { inputStream ->
                            inputStream.copyTo(outputStream, bufferSize = 8192)
                        }
                    }
                    // Delete temp file
                    tempFile.delete()
                    
                    // Clear progress and refresh list
                    removeDownloadProgress(model.name)
                    loadStorageInfo()
                } catch (e: Exception) {
                    removeDownloadProgress(model.name)
                    showSnackbar("${model.name}: ${e.message ?: "Failed to save file"}")
                    tempFile.delete()
                    destFile.delete()
                }
            }
        }
    }
    
    private fun getDownloadErrorMessage(cursor: android.database.Cursor): String {
        val reasonIndex = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
        if (reasonIndex == -1) return "Download failed"
        
        return when (cursor.getInt(reasonIndex)) {
            DownloadManager.ERROR_CANNOT_RESUME -> "Cannot resume download"
            DownloadManager.ERROR_DEVICE_NOT_FOUND -> "Storage device not found"
            DownloadManager.ERROR_FILE_ALREADY_EXISTS -> "File already exists"
            DownloadManager.ERROR_FILE_ERROR -> "Storage error"
            DownloadManager.ERROR_HTTP_DATA_ERROR -> "Network data error"
            DownloadManager.ERROR_INSUFFICIENT_SPACE -> "Insufficient storage space"
            DownloadManager.ERROR_TOO_MANY_REDIRECTS -> "Too many redirects"
            DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> "Server error"
            else -> "Download failed"
        }
    }
    
    private fun cleanupTempFile(model: ModelInfo) {
        val tempFile = File(context.getExternalFilesDir(null), model.filename)
        if (tempFile.exists()) {
            tempFile.delete()
        }
    }
}
