package myta.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import myta.core.Engine;
import myta.message.model.Message;
import myta.queue.model.IncomingMessageQueueEntry;
import myta.queue.service.IncomingMessageQueueManager;
import myta.service.IncomingMessageParser;

public class PostMessageFilter implements Filter {

    private IncomingMessageQueueManager incomingMessageQueueManager;

    private IncomingMessageParser       incomingMessageParser;

    @SuppressWarnings("unchecked")
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        boolean doRedirect = false;
        boolean showPostMessageForm = false;

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
                            int statusCode = 400; // BAD REQUEST
                            this.sendHttpExceptionResponse(httpServletResponse, statusCode, e);

                        }

                        if (parsedMessage != null) {

                            // enqueue message

                            Date dateTimeReceived = new Date();

                            IncomingMessageQueueEntry queueEntry = new IncomingMessageQueueEntry(dateTimeReceived, parsedMessage);

                            IncomingMessageQueueManager incomingMessageQueueManager = this.getIncomingMessageQueueManager();
                            incomingMessageQueueManager.enqueueMessage(queueEntry);

                            httpServletResponse.setStatus(202); // ACCEPTED

                        }

                    }

                }

            } else {

                // show error

                Exception exception = new Exception("Empty request");

                int statusCode = 400; // BAD REQUEST
                this.sendHttpExceptionResponse(httpServletResponse, statusCode, exception);

            }

        } else if (httpServletRequest.getMethod().equals("GET")) {

            // ExpiryService expiryService =
            // this.getEngine().getExpiryService();

            String output = httpServletRequest.getParameter("output");

            if ((output != null) && output.equals("nvp")) {

                // this.writeNameValuePairs(expiryService, httpServletResponse);

            } else {

                showPostMessageForm = true;

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

        } else if (showPostMessageForm) {

            this.showPostMessageForm(httpServletResponse);

        }

    }

    public void showPostMessageForm(HttpServletResponse httpServletResponse) throws IOException {

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("myta/servlet/postMessageForm.html");

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));

        Writer output = httpServletResponse.getWriter();

        String line = reader.readLine();
        while (line != null) {

            output.write(line);

            line = reader.readLine();
        }

        inputStream.close();
    }

    public void sendHttpExceptionResponse(HttpServletResponse httpServletResponse, int statusCode, Exception exception) throws IOException {

        httpServletResponse.sendError(statusCode, exception.getMessage());

    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        ServletContext servletContext = filterConfig.getServletContext();

        Object engineObject = servletContext.getAttribute("engine");

        if ((engineObject != null) && (engineObject instanceof Engine)) {

            Engine engine = (Engine) engineObject;
            this.setIncomingMessageQueueManager(engine.getIncomingMessageQueueManager());
        }

        this.incomingMessageParser = new IncomingMessageParser();

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

            IncomingMessageParser messageParser = this.getIncomingMessageParser();

            message = messageParser.parseMessageRequest(objectMap);

            if (message == null) {

                throw new Exception("Empty response from message parser");

            }

        }

        return message;

    }

    public IncomingMessageQueueManager getIncomingMessageQueueManager() {

        return this.incomingMessageQueueManager;

    }

    public void setIncomingMessageQueueManager(IncomingMessageQueueManager incomingMessageQueueManager) {
        this.incomingMessageQueueManager = incomingMessageQueueManager;
    }

    public IncomingMessageParser getIncomingMessageParser() {
        return this.incomingMessageParser;
    }

    public void setIncomingMessageParser(IncomingMessageParser incomingMessageParser) {
        this.incomingMessageParser = incomingMessageParser;
    }

}
