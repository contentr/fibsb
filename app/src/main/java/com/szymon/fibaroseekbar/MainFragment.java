package com.szymon.fibaroseekbar;

import android.app.Fragment;
import android.content.ClipData;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class MainFragment extends Fragment
        implements View.OnDragListener {

    private CustomSeekBar mSeekBar = null;

    public MainFragment() {
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container);

        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.layout);
        layout.setOnDragListener(this);

        mSeekBar = (CustomSeekBar) view.findViewById(R.id.seekBar);

        return view;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DROP:
                float seekBarHeight = (float)mSeekBar.getHeight();
                float position      = event.getY() - (seekBarHeight / 2);
                float paddingTop    = (float)v.getPaddingTop();
                float available     = (float)(v.getHeight() - v.getPaddingBottom()) - seekBarHeight;

                if (position < paddingTop) {
                    position = paddingTop;
                } else if (position > available) {
                    position = available;
                }

                mSeekBar.setY(position);
                break;
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mSeekBar = null;
    }
}
