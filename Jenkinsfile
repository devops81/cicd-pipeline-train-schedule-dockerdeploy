pipeline {
    agent any
    
     environment{
        
        registry = "devops81/train-schedule"
        registryCredential = 'docker_hub_login'
        prod_ip= '3.226.241.29'
    }
    stages {
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
        stage('DeployToProduction') {
           /* when {
                branch 'master'
            }*/
            steps {
                input 'Does the staging environment look OK?'
                milestone(1)
                withCredentials([usernamePassword(credentialsId: 'webserver_login', usernameVariable: 'USERNAME', passwordVariable: 'USERPASS')]) {
                    sshPublisher(
                        failOnError: true,
                        continueOnError: false,
                        publishers: [
                            sshPublisherDesc(
                                configName: 'production',
                                sshCredentials: [
                                    username: "$USERNAME",
                                    encryptedPassphrase: "$USERPASS"
                                ], 
                                transfers: [
                                    sshTransfer(
                                        sourceFiles: 'dist/trainSchedule.zip',
                                        removePrefix: 'dist/',
                                        remoteDirectory: '/tmp',
                                        
                                    )
                                ]
                            )
                        ]
                    )
                }
            }
        }
        stage('DeployToProduction2') {
                  
                    steps {
                        input 'Deploy to Production2?'
                        milestone(1)
                        withCredentials([usernamePassword(credentialsId: 'webserver_login', usernameVariable: 'USERNAME', passwordVariable: 'USERPASS')]) {
                            script {
                                sh "sshpass -p '$USERPASS' -v ssh -o StrictHostKeyChecking=no $USERNAME@$prod_ip \"docker pull willbla/train-schedule:${env.BUILD_NUMBER}\""
                                try {
                                    sh "sshpass -p '$USERPASS' -v ssh -o StrictHostKeyChecking=no $USERNAME@$prod_ip \"docker stop train-schedule\""
                                    sh "sshpass -p '$USERPASS' -v ssh -o StrictHostKeyChecking=no $USERNAME@$prod_ip \"docker rm train-schedule\""
                                } catch (err) {
                                    echo: 'caught error: $err'
                                }
                                sh "sshpass -p '$USERPASS' -v ssh -o StrictHostKeyChecking=no $USERNAME@$prod_ip \"docker run --restart always --name train-schedule -p 8080:8080 -d willbla/train-schedule:${env.BUILD_NUMBER}\""
                            }
                        }
                    }
                }
}
}
