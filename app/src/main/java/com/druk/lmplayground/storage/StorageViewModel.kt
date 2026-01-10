package com.druk.lmplayground.storage

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StorageViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = StoragePreferences(application)
    private val repository = StorageRepository(application, prefs)

    private val _storageInfo = MutableLiveData<StorageInfo>()
    val storageInfo: LiveData<StorageInfo> = _storageInfo

    private val _downloadedModels = MutableLiveData<List<ModelFile>>()
    val downloadedModels: LiveData<List<ModelFile>> = _downloadedModels

    private val _isStorageConfigured = MutableLiveData<Boolean>()
    val isStorageConfigured: LiveData<Boolean> = _isStorageConfigured

    fun checkStorageConfigured() {
        _isStorageConfigured.postValue(repository.isStorageConfigured())
    }

    fun loadStorageInfo() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _storageInfo.postValue(repository.getStorageInfo())
                _downloadedModels.postValue(repository.getModelFiles())
            }
        }
    }

    fun deleteModel(model: ModelFile) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteModel(model.name)
                loadStorageInfo()
            }
        }
    }

    fun setStorageFolder(uri: Uri) {
        repository.setStorageFolder(uri)
        _isStorageConfigured.postValue(true)
        loadStorageInfo()
    }

    fun hasValidPermission(): Boolean {
        return repository.hasValidPermission()
    }

    fun getRepository(): StorageRepository = repository
}
