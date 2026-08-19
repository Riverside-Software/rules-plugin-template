package org.sonar.plugins.openedge;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.SonarRuntime;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.plugins.openedge.api.AnnotationBasedRulesDefinition;
import org.sonar.plugins.openedge.api.Constants;

public class AcmeRulesDefinition implements RulesDefinition {
  public static final String REPOSITORY_KEY = "acme-rules";
  public static final String DB_REPOSITORY_KEY = "acme-db-rules";
  public static final String REPOSITORY_NAME = "OpenEdge rules (ACME)";

  private final SonarRuntime runtime;

  public AcmeRulesDefinition(SonarRuntime runtime) {
    this.runtime = runtime;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public void define(Context context) {
    var repository = context.createRepository(REPOSITORY_KEY, Constants.LANGUAGE_KEY).setName(
        REPOSITORY_NAME);
    var annotationLoader = new AnnotationBasedRulesDefinition(repository, Constants.LANGUAGE_KEY, runtime);
    List<Class> list = new ArrayList<>();
    list.addAll(AcmeRulesChecksProvider.ppCheckClasses());
    annotationLoader.addRuleClasses(false, list);

    repository.done();

    var repository2 = context.createRepository(DB_REPOSITORY_KEY, Constants.DB_LANGUAGE_KEY).setName(
        REPOSITORY_NAME);
    var annotationLoader2 = new AnnotationBasedRulesDefinition(repository2, Constants.DB_LANGUAGE_KEY, runtime);
    list = new ArrayList<>();
    list.addAll(AcmeRulesChecksProvider.dbCheckClasses());
    annotationLoader2.addRuleClasses(false, list);
    repository2.done();
  }

}