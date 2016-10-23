package app.venkat.hubremote;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {


    static Socket socket;
    static File localPath;
    static ListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        localPath = getFilesDir();

        File networks = NetworkManager.networks;


        ListView listView = (ListView) findViewById(R.id.networks);


        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, NetworkManager.getList());
        listView.setAdapter(adapter);


        TextView add_network = (TextView) findViewById(R.id.add_network);

        add_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Add_Network.class));
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("Clicked", ((NetworkManager) adapterView.getItemAtPosition(i))
                        .getName());
                connect((NetworkManager) adapterView.getItemAtPosition(i));
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                                // TODO add edit item option
                                                @Override
                                                public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int p, long l) {
                                                    new AlertDialog.Builder(MainActivity.this)
                                                            .setTitle("Delete?")
                                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    // Removes button

                                                                }
                                                            })
                                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    // Does nothing
                                                                    NetworkManager.remove((NetworkManager) adapterView.getItemAtPosition(p));
                                                                }
                                                            })
                                                            .setMessage("Delete Item?")
                                                            .show();


                                                    return true;
                                                }
                                            }

        );

    }

    private void connect(NetworkManager item) {
        try {
            final String ip = item.getIP(); // Gets the IP address from EditText field, ip
            final int port = item.getPort(); // Gets the port from EditText field, port

            final Socket client = new Socket(); // Creates a socket
            // Handler that allows alert dialog to display on current view.
            final Handler handler = new Handler();

            // Creates a Thread to run in background to connect to server
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d("MainActivity Network", "Attempt");

                        // Connects socket to ip address specified and port, waiting for 5 seconds
                        client.connect(new InetSocketAddress(ip, port), 5000);
                        // Lets Log know that device has successfully connected
                        if (client.isConnected()) {
                            Log.d("Button pushed", "Client has connected.");
                        }
                        socket = client;

                        startActivity(new Intent(MainActivity.this, Connected.class));

                    } catch (final IOException e) {

                        Log.d("Exception", e.getMessage());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Error Connecting")
                                        .setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                // Does nothing
                                            }
                                        })
                                        .setMessage("Failed to connect to " + ip + "\n Port:" + port)
                                        .show();
                            }
                        });


                    }
                }
            }).start();

        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

        }
    }

}
