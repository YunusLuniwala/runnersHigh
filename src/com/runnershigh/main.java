package com.runnershigh;

import android.R.bool;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


public class main extends Activity {
		PowerManager.WakeLock wakeLock ;
		/** Called when the activity is first created. */
	    @Override
		public void onCreate(Bundle savedInstanceState) {
	    	super.onCreate(savedInstanceState);
	    	//setContentView(R.layout.main);	 
	    	
	    	PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "tag");
			wakeLock.acquire();	    	
				        
			requestWindowFeature(Window.FEATURE_NO_TITLE);  
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
			
			//setContentView(R.layout.runnershigh);	        
			//(RunnersHighView) findViewById(R.id.runnersHighViewXML);
			RunnersHighView gameView = new RunnersHighView(getApplicationContext()); 
			setContentView(gameView);	     
			
		}	
		
	    @Override
	    protected void onDestroy() {

			super.onDestroy();
			wakeLock.release();
		}
		@Override
		public void onResume() {

			super.onResume();
			wakeLock.acquire();
		}
		@Override
		public void onPause() {

			super.onResume();
			wakeLock.release();
		}
	    
    
	public class RunnersHighView extends View implements Runnable {
		private Player player;
		private Level level;
		private int width;
		private int height;
		private Bitmap resetButton;
		private int resetButtonX = 350;
		private int resetButtonY = 10;
		private int resetButtonWidth = 100;
		private int resetButtonHeight = 41;
	
		public RunnersHighView(Context context) {
			super(context);
			
			Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			width= display.getWidth(); 
			height= display.getHeight();
			Util.getInstance().setScreenHeight(height);
			
			
			player = new Player(getApplicationContext(),height);
			level = new Level(context, width, height);
			
			resetButton = BitmapFactory.decodeResource(context.getResources(), R.drawable.resetbutton);
			
			Thread rHThread = new Thread(this);
			rHThread.start();
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				//REM Player update vor collision
				
				//player.doJump();
				if (player.update(level.getBlockData()))
					level.update();
				/*player.checkCollision(level.getBlockData());
				
				*/
				
				postInvalidate();
				try{ Thread.sleep(10); }
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		public void draw(Canvas canvas) {
			Paint paint = new Paint();
			paint.setColor(Color.LTGRAY);
			paint.setStyle(Paint.Style.FILL);
			paint.setAntiAlias(true);
			paint.setTextSize(18);

			canvas.drawText("Your Score: " + Integer.toString(level.getScoreCounter()), 20, 20, paint);
			canvas.drawBitmap(resetButton, resetButtonX, resetButtonY, null);

			level.draw(canvas);
			player.draw(canvas);
			
			invalidate();
		}
		
		public boolean onTouchEvent(MotionEvent event) {
			if(event.getAction() == MotionEvent.ACTION_UP)
				player.setJump(false);
			
			else if(event.getAction() == MotionEvent.ACTION_DOWN){
				player.setJump(true);
				if(event.getX() <= resetButtonX+resetButtonWidth && event.getX() > resetButtonX)
					if(event.getY() <= resetButtonY+resetButtonHeight && event.getY() > resetButtonY){
						Log.d("reset", "reset pressed");
						player.reset();
					}
			}
				
			
				
			return true;
		}
	}
}
