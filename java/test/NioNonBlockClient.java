package test;

/**
 * User: lovemooner
 * Date: 17-4-5
 * Time: 下午4:08
 */


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioNonBlockClient {



    private static Selector selector;
//    public static String HOST="slc11fsp.us.oracle.com";
    private static String HOST="localhost";

    public static void main(String[] args) throws IOException, InterruptedException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        selector = Selector.open();
        // 注册连接服务端socket动作
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        InetSocketAddress SERVER_ADDRESS = new InetSocketAddress(HOST, 8080);
//        InetSocketAddress SERVER_ADDRESS = new InetSocketAddress("localhost", 8080);
        socketChannel.connect(SERVER_ADDRESS);
        NioNonBlockClient client = new NioNonBlockClient();
        client.listen();

    }

    /**
     * @param selectionKey
     * @throws IOException
     */
    private void dispatch(SelectionKey selectionKey) throws IOException, InterruptedException {
        if (selectionKey.isConnectable()) {
            System.out.println("Client connect");
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            if (socketChannel.isConnectionPending()) {
                socketChannel.finishConnect();

            }
            //mock http server
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put(getHttpHeaders().getBytes());
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();
            buffer.put(getHttpHeaders1().getBytes());
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();
            buffer.put(getHttpBody().getBytes());
            buffer.flip();
            socketChannel.write(buffer);
            socketChannel.register(selector, SelectionKey.OP_READ);
        } else if (selectionKey.isReadable()) {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            //将缓冲区清空以备下次读取
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            //读取服务器发送来的数据到缓冲区中
            int count = socketChannel.read(buffer);
            if (count > 0) {
                String receiveText = new String(buffer.array(), 0, count);
                System.out.println("Client->Receive:" + receiveText);
            }

        }else if (selectionKey.isWritable()) {
            System.out.println("isWritable");
        }
    }


    public void listen() throws IOException, InterruptedException {
        while (true) {
            int val = selector.select();
            if (val > 0) {
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    dispatch(selectionKey);
                    iterator.remove();
                }
            }
        }
    }

    String content;

    {
        content = "{id:\"1\",name:\"nan\",age:\"18\"}";
    }
    public String getHttpHeaders() {
        StringBuffer sb = new StringBuffer();
        sb.append("GET /servlet/HelloServlet HTTP/1.1\r\n");
        sb.append("Host: ").append(HOST).append(":8080\r\n");

        sb.append("Connection: keep-alive\r\n");
        sb.append("Cache-Control: max-age=0\r\n");
        sb.append("User-Agent: Mozilla/5.0 (Windows NT 5.1) AppleWebKit/536.11 (KHTML, like Gecko) Chrome/20.0.1132.47 Safari/536.11\r\n");
        sb.append("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n");
        sb.append("Accept-Encoding: gzip,deflate,sdch\r\n");
        sb.append("Accept-Language: zh-CN,zh;q=0.8\r\n");
        sb.append("Accept-Charset: GBK,utf-8;q=0.7,*;q=0.3\r\n");
//        sb.append("content-length: ").append(content.length()).append("\r\n");
//        sb.append("\r\n");
        return sb.toString();

    }

    public String getHttpHeaders1() {
        StringBuffer sb = new StringBuffer();
        sb.append("content-length: ").append(content.length()).append("\r\n");
        sb.append("\r\n");
        return sb.toString();

    }


    public String getHttpBody() {
        StringBuilder sb = new StringBuilder();
        sb.append(content);
        return sb.toString();

    }


}
