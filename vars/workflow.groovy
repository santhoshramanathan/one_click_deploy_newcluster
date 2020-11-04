import poc.lib.BuildHandler

def call(BuildHandler bHandler) {

    final APP_NAME = bHandler.getAppName()
    final OS_LOGIN_ID=credentials('os-login-id')
    def BUILD_NO=env.BUILD_NUMBER
    final OPEN_SHIFT_URL="https://c100-e.eu-gb.containers.cloud.ibm.com:31724"
    final DOCKER_REGISTRY_URL="default-route-openshift-image-registry.cloud-integration-224380-6fb0b86391cd68c8282858623a1dddff-0000.eu-gb.containers.appdomain.cloud"
    final ACE_INSTALL_DIR="/opt/ibm/ace-11.0.0.9"
    final BUILD_FOLDER="/home/ucp4i/play/one-click-builds"
    final RELEASE_NAME_PREFIX="one-click"
    final RELEASE_NAME="${RELEASE_NAME_PREFIX}-${APP_NAME}-rel-${BUILD_NO}".toLowerCase().replaceAll('_', '-')
    final IMAGE_STREAM="image-registry.openshift-image-registry.svc:5000"

    def BUILD_NO_TO_DEL=BUILD_NO - 2;
    final RELEASE_NAME_TO_DEL="${RELEASE_NAME_PREFIX}-${APP_NAME}-rel-${BUILD_NO_TO_DEL}".toLowerCase().replaceAll('_', '-')

    aceworkflow = bHandler.getAcePipeline()
    
    withEnv([
                    "APP_NAME=${APP_NAME}",
                    "BUILD_NO=${BUILD_NO}",
                    "ACE_INSTALL_DIR=${ACE_INSTALL_DIR}",
                    "OS_LOGIN_CREDS=${OS_LOGIN_ID}",
                    "DOCKER_REGISTRY_URL=${DOCKER_REGISTRY_URL}",
                    "OPEN_SHIFT_URL=${OPEN_SHIFT_URL}",
                    "RELEASE_NAME=${RELEASE_NAME}",
                    "RELEASE_NAME_TO_DEL=${RELEASE_NAME_TO_DEL}",
                    "BUILD_FOLDER=${BUILD_FOLDER}",
                    "IMAGE_STREAM=${IMAGE_STREAM}"

    ]) {

        



        stage("Create Docker Image") {
        
            try {
                aceworkflow.step_createDockerImage()

            }
            catch (error) {
                println ">> image creation failed <<"
                throw error
            }
            finally {
                step([$class: 'WsCleanup', notFailBuild: true, deleteDirs: true])
            }
        
        }

        stage("Deploy Image") {
        
            try {
                aceworkflow.step_deployImage()

            }
            catch (error) {
                println ">> deploy image failed <<"
                throw error
            }
            finally {
                step([$class: 'WsCleanup', notFailBuild: true, deleteDirs: true])
            }
        
        }
        
    }

    
}
