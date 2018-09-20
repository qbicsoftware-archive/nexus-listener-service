# Nexus-Listener Service

[![Build Status](https://travis-ci.com/qbicsoftware/nexus-listener-service.svg?branch=development)](https://travis-ci.com/qbicsoftware/nexus-listener-service)[![Code Coverage]( https://codecov.io/gh/qbicsoftware/nexus-listener-service/branch/development/graph/badge.svg)](https://codecov.io/gh/qbicsoftware/nexus-listener-service)

Nexus-Listener Service - Service that listens for changes in a Nexus repository, downloads and install components locally.

## Author
Created by Jennifer Boedker (jennifer.boedker@student.uni-tuebingen.de).

## Description

The Nexus-Listener Service provides an automatic download for changed artifacts within the Nexus repository.
For the repositories a Nexus-webhook was created which sends a `POST` request to this service.
If the artifact update is relevant (relevant artifact types are provided via command line), this service processes this request and triggers a download of the respective artifact file which is then copied to the specified directory.


## How to Install
This is offered as a `systemd` service. We include a service file (see `src/main/systemd/nexus-listener.service`).

## Usage
In the command line one needs to specify the following parameters:

	- `-p` or `--port`: Port on which this service will listen to requests.
	- `-u` or `--url`: Base repository URL for the Nexus repository
    - `-f` or `--portlet-folder`: Folder on which portlets are copied
    - `-o` or `--non-portlet-folder`: Folder on which non-portlets are copied
    - `-k` or `--key`:  Secrete key which is used to create HMAC payload

Desired artifact types (e.g., `portlet`, `service`) are provided as positional parameters. 

Here is the output of executing this tool with `--help` as parameter:

```
Usage: nexus-listener [-hv] -f=<outPortlet> -k=<key> -o=<outNonPortlet>
                      -p=<port> -u=<url> <artifactType>...
Service that listenes for changes in the Nexus repository and updates the
testing-portal with the new file(s).
      <artifactType>...   List of types of artifacts to deploy (e.g portlet)
  -f, --portlet-folder=<outPortlet>
                          Folder on which portlets are copied.
  -h, --help              Prints usage and exists.
  -k, --key=<key>         Secrete key which is used to create HMAC payload.
  -o, --non-portlet-folder=<outNonPortlet>
                          Folder on which non-portlets are copied.
  -p, --port=<port>       Port on which this service will listen to requests.
  -u, --url=<url>         Base repository URL.
  -v, --version           Prints version and exits.
```

e.g.: `java nexus-listener-service.jar -p 8080 -k 1234 -u https://maven.re.po -p deploy -o /home/user portlet service`