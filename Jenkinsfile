node{
stage('Checkout source control') {
checkout scm
}
stage('connect'){
sh '''
oc login --server=https://c100-e.eu-gb.containers.cloud.ibm.com:31724 --token=eyJhbGciOiJSUzI1NiIsImtpZCI6Ill2T1N3V3JQTjI2YkE2WWVzajlxTDZXVW40QUdCSDgtMUxmV21wSW1tY3cifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJhY2UiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlY3JldC5uYW1lIjoiZGV2b3BzYWNjLXRva2VuLWpnZHRnIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6ImRldm9wc2FjYyIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6IjMwZDgxMjU2LWVjYzItNDQ3Yi1hZTQ0LTAxZDAxYWM4YjVhYiIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDphY2U6ZGV2b3BzYWNjIn0.QlULKiWdtLk0r3vBiqyYb_iBPzY89mNcz0gHJ-adLj11QPvJjw_LzAwbX45JlKJCqB4mEo7XYaWYijbI2ldndAl6_97Occ1b0FE_ckmSt4RUevTuCw24x78yUp22RJTpf_FWrQOQc4Ly3DOnGpAhnBV1VuJcOYnKh_0WuMs4osKzzt6AG0R6o3DQOzaHOb7aZpzDv1vwjQBOOdv2gbVGUp9ntGY_8VHf6XVpZ2-HSt4qcQTMWLKL2ylcuWHtS-_pwmnOOz_lMSMSpWwqP6U2LQZLYw66d4_Xw3xXkCVwiQPVAW0ThwZV2uk7_1HgrVQl8FmIS7AsJANsT4ORSx1VxA
oc project ace
'''
}
stage('Image Stream creation for base image'){
sh '''
oc apply -f ImageStream.yaml
'''
}
stage('Tag image stream with ACEv10 base image'){
sh '''
oc tag -n ace cp.icr.io/cp/appc/ace-server-prod@sha256:dd3c1e8d204b37775b792fc25a0bad4daba4fa35cd5aad996b29b1db63959baf ace-server-prod-10-new:latest-amd64
'''
}
stage('Custom Image Stream creation'){
sh '''
oc apply -f ImageStream2.yaml
'''
}

stage('Build config creation'){
sh '''
oc apply -f BuildConfig.yaml
oc set build-secret --pull bc/my-custom-ace-image-10-new ibm-entitlement-key
'''
}

stage('Start Build'){
sh '''
oc start-build my-custom-ace-image-10-new --from-file ./HTTPEcho.bar
oc describe builds
'''
}

stage('Create Integration server'){
sh '''
oc apply -f IntegrationServer.yaml
'''
}

}