fuser -k 10500/tcp || true
source production-identityprovider/env.sh
java -jar production-identityprovider/libs/identityprovider-0.0.1-SNAPSHOT.jar --spring.application.name=identity-provider --spring.profiles.active=prod --grpc.server.port=10500
