@echo off

docker ps | findStr "postgres-sample" >NUL && echo Postgres is running && exit /B 0

cd .\postgres || exit
docker build -t postgres-sample .
docker run -d -p 5432:5432 --rm --name postgres-container postgres-sample --max_prepared_transactions=100
cd ..\..\
