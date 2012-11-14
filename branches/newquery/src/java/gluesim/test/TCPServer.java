package gluesim.test;

import java.io.*;
import java.net.*;

public class TCPServer{
    private ServerSocket serverSocket;
    private Socket connection = null;
    private BufferedReader rd;
    private BufferedWriter wr;
    private int serverPort;

    public TCPServer(int port)
    {
    	serverPort = port;
    }

    public void start()
    {
        try{
	        serverSocket = new ServerSocket(serverPort);
			System.out.println("Waiting for connection");
			connection = serverSocket.accept();
			System.out.println("Connection received from " 
								+ connection.getInetAddress().getHostName());
			wr = new BufferedWriter(new OutputStreamWriter
									(connection.getOutputStream()));
			rd = new BufferedReader(new InputStreamReader
									(connection.getInputStream()));
	     
        }catch(Exception e){
            e.printStackTrace();
            stop();
            System.exit(1);
         }
    }
    
    public void stop()
    {
        try{
            rd.close();
            wr.close();
            serverSocket.close();
        }
        catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }
    
	public String recvMessage()
    {
    	String message = "";
    	
    	try {
	        message = rd.readLine();
        
    	} catch(IOException ioe) {
    		ioe.printStackTrace();
    		message = "";
    		
    	} 
        return message;
    }
    
    public void sendMessage(String msg)
    {
        try{
		    wr.write(msg);
		    wr.flush();
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }
    
    public static void main(String args[])
    {
    	TCPServer server = new TCPServer(2004);
        String msg = "";
        
        // Run Echo server forever
        while(true){
        	
        	// Disconnect the client that sends "bye". Look for another
        	// connection
        	server.start();
        	do{
                msg = server.recvMessage();
                if(msg == null)
                	break;	
                System.out.println("Received from client : " + msg);
                server.sendMessage(msg);
            }while(!msg.equals("bye"));
        	server.stop();
        }
    }
}
