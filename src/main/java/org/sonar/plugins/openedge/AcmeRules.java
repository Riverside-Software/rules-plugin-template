package org.sonar.plugins.openedge;

import org.sonar.api.Plugin;

public class AcmeRules implements Plugin {

  @Override
  public void define(Context context) {
    // Rules
    context.addExtension(AcmeRulesDefinition.class);
  }

}
