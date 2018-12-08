# guacamole-simple-connection
Tomcat servlet to access through RDP to VMs using Guacamole.
The access is done sending the needed Guacamole parameters inside a JWT.

It can be run toghether with the [docker-guacamole](https://github.com/cperezprieto/docker-guacamole) project.

## Build
mvn package

## Run
Before adding this servlet to Tomcat the JWT_SECRET environment variable should be declared in the Guacamole server.
