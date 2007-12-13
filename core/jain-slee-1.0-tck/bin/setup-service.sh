#!/bin/sh
#
# Sets up a service in the SLEE
# Syntax: <script> [-t <trace-level>] <service-DU-URL1> [<service-DU-URL2>,...]
# e.g.: ./bin/setup-service.sh -t Info file:///home/myname/sbbs/fooDU.jar

# Validate current working directory
if [ ! -f bin/read-config-variables.sh ]; then
 echo "This script must be run from the root sleetck directory"; exit 1
fi
. bin/read-config-variables.sh

if [ -z "$VENDOR_LIB" ]; then
 echo "VENDOR_LIB variable not set. Please specify a value for VENDOR_LIB in config/config_variables.unix"; exit 1
fi

CLASSPATH=$SLEETCK_LIB:$TCKTOOLS_LIB:$SLEE_LIB:$JMX_LIB:$VENDOR_LIB

$JAVA_HOME/bin/java -classpath $CLASSPATH com.opencloud.sleetck.ext.sleemanagement.SetupService $*
        
