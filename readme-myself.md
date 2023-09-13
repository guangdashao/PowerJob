本地编译打包：
windows   windows打的包，在linux环境无法运行

mvn clean package  -DskipTests -U -e 
copy /Y powerjob-server\powerjob-server-starter\target\*.jar powerjob-server\docker\powerjob-server.jar 
copy /Y powerjob-worker-agent\target\*.jar powerjob-worker-agent\powerjob-agent.jar 
copy /Y powerjob-worker-samples\target\*.jar powerjob-worker-samples\powerjob-worker-samples.jar

cd powerjob-server\docker
docker build . -t harbor.ansteel.cn/lib/yujia-job:4.3.6-arm64 --platform linux/arm64
docker build . -t harbor.ansteel.cn/lib/yujia-job:4.3.6-amd64
docker push harbor.ansteel.cn/lib/yujia-job:4.3.6-arm64
docker push harbor.ansteel.cn/lib/yujia-job:4.3.6-amd64
docker manifest rm harbor.ansteel.cn/lib/yujia-job:4.3.6
docker manifest create harbor.ansteel.cn/lib/yujia-job:4.3.6 harbor.ansteel.cn/lib/yujia-job:4.3.6-arm64 harbor.ansteel.cn/lib/yujia-job:4.3.6-amd64
docker manifest push harbor.ansteel.cn/lib/yujia-job:4.3.6
    