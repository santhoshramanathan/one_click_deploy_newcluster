def step_build(){
  node('master') {
      try {
          println (">> before checkout")
          checkout scm
          println (">> after checkout")

          wrap([$class: 'Xvfb', additionalOptions: '', assignedLabels: '', autoDisplayName: true, installationName: 'Xvfb', screen: '']) {
          
            sh ''' echo "BUILD_FOLDER *********" + ${BUILD_FOLDER}
                  . ${ACE_INSTALL_DIR}/server/bin/mqsiprofile
                  pwd
                  dir
                  mqsicreatebar -data . -b ${APP_NAME}.bar -a ${APP_NAME} -skipWSErrorCheck
                  cp ${APP_NAME}.bar ${BUILD_FOLDER}'''
              
            stash includes: '*.bar', name: 'barFileComponent'
          }
      }
      catch(error) {
        println ">> build failed"
        throw error
      }
    }
}

def step_run_ta() {
    node() {
      try {
        println (">> running TA  ${BUILD_NO} <<")
        
        sh ''' 
            . ${ACE_INSTALL_DIR}/server/bin/mqsiprofile
            mkdir -p /home/ucp4i/play/ta-dir/${APP_NAME}
            export TADataCollectorDirectory=/home/ucp4i/play/ta-dir/${APP_NAME}
            ${ACE_INSTALL_DIR}/server/bin/TADataCollector.sh ace run /home/ucp4i/play/one-click-builds/${APP_NAME}.bar
            '''
         
        println (">> custom image pushed to registry <<")
        
      }
      catch(error) {
        println " failure to push "
        throw error
      }
    
    }
}

def step_createDockerImage() {
    node('master') {
      try {
        
        println (">> creating custom image  ${BUILD_NO} <<")
        
        sh '''whoami
            pwd
            cp /home/ucp4i/play/one-click-builds/${APP_NAME}.bar .
            docker login ${DOCKER_REGISTRY_URL} --username jenkins --password eyJhbGciOiJSUzI1NiIsImtpZCI6IkRlNWhZdmtKb1lzMjB6U1F3eFZJN1BnbnFaX0gxVkdVdlJDQW1DVUJxSncifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJhY2UiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlY3JldC5uYW1lIjoiamVua2lucy10b2tlbi04NnY2eCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50Lm5hbWUiOiJqZW5raW5zIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQudWlkIjoiYjhiNzAxMGYtNjNjZi00ZjY3LTg2ZWMtYTNiNGViZjdlNzRmIiwic3ViIjoic3lzdGVtOnNlcnZpY2VhY2NvdW50OmFjZTpqZW5raW5zIn0.PBRl944eXhlDXemfCOOXnKseMR9Qrf3otHAp1DqOjtID2PdlnM0wa7asewZgx2-FFU2UTJ01u7H3kfShTetWIbhFciJulRDZCCwxLu0LRAyD9KIQFfJBzisOJhTGwHgyOPr07B7mdp5t7suJb_hodGADS4Bc9bhrZwKPT3l6QahTxC92G7zGlQJ4XTvw8DBvau-mHUOTz-So5h9jtAoTqIKIRCgcZvGuH9FjPe-8MFiPyI93uT35VPj2TlRIfPnWp-Pgm-rHbxvZ9JkYlBe6dZhIoOUyAzOpRPeTSijY2KQr_Fya3oL7Rh1tSC_BDh2eqg2HWDwpUxvuFfGBtTrPcw
            docker build -t barimage:${BUILD_NO} -f- . <<EOF
FROM ${DOCKER_REGISTRY_URL}/ace/ace:latest
COPY ${APP_NAME}.bar /home/aceuser/initial-config/bars/
RUN ace_compile_bars.sh 
EOF
            docker tag barimage:${BUILD_NO} ${DOCKER_REGISTRY_URL}/ace/barimage:${BUILD_NO}-amd64
            docker push ${DOCKER_REGISTRY_URL}/ace/barimage:${BUILD_NO}-amd64

            docker rmi ${DOCKER_REGISTRY_URL}/ace/barimage:${BUILD_NO}-amd64
            '''
         
        println (">> custom image pushed to registry <<")
        
      }
      catch(error) {
        println " failure to push "
        throw error
      }
    
    }
}

