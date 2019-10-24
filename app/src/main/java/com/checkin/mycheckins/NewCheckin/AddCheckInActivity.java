package com.checkin.mycheckins.NewCheckin;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.checkin.mycheckins.AppData.IntentKeys;
import com.checkin.mycheckins.MainActivity.MainActivity;
import com.checkin.mycheckins.Models.CheckinModel;
import com.checkin.mycheckins.NewCheckin.Interface.AddCheckInInterface;
import com.checkin.mycheckins.NewCheckin.Presenter.AddCheckInPresenter;
import com.checkin.mycheckins.R;
import com.checkin.mycheckins.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

public class AddCheckInActivity extends AppCompatActivity implements AddCheckInInterface.View,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST = 200;
    private static final int REQUEST_CHECK_LOCATION = 300;

    private TextView tvActivityTitle, tvLocation;
    private ImageView removeRecord, shareRecord, ivPicture;
    private EditText etTitle, etDescription, etPlace;
    private Button dateBtn, openMap, getLocation, save;
    private ConstraintLayout changePicture, addPicture, showPicture, hideKeyboard;

    private CheckinModel checkinModel;
    private String activityTitle, checkinTitle, checkinPlace, checkinDescription, checkinLocation, checkinDate;
    private byte[] checkinImage;
    private boolean isNewCheckin;

    private GoogleApiClient googleApiClient;
    private Location location = null;
    private LatLng latLng;
    private LocationManager locationManager;
    private LocationRequest locationRequest;
    private LocationListener locationListener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private LocationManager mLocationManager;

    private AddCheckInPresenter presenter;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_check_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeViews();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        presenter = new AddCheckInPresenter(this, this);

        activityTitle = getIntent().getStringExtra(IntentKeys.ACTIVITY_TITLE);
        isNewCheckin = getIntent().getBooleanExtra(IntentKeys.IS_NEW_CHECKIN, false);

        tvActivityTitle.setText(activityTitle);

        if (isNewCheckin) {
            removeRecord.setVisibility(View.GONE);
            shareRecord.setVisibility(View.GONE);
            openMap.setVisibility(View.GONE);
            addPicture.setVisibility(View.VISIBLE);
            showPicture.setVisibility(View.GONE);
        } else {
            getLocation.setVisibility(View.GONE);
            addPicture.setVisibility(View.GONE);
            showPicture.setVisibility(View.VISIBLE);

            getStrings();
            fillIncomingData();
        }

        removeRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.removeSingleRecord(checkinModel);
            }
        });

        shareRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.share(checkinTitle, checkinDescription);
            }
        });

        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("google.streetview:cbll=" + checkinLocation);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                // Make the Intent explicit by setting the Google Maps package
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });

        Calendar calendar = Calendar.getInstance();
        final DatePickerDialog time = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dateBtn.setText(newDate.getTime().toString());
            }

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(hideKeyboard.getWindowToken(), 0);
                time.show();
            }
        });

        addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(hideKeyboard.getWindowToken(), 0);
                picturerequest();
            }
        });

        changePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(hideKeyboard.getWindowToken(), 0);
                picturerequest();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(hideKeyboard.getWindowToken(), 0);

                if (etTitle.getText().toString().length() == 0) {
                    etTitle.setError(getResources().getString(R.string.field_required));
                } else if (etPlace.getText().toString().length() == 0) {
                    etPlace.setError(getResources().getString(R.string.field_required));
                } else if (etDescription.getText().toString().length() == 0) {
                    etDescription.setError(getResources().getString(R.string.field_required));
                } else if (dateBtn.getText().toString().equalsIgnoreCase(getResources().getString(R.string.add_date))) {
                    Toast.makeText(AddCheckInActivity.this, "Please Add Date!!", Toast.LENGTH_SHORT).show();
                } else if (addPicture.getVisibility() == View.VISIBLE) {
                    Toast.makeText(AddCheckInActivity.this, "Please Add Image!!", Toast.LENGTH_SHORT).show();
                } else if (tvLocation.getText().toString().equalsIgnoreCase(getResources().getString(R.string.nulll))) {
                    Toast.makeText(AddCheckInActivity.this, "Please Add Location!!", Toast.LENGTH_SHORT).show();
                } else {
                    if (isNewCheckin) {
                        CheckinModel checkinModel = new CheckinModel(
                                0,
                                etTitle.getText().toString(),
                                etPlace.getText().toString(),
                                etDescription.getText().toString(),
                                dateBtn.getText().toString(),
                                tvLocation.getText().toString(),
                                Utils.getBytes(imageView2Bitmap(ivPicture))
                        );
                        presenter.saveNewRecore(checkinModel, true);
                    } else {
                        CheckinModel checkinModel = new CheckinModel(
                                AddCheckInActivity.this.checkinModel.getId(),
                                etTitle.getText().toString(),
                                etPlace.getText().toString(),
                                etDescription.getText().toString(),
                                dateBtn.getText().toString(),
                                tvLocation.getText().toString(),
                                Utils.getBytes(imageView2Bitmap(ivPicture))
                        );
                        presenter.saveNewRecore(checkinModel, false);
                    }
                }
            }
        });

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(hideKeyboard.getWindowToken(), 0);
                locationRequest();
            }
        });
    }

    private void picturerequest() {
        if (ActivityCompat.checkSelfPermission(AddCheckInActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddCheckInActivity.this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    private void initializeViews() {
        tvActivityTitle = findViewById(R.id.tvActivityTitle);
        removeRecord = findViewById(R.id.removeRecord);
        shareRecord = findViewById(R.id.shareRecord);
        etTitle = findViewById(R.id.etTitle);
        etPlace = findViewById(R.id.etPlace);
        etDescription = findViewById(R.id.etDescription);
        dateBtn = findViewById(R.id.btnDate);
        tvLocation = findViewById(R.id.tvLocation);
        getLocation = findViewById(R.id.btnGetLocation);
        openMap = findViewById(R.id.btnOpenLocation);
        ivPicture = findViewById(R.id.ivPic);
        changePicture = findViewById(R.id.changePic);
        addPicture = findViewById(R.id.addNewPic);
        hideKeyboard = findViewById(R.id.hideKeyboard);
        showPicture = findViewById(R.id.showPicture);
        save = findViewById(R.id.btnSave);
    }

    private void getStrings() {
        checkinModel = (CheckinModel) getIntent().getSerializableExtra(IntentKeys.CHECKIN_MODEL);

        checkinTitle = checkinModel.getTitle();
        checkinPlace = checkinModel.getPlace();
        checkinDescription = checkinModel.getDescription();
        checkinDate = checkinModel.getDate();
        checkinLocation = checkinModel.getLocation();
        checkinImage = checkinModel.getImage();
    }

    private void fillIncomingData() {
        etTitle.setText(checkinTitle);
        etDescription.setText(checkinDescription);
        etPlace.setText(checkinPlace);
        dateBtn.setText(checkinDate);
        tvLocation.setText(checkinLocation);
        ivPicture.setImageBitmap(Utils.getImage(checkinImage));
    }

    @Override
    public void singleRecordRemoved() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void newRecordSaved(boolean isNewCheckin) {
        if (isNewCheckin) {
            Toast.makeText(this, "New CheckIn Saved", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "CheckIn Updated", Toast.LENGTH_LONG).show();
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void share(Intent intent) {
        startActivity(Intent.createChooser(intent, "Share Text Using"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private Bitmap imageView2Bitmap(ImageView view) {
        Bitmap bitmap = ((BitmapDrawable) view.getDrawable()).getBitmap();
        return bitmap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_CHECK_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationRequest();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ivPicture.setImageBitmap(bitmap);
                addPicture.setVisibility(View.GONE);
                showPicture.setVisibility(View.VISIBLE);
            }
        } else if (requestCode == REQUEST_CHECK_LOCATION) {
            if (resultCode == RESULT_OK) {
                locationRequest();
            } else {
                Toast.makeText(AddCheckInActivity.this, "Turn On GPS permission", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void locationRequest() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddCheckInActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}
                    , REQUEST_CHECK_LOCATION);
            return;
        }
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (!isNewCheckin) {
            tvLocation.setText(location.getLatitude() + ", " + location.getLongitude());
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location == null) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        String mLocation = Double.toString(location.getLatitude()) + ", " +
                Double.toString(location.getLongitude());
        if (isNewCheckin) {
            tvLocation.setText(mLocation);
        }
        // You can now create a LatLng Object for use with maps
        latLng = new LatLng(location.getLatitude(), location.getLongitude());

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    protected void startLocationUpdates() {
        // Create the location request
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                locationRequest, this);
    }
}
