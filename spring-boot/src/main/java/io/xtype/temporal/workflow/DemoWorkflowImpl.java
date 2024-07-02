package io.xtype.temporal.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import io.xtype.temporal.TaskQueue;
import io.xtype.temporal.activity.LocalDemoActivities;
import io.xtype.temporal.activity.RemoteDemoActivities;
import java.time.Duration;

@WorkflowImpl(taskQueues = {TaskQueue.DEMO_V1})
public class DemoWorkflowImpl implements DemoWorkflow {

  private String message = "";
  private String remoteResult;

  private final ActivityOptions activityOptions = ActivityOptions.newBuilder()
      .setStartToCloseTimeout(Duration.ofSeconds(5))
      .build();

  private final LocalDemoActivities localActivities = Workflow.newActivityStub(
      LocalDemoActivities.class,
      activityOptions);

  private final RemoteDemoActivities remoteActivities = Workflow.newActivityStub(
      RemoteDemoActivities.class, activityOptions);

  // WorkflowMethod
  @Override
  public String exec(WorkflowInput input) {

    remoteResult = remoteActivities.doSomethingInRemoteWorker(input.firstName());

    Workflow.await(() -> !message.isEmpty());

    var localResult = localActivities.doSomethingInLocalWorker(input.lastName());

    return remoteResult + " " + localResult + ": " + message;
  }

  // SignalMethod
  @Override
  public void userInput(String message) {
    this.message = message;
  }

  // QueryMethod
  @Override
  public String getRemoteResult() {
    return remoteResult;
  }
}
