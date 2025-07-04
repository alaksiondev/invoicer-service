package controller.features

import controller.viewmodel.payaccount.UpdatePayAccountViewModel
import controller.viewmodel.payaccount.toModel
import io.github.alaksion.invoicer.foundation.authentication.token.jwt.jwtProtected
import io.github.alaksion.invoicer.foundation.authentication.token.jwt.jwtUserId
import io.github.alaksion.invoicer.utils.uuid.parseUuid
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.delete
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import services.api.services.payaccount.DeletePayAccountService
import services.api.services.payaccount.UpdatePayAccountService

internal fun Routing.payAccountController() {
    route("/v1/company/{companyId}/pay_account") {
        jwtProtected {
            put("/{payAccountId}") {
                val body = call.receive<UpdatePayAccountViewModel>()
                val payAccountId = call.parameters["payAccountId"] ?: ""
                val companyId = call.parameters["companyId"] ?: ""
                val service by closestDI().instance<UpdatePayAccountService>()

                service.update(
                    companyId = parseUuid(companyId),
                    userId = parseUuid(jwtUserId()),
                    model = body.toModel(parseUuid(payAccountId))
                )
                call.respond(HttpStatusCode.NoContent)
            }
        }

        jwtProtected {
            delete("/{payAccountId}") {
                val payAccountId = call.parameters["payAccountId"] ?: ""
                val companyId = call.parameters["companyId"] ?: ""
                val service by closestDI().instance<DeletePayAccountService>()

                service.delete(
                    companyId = parseUuid(companyId),
                    userId = parseUuid(jwtUserId()),
                    accountId = parseUuid(payAccountId)
                )
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
