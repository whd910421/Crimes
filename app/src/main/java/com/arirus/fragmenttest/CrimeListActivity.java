package com.arirus.fragmenttest;

import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by whd910421 on 16/7/26.
 */
public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_twopane;
    }
}
