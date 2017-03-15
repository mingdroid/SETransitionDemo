package me.mingdroid.setransition;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DestinationActivity extends AppCompatActivity {
    private int current;
    private int adapterPosition;

    private ViewPager viewPager;
    private ColorDrawable colorDrawable;
    private int ALPHA_MAX = 0xFF;

    private DismissFrameLayout.OnDismissListener onDismissListener = new DismissFrameLayout.OnDismissListener() {
        @Override
        public void onScaleProgress(float scale) {
            colorDrawable.setAlpha(
                    Math.min(ALPHA_MAX, colorDrawable.getAlpha() - (int) (scale * ALPHA_MAX)));
        }

        @Override
        public void onDismiss() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAfterTransition();
            } else {
                finish();
            }
        }

        @Override
        public void onCancel() {
            colorDrawable.setAlpha(ALPHA_MAX);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
        }
        setContentView(R.layout.activity_destination);
        FrameLayout container = (FrameLayout) findViewById(R.id.container);
        colorDrawable = new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark));
        container.setBackgroundDrawable(colorDrawable);

        Intent intent = getIntent();
        ArrayList<Uri> uris = intent.getParcelableArrayListExtra("uris");
        adapterPosition = intent.getIntExtra("adapter_position", 0);
        current = intent.getIntExtra("current", 0);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new PhotoAdapter(uris, onDismissListener));
        viewPager.setCurrentItem(current);
    }

    @Override
    public void finishAfterTransition() {
        int pos = viewPager.getCurrentItem();
        Intent intent = new Intent();
        intent.putExtra("exit_position", pos);
        setResult(RESULT_OK, intent);
        if (current != pos) {
            View view = viewPager.findViewWithTag(
                    getString(R.string.transition_name, adapterPosition, pos));
            setSharedElementCallback(view);
        }
        super.finishAfterTransition();
    }

    @TargetApi(21)
    private void setSharedElementCallback(final View view) {
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                names.clear();
                sharedElements.clear();
                names.add(view.getTransitionName());
                sharedElements.put(view.getTransitionName(), view);
            }
        });
    }

    @TargetApi(21)
    private void setStartPostTransition(final View sharedView) {
        sharedView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedView.getViewTreeObserver().removeOnPreDrawListener(this);
                        startPostponedEnterTransition();
                        return false;
                    }
                });
    }

    public class PhotoAdapter extends PagerAdapter {
        List<Uri> uris;
        DismissFrameLayout.OnDismissListener onDismissListener;

        public PhotoAdapter(List<Uri> uris, DismissFrameLayout.OnDismissListener onDismissListener) {
            this.uris = uris;
            this.onDismissListener = onDismissListener;
        }

        @Override
        public int getCount() {
            return uris.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(container.getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setImageURI(uris.get(position));
            DismissFrameLayout layout = new DismissFrameLayout(container.getContext());
            layout.setDismissListener(onDismissListener);
            layout.setLayoutParams(new ViewPager.LayoutParams());
            layout.addView(imageView);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                String name = container.getContext()
                        .getString(R.string.transition_name, adapterPosition, position);
                imageView.setTransitionName(name);
                imageView.setTag(name);
                if (position == current) {
                    setStartPostTransition(imageView);
                }
            }

            container.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}
