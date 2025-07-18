package io.github.monolithic.invoicer.models.user

data class CreateUserModel(
    val email: String,
    val confirmEmail: String,
    val password: String,
    val identityProviderUuid: String? = null
)
