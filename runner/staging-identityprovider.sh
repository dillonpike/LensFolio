fuser -k 9500/tcp || true
source staging-portfolio/env.sh
java -jar staging-identityprovider/libs/identityprovider-0.0.1-SNAPSHOT.jar --spring.application.name=identity-provider --spring.profiles.active=test --grpc.server.port=9500
