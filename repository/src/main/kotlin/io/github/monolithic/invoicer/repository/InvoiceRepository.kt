package io.github.monolithic.invoicer.repository

import io.github.monolithic.invoicer.foundation.cache.CacheHandler
import io.github.monolithic.invoicer.repository.datasource.InvoiceDataSource
import java.util.*
import io.github.monolithic.invoicer.models.invoice.CreateInvoiceModel
import io.github.monolithic.invoicer.models.invoice.GetInvoicesFilterModel
import io.github.monolithic.invoicer.models.invoice.InvoiceListModel
import io.github.monolithic.invoicer.models.invoice.InvoiceModel

interface InvoiceRepository {
    suspend fun create(
        data: CreateInvoiceModel,
        userId: UUID,
    ): String

    suspend fun getById(
        id: UUID
    ): InvoiceModel?

    suspend fun getByInvoiceNumber(
        invoiceNumber: String
    ): InvoiceModel?

    suspend fun getAll(
        filters: GetInvoicesFilterModel,
        page: Long,
        limit: Int,
        companyId: UUID,
    ): InvoiceListModel

    suspend fun delete(
        id: UUID
    )
}


internal class InvoiceRepositoryImpl(
    private val invoiceDataSource: InvoiceDataSource,
    private val cacheHandler: CacheHandler
) : InvoiceRepository {

    override suspend fun create(
        data: CreateInvoiceModel,
        userId: UUID,
    ): String {
        return invoiceDataSource.create(
            data = data,
            userId = userId
        )
    }

    override suspend fun getById(id: UUID): InvoiceModel? {
        val cached = cacheHandler.get(
            key = id.toString(),
            serializer = InvoiceModel.serializer()
        )

        if (cached != null) return cached

        return invoiceDataSource.getById(id = id)?.also {
            cacheHandler.set(
                key = it.id.toString(),
                serializer = InvoiceModel.serializer(),
                value = it,
                ttlSeconds = CACHE_TTL_SECONDS
            )
        }
    }

    override suspend fun getByInvoiceNumber(invoiceNumber: String): InvoiceModel? {
        return invoiceDataSource.getByInvoiceNumber(
            invoiceNumber = invoiceNumber
        )
    }

    override suspend fun getAll(
        filters: GetInvoicesFilterModel,
        page: Long,
        limit: Int,
        companyId: UUID,
    ): InvoiceListModel {
        return invoiceDataSource.getAll(
            filters = filters,
            page = page,
            limit = limit,
            companyId = companyId
        )
    }

    override suspend fun delete(
        id: UUID
    ) {
        invoiceDataSource.delete(id = id).also {
            cacheHandler.delete(
                key = id.toString(),
            )
        }
    }

    companion object {
        const val CACHE_TTL_SECONDS = 600L
    }
}
