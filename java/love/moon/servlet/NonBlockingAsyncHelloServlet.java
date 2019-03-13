package love.moon.servlet;


import javax.servlet.AsyncContext;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@WebServlet(value = "/nonBlockingThreadPoolAsync", asyncSupported = true)
public class NonBlockingAsyncHelloServlet extends HttpServlet {

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 20, 5000L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100));

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AsyncContext asyncContext = request.startAsync();
        ServletInputStream inputStream = request.getInputStream();
        System.out.println("Thread " + Thread.currentThread().getName() + " will setReadListener");
        inputStream.setReadListener(new ReadListener() {

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