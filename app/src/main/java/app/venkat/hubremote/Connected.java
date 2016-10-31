package app.venkat.hubremote;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Connected extends Activity {


    private static ListAdapter adapter;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        Button ret = (Button) findViewById(R.id.ret);
        Button reload = (Button) findViewById(R.id.reload);


        final ArrayList<String> programList = new ArrayList<>();


        // Thread to get input from the server
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    out = new ObjectOutputStream(MainActivity.socket.getOutputStream());
                    out.writeObject("Client Connected");
                    //out.close();


                    in = new ObjectInputStream(MainActivity.socket.getInputStream());
                    Object temp = in.readObject();

                    if (temp instanceof ArrayList<?>) {
                        if (((ArrayList) temp).get(0) instanceof String) ;
                        programList.addAll((ArrayList<String>) temp);
                    }
                    Log.d("Connection", "Completed");
                    Log.d("Programs", programList.toString());


                    if (programList.size() == 0) {
                        runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              Toast.makeText(Connected.this, "No programs loaded from server", Toast.LENGTH_LONG).show();
                                          }
                                      }

                        );
                    }

                } catch (IOException |
                        ClassNotFoundException e
                        )

                {
                    Log.e("Error", "" + e);
                    finish();
                }
            }
        }

        ).

                start();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, programList);


        final ListView lv = (ListView) findViewById(R.id.listView);

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()

                                  {

                                      @Override
                                      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                          String choice = String.valueOf(adapterView.getItemAtPosition(i));
                                          if (!choice.isEmpty()) {
                                              try {
                                                  Log.v("Message", choice);
                                                  out.writeObject(choice);

                                              } catch (IOException e) {
                                                  Log.e("Error", "Could not send 'choice' to server");
                                              }
                                          }

                                      }
                                  }

        );


        ret.setOnClickListener(new View.OnClickListener()

                               {
                                   @Override
                                   public void onClick(View view) {
                                       finish();
                                   }
                               }

        );

        reload.setOnClickListener(new View.OnClickListener()

                                  {
                                      @Override
                                      public void onClick(final View view) {
                                          new Thread(new Runnable() {
                                              @Override
                                              public void run() {
                                                  try {
                                                      out.writeObject("reload");


                                                      Object temp = in.readObject();

                                                      if (temp instanceof ArrayList<?>) {
                                                          ArrayList<String> newList = (ArrayList<String>) temp;
                                                          int t = programList.size();
                                                          for (int i = 0; i < t; i++)
                                                              programList.remove(0);
                                                          programList.addAll(newList);
                                                          runOnUiThread(new Runnable() {
                                                              @Override
                                                              public void run() {
                                                                  ((BaseAdapter) adapter).notifyDataSetChanged();
                                                              }
                                                          });

                                                      }

                                                  } catch (IOException e) {
                                                      Log.e("ERROR", "Input/Output error with server.");

                                                  } catch (Exception e) {
                                                      Log.e("ERROR", "Something went wrong...." + e);

                                                      if (e instanceof ClassNotFoundException) {
                                                          Log.e("Error", "Object sent Class not found");
                                                      }

                                                  }
                                              }
                                          }).start();
                                      }
                                  }

        );


    }


    @Override
    public void finish() {
        try {
            MainActivity.socket.close();
        } catch (IOException e) {
            Log.e("Error", "Could not close connection to server");
        }
        super.finish();
    }


}

