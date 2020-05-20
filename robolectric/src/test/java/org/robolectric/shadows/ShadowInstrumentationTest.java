package org.robolectric.shadows;

import static android.os.Build.VERSION_CODES.N;
import static com.google.common.truth.Truth.assertThat;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.robolectric.Shadows.shadowOf;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.concurrent.SynchronousQueue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

/** Tests for the ShadowInstrumentation class. */
@RunWith(AndroidJUnit4.class)
public class ShadowInstrumentationTest {

  /**
   * This tests that concurrent startService operations are handled correctly.
   *
   * <p>It runs 2 concurrent threads, each of which call startService 10000 times. This should be
   * enough to trigger any issues related to concurrent state modifications in the
   * ShadowInstrumentation implementation.
   */
  @Test
  @Config(minSdk = N)
  public void concurrentStartService_hasCorrectStartServiceCount() throws InterruptedException {

    HandlerThread background1Thread = new HandlerThread("background1");
    background1Thread.start();
    Handler handler1 = new Handler(background1Thread.getLooper());
    HandlerThread background2Thread = new HandlerThread("background2");
    background2Thread.start();
    Handler handler2 = new Handler(background2Thread.getLooper());

    Intent intent = new Intent("do_the_thing");
    intent.setClassName("com.blah", "com.blah.service");
    Runnable startServicesTask =
        new Runnable() {
          @Override
          public void run() {
            for (int i = 0; i < 10000; i++) {
              ApplicationProvider.getApplicationContext().startService(intent);
            }
          }
        };

    SynchronousQueue<Boolean> finishedQueue = new SynchronousQueue<>();

    handler1.post(startServicesTask);
    handler2.post(startServicesTask);
    handler1.post(
        () -> {
          try {
            finishedQueue.put(true);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        });
    handler2.post(
        () -> {
          try {
            finishedQueue.put(true);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        });

    int finishedCount = 0;
    for (int i = 0; i < 2; i++) {
      if (finishedQueue.poll(10, SECONDS)) {
        finishedCount++;
      }
    }
    assertThat(finishedQueue).isEmpty();
    assertThat(finishedCount).isEqualTo(2);

    int intentCount = 0;

    while (true) {
      Intent serviceIntent =
          shadowOf((Application) ApplicationProvider.getApplicationContext())
              .getNextStartedService();
      if (serviceIntent == null) {
        break;
      }
      intentCount++;
    }

    assertThat(intentCount).isEqualTo(20000);
  }
}
