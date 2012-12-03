package gluesim.test;

import it.polito.appeal.traci.ReadObjectVarQuery;
import it.polito.appeal.traci.SumoTraciConnection;
import it.polito.appeal.traci.Vehicle;
import it.polito.appeal.traci.ChangeMaxSpeedQuery;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;


public class VehicleStatus {
	
	private final int TRACI_NS3_SOCK_PORT = 2004;
	// Simulation step interval in SUMO in Milliseconds
	private final int SUMO_SIM_STEP_INTERVAL = 1000;
	
	private SumoTraciConnection conn;
	private String sumoCfgFile;
	//private TCPServer server;
	private UDPServer server;
	
	public VehicleStatus()
	{
		sumoCfgFile = new String("/home/rudhir/Software/Simulator/traci4j/"+
				"branches/newquery/src/java/gluesim/"+
				"sumocfg/quickstart.sumocfg");
		
		conn = new SumoTraciConnection(
				sumoCfgFile,          // sumo config file
				12345,                // random seed
				false                 // look for geolocalization info in the map
				);
		
		server = new UDPServer(TRACI_NS3_SOCK_PORT);
	}
	
	public void run()
	{
		try {
			conn.runServer();
			
			// Traci-ns3 connection server 
        	server.start();
        	int simSteps = 0;
        	while(simSteps++ < 150){
        		
        		// Default command that is always executed
        		//String cmdStr = "nil,SIMSTEP;";
        		String cmdStr = "";
 
        		// When the program is started, remote address is unknown.
        		// Wait indefinitely for a packet from the remote machine.
        		// After receiving packets from remote machine, the address
        		// structure of remote machine is filled. In other words, 
        		// the first packet from the remote machine will trigger 
        		// this while loop.
        		// 
        		// Once the while loop is triggered, message receive is 
        		// performed with timeout, just to check if there are any 
        		// commands sent from remote machine. Timeout should be
        		// small and should not delay the while loop.
        		if(!server.isClientAddressValid()) {
        			System.out.println("client addr not valid");
        			cmdStr = server.recvMessage(0);
        		} else {
        			System.out.println("client addr valid");
        			cmdStr = server.recvMessage(5000);
        			//cmdStr = server.recvMessage(0);
        		}
                if((cmdStr == null) || (cmdStr.equals("bye"))) {
                	break;	
                }
                System.out.println("Received from client : " + cmdStr);

                execAllCommands(cmdStr);
        	}
        	System.out.println("End of simulation");
        	server.stop();
        	conn.close();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void execAllCommands(String cmdStr)
	{
		StringTokenizer tok = new StringTokenizer(cmdStr, ";");

        while (tok.hasMoreElements()) {
        		
        	    String strToken = (String) tok.nextElement();
                System.out.println("Cmd : "+strToken);
                execCommand(strToken);
        }
	}
	
	private void execCommand(String cmd)
	{
		try {
			Vehicle vehicle = null;
			String objId = "", cmdId = "", cmdArg = "";
			StringTokenizer tok = new StringTokenizer(cmd, ",");
			
			// Fill object Id
			if(tok.hasMoreElements())
				objId = (String) tok.nextElement();
			
			// Fill command Id
			if(tok.hasMoreElements())
				cmdId = (String) tok.nextElement();
			
			// Fill command Arg
			if(tok.hasMoreElements())
				cmdArg = (String) tok.nextElement();
			
			System.out.println("objId: "+objId+" cmdId: "+cmdId);
			
			// Commands to execute
			if(cmdId.equals("SIMSTEP")) {
				// Advance SUMO simulation by one step
				System.out.println("exec SIMSTEP");
				conn.nextSimStep(SUMO_SIM_STEP_INTERVAL);
				
			} else if(cmdId.equals("SET_SPEED")) {
				System.out.println("exec SET_SPEED");
				vehicle = getVehicle(objId);
				ChangeMaxSpeedQuery maxSpeedQuery = vehicle.queryChangeMaxSpeed();
				maxSpeedQuery.setValue(Double.parseDouble(cmdArg));
				maxSpeedQuery.run();
				
			} else if(cmdId.equals("GET_STATUS")) {
				// Send status to remote machine
                String stat = getAllVehiclesStat();
                System.out.println("GET_STATUS : "+stat);
                server.sendMessage(stat);
                
			} else {
				System.out.println("Unknown command");
			}
		
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	

	private Vehicle getVehicle(String vehId)
	{
		Vehicle vehicle = null;
		
		try {
			// Get vehicle Id 
			Collection<Vehicle> vehicles = 
					conn.getVehicleRepository().getAll().values();
			Iterator<Vehicle> vehIter = vehicles.iterator();
			
			while(vehIter.hasNext()){
			    Vehicle aVehicle = (Vehicle)vehIter.next();
			    
				if(aVehicle != null) {
					if(aVehicle.getID().equals(vehId)) {
						vehicle = aVehicle;
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			vehicle = null;
		}
		return vehicle;
	}
	
	private String getAllVehiclesStat()
	{
		String strOutput = "";
		
		try {
			// Get vehicle Id and speed
			Collection<Vehicle> vehicles = 
					conn.getVehicleRepository().getAll().values();
			Iterator<Vehicle> vehIter = vehicles.iterator();
			
			while(vehIter.hasNext()){
			    Vehicle aVehicle = (Vehicle)vehIter.next();
			    
				if(aVehicle != null) {
					ReadObjectVarQuery<java.lang.Double> speedQuery = 
							aVehicle.queryReadSpeed();
					double speed = speedQuery.get();
					ReadObjectVarQuery<java.awt.geom.Point2D> pos2dQuery = 
							aVehicle.queryReadPosition();
					Point2D pos = pos2dQuery.get();
					double X = pos.getX();
					double Y = pos.getY();
//					strOutput += ((" VehicleId = "+aVehicle)+
//					             (" Speed = "+speedQuery.get())+
//					             (" Dist = "+dist)+
//					             ", ");
					 strOutput += (aVehicle+","+X+","+Y+","+speed+";");
		           
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			strOutput = "";
		}
		return strOutput;
	}
	
	public static void main(String[] args) 
	{
		//while(true) {
			VehicleStatus v = new VehicleStatus();
			v.run();
		//}
	}
}
