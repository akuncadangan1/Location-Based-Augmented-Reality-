package com.location.w.locationar;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.location.w.locationar.locsutil.LokUtils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import uk.co.appoly.arcorelocation.LocationMarker;
import uk.co.appoly.arcorelocation.LocationScene;
import uk.co.appoly.arcorelocation.rendering.LocationNode;
import uk.co.appoly.arcorelocation.rendering.LocationNodeRender;
import uk.co.appoly.arcorelocation.utils.ARLocationPermissionHelper;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout r1, relative;
    private boolean installRequested;
    private boolean hasFinishedLoading = false;
    private ArSceneView arSceneView;
    private ViewRenderable Lr1, Lr2, Lr3, Lr4, Lr5;
    private LocationScene locationScene;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            r1.setVisibility(View.GONE);
            arSceneView.setVisibility(View.VISIBLE);
        }
    };
    private ProgressBar progressBar;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        r1 = findViewById(R.id.r1);
        relative = findViewById(R.id.relative);
        arSceneView = findViewById(R.id.scenelokasi);

        handler.postDelayed(runnable, 3000);

        progreesbar();
        setrenderable();

        ARLocationPermissionHelper.requestPermission(this);
    }

    private void progreesbar() {
        progressBar = findViewById(R.id.progressbar);
        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                count++;
                progressBar.setProgress(count);
                if (count ==1000)
                    timer.cancel();
            }
        };
        timer.schedule(timerTask,0,30);
    }

    private void setrenderable() {

        CompletableFuture<ViewRenderable> lyt1 = ViewRenderable.builder().setView(this, R.layout.viewrenderable).build();
        CompletableFuture<ViewRenderable> lyt2 = ViewRenderable.builder().setView(this, R.layout.viewrenderable).build();
        CompletableFuture<ViewRenderable> lyt3 = ViewRenderable.builder().setView(this, R.layout.viewrenderable).build();
        CompletableFuture<ViewRenderable> lyt4 = ViewRenderable.builder().setView(this, R.layout.viewrenderable).build();
        CompletableFuture<ViewRenderable> lyt5 = ViewRenderable.builder().setView(this, R.layout.viewrenderable).build();

        CompletableFuture.allOf(lyt1, lyt2, lyt4, lyt5, lyt3)
                .handle(
                        (notUsed, throwable) -> {
                            try {
                                Lr1 = lyt1.get();
                                Lr2 = lyt2.get();
                                Lr4 = lyt4.get();
                                Lr5 = lyt5.get();
                                Lr3 = lyt3.get();
                                hasFinishedLoading = true;

                            } catch (InterruptedException | ExecutionException ex) {
                                LokUtils.displayError(this, "Unable to load renderables", ex);
                            }

                            return null;
                        });

        arSceneView
                .getScene()
                .addOnUpdateListener(
                        frameTime -> {
                            if (!hasFinishedLoading) {
                                return;
                            }

                            if (locationScene == null) {
                                locationScene = new LocationScene(this, this, arSceneView);
                                locationScene.setOffsetOverlapping(true);

                                LocationMarker mushola1 = new LocationMarker(110.431738, -7.787661, createRenderable(-7.787661, 110.431738, "Bandara Adi Sucipto", Lr1));
                                LocationMarker mushola2 = new LocationMarker(110.40925, -7.762191, createRenderable(-7.762191, 110.40925, "UPN Kampus Pusat", Lr2));
                                LocationMarker toilet2 = new LocationMarker(110.415902, -7.779327, createRenderable(-7.779327, 110.415902, "Universitas Atmajaya", Lr4));
                                LocationMarker toiletluar = new LocationMarker(110.416106, -7.770356, createRenderable(-7.770356, 110.416106, "Bumi Perkemahan Barbarsari", Lr5));
                                LocationMarker pintukeluar = new LocationMarker(110.41985, -7.783005, createRenderable(-7.783005, 110.41985, "Transmart", Lr3));

                                mushola1.setRenderEvent(new LocationNodeRender() {
                                    @Override
                                    public void render(LocationNode node) {
                                        createrender(Lr1, "Bandara JOG", node);
                                    }
                                });

                                mushola2.setRenderEvent(new LocationNodeRender() {
                                    @Override
                                    public void render(LocationNode node) {
                                        createrender(Lr2, "UPN", node);
                                    }
                                });

                                toilet2.setRenderEvent(new LocationNodeRender() {
                                    @Override
                                    public void render(LocationNode node) {
                                        createrender(Lr4, "Atmajaya", node);
                                    }
                                });

                                toiletluar.setRenderEvent(new LocationNodeRender() {
                                    @Override
                                    public void render(LocationNode node) {

                                        createrender(Lr5, "Buper Babarsari", node);
                                    }
                                });

                                pintukeluar.setRenderEvent(new LocationNodeRender() {
                                    @Override
                                    public void render(LocationNode node) {
                                        createrender(Lr3, "Transmart", node);
                                    }
                                });

                                locationScene.mLocationMarkers.add(mushola1);
                                locationScene.mLocationMarkers.add(mushola2);
                                locationScene.mLocationMarkers.add(toilet2);
                                locationScene.mLocationMarkers.add(toiletluar);
                                locationScene.mLocationMarkers.add(pintukeluar);
                            }

                            Frame frame = arSceneView.getArFrame();
                            if (frame == null) {
                                return;
                            }

                            if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                                return;
                            }

                            if (locationScene != null) {
                                locationScene.processFrame(frame);
                            }

                        });
    }


    private void createrender(ViewRenderable renderable, String text, LocationNode node) {
        View eView = renderable.getView();
        TextView tittle = eView.findViewById(R.id.textView);
        TextView distance = eView.findViewById(R.id.textView2);
        tittle.setText(text);
        distance.setText(node.getDistance() + "M");
    }

    private Node createRenderable(Double lat, Double longi, String detail, ViewRenderable renderable) {
        Node base = new Node();
        base.setRenderable(renderable);
        Context c = this;
        View eView = renderable.getView();
        eView.setOnTouchListener((v, event) -> {
            showsnackbar(lat, longi, detail);
            return false;
        });

        return base;
    }

    private void showsnackbar(Double lat, Double longi, String detail) {
        Snackbar snackbar = Snackbar.make(relative, detail, Snackbar.LENGTH_INDEFINITE)
                .setAction("Navigasi", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mapdirection(lat, longi);
                    }
                });
        snackbar.show();
    }

    private void mapdirection(Double lat, Double longi) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + longi + "&mode=w");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (locationScene != null) {
            locationScene.resume();
        }

        if (arSceneView.getSession() == null) {
            try {
                Session session = LokUtils.createArSession(this, installRequested);
                if (session == null) {
                    installRequested = ARLocationPermissionHelper.hasPermission(this);
                    return;
                } else {
                    arSceneView.setupSession(session);
                }
            } catch (UnavailableException e) {
                LokUtils.handleSessionException(this, e);
            }
        }

        try {
            arSceneView.resume();
        } catch (CameraNotAvailableException ex) {
            LokUtils.displayError(this, "Unable to get camera", ex);
            finish();
            return;
        }

        if (arSceneView.getSession() != null) {

        }
    }


    @Override
    public void onPause() {
        super.onPause();

        if (locationScene != null) {
            locationScene.pause();
        }

        arSceneView.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        arSceneView.destroy();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        if (!ARLocationPermissionHelper.hasPermission(this)) {
            if (!ARLocationPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                ARLocationPermissionHelper.launchPermissionSettings(this);
            } else {
                Toast.makeText(
                        this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                        .show();
            }
            finish();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_VISIBLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
