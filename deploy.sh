#!/usr/bin/env sh

ecnucs=wyt

echo start deploying...
echo kill backend process...
ssh ${ecnucs} "if [ -f \"/var/log/app.pid\" ]; then kill \$(cat /var/log/app.pid); fi"

echo start backend...
scp $(ls build/libs/*jar) ${ecnucs}:/home/ubuntu/cs-platform
ssh ${ecnucs} "export LANG=\"en_US.UTF-8\";export JAVA_TOOL_OPTIONS=\"-Dfile.encoding=UTF8 -Dsun.jnu.encoding=utf8\";cd /home/ubuntu/cs-platform;nohup java -jar \$(ls *jar) > nohup.out 2> nohup.err &"

echo deployment completed
