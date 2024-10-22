package com.example.transactional.listener

import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager

@Component
class TransactionEventListener : TransactionSynchronization {

    override fun afterCompletion(status: Int) {
        if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
            println("트랜잭션이 롤백되었습니다.")
        } else {
            println("트랜잭션이 커밋되었습니다.")
        }
    }

    companion object {
        fun register() {
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.registerSynchronization(TransactionEventListener())
            }
        }
    }
}
