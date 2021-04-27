pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        echo 'Hello from Jenkins'
        sh '''
            withMaven(maven : \'apache-maven-3.6.1\') {
                bat\'mvn clean compile\'
            }

'''
      }
    }

  }
}