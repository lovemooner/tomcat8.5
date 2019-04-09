package love.moon.servlet.v3;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

@javax.servlet.annotation.WebServlet(value = "/simpleAsync", asyncSupported = true)
public class AsyncServlet100 extends javax.servlet.http.HttpServlet {
    @Override
    protected void doGet(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp) throws javax.servlet.ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.println("下订单开始: " + new Date() + "<br/>");
        out.flush();

        javax.servlet.AsyncContext ctx = req.startAsync();
        ctx.start(()->{
            new LongRunningProcess().run();
            try {
                ctx.getResponse().getWriter().write("Hello World!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            ctx.complete();
        });
    }

    @Override
    protected void doPost(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp) throws javax.servlet.ServletException, IOException {
        this.doGet(req, resp);
    }

    public static class LongRunningProcess {

        public void run() {
            try {

                int millis = ThreadLocalRandom.current().nextInt(2000);
                String currentThread = Thread.currentThread().getName();
                System.out.println(currentThread + " sleep for " + millis + " milliseconds.");
                Thread.sleep(millis);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}