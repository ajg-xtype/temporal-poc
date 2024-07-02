package io.xtype.temporal.activity;

import io.temporal.spring.boot.ActivityImpl;
import io.xtype.temporal.TaskQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ActivityImpl(taskQueues = {TaskQueue.DEMO_V1})
public class LocalDemoActivitiesImpl implements LocalDemoActivities {

  private static final Logger LOGGER = LoggerFactory.getLogger(LocalDemoActivitiesImpl.class);

  @Override
  public String doSomethingInLocalWorker(String input) {
    LOGGER.info("called doSomethingInLocalWorker");
    return input + " extended";
  }
}
