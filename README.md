# trading-platform-es

"Simple" example of applying a very simplified version of event sourcing 
pattern on the domain of financial instrument trading.

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

![Entity Diagram](Entity%20Diagram.png)

As can be noted on the diagram, to speed up development some entities
were reused between "events" and "orders" bounded contexts, essentially
breaking DDD principles. In realistic project decoupling would be done.

## Technology choices

Maven was chosen over Gradle as a more familiar tool to speed up
prototyping.

For demonstrative purposes no event sourcing frameworks (like Axon)
were chosen for the prototype.

Kafka was chosen as streaming platform and event store due to 
it's characteristics that make it usable both as durable storage
and streaming solution. In addition the most recent experience
with messaging was with Kafka making prototyping faster. JSON
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

Microservices were built using Spring Boot stack, with additional
usage of Spring Kafka and Spring Data MongoDB.

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

Event handling in accounts service is not "pure" event sourcing
due to performing validations over aggregates and performing
aggregate updates in the same "transaction" as producing subsequent
event. Defining more solid flow in this area would require more
substantial domain analysis and time investment to properly 
think through compensating actions/events and all possible scenarios.

In addition due to the complexity of transactionality in MongoDB and
time constraints only partial solution is available on the 
"transactional-solution" branch.
