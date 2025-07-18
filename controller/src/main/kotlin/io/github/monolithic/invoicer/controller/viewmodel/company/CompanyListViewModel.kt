package io.github.monolithic.invoicer.controller.viewmodel.company

import kotlinx.serialization.Serializable
import io.github.monolithic.invoicer.models.company.CompanyList

@Serializable
internal data class CompanyListViewModel(
    val companies: List<GetCompanyItemViewModel>,
    val total: Long,
    val nextPageIndex: Long?
)

@Serializable
internal data class GetCompanyItemViewModel(
    val document: String,
    val name: String,
    val id: String,
)

internal fun CompanyList.toViewModel(): CompanyListViewModel {
    return CompanyListViewModel(
        companies = items.map {
            GetCompanyItemViewModel(
                document = it.document,
                name = it.name,
                id = it.id.toString()
            )
        },
        total = totalCount,
        nextPageIndex = nextPage
    )
}
