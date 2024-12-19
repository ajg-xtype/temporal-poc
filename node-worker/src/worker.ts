import {NativeConnection, Runtime, Worker} from '@temporalio/worker';
import {doSomething, longRunningTask} from './activities';

async function run() {
  Runtime.install({
    telemetryOptions: {
      metrics: {
        prometheus: {
          bindAddress: "0.0.0.0:9464",
        },
      },
    },
  })

  const connection = await NativeConnection.connect({
    address: 'localhost:7233',
  });

  const worker = await Worker.create({
    connection,
    namespace: 'default',
    taskQueue: 'task-worker-v1',
    // Workflows are registered using a path as they run in a separate JS context.
    //workflowsPath: require.resolve('./workflows'),
    activities: {
      doSomethingInRemoteWorker: doSomething,
      longRunningTaskInRemoteWorker: longRunningTask
    },
  });

  await worker.run();
}

run().catch((err) => {
  console.error(err);
  process.exit(1);
});
