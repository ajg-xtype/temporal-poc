package io.xtype.temporal.workflow;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface DemoWorkflow {

  @WorkflowMethod
  String exec(WorkflowInput input);

  @SignalMethod
  void userInput(String message);

  @QueryMethod
  String getRemoteResult();

  record WorkflowInput(String firstName, String lastName) {

  }
}
