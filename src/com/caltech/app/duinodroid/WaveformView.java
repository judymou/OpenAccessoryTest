package com.caltech.app.duinodroid;

import java.util.ArrayList;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class WaveformView extends View {
	private final String TAG = "WavefomView";
	private Paint paint;
	//private final double MAX_ACCEL = 2 * 9.8;
	private float tInit;
	private float pInit;
	private float hInit;
	private float opticalInit;
	private float AHInit;
	private float AVInit;
	private float photovalInit;
	private float CH4Init;
	private float LPGInit;
	private float COInit;
	private float H2Init;
	private float geigerAvgGapInit;
	
	private double tScale;
	private double pScale;
	private double hScale;
	private double opticalScale;
	private double AHScale;
	private double AVScale;
	private double photovalScale;
	private double CH4Scale;
	private double LPGScale;
	private double COScale;
	private double H2Scale;
	private LinkedList<ValueMsg> samples;

	// colors from the Android UI guidelines
	private final int red = Color.parseColor("#CC0000");
	private final int blue = Color.parseColor("#003399");
	private final int green = Color.parseColor("#006600");
	private final int mediumGray = Color.parseColor("#808080");

	private final int border = 5;
	// duration of the waveform shown.
	private final int windowMilliseconds = 3000;
	// down-sample data to this rate for plotting
	private final int plotSamplesPerSecond = 100;

	/**
	 * @param context
	 */
	public WaveformView(Context context) {
		super(context);
		init();
	}

	public WaveformView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(red);

		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(4);

		samples = new LinkedList<ValueMsg>();
	}
	
	protected void addBatchData(ArrayList<ValueMsg> newSamples) {
		Log.i(TAG, "in running sensor value msg size: " + newSamples.size());
		
		double sampleSpacingMillis = 1000/ plotSamplesPerSecond;
		for (ValueMsg s : newSamples) {
			if (samples.isEmpty()) {
				tInit = s.getT();
				pInit = s.getP();
				hInit = s.getH();
				opticalInit = s.getOptical();
				AHInit = s.getAH();
				AVInit = s.getAV();
				photovalInit = s.getPhotoval();
				CH4Init = s.getCH4();
				LPGInit = s.getLPG();
				COInit = s.getCO();
				H2Init = s.getH2();
				geigerAvgGapInit = s.getgAvgGap();
			}
			
			long previousTime = System.currentTimeMillis() - windowMilliseconds;
			if (!samples.isEmpty()) {
				previousTime = samples.getLast().getTime();
			}
			
			if (s.getTime() > previousTime + sampleSpacingMillis) {
				samples.addLast(s);
			}
		}
		if (!samples.isEmpty()) {
			long latest = samples.getLast().getTime();
			long staleTime = latest - windowMilliseconds;
			while (!samples.isEmpty() && samples.getFirst().getTime() < staleTime) {
				samples.removeFirst();
			}
		}
		postInvalidate();
	}
	
	protected void addAccelSamples(ArrayList<Float> newSamples) {

//		double sampleSpacingMillis = 1000 / plotSamplesPerSecond;
//
//		// Only add sample if some amount of time has elapsed since the
//		// previous sample, e.g. downsample.
//		for (Float accelSample : newSamples) {
//
//			long previousTime = System.currentTimeMillis() - windowMilliseconds;
//			if (!samples.isEmpty()) {
//				previousTime = samples.getLast();
//			}
//
//			if (accelSample > previousTime + sampleSpacingMillis) {
//				samples.addLast(accelSample);
//			}
//		}
//
//		// only keep windowMilliseconds of data
//		if (!samples.isEmpty()) {
//			long latest = samples.getLast().t;
//			long staleTime = latest - windowMilliseconds;
//			while (!samples.isEmpty() && samples.getFirst().t < staleTime) {
//				samples.removeFirst();
//			}
//		}
		postInvalidate(); // Causes a screen refresh
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// an optimization: local variables are faster than accessing fields
		final Paint paint = this.paint;

		// Background color
		canvas.drawColor(Color.BLACK);

		// Parameters of the view.
		// TODO: this could be pushed into init and onSizeChanged
		final int height = this.getHeight();
		final int width = this.getWidth();

		// "box" to draw the waveforms in
		RectF waveformRect;
		/*
		 * standard image coordinate system, where (0,0) is the top left, y
		 * increases to the right, and x increases down
		 */
		int rectLeft = border;
		int rectTop = border;
		int rectRight = width - border;
		int rectBottom = height - border;
		Log.d(TAG, "l: " + rectLeft + " t: " + rectTop + " r: " + rectRight + " b: " + rectBottom);
		waveformRect = new RectF(rectLeft, rectTop, rectRight, rectBottom);

		paint.setColor(mediumGray);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawRect(waveformRect, paint);

		// conversion factor between acceleration and vertical pixel position
		//double vscale = (rectBottom - rectTop) / (2.0 * MAX_ACCEL);
		double eachScreenSize = (rectBottom - rectTop) / 12;
		// conversion factor between milliseconds to horizontal pixel position.
		double hscale = (rectRight - rectLeft)
				/ (1.0 * windowMilliseconds - 100); // buffer 100 ms off screen

		// plot the samples
		Path pathT = new Path();
		Path pathP = new Path();
		Path pathH = new Path();
		Path pathOptical = new Path();
		Path pathAH = new Path();
		Path pathAV = new Path();
		Path pathPhotoval = new Path();
		Path pathCH4 = new Path();
		Path pathLPG = new Path();
		Path pathCO = new Path();
		Path pathH2 = new Path();
		Path pathGeigerAvgGap = new Path();

		if (samples.isEmpty()) {
			Log.d(TAG, "samples is empty");
			return;
		}

		Log.d(TAG, "*******************plotting*****************************");
		//Log.d(TAG, "plotting " + samples.size() + " samples...");
		// First element is oldest sample
		//long startTime = samples.getFirst();
		long startTime = samples.getFirst().getTime();
		
		// move paths to first position
//		double x = samples.getFirst().getT() * vscale;
//		double y = samples.getFirst().getH() * vscale;
//		double z = samples.getFirst().getP() * vscale;
//
//		pathX.reset();
//		pathX.moveTo(absoluteX(waveformRect, 0), absoluteY(waveformRect,
//				(float) x));
//
//		pathY.reset();
//		pathY.moveTo(absoluteX(waveformRect, 0), absoluteY(waveformRect,
//				(float) y));
//
//		pathZ.reset();
//		pathZ.moveTo(absoluteX(waveformRect, 0), absoluteY(waveformRect,
//				(float) z));
		
		float verticalStartPathT = ((float)eachScreenSize)/2;
		float verticalStartPathP = (float) (((float)eachScreenSize)/2 + eachScreenSize);
		float verticalStartPathH = (float) (((float)eachScreenSize)/2 + 2 * eachScreenSize);
		float verticalStartPathOptical = (float) (((float)eachScreenSize)/2 + 3 * eachScreenSize);
		float verticalStartPathAH = (float) (((float)eachScreenSize)/2 + 4 * eachScreenSize);
		float verticalStartpathAV = (float) (((float)eachScreenSize)/2 + 5 * eachScreenSize);
		float verticalStartPathPhotoval = (float) (((float)eachScreenSize)/2 + 6 * eachScreenSize);
		float verticalStartPathCH4 = (float) (((float)eachScreenSize)/2 + 7 * eachScreenSize);
		float verticalStartPathLPG = (float) (((float)eachScreenSize)/2 + 8 * eachScreenSize);
		float verticalStartPathCO = (float) (((float)eachScreenSize)/2 + 9 * eachScreenSize);
		float verticalStartPathH2 = (float) (((float)eachScreenSize)/2 + 10 * eachScreenSize);
		float verticalStartPathGeigerAvgGap = (float) (((float)eachScreenSize)/2 + 11 * eachScreenSize);
		pathT.reset();
		pathT.moveTo(absoluteX(waveformRect, 0), verticalStartPathT);
		pathP.reset();
		pathP.moveTo(absoluteX(waveformRect, 0), verticalStartPathP);
		pathH.reset();
		pathH.moveTo(absoluteX(waveformRect, 0), verticalStartPathH);
		pathOptical.reset();
		pathOptical.moveTo(absoluteX(waveformRect, 0), verticalStartPathOptical);
		pathAH.reset();
		pathAH.moveTo(absoluteX(waveformRect, 0), verticalStartPathAH);
		pathAV.reset();
		pathAV.moveTo(absoluteX(waveformRect, 0), verticalStartpathAV);
		pathPhotoval.reset();
		pathPhotoval.moveTo(absoluteX(waveformRect, 0), verticalStartPathPhotoval);
		pathCH4.reset();
		pathCH4.moveTo(absoluteX(waveformRect, 0), verticalStartPathCH4);
		pathLPG.reset();
		pathLPG.moveTo(absoluteX(waveformRect, 0), verticalStartPathLPG);
		pathCO.reset();
		pathCO.moveTo(absoluteX(waveformRect, 0), verticalStartPathCO);
		pathH2.reset();
		pathH2.moveTo(absoluteX(waveformRect, 0), verticalStartPathH2);
		pathGeigerAvgGap.reset();
		pathGeigerAvgGap.moveTo(absoluteX(waveformRect, 0), verticalStartPathGeigerAvgGap);

		for (ValueMsg sample : samples) {
			// TODO: use quadto or some other smooth interpolation

			
			float tPixel = verticalStartPathT + (sample.getT() - tInit);
			float pPixel = verticalStartPathP + (sample.getP() - pInit);
			float hPixel = verticalStartPathH + (sample.getH() - hInit);
			float opticalPixel = verticalStartPathOptical + (sample.getOptical() - opticalInit);
			float AHPixel = verticalStartPathAH + (sample.getAH() - AHInit);
			float AVPixel = verticalStartpathAV + (sample.getAV() - AVInit);
			float photovalPixel = verticalStartPathPhotoval + (sample.getPhotoval() - photovalInit);
			float CH4Pixel = verticalStartPathCH4 + (sample.getCH4() - CH4Init);
			float LPGPixel = verticalStartPathLPG + (sample.getLPG() - LPGInit);
			float COPixel = verticalStartPathCO + (sample.getCO() - COInit);
			float H2Pixel = verticalStartPathH2 + (sample.getH2() - H2Init);
			float geigerAvgGapPixel = verticalStartPathGeigerAvgGap + (sample.getgAvgGap() - geigerAvgGapInit);

			// Log.d(TAG,"Lag: " + (sample.t-startTime));
			double t = (sample.getTime() - startTime) * hscale;
			// Log.d(TAG, "converted t: " + t);

			float timePixel = absoluteX(waveformRect, (float) t);

			pathT.lineTo(timePixel, tPixel);
			pathP.lineTo(timePixel, pPixel);
			pathH.lineTo(timePixel, hPixel);
			pathOptical.lineTo(timePixel, opticalPixel);
			pathAH.lineTo(timePixel, AHPixel);
			pathAV.lineTo(timePixel, AVPixel);
			pathPhotoval.lineTo(timePixel, photovalPixel);
			pathCH4.lineTo(timePixel, CH4Pixel);
			pathLPG.lineTo(timePixel, LPGPixel);
			pathCO.lineTo(timePixel, COPixel);
			pathH2.lineTo(timePixel, H2Pixel);
			pathGeigerAvgGap.lineTo(timePixel, geigerAvgGapPixel);
			
		}

		Log.d(TAG, "*******************plotting2*****************************");

		// round off the corners
		PathEffect smooth = new CornerPathEffect(10);
		paint.setPathEffect(smooth);

		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(red);
		canvas.drawPath(pathT, paint);
		paint.setColor(green);
		canvas.drawPath(pathP, paint);
		paint.setColor(blue);
		canvas.drawPath(pathH, paint);
		paint.setColor(red);
		canvas.drawPath(pathOptical, paint);
		paint.setColor(green);
		canvas.drawPath(pathAH, paint);
		paint.setColor(blue);
		canvas.drawPath(pathAV, paint);
		paint.setColor(red);
		canvas.drawPath(pathPhotoval, paint);
		paint.setColor(green);
		canvas.drawPath(pathCH4, paint);
		paint.setColor(blue);
		canvas.drawPath(pathLPG, paint);
		paint.setColor(red);
		canvas.drawPath(pathCO, paint);
		paint.setColor(green);
		canvas.drawPath(pathH2, paint);
		paint.setColor(blue);
		canvas.drawPath(pathGeigerAvgGap, paint);
	}

	/**
	 *
	 * @param rectF
	 * @param x
	 *            Horizontal pixel, relative to the rectangle's left.
	 * @return Absolute horizontal pixel coordinate.
	 */
	private float absoluteX(RectF rectF, float x) {
		return rectF.left + x;
	}

	/**
	 *
	 * @param rectF
	 * @param y
	 *            Vertical pixel, relative to the rectangle's left.
	 * @return Absolute vertical pixel coordinate.
	 */
	private float absoluteY(RectF rectF, float y) {
		return rectF.centerY() + y;
	}

}