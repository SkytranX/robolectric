
package org.robolectric.shadows;

import static com.google.common.truth.Truth.assertThat;
import static org.robolectric.Shadows.shadowOf;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.shadow.api.Shadow;

@RunWith(AndroidJUnit4.class)
public class ShadowBluetoothA2dpTest {
  private static final String MOCK_MAC_ADDRESS = "00:11:22:33:AA:BB";
  private final BluetoothDevice bluetoothDevice =
      ShadowBluetoothDevice.newInstance(MOCK_MAC_ADDRESS);
  private BluetoothA2dp bluetoothA2dp;

  @Before
  public void setUp() throws Exception {
    bluetoothA2dp = Shadow.newInstanceOf(BluetoothA2dp.class);
  }

  @Test
  public void getConnectionState_isA2dp() {
    assertThat(bluetoothA2dp.getConnectionState(bluetoothDevice)).isNotNull();
  }

  @Test
  public void getConnectedDevices_bluetoothConnected_reflectsAddDevice() {
    assertThat(bluetoothA2dp.getConnectedDevices()).isEmpty();

    shadowOf(bluetoothA2dp).addDevice(bluetoothDevice, BluetoothProfile.STATE_CONNECTED);
    assertThat(bluetoothA2dp.getConnectedDevices()).isNotEmpty();
  }

  @Test
  public void getConnectedDevices_bluetoothConnected_reflectsRemoveDevice() {
    assertThat(bluetoothA2dp.getConnectedDevices()).isEmpty();

    shadowOf(bluetoothA2dp).addDevice(bluetoothDevice, BluetoothProfile.STATE_CONNECTED);
    assertThat(bluetoothA2dp.getConnectedDevices()).isNotEmpty();

    shadowOf(bluetoothA2dp).removeDevice(bluetoothDevice);
    assertThat(bluetoothA2dp.getConnectedDevices()).isEmpty();
  }
}
