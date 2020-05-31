
package org.robolectric.shadows;

import static com.google.common.truth.Truth.assertThat;
import static org.robolectric.Shadows.shadowOf;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothDevice;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.android.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.shadow.api.Shadow;

@RunWith(AndroidJUnit4.class)
public class ShadowBluetoothA2dpTest {
  private static final String MOCK_MAC_ADDRESS = "00:11:22:33:AA:BB";
  private BluetoothA2dp bluetoothA2dp;

  @Before
  public void setUp() throws Exception {
    bluetoothA2dp = Shadow.newInstanceOf(BluetoothA2dp.class);
  }

  @Test
  public void getConnectionState_isA2dp() {
    assertThat(
            bluetoothA2dp.getConnectionState(ShadowBluetoothDevice.newInstance(MOCK_MAC_ADDRESS)))
        .isNotNull();
  }

  @Test
  public void getConnectedDevices_reflectsSetConnectedDevices() {
    assertThat(bluetoothA2dp.getConnectedDevices()).isEmpty();
    shadowOf(bluetoothA2dp)
        .setConnectedDevices(Lists.newArrayList(Shadow.newInstanceOf(BluetoothDevice.class)));
    assertThat(bluetoothA2dp.getConnectedDevices()).isNotEmpty();
  }
}
