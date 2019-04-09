package love.moon.servlet.v3;


import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@javax.servlet.annotation.WebServlet(value = "/nonBlockingThreadPoolAsync", asyncSupported = true)
public class NonBlockingAsyncHelloServlet extends javax.servlet.http.HttpServlet {

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 20, 5000L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100));

    protected void doPost(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp) throws javax.servlet.ServletException, IOException {
        doGet(req,resp);
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws IOException {
        javax.servlet.AsyncContext asyncContext = request.startAsync();
        javax.servlet.ServletInputStream inputStream = request.getInputStream();
        System.out.println("Thread " + Thread.currentThread().getName() + " will setReadListener");
        inputStream.setReadListener(new javax.servlet.ReadListener() {

            @Override
            public void onAllDataRead() throws IOException {
                System.out.println("Thread " + Thread.currentThread().getName() + " onAllDataRead ");
                StringBuilder sb = new StringBuilder();
                int len = -1;
                byte bytes[] = new byte[8];
                while (inputStream.isReady() && !inputStream.isFinished() && (len = inputStream.read(bytes)) != -1) {
                    String data = new String(bytes, 0, len);
                    sb.append(data);
                }
                System.out.println("Data read: " + sb.toString());
                try {
                    Thread.sleep(1000 * 5l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    asyncContext.getResponse().getWriter().write("Hello World! nonBlockingThreadPoolAsync");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                asyncContext.complete();

            }

            @Override
            public void onError(Throwable t) {
                asyncContext.complete();
            }

            @Override
            public void onDataAvailable() {
            }
        });


    }

}