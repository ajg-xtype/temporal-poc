package io.xtype.temporal.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface LongRunningRemoteActivity {

  @ActivityMethod(name = "longRunningTaskInRemoteWorker")
  void longRunningTaskInRemoteWorker();
}
