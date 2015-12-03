package src.ledserver;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import src.ledserver.comments.CommentsManager;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class imageListContainer {

    public ArrayList<MediaContainer> imgListForBack = new ArrayList<MediaContainer>();
    public ArrayList<MediaContainer> imgListForTakingPictures = new ArrayList<MediaContainer>();
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
	
	public imageListContainer(int stripWidth, int stripHeight) {
		this.stripWidth = stripWidth;
		this.stripHeight = stripHeight;
	}

	public void init(String backImgDir, String takingPicImgDir, String scenematicDir) {
		loadListOfImageFiles(backImgDir,imgListForBack);
		loadListOfImageFiles(takingPicImgDir,imgListForTakingPictures);
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

	         }
	         else {
	        	 MediaContainer mc = new MediaContainer(MediaContainer.MediaType.IMAGE, new Image(f.toURI().toString()));
	             // you probably want something more involved here
	             // to display in your UI
	   	         System.out.println("image: " + f.getName());
	   	         imgList.add(mc);
	   	         commentsForMedia.put(mc,new CommentsManager(f.getName()));
	   	         System.out.println(" width : " + mc.image.getWidth());
	             System.out.println(" height: " + mc.image.getHeight());
	             System.out.println(" size  : " + f.length());
	         }
         }
        	 
     }
     
	return true;
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
				case 1: activeList = imgListForTakingPictures;
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
		if(activeList != imgListForTakingPictures) 
			return null;
		String comment = commentsForMedia.get(activeList.get(index)).getNextComment();
		System.out.println("Getting string for display: " + comment);
		return comment;
	}

	public void removeCommentsForActivePic() {
		if(activeList != imgListForTakingPictures) 
			return;
		//commentsForMedia.get(activeList.get(index)).clearAllComments();
	}
}
