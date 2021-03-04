package com.rotilho.jnano.node.codec.peer

import com.rotilho.jnano.node.codec.network.TCPCodecSupport
import com.rotilho.jnano.node.peer.KeepAlive
import com.rotilho.jnano.node.utils.flatMap
import com.rotilho.jnano.node.utils.fromLittleEndian
import com.rotilho.jnano.node.utils.isEmpty
import com.rotilho.jnano.node.utils.toLittleEndian
import java.lang.Math.min
import java.net.InetAddress
import java.net.InetSocketAddress
import java.util.stream.IntStream
import java.util.stream.Stream
import kotlin.streams.toList

class KeepAliveCodec(private val nodeCodec: NodeCodec) : TCPCodecSupport {
    override fun encode(protocolVersion: Int, o: Any): ByteArray? {
        if (o !is KeepAlive) {
            return null
        }
        o.neighbourNodes
        val message = ByteArray(146)
        IntStream.range(0, o.neighbourNodes.size).forEach { encode(o.neighbourNodes[it], message, it) }
        return message
    }

    override fun decode(protocolVersion: Int, m: ByteArray): Any? {
        val node = nodeCodec.decode(protocolVersion, m) ?: return null
        val messageBody = m.copyOfRange(6, m.size)
        val peers = Stream.iterate(0) { i -> i + 18 }
            .limit(calculateNeighbourLimit(messageBody))
            .map { decode(messageBody, it + 2) }
            .filter { it != null }
            .map { it!! }
            .toList()
        return KeepAlive(node, peers);
    }

    private fun encode(socketAddress: InetSocketAddress, message: ByteArray, index: Int) {
        val address = getIPv6Address(socketAddress)
        val port = toLittleEndian(socketAddress.port)
        System.arraycopy(flatMap(address, port), 0, message, index * 18 + 2, 18)
    }

    private fun decode(message: ByteArray, index: Int): InetSocketAddress? {
        val addressBytes = message.copyOfRange(index, index + 16)
        if (isEmpty(addressBytes)) {
            return null
        }
        val address = InetAddress.getByAddress(addressBytes)
        val port = fromLittleEndian(message.copyOfRange(index + 16, index + 18));
        return InetSocketAddress(address, port)
    }

    private fun calculateNeighbourLimit(messageBody: ByteArray): Long {
        return min(messageBody.size / 18L, 8)
    }

    private fun getIPv6Address(socketAddress: InetSocketAddress): ByteArray {
        val address = socketAddress.address.address
        if (address.size == 16) {
            return address
        }
        val ipv6Address = ByteArray(16)
        ipv6Address[10] = -1
        ipv6Address[11] = -1
        System.arraycopy(address, 0, ipv6Address, 12, 4)
        return ipv6Address;
    }

}