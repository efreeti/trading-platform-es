FROM openjdk:11

ENV JAVA_OPTS="-Xmx128m -Xms64m -Dspring.profiles.active=docker"

COPY ./target/market-service-*.jar market-service.jar
RUN sh -c 'touch /market-service.jar'

EXPOSE 10002
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar market-service.jar"]
