package org.sonar.plugins.openedge;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.scanner.ScannerSide;
import org.sonar.api.server.ServerSide;
import org.sonar.plugins.openedge.api.CheckProvider;
import org.sonar.plugins.openedge.api.checks.OpenEdgeCheck;
import org.sonar.plugins.openedge.api.checks.OpenEdgeDumpFileCheck;
import org.sonar.plugins.openedge.api.checks.OpenEdgeProparseCheck;
import org.sonarsource.api.sonarlint.SonarLintSide;

import com.acme.rules.NoOpRule;
import com.acme.rules.TooManyParameters;

@ScannerSide
@SonarLintSide
@ServerSide
public class AcmeRulesChecksProvider implements CheckProvider {
  @SuppressWarnings("rawtypes")
  @Override
  public List<Class<? extends OpenEdgeCheck>> getChecks() {
    List<Class<? extends OpenEdgeCheck>> list = new ArrayList<>();
    list.addAll(ppCheckClasses());
    list.addAll(dbCheckClasses());

    return list;
  }

  /**
   * Lists all the checks provided by the plugin
   */
  static List<Class<? extends OpenEdgeProparseCheck>> ppCheckClasses() {
    return List.of(NoOpRule.class, TooManyParameters.class);
  }

  static List<Class<? extends OpenEdgeDumpFileCheck>> dbCheckClasses() {
    return List.of();
  }
}
