package com.druk.lmplayground.models

import android.net.Uri
import com.druk.lmplayground.storage.StorageRepository

object ModelInfoProvider {

    fun buildModelList(storageRepository: StorageRepository? = null): List<ModelInfo> {
        // Get names of downloaded models from SAF storage
        val downloadedModelNames = storageRepository?.getModelFiles()?.map { it.name }?.toSet() ?: emptySet()
        
        return listOf(
            ModelInfo(
                name = "Qwen 3 0.6B",
                file = if ("Qwen3-0.6B-Q4_K_M.gguf" in downloadedModelNames) java.io.File("downloaded") else null,
                remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/Qwen3-0.6B-GGUF/resolve/main/Qwen3-0.6B-Q4_K_M.gguf"),
                inputPrefix = "",
                inputSuffix = "",
                antiPrompt = arrayOf(""),
                description = "484Mb language model with 0.6 billion parameters"
            ),
            ModelInfo(
                name = "Qwen 3 1.7B",
                file = if ("Qwen3-1.7B-Q4_K_M.gguf" in downloadedModelNames) java.io.File("downloaded") else null,
                remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/Qwen3-1.7B-GGUF/resolve/main/Qwen3-1.7B-Q4_K_M.gguf"),
                inputPrefix = "",
                inputSuffix = "",
                antiPrompt = arrayOf(""),
                description = "1.28Gb language model with 1.7 billion parameters"
            ),
            ModelInfo(
                name = "Qwen 3 4B",
                file = if ("Qwen3-4B-Q4_K_M.gguf" in downloadedModelNames) java.io.File("downloaded") else null,
                remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/Qwen3-4B-GGUF/resolve/main/Qwen3-4B-Q4_K_M.gguf"),
                inputPrefix = "",
                inputSuffix = "",
                antiPrompt = arrayOf(""),
                description = "2.5Gb language model with 4 billion parameters"
            ),
            ModelInfo(
                name = "Gemma 3 1B",
                file = if ("gemma-3-1b-it-Q4_K_M.gguf" in downloadedModelNames) java.io.File("downloaded") else null,
                remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/gemma-3-1b-it-GGUF/resolve/main/gemma-3-1b-it-Q4_K_M.gguf"),
                inputPrefix = "",
                inputSuffix = "",
                antiPrompt = arrayOf(""),
                description = "806Mb language model with 1 billion parameters"
            ),
            ModelInfo(
                name = "Gemma 3 4B",
                file = if ("gemma-3-4b-it-Q4_K_M.gguf" in downloadedModelNames) java.io.File("downloaded") else null,
                remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/gemma-3-4b-it-GGUF/resolve/main/gemma-3-4b-it-Q4_K_M.gguf"),
                inputPrefix = "",
                inputSuffix = "",
                antiPrompt = arrayOf(""),
                description = "2.49Gb language model with 4 billion parameters"
            ),
            ModelInfo(
                name = "Llama 3.2 1B",
                file = if ("Llama-3.2-1B-Instruct-Q4_K_M.gguf" in downloadedModelNames) java.io.File("downloaded") else null,
                remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/Llama-3.2-1B-Instruct-GGUF/resolve/main/Llama-3.2-1B-Instruct-Q4_K_M.gguf"),
                inputPrefix = "",
                inputSuffix = "",
                antiPrompt = arrayOf(""),
                description = "808Mb language model with 1 billion parameters"
            ),
            ModelInfo(
                name = "Llama 3.2 3B",
                file = if ("Llama-3.2-3B-Instruct-Q4_K_M.gguf" in downloadedModelNames) java.io.File("downloaded") else null,
                remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/Llama-3.2-3B-Instruct-GGUF/resolve/main/Llama-3.2-3B-Instruct-Q4_K_M.gguf"),
                inputPrefix = "",
                inputSuffix = "",
                antiPrompt = arrayOf(""),
                description = "2.02Gb language model with 3 billion parameters"
            ),
            ModelInfo(
                name = "Phi-4 mini",
                file = if ("Phi-4-mini-instruct-Q4_K_M.gguf" in downloadedModelNames) java.io.File("downloaded") else null,
                remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/Phi-4-mini-instruct-GGUF/resolve/main/Phi-4-mini-instruct-Q4_K_M.gguf"),
                inputPrefix = "",
                inputSuffix = "",
                antiPrompt = arrayOf(""),
                description = "2.49Gb language model with 3.8 billion parameters"
            ),
            ModelInfo(
                name = "DeepSeek R1 Distill 1.5B",
                file = if ("DeepSeek-R1-Distill-Qwen-1.5B-Q4_K_M.gguf" in downloadedModelNames) java.io.File("downloaded") else null,
                remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/DeepSeek-R1-Distill-Qwen-1.5B-GGUF/resolve/main/DeepSeek-R1-Distill-Qwen-1.5B-Q4_K_M.gguf"),
                inputPrefix = "",
                inputSuffix = "",
                antiPrompt = arrayOf(""),
                description = "1.12Gb language model distilled from Qwen 1.5B"
            ),
            ModelInfo(
                name = "DeepSeek R1 Distill 7B",
                file = if ("DeepSeek-R1-Distill-Qwen-7B-Q4_K_M.gguf" in downloadedModelNames) java.io.File("downloaded") else null,
                remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/DeepSeek-R1-Distill-Qwen-7B-GGUF/resolve/main/DeepSeek-R1-Distill-Qwen-7B-Q4_K_M.gguf"),
                inputPrefix = "",
                inputSuffix = "",
                antiPrompt = arrayOf(""),
                description = "4.68Gb language model distilled from Qwen 7B"
            ),
            // Obsolete models - only shown if already downloaded
            ModelInfo(
                name = "Qwen2.5 0.5B",
                file = if ("Qwen2.5-0.5B-Instruct-Q4_K_M.gguf" in downloadedModelNames) java.io.File("downloaded") else null,
                remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/Qwen2.5-0.5B-Instruct-GGUF/resolve/main/Qwen2.5-0.5B-Instruct-Q4_K_M.gguf?download=true"),
                inputPrefix = "<|im_start|>user\n",
                inputSuffix = "<|im_end|>\n<|im_start|>assistant\n",
                antiPrompt = arrayOf("<|im_end|>"),
                obsolete = true,
                description = "0.5 billion parameters language model"
            ),
            ModelInfo(
                name = "Qwen2.5 1.5B",
                file = if ("Qwen2.5-1.5B-Instruct-Q4_K_M.gguf" in downloadedModelNames) java.io.File("downloaded") else null,
                remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/Qwen2.5-1.5B-Instruct-GGUF/resolve/main/Qwen2.5-1.5B-Instruct-Q4_K_M.gguf?download=true"),
                inputPrefix = "<|im_start|>user\n",
                inputSuffix = "<|im_end|>\n<|im_start|>assistant\n",
                antiPrompt = arrayOf("<|im_end|>"),
                obsolete = true,
                description = "1.5 billion parameters language model"
            ),
            ModelInfo(
                name = "Llama3.2 1B",
                file = if ("Llama-3.2-1B-Instruct-Q4_K_M.gguf" in downloadedModelNames) java.io.File("downloaded") else null,
                remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/Llama-3.2-1B-Instruct-GGUF/resolve/main/Llama-3.2-1B-Instruct-Q4_K_M.gguf?download=true"),
                inputPrefix = "<|start_header_id|>user<|end_header_id|>\n\n",
                inputSuffix = "<|eot_id|><|start_header_id|>assistant<|end_header_id|>\n\n",
                antiPrompt = arrayOf("<|eot_id|>"),
                obsolete = true,
                description = "1 billions parameters language model"
            ),
            ModelInfo(
                name = "Llama3.2 3B",
                file = if ("Llama-3.2-3B-Instruct-Q4_K_M.gguf" in downloadedModelNames) java.io.File("downloaded") else null,
                remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/Llama-3.2-3B-Instruct-GGUF/resolve/main/Llama-3.2-3B-Instruct-Q4_K_M.gguf?download=true"),
                inputPrefix = "<|start_header_id|>user<|end_header_id|>\n\n",
                inputSuffix = "<|eot_id|><|start_header_id|>assistant<|end_header_id|>\n\n",
                antiPrompt = arrayOf("<|eot_id|>"),
                obsolete = true,
                description = "3 billions parameters language model"
            ),
            ModelInfo(
                name = "Phi3.5 mini",
                file = if ("Phi-3.5-mini-instruct-Q4_K_M.gguf" in downloadedModelNames) java.io.File("downloaded") else null,
                remoteUri = Uri.parse("https://huggingface.co/bartowski/Phi-3.5-mini-instruct-GGUF/resolve/main/Phi-3.5-mini-instruct-Q4_K_M.gguf?download=true"),
                inputPrefix = "<|user|>\n",
                inputSuffix = "<|end|>\n<|assistant|>\n",
                antiPrompt = arrayOf("<|end|>", "<|assistant|>"),
                obsolete = true,
                description = "3.8 billions parameters language model"
            ),
            ModelInfo(
                name = "Mistral 7B",
                file = if ("Mistral-7B-Instruct-v0.3-Q4_K_M.gguf" in downloadedModelNames) java.io.File("downloaded") else null,
                remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/Mistral-7B-Instruct-v0.3-GGUF/resolve/main/Mistral-7B-Instruct-v0.3-Q4_K_M.gguf?download=true"),
                inputPrefix = "[INST]",
                inputSuffix = "[/INST]",
                obsolete = true,
                description = "7.3 billions parameter language model"
            ),
            ModelInfo(
                name = "Llama3.1 8B",
                file = if ("Meta-Llama-3.1-8B-Instruct-Q4_K_M.gguf" in downloadedModelNames) java.io.File("downloaded") else null,
                remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/Meta-Llama-3.1-8B-Instruct-GGUF/resolve/main/Meta-Llama-3.1-8B-Instruct-Q4_K_M.gguf?download=true"),
                inputPrefix = "<|start_header_id|>user<|end_header_id|>\n\n",
                inputSuffix = "<|eot_id|><|start_header_id|>assistant<|end_header_id|>\n\n",
                antiPrompt = arrayOf("<|eot_id|>"),
                obsolete = true,
                description = "8 billions parameters language model"
            ),
            ModelInfo(
                name = "Gemma2 9B",
                file = if ("gemma-2-9b-it-Q4_K_M.gguf" in downloadedModelNames) java.io.File("downloaded") else null,
                remoteUri = Uri.parse("https://huggingface.co/bartowski/gemma-2-9b-it-GGUF/resolve/main/gemma-2-9b-it-Q4_K_M.gguf"),
                inputPrefix = "<start_of_turn>user\n",
                inputSuffix = "<end_of_turn>\n<start_of_turn>model\n",
                antiPrompt = arrayOf("<start_of_turn>user", "<start_of_turn>model", "<end_of_turn>"),
                obsolete = true,
                description = "8.5 billions parameters language model"
            )
        ).filter {
            // Hide obsolete models that aren't downloaded
            !(it.obsolete && it.file == null)
        }
    }
}
