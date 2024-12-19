package io.xtype.temporal.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import io.xtype.temporal.TaskQueue;
import io.xtype.temporal.activity.LocalDemoActivities;
import io.xtype.temporal.activity.LongRunningRemoteActivity;
import io.xtype.temporal.activity.SimpleRemoteActivity;
import java.time.Duration;

@WorkflowImpl(taskQueues = {TaskQueue.WORKFLOW_V1})
public class DemoWorkflowImpl implements DemoWorkflow {

  private String message = "";
  private String remoteResult;

  private final LocalDemoActivities localActivities = Workflow.newActivityStub(
      LocalDemoActivities.class,
      ActivityOptions.newBuilder()
          .setStartToCloseTimeout(Duration.ofSeconds(5))
          .setTaskQueue(TaskQueue.WORKFLOW_V1)
          .build());

  private final SimpleRemoteActivity simpleRemoteActivity = Workflow.newActivityStub(
      SimpleRemoteActivity.class, ActivityOptions.newBuilder()
          .setStartToCloseTimeout(Duration.ofSeconds(5))
          .setTaskQueue(TaskQueue.TASK_WORKER_V1)
          .build());

  private final LongRunningRemoteActivity longRunningRemoteActivity = Workflow.newActivityStub(
      LongRunningRemoteActivity.class, ActivityOptions.newBuilder()
          .setScheduleToCloseTimeout(Duration.ofHours(1))
          .setHeartbeatTimeout(Duration.ofSeconds(30))
          .setTaskQueue(TaskQueue.TASK_WORKER_V1)
          .build());

  // WorkflowMethod
  @Override
  public String exec(WorkflowInput input) {
    longRunningRemoteActivity.longRunningTaskInRemoteWorker();

    remoteResult = simpleRemoteActivity.doSomethingInRemoteWorker(input.firstName());

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
