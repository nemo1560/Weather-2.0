package com.example.nemo1.weather21;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;

import com.example.nemo1.weather21.custom.CustomTextView;
import com.example.nemo1.weather21.entity.Country;

public class CountryInfoFragment extends DialogFragment {
    private CustomTextView population, callingCodes;
    private TextView fullname,region,capital,languages;
    private ImageView logo;
    private Country country;
    private Bitmap flag;

    public static CountryInfoFragment newInstance(Country country, byte[] flag){
        CountryInfoFragment countryInfoFragment = new CountryInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("country",country);
        bundle.putByteArray("flag",flag);
        countryInfoFragment.setArguments(bundle);
        return countryInfoFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_countryinfo,container,false);
        if(getArguments() != null){
            country = (Country) getArguments().getSerializable("country");
            byte[] byteArray = getArguments().getByteArray("flag");
            flag = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        region = view.findViewById(R.id.region);
        capital = view.findViewById(R.id.capital);
        population = view.findViewById(R.id.population);
        languages = view.findViewById(R.id.languages);
        callingCodes = view.findViewById(R.id.callingCodes);
        fullname = view.findViewById(R.id.fullname);
        logo = view.findViewById(R.id.logo);
        initLayout();
    }

    private void initLayout() {
        region.setText(country.getRegion());
        capital.setText(country.getCapital());
        population.setText(country.getPopulation());
        languages.setText(country.getLanguages());
        callingCodes.setText(country.getCallingCodes());
        fullname.setText(country.getFullname());
        logo.setImageBitmap(flag);
        logo.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.alpha_aim));
        logo.setVisibility(View.VISIBLE);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(1000, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }
}
