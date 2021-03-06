# trading-platform-es

"Simple" example of applying a very simplified version of event sourcing 
pattern on the domain of financial instrument trading.

**PLEASE READ COMPLETELY BEFORE EVALUATING**

## System Design

![System Diagram](System%20Diagram.png)

#### Accounts Service

Performs commands and manages account aggregate state based on events.

#### Orders Service

Performs commands and manages order aggregate state based on events.

#### Market Service

Dummy collaborator service that simulates execution of orders on the
stock exchange market. This is a stub service with simplified logic.

## Entities

![Accounts Entity Diagram](Accounts%20Entity%20Diagram.png)

![Orders Entity Diagram](Orders%20Entity%20Diagram.png)

![Events Entity Diagram](Events%20Entity%20Diagram.png)

## Flows

![Account Creation Flow Diagram](Account%20Creation%20Flow%20Diagram.png)

![Buy Order Flow Diagram](Buy%20Order%20Flow%20Diagram.png)

![Sell Order Flow Diagram](Sell%20Order%20Flow%20Diagram.png)

## Design choices

The designed and implemented system is not "pure" event sourcing.
There were couple of simplification choices done due to the time
constraints.

First of all as focus was mostly on the order flow there is a 
single central event store (using Kafka) and no separate stores 
per bounded context. That would complicate the solution and would
need additional time investment in designing fully decoupled
systems. In the designed system majority of events are around order
status changes and different components contribute to the order status
changes directly. In a well designed event sourcing system each
bounded context would potentially end up with own event store and
would use event broker to align state.

Secondly there is one step in the processing pipeline where service
performs validations over aggregates and performs aggregate updates
in the same "transaction" as producing subsequent event. Defining
more solid flow in this area would require more substantial domain
analysis and time investment to properly think through compensating
actions/events and all possible scenarios.

Additionally it can be noticed on the diagrams, that to speed up
development some entities were reused between "events" and "orders"
bounded contexts, essentially breaking DDD principles. In realistic
project decoupling would be done.

## Building

To build the project run following command in the root:

```
mvn clean install
```

## Automated Testing

There is only one JUnit test to show case testing. Tests are run as part of the build.
To run tests separately:

```
mvn test
```

## Manual Testing

To create account run following command in the root of the project:

```
./scripts/create-account.sh
```

This will return the account id. Use that in further testing.

To see account balance open http://localhost:10000/<account-id>/balance in the browser.

To see account portfolio open http://localhost:10000/<account-id>/portfolio in the browser.

To create order run following command in the root of the project:

```
./scripts/create-order.sh <account-id> <BUY|SELL>? <instrument-id>? <quantity>? <price>?
```

This will return the order id. Use that in further testing.

To see orders for account open http://localhost:10001/orders?accountId=<account-id> in the browser.

To see specific order open http://localhost:10001/orders/<order-id> in the browser.


## Running

Project contains docker-compose configuration that represents the services
and their dependencies. Each service has a Dockerfile describing it's docker
image building strategy. Each service has application.yml for running outside
docker and application-docker.yml for running inside docker.

#### Run services and dependencies without docker

You can install Zookeeper, Kafka and MongoDB standalone, change application.yml
configs of all involved services and run services either using:

```
cd <name>-service
mvn spring-boot:run
```

or

```
java -jar <name>-service-1.0.0-SNAPSHOT.jar
```

#### Run services without docker and dependencies with docker

docker-compose can be used to spin up only Zookeeper, Kafka and MongoDB.
Run following command in the root of the project:

```
docker-compose up zookeeper kafka mongo
```

Then run services as described in previous section.

#### Run everything with docker

```
docker-compose up
```

## Technology choices

Maven was chosen over Gradle as a more fluent tool to speed up
prototyping.

For demonstrative purposes no event sourcing frameworks (like Axon)
were chosen for the prototype.

Kafka was chosen as streaming platform and event store due to 
it's characteristics that make it usable both as durable storage
and streaming solution. In addition the most recent experience
with messaging was with Kafka, which speeds up prototyping. JSON
was chosen as a persistence format out of simplicity, instead of
choosing more production ready solutions like Avro or Protobuf.
Single central "events" topic was used for storing/distributing 
events. No complex tuning of the Kafka setup was done (like delivery
guarantees, partitions, etc.)

MongoDB was chosen as persistence storage for aggregate data/views.
No complex tuning of the DB setup was done, no authentication
configured.

In general it was a conscious choice not to invest time into setting
up security for the services involved.

For development convenience Lombok library was chosen to reduce
boilerplate. You will need to install plugins in your IDE of 
choice to avoid error messages. Most container/domain objects
were made immutable except couple of cases of direct persistence
to MongoDB for convenience.

UUID was used as identification scheme for entities to reduce
time investment in prototype implementation. In production 
environments identification scheme should be thought through better

Microservices were built using Spring Boot stack, with usage of 
Spring MVC, Spring Kafka and Spring Data MongoDB.

Most components for Spring Boot app use constructor injections
with some minor exception that was done for convenience.

The domain/model structure is not optimal there is some unnecessary
coupling and redundancy in structure. Most of it due to tight
time constraints.

Amount of libraries was kept to the minimum to keep simplicity.
For example Spring ModelMapper could be used in some cases of
mapping domain to DTO.

There were some choices made in the design of events domain (polymorphism)
that are suitable for JSON serialization/deserialization strategy
but would have to be rethought when using Avro/Protobuf.

In addition due to the complexity of transactionality in MongoDB and
time constraints only partial solution is available on the 
"transactional-solution" branch (it's not finished).
