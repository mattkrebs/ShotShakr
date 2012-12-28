package com.ratchet.ShotShakr;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.TextView;

public class ShotNameTV extends TextView {

	private Matrix mForward = new Matrix();

	public ShotNameTV(Context context) {
		super(context);

		// TODO Auto-generated constructor stub
	}

	public ShotNameTV(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public ShotNameTV(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	@Override
	protected void onDraw(Canvas canvas) {

		canvas.save();
		canvas.rotate(4);
		super.onDraw(canvas);
		canvas.restore();

	}

}
