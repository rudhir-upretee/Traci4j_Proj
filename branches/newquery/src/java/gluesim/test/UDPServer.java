package gluesim.test;

import java.io.*;
import java.net.*;

public class UDPServer{

   
    
    private DatagramSocket serverSocket;
    private int serverPort;
    private InetAddress clientIPAddress;
    private int clientPort;

    byte[] sendData; 

    public UDPServer(int port)
    {
    	serverPort = port;
    	clientIPAddress = null;
    	clientPort = -1;
    }

    public void start()
    {
        try{
        	System.out.println("Waiting for connection");
        	serverSocket = new DatagramSocket(serverPort); 
	     
        }catch(Exception e){
            e.printStackTrace();
            stop();
            System.exit(1);
        }
    }
    
    public void stop()
    {
        try{
        	serverPort = -1;
        	clientIPAddress = null;
        	clientPort = -1;
        	serverSocket.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    // This routine can be used in blocking or non-blocking mode.
    // For non-blocking mode, give the timeout in milliseconds.
	public String recvMessage(int timeout)
	{
		String msg = "";
    	
    	try {
    		byte[] receiveData = new byte[1024]; 
    		String data = "";
    		
    		DatagramPacket receivePacket = 
   	             new DatagramPacket(receiveData, receiveData.length); 

    		if(timeout != 0) {  
    			serverSocket.setSoTimeout(timeout); 
    		}
    		serverSocket.receive(receivePacket); 

    		clientIPAddress = receivePacket.getAddress(); 
    		clientPort = receivePacket.getPort();
    		data = new String(receivePacket.getData()); 
    		msg = trimCharsFromString(data, '\u0000');
         
    	} catch(SocketTimeoutException ste) {
    		msg = "";
    	} catch(IOException ioe) {
    		ioe.printStackTrace();
    		msg = "";
    	}
        return msg;
    }
    
    // This will remove the specified characters from the specified
    // string. This is useful in removing null characters from java strings.
	// Null characters in java string will make string routines like
	// equals, split etc to fail.
	public String trimCharsFromString(String in, char ch)
	{
		final StringBuilder sb = new StringBuilder();
	    for (final char character : in.toCharArray()) {
	    	if (character != ch) {
	    		sb.append(character);
	    	}
	    }  
	    return sb.toString();
	}
	
    public void sendMessage(String msg)
    {
        try{
        	byte[] sendData = new byte[1024]; 
        	sendData = msg.getBytes(); 
      	  
	        DatagramPacket sendPacket = 
	             new DatagramPacket(sendData, sendData.length, 
	            		 			clientIPAddress, clientPort); 
	  
	          serverSocket.send(sendPacket); 
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }
    
    public boolean isClientAddressValid()
    {
    	if((clientIPAddress != null) && (clientPort != -1)) {
    		return true;
    	}
    	return false;
    }
    
    // Test code
    public static void main(String args[])
    {
    	UDPServer server = new UDPServer(2004);
        String msg = "";
        
        // Run Echo server forever
        while(true){
        	
        	// Disconnect the client that sends "bye". Look for another
        	// connection
        	server.start();
        	do{
                msg = server.recvMessage(0);
                if(msg == null)
                	break;	
                System.out.println("Received from client : " + msg);
                server.sendMessage(msg);
            }while(!msg.equals("bye"));
        	server.stop();
        }
    }
}
