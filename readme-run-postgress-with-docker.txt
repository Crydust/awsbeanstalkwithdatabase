docker-compose build
docker-compose up -d
docker container logs awbeanstalkwithdatabase_example_web_1
docker container logs awbeanstalkwithdatabase_example_db_1
open http://127.0.0.1:8080/

docker-compose down -v