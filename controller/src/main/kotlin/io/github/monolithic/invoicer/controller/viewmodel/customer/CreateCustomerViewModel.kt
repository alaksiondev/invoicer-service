package io.github.monolithic.invoicer.controller.viewmodel.customer

import io.github.monolithic.invoicer.controller.validation.requiredString
import io.github.monolithic.invoicer.utils.uuid.parseUuid
import kotlinx.serialization.Serializable
import io.github.monolithic.invoicer.models.customer.CreateCustomerModel

@Serializable
internal data class CreateCustomerViewModel(
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
)

@Serializable
internal data class CreateCustomerResponseViewModel(
    val id: String,
)

internal fun CreateCustomerViewModel.toModel(
    companyId: String
) = CreateCustomerModel(
    name = requiredString(name, "missing required field: name"),
    email = requiredString(email, "missing required field: email"),
    phone = phone,
    companyId = parseUuid(companyId)
)
