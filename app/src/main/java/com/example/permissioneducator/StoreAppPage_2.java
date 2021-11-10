package com.example.permissioneducator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class StoreAppPage_2 extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ImageButton backButton;
    private Button postButton;
    String postPermissions = "";
    ListView listView;
    String[] permissionArray;
    String[] allPermissionArray;
    private String url = "http://10.0.2.2:5000/naticus";
    private String url2 = "http://10.0.2.2:5000/perm_info";
    AlertDialog.Builder alert;


    /*
    onCreate gives us the buttons on the store app page and creates the spinner the user will
    us to choose specified store apps.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_apps);
        final String TAG = "PM INFO ";
        final PackageManager pm = getPackageManager();




        /*
        This set of lists and the for loop is what filters out the system apps from the store apps.
        This is used later in the code to print out permissions for specified app types. Will need
        to then use these to act as the spinner variables and the text that comes with them.
         */
        List<ApplicationInfo> installedApps = pm.getInstalledApplications(0);
        List<ApplicationInfo> storeApps = new ArrayList<>();
        List<String> storeAppNames = new ArrayList<>();


        for(ApplicationInfo applicationInfo: installedApps) {
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                storeApps.add(applicationInfo);

                //This gives us the proper names of the apps instead of gibberish
                String label = (String)pm.getApplicationLabel(applicationInfo);
                storeAppNames.add(label);
            }
        }
        for(int i = 0; i < storeApps.size(); i++){
            System.out.println(i + ".) Store app: " + storeApps.get(i) + "\n");
        }


        //This sets up our drop-down list for what apps to choose from
        Spinner spinner = findViewById(R.id.store_app_list);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, storeAppNames);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);
    }


    /*
    onItemSelected allows the user to see specified permissions based on what app they choose from the
    drop-down list.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String choice = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), choice, Toast.LENGTH_SHORT).show();

        String appName = (String) parent.getItemAtPosition(position);
        List<String> permissions;
        List<String> allPermissions;
        int i = 0;
        int j = 0;

        //Gets the specified permissions and puts them in the ListView for the user to see.
        permissions = getPermissionList(appName,0);
        permissionArray = new String[permissions.size()];

        allPermissions = getPermissionList(appName, 1);
        allPermissionArray = new String[allPermissions.size()];

        if(permissions.size() == 0){
            permissionArray = new String[permissions.size() + 1];
            permissionArray[0] = "This app has no Dangerous Permissions.";
        }

        //Creates both the array for the ListView as well as the single string for the server
        for(final String permissionsList: permissions){
            permissionArray[i] = permissionsList;
            i++;
        }

        //Sets up the array for the full list of permissions.
        for(final String fullPermissionsList: allPermissions){
            allPermissionArray[j] = fullPermissionsList;
            postPermissions = fullPermissionsList + "," + postPermissions;
            j++;
        }

        //Set up for ListView
        listView = findViewById(R.id.storePermissionList);
        ArrayAdapter<String> permissionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, permissionArray);
        listView.setAdapter(permissionAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String permission = parent.getItemAtPosition(position).toString();
                //Here in place of toast, send the permission to the server to retrieve information
                //Toast.makeText(getApplicationContext(), "You chose the permission " + permission, Toast.LENGTH_LONG).show();
                ServerConnection serverConnection = new ServerConnection();
                RequestBody requestBody = serverConnection.requestBodyPermissionName(permission);
                getPostResponse(requestBody, 1);
            }
        });

        postButton = findViewById(R.id.malButton);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendPermissions(postPermissions);
                ServerConnection serverConnection = new ServerConnection();
                RequestBody requestBody = serverConnection.buildRequestBody(postPermissions);
                getPostResponse(requestBody, 2);

            }
        });
    }

    private void getPostResponse(RequestBody requestBody, int flag) {
        if (flag == 2) {

            Request request = new Request.Builder().post(requestBody).url(url).build();
            new OkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull final Call call, @NotNull final IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            call.cancel();

                        }
                    });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                /*
                Write Dialog code here
                Instead of showing just a Toast, it would be better to have an alert Dialog pop up.
                Same should be implemented on the SystemAppPage_2.
                 */
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                classificationDialog(StoreAppPage_2.this, "Classification of App", response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            });
        }

        else{
            Request request = new Request.Builder().post(requestBody).url(url2).build();
            new OkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull final Call call, @NotNull final IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            call.cancel();

                        }
                    });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                /*
                Write Dialog code here
                Instead of showing just a Toast, it would be better to have an alert Dialog pop up.
                Same should be implemented on the SystemAppPage_2.
                 */
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //Toast.makeText(getApplicationContext(), response.body().string(), Toast.LENGTH_LONG).show();
                                //Open New Activity
                                String details = response.body().string();
                                String[] permInfo = new String[3];
                                permInfo = details.split("~");
                                String permName = permInfo[0].trim();
                                String permAbout = permInfo[1].trim();
                                String permCategory = permInfo[2].trim();

                                Intent intent = new Intent(getApplicationContext(), com.example.permissioneducator.PermissionInfo.class);
                                intent.putExtra("permName", permName);
                                intent.putExtra("permAbout", permAbout);
                                intent.putExtra("permCategory", permCategory);

                                startActivity(intent);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    //work for getting eXAI diagram for confidence level
                }
            });
        }
    }

    // onNothingSelected is not used as of right now
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // openMainPage connects with the back button to return to the main menu
    public void openMainPage(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /*
    getPermissionList is the main code for finding all the apps on the phone and sorting them to
    store apps, getting those app's permissions, and then putting them in lists to be sent to other
    methods when called
     */
    public List<String> getPermissionList(String nameOfApp, int listType){

        final String TAG = "PM INFO ";
        final PackageManager pm = getPackageManager();

        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List pkgAppsList = getPackageManager().queryIntentActivities(mainIntent, 0);
        List<ApplicationInfo> installedApps = pm.getInstalledApplications(0);

        ApplicationInfo specifiedApp = null;
        List<String> specifiedPermissions;
        HashSet<String> specifiedPermissionsSet = new HashSet<>();


        //This gets us the specified app
        for(ApplicationInfo applicationInfo: installedApps) {
            String label = (String)pm.getApplicationLabel(applicationInfo);

            if(label.matches(nameOfApp)){
                System.out.println("We got a match: " + label + " matched with " + nameOfApp);
                specifiedApp = applicationInfo;
            }
        }


        for (Object obj : pkgAppsList) {
            ResolveInfo resolveInfo = (ResolveInfo) obj;
            PackageInfo packageInfo = null;

            //This try block is for printing out specific information on the apps that I use to help sort and find data that could be useful
            //To use things such as .getPermissionInfo or .getApplicationInfo you need a try-catch block.
            try {
                packageInfo = getPackageManager().getPackageInfo(resolveInfo.activityInfo.packageName, PackageManager.GET_PERMISSIONS);
                ApplicationInfo appInfo = pm.getApplicationInfo(packageInfo.packageName, 0);
            }
            catch (PackageManager.NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            //This provides the array of all the permissions
            String[] requestedPermissions = packageInfo.requestedPermissions;
            Log.e("Name", resolveInfo.activityInfo.packageName);

            if (requestedPermissions != null &&  listType == 0) {
                for(int i = 0; i < requestedPermissions.length; i++){
                    try{
                        PermissionInfo permissionInfo = getPackageManager().getPermissionInfo(requestedPermissions[i], PackageManager.GET_META_DATA);

                        if((permissionInfo.protectionLevel == 1 || permissionInfo.protectionLevel == 4097) && packageInfo.packageName.matches(specifiedApp.packageName)){
                            String currentPermission = requestedPermissions[i];
                            specifiedPermissionsSet.add(currentPermission);
                        }
                    }
                    catch(PackageManager.NameNotFoundException e) {
                        // TODO Auto-generated catch block
                    }
                }
            }
            else if (requestedPermissions != null && listType == 1) {
                for(int i = 0; i < requestedPermissions.length; i++){
                    if(packageInfo.packageName.matches(specifiedApp.packageName)){
                        specifiedPermissionsSet.add(requestedPermissions[i]);
                    }
                }
            }
        }

        specifiedPermissions = new ArrayList<String>(specifiedPermissionsSet);
        return specifiedPermissions;
    }
    public void classificationDialog(final Context context, String warning, String message) {
        alert = new AlertDialog.Builder(context);
        alert.setTitle(warning);
        alert.setMessage(message);
        alert.setNegativeButton(android.R.string.no, null);
        AlertDialog alertDialog = alert.create();
        alert.show();
    }
}