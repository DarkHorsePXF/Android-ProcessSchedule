package com.example.process;

import java.util.ArrayList;

import com.example.process.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

public class PrintActivity extends Activity implements OnTouchListener{
	//手指向右滑动时的最小速度  
    private static final int XSPEED_MIN = 200;  
      
    //手指向右滑动时的最小距离  
    private static final int XDISTANCE_MIN = 150;  
      
    //记录手指按下时的横坐标。  
    private float xDown;  
      
    //记录手指移动时的横坐标。  
    private float xMove;  
      
    //用于计算手指滑动的速度。  
    private VelocityTracker mVelocityTracker;


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_print);
		LinearLayout ll=(LinearLayout) findViewById(R.id.llPrint);
		ListView lvPrint=(ListView) findViewById(R.id.lvPrint);
		
		ll.setOnTouchListener(this);
		
		Intent intent=getIntent();
		Object[] printObjects =intent.getStringArrayListExtra("print").toArray();
		ArrayList<String> printStrings = new ArrayList<String>();
 		if(printObjects.length>0){
			for(int i=0;i<printObjects.length;i++){
				printStrings.add(printObjects[i].toString());
			}
		}
		
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, R.layout.txt_print, printStrings);
		lvPrint.setAdapter(adapter);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		createVelocityTracker(event);  
        switch (event.getAction()) {  
        case MotionEvent.ACTION_DOWN:  
            xDown = event.getRawX();  
            break;  
        case MotionEvent.ACTION_MOVE:  
            xMove = event.getRawX();  
            //活动的距离  
            int distanceX = (int) (xMove - xDown);  
            //获取顺时速度  
            int xSpeed = getScrollVelocity();  
            //当滑动的距离大于我们设定的最小距离且滑动的瞬间速度大于我们设定的速度时，返回到上一个activity  
            if(distanceX > XDISTANCE_MIN && xSpeed > XSPEED_MIN) {  
                finish();  
            }              break;  
        case MotionEvent.ACTION_UP:  
            recycleVelocityTracker();  
            break;  
        default:  
            break;  
        }
		return true;
	}
	private void createVelocityTracker(MotionEvent event) {  
        if (mVelocityTracker == null) {  
            mVelocityTracker = VelocityTracker.obtain();  
        }  
        mVelocityTracker.addMovement(event);  
    }  
      
    /** 
     * 回收VelocityTracker对象。 
     */  
    private void recycleVelocityTracker() {  
        mVelocityTracker.recycle();  
        mVelocityTracker = null;  
    }  
      
    /** 
     * 获取手指在content界面滑动的速度。 
     *  
     * @return 滑动速度，以每秒钟移动了多少像素值为单位。 
     */  
    private int getScrollVelocity() {  
        mVelocityTracker.computeCurrentVelocity(1000);  
        int velocity = (int) mVelocityTracker.getXVelocity();  
        return Math.abs(velocity);  
    }

	

}
