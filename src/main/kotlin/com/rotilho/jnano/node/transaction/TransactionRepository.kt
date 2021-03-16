package com.rotilho.jnano.node.transaction

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.rotilho.jnano.commons.NanoAmount
import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.Parameter
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.stereotype.Repository
import org.springframework.transaction.reactive.TransactionalOperator
import java.math.BigDecimal
import java.math.BigInteger
import java.util.concurrent.TimeUnit
import java.util.function.BiFunction


@Repository
class TransactionRepository(
    properties: TransactionProperties,
    val client: DatabaseClient,
    val operator: TransactionalOperator
) {
    private val cache: Cache<ByteArray, Deferred<Transaction>> = CacheBuilder.newBuilder()
        .maximumSize(properties.entityCacheMaxSize!!)
        .expireAfterWrite(properties.entityCacheExpirationTimeInSeconds!!, TimeUnit.SECONDS)
        .build()

    val insertOrUpdateStatement = """
            INSERT IGNORE INTO transactions
                        (
                                    hash,
                                    blockType,
                                    blockSubType,
                                    accountVersion,
                                    publicKey,
                                    previous,
                                    representative,
                                    balance,
                                    link,
                                    height,
                                    signature,
                                    work
                        )
                        VALUES
                        (
                                    :hash,
                                    :blockType,
                                    :blockSubType,
                                    :accountVersion,
                                    :publicKey,
                                    :previous,
                                    :representative,
                                    :balance,
                                    :link,
                                    :height,
                                    :signature,
                                    :work
                        )
    """.trimIndent()

    //    ON DUPLICATE KEY UPDATE
//    blockSubType = :blockSubType,
//    accountVersion = :accountVersion,
//    publicKey = :publicKey,
//    previous = :previous, // probably immutable
//    representative = :representative,
//    balance = :balance,
//    link = :link, // probably immutable
//    height = :height
    val mapping: BiFunction<Row, RowMetadata, Transaction> =
        BiFunction<Row, RowMetadata, Transaction> { row, rowMetaData ->
            val hash = row.get("hash", ByteArray::class.java)!!
            val status = row.get("hash", TransactionStatus::class.java)!!
            val version = row.get("version", Int::class.java)!!
            val blockType = row.get("blockType", BlockType::class.java)!!
            val blockSubType = row.get("blockSubType", BlockSubType::class.java)!!
            val accountVersion = row.get("accountVersion", BigInteger::class.java)!!
            val publicKey = row.get("publicKey", ByteArray::class.java)!!
            val previous = row.get("previous", ByteArray::class.java)!!
            val representative = row.get("representative", ByteArray::class.java)!!
            val balance = row.get("representative", BigDecimal::class.java)!!
            val link = row.get("link", ByteArray::class.java)!!
            val height = row.get("height", BigInteger::class.java)
            val signature = row.get("signature", ByteArray::class.java)!!
            val work = row.get("work", ByteArray::class.java)!!

            when (blockType) {
                BlockType.OPEN -> TransactionOpenBlock(
                    hash,
                    status,
                    version,
                    accountVersion,
                    publicKey,
                    representative,
                    NanoAmount.ofRaw(balance),
                    link,
                    signature,
                    work
                )
                BlockType.SEND -> TransactionSendBlock(
                    hash,
                    status,
                    version,
                    accountVersion,
                    publicKey,
                    previous,
                    representative,
                    NanoAmount.ofRaw(balance),
                    link,
                    height,
                    signature,
                    work
                )
                BlockType.RECEIVE -> TransactionReceiveBlock(
                    hash,
                    status,
                    version,
                    accountVersion,
                    publicKey,
                    previous,
                    representative,
                    NanoAmount.ofRaw(balance),
                    link,
                    height,
                    signature,
                    work
                )
                BlockType.CHANGE -> TransactionChangeBlock(
                    hash,
                    status,
                    version,
                    accountVersion,
                    publicKey,
                    previous,
                    representative,
                    NanoAmount.ofRaw(balance),
                    link,
                    height,
                    signature,
                    work
                )
                BlockType.STATE -> TransactionStateBlock(
                    hash,
                    status,
                    version,
                    blockSubType,
                    accountVersion,
                    publicKey,
                    previous,
                    representative,
                    NanoAmount.ofRaw(balance),
                    link,
                    height,
                    signature,
                    work
                )
            }
        }


    suspend fun find(hash: ByteArray): Transaction? {
        val transaction = cache.get(hash) {
            GlobalScope.async {
                client
                    .sql("SELECT * FROM transactions WHERE hash = :hash")
                    .bind("hash", hash)
                    .map(mapping)
                    .one()
                    .awaitSingle()
            }
        }

        return transaction.await()
    }

    suspend fun insert(transaction: Transaction): Boolean {
        val inserts = this.client.sql(insertOrUpdateStatement)
            .bind("hash", transaction.getHash())
            .bind("blockType", transaction.getBlockType())
            .bind("blockSubType", Parameter.fromOrEmpty(transaction.getBlockSubType(), BlockSubType::class.java))
            .bind("accountVersion", Parameter.fromOrEmpty(transaction.getAccountVersion(), BigInteger::class.java))
            .bind("publicKey", Parameter.fromOrEmpty(transaction.getPublicKey(), ByteArray::class.java))
            .bind("previous", Parameter.fromOrEmpty(transaction.getPrevious(), ByteArray::class.java))
            .bind("representative", Parameter.fromOrEmpty(transaction.getRepresentative(), ByteArray::class.java))
            .bind("balance", Parameter.fromOrEmpty(transaction.getBalance()?.toRaw(), BigDecimal::class.java))
            .bind("link", Parameter.fromOrEmpty(transaction.getLink(), ByteArray::class.java))
            .bind("height", Parameter.fromOrEmpty(transaction.getHeight(), BigInteger::class.java))
            .bind("signature", transaction.getSignature())
            .bind("work", transaction.getWork())
            .fetch()
            .awaitRowsUpdated()

        return inserts == 1
    }
}