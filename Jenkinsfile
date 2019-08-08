pipeline {
  agent none
  stages {
    stage('Configure') {
      steps {
        script {
          def cfg
          node("master") {
            cfg = load 'configure.groovy'
          }
          cfg.configure()
        }
      }
    }
  }
}