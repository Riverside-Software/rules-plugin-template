package org.sonar.plugins.openedge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.plugins.openedge.api.CheckRegistration;
import org.sonar.plugins.openedge.api.checks.OpenEdgeDumpFileCheck;
import org.sonar.plugins.openedge.api.checks.OpenEdgeProparseCheck;

import com.acme.rules.NoOpRule;

public class AcmeChecksRegistration implements CheckRegistration {
  private static final Logger LOGGER = LoggerFactory.getLogger(AcmeChecksRegistration.class);

  /**
   * Register the classes that will be used to instantiate checks during analysis.
   */
  @Override
  public void register(Registrar registrar) {
    LOGGER.debug("Registering CheckRegistrar {}", AcmeChecksRegistration.class);

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
    return new Class[] {NoOpRule.class};
  }

  @SuppressWarnings("unchecked")
  public static Class<? extends OpenEdgeDumpFileCheck>[] dbCheckClasses() {
    return new Class[] {};
  }
}
