package love.moon.servlet.v3;

import java.io.IOException;

public class AsyncIOServlet101 extends javax.servlet.http.HttpServlet {
    @Override
    public void doPost(javax.servlet.http.HttpServletRequest request,
                       javax.servlet.http.HttpServletResponse response)
            throws IOException {
        final javax.servlet.AsyncContext acontext = request.startAsync();
        final javax.servlet.ServletInputStream input = request.getInputStream();

        input.setReadListener(new javax.servlet.ReadListener() {
            byte buffer[] = new byte[4 * 1024];
            StringBuilder sb = new StringBuilder();

            @Override
            public void onDataAvailable() {
                try {
                    do {
                        int length = input.read(buffer);
                        sb.append(new String(buffer, 0, length));
                    } while (input.isReady());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(sb.toString());
            }

            @Override
            public void onAllDataRead() {
                try {
                    acontext.getResponse().getWriter()
                            .write("...the response...");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                acontext.complete();
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("error");
            }
        });
    }
}
