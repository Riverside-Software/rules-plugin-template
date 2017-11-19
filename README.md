# ACME Rules

This module is a template plugin for SonarQube, which adds a sample OpenEdge rule.

## How to build

First clone the Riverside-Software/sonar-openedge plugin, and execute `mvn install`, then execute `mvn package` in this module.
Deploy to your SonarQube instance by pushing the JAR file in `target` directory to `$SONAR_HOME/extensions/downloads`.
