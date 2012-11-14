#!/bin/bash

LOG4J_JAR="/home/rudhir/Software/Simulator/traci4j/branches/newquery/lib/log4j.jar"
JUNIT_JAR="/home/rudhir/Software/Simulator/traci4j/branches/newquery/lib/junit.jar"
HAMCREST_JAR="/home/rudhir/Software/Simulator/traci4j/branches/newquery/lib/org.hamcrest.core_1.1.0.v20090501071000.jar"

if [ "$1" == 1 ]
then
	echo ">>>> GetVehicleInfo No GUI <<<<"
	java "-Dit.polito.appeal.traci.sumo_exe=/usr/local/bin/sumo" -cp ".:$LOG4J_JAR" gluesim.test.GetVehicleInfo
elif [ "$1" == 2 ]
then
	echo ">>>> GetVehicleInfo GUI <<<<"
	java "-Dit.polito.appeal.traci.sumo_exe=/usr/local/bin/sumo-gui" -cp ".:$LOG4J_JAR" gluesim.test.GetVehicleInfo
elif [ "$1" == 3 ]
then
	echo ">>>> TraCIServerTest No GUI <<<<"
	java "-Dit.polito.appeal.traci.sumo_exe=/usr/local/bin/sumo" -cp ".:$LOG4J_JAR:$JUNIT_JAR:$HAMCREST_JAR" gluesim.test.TraCIServerTest
elif [ "$1" == 4 ]
then
	echo ">>>> VehicleStatus No GUI <<<<"
	java "-Dit.polito.appeal.traci.sumo_exe=/usr/local/bin/sumo" -cp ".:$LOG4J_JAR" gluesim.test.VehicleStatus
elif [ "$1" == 5 ]
then
	echo ">>>> VehicleStatus GUI <<<<"
	java "-Dit.polito.appeal.traci.sumo_exe=/usr/local/bin/sumo-gui" -cp ".:$LOG4J_JAR" gluesim.test.VehicleStatus
else
	echo "USAGE ERROR : script [argument]"
fi
