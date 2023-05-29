// globalVars.groovy
def loadGlobalVars() {
  def properties = new Properties()
  def inputStream = new FileInputStream('/var/lib/jenkins/workspace/test-job/jenkinsfile.pipeline.properties')
  properties.load(inputStream)
  inputStream.close()

  // Define global variables
  env.GLOBAL_VARIABLE_1 = properties.getProperty('property1')
  env.GLOBAL_VARIABLE_2 = properties.getProperty('property2')
}

loadGlobalVars() // Load the global variables immediately
