package com.acme.rules;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.rule.RuleKey;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class NoOpRuleTest extends AbstractTest {
  private RuleKey ruleKey;

  @BeforeTest
  public void init() {
    ruleKey = RuleKey.parse("acme-main:NoOpRule");
  }

  @Test
  public void test1() {
    InputFile inputFile = getInputFile("noop01.p");
    NoOpRule rule = new NoOpRule();
    rule.setContext(ruleKey, context, null);
    rule.initialize();
    rule.sensorExecute(inputFile, getParseUnit(inputFile));

    // This line has to be updated to match the rule's logic
    Assert.assertEquals(context.allIssues().size(), 0);
  }

}
