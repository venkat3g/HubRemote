package app.venkat.hubremote;

import android.util.Log;
import android.widget.BaseAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;


public class NetworkManager {

    static File networks;
    private static ArrayList<NetworkManager> networkList = new ArrayList<>();
    final static Scanner fileScanner;

    private String name;
    private String ip;
    private int port;
    private String path;

    static {

        networks = new File(MainActivity.localPath, "network.txt");
        if (!networks.exists()) {
            try {
                networks.createNewFile();
            } catch (IOException e) {
                Log.e("ERROR", "Could not initialized network.txt");
            }
        }
        Scanner tempScanner;
        try {
            tempScanner = new Scanner(networks);
        } catch (FileNotFoundException e) {
            tempScanner = null;
            Log.e("File Error", "Could not find network.txt file");
        }
        fileScanner = tempScanner;
    }

    private NetworkManager(String namePath) {
        name = namePath.substring(0, namePath.indexOf(":"));
        ip = namePath.substring(namePath.indexOf(":") + 1, namePath.indexOf(":", namePath.indexOf(":") + 1));
        port = Integer.parseInt(namePath.substring(namePath.indexOf(":", namePath.indexOf(":") + 1) + 1, namePath.length()));
        path = namePath;
    }

    private static void updateList() {
        try {
            while (fileScanner.hasNextLine()) {
                networkList.add(new NetworkManager(fileScanner.nextLine()));
            }
        } catch (NullPointerException e) {
            Log.e("Scanner error", "Scanner is null");
        }

    }

    public static ArrayList<NetworkManager> getList() {
        updateList();
        return networkList;
    }


    public static void add(String name, String ip, int port) {

        try {
            PrintWriter pw = new PrintWriter(new FileWriter(networks, true));
            pw.println(name + ":" + ip + ":" + port);
            pw.close();
            networkList.add(new NetworkManager(name + ":" + ip + ":" + port));


        } catch (IOException e) {
            Log.e("File Write Error", "Could not write to network.txt file");
        }

        //TODO not working
        if (MainActivity.adapter != null) {
            ((BaseAdapter) MainActivity.adapter).notifyDataSetChanged();
        }
    }

    public String getName() {
        return name;
    }

    public String getIP() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String toString() {
        return "" + name;
    }

    public String getPath() {
        return path;
    }

    public static void remove(NetworkManager itemAtPosition) {
        networkList.remove(itemAtPosition);
        try {

            PrintWriter pw = new PrintWriter(new FileWriter(networks, false));

            for (int i = 0; i < networkList.size(); i++)
                pw.println(networkList.get(i).getPath());

            ((BaseAdapter) MainActivity.adapter).notifyDataSetChanged();

            pw.close();
        } catch (IOException e) {
            Log.e("ERROR", "" + e);
        }


    }

}
