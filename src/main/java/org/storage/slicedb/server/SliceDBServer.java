package org.storage.slicedb.server;


import org.storage.slicedb.store.InMemoryStore;
import org.storage.slicedb.store.KeyValueStore;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class SliceDBServer {
    private final int port;
    private final KeyValueStore store;

    public SliceDBServer(int port) {
        this.port = port;
        this.store = new InMemoryStore();
    }

    public void start() throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("SliceDB started on port " + port);

        while (true) {
            selector.select(); // block until events
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();

                if (key.isAcceptable()) {
                    SocketChannel client = serverChannel.accept();
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                    System.out.println("New connection: " + client.getRemoteAddress());
                }
                if (key.isReadable()) {
                    SocketChannel client = (SocketChannel) key.channel();
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    int read = client.read(buffer);
                    if (read == -1) {
                        client.close();
                        continue;
                    }
                    if (read > 0) {
                        buffer.flip();
                        String input = new String(buffer.array(), 0, buffer.limit()).trim();
                        buffer.clear();

                        String response = org.storage.slicedb.protocol.CommandParser.handle(input, store);
                        client.write(ByteBuffer.wrap((response + "\n").getBytes()));
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new SliceDBServer(9090).start();
    }
}