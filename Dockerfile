FROM debian:latest
ADD tshirt-service tshirt-service
WORKDIR tshirt-service
RUN apt-get update && apt-get install -y default-jdk && apt-get install -y maven
EXPOSE 8888
CMD ["mvn","spring-boot:run"]