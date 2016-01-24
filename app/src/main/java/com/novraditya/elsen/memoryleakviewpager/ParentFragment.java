package com.novraditya.elsen.memoryleakviewpager;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.ButterKnife;

/**
 * Created by elsennovraditya on 1/21/16.
 */
public class ParentFragment extends Fragment {

    private static final AtomicInteger NEXT_GENERATED_ID = new AtomicInteger(1);
    private static final String TITLE = "TITLE";
    private static final int CHILD_COUNT = 5;
    private static final Map<String, List<String>> CHILD_FRAGMENT_TAGS_MAP = new HashMap<>();
    private LinearLayout mRootView;
    private String mParentId;

    public static ParentFragment getInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);

        ParentFragment parentFragment = new ParentFragment();
        parentFragment.setArguments(bundle);

        return parentFragment;
    }

    public static int generateViewId() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            for (; ; ) {
                final int result = NEXT_GENERATED_ID.get();
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF) {
                    newValue = 1; // Roll over to 1, not 0.
                }
                if (NEXT_GENERATED_ID.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        } else {
            return View.generateViewId();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParentId = getArguments().getString(TITLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (LinearLayout) inflater.inflate(R.layout.fragment_parent, container, false);
        ButterKnife.bind(this, mRootView);
        removeAllChildFragments();
        initChild();
        return mRootView;
    }

    private void removeAllChildFragments() {
        List<String> childFragmentTags = CHILD_FRAGMENT_TAGS_MAP.get(mParentId);
        if (childFragmentTags != null && !childFragmentTags.isEmpty()) {
            List<Fragment> childFragments = getChildFragmentManager().getFragments();
            if (childFragments != null && !childFragments.isEmpty()) {
                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                for (Fragment childFragment : childFragments) {
                    if (childFragment != null) {
                        if (childFragmentTags.contains(childFragment.getTag())) {
                            fragmentTransaction.remove(childFragment);
                        }
                    }
                }
                fragmentTransaction.commitAllowingStateLoss();
                childFragmentTags.clear();
            }
        }
    }

    private void initChild() {
        for (int i = 0; i < CHILD_COUNT; i++) {
            addFragmentToContainer(new ChildFragment());
        }
    }

    private void addFragmentToContainer(Fragment childFragment) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 10, 0, 0);

        int id = generateViewId();
        String tag = String.valueOf(id);
        addTagToChildFragmentTagsMap(tag);

        FrameLayout frameLayout = new FrameLayout(getActivity());
        frameLayout.setId(id);
        addViewToContainer(frameLayout, layoutParams);

        getChildFragmentManager()
                .beginTransaction()
                .replace(frameLayout.getId(), childFragment, tag)
                .commitAllowingStateLoss();
    }

    private void addTagToChildFragmentTagsMap(String childFragmentTag) {
        List<String> childFragmentTags = CHILD_FRAGMENT_TAGS_MAP.get(mParentId);
        if (childFragmentTags == null) {
            List<String> initialChildFragmentTags = new ArrayList<>();
            initialChildFragmentTags.add(childFragmentTag);
            CHILD_FRAGMENT_TAGS_MAP.put(mParentId, initialChildFragmentTags);
        } else {
            childFragmentTags.add(childFragmentTag);
        }
    }

    /**
     * Add view to container with configurable layout params
     */
    private void addViewToContainer(View view, LinearLayout.LayoutParams layoutParams) {
        view.setFocusable(false);
        view.setFocusableInTouchMode(false);

        mRootView.addView(view, layoutParams);
    }

}
