pipeline {
  agent none
  stages {
    stage('Configure') {
      agent none
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