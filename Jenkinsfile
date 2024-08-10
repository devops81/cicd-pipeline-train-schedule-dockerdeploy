pipeline {
    /* agent { label 'Built-In'} */
    /*triggers {
        pollSCM '* * * * *'
        cron('49 3 * * *') } */
     environment{

        registry = "devops81/train-schedule"
        registryCredential = 'docker_hub_login'

    
    }
    stages {
        
        stage('Read Properties') {
      steps {
        script {
          load 'globalVars.groovy' // Load the script
        }
      }
    }
        
   stage('Use Global Variables') {
      steps {
        echo "Value of GLOBAL_VARIABLE_1: ${env.GLOBAL_VARIABLE_1}"
        echo "Value of GLOBAL_VARIABLE_2: ${env.GLOBAL_VARIABLE_2}"
        echo "Value of GLOBAL_VARIABLE_2: ${env.GLOBAL_VARIABLE_3}"
      }
    }

        stage('Build') {
            steps {
                echo 'Runnings build automation'
                sh './gradlew build --no-daemon'
                archiveArtifacts artifacts: 'dist/trainSchedule.zip'
            }
        }
        stage('Build Docker Image') {
           /* when {
                branch 'origin/example-solution'
            }*/
            steps {
                script {
         app = docker.build(registry,'--network=host .')
         app.inside {
                        sh 'echo $(curl localhost:8080)'
                    }
                        }
            
            }
        }
       stage('Push Docker Image') {
           /* when {
                branch 'origin/example-solution'
            } */
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'docker_hub_login') {
                        app.push("${env.BUILD_NUMBER}")
                        app.push("latest")
                    }
                }
            }
        }

        stage('DeployToProduction2') {
                
                    steps {
                        input 'Deploy to Production2?'
                        milestone(1)
                        withCredentials([usernamePassword(credentialsId: 'webserver_login', usernameVariable: 'USERNAME', passwordVariable: 'USERPASS')]) {
                            script {
                                sh "/usr/local/bin/sshpass -p '$USERPASS' -v ssh -o StrictHostKeyChecking=no $USERNAME@${env.GLOBAL_VARIABLE_3} \"docker pull devops81/train-schedule:${env.BUILD_NUMBER}\""
                                try {
                                    sh "/usr/local/bin/sshpass -p '$USERPASS' -v ssh -o StrictHostKeyChecking=no $USERNAME@${env.GLOBAL_VARIABLE_3} \"docker stop train-schedule\""
                                    sh "/usr/local/bin/sshpass -p '$USERPASS' -v ssh -o StrictHostKeyChecking=no $USERNAME@${env.GLOBAL_VARIABLE_3} \"docker rm train-schedule\""
                                } catch (err) {
                                    echo: 'caught error: $err'
                                }
                                sh "/usr/local/bin/sshpass -p '$USERPASS' -v ssh -o StrictHostKeyChecking=no $USERNAME@$prod_ip \"docker run --restart always  --name train-schedule -p 8080:8080 -d devops81/train-schedule:${env.BUILD_NUMBER}\""
                            }
                        }
                    }
                }
}
}
