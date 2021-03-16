package com.rotilho.jnano.node.transaction

import com.rotilho.jnano.node.Event

data class NewTransactionReceived(val transaction: Transaction) : Event<Transaction>(transaction)