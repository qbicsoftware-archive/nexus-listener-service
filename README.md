# Nexus-Listener Service

[![Build Status](https://travis-ci.com/qbicsoftware/nexus-listener-service.svg?branch=development)](https://travis-ci.com/qbicsoftware/nexus-listener-service)[![Code Coverage]( https://codecov.io/gh/qbicsoftware/nexus-listener-service/branch/development/graph/badge.svg)](https://codecov.io/gh/qbicsoftware/nexus-listener-service)

Nexus-Listener Service, version 1.0.0-SNAPSHOT - Service that listens for changes in the Nexus repository and updates the testing-portal with the new file(s).

## Author
Created by Jennifer Boedker (jennifer.boedker@student.uni-tuebingen.de).

## Description

The Nexus-Listener Service provides an automatic download for changed artifacts within the Nexus repository.
For the repositories a Nexus-webhook was created which sends a POST request to this service.
If the artifact update is relevant this request is then processed and triggers a download of the respective artifact file which is then uploaded to the specified directory.


## How to Install

TODO!!!!

## Run
In the commandline once needs to specify:

		**-p or --port:** Port on which this service will listen to requests

		**-k or --key:** Secrete key which is used to create HMAC payload

		**-u or -url:** Base repository URL

		**-t or --type:** List of types of artifacts to deploy

		**--portletFolder:** Folder on which portlets are copied

		**--non-portletFolder:** Folder on which non-portlets are copied

e.g.: java nexus-listener-service.jar -p 8080 -k 123456789 -u https://qbic-repo.am10.uni-tuebingen.de -t portlet --portletFolder /home/user --non-portletFolder /home/user

