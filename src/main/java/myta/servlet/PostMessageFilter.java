package myta.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;

import myta.message.model.Message;
import myta.service.IncomingMessageQueueManager;

public class PostMessageFilter implements Filter {

    private IncomingMessageQueueManager incomingMessageQueueManager;

    @SuppressWarnings("unchecked")
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

                if (requestObject instanceof Map) {

                    Map<String, Object> requestMap = null;

                    requestMap = (Map<String, Object>) requestObject;

                    if (requestMap != null) {

                        Message parsedMessage = null;

                        try {

                            parsedMessage = this.parseMessage(requestMap);

                        } catch (Exception e) {

                            // show error

                        }

                        if (parsedMessage != null) {

                            // enqueue message
                            IncomingMessageQueueManager incomingMessageQueueManager = this.getIncomingMessageQueueManager();

                            if (incomingMessageQueueManager != null) {

                                incomingMessageQueueManager.enqueueMessage(parsedMessage);

                            }

                        }

                    }

                }

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
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub

        ServletContext servletContext = filterConfig.getServletContext();
        Object incomingMessageQueueManagerObject = servletContext.getAttribute("incomingMessageQueueManager");

        if (incomingMessageQueueManagerObject != null) {

            if (incomingMessageQueueManagerObject instanceof IncomingMessageQueueManager) {

                this.incomingMessageQueueManager = (IncomingMessageQueueManager) incomingMessageQueueManagerObject;

            }

        }

    }

    public Object parseJsonRequestObject(HttpServletRequest httpServletRequest) throws IOException {

        Object obj = null;

        String contentType = httpServletRequest.getContentType();

        if ((contentType != null) && contentType.toLowerCase().startsWith("application/json")) {

            InputStream inputStream = httpServletRequest.getInputStream();

            obj = this.parseJsonRequestBody(inputStream);

        }

        return obj;

    }

    public Object parseJsonRequestBody(InputStream inputStream) throws IOException {

        Object obj = null;

        ObjectMapper mapper = new ObjectMapper();

        obj = mapper.readValue(inputStream, Object.class);

        return obj;

    }

    public Message parseMessage(Map<String, Object> objectMap) throws Exception {

        Message message = null;

        if (objectMap != null) {

            // check required fields

            // check valid email addresses

        }

        return message;

    }

    public IncomingMessageQueueManager getIncomingMessageQueueManager() {

        return this.incomingMessageQueueManager;

    }

}
