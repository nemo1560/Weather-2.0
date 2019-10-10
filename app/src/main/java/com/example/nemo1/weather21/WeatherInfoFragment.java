package com.example.nemo1.weather21;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.nemo1.weather21.custom.CustomTextView;
import com.example.nemo1.weather21.entity.Current;

public class WeatherInfoFragment extends DialogFragment {
    private CustomTextView feel_c,
            wind,
            pressure,
            vis_km;

    private ImageView logo;
    private Current current;

    public WeatherInfoFragment() {
    }

    public static WeatherInfoFragment newInstance(Current current){
        WeatherInfoFragment weatherInfoFragment = new WeatherInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("current",current);
        weatherInfoFragment.setArguments(bundle);
        return weatherInfoFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weatherinfo,container,false);
        if(getArguments() != null){
            current = (Current) getArguments().getSerializable("current");
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        feel_c = view.findViewById(R.id.feel_c);
        wind = view.findViewById(R.id.wind);
        pressure = view.findViewById(R.id.pressure);
        vis_km = view.findViewById(R.id.vis_km);
        logo = view.findViewById(R.id.logo);
        initLayout();
    }

    private void initLayout() {
        feel_c.setText(current.getFeelslike_c());
        wind.setText(current.getWind_kph());
        pressure.setText(current.getPressure_mb());
        vis_km.setText(current.getVis_km());
        logo.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.alpha_aim));
        logo.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(1000, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }
}
