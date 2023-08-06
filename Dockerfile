FROM amd64/openjdk
COPY ./target/maven-graduation-project-0.0.1-SNAPSHOT.jar /usr/app/
WORKDIR /usr/app
ENTRYPOINT ["java","-jar","maven-graduation-project-0.0.1-SNAPSHOT.jar"]