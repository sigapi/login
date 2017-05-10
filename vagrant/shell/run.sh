#!/bin/bash

APP=login

# Para a execução anterior
echo "Execução anterior..."
fuser -k -TERM -n tcp 80 -s

# Prepara o diretório
echo "Diretório..."
dir=/sigapi
mkdir -p ${dir}
cd ${dir}
cp /tmp/sigapi/* .

# Salva cópia dos logs
echo "Logs..."
if [ -d logs ]; then
    cd logs
    if [ -f $FILE ]; then
        cp ${APP}.{log,"$(date +%Y%m%d-%H%M%S)".log}
        rm -rf ${APP}.log
    fi
fi

# Iniciando execução atual
echo "Iniciando..."
cd ${dir}
nohup java -Dspring.profiles.active=public -jar ${APP}.jar &
sleep 5s