package src.ledserver.comments.db;

import java.util.List;

public class ImageElement {

	public String imageName;
	public List<Comment> commentList;
	
	public ImageElement(String imageName, List<Comment> commentList) {
		this.imageName = imageName;
		this.commentList = commentList;
	}
}
