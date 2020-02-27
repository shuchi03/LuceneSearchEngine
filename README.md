# LuceneSearchEngine

To build and run the jar files, change directory to LuceneSearchEngine by using below command - //
`cd LuceneSearchEngine`

1) To build jar file, use the below command - //
    `mvn package`

2) To run the jar file, use the below command - //
    `java -jar target/searchEngine-0.0.1-SNAPSHOT.jar`

3) To run the trec_eval on query result, change directory to trec_eval-9.0.7 by using below command - //
   `cd trec_eval-9.0.7` <br />
   The result of queries is stored in queryResult file. To run trec_eval on this file, use below command - //
  `./trec_eval '../cran/QRelsCorrectedforTRECeval' '../queryResult'`
