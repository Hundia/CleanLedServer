package src.ledserver.udp;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.io.*; 

import ClientToServer.AddNewCommentRequest;
import ClientToServer.AdjustBrightness;
import ClientToServer.ChangeModeRequest;
import ClientToServer.ChangeModeRequest.Mode;
import ClientToServer.DeleteCommentRequest;
import ClientToServer.ClientToServerMessage;
import ClientToServer.ImageSwipeRequest;
import ClientToServer.ImageSwipeRequest.SwipeDirection;
import javafx.application.Platform;
import src.ledserver.LedFxManager;

public class ledServer implements Runnable
{ 
	DatagramSocket serverSocket = null;
	Socket clientSocket = null;
	LedFxManager mgr = null;
	int wait_for_scene_dur_in_mili;
	 public ledServer(int port,LedFxManager mgr, int wait_for_scene_dur_in_mili) {
		 this.mgr = mgr;
		 this.wait_for_scene_dur_in_mili = wait_for_scene_dur_in_mili;
	   }

	private void accept() {
	    
	    System.out.println ("Waiting for input.....");	    
	    objectReceive();

	}
	
	@Override
	public void run() {
	   	
		System.out.println ("Waiting for connection.....");

		try { 
	   		serverSocket = new DatagramSocket(10007); 
	    } 
	    catch (IOException e) 
        { 
         System.err.println("Could not listen on port: 10007."); 
         System.exit(1); 
        }
	    System.out.println ("Connection successful");

		while(true) {
			accept();	
		}	    
		
	}

//	private void Receive() {
//		
//		byte[] receivedData = new byte[1024];
//		DatagramPacket ReceivePacket = new DatagramPacket(receivedData,receivedData.length);
//		try {
//			serverSocket.setSoTimeout(wait_for_scene_dur_in_mili);
//			serverSocket.receive(ReceivePacket);
//		}
//		catch (SocketTimeoutException e) {
//            // timeout exception.
//            System.out.println("Timeout reached!!! turning on scene" + e);
//            handleChangeMode(Mode.SCENEMATIC, 0);
//           // serverSocket.close();
//            return;
//        }
//		catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	    ByteBuffer bf = ByteBuffer.wrap(ReceivePacket.getData()); 
//	
//	    //	API: Command type: 
//	    //	0 == Change Mode, 1 == Swiping, 2 == idle, 3 == add text, 4 == delete comment
//	    int cmdType = bf.getInt();
//	    int actualCommand = bf.getInt();
//	    final int screen = bf.getInt();
//	    if(1 == cmdType) {
//	    		handleSwipe(actualCommand, screen);	
//	    }
//	    else if(0 == cmdType) {
//	    		handleChangeMode(actualCommand, screen);
//	    }
//	    else if(3 == cmdType) {
//	    	byte[] arr = new byte[bf.remaining()];
//	    	bf.get(arr,0,bf.remaining());
//	    	String s = null;
//			try {
//				s = new String(arr,"UTF-8");
//			} catch (UnsupportedEncodingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			handleAddComment2Pic(screen,s);
//	    }
//	    else if(4 == cmdType) {
//	    		handleDeleteCommentForCurrentPic(screen);
//	    }
//	   // serverSocket.close(); 
//	}

	private void objectReceive() {
		
		byte[] receivedData = new byte[1024];
		DatagramPacket ReceivePacket = new DatagramPacket(receivedData,receivedData.length);
		try {
			serverSocket.setSoTimeout(wait_for_scene_dur_in_mili);
			serverSocket.receive(ReceivePacket);
		}
		catch (SocketTimeoutException e) {
            // timeout exception.
            System.out.println("Timeout reached!!! turning on scene" + e);
            handleChangeMode(Mode.SCENEMATIC, 0);
           // serverSocket.close();
            return;
        }
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ByteArrayInputStream in = new ByteArrayInputStream(ReceivePacket.getData());
        ObjectInputStream is = null;
		try {
			is = new ObjectInputStream(in);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        try {
        	//	Get the interface
        	ClientToServerMessage Message = (ClientToServerMessage) is.readObject();
            
        	//	Check for actual instance received
        	if(Message instanceof ChangeModeRequest) {
            		ChangeModeRequest req = (ChangeModeRequest) Message;
            		handleChangeMode(req.mode, req.screenId);	
            }
            
            else if(Message instanceof AddNewCommentRequest) {
            	AddNewCommentRequest req = (AddNewCommentRequest) Message;
            	String s = null;
        		try {
        			s = new String(req.comment.getBytes(),"UTF-8");
        		} catch (UnsupportedEncodingException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		handleAddComment2Pic(req.screenId,s);            
        	}
            
        	//	Swipe request
            else if(Message instanceof ImageSwipeRequest) {
            	ImageSwipeRequest req = (ImageSwipeRequest) Message;
        		String s = null;
    			
    			handleSwipe(req.swipe, req.screenId);            
    		}
        	
        	//	Delete comment request
            else if(Message instanceof DeleteCommentRequest) {
            	DeleteCommentRequest req = (DeleteCommentRequest) Message;
        		String s = null;
    			
        		handleDeleteCommentForCurrentPic(req.screenId);            
    		}
        	
        	//	Adjust brightness -  AdjustBrightness
            else if(Message instanceof AdjustBrightness) {
            	AdjustBrightness req = (AdjustBrightness) Message;
        		
            	mgr.setBrightnessLevel(req.screenId, req.brightnessLevel);
    		}
        		
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        } 
	}

	
	private void handleDeleteCommentForCurrentPic(int screen) {
		mgr.handleDeleteCommentForCurrentPic(screen);
		
	}

	private void handleChangeMode(Mode actualCommand, int screen) {
		System.out.println ("Change Mod: " + actualCommand + " Screen: " + screen); 
		int mode = 0;
		if(actualCommand == Mode.ALBUM) 
			mode = 1;
		else if (actualCommand == Mode.SCENEMATIC)
			mode = 2;
		mgr.replaceDisplayMode(screen,mode);
	}

	private void handleAddComment2Pic(int screen, String comment) {
		mgr.handleAddComment2Pic(screen,comment);
	}
	private void handleSwipe(SwipeDirection actualCommand, final int screen) {
		final String swipeDirection;
		if(actualCommand == SwipeDirection.LEFT)
			swipeDirection = "L";
		else
			swipeDirection = "R";
		
		System.out.println ("Swipe direction: " + swipeDirection + " Screen: " + screen); 
		 Platform.runLater(new Runnable() {
		      @Override
		      public void run() {
		    	  if (swipeDirection.equals("R")) {
		    		  mgr.SwipeRight(screen);  
		    	  }
				     
				     
				  if (swipeDirection.equals("L")) 
				     mgr.SwipeLeft(screen); 			          
				    }
		  });
	}
} 