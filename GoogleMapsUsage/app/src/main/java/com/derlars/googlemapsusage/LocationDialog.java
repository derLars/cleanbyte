package com.derlars.googlemapsusage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.derlars.whosin.RecyclerAdapter.SelectLocationRecyclerAdapter;
import com.derlars.whosin.Templates.Dialogs.BaseDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationDialog extends BaseDialog implements BaseDialog.DoneBtn, BaseDialog.CancelBtn, OnMapReadyCallback {
    protected List<Address> resource = new ArrayList();

    private RecyclerView recyclerView;
    private SelectLocationRecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    MapView locationMap;

    EditText addressText;
    ImageButton searchBtn;

    private Marker selectedLocation;

    List<Address> addresses = new ArrayList<>();
    Address address;

    GoogleMap googleMap;

    double latitude;
    double longitude;
    String location;
    @Override
    protected void onDialogCreated(final View view) {
        latitude = getArguments().getDouble("latitude");
        longitude = getArguments().getDouble("longitude");
        location = getArguments().getString("location");

        adapter = new SelectLocationRecyclerAdapter(resource);
        layoutManager = new LinearLayoutManager(getActivity());

        locationMap = view.findViewById(R.id.location_map);
        setUpMap();

        addressText = view.findViewById(R.id.address_text);

        searchBtn = view.findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard(v);
                recyclerView.setVisibility(View.VISIBLE);

                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                try{
                    addresses = geocoder.getFromLocationName(addressText.getText().toString(),10);

                    resource = addresses;
                    setUpRecycler(view);
                }catch(Exception ex) {
                    Log.e(TAG, ex.toString());
                }
            }
        });

        recyclerView = view.findViewById(R.id.recycler_view);
        setUpRecycler(view);
    }

    @Override
    protected int setLayout() {
        return R.layout.dialog_location;
    }

    private void setUpMap() {
        locationMap.onCreate(null);
        locationMap.onResume();
        locationMap.getMapAsync(this);
    }

    private void setUpRecycler(final View view) {
        adapter = new SelectLocationRecyclerAdapter(resource);
        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new SelectLocationRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                try {
                    if(selectedLocation != null) {
                        selectedLocation.remove();
                    }
                    address = addresses.get(position);

                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
                    selectedLocation = googleMap.addMarker(new MarkerOptions().position(latLng));
                    addressText.setText(address.getAddressLine(0));

                    getArguments().putString("location",address.getAddressLine(0));
                    getArguments().putDouble("latitude",address.getLatitude());
                    getArguments().putDouble("longitude",address.getLongitude());

                    recyclerView.setVisibility(View.GONE);
                }catch(Exception ex) {
                    Log.e(TAG,ex.toString());
                }
            }

            @Override
            public void onItemLongClick(int position) {

            }

            @Override
            public void onViewChanged() {

            }
        });
    }

    @Override
    public void onDoneBtnClick() {
        if(addressText.getText().toString().length() > 0) {
            getArguments().putString("location",addressText.getText().toString());
        }

        Intent intent = new Intent();

        intent.putExtra("location",getArguments().getString("location"));
        intent.putExtra("latitude",getArguments().getDouble("latitude"));
        intent.putExtra("longitude",getArguments().getDouble("longitude"));
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,intent);
        dismiss();
    }

    @Override
    public void onCancelBtnClick() {
        dismiss();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        final Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

        this.googleMap = googleMap;

        this.googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(selectedLocation != null) {
                    selectedLocation.remove();
                }
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
                    if(addresses.size() > 0) {
                        address = addresses.get(0);
                        getArguments().putString("location",address.getAddressLine(0));
                        getArguments().putDouble("latitude",address.getLatitude());
                        getArguments().putDouble("longitude",address.getLongitude());

                        addressText.setText(address.getAddressLine(0));
                    }
                } catch (Exception e) {
                    Log.e(TAG,"1 Select Location: " +e.toString());
                }

                selectedLocation = LocationDialog.this.googleMap.addMarker(new MarkerOptions().position(latLng));
            }
        });

        try {
            if(latitude > 0.001 || latitude < -0.001 || longitude > 0.001 || longitude < -0.001) {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            }else if(location != null){
                addresses = geocoder.getFromLocationName(location,1);
                if(addresses.size() > 0) {
                    latitude = addresses.get(0).getLatitude();
                    longitude = addresses.get(0).getLongitude();
                }
            }
            if(addresses.size() > 0) {
                address = addresses.get(0);
                LatLng latLng = new LatLng(latitude,longitude);
                selectedLocation = LocationDialog.this.googleMap.addMarker(new MarkerOptions().position(latLng));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
            }

            addressText.setText(location);
        } catch (IOException e) {
            Log.e(TAG,"2 Select Location: " + e.toString());
        }
    }

    public void hideKeyBoard(View view) {
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
