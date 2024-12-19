package io.xtype.temporal.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface SimpleRemoteActivity {

  @ActivityMethod(name = "doSomethingInRemoteWorker")
  String doSomethingInRemoteWorker(String input);
}
