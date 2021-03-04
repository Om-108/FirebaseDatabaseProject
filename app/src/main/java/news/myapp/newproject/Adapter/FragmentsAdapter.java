package news.myapp.newproject.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import news.myapp.newproject.Fragments.EnrollFragment;
import news.myapp.newproject.Fragments.UsersFragment;

public class FragmentsAdapter extends FragmentPagerAdapter {
    public FragmentsAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position)
        {
            case 0: return new UsersFragment();
            case 1: return new EnrollFragment();
            default: return new UsersFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;

        if(position == 0)
            title = "Users";
        if(position == 1)
            title = "Enroll";

        return title;
    }
}
