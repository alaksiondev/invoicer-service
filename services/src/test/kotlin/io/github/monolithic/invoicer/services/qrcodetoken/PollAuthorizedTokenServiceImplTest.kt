package io.github.monolithic.invoicer.services.qrcodetoken

import io.github.monolithic.invoicer.foundation.log.fakes.FakeLogger
import io.github.monolithic.invoicer.services.fakes.qrcode.FakeGetQrCodeTokenByContentIdService
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import io.github.monolithic.invoicer.models.fixtures.qrCodeTokenModelFixture
import io.github.monolithic.invoicer.models.qrcodetoken.AuthorizedQrCodeToken
import io.github.monolithic.invoicer.repository.fakes.FakeQrCodeTokenRepository

class PollAuthorizedTokenServiceImplTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var service: PollAuthorizedTokenServiceImpl
    private lateinit var qrCodeTokenRepository: FakeQrCodeTokenRepository
    private lateinit var getTokenService: FakeGetQrCodeTokenByContentIdService
    private lateinit var logger: FakeLogger

    @BeforeTest
    fun setUp() {
        qrCodeTokenRepository = FakeQrCodeTokenRepository()
        getTokenService = FakeGetQrCodeTokenByContentIdService()
        logger = FakeLogger()

        service = PollAuthorizedTokenServiceImpl(
            qrCodeTokenRepository = qrCodeTokenRepository,
            getTokenService = getTokenService,
            dispatcher = dispatcher,
            logger = logger
        )
    }


    @Test
    fun `should return cancel action if qrToken is not found`() = runTest(dispatcher) {
        getTokenService.response = null

        val result = service.poll(
            contentId = "123",
            interval = 1.seconds
        )

        assertIs<PollAuthorizedTokenService.Response.CloseConnection>(result)
    }

    @Test
    fun `should return cancel action if after 60 seconds qrToken is not found`() = runTest(dispatcher) {
        getTokenService.response = qrCodeTokenModelFixture
        qrCodeTokenRepository.getAuthorizedQrCodeToken = { null }

        val result = service.poll(
            contentId = "123",
            interval = 1.seconds
        )

        assertIs<PollAuthorizedTokenService.Response.CloseConnection>(result)
    }

    @Test
    fun `should return success response if auth token is found`() = runTest(dispatcher) {
        getTokenService.response = qrCodeTokenModelFixture
        qrCodeTokenRepository.getAuthorizedQrCodeToken = {
            AuthorizedQrCodeToken(
                rawContent = "content",
                refreshToken = "refresh",
                accessToken = "refresh"
            )
        }

        val result = service.poll(
            contentId = "123",
            interval = 1.seconds
        )

        assertIs<PollAuthorizedTokenService.Response.Success>(result)
    }
}
