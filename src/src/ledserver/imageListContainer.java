package src.ledserver;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import src.ledserver.MediaContainer.MediaType;
import src.ledserver.comments.CommentsManager;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class imageListContainer {

    public ArrayList<MediaContainer> imgListForBack = new ArrayList<MediaContainer>();
    public ArrayList<MediaContainer> imgListForAlbum = new ArrayList<MediaContainer>();
    public ArrayList<MediaContainer> imgListForScenematic = new ArrayList<MediaContainer>();
    public HashMap<MediaContainer,CommentsManager> commentsForMedia = new HashMap<MediaContainer,CommentsManager>();
    public ArrayList<MediaContainer> activeList = null;
	private int index = 0;
	public ImageView mainImage;
	public MediaView mainMedia;
	public AnchorPane rootLayout;
	int stripWidth = 800; 
	int stripHeight = 600;
	private int currentMode = 2; // 2 == scenematic
	Logger logger = Logger.getLogger(imageListContainer.class);

	public imageListContainer(int stripWidth, int stripHeight) {
		this.stripWidth = stripWidth;
		this.stripHeight = stripHeight;
	}

	public void init(String backImgDir, String takingPicImgDir, String scenematicDir) {
		loadListOfImageFiles(backImgDir,imgListForBack);
		loadListOfImageFiles(takingPicImgDir,imgListForAlbum);
		loadListOfImageFiles(scenematicDir,imgListForScenematic);
		
    	// Load root layout from fxml file.
		rootLayout = new AnchorPane();
		mainImage = new ImageView();
		mainImage.setFitWidth(stripWidth);
		mainImage.setFitHeight(stripHeight);

		mainMedia = new MediaView();
		mainMedia.setFitWidth(stripWidth);
		mainMedia.setFitHeight(stripHeight);
		
	//	TODO: Make this feature generic
		activeList = imgListForScenematic;
		
		mainMedia.setMediaPlayer(((MediaContainer)activeList.get(0)).mediaPlayer);
		((MediaContainer)activeList.get(0)).mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
		((MediaContainer)activeList.get(0)).mediaPlayer.play();
		rootLayout.getChildren().add(mainMedia);
	}
	

	
    public void UpdateIndexPosition(boolean swipeLeft) {
    	if(swipeLeft) {
    		if(0 == index) {
    			index = activeList.size() - 1;
    		}
    		else {
    			index--;
    		}
    	}
    	else {
    		if(index == (activeList.size() - 1)) {
    			index = 0;
    		}
    		else {
    			index++;
    		}
    	}
    }
    
	public void SwipeRight() {
//		if(currentMode == 2) {
//			replaceDisplayMode(0);
//			return;
//		}
		UpdateIndexPosition(false);
		if(activeList.get(index).type == MediaContainer.MediaType.IMAGE) {
			mainImage.setImage(((MediaContainer)activeList.get(index)).image);
			if(!rootLayout.getChildren().get(0).equals(mainImage))
			{
				Platform.runLater(new Runnable() {
				    @Override
				    public void run() {
				    	rootLayout.getChildren().clear();
						rootLayout.getChildren().add(mainImage);				    
						}
				});
				
			}
		}
		//	Were playing a video
		else
		{
			mainMedia.setMediaPlayer(((MediaContainer)activeList.get(index)).mediaPlayer);
			((MediaContainer)activeList.get(index)).mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
			if(!rootLayout.getChildren().get(0).equals(mainMedia))
			{
				Platform.runLater(new Runnable() {
				    @Override
				    public void run() {
						rootLayout.getChildren().clear();
						rootLayout.getChildren().add(mainMedia);				    
						}
				});

			}
			else {
				mainMedia.getMediaPlayer().stop();
			}
			(((MediaContainer)activeList.get(index))).mediaPlayer.stop();
			new Thread(new Runnable(){
	             public void run(){
	                  try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	             }
	       }).run();
			(((MediaContainer)activeList.get(index))).mediaPlayer.play();
		}
		
	}
	
	public void SwipeLeft() {
//		if(currentMode == 2) {
//			replaceDisplayMode(0);
//			return;
//		}
		UpdateIndexPosition(true);
		if(activeList.get(index).type == MediaContainer.MediaType.IMAGE) {
			mainImage.setImage(((MediaContainer)activeList.get(index)).image);
			if(!rootLayout.getChildren().get(0).equals(mainImage))
			{
				Platform.runLater(new Runnable() {
				    @Override
				    public void run() {
				    	rootLayout.getChildren().clear();
						rootLayout.getChildren().add(mainImage);				    
						}
				});
				
			}
		}
		//	Were playing a video
		else
		{
			mainMedia.setMediaPlayer(((MediaContainer)activeList.get(index)).mediaPlayer);
			((MediaContainer)activeList.get(index)).mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
			if(!rootLayout.getChildren().get(0).equals(mainMedia))
			{
				Platform.runLater(new Runnable() {
				    @Override
				    public void run() {
				    	rootLayout.getChildren().clear();
						rootLayout.getChildren().add(mainMedia);				    
						}
				});
				
			}
			else {
				mainMedia.getMediaPlayer().stop();
			}
			(((MediaContainer)activeList.get(index))).mediaPlayer.stop();
			new Thread(new Runnable(){
	             public void run(){
	                  try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	             }
	       }).run();
			(((MediaContainer)activeList.get(index))).mediaPlayer.play();
		}
	}
	
    private boolean loadListOfImageFiles(String imgDir, ArrayList<MediaContainer> imgList) {
 	   // File representing the folder that you select using a FileChooser
     File dir = new File(imgDir);

     // array of supported extensions (use a List if you prefer)
     String[] EXTENSIONS_IMAGE = new String[]{
         "gif", "png", "bmp", "jpg", "JPG", "m4v", "mp4" // and other formats you need
     };
     
  // array of supported VIDEO extensions (use a List if you prefer)
     String[] EXTENSIONS_VIDEO = new String[]{
         "MOV", "AVI", "MPEG", "jpg", "JPG" // and other formats you need
     };
     // filter to identify images based on their extensions
     final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

         @Override
         public boolean accept(final File dir, final String name) {
             for (final String ext : EXTENSIONS_IMAGE) {
                 if (name.endsWith("." + ext)) {
                     return (true);
                 }
             }
             return (false);
         }
     };

     // filter to identify videos based on their extensions
     final FilenameFilter VIDEO_FILTER = new FilenameFilter() {

         @Override
         public boolean accept(final File dir, final String name) {
             for (final String ext : EXTENSIONS_VIDEO) {
                 if (name.endsWith("." + ext)) {
                     return (true);
                 }
             }
             return (false);
         }
     };
     
     Image img2 = null;
     Media media = null;
     if (dir.isDirectory()) { // make sure it's a directory
         for (final File f : dir.listFiles(IMAGE_FILTER)) {
	         if(f.toURI().toString().contains("m4v") || f.toURI().toString().contains("mp4")) {
	        	 	media = new Media(f.toURI().toString());
	        	 	MediaContainer mc = new MediaContainer(MediaContainer.MediaType.VIDEO, new MediaPlayer(media));
	   	        System.out.println("Video: " + f.getName());
	   	        
             // make the video conform to the size of the stage now...
//	   	        mediaPlayer.setFitWidth(stripWidth);
//	   	        mediaPlayer.setFitHeight(stripHeight);
	   	        mc.mediaPlayer.setMute(true);
	   	        imgList.add(mc);
	   	        
	   	        commentsForMedia.put(mc,new CommentsManager(f.getName()));
	   	        
	   	        //	Get comments for media
	   	        getCommentsForMedia(f, mc);
	         }
	         else {
	        	 MediaContainer mc = new MediaContainer(MediaContainer.MediaType.IMAGE, new Image(f.toURI().toString()));
	             // you probably want something more involved here
	             // to display in your UI
	   	         System.out.println("image: " + f.getName());
	   	        
	   	         imgList.add(mc);
	   	         commentsForMedia.put(mc,new CommentsManager(f.getName()));
	   	        
	   	         //	Get comments for media
	   	         getCommentsForMedia(f, mc);
	   	         
	   	         System.out.println(" width : " + mc.image.getWidth());
	             System.out.println(" height: " + mc.image.getHeight());
	             System.out.println(" size  : " + f.length());
	         }
         }
        	 
     }
     
	return true;
    }

	private void getCommentsForMedia(final File f, MediaContainer mc) {
		//	Get the existing comments from the DB
		 Map<Integer,String> commentMap = LedFxManager.CommentsDb.getExistingComments(f.getName());
		 
		 if(null != commentMap) {
			 //	Get an iterator for the map result
			 Iterator it = commentMap.entrySet().iterator();
			 while (it.hasNext()) {
				 Map.Entry<Integer,String> pair = (Map.Entry<Integer,String>)it.next();
				 if(commentsForMedia == null || mc == null) {
					 System.out.println("Here..!");
				 }
				 else if(pair == null || commentsForMedia.get(mc) == null) {
					 System.out.println("Here 2..!");
				 }
				 commentsForMedia.get(mc).loadCommentFromDb(pair.getValue(),pair.getKey());
			 }
			 
			 //	Finalize the loading with this call..!
			 commentsForMedia.get(mc).doneLoadingFromDb();
		 }
	}

    /**
     * Modes: 0 = back, 1 = pic, 2 = play scenematic
     * @param newMode
     */
	public void replaceDisplayMode(int newMode) {
		if(newMode != currentMode ) {
			switch (newMode) {
				case 0: activeList = imgListForBack;
					break;
				case 1: activeList = imgListForAlbum;
					break;
				case 2: activeList = imgListForScenematic;
					break;
			}
			index = 0;
			currentMode = newMode;
			System.out.println("Changing system mode: " + newMode);
			SwipeRight();
		}	
	}

	public void addTextToCurrentPic(String comment) {
		commentsForMedia.get(activeList.get(index)).addNewComment(comment);
	}
	
	public String getCommentForDisplay() {
		if(activeList != imgListForAlbum) 
			return null;
		String comment = commentsForMedia.get(activeList.get(index)).getNextComment();
		System.out.println("Getting string for display: " + comment);
		return comment;
	}

	public void removeCommentsForActivePic() {
		if(activeList != imgListForAlbum) 
			return;
		//commentsForMedia.get(activeList.get(index)).clearAllComments();
	}

	public void setImageBrightness(int level) {
		 ColorAdjust colorAdjust = new ColorAdjust();
		 float floatedLevel = (float) ((float)level*(float)0.01);
		 floatedLevel = -floatedLevel;
		 colorAdjust.setBrightness(floatedLevel);
		 logger.info("Requested to change brightness to: " + floatedLevel);
		 mainImage.setEffect(colorAdjust);
	}
}
