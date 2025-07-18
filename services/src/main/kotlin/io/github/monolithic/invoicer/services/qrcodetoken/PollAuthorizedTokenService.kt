package io.github.monolithic.invoicer.services.qrcodetoken

import io.github.monolithic.invoicer.foundation.log.LogLevel
import io.github.monolithic.invoicer.foundation.log.Logger
import io.github.monolithic.invoicer.models.qrcodetoken.AuthorizedQrCodeToken
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.selects.select
import io.github.monolithic.invoicer.repository.QrCodeTokenRepository

interface PollAuthorizedTokenService {
    suspend fun poll(
        contentId: String,
        interval: Duration,
    ): Response

    sealed interface Response {
        data class CloseConnection(val message: String) : Response
        data class Success(val token: AuthorizedQrCodeToken) : Response
    }
}

internal class PollAuthorizedTokenServiceImpl(
    private val qrCodeTokenRepository: QrCodeTokenRepository,
    private val getTokenService: GetQrCodeTokenByContentIdService,
    private val dispatcher: CoroutineDispatcher,
    private val logger: Logger
) : PollAuthorizedTokenService {

    override suspend fun poll(
        contentId: String,
        interval: Duration,
    ): PollAuthorizedTokenService.Response {
        return coroutineScope {
            getTokenService.find(contentId)
                ?: return@coroutineScope PollAuthorizedTokenService.Response.CloseConnection("Token not found")

            val cancellation = async(dispatcher) {
                delay(60.seconds)
                logger.log(
                    type = PollAuthorizedTokenServiceImpl::class,
                    message = "Authorized QrToken timed out. Aborting connection",
                    level = LogLevel.Debug
                )
                PollAuthorizedTokenService.Response.CloseConnection("Connection timed out")
            }

            val poll = async(dispatcher) { pollToken(contentId, interval) }

            select {
                cancellation.onAwait { it }
                poll.onAwait { it }
            }.also {
                if (cancellation.isActive) cancellation.cancel()
                if (poll.isActive) poll.cancel()
            }
        }
    }

    private suspend fun pollToken(
        contentId: String,
        interval: Duration
    ): PollAuthorizedTokenService.Response {
        val authData = qrCodeTokenRepository.getAuthorizedToken(contentId = contentId)
        if (authData != null) {
            logger.log(
                type = PollAuthorizedTokenServiceImpl::class,
                message = "Authorized QrToken found in cache, deleting it.",
                level = LogLevel.Debug
            )
            qrCodeTokenRepository.clearAuthorizedToken(contentId)
            return PollAuthorizedTokenService.Response.Success(authData)
        } else {
            logger.log(
                type = PollAuthorizedTokenServiceImpl::class,
                message = "Authorized QrToken not found in cache, waiting 1sec before another poll.",
                level = LogLevel.Debug
            )
            delay(interval)
            return pollToken(contentId, interval)
        }
    }
}
