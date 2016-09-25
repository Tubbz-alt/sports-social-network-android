package vn.datsan.datsan.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowCloseListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import vn.datsan.datsan.R;
import vn.datsan.datsan.activities.FieldDetailActivity;
import vn.datsan.datsan.models.Field;
import vn.datsan.datsan.serverdata.CallBack;
import vn.datsan.datsan.serverdata.FieldService;
import vn.datsan.datsan.ui.adapters.DividerItemDecoration;
import vn.datsan.datsan.ui.adapters.FlexListAdapter;
import vn.datsan.datsan.ui.adapters.RecyclerTouchListener;
import vn.datsan.datsan.ui.customwidgets.Alert.SimpleAlert;

/**
 * Created by xuanpham on 7/25/16.
 */

public class SportFieldFragment extends Fragment implements
        OnMarkerClickListener,
        OnInfoWindowClickListener,
        OnMarkerDragListener,
        SeekBar.OnSeekBarChangeListener,
        OnMapReadyCallback,
        OnInfoWindowLongClickListener,
        OnInfoWindowCloseListener {

    private GoogleMap mMap;
    private FlexListAdapter adapter;
    private View searchResultView;

    private OnFragmentInteractionListener mListener;

    public SportFieldFragment() {
        // Required empty public constructor
    }

    public static SportFieldFragment newInstance(String param1, String param2) {
        SportFieldFragment fragment = new SportFieldFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sport_field, null);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        searchResultView = view.findViewById(R.id.searchResultView);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new FlexListAdapter(getActivity()) {

            @Override
            public void setImage(Context context, ImageView imageView, String imageUrl) {

            }
        };
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(getActivity(), "Touch " + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), FieldDetailActivity.class);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        ((ImageButton) view.findViewById(R.id.btn_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchResultView.setVisibility(View.GONE);
            }
        });
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
//        if (marker.getData() != null && marker.getData() instanceof Field) {
//            Intent intent = new Intent(getActivity(), FieldDetailActivity.class);
//            intent.putExtra("data", (Field) marker.getData());
//            startActivity(intent);
//        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(10.777098, 106.695487);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Quan 1, tp.HCM"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnInfoWindowClickListener(this);

        List<Field> fields = FieldService.getInstance().getFields(new CallBack.OnResultReceivedListener() {
            @Override
            public void onResultReceived(Object result) {
                if (result != null) {
                    List<Field> fields = (List<Field>) result;
                    addMarkers(fields);
                }
            }
        });

        addMarkers(fields);
    }

    private void addMarkers(List<Field> fieldList) {
        for (Field field : fieldList) {
            String location = field.getLocation();
            if (location != null && location.length() > 6) {
                String arr[] = location.split(",");
                MarkerOptions marker = new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(arr[0]), Double.parseDouble(arr[1])))
                        .title(field.getName())
                        .snippet(field.getAddress())
                        .snippet("Suc chua: 16.300")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.football_field))
                        .infoWindowAnchor(0.5f, 0.5f);
                mMap.addMarker(marker);
            }
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void showSearchResultView(boolean open) {
        if (open)
            searchResultView.setVisibility(View.VISIBLE);
        else
            searchResultView.setVisibility(View.GONE);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onInfoWindowClose(Marker marker) {

    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    public void showSearchResult(Object object) {
        if (object == null)
            return;
        List<Field> fields = (List<Field>) object;
        if (fields.isEmpty()) {
            SimpleAlert.showAlert(getActivity(), "Tìm sân", "Không tìm thấy kết quả !", getString(R.string.close));
            return;
        }


        searchResultView.setVisibility(View.VISIBLE);
        adapter.update(fields);
    }
}
