fuser -k 9501/tcp || true
source staging-portfolio/env.sh
java -jar staging-portfolio/libs/portfolio-0.0.1-SNAPSHOT.jar \
    --server.port=9501 \
    --spring.application.name=portfolio \
    --spring.profiles.active=test \
    --grpc.client.identity-provider-grpc-server.address=static://127.0.0.1:9500 \
    --grpc.client.identity-provider-grpc-server.enableKeepAlive=true \
    --grpc.client.identity-provider-grpc-server.keepAliveWithoutCalls=true \
    --grpc.client.identity-provider-grpc-server.negotiationType=plaintext 
