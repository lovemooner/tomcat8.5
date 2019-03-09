package test;

import love.moon.common.HttpResponse;
import love.moon.util.HttpUtil;

import java.io.IOException;

public class MockClient {

    public static void main(String[] args) throws IOException {
        String url_HelloServlet="http://localhost:8080/servlet/HelloServlet";
        String url_nonBlockingThreadPoolAsync="http://slc11fsp.us.oracle.com:8080/servlet/nonBlockingThreadPoolAsync";
   for(int i=0;i<10;i++){
       new Thread(()->{
           try {
               System.out.println("Client Thread "+Thread.currentThread().getName()+" start");
               HttpResponse response = HttpUtil.sendBrowserGet(url_nonBlockingThreadPoolAsync);
               System.out.println(response.getContent());
           } catch (IOException e) {
               e.printStackTrace();
           }
       }).start();
   }
    }
}
