package com.rotilho.jnano.node.transaction

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.rotilho.jnano.node.BroadcastEvent
import com.rotilho.jnano.node.BroadcastStrategy
import com.rotilho.jnano.node.EventBus
import com.rotilho.jnano.node.InboundEvent
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
class TransactionService(properties: TransactionProperties, val repository: TransactionRepository) {
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
                .collect { process(BroadcastStrategy.MINORITY, it) }
        }
    }

    private suspend fun process(broadcastStrategy: BroadcastStrategy, transaction: Transaction) {
        val saved = cache.get(transaction.hash) {
            GlobalScope.async {
                repository.save(transaction).hash
            }
        }

        try {
            saved.await()

            EventBus.publish(BroadcastEvent(broadcastStrategy, transaction))

            logger.debug { "Saved $transaction" }
        } catch (e: Exception) {
            logger.error(e) { "Failed to save $transaction" }
            cache.invalidate(transaction.hash)
        }
    }
}