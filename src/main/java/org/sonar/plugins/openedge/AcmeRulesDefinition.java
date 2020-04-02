package org.sonar.plugins.openedge;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.SonarRuntime;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.plugins.openedge.api.AnnotationBasedRulesDefinition;
import org.sonar.plugins.openedge.api.CheckRegistration;
import org.sonar.plugins.openedge.api.Constants;
import org.sonar.plugins.openedge.api.checks.OpenEdgeDumpFileCheck;
import org.sonar.plugins.openedge.api.checks.OpenEdgeProparseCheck;

import com.acme.rules.NoOpRule;
import com.acme.rules.TooManyParameters;

public class AcmeRulesDefinition implements RulesDefinition, CheckRegistration {
  private static final Logger LOGGER = LoggerFactory.getLogger(AcmeRulesDefinition.class);

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
    NewRepository repository = context.createRepository(REPOSITORY_KEY, Constants.LANGUAGE_KEY).setName(
        REPOSITORY_NAME);
    AnnotationBasedRulesDefinition annotationLoader = new AnnotationBasedRulesDefinition(repository,
        Constants.LANGUAGE_KEY, runtime);
    annotationLoader.addRuleClasses(false, Arrays.<Class> asList(ppCheckClasses()));
    repository.done();

    NewRepository dbRepository = context.createRepository(DB_REPOSITORY_KEY, Constants.DB_LANGUAGE_KEY).setName(
        REPOSITORY_NAME);
    AnnotationBasedRulesDefinition annotationLoader2 = new AnnotationBasedRulesDefinition(dbRepository,
        Constants.DB_LANGUAGE_KEY, runtime);
    annotationLoader2.addRuleClasses(false, Arrays.<Class> asList(dbCheckClasses()));
    dbRepository.done();
  }

  /**
   * Register the classes that will be used to instantiate checks during analysis.
   */
  @Override
  public void register(Registrar registrar) {
    LOGGER.debug("Registering CheckRegistrar {}", AcmeRulesDefinition.class);

    for (Class<? extends OpenEdgeProparseCheck> clz : ppCheckClasses()) {
      registrar.registerParserCheck(clz);
    }
    for (Class<? extends OpenEdgeDumpFileCheck> clz : dbCheckClasses()) {
      registrar.registerDumpFileCheck(clz);
    }
  }

  /**
   * Lists all the checks provided by the plugin
   */
  @SuppressWarnings("unchecked")
  public static Class<? extends OpenEdgeProparseCheck>[] ppCheckClasses() {
    return new Class[] {NoOpRule.class, TooManyParameters.class};
  }

  @SuppressWarnings("unchecked")
  public static Class<? extends OpenEdgeDumpFileCheck>[] dbCheckClasses() {
    return new Class[] {};
  }

}