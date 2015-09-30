package com.magic.somusic.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import com.magic.somusic.R;
import com.magic.somusic.ui.MyScrollView;


public class MainFragment extends Fragment {

    private ViewPager m_viewPager;
    private RadioGroup radioGroup;
    private LinearLayout ll_nav;
    Fragment[] fragments=new Fragment[2];
    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragments[0] = new MyPagerFragment();
        fragments[1] = new RecommendPageFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main,null);
        radioGroup = (RadioGroup) view.findViewById(R.id.main_fg_rg);
        radioGroup.check(R.id.main_fg_rb_my);
        ll_nav = (LinearLayout) view.findViewById(R.id.mainfg_ll_nav);
        m_viewPager = (ViewPager) view.findViewById(R.id.main_fragment_pager);
        m_viewPager.setAdapter(new MyPageAdapter(this.getActivity().getSupportFragmentManager()));
        m_viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        radioGroup.check(R.id.main_fg_rb_my);
                        ll_nav.setBackgroundColor(Color.argb(0x4f, 0x29, 0xb4, 0xff));
                        break;
                    case 1:
                        radioGroup.check(R.id.main_fg_rb_recommend);
                        ll_nav.setBackgroundColor(Color.argb(0xff,0x29,0xb4,0xff));
                        View view = (View) m_viewPager.getAdapter().instantiateItem(m_viewPager,1);
                        view.setPadding(view.getPaddingLeft(),ll_nav.getHeight(),view.getPaddingRight(),view.getPaddingBottom());
                        break;
                    case 2:
                        radioGroup.check(R.id.main_fg_rb_find);
                        ll_nav.setBackgroundColor(Color.argb(0xff,0x29,0xb4,0xff));
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.main_fg_rb_my:
                        m_viewPager.setCurrentItem(0);
                        break;
                    case R.id.main_fg_rb_recommend:
                        m_viewPager.setCurrentItem(1);
                        break;
                    case R.id.main_fg_rb_find:
                        m_viewPager.setCurrentItem(2);
                        break;
                }
            }
        });
        //m_viewPager.getAdapter().instantiateItem(m_viewPager,0);
        ll_nav.setBackgroundColor(Color.argb(0x4f, 0x29, 0xb4, 0xff));
        //View myView = (View)m_viewPager.getAdapter().instantiateItem(m_viewPager,0);
        //ScrollView scrollView = (ScrollView) myView.findViewById(R.id.myfg_scrview);

        /*scrollView.setOnScrollListener(new MyScrollView.OnScrollListener() {
            @Override
            public void onScroll(MyScrollView view, int x, int y, int oldx, int oldy) {
                ll_nav.setBackgroundColor(Color.argb((0x4f+y), 0x29, 0xb4, 0xff));
            }
        });*/
        return view;
    }

    public class MyPageAdapter extends PagerAdapter {
        FragmentManager fm;

        public MyPageAdapter(FragmentManager fm) {
            this.fm = fm;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //return super.instantiateItem(container, position);
            if (!fragments[position].isAdded()){
                android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                ft.add(fragments[position],fragments[position].getClass().getSimpleName());
                ft.commit();
                fm.executePendingTransactions();
            }
            if (fragments[position].getView().getParent()==null){
                container.addView(fragments[position].getView());
            }
            return fragments[position].getView();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //super.destroyItem(container, position, object);
            container.removeView(fragments[position].getView());
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }


        @Override
        public int getCount() {
            return fragments.length;
        }
    }
}
