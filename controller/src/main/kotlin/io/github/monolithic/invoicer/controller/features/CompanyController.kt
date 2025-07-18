package io.github.monolithic.invoicer.controller.features

import io.github.monolithic.invoicer.controller.Constants
import io.github.monolithic.invoicer.controller.viewmodel.company.CreateCompanyResponseViewModel
import io.github.monolithic.invoicer.controller.viewmodel.company.CreateCompanyViewModel
import io.github.monolithic.invoicer.controller.viewmodel.company.UpdateCompanyAddressViewModel
import io.github.monolithic.invoicer.controller.viewmodel.company.toModel
import io.github.monolithic.invoicer.controller.viewmodel.company.toViewModel
import io.github.monolithic.invoicer.foundation.authentication.token.jwt.jwtProtected
import io.github.monolithic.invoicer.foundation.authentication.token.jwt.jwtUserId
import io.github.monolithic.invoicer.services.company.CreateCompanyService
import io.github.monolithic.invoicer.services.company.GetCompaniesService
import io.github.monolithic.invoicer.services.company.GetUserCompanyDetailsService
import io.github.monolithic.invoicer.services.company.UpdateCompanyAddressService
import io.github.monolithic.invoicer.utils.uuid.parseUuid
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

internal fun Routing.companyController() {
    route("/v1/company") {
        createCompany()
        updateCompanyAddress()
        companyDetails()
        companyList()
    }
}

private fun Route.createCompany() = jwtProtected {
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

private fun Route.updateCompanyAddress() = jwtProtected {
    patch("/{companyId}/address") {
        val companyId = call.parameters["companyId"]!!
        val body = call.receive<UpdateCompanyAddressViewModel>()
        val service by closestDI().instance<UpdateCompanyAddressService>()
        service.updateCompanyAddress(
            model = body.toModel(companyId),
            userId = parseUuid(jwtUserId())
        )

        call.respond(HttpStatusCode.NoContent)
    }
}

private fun Route.companyDetails() = jwtProtected {
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

private fun Route.companyList() = jwtProtected {
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
