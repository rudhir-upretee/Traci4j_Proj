package gluesim.core;

import it.polito.appeal.traci.test.TraCIServerTest;
import java.lang.Exception;

public class VehicleStatus
	{
	public static void main(String[] args)
		{
		TraCIServerTest traciServer = new TraCIServerTest();

		try
			{
			traciServer.setUp();
			traciServer.testGetVersionLowLevel();
			traciServer.tearDown();
			}
		catch(Exception e)
			{
			e.printStackTrace();
			}
		}
	}