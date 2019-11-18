package com.pds.netty


import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender
import io.netty.handler.codec.protobuf.ProtobufEncoder

/**
 * @author: pengdaosong
 * CreateTime:  2019-11-07 16:06
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class MotoAppClient {
    @Throws(Exception::class)
    fun connect(host: String, port: Int) {
        val workerGroup = NioEventLoopGroup()
        try {
            val b = Bootstrap()
            b.group(workerGroup)
            b.channel(NioSocketChannel::class.java)
            b.option(ChannelOption.SO_KEEPALIVE, true)
            b.handler(object : ChannelInitializer<SocketChannel>() {
                @Throws(Exception::class)
                override fun initChannel(ch: SocketChannel) {
                    //decoded
                    ch.run {
                        pipeline().addLast(LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4))
                        //encoded
                        pipeline().addLast(LengthFieldPrepender(4))
                        pipeline().addLast(ProtobufEncoder())
                        // ע��handler
                        pipeline().addLast(MotoAppClientHandler())
                    }
                }
            })
            val f = b.connect(host, port).sync()
            f.channel().closeFuture().sync()
        } finally {
            workerGroup.shutdownGracefully()
        }
    }
}