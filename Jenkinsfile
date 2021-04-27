pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        echo 'Hello from Jenkins'
        dir('15-rest-mvc-boot-hateoas') {
            bat 'gradlew task'
        }
      }
    }
  }
}
