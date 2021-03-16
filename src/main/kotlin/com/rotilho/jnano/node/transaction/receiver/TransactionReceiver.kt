package com.rotilho.jnano.node.transaction.receiver

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.rotilho.jnano.node.EventBus
import com.rotilho.jnano.node.InboundEvent
import com.rotilho.jnano.node.transaction.NewTransactionReceived
import com.rotilho.jnano.node.transaction.Transaction
import com.rotilho.jnano.node.transaction.TransactionProperties
import com.rotilho.jnano.node.transaction.TransactionRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct

@Service
class TransactionReceiver(properties: TransactionProperties, val repository: TransactionRepository) {
    private val logger = KotlinLogging.logger {}

    private val cache: Cache<ByteArray, Deferred<ByteArray>> = CacheBuilder.newBuilder()
        .expireAfterWrite(properties.hashCacheExpirationTimeInSeconds!!, TimeUnit.SECONDS)
        .build()

    @PostConstruct
    fun initialize() {
        GlobalScope.launch {
            initializeTransactionListener()
        }
    }

    private fun initializeTransactionListener() {
        GlobalScope.launch {
            EventBus.listen(InboundEvent::class.java, Transaction::class.java)
                .collect { receive(it) }
        }
    }

    private suspend fun receive(transaction: Transaction) {
        val saved = cache.get(transaction.getHash()) {
            GlobalScope.async {

                if (repository.insert(transaction)) {
                    logger.debug { "Saved $transaction" }
                    EventBus.publish(NewTransactionReceived(transaction))
                }

                transaction.getHash()
            }
        }

        try {
            saved.await()
        } catch (e: Exception) {
            logger.error(e) { "Failed to save $transaction" }
            cache.invalidate(transaction.getHash())
        }
    }
}