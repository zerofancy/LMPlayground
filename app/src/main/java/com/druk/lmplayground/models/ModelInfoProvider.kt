package com.druk.lmplayground.models

import android.net.Uri

object ModelInfoProvider {
    
    /**
     * Static list of all available models
     */
    val allModels: List<ModelInfo> = listOf(
        ModelInfo(
            name = "Qwen 3 0.6B",
            filename = "Qwen3-0.6B-Q4_K_M.gguf",
            remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/Qwen3-0.6B-GGUF/resolve/main/Qwen3-0.6B-Q4_K_M.gguf"),
            description = "484Mb language model with 0.6 billion parameters"
        ),
        ModelInfo(
            name = "Qwen 3 1.7B",
            filename = "Qwen3-1.7B-Q4_K_M.gguf",
            remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/Qwen3-1.7B-GGUF/resolve/main/Qwen3-1.7B-Q4_K_M.gguf"),
            description = "1.28Gb language model with 1.7 billion parameters"
        ),
        ModelInfo(
            name = "Qwen 3 4B",
            filename = "Qwen3-4B-Q4_K_M.gguf",
            remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/Qwen3-4B-GGUF/resolve/main/Qwen3-4B-Q4_K_M.gguf"),
            description = "2.5Gb language model with 4 billion parameters"
        ),
        ModelInfo(
            name = "Gemma 3 1B",
            filename = "gemma-3-1b-it-Q4_K_M.gguf",
            remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/gemma-3-1b-it-GGUF/resolve/main/gemma-3-1b-it-Q4_K_M.gguf"),
            description = "806Mb language model with 1 billion parameters"
        ),
        ModelInfo(
            name = "Gemma 3 4B",
            filename = "gemma-3-4b-it-Q4_K_M.gguf",
            remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/gemma-3-4b-it-GGUF/resolve/main/gemma-3-4b-it-Q4_K_M.gguf"),
            description = "2.49Gb language model with 4 billion parameters"
        ),
        ModelInfo(
            name = "Llama 3.2 1B",
            filename = "Llama-3.2-1B-Instruct-Q4_K_M.gguf",
            remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/Llama-3.2-1B-Instruct-GGUF/resolve/main/Llama-3.2-1B-Instruct-Q4_K_M.gguf"),
            description = "808Mb language model with 1 billion parameters"
        ),
        ModelInfo(
            name = "Llama 3.2 3B",
            filename = "Llama-3.2-3B-Instruct-Q4_K_M.gguf",
            remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/Llama-3.2-3B-Instruct-GGUF/resolve/main/Llama-3.2-3B-Instruct-Q4_K_M.gguf"),
            description = "2.02Gb language model with 3 billion parameters"
        ),
        ModelInfo(
            name = "Phi-4 mini",
            filename = "Phi-4-mini-instruct-Q4_K_M.gguf",
            remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/Phi-4-mini-instruct-GGUF/resolve/main/Phi-4-mini-instruct-Q4_K_M.gguf"),
            description = "2.49Gb language model with 3.8 billion parameters"
        ),
        ModelInfo(
            name = "DeepSeek R1 Distill 1.5B",
            filename = "DeepSeek-R1-Distill-Qwen-1.5B-Q4_K_M.gguf",
            remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/DeepSeek-R1-Distill-Qwen-1.5B-GGUF/resolve/main/DeepSeek-R1-Distill-Qwen-1.5B-Q4_K_M.gguf"),
            description = "1.12Gb language model distilled from Qwen 1.5B"
        ),
        ModelInfo(
            name = "DeepSeek R1 Distill 7B",
            filename = "DeepSeek-R1-Distill-Qwen-7B-Q4_K_M.gguf",
            remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/DeepSeek-R1-Distill-Qwen-7B-GGUF/resolve/main/DeepSeek-R1-Distill-Qwen-7B-Q4_K_M.gguf"),
            description = "4.68Gb language model distilled from Qwen 7B"
        ),
        ModelInfo(
            name = "Qwen2.5 0.5B",
            filename = "Qwen2.5-0.5B-Instruct-Q4_K_M.gguf",
            remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/Qwen2.5-0.5B-Instruct-GGUF/resolve/main/Qwen2.5-0.5B-Instruct-Q4_K_M.gguf"),
            inputPrefix = "<|im_start|>user\n",
            inputSuffix = "<|im_end|>\n<|im_start|>assistant\n",
            antiPrompt = arrayOf("<|im_end|>"),
            description = "398Mb language model with 0.5 billion parameters"
        ),
        ModelInfo(
            name = "Qwen2.5 1.5B",
            filename = "Qwen2.5-1.5B-Instruct-Q4_K_M.gguf",
            remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/Qwen2.5-1.5B-Instruct-GGUF/resolve/main/Qwen2.5-1.5B-Instruct-Q4_K_M.gguf"),
            inputPrefix = "<|im_start|>user\n",
            inputSuffix = "<|im_end|>\n<|im_start|>assistant\n",
            antiPrompt = arrayOf("<|im_end|>"),
            description = "986Mb language model with 1.5 billion parameters"
        ),
        ModelInfo(
            name = "Phi3.5 mini",
            filename = "Phi-3.5-mini-instruct-Q4_K_M.gguf",
            remoteUri = Uri.parse("https://huggingface.co/bartowski/Phi-3.5-mini-instruct-GGUF/resolve/main/Phi-3.5-mini-instruct-Q4_K_M.gguf"),
            inputPrefix = "<|user|>\n",
            inputSuffix = "<|end|>\n<|assistant|>\n",
            antiPrompt = arrayOf("<|end|>", "<|assistant|>"),
            description = "2.2Gb language model with 3.8 billion parameters"
        ),
        ModelInfo(
            name = "Mistral 7B",
            filename = "Mistral-7B-Instruct-v0.3-Q4_K_M.gguf",
            remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/Mistral-7B-Instruct-v0.3-GGUF/resolve/main/Mistral-7B-Instruct-v0.3-Q4_K_M.gguf"),
            inputPrefix = "[INST]",
            inputSuffix = "[/INST]",
            description = "4.37Gb language model with 7.3 billion parameters"
        ),
        ModelInfo(
            name = "Llama 3.1 8B",
            filename = "Meta-Llama-3.1-8B-Instruct-Q4_K_M.gguf",
            remoteUri = Uri.parse("https://huggingface.co/lmstudio-community/Meta-Llama-3.1-8B-Instruct-GGUF/resolve/main/Meta-Llama-3.1-8B-Instruct-Q4_K_M.gguf"),
            inputPrefix = "<|start_header_id|>user<|end_header_id|>\n\n",
            inputSuffix = "<|eot_id|><|start_header_id|>assistant<|end_header_id|>\n\n",
            antiPrompt = arrayOf("<|eot_id|>"),
            description = "4.92Gb language model with 8 billion parameters"
        ),
        ModelInfo(
            name = "Gemma2 9B",
            filename = "gemma-2-9b-it-Q4_K_M.gguf",
            remoteUri = Uri.parse("https://huggingface.co/bartowski/gemma-2-9b-it-GGUF/resolve/main/gemma-2-9b-it-Q4_K_M.gguf"),
            inputPrefix = "<start_of_turn>user\n",
            inputSuffix = "<end_of_turn>\n<start_of_turn>model\n",
            antiPrompt = arrayOf("<start_of_turn>user", "<start_of_turn>model", "<end_of_turn>"),
            description = "5.44Gb language model with 9 billion parameters"
        )
    )
    
    /**
     * Get all known model filenames
     */
    val knownFilenames: Set<String> = allModels.map { it.filename }.toSet()
    
    /**
     * Get model by filename
     */
    fun getByFilename(filename: String): ModelInfo? = allModels.find { it.filename == filename }
    
    /**
     * Get display name for a filename
     */
    fun getDisplayName(filename: String): String = getByFilename(filename)?.name ?: filename.removeSuffix(".gguf")
    
    /**
     * Get models with their download status.
     */
    fun getModelsWithStatus(downloadedFilenames: Set<String>): List<ModelWithStatus> {
        return allModels.map { model ->
            ModelWithStatus(
                model = model,
                isDownloaded = model.filename in downloadedFilenames
            )
        }
    }
}
