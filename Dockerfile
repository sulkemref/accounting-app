FROM amd64/maven:3.8.3-openjdk-17
WORKDIR usr/app
COPY . .
ENTRYPOINT ["mvn","spring-boot:run"]