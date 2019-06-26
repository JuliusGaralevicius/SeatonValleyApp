package com.seatonvalleyccapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.maps.android.PolyUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;


/**
 * Created by julius on 22/11/2017.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, View.OnClickListener {

    // Class constants
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int initialPeekHeight = 0;
    private static final int MARKER_DELETE_DAYS = 3;

    // Class variables
    private int peekHeight;
    private String selectedImagePath;
    private Bitmap selectedImageBitmap;

    // Firebase class fields
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mMarkersDatabase;
    private StorageReference mPhotoStorage;

    // UI elements
    private GoogleMap mGoogleMap;
    private FloatingActionButton fab;
    private Button confirmLocationButton;
    private Button confirmIssueReportButton;
    private EditText descriptionText;
    private ImageView issueImageView;
    private TextView postsAvailable;

    // Other
    private Polygon polygon;
    private BottomSheetBehavior behavior;
    private boolean bInSelectMode = false;
    private Marker mLastMarker = null;
    private MarkerHashMap markers;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_map, container, false);
        initUIFields(mainView);
        peekHeight = confirmLocationButton.getMeasuredHeight();
        View bottomSheet = mainView.findViewById(R.id.map_bottom_sheet);

        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setPeekHeight(initialPeekHeight);

        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch(newState)
                {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        confirmLocationButton.setText("Cancel");
                        System.out.println("Cancel");
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        confirmLocationButton.setText("Confirm Location");
                        System.out.println("Confirm Location");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        markers = new MarkerHashMap();
        initFirebase();
        addPosts();
        listenToPostsAvailable();
        return mainView;
    }

    private void listenToPostsAvailable() {
        mDatabase.getReference("users").child(mAuth.getCurrentUser().getUid()).child("postsAvailable").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer value = dataSnapshot.getValue(Integer.class);
                String text = value.toString();
                postsAvailable.setText(text);
                if (value<=0){
                    fab.setClickable(false);
                }
                else {
                    fab.setClickable(true);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void addPosts() {
        mDatabase.getReference("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserData data = dataSnapshot.getValue(UserData.class);
                if(data.getMarkerUsed()!=0){
                    if (System.currentTimeMillis()-data.getMarkerUsed()>=86400000){
                        data.postsAvailable = data.getStars();
                        data.markerUsed= 0;
                        mDatabase.getReference("users").child(mAuth.getCurrentUser().getUid()).setValue(data);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void initUIFields(View mainView) {
        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        fab = (FloatingActionButton) mainView.findViewById(R.id.map_fab);
        fab.setOnClickListener(this);
        postsAvailable = (TextView) mainView.findViewById(R.id.tv_map_posts_available);
        confirmLocationButton = (Button) mainView.findViewById(R.id.btn_add_issue_confirm_location);
        confirmLocationButton.setOnClickListener(this);
        confirmLocationButton.post(new Runnable() {
            @Override
            public void run() {
                peekHeight = confirmLocationButton.getHeight();
            }
        });
        confirmIssueReportButton =  mainView.findViewById(R.id.btn_add_issue_confirm_issue);
        confirmIssueReportButton.setOnClickListener(this);
        descriptionText = (EditText) mainView.findViewById(R.id.et_add_issue_description);
        issueImageView = (ImageView) mainView.findViewById(R.id.iv_add_issue_photo);
        issueImageView.setOnClickListener(this);
    }

    private void initFirebase() {
        mDatabase = FirebaseDatabase.getInstance();
        mMarkersDatabase = mDatabase.getReference("markers");
        mPhotoStorage = FirebaseStorage.getInstance().getReference("photos");
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mGoogleMap = googleMap;
        drawPolygon(mGoogleMap);
        formatMap(mGoogleMap);

        mGoogleMap.setOnMarkerClickListener(this);

        mMarkersDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String id = dataSnapshot.getKey();
                MarkerData data = dataSnapshot.getValue(MarkerData.class);
                if (data.resolvedTimestamp!=0 && (System.currentTimeMillis() - data.resolvedTimestamp >= MARKER_DELETE_DAYS*86400000L)){
                    mMarkersDatabase.child(dataSnapshot.getKey()).removeValue();
                    mPhotoStorage.child(dataSnapshot.getKey()).delete();
                    return;
                }
                BitmapDescriptor descriptor;
                if (data.bResolved){
                    descriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                }
                else {
                    descriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                }
                Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(data.getLat(), data.getLon())).icon(descriptor));
                markers.addMarker(marker, id);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String id = dataSnapshot.getKey();
                if (markers.getMarker(id)!=null)
                    markers.deleteMarker(id);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void drawPolygon(GoogleMap mGoogleMap) {
        // Instantiates a new Polygon object and adds points to define a rectangle
        PolygonOptions rectOptions = new PolygonOptions()
                .add(new LatLng(55.101896, -1.494055),
                        new LatLng(55.101503, -1.492596),
                        new LatLng(55.085589, -1.474743),
                        new LatLng(55.085688, -1.472425),
                        new LatLng(55.085442, -1.470795),
                        new LatLng(55.084607, -1.469936),
                        new LatLng(55.083477, -1.47028),
                        new LatLng(55.082986, -1.470795),
                        new LatLng(55.081561, -1.47028),
                        new LatLng(55.080382, -1.469164),
                        new LatLng(55.079939, -1.468906),
                        new LatLng(55.079546, -1.466932),
                        new LatLng(55.079596, -1.465216),
                        new LatLng(55.079497, -1.463842),
                        new LatLng(55.077679, -1.463156),
                        new LatLng(55.077041, -1.463327),
                        new LatLng(55.076402, -1.46307),
                        new LatLng(55.07596, -1.462641),
                        new LatLng(55.077237, -1.461353),
                        new LatLng(55.076893, -1.460838),
                        new LatLng(55.076254, -1.461267),
                        new LatLng(55.075419, -1.461439),
                        new LatLng(55.075124, -1.462641),
                        new LatLng(55.075468, -1.468649),
                        new LatLng(55.073208, -1.47294),
                        new LatLng(55.066622, -1.479635),
                        new LatLng(55.065787, -1.48736),
                        new LatLng(55.065738, -1.492338),
                        new LatLng(55.056005, -1.487532),
                        new LatLng(55.05394, -1.488218),
                        new LatLng(55.054776, -1.493282),
                        new LatLng(55.051433, -1.498604),
                        new LatLng(55.048335, -1.50753),
                        new LatLng(55.055169, -1.510448),
                        new LatLng(55.054825, -1.515512),
                        new LatLng(55.053153, -1.535168),
                        new LatLng(55.061265, -1.540232),
                        new LatLng(55.058266, -1.542206),
                        new LatLng(55.055759, -1.550531),
                        new LatLng(55.053891, -1.55654),
                        new LatLng(55.054383, -1.568127),
                        new LatLng(55.055808, -1.577311),
                        new LatLng(55.064853, -1.579113),
                        new LatLng(55.067065, -1.579628),
                        new LatLng(55.071832, -1.574736),
                        new LatLng(55.071537, -1.570787),
                        new LatLng(55.076304, -1.561689),
                        new LatLng(55.075714, -1.56023),
                        new LatLng(55.076254, -1.557741),
                        new LatLng(55.076205, -1.550274),
                        new LatLng(55.077139, -1.547441),
                        new LatLng(55.079792, -1.549416),
                        new LatLng(55.079104, -1.553879),
                        new LatLng(55.084607, -1.561089),
                        new LatLng(55.084361, -1.56744),
                        new LatLng(55.099489, -1.566324),
                        new LatLng(55.100177, -1.559458),
                        new LatLng(55.102681, -1.549587),
                        new LatLng(55.100963, -1.542892),
                        new LatLng(55.103663, -1.533966),
                        new LatLng(55.096543, -1.530018),
                        new LatLng(55.097181, -1.522808),
                        new LatLng(55.097918, -1.521091),
                        new LatLng(55.097672, -1.514139));
        // Get back the mutable Polygon
        polygon = mGoogleMap.addPolygon(rectOptions);
        polygon.setFillColor(Color.TRANSPARENT);
        polygon.setStrokeColor(Color.rgb(115,46,119));
        polygon.setStrokeWidth(10);
        // This is false in order for the onMapClick to be triggered when clicking inside the polygon
        polygon.setClickable(false);
    }
    private LatLngBounds getPolygonBounds(){
        double maxLon=polygon.getPoints().get(0).longitude;
        double maxLat=polygon.getPoints().get(0).latitude;;
        double minLon=polygon.getPoints().get(1).longitude;;
        double minLat=polygon.getPoints().get(1).latitude;;
        for (LatLng point: polygon.getPoints()){
            maxLon = point.longitude>maxLon? point.longitude : maxLon;
            maxLat = point.latitude>maxLat? point.latitude : maxLat;
            minLon = point.longitude<minLon? point.longitude : minLon;
            minLat = point.latitude<minLat? point.latitude : minLat;
        }
        LatLngBounds bounds = new LatLngBounds(new LatLng(minLat, minLon), new LatLng(maxLat, maxLon));
        return bounds;
    }

    private void formatMap(GoogleMap mGoogleMap) {
        mGoogleMap.setLatLngBoundsForCameraTarget(getPolygonBounds());
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getPolygonBounds(), width,
                height, 0));
        mGoogleMap.setMinZoomPreference(mGoogleMap.getCameraPosition().zoom);
    }

    private void sendImage(Bitmap image, final String id, final MarkerData data) {


        byte[] bytes = compressImage(image, 10);
        if (bytes == null) {
            Toast.makeText(getContext(), "Image file too large, it will not be displayed", Toast.LENGTH_SHORT).show();
            return;
        }
        UploadTask uploadTask = mPhotoStorage.child(id).putBytes(bytes);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(MapFragment.this.getContext(), "Issue submitted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private byte[] compressImage(Bitmap image, int size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final long megabyte = 1024 * 1024;
        final long allowedSize = size * megabyte;
        for (int i = 100; i > 0; i -= 10) {
            image.compress(Bitmap.CompressFormat.JPEG, i, baos);
            final byte[] bytes = baos.toByteArray();
            if (bytes.length <= allowedSize) {
                return bytes;
            }
        }
        return null;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        DialogFragment fragment = new ViewIssueFragment();
        Bundle bundle = new Bundle(1);
        String markerID = markers.getID(marker);
        bundle.putString("markerID", markerID);
        fragment.setArguments(bundle);
        fragment.show(getActivity().getSupportFragmentManager(), fragment.getTag());
        return true;
    }

    private void submitIssue() {

        double lat = mLastMarker.getPosition().latitude;
        double lon = mLastMarker.getPosition().longitude;
        String description = descriptionText.getText().toString();
        if (TextUtils.isEmpty(description.trim())) {
            Toast.makeText(getContext(), "please enter description", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!hasImage()) {
            Toast.makeText(getContext(), "please include a photo", Toast.LENGTH_SHORT).show();
            return;
        }
        MarkerData data = new MarkerData(System.currentTimeMillis(), mAuth.getCurrentUser().getUid(), false, description, lat, lon);

        String id = mMarkersDatabase.push().getKey();
        sendImage(selectedImageBitmap, id, data);
        resetFields();
        mMarkersDatabase.child(id).setValue(data);
        removePostFromUser(mAuth.getCurrentUser().getUid());
    }

    private void removePostFromUser(final String uid) {
        mDatabase.getReference("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserData data = dataSnapshot.getValue(UserData.class);
                data.postsAvailable=data.postsAvailable-1;
                if (data.markerUsed==0){
                    data.markerUsed=System.currentTimeMillis();
                }
                mDatabase.getReference("users").child(uid).setValue(data);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void resetFields() {
        bInSelectMode = false;
        if (mLastMarker != null) {
            mLastMarker.remove();
        }
        selectedImageBitmap = null;
        selectedImagePath = null;
        issueImageView.setImageDrawable(null);
        mGoogleMap.setOnMapClickListener(null);
        descriptionText.setText(null);
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        behavior.setPeekHeight(initialPeekHeight);
        fab.setRotation(0);
        mGoogleMap.setOnMarkerClickListener(this);
    }

    private void allowSelection() {
        bInSelectMode = true;
        mGoogleMap.setOnMarkerClickListener(null);
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                if (mLastMarker != null) {
                    mLastMarker.remove();
                    confirmLocationButton.setText("Confirm location");
                }
                if (!insidePolygon(latLng)) {
                    Toast.makeText(getContext(), "Select location inside the area", Toast.LENGTH_SHORT).show();
                    return;
                }
                mLastMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng));
                behavior.setPeekHeight(peekHeight);
            }
        });
        fab.setRotation(45);
    }

    private boolean insidePolygon(LatLng latLng) {
        return PolyUtil.containsLocation(latLng, polygon.getPoints(), false);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            String path = selectedImagePath;

            File imgFile = new File(path);
            selectedImageBitmap = BitmapFactory.decodeFile(imgFile.getPath());

            Glide.with(getContext()).load(imgFile).into(new GlideDrawableImageViewTarget(issueImageView) {
                @Override
                public void onResourceReady(GlideDrawable resource,
                                            GlideAnimation<? super GlideDrawable> animation) {
                    super.onResourceReady(resource, animation);
                }
            });
        }

    }
    private boolean hasImage() {
        return selectedImageBitmap != null;
    }
    private void requestPhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File storageDir = getContext().getFilesDir();
        File imageFile = null;
        try {
            imageFile = File.createTempFile("tempFile", ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Uri imgUri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".fileprovider", imageFile);
        selectedImagePath = imageFile.getAbsolutePath();
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.map_fab:
                if (!bInSelectMode) {
                    Toast.makeText(getActivity(), "Click anywhere inside the council area to place a marker", Toast.LENGTH_SHORT).show();
                    allowSelection();
                } else {
                    resetFields();
                }
                break;
            case R.id.btn_add_issue_confirm_location:
                if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                break;
            case R.id.btn_add_issue_confirm_issue:
                submitIssue();
                break;
            case R.id.iv_add_issue_photo:
                requestPhoto();
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
