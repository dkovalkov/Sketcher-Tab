package org.sketchertab;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;

public final class Surface extends SurfaceView implements Callback {
	private DrawThread drawThread;
	private final Canvas drawCanvas = new Canvas();
	private final Controller controller = new Controller(drawCanvas);
	private Bitmap initialBitmap;
	private Bitmap bitmap;
	private final HistoryHelper mHistoryHelper = new HistoryHelper(this);
    private float curX, curY;
    private Context context;

	public Surface(Context context, AttributeSet attributes) {
		super(context, attributes);

        this.context = context;

		getHolder().addCallback(this);
		setFocusable(true);
	}

    private void switchMenu() {
        ((Sketcher) context).switchToolbars();
    }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                curX = event.getRawX();
                curY = event.getRawY();
//                Log.i("cur X, Y", String.valueOf(curX) + " " + String.valueOf(curY));
                break;
            case MotionEvent.ACTION_UP:
//                mHistoryHelper.saveState();
                if (curX == event.getRawX() && curY == event.getRawY()) {
                    switchMenu();
                }

                break;
		}
		return controller.onTouch(this, event);
	}

	public void setStyle(Style style) {
		controller.setStyle(style);
	}

    public Style getStyle() {
        return controller.getStyle();
    }

	public DrawThread getDrawThread() {
		if (drawThread == null) {
			drawThread = new DrawThread();
		}
		return drawThread;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.eraseColor(Color.WHITE);

		drawCanvas.setBitmap(bitmap);

		if (initialBitmap != null) {
			drawCanvas.drawBitmap(initialBitmap, 0, 0, null);
		}
//		mHistoryHelper.saveState();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		getDrawThread().start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		getDrawThread().stopDrawing();
		while (true) {
			try {
				getDrawThread().join();
				break;
			} catch (InterruptedException e) {
			}
		}
		drawThread = null;
	}

	public void clearBitmap() {
		bitmap.eraseColor(controller.getBackgroundColor());
		controller.clear();
//		mHistoryHelper.saveState();
	}

	public void setPaintColor(int color) {
		controller.setPaintColor(color);
	}

	public int getPaintColor() {
		return controller.getPaintColor();
	}

    public void setOpacity(int opacity) {
        controller.setOpacity(opacity);
    }

    public int getOpacity() {
        return controller.getOpacity();
    }

    public void setStrokeWidth(float width) {
        controller.setStrokeWidth(width);
    }

    public float getStrokeWidth() {
        return controller.getStrokeWidth();
    }

    public void setBackgroundColor(int color) {
		controller.setBackgroundColor(color);
	}

    public int getBackgroundColor() {
		return controller.getBackgroundColor();
	}

	public void setInitialBitmap(Bitmap initialBitmap) {
		this.initialBitmap = initialBitmap;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

//	public void undo() {
//		mHistoryHelper.undo();
//	}

    public final class DrawThread extends Thread {
        private boolean mRun = true;
        private boolean mPause = false;

        @Override
        public void run() {
            waitForBitmap();

            final SurfaceHolder surfaceHolder = getHolder();
            Canvas canvas = null;

            while (mRun) {
                try {
                    while (mRun && mPause) {
                        Thread.sleep(100);
                    }

                    canvas = surfaceHolder.lockCanvas();
                    if (canvas == null) {
                        break;
                    }

                    synchronized (surfaceHolder) {
                        controller.draw();
                        canvas.drawBitmap(bitmap, 0, 0, null);
                    }

                    Thread.sleep(10);
                } catch (InterruptedException e) {
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

        private void waitForBitmap() {
            while (bitmap == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stopDrawing() {
            mRun = false;
        }

        public void pauseDrawing() {
            mPause = true;
        }

        public void resumeDrawing() {
            mPause = false;
        }
    }
}
