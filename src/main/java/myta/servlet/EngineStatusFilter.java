package myta.servlet;

import java.io.IOException;
import java.util.LinkedHashMap;
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

import myta.core.Engine;

public class EngineStatusFilter implements Filter {

    private Engine engine;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        String path = httpServletRequest.getRequestURI();

        boolean success = false;
        String message = "ok";

        String output = httpServletRequest.getParameter("output");

        if ((output != null) && output.equals("nvp")) {

            Map<String, String> values = this.collectNameValuePairs(engine);
            this.writeNameValuePairs(values, httpServletResponse);

        } else {

            if (engine != null) {

                if ((path != null) && path.contains("/pause/")) {

                    engine.pause();
                    success = true;

                } else if ((path != null) && path.contains("/unpause/")) {

                    engine.unpause();
                    success = true;

                } else if (engine.isInitialized()) {

                    if (!engine.isPaused()) {

                        success = true;

                    } else {

                        message = "paused";

                    }

                } else {

                    message = "initializing";

                }

            } else {

                message = "unknown";

            }

            if (success) {

                // assume all is well
                httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                httpServletResponse.setContentType("text/plain");

            } else {

                // throw error
                httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                httpServletResponse.setContentType("text/plain");
            }

            httpServletResponse.getWriter().append(message);

        }

    }

    public String getEngineStatus(Engine engine) {

        String status = "unknown";
        if (engine.isInitialized()) {

            if (engine.isPaused()) {

                status = "paused";

            } else {

                status = "ok";

            }

        } else {

            status = "initializing";

        }

        return status;

    }

    public Map<String, String> collectNameValuePairs(Engine engine) {

        Map<String, String> values = new LinkedHashMap<String, String>();

        String status = this.getEngineStatus(engine);
        values.put("status", status);

        int numWorkers = engine.getNumWorkers();
        values.put("numWorkers", String.valueOf(numWorkers));

        int incomingQueueSize = engine.getIncomingMessageQueueManager().getQueueSize();
        values.put("incomingQueueSize", String.valueOf(incomingQueueSize));

        int incomingQueueMaxSize = engine.getIncomingMessageQueueManager().getQueueMaxSize();
        values.put("incomingQueueMaxSize", String.valueOf(incomingQueueMaxSize));

        int mailsSent = engine.getNumMailsSent();
        values.put("mailsSent", String.valueOf(mailsSent));

        // TODO num mails failed

        long freeMemory = Runtime.getRuntime().freeMemory();
        values.put("freeMemory", String.valueOf(freeMemory));

        long totalMemory = Runtime.getRuntime().totalMemory();
        values.put("totalMemory", String.valueOf(totalMemory));

        long maxMemory = Runtime.getRuntime().maxMemory();
        values.put("maxMemory", String.valueOf(maxMemory));

        int uptime = engine.getUptime();
        values.put("uptime", String.valueOf(uptime));

        return values;

    }

    public void writeNameValuePairs(Map<String, String> values, HttpServletResponse httpServletResponse) throws IOException {

        httpServletResponse.setContentType("text/plain");

        StringBuilder sb = new StringBuilder();

        if ((values != null) && (values.size() > 0)) {

            boolean first = true;

            for (String key : values.keySet()) {

                if (!first) {

                    sb.append(" ");

                }

                sb.append(key);
                sb.append(":");

                String value = values.get(key);
                if (value != null) {
                    sb.append(value);
                }

                first = false;

            }

        }

        httpServletResponse.getWriter().print(sb.toString());

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
            this.engine = engine;

        }

    }

}
