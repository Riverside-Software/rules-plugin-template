package org.sonar.plugins.openedge;

import java.util.Arrays;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.plugins.openedge.api.AnnotationBasedRulesDefinition;
import org.sonar.plugins.openedge.api.Constants;

public class AcmeRulesDefinition implements RulesDefinition {
  public static final String REPOSITORY_KEY = "acme-rules";
  public static final String REPOSITORY_NAME = "OpenEdge rules (ACME)";

  @SuppressWarnings("rawtypes")
  @Override
  public void define(Context context) {
    NewRepository repository = context.createRepository(REPOSITORY_KEY, Constants.LANGUAGE_KEY).setName(
        REPOSITORY_NAME);

    AnnotationBasedRulesDefinition annotationLoader = new AnnotationBasedRulesDefinition(repository,
        Constants.LANGUAGE_KEY);
    annotationLoader.addRuleClasses(false, Arrays.<Class> asList(AcmeRulesRegistrar.ppCheckClasses()));
    annotationLoader.addRuleClasses(false, Arrays.<Class> asList(AcmeRulesRegistrar.dbCheckClasses()));

    repository.done();
  }
}