def step_createDockerImage_MQ() {
    node('master') {
      try {
        
        println (">> creating custom image  ${BUILD_NO} <<")
        
        sh '''whoami
            pwd
            cp /home/ucp4i/play/one-click-builds/config.mqsc .
            docker login ${DOCKER_REGISTRY_URL} --username jenkins --password eyJhbGciOiJSUzI1NiIsImtpZCI6IkRlNWhZdmtKb1lzMjB6U1F3eFZJN1BnbnFaX0gxVkdVdlJDQW1DVUJxSncifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJhY2UiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlY3JldC5uYW1lIjoiamVua2lucy10b2tlbi04NnY2eCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50Lm5hbWUiOiJqZW5raW5zIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQudWlkIjoiYjhiNzAxMGYtNjNjZi00ZjY3LTg2ZWMtYTNiNGViZjdlNzRmIiwic3ViIjoic3lzdGVtOnNlcnZpY2VhY2NvdW50OmFjZTpqZW5raW5zIn0.PBRl944eXhlDXemfCOOXnKseMR9Qrf3otHAp1DqOjtID2PdlnM0wa7asewZgx2-FFU2UTJ01u7H3kfShTetWIbhFciJulRDZCCwxLu0LRAyD9KIQFfJBzisOJhTGwHgyOPr07B7mdp5t7suJb_hodGADS4Bc9bhrZwKPT3l6QahTxC92G7zGlQJ4XTvw8DBvau-mHUOTz-So5h9jtAoTqIKIRCgcZvGuH9FjPe-8MFiPyI93uT35VPj2TlRIfPnWp-Pgm-rHbxvZ9JkYlBe6dZhIoOUyAzOpRPeTSijY2KQr_Fya3oL7Rh1tSC_BDh2eqg2HWDwpUxvuFfGBtTrPcw
            docker build -t mqimage:${BUILD_NO} -f- . <<EOF
FROM ${DOCKER_REGISTRY_URL}/mq/mq-newapp:latest
USER root
RUN useradd admin -G mqm \
    && echo admin:passw0rd | chpasswd \
# Create the mqclient group
    && groupadd mqclient \
# Create the app user as a member of the mqclient group and set their password
    && useradd app -G mqclient \
    && echo app:passw0rd | chpasswd
# Copy the configuration script to /etc/mqm where it will be picked up automatically
USER mqm
COPY config.mqsc /etc/mqm/
EOF
            docker tag mqimage:${BUILD_NO} ${DOCKER_REGISTRY_URL}/mq/mqimage:${BUILD_NO}-amd64
            docker push ${DOCKER_REGISTRY_URL}/mq/mqimage:${BUILD_NO}-amd64

            #docker rmi ${DOCKER_REGISTRY_URL}/mq/mqimage:${BUILD_NO}-amd64
            '''
         
        println (">> custom image pushed to registry <<")
        
      }
      catch(error) {
        println " failure to push "
        throw error
      }
    
    }
}

def step_deployImage() {
    node() {
      try {
        
      sh '''
      oc login ${OPEN_SHIFT_URL} --token=eyJhbGciOiJSUzI1NiIsImtpZCI6IkRlNWhZdmtKb1lzMjB6U1F3eFZJN1BnbnFaX0gxVkdVdlJDQW1DVUJxSncifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJhY2UiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlY3JldC5uYW1lIjoiamVua2lucy10b2tlbi04NnY2eCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50Lm5hbWUiOiJqZW5raW5zIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQudWlkIjoiYjhiNzAxMGYtNjNjZi00ZjY3LTg2ZWMtYTNiNGViZjdlNzRmIiwic3ViIjoic3lzdGVtOnNlcnZpY2VhY2NvdW50OmFjZTpqZW5raW5zIn0.PBRl944eXhlDXemfCOOXnKseMR9Qrf3otHAp1DqOjtID2PdlnM0wa7asewZgx2-FFU2UTJ01u7H3kfShTetWIbhFciJulRDZCCwxLu0LRAyD9KIQFfJBzisOJhTGwHgyOPr07B7mdp5t7suJb_hodGADS4Bc9bhrZwKPT3l6QahTxC92G7zGlQJ4XTvw8DBvau-mHUOTz-So5h9jtAoTqIKIRCgcZvGuH9FjPe-8MFiPyI93uT35VPj2TlRIfPnWp-Pgm-rHbxvZ9JkYlBe6dZhIoOUyAzOpRPeTSijY2KQr_Fya3oL7Rh1tSC_BDh2eqg2HWDwpUxvuFfGBtTrPcw
      cd /opt/certs
      helm init --client-only
      helm repo add ibm-entitled-charts https://raw.githubusercontent.com/IBM/charts/master/repo/entitled/
      helm install --name ${RELEASE_NAME} ibm-entitled-charts/ibm-ace-server-icp4i-prod --namespace ace --set imageType=ace  --set image.aceonly=${IMAGE_STREAM}/ace/barimage:${BUILD_NO} --set productionDeployment=false --set image.pullSecret=ibm-entitlement-key --set service.iP=icp-console.cloud-integration-224380-6fb0b86391cd68c8282858623a1dddff-0000.eu-gb.containers.appdomain.cloud --set aceonly.replicaCount=1 --set dataPVC.storageClassName=ibmc-file-bronze --set integrationServer.name=${RELEASE_NAME} --set license=accept --tls
      #oc expose svc ${RELEASE_NAME}-ibm-ace-server-icp4i-prod --port=7800 --name=${RELEASE_NAME}-http
      #oc delete all --selector release=${RELEASE_NAME_TO_DEL}
      '''
        
      }
      catch(error) {
        println ">> image deployment failed <<"
        throw error
      }
    
    }
}    
   
