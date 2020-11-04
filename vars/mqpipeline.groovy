def step_createDockerImage_MQ() {
    node('master') {
      try {
        
        println (">> creating custom image  ${BUILD_NO} <<")
        
        sh '''whoami
            pwd
            cp /home/ucp4i/play/one-click-builds/config.mqsc .
            docker login ${DOCKER_REGISTRY_URL} --username jenkins --password eyJhbGciOiJSUzI1NiIsImtpZCI6IkRlNWhZdmtKb1lzMjB6U1F3eFZJN1BnbnFaX0gxVkdVdlJDQW1DVUJxSncifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJhY2UiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlY3JldC5uYW1lIjoiamVua2lucy10b2tlbi04NnY2eCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50Lm5hbWUiOiJqZW5raW5zIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQudWlkIjoiYjhiNzAxMGYtNjNjZi00ZjY3LTg2ZWMtYTNiNGViZjdlNzRmIiwic3ViIjoic3lzdGVtOnNlcnZpY2VhY2NvdW50OmFjZTpqZW5raW5zIn0.PBRl944eXhlDXemfCOOXnKseMR9Qrf3otHAp1DqOjtID2PdlnM0wa7asewZgx2-FFU2UTJ01u7H3kfShTetWIbhFciJulRDZCCwxLu0LRAyD9KIQFfJBzisOJhTGwHgyOPr07B7mdp5t7suJb_hodGADS4Bc9bhrZwKPT3l6QahTxC92G7zGlQJ4XTvw8DBvau-mHUOTz-So5h9jtAoTqIKIRCgcZvGuH9FjPe-8MFiPyI93uT35VPj2TlRIfPnWp-Pgm-rHbxvZ9JkYlBe6dZhIoOUyAzOpRPeTSijY2KQr_Fya3oL7Rh1tSC_BDh2eqg2HWDwpUxvuFfGBtTrPcw
            docker build -t mqimage:${BUILD_NO} -f- . <<EOF
FROM ibmcom/mq:latest
USER mqm
COPY config.mqsc /etc/mqm/
EOF
            docker tag mqimage:${BUILD_NO} ${DOCKER_REGISTRY_URL}/ace/mqimage:${BUILD_NO}-amd64
            docker push ${DOCKER_REGISTRY_URL}/ace/mqimage:${BUILD_NO}-amd64

            #docker rmi ${DOCKER_REGISTRY_URL}/ace/mqimage:${BUILD_NO}-amd64
            '''
         
        println (">> custom image pushed to registry <<")
        
      }
      catch(error) {
        println " failure to push "
        throw error
      }
    
    }
}
