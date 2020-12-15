FROM openjdk:11

ENV JAVA_OPTS="-Xmx128m -Xms64m -Dspring.profiles.active=docker"

COPY ./target/order-service-*.jar order-service.jar
RUN sh -c 'touch /order-service.jar'

EXPOSE 10001
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar order-service.jar"]
