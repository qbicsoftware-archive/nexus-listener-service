package life.qbic.service;

import life.qbic.cli.QBiCTool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implementation of Nexus-Listener Service. Its command-line arguments are contained in instances of {@link nexus-listenerCommand}.
 */
public class nexus-listenerService extends QBiCTool<nexus-listenerCommand> {

    private static final Logger LOG = LogManager.getLogger(nexus-listenerService.class);

    /**
     * Constructor.
     * 
     * @param command an object that represents the parsed command-line arguments.
     */
    public nexus-listenerService(final nexus-listenerCommand command) {
        super(command);
    }

    @Override
    public void execute() {
        // get the parsed command-line arguments
        final nexus-listenerCommand command = super.getCommand();

        // TODO: do something useful with the obtained command.
        //
    }

    @Override
    public void shutdown() {
        // TODO: perform clean-up tasks
        // Important: do not call System.exit. This method is executed by a "shutdown hook thread"
        //            See: https://docs.oracle.com/javase/8/docs/api/java/lang/Runtime.html#exit-int-
    }
}