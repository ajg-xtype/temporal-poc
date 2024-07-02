package io.xtype.temporal;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowExecutionAlreadyStarted;
import io.temporal.client.WorkflowNotFoundException;
import io.temporal.client.WorkflowOptions;
import io.xtype.temporal.workflow.DemoWorkflow;
import io.xtype.temporal.workflow.DemoWorkflow.WorkflowInput;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TriggerDemoWorkflowController {

  private final WorkflowClient workflowClient;

  public TriggerDemoWorkflowController(WorkflowClient workflowClient) {
    this.workflowClient = workflowClient;
  }

  @RequestMapping(method = RequestMethod.POST, value = "/trigger")
  ResponseEntity<String> triggerWorkflow(@RequestParam("id") String id) {
    var workflow = workflowClient.newWorkflowStub(
        DemoWorkflow.class,
        WorkflowOptions
            .newBuilder()
            .setTaskQueue(TaskQueue.DEMO_V1)
            .setWorkflowId(id)
            .build()
    );

    try {
      var workflowExecution = WorkflowClient.start(workflow::exec,
          new WorkflowInput("John", "Doe"));
      return ResponseEntity.ok(
          workflowExecution.getWorkflowId() + "_" + workflowExecution.getRunId());
    } catch (WorkflowExecutionAlreadyStarted e) {
      return ResponseEntity.badRequest().body("Already running");
    }
  }

  @RequestMapping(method = RequestMethod.POST, value = "/signal")
  ResponseEntity<String> sendSignal(@RequestParam("id") String id) {
    var workflow = workflowClient.newWorkflowStub(DemoWorkflow.class, id);

    try {
      workflow.userInput("hi there");
      return ResponseEntity.ok("ok");
    } catch (WorkflowNotFoundException e) {
      return ResponseEntity.badRequest()
          .body("Workflow with id " + e.getExecution().getWorkflowId() + " not found");
    }
  }

}
