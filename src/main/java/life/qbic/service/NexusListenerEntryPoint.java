package life.qbic.service;

import life.qbic.cli.ToolExecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Entry point for the Nexus-Listener Service application.
 * 
 * The purpose of this class is to act as a bridge between the command line and the <i>real</i> implementation of a tool by using a {@link ToolExecutor}.
 */
public class nexus-listenerEntryPoint {

    private static final Logger LOG = LogManager.getLogger(nexus-listenerEntryPoint.class);

    /**
     * Main method.
     * 
     * @param args the command-line arguments.
     */
    public static void main(final String[] args) {
        LOG.debug("Starting nexus-listener tool");
        final ToolExecutor executor = new ToolExecutor();
        executor.invoke(nexus-listenerService.class, nexus-listenerCommand.class, args);
    }
}