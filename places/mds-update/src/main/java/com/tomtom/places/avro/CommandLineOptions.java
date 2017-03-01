package com.tomtom.places.avro;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

public class CommandLineOptions {

    @Argument(index = 0, required = true, usage = "Path to input file containing feature IDs.")
    public String inputFile;

    @Option(name = "-u", required = true,
        usage = "LDAP user name for creating the transaction.")
    public String userName;

    @Option(name = "-c", required = true, usage = "Baseline name.")
    public String baselineName;

    @Option(name = "-b", required = true, usage = "Baseline version.")
    public String baselineVersion;

    @Option(name = "-cdb", required = true, usage = "URL of CoreDB web service.")
    public String coreDbUrl;

    @Option(name = "-ap", required = true, usage = "URL of access point web service.")
    public String accessPointUrl;

    @Option(name = "-cc", required = true, usage = "URL of commit controller web service.")
    public String commitControllerUrl;

    @Option(name = "-auth", required = true, usage = "URL of authserver web service.")
    public String authServerUrl;
}
