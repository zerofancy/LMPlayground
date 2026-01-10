@file:OptIn(ExperimentalMaterial3Api::class)

package com.druk.lmplayground.storage

import android.text.format.Formatter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.druk.lmplayground.R
import com.druk.lmplayground.theme.PlaygroundTheme

@Composable
fun StorageManagementScreen(
    storageInfo: StorageInfo?,
    downloadedModels: List<ModelFile>,
    onBackClick: () -> Unit,
    onChangeFolderClick: () -> Unit,
    onDeleteModel: (ModelFile) -> Unit
) {
    var modelToDelete by remember { mutableStateOf<ModelFile?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.storage_management)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Storage info card
            item {
                if (storageInfo != null) {
                    StorageInfoCard(
                        storageInfo = storageInfo,
                        onChangeFolderClick = onChangeFolderClick,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Section header
            item {
                Text(
                    text = stringResource(R.string.downloaded_models),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (downloadedModels.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.no_downloaded_models),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(downloadedModels) { model ->
                    ModelFileItem(
                        model = model,
                        onDeleteClick = { modelToDelete = model }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }

    // Delete confirmation dialog
    modelToDelete?.let { model ->
        AlertDialog(
            onDismissRequest = { modelToDelete = null },
            title = { Text(stringResource(R.string.delete_model_confirm, model.displayName)) },
            text = { Text(stringResource(R.string.delete_model_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteModel(model)
                        modelToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.delete_model))
                }
            },
            dismissButton = {
                TextButton(onClick = { modelToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun StorageInfoCard(
    storageInfo: StorageInfo,
    onChangeFolderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Folder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (storageInfo.isCustomFolder) 
                            stringResource(R.string.custom_folder) 
                        else 
                            stringResource(R.string.downloads_folder),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = storageInfo.path,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Storage usage
            val usedFormatted = Formatter.formatFileSize(context, storageInfo.usedBytes)
            Text(
                text = stringResource(R.string.storage_used_models, usedFormatted),
                style = MaterialTheme.typography.bodyMedium
            )

            if (storageInfo.totalBytes > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                val progressValue = storageInfo.usedBytes.toFloat() / storageInfo.totalBytes.toFloat()
                LinearProgressIndicator(
                    progress = progressValue.coerceIn(0f, 1f),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(4.dp))
                val availableFormatted = Formatter.formatFileSize(context, storageInfo.availableBytes)
                Text(
                    text = stringResource(R.string.storage_available, availableFormatted),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onChangeFolderClick) {
                    Text(stringResource(R.string.change_folder))
                }
            }
        }
    }
}

@Composable
private fun ModelFileItem(
    model: ModelFile,
    onDeleteClick: () -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = model.displayName,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = Formatter.formatFileSize(context, model.sizeBytes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = onDeleteClick) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = stringResource(R.string.delete_model),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview
@Composable
private fun StorageManagementScreenPreview() {
    PlaygroundTheme {
        StorageManagementScreen(
            storageInfo = StorageInfo(
                path = "/storage/emulated/0/Download",
                usedBytes = 5_000_000_000,
                totalBytes = 64_000_000_000,
                availableBytes = 30_000_000_000,
                isCustomFolder = false
            ),
            downloadedModels = listOf(
                ModelFile(
                    name = "Qwen3-0.6B-Q4_K_M.gguf",
                    displayName = "Qwen3 0.6B",
                    sizeBytes = 484_000_000,
                    uri = android.net.Uri.EMPTY
                ),
                ModelFile(
                    name = "Llama-3.2-1B-Instruct-Q4_K_M.gguf",
                    displayName = "Llama 3.2 1B Instruct",
                    sizeBytes = 808_000_000,
                    uri = android.net.Uri.EMPTY
                )
            ),
            onBackClick = {},
            onChangeFolderClick = {},
            onDeleteModel = {}
        )
    }
}
