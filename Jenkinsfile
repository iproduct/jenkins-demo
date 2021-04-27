pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        echo 'Hello from Jenkins'
        dir(path: '15-rest-mvc-boot-hateoas') {
          bat 'gradlew task'
          bat(script: 'gradlew test --debug', returnStatus: true, returnStdout: true)
        }

      }
    }

  }
}