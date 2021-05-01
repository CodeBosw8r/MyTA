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

        IncomingMessageQueueManager incomingMessageQueueManager = new IncomingMessageQueueManager();

        servletContext.setAttribute("incomingMessageQueueManager", incomingMessageQueueManager);

    }
}
