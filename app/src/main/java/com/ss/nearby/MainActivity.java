package com.ss.nearby;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.ss.nearby.adapter.EndpointListAdapter;
import com.ss.nearby.fragment.ChatFragment;
import com.ss.nearby.fragment.EndpointListFragment;
import com.ss.nearby.model.Endpoint;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "NearbyChat";

    private final static String[] REQUIRED_PERMISSIONS =
            new String[]{
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };

    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;

    private final static Strategy STRATEGY = Strategy.P2P_STAR;

    private final static String mCodeName = "User " + new Random().nextInt(100);


    private ConnectionsClient mConnectionsClient;

    private String mOpponentName;
    private String mOpponentId;

    private ConnectionLifecycleCallback mConnectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
            Log.i(TAG, "onConnectionInitiated: accepting connection");
            mConnectionsClient.acceptConnection(s, mPayloadCallback);
            mOpponentId = s;
            mOpponentName = connectionInfo.getEndpointName();
        }

        @Override
        public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution connectionResolution) {
            if (connectionResolution.getStatus().isSuccess()) {
                Log.i(TAG, "onConnectionResult: connection successful");

                mConnectionsClient.stopAdvertising();
                mConnectionsClient.stopDiscovery();

            }
        }

        @Override
        public void onDisconnected(@NonNull String s) {
            Log.i(TAG, "onDisconnected: disconnected from the opponent");
        }
    };

    private EndpointDiscoveryCallback mEndpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String s, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
            Log.i(TAG, "onEndpointFound: endpoint found");
            mConnectionsClient.requestConnection(mCodeName, s, mConnectionLifecycleCallback);

            EndpointListFragment endpointListFragment = (EndpointListFragment) getFragmentManager().findFragmentByTag("tag1");

            endpointListFragment.addEndpoint(new Endpoint(s, discoveredEndpointInfo.getEndpointName()));
        }

        @Override
        public void onEndpointLost(@NonNull String s) {
            Log.i(TAG, "onEndpointFound: endpoint lost");
            EndpointListFragment endpointListFragment = (EndpointListFragment) getFragmentManager().findFragmentByTag("tag1");

            endpointListFragment.removeEndpointById(s);
        }
    };

    private PayloadCallback mPayloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
            Log.i(TAG, "onPayloadReceived: message received");
            Toast.makeText(MainActivity.this, new String(payload.asBytes()), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle(getString(R.string.app_name));

        mConnectionsClient = Nearby.getConnectionsClient(this);

        openEndpointListFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
        }

        startAdvertising();
    }

    private boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != REQUEST_CODE_REQUIRED_PERMISSIONS)
            return;

        for (int granResult : grantResults) {
            if (granResult == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Application can't start without required permissions", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }
        recreate();
    }

    private void startAdvertising() {
        mConnectionsClient.startAdvertising(mCodeName, getPackageName(), mConnectionLifecycleCallback,
                new AdvertisingOptions.Builder()
                        .setStrategy(STRATEGY)
                        .build());
    }

    private void startDiscovery() {
        mConnectionsClient.startDiscovery(getPackageName(), mEndpointDiscoveryCallback,
                new DiscoveryOptions.Builder()
                        .setStrategy(STRATEGY)
                        .build());
    }

    private void sendMessage(String text) {
        mConnectionsClient.sendPayload(mOpponentId, Payload.fromBytes(text.getBytes(StandardCharsets.UTF_8)));
    }

    private void openEndpointListFragment() {
        EndpointListFragment endpointListFragment = new EndpointListFragment();
        endpointListFragment.setItemClickListener(new EndpointListAdapter.OnItemClickListener() {
            @Override
            public void onClick(Endpoint endpoint) {
                Log.i(TAG, "onEndpoint: request connection");
//                mConnectionsClient.requestConnection(mCodeName, endpoint.getId(), mConnectionLifecycleCallback);
                openChatFragment();
            }
        });

        getFragmentManager().beginTransaction().replace(R.id.fragment_container, endpointListFragment, "tag1").commit();
    }

    private void openChatFragment() {
        ChatFragment chatFragment = ChatFragment.newInstance(mOpponentName);
        chatFragment.setListener(new ChatFragment.OnSendButtonClick() {
            @Override
            public void onSend(String text) {
                sendMessage(text);
            }
        });
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, chatFragment, "tag2")
                .addToBackStack(null).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                startDiscovery();
                return true;
            case android.R.id.home:
                getFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
