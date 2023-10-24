# ACME Rules

This project is a template for [custom OpenEdge rules](https://github.com/Riverside-Software/sonar-openedge) in [SonarQube](https://www.sonarqube.org).

Read [this presentation](https://dl.rssw.eu/CustomRules.pdf) before writing custom rules !

## How to build

* Clone the project locally, then execute `mvn package`.
* Deploy to your SonarQube instance by pushing the JAR file in `target` directory to `$SONAR_HOME/extensions/downloads`.
