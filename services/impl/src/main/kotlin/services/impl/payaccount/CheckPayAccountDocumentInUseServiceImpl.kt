package services.impl.payaccount

import repository.PaymentAccountRepository
import services.api.services.payaccount.CheckPayAccountDocumentInUseService

internal class CheckPayAccountDocumentInUseServiceImpl(
    private val paymentAccountRepository: PaymentAccountRepository
) : CheckPayAccountDocumentInUseService {

    override suspend fun checkSwiftInUse(swift: String): Boolean {
        return paymentAccountRepository.getBySwift(swift) != null
    }

    override suspend fun checkIbanInUse(iban: String): Boolean {
        return paymentAccountRepository.getByIban(iban) != null
    }
}
