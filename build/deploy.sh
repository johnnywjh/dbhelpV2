#!/bin/bash -il
################## https://blog.csdn.net/ejennahuang/article/details/86087786
# sh ${WORKSPACE}/build/deploy.sh ${ENV} ${PROJECT_PATH} ${WORKSPACE} ${JOB_NAME} ${BUILD_NUMBER} ${PROT} ${BRANCH}
# def params
ENV=$1
PROJECT_PATH=$2
WORKSPACE=$3
JOB_NAME=$4
BUILD_NUMBER=$5
PROT=$6
BRANCH=$7
#imageurl="harbor.node3.cn/johnnywjh"
imageurl="10.168.1.109:5000"
DEPLOY_NAME=${JOB_NAME//./-}
########################################################################
echo "#######################"
echo "ENV : "${ENV}
echo "PROJECT_PATH : "${PROJECT_PATH}
echo "WORKSPACE : "${WORKSPACE}
echo "JOB_NAME : "${JOB_NAME}
echo "BUILD_NUMBER : "${BUILD_NUMBER}
echo "PROT : "${PROT}
echo "BRANCH : "${BRANCH}
echo "DEPLOY_NAME : "${DEPLOY_NAME}
echo "#######################"
########################################################################

########################################################################
# get git revison
cd ${WORKSPACE}
version=`git log --pretty=format:%h -1`
revision=G${version}${BUILD_NUMBER}
echo "revision : "${revision}
########################################################################

cd ${WORKSPACE}
mvn clean install
cd ${WORKSPACE}/${PROJECT_PATH}
mvn clean package docker:build

newimagename=${imageurl}/${JOB_NAME}:${revision}
echo "update images name"
echo "docker tag ${JOB_NAME}:latest ${newimagename}"
docker tag ${JOB_NAME}:latest ${newimagename}

echo "docker push images"
#docker login -u admin -p Admin123 http://harbor.node3.cn
echo "docker push ${newimagename}"
docker push ${newimagename}

docker rmi $(docker images -q -f dangling=true)
docker rmi ${newimagename}

echo "连接k8s 更新项目镜像,重新部署项目"
sed -e "s#{JOB_NAME}#${JOB_NAME}#g;s#{DEPLOY_NAME}#${DEPLOY_NAME}#g;s#{newimagename}#${newimagename}#g;s#{port}#${PROT}#g" ${WORKSPACE}/build/common-deployment.yaml > ${WORKSPACE}/build/${DEPLOY_NAME}-k8s-deployment.${ENV}.tmp
kubectl --kubeconfig=/home/rke/.kube/config apply -f ${WORKSPACE}/build/${DEPLOY_NAME}-k8s-deployment.${ENV}.tmp
# fail and exit
if [ $? -ne 0 ]
then
echo "ERROR: kubectl apply fail"
exit 1
fi


########################################################################
echo "==================================================="
echo "域名 : "${JOB_NAME}
echo "环境 ："${ENV}
echo "版本 ："${revision}
echo "==================================================="
########################################################################