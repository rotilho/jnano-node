package com.rotilho.jnano.node

import com.rotilho.jnano.commons.NanoKeys
import com.rotilho.jnano.commons.NanoSeeds

object KeysProvider {
    private val seed = NanoSeeds.generateSeed();
    private val privateKey = NanoKeys.createPrivateKey(seed, 0);
    private val publicKey = NanoKeys.createPublicKey(privateKey);

    fun getPrivateKey(): ByteArray {
        return privateKey;
    }

    fun getPublicKey(): ByteArray {
        return publicKey;
    }
}