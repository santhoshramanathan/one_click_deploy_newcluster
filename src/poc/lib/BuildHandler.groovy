package poc.lib

class BuildHandler implements Serializable {

def acepipeline
def appName
def mqpipeline

def getAcePipeline() {
 	return acepipeline
 }
 
void setAcePipeline(acepipeline) {
	this.acepipeline = acepipeline
}

def getAppName() {
 	return appName
 }
 
void setAppName(appName) {
	this.appName = appName
}

def getMQPipeline() {
 	return mqpipeline
 }
 
void setMQPipeline(mqpipeline) {
	this.mqpipeline = mqpipeline
}

}
