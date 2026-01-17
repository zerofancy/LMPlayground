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

class ModelsFragment : Fragment() {

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
            viewModel.requestStorageFolderChange(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadStorageInfo()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(inflater.context).apply {
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        setContent {
            val storageInfo by viewModel.storageInfo.observeAsState()
            val allModels by viewModel.allModels.observeAsState(emptyList())
            val downloadingProgress by viewModel.downloadingModels.observeAsState(emptyMap())
            val snackbarMessage by viewModel.snackbarMessage.observeAsState()
            val pendingMigration by viewModel.pendingMigration.observeAsState()
            val migrationProgress by viewModel.migrationProgress.observeAsState()

            PlaygroundTheme {
                ModelsScreen(
                    storageInfo = storageInfo,
                    allModels = allModels,
                    downloadingModels = downloadingProgress,
                    snackbarMessage = snackbarMessage,
                    pendingMigration = pendingMigration,
                    migrationProgress = migrationProgress,
                    onBackClick = { findNavController().popBackStack() },
                    onChangeFolderClick = { folderPickerLauncher.launch(null) },
                    onDeleteModel = { model -> 
                        viewModel.deleteModel(model)
                    },
                    onDownloadModel = { model ->
                        viewModel.downloadModel(model)
                    },
                    onCancelDownload = { model ->
                        viewModel.cancelDownload(model)
                    },
                    onSnackbarDismiss = {
                        viewModel.clearSnackbar()
                    },
                    onConfirmMigration = {
                        viewModel.confirmMigration()
                    },
                    onSkipMigration = {
                        viewModel.skipMigration()
                    },
                    onCancelMigration = {
                        viewModel.cancelMigration()
                    }
                )
            }
        }
    }
}
