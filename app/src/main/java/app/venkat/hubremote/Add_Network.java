package app.venkat.hubremote;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Add_Network extends AppCompatActivity {

    static Button add;
    static EditText ip, port;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_network);

        final EditText name = (EditText) findViewById(R.id.name);
        add = (Button) findViewById(R.id.add);
        ip = (EditText) findViewById(R.id.IP);
        port = (EditText) findViewById(R.id.port);
        Button cancel = (Button) findViewById(R.id.cancel_add_network);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String tempName = name.getText().toString();
                    String tempIP = ip.getText().toString();
                    int tempPort = Integer.parseInt(port.getText().toString());

                    NetworkManager.add(tempName, tempIP, tempPort);
                    finish();
                } catch (NumberFormatException | NullPointerException e) {
                    Log.d("Error", "" + e);
                    if (e instanceof NumberFormatException)
                        Toast.makeText(Add_Network.this, "Missing or invalid port", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(Add_Network.this, "Missing field(s)", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.d("Error", "" + e);
                }
            }
        });


    }


}
