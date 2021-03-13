package com.rotilho.jnano.node.codec

import com.rotilho.jnano.node.Node
import com.rotilho.jnano.node.codec.network.PacketType
import com.rotilho.jnano.node.codec.network.TCPCodec
import com.rotilho.jnano.node.codec.peer.HandshakeAnswerCodec
import com.rotilho.jnano.node.codec.peer.HandshakeChallengeCodec
import com.rotilho.jnano.node.codec.peer.KeepAliveCodec
import com.rotilho.jnano.node.codec.peer.NodeCodec
import com.rotilho.jnano.node.codec.transaction.*
import com.rotilho.jnano.node.codec.vote.VoteCodec
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CodecConfiguration {

    @Bean
    fun tcpHandshakeChallengeCodec(node: Node, nodeCodec: NodeCodec): TCPCodec<*> {
        return TCPCodec(PacketType.HANDSHAKE, node, HandshakeChallengeCodec(nodeCodec))
    }

    @Bean
    fun tcpHandshakeAnswerCodec(node: Node, nodeCodec: NodeCodec): TCPCodec<*> {
        return TCPCodec(PacketType.HANDSHAKE, node, HandshakeAnswerCodec(nodeCodec))
    }

    @Bean
    fun tcpKeepAliveCodec(node: Node, nodeCodec: NodeCodec): TCPCodec<*> {
        return TCPCodec(PacketType.KEEP_ALIVE, node, KeepAliveCodec(nodeCodec))
    }

    @Bean
    fun tcpPublishStateTransactionCodec(node: Node): TCPCodec<*> {
        return TCPCodec(PacketType.PUBLISH, node, TransactionStateBlockCodec())
    }

    @Bean
    fun tcpPublishOpenTransactionCodec(node: Node): TCPCodec<*> {
        return TCPCodec(PacketType.PUBLISH, node, TransactionOpenBlockCodec())
    }

    @Bean
    fun tcpPublishReceiveTransactionCodec(node: Node): TCPCodec<*> {
        return TCPCodec(PacketType.PUBLISH, node, TransactionReceiveBlockCodec())
    }

    @Bean
    fun tcpPublishSendTransactionCodec(node: Node): TCPCodec<*> {
        return TCPCodec(PacketType.PUBLISH, node, TransactionSendBlockCodec())
    }

    @Bean
    fun tcpPublishChangeTransactionCodec(node: Node): TCPCodec<*> {
        return TCPCodec(PacketType.PUBLISH, node, TransactionChangeBlockCodec())
    }

    @Bean
    fun tcpRequestConfirmStateTransactionCodec(node: Node): TCPCodec<*> {
        return TCPCodec(PacketType.CONFIRM_REQUEST, node, TransactionStateBlockCodec())
    }

    @Bean
    fun tcpRequestConfirmOpenTransactionCodec(node: Node): TCPCodec<*> {
        return TCPCodec(PacketType.CONFIRM_REQUEST, node, TransactionOpenBlockCodec())
    }

    @Bean
    fun tcpRequestConfirmReceiveTransactionCodec(node: Node): TCPCodec<*> {
        return TCPCodec(PacketType.CONFIRM_REQUEST, node, TransactionReceiveBlockCodec())
    }

    @Bean
    fun tcpRequestConfirmSendTransactionCodec(node: Node): TCPCodec<*> {
        return TCPCodec(PacketType.CONFIRM_REQUEST, node, TransactionSendBlockCodec())
    }

    @Bean
    fun tcpRequestConfirmChangeTransactionCodec(node: Node): TCPCodec<*> {
        return TCPCodec(PacketType.CONFIRM_REQUEST, node, TransactionChangeBlockCodec())
    }

    @Bean
    fun tcpRequestAckStateTransactionCodec(node: Node): TCPCodec<*> {
        return TCPCodec(PacketType.CONFIRM_ACKNOWLEDGE, node, VotedTransactionCodec(TransactionStateBlockCodec()))
    }

    @Bean
    fun tcpRequestAckOpenTransactionCodec(node: Node): TCPCodec<*> {
        return TCPCodec(PacketType.CONFIRM_ACKNOWLEDGE, node, VotedTransactionCodec(TransactionOpenBlockCodec()))
    }

    @Bean
    fun tcpRequestAckReceiveTransactionCodec(node: Node): TCPCodec<*> {
        return TCPCodec(PacketType.CONFIRM_ACKNOWLEDGE, node, VotedTransactionCodec(TransactionReceiveBlockCodec()))
    }

    @Bean
    fun tcpRequestAckSendTransactionCodec(node: Node): TCPCodec<*> {
        return TCPCodec(PacketType.CONFIRM_ACKNOWLEDGE, node, VotedTransactionCodec(TransactionSendBlockCodec()))
    }

    @Bean
    fun tcpRequestAckChangeTransactionCodec(node: Node): TCPCodec<*> {
        return TCPCodec(PacketType.CONFIRM_ACKNOWLEDGE, node, VotedTransactionCodec(TransactionChangeBlockCodec()))
    }

    @Bean
    fun tcpVoteCodec(node: Node): TCPCodec<*> {
        return TCPCodec(PacketType.CONFIRM_ACKNOWLEDGE, node, VoteCodec())
    }
}