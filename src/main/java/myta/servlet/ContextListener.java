package myta.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import myta.service.IncomingMessageQueueManager;

public class ContextListener implements ServletContextListener {

    public void contextDestroyed(ServletContextEvent arg0) {
    }

    public void contextInitialized(ServletContextEvent servletContextEvent) {

        ServletContext servletContext = servletContextEvent.getServletContext();

        int queueSize = 1000;

        IncomingMessageQueueManager incomingMessageQueueManager = new IncomingMessageQueueManager(queueSize);

        servletContext.setAttribute("incomingMessageQueueManager", incomingMessageQueueManager);

    }
}
