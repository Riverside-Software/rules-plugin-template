package com.acme.rules;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.rule.RuleKey;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.openedge.api.LicenceRegistrar.Licence;
import org.sonar.plugins.openedge.api.checks.OpenEdgeProparseCheck;
import org.sonar.plugins.openedge.api.model.SqaleConstantRemediation;
import org.sonar.plugins.openedge.api.org.prorefactor.treeparser.ParseUnit;

@Rule(priority = Priority.INFO, name = "No-op rule", tags = {"acme"})
@SqaleConstantRemediation(value = "5min")
public class NoOpRule extends OpenEdgeProparseCheck {

  public NoOpRule(RuleKey ruleKey, SensorContext context, Licence licence, String serverId) {
    super(ruleKey, context, licence, serverId);
  }

  @Override
  public void execute(InputFile file, ParseUnit o) {
    // No-op
  }

}
