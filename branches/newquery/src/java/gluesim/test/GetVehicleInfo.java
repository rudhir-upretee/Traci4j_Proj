package gluesim.test;

import it.polito.appeal.traci.SumoTraciConnection;
import it.polito.appeal.traci.Vehicle;

import java.util.Collection;

public class GetVehicleInfo {

	public static void main(String[] args) {
		
		String cfgPath = new String("/home/rudhir/Software/Simulator/traci4j/"+
									"branches/newquery/src/java/gluesim/sumocfg/quickstart.sumocfg");
		SumoTraciConnection conn = new SumoTraciConnection(
				cfgPath,                               // config file
				12345,                                 // random seed
				false                                  // look for geolocalization info in the map
				);
		try {
			conn.runServer();
			
			// the first two steps of this simulation have no vehicles.
			conn.nextSimStep();
			conn.nextSimStep();
			
			Collection<Vehicle> vehicles = conn.getVehicleRepository().getAll().values();

			Vehicle aVehicle = vehicles.iterator().next();
			
			System.out.println("Vehicle " + aVehicle
					+ " will traverse these edges: "
					+ aVehicle.queryReadCurrentRoute().get());
			conn.close();
		}		
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
