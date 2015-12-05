package src.ledserver.comments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import src.ledserver.LedFxManager;
import src.ledserver.comments.db.CommentsDb;
import src.ledserver.comments.db.ICommentsDbAPI;

public class CommentsManager {
	
	private ArrayList<String> commentsList = new ArrayList<String>();
	//	Used for rounding around the comments for display
	private int displayIndex = 0;
	private int commentId = 0;
	private String imageName;
	private HashMap<Integer, Integer> indexToCommentId = new HashMap<Integer, Integer>();
	
	public CommentsManager(String imageName) {
		this.imageName = imageName;
	}

	public synchronized String getNextComment() {
		if(commentsList.size() == 0) {
			return null;
		}
		String retVal = commentsList.get(displayIndex);
		System.out.println("Comments: Getting index: " + displayIndex);
		displayIndex = (displayIndex + 1) % commentsList.size();
		System.out.println("Comments: index after update: " + displayIndex);
		System.out.println("Comments: Returning Str " + retVal);
		
		return retVal;
	}
	
	public synchronized void addNewComment(String comment) {
		//	Add to the actual list
		commentsList.add(displayIndex, comment);

		//	Add to the XML DB
		LedFxManager.CommentsDb.addNewComment(imageName, commentId, comment);
		indexToCommentId.put(displayIndex, commentId);
		commentId++;
		displayIndex++;
	}

	/**
	 * 	Called upon loading of the application
	 * @param comment the comment string
	 * @param id the id of the comment from the DB
	 */
	public synchronized void loadCommentFromDb(String comment, int id) {
		//	Add to the actual list
		commentsList.add(displayIndex, comment);
		displayIndex++;
		//	Add to the mapper
		indexToCommentId.put(displayIndex, id);
		
		//	Set the maximum ID as the starting ID to avoid ID collisions
		//	due to the nature of the incrementation of the commentID
		if(id > commentId)
			commentId = id;
	}
	
	public void doneLoadingFromDb() {
		displayIndex = 0;
	}
	
	/**
	 * Deletes a specific comment from the list and the DB
	 * @param commentId to delete
	 * @return true of successful
	 */
	public boolean deleteSpecificComment(int commentId) {
		//	Remove from active display list
		commentsList.remove(indexToCommentId.get(commentId));
		//	Remove from mapping of index in display list to actual ID
		indexToCommentId.remove(commentId);
		//	Return result from removing from DB
		return LedFxManager.CommentsDb.removeExistingComment(imageName, commentId);
	}

}
