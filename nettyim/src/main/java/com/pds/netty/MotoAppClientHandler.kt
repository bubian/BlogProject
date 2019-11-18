package com.pds.netty

import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelHandlerContext

/**
 * @author: pengdaosong
 * CreateTime:  2019-11-07 16:07
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class MotoAppClientHandler : ChannelInboundHandlerAdapter() {
    @Throws(Exception::class)
    override fun channelActive(ctx: ChannelHandlerContext) {

        ctx.writeAndFlush(null)
    }

    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        ctx.close()
    }
}