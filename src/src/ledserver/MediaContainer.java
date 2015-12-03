package src.ledserver;

import src.ledserver.MediaContainer.MediaType;
import javafx.scene.image.Image;
import javafx.scene.media.MediaPlayer;

public class MediaContainer {

	public MediaContainer(MediaType video, MediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;
		type = video;
	}
	
	public MediaContainer(MediaType video, Image image) {
		this.image = image;
		type = video;
	}
	
	public enum MediaType {
	    IMAGE, VIDEO
	}

	public Image image;
	public MediaPlayer mediaPlayer;
	public MediaType type;
	
}
