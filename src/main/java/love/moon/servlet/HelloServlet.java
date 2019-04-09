package love.moon.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Author: lovemooner
 * Date: 2017/5/18 14:09
 */
public class HelloServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        System.out.println("coming request");
        BufferedReader br = request.getReader();
        String str, wholeStr = "";
        while ((str = br.readLine()) != null) {
            wholeStr += str;
        }
        System.out.println(wholeStr);

        String output = "Hello Servlet";
        try {
            System.out.println("Thread " + Thread.currentThread() + " will sleep");
            Thread.sleep(1000 * 2l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        response.getWriter().println(output);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }
}