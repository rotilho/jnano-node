package com.rotilho.jnano.node.transaction.finder

import com.rotilho.jnano.node.transaction.Transaction
import com.rotilho.jnano.node.transaction.TransactionRepository
import org.springframework.stereotype.Service

@Service
class TransactionSeeker(val repository: TransactionRepository) {

    suspend fun seek(publicKey: ByteArray?, hash: ByteArray): Transaction? {
        val transaction = repository.find(hash)
        if (transaction != null) {
            return transaction
        }
        return null
    }
}