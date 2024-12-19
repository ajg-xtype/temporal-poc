import {clearInterval, setInterval} from "timers";
import {activityInfo, cancellationSignal, heartbeat} from "@temporalio/activity";

export const doSomething = async (input: string): Promise<string> => {
  return `${input} extended`;
}

export const longRunningTask = async (): Promise<void> => {
  const heartbeatTimeout = activityInfo().heartbeatTimeoutMs ?? 30000
  let heartbeatInterval = setInterval(() => heartbeat(), heartbeatTimeout / 2)
  let counter = 1
  const id = activityInfo().workflowExecution.workflowId + "_" + activityInfo().workflowExecution.runId
  const signal = cancellationSignal()
  try {
    while (true) {
      if (signal.aborted) {
        console.log(`${id} - signal aborted, activity stopped`)
        break;
      }
      console.log(`${id} - long running task still running: ${counter++}`)
      await new Promise((resolve) => setTimeout(resolve, 3000))
    }
  } finally {
    if (heartbeatInterval) {
      console.log(`${id} - cleanup of heartbeat interval`)
      clearInterval(heartbeatInterval)
    }
  }
}
