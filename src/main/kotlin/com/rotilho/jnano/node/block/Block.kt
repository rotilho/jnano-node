package com.rotilho.jnano.node.block

interface Block {
    fun getType(): BlockType
    fun getHash(): ByteArray
    fun getPrevious(): ByteArray
    fun getPublicKey(): ByteArray?
}

enum class BlockType(val code: Short, val blockSize: Int) {
    OPEN(4, 96),
    CHANGE(5, 64),
    RECEIVE(3, 64),
    SEND(2, 80),
    STATE(6, 144);

    companion object {
        private val CODE_MAP = BlockType.values().associateBy(BlockType::code)
        fun fromCode(code: Short): BlockType? {
            return CODE_MAP[code]
        }
    }
}
