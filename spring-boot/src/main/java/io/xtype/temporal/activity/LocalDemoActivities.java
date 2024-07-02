package io.xtype.temporal.activity;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface LocalDemoActivities {

  String doSomethingInLocalWorker(String input);
}
