package myta.servlet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import myta.core.Engine;

public class ContextListener implements ServletContextListener {

    public void contextDestroyed(ServletContextEvent servletContextEvent) {

        ServletContext servletContext = servletContextEvent.getServletContext();

        Object engineObject = servletContext.getAttribute("engine");

        if ((engineObject != null) && (engineObject instanceof Engine)) {

            Engine engine = (Engine) engineObject;
            engine.shutdown();

        }

    }

    public void contextInitialized(ServletContextEvent servletContextEvent) {

        ServletContext servletContext = servletContextEvent.getServletContext();

        Map<String, String> params = new HashMap<String, String>();

        Map<String, String> env = System.getenv();

        if (env.containsKey("workers") && (env.get("workers") != null) && env.get("workers").toString().matches("[1-9]{1}[0-9]{0,}")) {

            params.put("numWorkers", env.get("workers").toString());

        }

        if (env.containsKey("queuesize") && (env.get("queuesize") != null) && env.get("queuesize").toString().matches("[1-9]{1}[0-9]{0,}")) {

            params.put("queueSize", env.get("queuesize").toString());

        }

        if (env.containsKey("relayhost") && (env.get("relayhost") != null) && !env.get("relayhost").equals("")) {

            params.put("relayHost", env.get("relayhost").toString());

        }

        Engine engine = new Engine();
        engine.initialize(params);

        servletContext.setAttribute("engine", engine);

    }
}
