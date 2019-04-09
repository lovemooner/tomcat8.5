package love.moon.servlet.v3;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@javax.servlet.annotation.WebServlet(value = "/threadPoolAsync", asyncSupported = true)
public class AsyncServlet101 extends javax.servlet.http.HttpServlet {

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(100, 200,
            50000L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100));

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response)  {

        javax.servlet.AsyncContext asyncContext = request.startAsync();
        executor.execute(() -> {

            new AsyncServlet100.LongRunningProcess().run();

            try {
                asyncContext.getResponse().getWriter().write("Hello World!");
            } catch (IOException e) {
                e.printStackTrace();
            }

            asyncContext.complete();

        });
    }

}
