# compile war, build docker compose and run in background
mvn clean package && docker-compose up --build --detach

# see logs
docker container logs awsbeanstalkwithdatabase_example_web_1
docker container logs awsbeanstalkwithdatabase_example_db_1

# open website in safari
open http://127.0.0.1:8080/

# stop docker compose and remove volumes
docker-compose down --volumes

# remove dangling images
docker image prune

