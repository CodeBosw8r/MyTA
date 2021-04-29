package myta.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.Charset;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class PostMessageFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        // TODO Auto-generated method stub

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        // HttpServletResponse httpServletResponse = (HttpServletResponse)
        // servletResponse;

        boolean doRedirect = false;
        boolean showExpiryForm = false;

        // make sure this is only done using post method
        if (httpServletRequest.getMethod().equals("POST")) {

            Object requestObject = this.parseJsonRequestObject(httpServletRequest);

            if (requestObject != null) {

            } else {

                // show error

            }

        } else if (httpServletRequest.getMethod().equals("GET")) {

            // ExpiryService expiryService =
            // this.getEngine().getExpiryService();

            String output = httpServletRequest.getParameter("output");

            if ((output != null) && output.equals("nvp")) {

                // this.writeNameValuePairs(expiryService, httpServletResponse);

            } else {

                showExpiryForm = true;

            }

        }

        if (doRedirect) {

            // String successUrl = this.getSuccessUrl(httpServletRequest);
            //
            // if (successUrl != null) {
            //
            // httpServletResponse.setStatus(302);
            // httpServletResponse.addHeader("Location", successUrl);
            //
            // return;
            //
            // }

        } else if (showExpiryForm) {

            this.showPostMessageForm(servletResponse);

        }

    }

    public void showPostMessageForm(ServletResponse servletResponse) throws IOException {

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("myta/servlet/postMessageForm.html");

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));

        Writer output = servletResponse.getWriter();

        String line = reader.readLine();
        while (line != null) {

            output.write(line);

            line = reader.readLine();
        }

        inputStream.close();
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

    @Override
    public void init(FilterConfig var1) throws ServletException {
        // TODO Auto-generated method stub

    }

    public Object parseJsonRequestObject(HttpServletRequest httpServletRequest) {

        Object obj = null;

        if (httpServletRequest.getContentType().equals("application/json")) {

        }

        return obj;

    }

}
