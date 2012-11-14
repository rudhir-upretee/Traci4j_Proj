package gluesim.test;

import static org.junit.Assert.*;

import it.polito.appeal.traci.protocol.Command;
import it.polito.appeal.traci.protocol.Constants;
import it.polito.appeal.traci.protocol.RequestMessage;
import it.polito.appeal.traci.protocol.ResponseMessage;
import it.polito.appeal.traci.protocol.ResponseContainer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Here we verify some assumptions about the TraCI protocol and the behaviour of
 * SUMO.
 * 
 * @author Enrico Gueli &lt;enrico.gueli@polito.it&gt;
 *
 */
public class TraCIServerTest {
	
	private static final int API_VERSION = 3;

	/**
	 * The system property name to get the executable path and name to run.
	 */
	public static final String SUMO_EXE_PROPERTY = "it.polito.appeal.traci.sumo_exe";

	public static final int TRACI_PORT = 15000;
	
	private DataInputStream inStream;
	private DataOutputStream outStream;
	private Socket socket;

	private Process sumoProcess;
	
	@Before
	public void setUp() throws UnknownHostException, IOException, InterruptedException {
		
		runSUMO();
		
		Thread.sleep(1000);
		
		socket = new Socket();
		socket.connect(new InetSocketAddress(InetAddress.getLocalHost(), TRACI_PORT));
		
		inStream = new DataInputStream(socket.getInputStream());
		outStream = new DataOutputStream(socket.getOutputStream());
	}

	private void runSUMO() throws IOException {
		final String sumoEXE = System.getProperty(SUMO_EXE_PROPERTY);
		if (sumoEXE == null)
			throw new RuntimeException("System property " + SUMO_EXE_PROPERTY
					+ " must be set");

		String cfgPath = new String("/home/rudhir/Software/Simulator/traci4j/"+
				"branches/newquery/src/java/gluesim/sumocfg/quickstart.sumocfg");
		String[] args;
		args = new String[] { 
				sumoEXE, 
				"-c", cfgPath, 
				"--remote-port", Integer.toString(TRACI_PORT)
				};

		sumoProcess = Runtime.getRuntime().exec(args);

	}
	
	@After
	public void tearDown() throws IOException, InterruptedException {
		inStream.close();
		outStream.close();
		sumoProcess.waitFor();
	}
	
	@Test
	public void testJustConnect() {
		
	}
	
	@Test
	public void testGetVersionLowLevel() throws IOException {
		outStream.writeInt(6); // msg length
		outStream.writeByte(2); // cmd length
		outStream.writeByte(Constants.CMD_GETVERSION);

		
		int msgLength = inStream.readInt();
		System.out.println("message length = " + msgLength);
		assertTrue("minimum message length", msgLength >= 21);

		/*
		byte[] all = new byte[msgLength - 4];
		inStream.readFully(all);
		System.out.println(Arrays.toString(all));
		fail();
		*/
		
		testVersionResponseLowLevel();
	}

	private void testVersionResponseLowLevel() throws IOException {
		
		assertEquals("status resp len", 7, inStream.readByte());
		assertEquals("status resp ID", Constants.CMD_GETVERSION, inStream.readByte());
		assertEquals("status code OK", 0, inStream.readByte());
		assertEquals("status descr empty", 0, inStream.readInt());
		
		int respLen = inStream.readByte();
		System.out.println("response length = " + respLen);
		assertTrue("minimum response length", respLen > 5);
		assertEquals("Response ID", Constants.CMD_GETVERSION, inStream.readByte());
		assertEquals("API version", API_VERSION, inStream.readInt());
		int nameLen = inStream.readInt();
		assertEquals(1 + 1 + 4 + 4 + nameLen, respLen);
		byte[] name = new byte[nameLen];
		inStream.readFully(name);
		System.out.println("Version name: \"" + new String(name) + "\"");
	}

	@Test
	public void testTwoGetVersion() throws IOException {
		outStream.writeInt(8); // msg length
		outStream.writeByte(2); // cmd length
		outStream.writeByte(Constants.CMD_GETVERSION);
		outStream.writeByte(2); // cmd length
		outStream.writeByte(Constants.CMD_GETVERSION);

		
		int msgLength = inStream.readInt();
		System.out.println("message length = " + msgLength);
		assertTrue("minimum message length", msgLength > 21);

		/*
		byte[] all = new byte[msgLength - 4];
		inStream.readFully(all);
		System.out.println(Arrays.toString(all));
		fail();
		*/

		testVersionResponseLowLevel();
		testVersionResponseLowLevel();
	}

	@Test
	public void testLongMessageLowLevel() throws IOException {
		final int REPETITIONS = 30;
		
		outStream.writeInt(4 + 2 * REPETITIONS);
		for (int i=0; i<REPETITIONS; i++) {
			outStream.writeByte(2); // cmd length
			outStream.writeByte(Constants.CMD_GETVERSION);
		}
		
		int msgLength = inStream.readInt();
		System.out.println("message length = " + msgLength);
		assertTrue("minimum message length", msgLength > 255);

		for (int i=0; i<REPETITIONS; i++) {
			testVersionResponseLowLevel();
		}
	}
	
	@Test
	public void testGetVersionHighLevel() throws IOException {
		RequestMessage reqm = new RequestMessage();
		reqm.append(new Command(Constants.CMD_GETVERSION));
		reqm.writeTo(outStream);
		
		ResponseMessage respm = new ResponseMessage(inStream);
		assertEquals(1, respm.responses().size());
		
		ResponseContainer pair = respm.responses().get(0);
		assertEquals(Constants.CMD_GETVERSION, pair.getStatus().id());
		
		Command resp = pair.getResponse();
		assertEquals(Constants.CMD_GETVERSION, resp.id());
		assertEquals(API_VERSION, resp.content().readInt());
		System.out.println(resp.content().readStringASCII());
	}
	
	@Test
	public void testCloseHighLevel() throws IOException {
		RequestMessage reqm = new RequestMessage();
		reqm.append(new Command(Constants.CMD_CLOSE));
		reqm.writeTo(outStream);
		
		ResponseMessage respm = new ResponseMessage(inStream);
		assertEquals(1, respm.responses().size());
		
		ResponseContainer pair = respm.responses().get(0);
		assertEquals(Constants.CMD_CLOSE, pair.getStatus().id());
		assertEquals("Goodbye", pair.getStatus().description()); // undocumented!
		
		assertNull(pair.getResponse());
	}
	
	public static void main(String[] args)
	{
		TraCIServerTest traciServer = new TraCIServerTest();
		
		try {
			traciServer.setUp();
			traciServer.testGetVersionLowLevel();
			traciServer.tearDown();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
