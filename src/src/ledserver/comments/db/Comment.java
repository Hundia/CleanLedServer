package src.ledserver.comments.db;

public class Comment {

	public int commentId = -1;
	public String commentText;
	
	public Comment(int id, String txt) {
		this.commentId = id;
		this.commentText = txt;
	}
}
