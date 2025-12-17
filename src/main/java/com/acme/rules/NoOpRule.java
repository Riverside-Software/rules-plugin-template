package com.acme.rules;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.openedge.api.checks.OpenEdgeProparseCheck;
import org.sonar.plugins.openedge.api.model.CleanCode;
import org.sonar.plugins.openedge.api.model.Impact;
import org.sonar.plugins.openedge.api.model.SqaleConstantRemediation;
import org.prorefactor.core.ABLNodeType;
import org.prorefactor.core.JPNode;
import org.prorefactor.treeparser.ParseUnit;

@Rule(priority = Priority.INFO, name = "No-op rule", tags = {"acme"})
@SqaleConstantRemediation(value = "5min")
// Quality must be one of: SECURITY, RELIABILITY, MAINTAINABILITY
// Severity must be one of: INFO, LOW, MEDIUM, HIGH, BLOCKER
@Impact(quality = "MAINTAINABILITY", severity = "INFO")
// Attribute must be one of:
//   FORMATTED, CONVENTIONAL, IDENTIFIABLE
//   CLEAR, LOGICAL, COMPLETE, EFFICIENT 
//   FOCUSED, DISTINCT, MODULAR 
//   LAWFUL, TRUSTWORTHY, RESPECTFUL
// See https://docs.sonarsource.com/sonarqube-server/2025.5/glossary/#c
@CleanCode(attribute = "FORMATTED")
public class NoOpRule extends OpenEdgeProparseCheck {

  @Override
  public void execute(InputFile file, ParseUnit unit) {
    // No-op

    // Report an issue at line 1
    // reportIssue(file, 1, "Message here...");

    // Report an issue on each MESSAGE keyword
    // for (JPNode node : unit.getTopNode().query(ABLNodeType.MESSAGE)) {
    //   reportIssue(file, node, "Message here...");
    // }
  }

}
