package services.impl.user

import kotlinx.coroutines.test.runTest
import models.fixtures.userModelFixture
import repository.fakes.FakeUserRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetUserByEmailServiceImplTest {
    private lateinit var service: GetUserByEmailServiceImpl
    private lateinit var repository: FakeUserRepository

    @BeforeTest
    fun setUp() {
        repository = FakeUserRepository()
        service = GetUserByEmailServiceImpl(repository)
    }

    @Test
    fun `should return user from repository`() = runTest {
        val user = userModelFixture.copy(
            email = "email@gmail.com"
        )

        repository.getByEmailResponse = { user }

        val result = service.get("email@gmail.com")

        assertEquals(
            actual = result,
            expected = user
        )
    }
}
