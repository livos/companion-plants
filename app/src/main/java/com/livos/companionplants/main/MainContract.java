package com.livos.companionplants.main;


public interface MainContract {
    interface View {
        void lockDrawer();
        void unlockDrawer();
    }

    interface Presenter {
        void dropView();
        void setView(MainContract.View view);
        void onDrawerOptionAboutClick();
        void onDrawerRateUsClick();
        void onNavMenuCreated();
    }
}
