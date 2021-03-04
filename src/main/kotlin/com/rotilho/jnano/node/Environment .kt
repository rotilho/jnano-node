package com.rotilho.jnano.node

import com.rotilho.jnano.commons.NanoAccounts
import com.rotilho.jnano.commons.NanoHelper

enum class Environment {
    LIVE(
        "RC",
        "xrb_3t6k35gi95xu6tergt6p69ck76ogmitsa8mnijtpxm9fkcm736xtoncuohr3",
        "E89208DD038FBB269987689621D52292AE9C35941A7484756ECCED92A65093BA"
    ),
    BETA(
        "RB",
        "xrb_3betaz86ypbygpqbookmzpnmd5jhh4efmd8arr9a3n4bdmj1zgnzad7xpmfp",
        "A59A47CC4F593E75AE9AD653FDA9358E2F7898D9ACC8C60E80D0495CE20FBA9F"
    ),
    TEST(
        "RA",
        "xrb_3e3j5tkog48pnny9dmfzj1r16pg8t1e76dz5tmac6iq689wyjfpiij4txtdo",
        "B0311EA55708D6A53C75CDBF88300259C6D018522FE3D4D0A242E431F9E8B6D0"
    );

    val code: String
    val genesisPublicKey: ByteArray
    val genesisHash: ByteArray

    constructor(code: String, genesisAccount: String, genesisHash: String) {
        this.code = code
        this.genesisPublicKey = NanoAccounts.toPublicKey(genesisAccount)
        this.genesisHash = NanoHelper.toByteArray(genesisHash)
    }

    companion object {
        private val CODE_MAP = values().associateBy(Environment::code)
        fun fromCode(code: String): Environment? {
            return CODE_MAP[code]
        }
    }
}