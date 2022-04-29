package com.example.testrrandroidaar2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.robotraconteur.*;
import com.robotraconteur.*;
import com.robotraconteur.javatest.*;
import com.robotraconteur.testing.TestService1.com__robotraconteur__testing__TestService1Factory;
import com.robotraconteur.testing.TestService2.com__robotraconteur__testing__TestService2Factory;
import java.io.*;
import java.util.*;
import java.net.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RobotRaconteurNode.s().setLogLevelFromString("DEBUG");
        TcpTransport c=new TcpTransport();
        c.enableNodeDiscoveryListening();
        RobotRaconteurNode.s().registerServiceType(new com__robotraconteur__testing__TestService1Factory());
        RobotRaconteurNode.s().registerServiceType(new com__robotraconteur__testing__TestService2Factory());
        RobotRaconteurNode.s().registerTransport(c);
    }

    private void runTest2(String url, String authurl)
    {
        Looper.prepare();

        try {
            ServiceTestClient client = new ServiceTestClient();
            client.connectService(url);
            client.TestProperties();
            client.TestFunctions();
            client.TestEvents();
            client.TestObjRefs();
            client.TestPipes();
            client.TestCallbacks();
            client.TestWires();
            //TestMemories();

            client.disconnectService();

            client.TestAuthentication(authurl);
            client.TestObjectLock(authurl);
            client.TestMonitorLock(url);

            client.TestAsync(authurl);

            System.out.println("detected services:");

            ServiceInfo2[] ret=RobotRaconteurNode.s().findServiceByType(
                    "com.robotraconteur.testing.TestService1.testroot",new String[] {"rr+tcp"}
            );
            for (ServiceInfo2 s : ret)
            {
                print_ServiceInfo2(s);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            Context context = getApplicationContext();
            CharSequence text = "Error: " + e.toString();
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }
        Context context = getApplicationContext();
        CharSequence text = "Test Complete";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        System.out.println("Robot Raconteur Test Done!");
    }

    public void runTest(View v)
    {
        Thread thread = new Thread() {
            public void run() {
                runTest2("rr+tcp://192.168.1.118:22222?service=RobotRaconteurTestService",
                        "rr+tcp://192.168.1.118:22222?service=RobotRaconteurTestService_auth");
            }
        };

        thread.start();

    }

    static void print_ServiceInfo2(ServiceInfo2 s)
    {
        System.out.println("Name: " + s.Name);
        System.out.println("RootObjectType: " + s.RootObjectType);
        System.out.println("RootObjectImplements: " + join(", ", s.RootObjectImplements));
        System.out.println("ConnectionURL: " + join(", ",s.ConnectionURL));
        System.out.println("Attributes: " + s.Attributes);
        System.out.println("NodeID: " + s.NodeID.toString());
        System.out.println("NodeName: " + s.NodeName);
        System.out.println("");
    }

    static String join(String s, String[] a)
    {
        if (a.length==0) return "";
        String o=a[0];
        for (int i=1; i<a.length; i++)
        {
            o+=(s + a[i]);
        }
        return o;
    }

}