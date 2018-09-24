package com.acme.rules;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.openedge.api.checks.OpenEdgeProparseCheck;
import org.sonar.plugins.openedge.api.model.SqaleConstantRemediation;
import org.prorefactor.core.ABLNodeType;
import org.prorefactor.core.JPNode;
import org.prorefactor.treeparser.ParseUnit;

@Rule(priority = Priority.INFO, name = "No-op rule", tags = {"acme"})
@SqaleConstantRemediation(value = "5min")
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
