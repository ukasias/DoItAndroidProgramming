package com.ukasias.android.multimemo;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.media.Image;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

public class CoverFlow extends Gallery {
    private static String TAG = "CoverFlow";

    /**
     * Variables
     */
    private Context context;
    private Camera camera;
    private static int maxRotationAngle = 55;   // ??
    private static int maxZoom = -60;           // ??
    private int centerPoint;

    public CoverFlow(Context context) {
        super(context);
        this.context = context;

        init();
    }

    public CoverFlow(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;

        init();
    }

    public CoverFlow(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        this.context = context;

        init();
    }

    private void init() {
        print("init() called.");
        camera = new Camera();
        this.setStaticTransformationsEnabled(true);
    }

    public int getMaxRotationAngle() {
        return maxRotationAngle;
    }

    public void setMaxRotationAngle(int maxRotationAngle) {
        CoverFlow.maxRotationAngle = maxRotationAngle;
    }

    public int getMaxZoom() {
        return maxZoom;
    }

    public void setMaxZoom(int zoom) {
        maxZoom = zoom;
    }

    private int getCenterOfCoverflow() {
        return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
    }

    private int getCenterOfView(View view) {
        return view.getLeft() + view.getWidth()/2;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        print("onSizeChanged(w: " + w + ", h: " + h +
                ", oldw: " + oldw + ", oldh: " + oldh + ") called.");

        centerPoint = getCenterOfCoverflow();
    }

    private void transformImageBitmap(ImageView child, Transformation t, int rotationAngle) {
        print("transformImageBitmap(rotationAngle: " + rotationAngle + ") called.");
        camera.save();

        // <1> translation to x, y, z
        camera.translate(0.0f, 0.0f, 100.0f);   // (x, y, z)
        int absRotationAngle = Math.abs(rotationAngle);
        if (absRotationAngle < maxRotationAngle) {
            float zoomAmount = (float) (maxZoom + (absRotationAngle * 1.5));
            camera.translate(0.0f, 0.0f, zoomAmount);
        }

        // <2> rotate to y
        camera.rotateY(rotationAngle);

        // <3> preTranslate, postTranslate
        final int imageWidth = child.getLayoutParams().width;
        final int imageHeight = child.getLayoutParams().height;

        final Matrix matrix = t.getMatrix();
        camera.getMatrix(matrix);

        matrix.preTranslate(-(imageWidth/2), -(imageHeight/2));
        matrix.postTranslate((imageWidth/2), (imageHeight/2));

        camera.restore();
    }

    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t) {
        final int childCenter = getCenterOfView(child);
        final int childWidth = child.getWidth();
        int rotationAngle = 0;

        t.clear();
        t.setTransformationType(Transformation.TYPE_MATRIX);

        /**
         * child view의 중심점과 Coverflow의 중심점 사이의 길이에 비례해 rotation angle을 크게 잡는다.
         * 두 중심점이 같은 위치일 때 rotation angle은 0.
         * 두 중심점의 최대 차이가 child view의 가로 길이일 때, max rotation angle을 갖는다.
         * max rotation angle은 55다.
         */

        if (childCenter == centerPoint) {
            /**
             * child의 center of width가 이 Coverflow의 center of width와 같을 때
             * : rotation angle은 0.
             */
            transformImageBitmap((ImageView) child, t, 0);
        }
        else {
            /**
             * child의 center of width가 이 Coverflow의 center of width와 다를 때,
             * 그 차이 값에 비례한 rotation angle 값을 갖는다.
             * 차이 값이 0 ~ width of child 일 때, 0 ~ 55(max rotation angle)이 된다.
             */
            rotationAngle = (int) ((float)(centerPoint - childCenter) / childWidth)
                    * maxRotationAngle;
            /* 계산상 이렇게 될 수가 있지 */
            if (Math.abs(rotationAngle) > maxRotationAngle) {
                rotationAngle =
                        rotationAngle > 0? maxRotationAngle : (-1) * maxRotationAngle;
            }
            transformImageBitmap((ImageView) child, t, rotationAngle);
        }

        return true;
    }

    private void print(String message) {
        Log.d(TAG, message + "\n");
    }
}
