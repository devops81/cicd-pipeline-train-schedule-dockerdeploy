pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                echo 'Runnings build automation'
                sh './gradlew build --no-daemon'
                archiveArtifacts artifacts: 'dist/trainSchedule.zip'
            }
        }
    }
}
