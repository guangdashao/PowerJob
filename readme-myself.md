本地编译打包：
windows   windows打的包，在linux java -jar 执行，但是容器内部执行失败。需要在github上做完镜像之后，下载，重新打包

mvn clean package  -DskipTests -U -e 
copy /Y powerjob-server\powerjob-server-starter\target\*.jar powerjob-server\docker\powerjob-server.jar 
copy /Y powerjob-worker-agent\target\*.jar powerjob-worker-agent\powerjob-agent.jar 
copy /Y powerjob-worker-samples\target\*.jar powerjob-worker-samples\powerjob-worker-samples.jar

cd powerjob-server\docker

docker build . -t harbor.ansteel.cn/platform/job:4.3.6-2-arm64 --platform linux/arm64
docker build . -t harbor.ansteel.cn/platform/job:4.3.6-2-amd64
docker push harbor.ansteel.cn/platform/job:4.3.6-2-arm64
docker push harbor.ansteel.cn/platform/job:4.3.6-2-amd64
docker manifest rm harbor.ansteel.cn/platform/job:4.3.6-2
docker manifest create harbor.ansteel.cn/platform/job:4.3.6-2 harbor.ansteel.cn/platform/job:4.3.6-2-arm64 harbor.ansteel.cn/platform/job:4.3.6-2-amd64
docker manifest push harbor.ansteel.cn/platform/job:4.3.6-2

