package com.example.process;

public class CPU {
	
	/**
	 * 用来描述处理机状态
	 */
	private boolean isBusy=false;
	private int runtime=2;
	public int queue1continueTimes=0;
	public int queue2continueTimes=0;
	/**
	 * @return the isBusy
	 */
	public boolean isBusy() {
		return isBusy;
	}

	/**
	 * @param isBusy the isBusy to set
	 */
	public void setBusy(boolean isBusy) {
		this.isBusy = isBusy;
	}

	public int getRuntime() {
		return runtime;
	}

	public void setRuntime(int runtime) {
		this.runtime = runtime;
	}


}
