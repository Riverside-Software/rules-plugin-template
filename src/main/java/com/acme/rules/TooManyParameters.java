package com.acme.rules;

import java.util.ArrayList;
import java.util.List;

import org.prorefactor.core.ABLNodeType;
import org.prorefactor.core.JPNode;
import org.prorefactor.treeparser.ParseUnit;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.openedge.api.checks.OpenEdgeProparseCheck;
import org.sonar.plugins.openedge.api.model.SqaleConstantRemediation;

@Rule(name = "Too many parameters in call", priority = Priority.MAJOR, tags = {"clumsy"})
@SqaleConstantRemediation(value = "2h")
public class TooManyParameters extends OpenEdgeProparseCheck {

  @RuleProperty(description = "Number of parameters", defaultValue = "15")
  public int numParameters = 15;

  @Override
  public void execute(InputFile file, ParseUnit unit) {
    for (JPNode node : unit.getTopNode().query(ABLNodeType.RUN, ABLNodeType.PUBLISH, ABLNodeType.DYNAMICNEW)) {
      JPNode list = getFirstDirectChild(node, ABLNodeType.PARAMETER_LIST);
      if (list != null) {
          int numComma = getDirectChildren(list, ABLNodeType.COMMA).size();
          if (numComma + 1 >= numParameters) {
            reportIssue(file, node, String.format("Statement has %d parameters", numComma));
          }
      }
    }
    // Method call == ID followed by METHOD_PARAM_LIST
    for (JPNode node : unit.getTopNode().query(ABLNodeType.METHOD_PARAM_LIST)) {
      if (node.getPreviousNode().getNodeType() != ABLNodeType.ID)
        continue;
      int numComma = getDirectChildren(node, ABLNodeType.COMMA).size();
      if (numComma + 1 >= numParameters) {
        reportIssue(file, node.getPreviousNode(), String.format("Method call has %d parameters", numComma));
      }
    }
    // No PARAMETER_LIST wrapper on DYNAMIC-FUNCTION and DYNAMIC-INVOKE, just a LEFTPAREN following the keyword
    for (JPNode node : unit.getTopNode().query(ABLNodeType.DYNAMICFUNCTION, ABLNodeType.DYNAMICINVOKE)) {
      // Function / method name is the first argument, so remove 1 from total
      int numComma = getDirectChildren(node, ABLNodeType.COMMA).size();
      if (numComma >= numParameters) {
        reportIssue(file, node, String.format("Statement has %d parameters", numComma));
      }
    }
  }

  @Override
  public String getNoSonarKeyword() {
    return "num_parameters";
  }

  // Next two methods add in JPNode.java in 2.3.0
  // Can then be removed from this class
  private static List<JPNode> getDirectChildren(JPNode node, ABLNodeType type) {
    List<JPNode> ret = new ArrayList<>();
    JPNode n = node.getFirstChild();
    while (n != null) {
      if (n.getNodeType() == type)
        ret.add(n);
      n = n.getNextSibling();
    }
    return ret;
  }

  private static JPNode getFirstDirectChild(JPNode node, ABLNodeType type) {
    JPNode n = node.getFirstChild();
    while (n != null) {
      if (n.getNodeType() == type)
        return n;
      n = n.getNextSibling();
    }
    return null;
  }

}
