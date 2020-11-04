package vars
import poc.lib.BuildHandler

def call () {
    node() {
    
      try {
          BuildHandler bHandler = new BuildHandler()
          bHandler.setMQPipeline(mqpipeline)
          workflow(bHandler)
          }
      catch (Exception e) {
        echo e.message
        throw e
         }
         
      finally {
       step([$class: 'WsCleanup', notFailBuild: true, deleteDirs: true])
       }
    }
    
}

