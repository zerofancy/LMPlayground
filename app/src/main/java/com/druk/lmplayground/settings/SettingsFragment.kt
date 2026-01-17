package com.druk.lmplayground.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.druk.lmplayground.BuildConfig
import com.druk.lmplayground.R
import com.druk.lmplayground.theme.PlaygroundTheme

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(inflater.context).apply {
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        setContent {
            PlaygroundTheme {
                SettingsScreen(
                    onBackClick = { findNavController().popBackStack() },
                    onModelsClick = { 
                        findNavController().navigate(R.id.action_settings_to_models) 
                    },
                    appVersion = BuildConfig.VERSION_NAME
                )
            }
        }
    }
}
