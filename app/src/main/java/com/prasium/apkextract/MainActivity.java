package com.prasium.apkextract;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.prasium.apkextract.R.string.share;

public class MainActivity extends AppCompatActivity {
    private AdView mAdView;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private  RecyclerView.LayoutManager layoutManager;
    RVAdapter adapter;
    ArrayList<RVModel> apps = new ArrayList<>();
   // ArrayList<RVModel> allapps=new ArrayList<>(); // for system installed apps might use later
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            //Ads Integration
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);          // requests Google Ads

                Dexter.withContext(this)
                        .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                if(!multiplePermissionsReport.areAllPermissionsGranted()){
                                    Toast.makeText(getApplicationContext(),"Need Storage permissions to work",Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).onSameThread().check();
                recyclerView=(RecyclerView)findViewById(R.id.rvApps);
        PackageManager packageManager = getPackageManager();
        for(ApplicationInfo applicationInfo: packageManager.getInstalledApplications(PackageManager.GET_META_DATA)){
            String aname,pname;
            Drawable icon;
            aname=applicationInfo.loadLabel(packageManager).toString();
            pname=applicationInfo.packageName;
            icon=applicationInfo.loadIcon(packageManager);
            if(!isSystemPackage(applicationInfo))
           {
               apps.add(new RVModel(aname,pname,icon));
           }
           // allapps.add(new RVModel(aname,pname,icon));
        }
 adapter = new RVAdapter(apps);
        Collections.sort(apps, new Comparator<RVModel>() {  // sorts alphabetically
            @Override
            public int compare(RVModel rvModel, RVModel t1) {
                return rvModel.getAppName().compareTo(t1.getAppName());
            }
        });
    recyclerView.setAdapter(adapter);
     recyclerView.setLayoutManager(new LinearLayoutManager(this));
     recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL));

    }

    private boolean isSystemPackage(ApplicationInfo applicationInfo) {
        return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menutool, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return  false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                share();
                return true;

            case R.id.settings:
                settings();
                return true;
        }
        return false;
    }

    private void settings() {
        Intent callSettings = new Intent(this,Settings.class);
        startActivity(callSettings);
    }

    private void share() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.promo).toString());
        intent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(intent, null);
        startActivity(shareIntent);

    }

    public void tapmsg(View view) {
        Toast.makeText(getApplicationContext(),"Tap on the Button to extract!",Toast.LENGTH_SHORT).show();
    }
}