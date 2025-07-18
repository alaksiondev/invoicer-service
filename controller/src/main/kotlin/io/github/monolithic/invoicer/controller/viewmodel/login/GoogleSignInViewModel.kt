package io.github.monolithic.invoicer.controller.viewmodel.login

import kotlinx.serialization.Serializable

@Serializable
internal data class GoogleSignInViewModel(
    val token: String? = null
)
