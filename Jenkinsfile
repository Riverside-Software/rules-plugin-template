pipeline {
  agent { label 'Linux-Office03' }

  options {
    buildDiscarder(logRotator(numToKeepStr:'10'))
    timeout(time: 15, unit: 'MINUTES')
    skipDefaultCheckout()
  }

  stages {
    stage ('Build Custom Rules') {
      steps {
        checkout([$class: 'GitSCM', branches: scm.branches, extensions: scm.extensions + [[$class: 'CleanCheckout']], userRemoteConfigs: scm.userRemoteConfigs])
        script {
          withEnv(["MVN_HOME=${tool name: 'Maven 3', type: 'hudson.tasks.Maven$MavenInstallation'}", "JAVA_HOME=${tool name: 'JDK17', type: 'jdk'}"]) {
            sh "git rev-parse --short HEAD > current-commit"
            def currCommit = readFile('current-commit').replace("\n", "").replace("\r", "")
            sh "$MVN_HOME/bin/mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent install -Dmaven.test.failure.ignore=true -Dgit.commit=${currCommit}"
          }
        }
        archiveArtifacts artifacts: 'target/acme-rules-plugin-*.jar'
        step([$class: 'Publisher', reportFilenamePattern: '**/testng-results.xml'])
      }
    }

  }
}
