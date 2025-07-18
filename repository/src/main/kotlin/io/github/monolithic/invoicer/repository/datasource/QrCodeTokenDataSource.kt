package io.github.monolithic.invoicer.repository.datasource

import io.github.monolithic.invoicer.repository.entities.QrCodeTokenEntity
import io.github.monolithic.invoicer.repository.entities.QrCodeTokenStatus
import io.github.monolithic.invoicer.repository.entities.QrCodeTokensTable
import io.github.monolithic.invoicer.repository.mapper.toModel
import java.util.*
import kotlin.time.Duration.Companion.seconds
import kotlinx.datetime.Clock
import io.github.monolithic.invoicer.models.qrcodetoken.QrCodeTokenModel
import org.jetbrains.exposed.sql.insertReturning
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.updateReturning

interface QrCodeTokenDataSource {
    suspend fun createQrCodeToken(
        ipAddress: String,
        agent: String,
        base64Content: String,
        content: String,
    ): QrCodeTokenModel

    suspend fun getQrCodeTokenByUUID(
        tokenId: UUID
    ): QrCodeTokenModel?

    suspend fun consumeQrCodeToken(
        tokenId: UUID
    ): QrCodeTokenModel?

    suspend fun expireQrCodeToken(
        tokenId: UUID
    )

    suspend fun getQrCodeByContentId(
        contentId: String,
    ): QrCodeTokenModel?
}

internal class QrCodeTokenDataSourceImpl(
    private val clock: Clock
) : QrCodeTokenDataSource {

    override suspend fun createQrCodeToken(
        ipAddress: String,
        agent: String,
        base64Content: String,
        content: String,
    ): QrCodeTokenModel {
        return newSuspendedTransaction {
            QrCodeTokensTable.insertReturning {
                it[QrCodeTokensTable.ipAddress] = ipAddress
                it[QrCodeTokensTable.agent] = agent
                it[QrCodeTokensTable.base64Content] = base64Content
                it[QrCodeTokensTable.content] = content
                it[status] = QrCodeTokenStatus.GENERATED.value
                it[createdAt] = clock.now()
                it[updatedAt] = clock.now()
                it[expiresAt] = clock.now().plus(60.seconds)
            }.map {
                QrCodeTokenEntity.Companion.wrapRow(it)
            }.first().toModel()
        }
    }

    override suspend fun getQrCodeTokenByUUID(tokenId: UUID): QrCodeTokenModel? {
        return newSuspendedTransaction {
            QrCodeTokenEntity.Companion.find { QrCodeTokensTable.id eq tokenId }
                .firstOrNull()
                ?.toModel()
        }
    }

    override suspend fun consumeQrCodeToken(tokenId: UUID): QrCodeTokenModel? {
        return newSuspendedTransaction {
            QrCodeTokensTable.updateReturning(
                where = {
                    QrCodeTokensTable.id eq tokenId
                }
            ) {
                it[status] = QrCodeTokenStatus.CONSUMED.value
                it[updatedAt] = clock.now()
            }.map {
                QrCodeTokenEntity.Companion.wrapRow(it).toModel()
            }.first()
        }
    }

    override suspend fun expireQrCodeToken(tokenId: UUID) {
        return newSuspendedTransaction {
            QrCodeTokensTable.updateReturning(
                where = {
                    QrCodeTokensTable.id eq tokenId
                }
            ) {
                it[status] = QrCodeTokenStatus.EXPIRED.value
                it[updatedAt] = clock.now()
            }
        }
    }

    override suspend fun getQrCodeByContentId(contentId: String): QrCodeTokenModel? {
        return newSuspendedTransaction {
            QrCodeTokenEntity.Companion.find { QrCodeTokensTable.content eq contentId }
                .firstOrNull()
                ?.toModel()
        }
    }
}
