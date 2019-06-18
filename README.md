# Akka Persistence Demo with Cassandra and Protobuf

With Cassandra running at port 9042. Run
`sbt 'runMain com.github.phisgr.akka.demo.Main'`
in the console to start the demo.
It will create the tables,
then the persistent actor will
write a few messages to the table `akka.messages`.
Then it will crash and restart and crash again.

## Running Cassandra

Running Cassandra locally can be a pain with different Java versions.
(The error I got with Java 11 is
`intx ThreadPriorityPolicy=42 is outside the allowed range [ 0 ... 1 ]`.)

The easiest way out is to use Docker.

`docker run --name akka-cassandra-demo -d -p 127.0.0.1:9042:9042 cassandra`