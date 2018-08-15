package life.qbic.service;

import life.qbic.cli.AbstractCommand;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.util.List;


/**
 * Abstraction of command-line arguments that will be passed to {@link NexusListenerService} at construction time.
 * 
 * 
 */
@Command(
   name="nexus-listener",
   description="Service that listenes for changes in the Nexus repository and updates the testing-portal with the new file(s).")
public class NexusListenerCommand extends AbstractCommand {

		@Option(names={"-p", "--port"}, description="Port on which this service will listen to requests.", required=true)
	    public int port;
		
		@Option(names={"-k", "--key"}, description="Secrete key which is used to create HMAC payload.", required=true)
	    public String key;

		@Option(names={"-u", "--url"}, description="Base repository URL.", required=true)
	    public String url;

		@Option(names={"--portletFolder"}, description="Folder on which portlets are copied.", required=true)
	    public String outPortlet;
		
		@Option(names={"--non-portletFolder"}, description="Folder on which non-portlets are copied.", required=true)
	    public String outNonPortlet;

	//	@Option(names={"-t", "--type"}, description="List of types of artifacts to deploy.", required=true)
	//	String firstArtifact;
	    @CommandLine.Parameters(arity = "1..*",description = "List of types of artifacts to deploy (e.g portlet)")
	    List<String> artifactType;

	
}