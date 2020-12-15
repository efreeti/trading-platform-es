FROM openjdk:11

ENV JAVA_OPTS="-Xmx128m -Xms64m -Dspring.profiles.active=docker"

COPY ./target/account-service-*.jar account-service.jar
RUN sh -c 'touch /account-service.jar'

EXPOSE 10000
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar account-service.jar"]
