package org.robolectric.shadows;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

/** Shadow of {@link BluetoothA2dp}. */
@Implements(BluetoothA2dp.class)
public class ShadowBluetoothA2dp {
  private final List<BluetoothDevice> bluetoothDevices = new ArrayList<>();

  public void addDevice(BluetoothDevice bluetoothDevice, int connectionState) {
    if (connectionState != BluetoothProfile.STATE_CONNECTED) {
      return;
    }
    bluetoothDevices.add(bluetoothDevice);
  }

  public void removeDevice(BluetoothDevice bluetoothDevice) {
    bluetoothDevices.remove(bluetoothDevice);
  }

  @Implementation
  protected List<BluetoothDevice> getConnectedDevices() {
    return ImmutableList.copyOf(bluetoothDevices);
  }

  @Implementation
  protected int getConnectionState(BluetoothDevice device) {
    return BluetoothProfile.A2DP;
  }
}
