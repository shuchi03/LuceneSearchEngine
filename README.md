# LuceneSearchEngine

To build jar file, use the below command -
`mvn package'

To run the jar file, use the below command -
`java -jar target/searchEngine-0.0.1-SNAPSHOT.jar`

The result of queries is stored in queryResult file. To run trec_eval on this file, use below command - 
`./trec_eval '../cran/QRelsCorrectedforTRECeval' '../queryResult'`
