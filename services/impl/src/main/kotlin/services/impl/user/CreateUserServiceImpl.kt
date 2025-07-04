package services.impl.user

import utils.exceptions.http.HttpCode
import io.github.alaksion.invoicer.utils.validation.EmailValidator
import models.user.CreateUserModel
import repository.UserRepository
import services.api.services.user.CreateUserService
import services.api.services.user.GetUserByEmailService
import utils.exceptions.http.badRequestError
import utils.exceptions.http.httpError
import foundation.password.PasswordEncryption
import foundation.password.PasswordStrength
import foundation.password.PasswordValidator

internal class CreateUserServiceImpl(
    private val getUserByEmailService: GetUserByEmailService,
    private val passwordValidator: PasswordValidator,
    private val repository: UserRepository,
    private val passwordEncryption: PasswordEncryption,
    private val emailValidator: EmailValidator
) : CreateUserService {

    override suspend fun create(userModel: CreateUserModel): String {

        if (userModel.email != userModel.confirmEmail)
            badRequestError(message = "E-mails must mach")

        if (emailValidator.validate(userModel.email).not())
            badRequestError(message = "invalid email format: ${userModel.email}")

        if (getUserByEmailService.get(userModel.email) != null)
            httpError(message = "E-mail ${userModel.email} already in use", code = HttpCode.Conflict)

        val passwordLevel = passwordValidator.validate(userModel.password)

        if (passwordLevel is PasswordStrength.WEAK) {
            badRequestError(message = passwordLevel.reason)
        }

        val encryptedPassword = passwordEncryption.encryptPassword(userModel.password)

        return repository.createUser(userModel.copy(password = encryptedPassword))
    }

}
