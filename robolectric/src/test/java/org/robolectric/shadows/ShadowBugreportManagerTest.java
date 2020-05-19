package org.robolectric.shadows;

import static android.os.Build.VERSION_CODES.Q;
import static com.google.common.truth.Truth.assertThat;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static org.mockito.Mockito.mock;

import android.content.Context;
import android.os.BugreportManager;
import android.os.BugreportManager.BugreportCallback;
import android.os.BugreportParams;
import android.os.IDumpstate;
import android.os.ParcelFileDescriptor;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.io.File;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.shadow.api.Shadow;

/** Tests for {@link ShadowBugreportManager}. */
@RunWith(AndroidJUnit4.class)
@Config(minSdk = Q)
public final class ShadowBugreportManagerTest {

  private static final int NO_ERROR = 0;

  BugreportManager bugreportManager;
  ShadowBugreportManager shadowBugreportManager;
  Context context = ApplicationProvider.getApplicationContext();

  @Before
  public void setUp() {
    IDumpstate binder = mock(IDumpstate.class);
    bugreportManager = new BugreportManager(context, binder);
    shadowBugreportManager = Shadow.extract(bugreportManager);
  }

  @Test
  public void startBugreport() throws Exception {
    BugreportCallbackImpl callback = new BugreportCallbackImpl();
    bugreportManager.startBugreport(
        createWriteFile("bugreport"),
        createWriteFile("screenshot"),
        new BugreportParams(BugreportParams.BUGREPORT_MODE_FULL),
        directExecutor(),
        callback);

    assertThat(shadowBugreportManager.isBugreportInProgress()).isTrue();
    assertThat(callback.finished).isFalse();
    assertThat(callback.error).isEqualTo(NO_ERROR);
  }

  @Test
  public void startTwoBugreports() throws Exception {
    BugreportCallbackImpl callback = new BugreportCallbackImpl();
    bugreportManager.startBugreport(
        createWriteFile("bugreport"),
        createWriteFile("screenshot"),
        new BugreportParams(BugreportParams.BUGREPORT_MODE_FULL),
        directExecutor(),
        callback);

    assertThat(shadowBugreportManager.isBugreportInProgress()).isTrue();
    assertThat(callback.finished).isFalse();
    assertThat(callback.error).isEqualTo(NO_ERROR);

    BugreportCallbackImpl newCallback = new BugreportCallbackImpl();
    bugreportManager.startBugreport(
        createWriteFile("bugreport_new"),
        createWriteFile("screenshot_new"),
        new BugreportParams(BugreportParams.BUGREPORT_MODE_FULL),
        directExecutor(),
        newCallback);

    assertThat(shadowBugreportManager.isBugreportInProgress()).isTrue();
    assertThat(newCallback.finished).isTrue();
    assertThat(newCallback.error)
        .isEqualTo(BugreportCallback.BUGREPORT_ERROR_ANOTHER_REPORT_IN_PROGRESS);
    assertThat(callback.finished).isFalse();
    assertThat(callback.error).isEqualTo(NO_ERROR);

    shadowBugreportManager.executeOnFinished();

    assertThat(shadowBugreportManager.isBugreportInProgress()).isFalse();
    assertThat(callback.finished).isTrue();
    assertThat(callback.error).isEqualTo(NO_ERROR);
  }

  @Test
  public void cancelBugreport() throws Exception {
    BugreportCallbackImpl callback = new BugreportCallbackImpl();
    bugreportManager.startBugreport(
        createWriteFile("bugreport"),
        createWriteFile("screenshot"),
        new BugreportParams(BugreportParams.BUGREPORT_MODE_FULL),
        directExecutor(),
        callback);

    assertThat(shadowBugreportManager.isBugreportInProgress()).isTrue();
    assertThat(callback.finished).isFalse();
    assertThat(callback.error).isEqualTo(NO_ERROR);

    bugreportManager.cancelBugreport();

    assertThat(shadowBugreportManager.isBugreportInProgress()).isFalse();
    assertThat(callback.finished).isTrue();
    assertThat(callback.error).isEqualTo(BugreportCallback.BUGREPORT_ERROR_RUNTIME);
  }

  @Test
  public void executeOnError() throws Exception {
    BugreportCallbackImpl callback = new BugreportCallbackImpl();
    bugreportManager.startBugreport(
        createWriteFile("bugreport"),
        createWriteFile("screenshot"),
        new BugreportParams(BugreportParams.BUGREPORT_MODE_FULL),
        directExecutor(),
        callback);

    assertThat(shadowBugreportManager.isBugreportInProgress()).isTrue();
    assertThat(callback.finished).isFalse();
    assertThat(callback.error).isEqualTo(NO_ERROR);

    shadowBugreportManager.executeOnError(BugreportCallback.BUGREPORT_ERROR_INVALID_INPUT);

    assertThat(shadowBugreportManager.isBugreportInProgress()).isFalse();
    assertThat(callback.finished).isTrue();
    assertThat(callback.error).isEqualTo(BugreportCallback.BUGREPORT_ERROR_INVALID_INPUT);
  }

  @Test
  public void executeOnFinished() throws Exception {
    BugreportCallbackImpl callback = new BugreportCallbackImpl();
    bugreportManager.startBugreport(
        createWriteFile("bugreport"),
        createWriteFile("screenshot"),
        new BugreportParams(BugreportParams.BUGREPORT_MODE_FULL),
        directExecutor(),
        callback);

    assertThat(shadowBugreportManager.isBugreportInProgress()).isTrue();
    assertThat(callback.finished).isFalse();
    assertThat(callback.error).isEqualTo(NO_ERROR);

    shadowBugreportManager.executeOnFinished();

    assertThat(shadowBugreportManager.isBugreportInProgress()).isFalse();
    assertThat(callback.finished).isTrue();
    assertThat(callback.error).isEqualTo(NO_ERROR);
  }

  @Test
  public void isBugreportInProgress() throws Exception {
    assertThat(shadowBugreportManager.isBugreportInProgress()).isFalse();

    BugreportCallbackImpl callback = new BugreportCallbackImpl();
    bugreportManager.startBugreport(
        createWriteFile("bugreport"),
        createWriteFile("screenshot"),
        new BugreportParams(BugreportParams.BUGREPORT_MODE_FULL),
        directExecutor(),
        callback);

    assertThat(shadowBugreportManager.isBugreportInProgress()).isTrue();
    assertThat(callback.finished).isFalse();
    assertThat(callback.error).isEqualTo(NO_ERROR);

    shadowBugreportManager.executeOnFinished();

    assertThat(shadowBugreportManager.isBugreportInProgress()).isFalse();
    assertThat(callback.finished).isTrue();
    assertThat(callback.error).isEqualTo(NO_ERROR);
  }

  static final class BugreportCallbackImpl extends BugreportCallback {
    private int error = NO_ERROR;
    private boolean finished = false;

    @Override
    public void onProgress(float progress) {}

    @Override
    public void onError(int errorCode) {
      error = errorCode;
      finished = true;
    }

    @Override
    public void onFinished() {
      finished = true;
    }
  }

  private ParcelFileDescriptor createWriteFile(String fileName) throws IOException {
    File f = new File(context.getFilesDir(), fileName);
    if (f.exists()) {
      f.delete();
    }
    f.createNewFile();
    return ParcelFileDescriptor.open(
        f, ParcelFileDescriptor.MODE_WRITE_ONLY | ParcelFileDescriptor.MODE_APPEND);
  }
}
