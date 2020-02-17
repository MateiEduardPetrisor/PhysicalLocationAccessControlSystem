package com.example.user.l_1_1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int permissionCode = 99;
    private static boolean permissionOk = true;
    public DatabaseHelper databaseHelper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        if (checkPermission(Manifest.permission.SEND_SMS) && checkPermission(Manifest.permission.RECEIVE_SMS) && checkPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED)) {

        } else {
            permissionOk = false;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.RECEIVE_BOOT_COMPLETED},
                    permissionCode);
        }

        final ListView lvMessages = (ListView) findViewById(R.id.lvMessages);
        List<String> lstMessages;
        try {
            lstMessages = databaseHelper.getEventsInString();
            MessagesAdapter messagesAdapter = new MessagesAdapter(getApplicationContext(), R.layout.message_details1, lstMessages);
            lvMessages.setAdapter(messagesAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        lvMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), HousePlanActivity.class);
                String item = lvMessages.getItemAtPosition(position).toString();
                intent.putExtra("itemData", item);
                startActivity(intent);
            }
        });


        Button clearEvents = (Button) findViewById(R.id.btnClearEvents);
        clearEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvMessages.removeAllViewsInLayout();
                databaseHelper.clearEvents();
                Toast.makeText(getApplicationContext(), "All Events Cleared", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case permissionCode:
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    permissionOk = true;
                }
                return;
        }
    }
}
