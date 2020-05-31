package org.robolectric.shadows;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import java.util.ArrayList;
import java.util.List;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

/** Shadow of {@link BluetoothA2dp}. */
@Implements(BluetoothA2dp.class)
public class ShadowBluetoothA2dp {
  private List<BluetoothDevice> bluetoothDevices = new ArrayList<>();

  public void setConnectedDevices(List<BluetoothDevice> bluetoothDevices) {
    this.bluetoothDevices = bluetoothDevices;
  }

  @Implementation
  protected List<BluetoothDevice> getConnectedDevices() {
    return bluetoothDevices;
  }

  @Implementation
  protected int getConnectionState(BluetoothDevice device) {
    return BluetoothProfile.A2DP;
  }
}
