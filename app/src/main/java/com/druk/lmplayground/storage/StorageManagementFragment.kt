package com.druk.lmplayground.storage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.druk.lmplayground.theme.PlaygroundTheme

class StorageManagementFragment : Fragment() {

    private val viewModel: StorageViewModel by viewModels()

    private val folderPickerLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            requireContext().contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            viewModel.setStorageFolder(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(inflater.context).apply {
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        setContent {
            val storageInfo by viewModel.storageInfo.observeAsState()
            val downloadedModels by viewModel.downloadedModels.observeAsState(emptyList())

            PlaygroundTheme {
                StorageManagementScreen(
                    storageInfo = storageInfo,
                    downloadedModels = downloadedModels,
                    onBackClick = { findNavController().popBackStack() },
                    onChangeFolderClick = { folderPickerLauncher.launch(null) },
                    onDeleteModel = { model -> 
                        viewModel.deleteModel(model)
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadStorageInfo()
    }
}
