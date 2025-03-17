package com.example.smartnfctag;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.smartnfctag.Fragment.Page1Fragment;
import com.example.smartnfctag.Fragment.Page2Fragment;
import com.example.smartnfctag.Fragment.Page3Fragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new Page1Fragment();
            case 1:
                return new Page2Fragment();
            case 2:
                return new Page3Fragment();
            default:
                return new Page1Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}