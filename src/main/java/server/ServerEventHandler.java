package server;

import channelinfo.ChannelInfoListener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class ServerEventHandler extends SimpleChannelInboundHandler<String> {
    static final Map<String, Channel> clients = new HashMap<>();
    static final Queue<String> history = new LinkedList<>();
    static final int MAX_HISTORY = 5;

    private void handleBroadcast(Message message, ChannelHandlerContext context) {
        String channelId = context.channel()
                .id()
                .asShortText();

        clients.forEach((id, channel) -> {
            if (!id.equals(channelId)){
                ChannelFuture relay = channel.writeAndFlush(message.toString());
                relay.addListener(new ChannelInfoListener("message relayed to " + id));
            }
        });

        // history-control code...
        history.add(message.toString());
        if (history.size() > MAX_HISTORY)
            history.poll();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, String msg) throws Exception {
        handleBroadcast(Message.parse(msg), context);
    }

    @Override
    public void channelActive(final ChannelHandlerContext context) {
        Channel channel = context.channel();
        clients.put(channel.id().asShortText(), channel);

        history.forEach(channel::writeAndFlush);

        handleBroadcast(new Message("system", "client online"), context);
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) {
        Channel channel = context.channel();
        clients.remove(channel.id().asShortText());

        handleBroadcast(new Message("system", "client offline"), context);
    }
}
