package life.qbic.service;

import life.qbic.cli.AbstractCommand;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Abstraction of command-line arguments that will be passed to {@link nexus-listenerService} at construction time.
 * 
 * 
 */
@Command(
   name="nexus-listener",
   description="Service that listenes for changes in the Nexus repository and updates the testing-portal with the new file(s).")
public class nexus-listenerCommand extends AbstractCommand {
    // TODO: add your command-line options as members of this class using picocli's annotations, for instance:
    //
    // @Option(names={"-u", "--url"}, description="openBIS server URL.", required=true)
    // String url;
    //
    // using package access level for these members will allow you access them within your main and test classes
    //
    // IMPORTANT: Typically you won't require a fancy constructor, but if you do, you must know that
    //            ToolExecutor requires that all command classes contain a public constructor that takes no arguments.
    //
    //            If you need a custom constructor, make sure to provide a no-arguments public constructor as well.
    //            See: https://docs.oracle.com/javase/tutorial/java/javaOO/constructors.html
	
//	@Option(names={"-c", "--config"}, description="Config file as input?", required=true)
//    public boolean config;
//	
//	//if config is input then just handle this file, else parse each parameter individually
//	if(config) {
//		@Option(names={"-i", "--input"}, description="'Input configuration file which contains all parameters. The file requires \n -p  port"
//				+ "\n -k secret key \n -u Base repository URL \n -t artifact type \n -o output folder", required=true)
//	    public String con_file;
//	}
		@Option(names={"-p", "--port"}, description="Port on which this service will listen to requests.", required=true)
	    public int port;
		
		@Option(names={"-k", "--key"}, description="Secrete key which is used to create HMAC payload.", required=true)
	    public int key;
		
		@Option(names={"-u", "--url"}, description="Base repository URL.", required=true)
	    public String url;
		
		@Option(names={"-t", "--type"}, description="Type of artifact to deploy.", required=true)
	    public String artifact_type;
	
//		@Option(names={"-op", "--out-portlet"}, description="Folder on which portlets are copied.", required=true)
//	    public String out_portlet;
		
		@Option(names={"-o", "--out"}, description="Folder on which artifacts are copied.", required=true)
	    public String out;

	
}