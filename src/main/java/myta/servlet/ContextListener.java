package myta.servlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import myta.config.model.EngineConfig;
import myta.config.model.SmtpConfiguration;
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

        EngineConfig engineConfig = new EngineConfig();

        Map<String, String> env = System.getenv();

        if (env.containsKey("NUM_WORKERS") && (env.get("NUM_WORKERS") != null)) {

            if (env.get("NUM_WORKERS").toString().matches("[1-9]{1}[0-9]{0,}")) {

                engineConfig.setNumWorkers(Integer.valueOf(env.get("NUM_WORKERS")));

            }

        }

        if (env.containsKey("INCOMING_QUEUE_SIZE") && (env.get("INCOMING_QUEUE_SIZE") != null)) {

            if (env.get("INCOMING_QUEUE_SIZE").toString().matches("[1-9]{1}[0-9]{0,}")) {

                engineConfig.setIncomingMessageQueueSize(Integer.valueOf(env.get("INCOMING_QUEUE_SIZE")));

            }

        }

        if (env.containsKey("RELAY_HOST") && (env.get("RELAY_HOST") != null) && !env.get("RELAY_HOST").equals("")) {

            String servers[] = env.get("RELAY_HOST").replaceAll(" ", "").split(",");

            List<SmtpConfiguration> relayServers = new ArrayList<SmtpConfiguration>(servers.length);

            for (String server : servers) {

                server = server.trim();

                if (!server.equals("")) {

                    String[] serverParts = server.split(":");

                    String serverName = serverParts[0];

                    SmtpConfiguration relayServer = new SmtpConfiguration(serverName);
                    relayServers.add(relayServer);

                }

            }

            if (relayServers.size() > 0) {
                engineConfig.setRelayServers(relayServers);
            }

        }

        Engine engine = new Engine();
        engine.initialize(engineConfig);

        servletContext.setAttribute("engine", engine);

    }
}
