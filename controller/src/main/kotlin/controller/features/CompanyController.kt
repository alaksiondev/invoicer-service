package controller.features

import controller.Constants
import controller.viewmodel.company.CreateCompanyResponseViewModel
import controller.viewmodel.company.CreateCompanyViewModel
import controller.viewmodel.company.toModel
import controller.viewmodel.company.toViewModel
import io.github.alaksion.invoicer.foundation.authentication.token.jwt.jwtProtected
import io.github.alaksion.invoicer.foundation.authentication.token.jwt.jwtUserId
import io.github.alaksion.invoicer.utils.uuid.parseUuid
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import services.api.services.company.CreateCompanyService
import services.api.services.company.GetCompaniesService
import services.api.services.company.GetUserCompanyDetailsService

internal fun Routing.companyController() {
    route("/v1/company") {
        jwtProtected {
            post {
                val body = call.receive<CreateCompanyViewModel>()
                val service by closestDI().instance<CreateCompanyService>()

                call.respond(
                    status = HttpStatusCode.Created,
                    message =
                        CreateCompanyResponseViewModel(
                            id = service.createCompany(
                                data = body.toModel(),
                                userId = parseUuid(jwtUserId())
                            )
                        ),
                )
            }
        }

        jwtProtected {
            get("/{companyId}") {
                val companyId = call.parameters["companyId"]!!
                val service by closestDI().instance<GetUserCompanyDetailsService>()

                call.respond(
                    message = service.get(
                        userId = parseUuid(jwtUserId()),
                        companyId = parseUuid(companyId)
                    ).toViewModel(),
                    status = HttpStatusCode.Created
                )
            }
        }

        jwtProtected {
            get {
                val page = call.request.queryParameters["page"]?.toLongOrNull() ?: Constants.DEFAULT_PAGE
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_LIMIT
                val service by closestDI().instance<GetCompaniesService>()

                call.respond(
                    status = HttpStatusCode.OK,
                    message = service.get(
                        userId = parseUuid(jwtUserId()),
                        page = page.toInt(),
                        limit = limit
                    ).toViewModel()
                )
            }
        }
    }
}
