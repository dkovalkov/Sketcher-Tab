package org.sketchertab;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import org.sketchertab.style.StylesFactory;

import java.util.HashMap;
import java.util.Map;

public final class Surface extends SurfaceView implements Callback {
	private DrawThread drawThread;
	private final Canvas drawCanvas = new Canvas();
	private final DrawController drawController = new DrawController(drawCanvas);
	private Bitmap initialBitmap;
	private Bitmap bitmap;

	public Surface(Context context, AttributeSet attributes) {
		super(context, attributes);

		getHolder().addCallback(this);
		setFocusable(true);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return drawController.onTouch(this, event);
	}

	public void setStyle(Style style) {
		drawController.setStyle(style);
	}

	public DrawThread getDrawThread() {
		if (drawThread == null) {
			drawThread = new DrawThread();
		}
		return drawThread;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.eraseColor(Color.WHITE);

		drawCanvas.setBitmap(bitmap);

		if (initialBitmap != null) {
			drawCanvas.drawBitmap(initialBitmap, 0, 0, null);
		}
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
                e.printStackTrace();
			}
		}
		drawThread = null;
	}

	public void clearBitmap() {
        Bitmap old = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Map<StylesFactory.BrushType, Object> brushData = new HashMap<StylesFactory.BrushType, Object>();
        StylesFactory.saveState(brushData);

        bitmap.eraseColor(drawController.getBackgroundColor());
        drawController.clear();
        HistoryItem item = new HistoryItem(old, brushData);
        DocumentHistory.getInstance().pushNewItem(item);
	}

	public void setPaintColor(int color) {
		drawController.setPaintColor(color);
	}

	public int getPaintColor() {
		return drawController.getPaintColor();
	}

    public void setOpacity(int opacity) {
        drawController.setOpacity(opacity);
    }

    public int getOpacity() {
        return drawController.getOpacity();
    }

    public void setStrokeWidth(float width) {
        drawController.setStrokeWidth(width);
    }

    public float getStrokeWidth() {
        return drawController.getStrokeWidth();
    }

    public void setBackgroundColor(int color) {
		drawController.setBackgroundColor(color);
	}

    public int getBackgroundColor() {
		return drawController.getBackgroundColor();
	}

	public void setInitialBitmap(Bitmap initialBitmap) {
		this.initialBitmap = initialBitmap;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        drawCanvas.setBitmap(bitmap);
    }

    public Canvas getDrawCanvas() {
        return drawCanvas;
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
                        drawController.draw();
                        canvas.drawBitmap(bitmap, 0, 0, null);
                    }

                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
