package com.challange.heremobility.pjgwg.ktc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapLabeledMarker;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.search.DiscoveryRequest;
import com.here.android.mpa.search.DiscoveryResult;
import com.here.android.mpa.search.DiscoveryResultPage;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.ExploreRequest;
import com.here.android.mpa.search.PlaceLink;
import com.here.android.mpa.search.ResultListener;
import com.here.android.mpa.search.SearchRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    // permissions request code
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    //for storing user's current location
    private FusedLocationProviderClient fusedLocationClient;
    private Double lat,log;
    private ProgressBar progressBar;
    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lat = 28.7041;
        log = 77.1025;
        checkPermissions();
    }

    @SuppressLint({"MissingPermission", "ResourceAsColor"})
    private void initialize() {
        setContentView(R.layout.activity_map);

        //getting parameters from Intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("TITLE");

        //toolbar settings
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_home);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = (ProgressBar) findViewById(R.id.progress_circle_map);
        progressBar.getIndeterminateDrawable().setColorFilter(R.color.colorPrimaryDark2, PorterDuff.Mode.MULTIPLY);
        progressBar.setVisibility(View.VISIBLE);
        title = (String) getSupportActionBar().getTitle();
        //Displaying message because it is just a demo version of the application
        String message = null;
        if (title.contains("Locations") || title.contains("Hotels")) {
            message = "These map locations are for illustration purpose only. So these are getting data from server" +
                    " for same place again and again. Also data is not categorized to extract exact places and hotels." +
                    "\n" + "Thank you.";
        }

        if (title.contains("Hospitals") || title.contains("Local Shops")) {
            message = "Data for hospitals and local vendors is not available know." +
                    " It will be updated in near future";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setCancelable(false).setMessage(message);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


        //fetching the current location of the user
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            lat = location.getLatitude();
                            log = location.getLongitude();
                        }

                    }
                });
        // Search for the Map Fragment
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapfragment);
        // initialize the Map Fragment and
        // retrieve the map that is associated to the fragment
        // Set up disk cache path for the map service for this application
        boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(
                getApplicationContext().getExternalFilesDir(null) + File.separator + ".here-maps",
                "com.challange.heremobility.pjgwg.ktc.MapService");


        if (!success) {
            Toast.makeText(getApplicationContext(), "Unable to set isolated disk cache path.", Toast.LENGTH_LONG).show();
        } else {
            mapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(
                        OnEngineInitListener.Error error) {

                    if (error == OnEngineInitListener.Error.NONE) {
                        // now the map is ready to be used
                        Map map = mapFragment.getMap();
                        // Set the map center to Vancouver, Canada.
                        if (map != null) {
                            MapLabeledMarker marker = new MapLabeledMarker(new GeoCoordinate(lat,log));
                            marker.setLabelText("eng","Marker at haridwar");
                            map.addMapObject(marker);
                            Log.v("MAP", "YES MAP");
                            map.setCenter(new GeoCoordinate(lat,log), Map.Animation.LINEAR);

                            // Get the maximum,minimum zoom level.
                            double maxZoom = map.getMaxZoomLevel();
                            double minZoom = map.getMinZoomLevel();

                            // Set the zoom level to the median (10).
                            map.setZoomLevel((maxZoom + minZoom) / 2);

                            // Rotate 180 degrees.
                            map.setOrientation(180);

                            performUserSearch();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "ERROR: Cannot initialize MapFragment", Toast.LENGTH_SHORT).show();
                        Log.v("MAP", "NO MAP");
                    }
                }
            });
        }

    }

    private void performUserSearch() {
        //to handle all location related searches
        String testStr = getSupportActionBar().getTitle().toString().substring(0, getSupportActionBar().getTitle().toString().indexOf(" "));
        Log.v("TEST", testStr);
        String searchStr = "Places";
        switch (testStr) {
            case "Locations":
                searchStr = "locations";
                break;
            case "Hotels":
                searchStr = "restaurant";
                break;
            case "Hospitals":
                searchStr = "hospital";
                break;
            case "Local":
                searchStr = "places near me";
                break;
        }
        DiscoveryRequest request;
        if (searchStr.equals("locations")) {
            request = new ExploreRequest().setSearchCenter(new GeoCoordinate(lat,log));
        } else {
            request = new SearchRequest(searchStr).setSearchCenter(new GeoCoordinate(lat,log));
        }
        request.setCollectionSize(10);
        ErrorCode errorCode = request.execute(new SearchResultListener());
        if (errorCode != ErrorCode.NONE) {
            Toast.makeText(getApplicationContext(), "Some error occured", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Checks the dynamically controlled permissions and requests missing permissions from end user.
     */
    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
                initialize();
                break;
        }
    }

    class SearchResultListener implements ResultListener<DiscoveryResultPage> {

        @Override
        public void onCompleted(DiscoveryResultPage discoveryResultPage, ErrorCode errorCode) {
            StringBuilder builder = new StringBuilder();
            if (errorCode != ErrorCode.NONE) {
                Toast.makeText(getApplicationContext(), "Some error occured", Toast.LENGTH_LONG).show();
            } else {
                List<DiscoveryResult> results = discoveryResultPage.getItems();
                Map map = mapFragment.getMap();
                for (DiscoveryResult result : results) {
                    MapMarker marker = new MapMarker();
                    if (result.getResultType() == DiscoveryResult.ResultType.PLACE) {
                        PlaceLink placeLink = (PlaceLink) result;
                        builder.append(placeLink.getTitle()).append("\n");
                        marker.setCoordinate(placeLink.getPosition());
                        marker.setTitle(placeLink.getTitle());
                        if (map != null)
                            map.addMapObject(marker);
                    }
                }
            }
            if (progressBar.getVisibility() == View.VISIBLE)
                progressBar.setVisibility(View.INVISIBLE);
        }
    }

}
