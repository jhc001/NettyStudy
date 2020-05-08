package com.mashibing.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

public class MyServer {
    public static void main(String[] args) {
        new MyNetrtyServer(8888).serverStart();
    }
}

class MyNetrtyServer {


    int port = 8888;

    public MyNetrtyServer(int port) {
        this.port = port;
    }

    public void serverStart() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();

        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new MyHander());
                    }
                });

        try {
            ChannelFuture f = b.bind(port).sync();

            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }


    }
}

class MyHander extends ChannelInboundHandlerAdapter {
	private ByteBuf merge = null;
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	ByteBuf m = (ByteBuf)msg;
    	merge.writeBytes(m);
    	if(merge.readableBytes()>=20) {
    		System.out.println(merge.toString(CharsetUtil.UTF_8));
    		m.release();
    		ctx.close();
    	}
    }

    @Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    	merge = ctx.alloc().buffer(20);
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		this.merge.release();
		merge = null;
	}




	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
