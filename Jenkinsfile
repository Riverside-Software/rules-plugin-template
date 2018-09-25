#!groovy

stage 'Build OpenEdge rules'
node ('master') {
  checkout scm
  withEnv(["PATH+MAVEN=${tool name: 'Maven 3', type: 'hudson.tasks.Maven$MavenInstallation'}/bin"]) {
    sh "git rev-parse HEAD > current-commit"
    def currCommit = readFile('current-commit').replace("\n", "").replace("\r", "")
    sh "mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent install -Dmaven.test.failure.ignore=true -Dgit.commit=${currCommit}"
  }
  archiveArtifacts artifacts: 'target/*-plugin-*.jar'
  step([$class: 'Publisher', reportFilenamePattern: '**/testng-results.xml'])
}
