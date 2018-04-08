package com.livos.companionplants.ui.plants;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.livos.companionplants.R;
import com.livos.companionplants.di.component.ActivityComponent;
import com.livos.companionplants.ui.base.BaseFragment;
import com.livos.companionplants.ui.events.EmptyTabEvent;
import com.livos.companionplants.ui.plants.adapters.PlantsRecyclerViewAdapter;
import com.livos.companionplants.ui.events.PlantSelectedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlantsFragment extends BaseFragment implements PlantsMvpView {
    public static final String TAG = "PlantsFragment";
    private int tabIndex = -1;
    private long selectedPlantId = 0;
    private PlantsRecyclerViewAdapter adapter;
    private String localeCode;

    List<AssociatedPlant> plants;
    PlantSelectedEvent plantSelectedEvent;



    @Inject
    PlantsMvpPresenter<PlantsMvpView> presenter;

    @BindView(R.id.rv_plants)
    RecyclerView rvPlants;

    @BindView(R.id.ll_tabs)
    LinearLayout llTabs;



    public static PlantsFragment newInstance(int tabIndex) {
        Bundle args = new Bundle();
        args.putInt("tabIndex",tabIndex);
        PlantsFragment fragment = new PlantsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            this.tabIndex = bundle.getInt("tabIndex");
            this.selectedPlantId = bundle.getLong("plantSelectedId");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plants, container, false);
        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            setUnBinder(ButterKnife.bind(this, view));
            presenter.onAttach(this);
        }
        localeCode = getResources().getConfiguration().locale.getLanguage();


        readBundle(getArguments());

        rvPlants.setLayoutManager(new GridLayoutManager(getContext(), 4));

        // Fetch the empty view from the layout and set it on
        // the new recycler view

        //rvPlants.setEmptyView(flPlantsEmptyView);


        adapter = new PlantsRecyclerViewAdapter(getContext());

        return view;
    }

    @Subscribe
    public void onSelectedPlantEvent(PlantSelectedEvent plantSelectedEvent) {
        this.plantSelectedEvent = plantSelectedEvent;
        this.plantSelectedEvent.setTabIdx(tabIndex);

        presenter.onSelectedPlantChanged(plantSelectedEvent, tabIndex);
    }


    @Override
    protected void setUp(View view) {
        rvPlants.setAdapter(adapter);

        presenter.onViewPrepared(tabIndex);


    }

    @Override
    public void loadPlants(List<AssociatedPlant> plants) {
        hideKeyboard();


        if(plants.size() != 0) {

            this.plants = plants;

            adapter.updateAssociatedPlants(plants, localeCode);
            adapter.notifyDataSetChanged();
            rvPlants.setVisibility(View.VISIBLE);
        } else {
            rvPlants.setVisibility(View.GONE);
        }



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDetach();
    }
}
