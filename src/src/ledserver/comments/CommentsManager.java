package src.ledserver.comments;

import java.util.ArrayList;
import java.util.HashMap;

import src.ledserver.LedFxManager;
import src.ledserver.comments.db.CommentsDb;
import src.ledserver.comments.db.ICommentsDbAPI;

public class CommentsManager {
	
	private ArrayList<String> commentsList = new ArrayList<String>();
	private int index = 0;
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
		String retVal = commentsList.get(index);
		System.out.println("Comments: Getting index: " + index);
		index = (index + 1) % commentsList.size();
		System.out.println("Comments: index after update: " + index);
		System.out.println("Comments: Returning Str " + retVal);
		
		return retVal;
	}
	
	public synchronized void addNewComment(String comment) {
		//	Add to the actual list
		commentsList.add(index, comment);

		//	Add to the XML DB
		LedFxManager.CommentsDb.addNewComment(imageName, commentId, comment);
		indexToCommentId.put(index, commentId);
		commentId++;
	}

	public boolean deleteSpecificComment(int commentId) {
		//	Remove from active display list
		commentsList.remove(indexToCommentId.get(commentId));
		//	Remove from mapping of index in display list to actual ID
		indexToCommentId.remove(commentId);
		//	Return result from removing from DB
		return LedFxManager.CommentsDb.removeExistingComment(imageName, commentId);
	}
}
