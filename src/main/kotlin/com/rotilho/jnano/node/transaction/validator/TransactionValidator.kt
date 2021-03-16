package com.rotilho.jnano.node.transaction.validator

import com.rotilho.jnano.node.EventBus
import com.rotilho.jnano.node.transaction.BlockSubType
import com.rotilho.jnano.node.transaction.NewTransactionReceived
import com.rotilho.jnano.node.transaction.Transaction
import com.rotilho.jnano.node.transaction.TransactionRepository
import com.rotilho.jnano.node.transaction.finder.TransactionSeeker
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class TransactionValidator(val seeker: TransactionSeeker, val repository: TransactionRepository) {
    private val logger = KotlinLogging.logger {}

    @PostConstruct
    fun initialize() {
        GlobalScope.launch {
            initializeNewTransactionListener()
        }
    }

    private fun initializeNewTransactionListener() {
        GlobalScope.launch {
            EventBus.listen(NewTransactionReceived::class.java, Transaction::class.java)
                .collect { validate(it) }
        }
    }

    private suspend fun validate(transaction: Transaction) {
        // TODO: I stopped here
        val previous = getOrSeekPrevious(transaction)
        if (transaction.getBlockSubType() != BlockSubType.OPEN &&) {

        }
    }

    private suspend fun getOrSeekPrevious(transaction: Transaction): Transaction? {
        if (transaction.getBlockSubType() != BlockSubType.OPEN) {
            return null
        }

        val previousHash = transaction.getPrevious()!!
        val previousTransaction = repository.find(previousHash)

        if (previousTransaction != null) {
            return previousTransaction
        }

        seeker.seek(transaction.getPublicKey(), previousHash)

        return null
    }


}