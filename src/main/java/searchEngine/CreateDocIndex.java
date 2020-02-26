package searchEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;;

public class CreateDocIndex {
	
	public void indexDocs(IndexWriter writer, Path docs)
	{
		try {
			//Read the file from given path
			InputStream stream = Files.newInputStream(docs);
			BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
		    
			String currentLine = br.readLine();
		    String docLine = "";
		    Document document = null;
		    String docType = "";
		    while(currentLine != null){
		    	
		    	//For adding a single document 
		        if(currentLine.matches("(\\.I)( )(\\d)*")){
			      document = new Document();
			      
			      // Store document number as string field
		          Field pathField = new StringField("path", currentLine, Field.Store.YES);
		          document.add(pathField);
		          currentLine = br.readLine();
		          while(!(currentLine.matches("(\\.I)( )(\\d)*"))){	
		        	  
		        	//Store the whole title as text field
		            if(currentLine.matches("(\\.T)")){
		              docType = "Title";
		              while(!currentLine.matches("(\\.A)")) {
		            	  currentLine = br.readLine();
		            	  if(!currentLine.matches("(\\.A)"))
		            		  docLine = docLine.concat(" "+currentLine);
		              }		              
		            } 
		            
		            //Store the whole author name as text field
		            else if(currentLine.matches("(\\.A)")){
		              docType = "Author";
		              while(!currentLine.matches("(\\.B)")) {
		            	  currentLine = br.readLine();
		            	  if(!currentLine.matches("(\\.B)"))
		            		  docLine = docLine.concat(" "+currentLine);
			              }
		            } 
		            
		            //Store the whole content as text field
		            else if(currentLine.matches("(\\.W)")){
		              docType = "Words";
		              while(currentLine!=null && !currentLine.matches("(\\.I)( )(\\d)*") ) {
		            	  currentLine = br.readLine();
		            	  if(currentLine == null)
		            		  break;
		            	  else if(!currentLine.matches("(\\.I)( )(\\d)*"))
		            		  docLine = docLine.concat(" "+currentLine);
			              }
		            } 
		            
		            //Store the whole bibliography as text field
		            else if(currentLine.matches("(\\.B)")){
		              docType = "Bibliography";
		              while(!currentLine.matches("(\\.W)")) {
		            	  currentLine = br.readLine();
		            	  if(!currentLine.matches("(\\.W)"))
		            		  docLine = docLine.concat(" "+currentLine);
			              }
		            }
		            
		            document.add(new TextField(docType, docLine, Field.Store.YES));
		            docLine="";
		            if(currentLine == null){
		              break;
		            }
		          }
		        }
		        writer.addDocument(document);
		    }
		    
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
