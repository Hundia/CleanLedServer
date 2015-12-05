package src.ledserver.comments.db;

import java.util.List;
import java.util.Map;

public interface ICommentsDbAPI {

	/**
	 * Initialize the DB with a proper xml file
	 * @param xmlFileName file abs path
	 * @return false if the file does not exist or is bad schemed
	 */
	boolean init(String xmlFileName);
	/**
	 * Adds a new comment, if the image does not exist it adds it as a new one
	 * @param imageName Image name
	 * @param CommentId CommentId
	 * @param Comment Actual comment text
	 * @return false if the comment ID already exists..!
	 */
	boolean addNewComment(String imageName, int CommentId, String Comment);
	
	/**
	 * Assuming unique image names..!
	 * @param imageName Image Name
	 * @param CommentId Comment ID in the image to remove
	 * @return returns false if the comment doesn't exist or the image name doesnt exist
	 */
	boolean removeExistingComment(String imageName, int CommentId);
	
	/**
	 * Prints out all comments to console
	 */
	void printAllComments();
	
	/**
	 * Get List of comments for a given image
	 * @param imageName name of image
	 * @return List of comments in image
	 */
	List<Comment> getCommentsForImage(String imageName);
	
	/**
	 * Gets all the images with there comments
	 * @return image list with comments
	 */
	List<ImageElement> getAllImages();
	
	/**
	 * Gets existing Map of images for the given image name
	 * @param name image name to get the comments for
	 */
	Map<Integer, String> getExistingComments(String name);
}
