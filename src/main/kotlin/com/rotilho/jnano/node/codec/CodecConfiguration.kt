package com.rotilho.jnano.node.codec

import com.rotilho.jnano.node.Node
import com.rotilho.jnano.node.codec.block.*
import com.rotilho.jnano.node.codec.peer.HandshakeAnswerCodec
import com.rotilho.jnano.node.codec.peer.HandshakeChallengeCodec
import com.rotilho.jnano.node.codec.peer.KeepAliveCodec
import com.rotilho.jnano.node.codec.peer.NodeCodec
import com.rotilho.jnano.node.codec.transaction.TransactionCodec
import com.rotilho.jnano.node.codec.network.PacketType
import com.rotilho.jnano.node.codec.network.TCPCodec
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
    fun tcpPublishStateTransactionCodec(node: Node, codec: StateBlockCodec): TCPCodec<*> {
        return TCPCodec(PacketType.PUBLISH, node, TransactionCodec(codec))
    }

    @Bean
    fun tcpPublishOpenTransactionCodec(node: Node, codec: OpenBlockCodec): TCPCodec<*> {
        return TCPCodec(PacketType.PUBLISH, node, TransactionCodec(codec))
    }

    @Bean
    fun tcpPublishReceiveTransactionCodec(node: Node, codec: ReceiveBlockCodec): TCPCodec<*> {
        return TCPCodec(PacketType.PUBLISH, node, TransactionCodec(codec))
    }


    @Bean
    fun tcpPublishSendTransactionCodec(node: Node, codec: SendBlockCodec): TCPCodec<*> {
        return TCPCodec(PacketType.PUBLISH, node, TransactionCodec(codec))
    }

    @Bean
    fun tcpPublishChangeTransactionCodec(node: Node, codec: ChangeBlockCodec): TCPCodec<*> {
        return TCPCodec(PacketType.PUBLISH, node, TransactionCodec(codec))
    }

    @Bean
    fun tcpRequestConfirmStateTransactionCodec(node: Node, codec: StateBlockCodec): TCPCodec<*> {
        return TCPCodec(PacketType.CONFIRM_REQUEST, node, TransactionCodec(codec))
    }

    @Bean
    fun tcpRequestConfirmOpenTransactionCodec(node: Node, codec: OpenBlockCodec): TCPCodec<*> {
        return TCPCodec(PacketType.CONFIRM_REQUEST, node, TransactionCodec(codec))
    }

    @Bean
    fun tcpRequestConfirmReceiveTransactionCodec(node: Node, codec: ReceiveBlockCodec): TCPCodec<*> {
        return TCPCodec(PacketType.CONFIRM_REQUEST, node, TransactionCodec(codec))
    }

    @Bean
    fun tcpRequestConfirmSendTransactionCodec(node: Node, codec: SendBlockCodec): TCPCodec<*> {
        return TCPCodec(PacketType.CONFIRM_REQUEST, node, TransactionCodec(codec))
    }

    @Bean
    fun tcpRequestConfirmChangeTransactionCodec(node: Node, codec: ChangeBlockCodec): TCPCodec<*> {
        return TCPCodec(PacketType.CONFIRM_REQUEST, node, TransactionCodec(codec))
    }

    @Bean
    fun tcpRequestAckStateTransactionCodec(node: Node, codec: StateBlockCodec): TCPCodec<*> {
        return TCPCodec(PacketType.CONFIRM_ACKNOWLEDGE, node, TransactionCodec(VotedBlockCodec(codec)))
    }

    @Bean
    fun tcpRequestAckOpenTransactionCodec(node: Node, codec: OpenBlockCodec): TCPCodec<*> {
        return TCPCodec(PacketType.CONFIRM_ACKNOWLEDGE, node, TransactionCodec(VotedBlockCodec(codec)))
    }

    @Bean
    fun tcpRequestAckReceiveTransactionCodec(node: Node, codec: ReceiveBlockCodec): TCPCodec<*> {
        return TCPCodec(PacketType.CONFIRM_ACKNOWLEDGE, node, TransactionCodec(VotedBlockCodec(codec)))
    }

    @Bean
    fun tcpRequestAckSendTransactionCodec(node: Node, codec: SendBlockCodec): TCPCodec<*> {
        return TCPCodec(PacketType.CONFIRM_ACKNOWLEDGE, node, TransactionCodec(VotedBlockCodec(codec)))
    }

    @Bean
    fun tcpRequestAckChangeTransactionCodec(node: Node, codec: ChangeBlockCodec): TCPCodec<*> {
        return TCPCodec(PacketType.CONFIRM_ACKNOWLEDGE, node, TransactionCodec(VotedBlockCodec(codec)))
    }

    @Bean
    fun tcpVoteCodec(node: Node): TCPCodec<*> {
        return TCPCodec(PacketType.CONFIRM_ACKNOWLEDGE, node, VoteCodec())
    }
}