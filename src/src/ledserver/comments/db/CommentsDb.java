package src.ledserver.comments.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Text;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class CommentsDb implements ICommentsDbAPI {
	private FileWriter fw;
	private File xmlFile;
	private Document document;
	public String eventName;
	public int numberOfPictures;
	public int totalNumOfComments;
	Logger logger = Logger.getRootLogger();
	
	@Override
	public boolean addNewComment(String imageName, int CommentId, String Comment) {
		//	Get xml Elements
		Element rootNode = document.getRootElement();
		Element imagesNode = rootNode.getChild("images");
		
		//	First lets see if the image exists, if so add the comment to it..!
		List<Element> imageList = imagesNode.getChildren("image");
		
		int imageIndex = -1;
		for (int i = 0; i < imageList.size(); i++) {
			Element node = (Element) imageList.get(i);
			if(0 == node.getAttributeValue("name").compareTo(imageName)) {
				imageIndex = i;
				break;
			}
		}
		//	Prepare element for image
		Element image;
		//	Check result of search
		if(imageIndex > -1) {
			image = (Element) imageList.get(imageIndex);
		}
		else {
			image = new Element("image");
		}
		
		//	Prepare Elements
		Element comment = new Element("comment");
		Element commentId = new Element("comment_id");
		Element comment_string = new Element("comment_string");
		
		//	Set image name
		image.setAttribute("name",imageName);
		
		//	Set comment ID
		commentId.addContent(new Text(String.valueOf(CommentId)));
		
		//	Set comment text
		comment_string.addContent(new Text(Comment));
		
		//	Add id and text elements to the comment element
		comment.addContent(commentId);
		comment.addContent(comment_string);
		
		//	Add the comment to the image
		image.addContent(comment);
		
		//	If the image didnt exist, add it to the tree..!
		if(imageIndex == -1) {
			//	Add the image to the images list..!
			imagesNode.addContent(image);	
		}
		
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		
	    try {
			fw = new FileWriter(xmlFile);
		} catch (IOException e1) {
			System.out.println("Something went wrong while trying to write to the XML..");
			e1.printStackTrace();
			return false;
		}

	    
	    try {
	      outputter.output(document, fw);       
	    }
	    catch (IOException e) {
	    	e.printStackTrace();
	    	System.out.println("Something went wrong while trying to write to the XML..");
	    	return false;
	    }
	    
	    try {
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Something went wrong while trying to write to the XML..");
			return false;
		}
	    
		return true;
	}

	@Override
	public boolean removeExistingComment(String imageName, int CommentId) {
			//	Get xml Elements
			Element rootNode = document.getRootElement();
			Element imagesNode = rootNode.getChild("images");
			
			//	First lets see if the image exists, if so add the comment to it..!
			List<Element> imageList = imagesNode.getChildren("image");
			
			int imageIndex = -1;
			for (int i = 0; i < imageList.size(); i++) {
				Element node = (Element) imageList.get(i);
				if(0 == node.getAttributeValue("name").compareTo(imageName)) {
					imageIndex = i;
					break;
				}
			}
			//	Prepare element for image
			Element image;
			
			//	Check result of search
			if(imageIndex > -1) {
				image = (Element) imageList.get(imageIndex);
			}
			else {
				System.out.println("Image: " + imageName + " Does not exist in the db..");
				System.out.println("Canceling removal..");
				return false;
			}
			
			//	Now lets find the relevant comment
			List commentList = image.getChildren("comment");
			int commentIndex = -1;
			for (int j = 0; j < commentList.size(); j++) {	
			   Element commentNode = (Element) commentList.get(j);
			   if(0 == commentNode.getChildText("comment_id").compareTo(String.valueOf(CommentId))) {
				   commentIndex = j;
				   break;
				}
			}
			
			Element commentNode;
			
			//	Check result of search for corresponding comment
			if(commentIndex > -1) {
				commentNode = (Element) commentList.get(commentIndex);
			}
			else {
				System.out.println("Comment: " + CommentId + " Does not exist in the db for image: " +imageName);
				System.out.println("Canceling removal..");
				return false;
			}
			
			//	remove comment
			image.removeContent(commentNode);
			
			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
			
		    try {
				fw = new FileWriter(xmlFile);
			} catch (IOException e1) {
				System.out.println("Something went wrong while trying to write to the XML..");
				e1.printStackTrace();
				return false;
			}

		    
		    try {
		      outputter.output(document, fw);       
		    }
		    catch (IOException e) {
		    	e.printStackTrace();
		    	System.out.println("Something went wrong while trying to write to the XML..");
		    	return false;
		    }
		    
		    try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Something went wrong while trying to write to the XML..");
				return false;
			
			}
		    
		return true;
	}

	@Override
	public boolean init(String xmlFileName) {
		SAXBuilder builder = new SAXBuilder();
		xmlFile = new File(xmlFileName);
		File xsdFile = new File(getClass().getClassLoader().getResource("Comments_Per_Pic_Template.xsd").getPath());
		
		InputStream xmlStream = null;
		InputStream xsdStream = null;
		//	First off validate the XML
		try {
			xmlStream = new FileInputStream(xmlFile);
			xsdStream = new FileInputStream(xsdFile);
		} catch (FileNotFoundException e1) {
			logger.error("Failed to read XML / XSD FILE..!");
			e1.printStackTrace();
		}
		validateAgainstXSD(xmlStream, xsdStream);
		
		//	Now build the XML
		try {
			document = (Document) builder.build(xmlFile);
		} catch (JDOMException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	
	boolean validateAgainstXSD(InputStream xml, InputStream xsd)
	{
	    try
	    {
	        SchemaFactory factory = 
	            SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	        Schema schema = factory.newSchema(new StreamSource(xsd));
	        Validator validator = schema.newValidator();
	        validator.validate(new StreamSource(xml));
	        return true;
	    }
	    catch(Exception ex)
	    {
	    	ex.printStackTrace();
	        return false;
	    }
	}
	
	@Override
	public void printAllComments() {
		Element rootNode = document.getRootElement();
		Element imagesNode = rootNode.getChild("images");
		List imageList = imagesNode.getChildren("image");

		for (int i = 0; i < imageList.size(); i++) {
		
		   Element node = (Element) imageList.get(i);
		   System.out.println("Image Name : " + node.getAttributeValue("name"));
		   List commentList = node.getChildren("comment");
		   
		   for (int j = 0; j < commentList.size(); j++) {	
			   Element commentNode = (Element) commentList.get(j);
			   System.out.println("comment_id : " + commentNode.getChildText("comment_id"));
			   System.out.println("comment_string : " + commentNode.getChildText("comment_string"));
		   }
		}
	}

	@Override
	public List<Comment> getCommentsForImage(String imageName) {
		Element rootNode = document.getRootElement();
		Element imagesNode = rootNode.getChild("images");
		List imageList = imagesNode.getChildren("image");

		int imageIndex = -1;
		for (int i = 0; i < imageList.size(); i++) {
			Element node = (Element) imageList.get(i);
			if(0 == node.getAttributeValue("name").compareTo(imageName)) {
				imageIndex = i;
				break;
			}
		}
		//	Prepare element for image
		Element image;
		//	Check result of search
		if(imageIndex > -1) {
			image = (Element) imageList.get(imageIndex);
		}
		else {
			System.out.println("Image: " + imageName + " Does not exist in the db..");
			System.out.println("Canceling removal..");
			return null;
		}
		
		List commentList = image.getChildren("comment");
		List<Comment> commentInImage = new ArrayList<Comment>();
		for (int j = 0; j < commentList.size(); j++) {	
			Element commentNode = (Element) commentList.get(j);
			commentInImage.add(new Comment(Integer.valueOf(commentNode.getChildText("comment_id")),commentNode.getChildText("comment_string")));
		}
		   
		return commentInImage;
	}

	@Override
	public List<ImageElement> getAllImages() {
		Element rootNode = document.getRootElement();
		Element imagesNode = rootNode.getChild("images");
		List imageList = imagesNode.getChildren("image");
		List<ImageElement> list = new ArrayList<ImageElement>();
		int imageIndex = -1;
		for (int i = 0; i < imageList.size(); i++) {
			Element node = (Element) imageList.get(i);
			node.getAttributeValue("name");
			list.add(new ImageElement(node.getAttributeValue("name")
					, getCommentsForImage(node.getAttributeValue("name"))));
		}
		
		return list;
	}
	
	public static void main(String[] args) {
		CommentsDb db = new CommentsDb();
		db.init("/Users/elihundia/Development/LED/LedFx_Server/comments_db/comments_bat_mizva_21_11.xml");
		
//		db.removeExistingComment("Image5", 0);
//		db.printAllComments();
		//db.addNewComment("Image5", 1, "Test 3");
//		List<Comment> list = db.getCommentsForImage("Image5");
//		for (int j = 0; j < list.size(); j++) {	
//			System.out.println("Comment ID: " + list.get(j).commentId + " Comment: " + list.get(j).commentText);
//		}
		
		List<ImageElement> lst = db.getAllImages();
		for (int i = 0; i < lst.size(); i++) {
			System.out.println("Image Name: " + lst.get(i).imageName);
			for (int j = 0; j < lst.get(i).commentList.size(); j++) {	
				System.out.println("Comment ID: " + lst.get(i).commentList.get(j).commentId + " Comment: " + lst.get(i).commentList.get(j).commentText);
			}
		}
	}

	@Override
	public Map<Integer,String> getExistingComments(String name) {
		Map<Integer,String> mapOfExistingComments = new HashMap<Integer,String>();
		List<Comment> lst = getCommentsForImage(name);
		//	If the media has any comments..
		if(null != lst) {
			for(int i=0; i < lst.size(); i++) {
				mapOfExistingComments.put(lst.get(i).commentId, lst.get(i).commentText);
			}
			
			return mapOfExistingComments;	
		}
		
		return null;
		
	}


}
