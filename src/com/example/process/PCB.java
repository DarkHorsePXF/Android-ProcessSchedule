package com.example.process;

import android.widget.Button;

public class PCB {


	/**
	 * 进程控制块
	 * @author pxf
	 * @date 2014-11-30
	 * 
	 */
	private String pcbName;	  	//进程名	
	private int timeNeeded;		//运行所需时间
	private int piority=0;		//优先级
	private boolean run=false;	//是否正在
	private int runtimes=1;		//执行次数
	private boolean isFinished;	//是否完成
	public Button btn;			//进程绑定的按钮
	public int curQueue=1;		//所在的队列
	public PCB(String name,int timeNeeded,int piority,Button btn){
		this.pcbName=name;
		this.piority=piority;
		this.timeNeeded=timeNeeded;
		this.btn=btn;
	}
	/**
	 * @return the jobName
	 */
	public String getName() {
		return pcbName;
	}
	/**
	 * @param jobName the jobName to set
	 */
	public void setName(String name) {
		this.pcbName = name;
	}
	/**
	 * @return the timeNeeded
	 */
	public int getTimeNeeded() {
		return timeNeeded;
	}
	/**
	 * @param timeNeeded the timeNeeded to set
	 */
	public void setTimeNeeded(int timeNeeded) {
		this.timeNeeded = timeNeeded;
	}
	
	/**
	 * @return the run
	 */
	public boolean isRun() {
		return run;
	}
	/**
	 * @param run the run to set
	 */
	public void setRun(boolean run) {
		this.run = run;
	}
	public int getPiority() {
		return piority;
	}
	public void setPiority(int piority) {
		this.piority = piority;
	}
	
	public boolean isFinished() {
		return isFinished;
	}
	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}
	public int getRuntimes() {
		return runtimes;
	}
	public void setRuntimes(int runtimes) {
		this.runtimes = runtimes;
	}


	

}
