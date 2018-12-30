package com.acme.rules;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.sonar.api.batch.fs.InputFile;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TooManyParametersTest extends AbstractTest {

  @Test
  public void test1() {
    InputFile inputFile = getInputFile("toomanyparams01.p");
    TooManyParameters rule = new TooManyParameters();
    rule.setContext(ruleKey, context, null);
    rule.initialize();
    rule.numParameters = 5;
    rule.sensorExecute(inputFile, getParseUnit(inputFile));

    List<Integer> lines = context.allIssues().stream().map(
        issue -> issue.primaryLocation().textRange().start().line()).sorted().collect(Collectors.toList());
    Assert.assertEquals(context.allIssues().size(), 7);
    Assert.assertEquals(lines, Arrays.asList(new Integer[] {5, 6, 12, 18, 21, 22, 26}));
  }

}
