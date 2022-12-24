//${WORKSPACE}/build/java.groovy ${ENV} ${PROJECT_PATH} ${WORKSPACE} ${JOB_NAME} ${BUILD_NUMBER} ${PROT}
this.args.each{
    println it
}

def pringParams(){
    println("ENV : $env.ENV")
}



