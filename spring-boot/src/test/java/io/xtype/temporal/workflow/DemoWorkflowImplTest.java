package io.xtype.temporal.workflow;

import static org.assertj.core.api.Assertions.assertThat;

import io.temporal.api.history.v1.HistoryEvent;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.testing.TestWorkflowEnvironment;
import io.xtype.temporal.TaskQueue;
import io.xtype.temporal.activity.LocalDemoActivities;
import io.xtype.temporal.activity.LongRunningRemoteActivity;
import io.xtype.temporal.activity.SimpleRemoteActivity;
import io.xtype.temporal.workflow.DemoWorkflow.WorkflowInput;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoWorkflowImplTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(DemoWorkflowImplTest.class);

  @Autowired
  private LocalDemoActivities localDemoActivities;

  @Test
  void sunshine_case() throws TimeoutException {
    var testEnv = TestWorkflowEnvironment.newInstance();
    var localWorker = testEnv.newWorker(TaskQueue.WORKFLOW_V1);
    localWorker.registerWorkflowImplementationTypes(DemoWorkflowImpl.class);
    localWorker.registerActivitiesImplementations(localDemoActivities);

    var remoteWorker = testEnv.newWorker(TaskQueue.TASK_WORKER_V1);
    remoteWorker.registerActivitiesImplementations(
        (SimpleRemoteActivity) input -> input + " extended",
        (LongRunningRemoteActivity) () -> LOGGER.info("longRunningTaskInRemoteWorker called")
    );

    testEnv.start();

    var client = testEnv.getWorkflowClient();
    var workflow = client.newWorkflowStub(
        DemoWorkflow.class,
        WorkflowOptions.newBuilder().setTaskQueue(TaskQueue.WORKFLOW_V1).build()
    );
    var workflowExecution = WorkflowClient.start(workflow::exec, new WorkflowInput("John", "Doe"));
    assertThat(workflowExecution.getWorkflowId()).isNotBlank();

    workflow.userInput("hi there");

    var result = WorkflowStub.fromTyped(workflow).getResult(5, TimeUnit.SECONDS, String.class);
    assertThat(result).isEqualTo("John extended Doe extended: hi there");

    var history = client.fetchHistory(workflowExecution.getWorkflowId(),
        workflowExecution.getRunId());
    assertThat(history.getHistory().getEventsList().stream()
        .filter(HistoryEvent::hasWorkflowExecutionSignaledEventAttributes)).hasSize(
        1); // 1 signal received = userInput
  }
}
