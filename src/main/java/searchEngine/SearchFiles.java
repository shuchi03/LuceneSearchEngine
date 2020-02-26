package searchEngine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SearchFiles {

	//Path where indexed document is stored
	private static String INDEX_DIRECTORY = "./index";
	
	//Number of results to show after searching
	private static int MAX_RESULTS = 1400;
	
	public static void main(String[] args) throws Exception {
		
		//Path where all documents are stored
		String docName = "./cran/cran.all.1400";
		Path docDir = Paths.get(docName);
				
		//Path where the indexed document will be stored
		Directory dir = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
				
		//Custom Analyzer used
		Analyzer analyzer = new CustomAnalyzer();
				
		//Create objects to write the indexed document
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		//Create mode used so that indexed documents will be written to avoid inaccurate querying
		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		IndexWriter writer = new IndexWriter(dir, config);
		
		CreateDocIndex cdi = new CreateDocIndex();
		cdi.indexDocs(writer,docDir);
		System.out.println("Document indexing completed");
		writer.close();
				
		//Path where queries are stored
		String fileName = "./cran/cran.qry";
		
		Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
		
		//Path where score with document number against the relevant query will be stored
		String queryWriteDoc = "./queryResult";
		Path queryDir = Paths.get(fileName);
		
		//Create objects to read and search across the index
		DirectoryReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);
		
		//isearcher.setSimilarity(new ClassicSimilarity());
		isearcher.setSimilarity(new BM25Similarity());
		
		//Query parser to parse across four fields
		MultiFieldQueryParser parser = new MultiFieldQueryParser (new String[]{"Title", "Author", "Bibliography", "Words"},analyzer);
		querySearch(isearcher, parser,queryDir,queryWriteDoc);
		System.out.println("Query search completed");
		//close all resources when searching is done
		ireader.close();
		directory.close();
		writer.close();

	}
	public static void querySearch(IndexSearcher isearcher, QueryParser parser,Path queryDoc, String queryWriteDoc) throws Exception
	{
		//Read the file from given path
		InputStream stream = Files.newInputStream(queryDoc);
		BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
		
		//Write file at given path
		BufferedWriter writer = new BufferedWriter(new FileWriter(queryWriteDoc));

		try
		{
			String currentLine = br.readLine();
			
			//Used for storing query number
		    int count=1;
		    
		    while(currentLine != null){
			    String queryLine="";
			    
			    //To read and parse only the relevant lines of query
		        if(currentLine.matches("(\\.I)( )(\\d)*")){
		        	currentLine = br.readLine();
		        	while(!currentLine.matches("(\\.I)( )(\\d)*"))
		        	{
		        		currentLine = br.readLine();
			        	if(currentLine == null)
			        		break;
			        	else if(!currentLine.matches("(\\.I)( )(\\d)*"))
			        		queryLine=queryLine.concat(" "+currentLine);
		        	}
		        	
		        	//Ignore escape characters and parse a single query
		        	Query query = parser.parse(QueryParser.escape( queryLine.trim() ) );

		        	//Get the set of results from the searcher in descending order
					ScoreDoc[] hits = isearcher.search(query, MAX_RESULTS).scoreDocs;
					
					//Print the results and write its contents in a file
					for (int i = 0; i < hits.length; i++)
					{
						Document hitDoc = isearcher.doc(hits[i].doc);
						String fileContent = count + " 0 " + hitDoc.get("path").substring(3) + " 0 " + (hits[i].score)+" EXP\n";
						writer.write(fileContent);
					}
					count++;
		        }
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			//close all resources when searching is done
			writer.close();
			br.close();
			stream.close();
		}
		
	}

}
