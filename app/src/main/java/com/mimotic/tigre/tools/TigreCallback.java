package com.mimotic.tigre.tools;

import android.support.v4.app.Fragment;

public interface TigreCallback {

    public void loadFragment(Fragment mfragment);

    public void loadFragment(Fragment mfragment, boolean addToBack);

}
