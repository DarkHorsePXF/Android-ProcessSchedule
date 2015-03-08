package com.example.process;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import com.example.process.R;



import android.R.bool;
import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint.Join;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.text.format.Time;
import android.webkit.WebView.FindListener;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks{
	ActionBarDrawerToggle drawerToggle;

	public final static int PCB_NUMBER=5;
	public final static int RR=1;//调度算法
	public final static int PIORITY=2;
	public final static int MFQ=3;
	
	private static int scheduleAlg=-1;
	private static CPU cpu;
	private static Button btnPcb1;
	private static boolean isProgramRun=false;
	private static Button btnCPU;
	private static Button btnPcb2;
	private static Button btnPcb3;
	private static Button btnPcb4;
	private static Button btnPcb5;
	private static ProgressBar pbCPU;
	private static ProgressBar pbRR;
	
	private static ArrayList<String> printStrings;
	private static LinkedList<PCB> pcbReadyQueue1;
	private static LinkedList<PCB> pcbReadyQueue2;
	private static LinkedList<PCB> pcbReadyQueue3;
	private static TextView txtProcessReadyQueue1;
	private static TextView txtProcessReadyQueue2;
	private static TextView txtProcessReadyQueue3;
	private static LinearLayout llpcRQ2;
	private static LinearLayout llpcRQ3;
	
	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		
		cpu=new CPU();
		printStrings=new ArrayList<String>();
		
	}

	

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						PlaceholderFragment.newInstance(position + 1)).commit();
	}

	public void onSectionAttached(int position) {
		MainActivity.scheduleAlg=position;
		switch (position) {
		case RR:
			mTitle = getString(R.string.title_section1);
			alert(this, "您所选择的是轮转法");
			break;
		case PIORITY:
			mTitle = getString(R.string.title_section2);
			alert(this,"您所选择的是优先级轮转法");
			break;
		case MFQ:
			mTitle = getString(R.string.title_section3);
			alert(this,"您所选择的是多级反馈队列轮转法");
			break;
		}
		isProgramRun=false;
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		if (item.getItemId() == R.id.action_run&&!isProgramRun) {
			if(scheduleAlg==-1){
				Toast.makeText(this, "未选择调度算法！请选择！", Toast.LENGTH_LONG)
						.show();
			}
			else{
				alert(this,"CPU开始运转……");
				createQueue();
				btnCPU.setBackgroundColor(getResources().getColor(R.color.green));
				isProgramRun=true;
				return true;
			}
			
		}
		else if (item.getItemId() == R.id.action_stop&&isProgramRun) {
			alert(this,"停止调度……");
			btnCPU.setBackgroundColor(getResources().getColor(R.color.gray));
			isProgramRun=false;
			Button[] buttons={btnCPU,btnPcb1,btnPcb2,btnPcb3,btnPcb4,btnPcb5};
			txtProcessReadyQueue1.setText("");
			
			pcbReadyQueue1.clear();
			if(scheduleAlg==MFQ){
				pcbReadyQueue2.clear();
				pcbReadyQueue3.clear();
				txtProcessReadyQueue2.setText("");
				txtProcessReadyQueue3.setText("");
			}
			for(int i=0;i<buttons.length;i++){
				buttons[i].setBackgroundColor(getResources().getColor(R.color.gray));
				if(i>0){
					buttons[i].setClickable(true);
				}
			}
			return true;
		}

		else if (item.getItemId() == R.id.action_print) {
			Intent intent=new Intent();
			intent.setClass(this, PrintActivity.class);
			intent.putStringArrayListExtra("print", printStrings);
			startActivity(intent);
			return true;
		}
		else if(item.getItemId() == R.id.action_clean){
			alert(this,"清除打印记录！");
			printStrings.clear();
		}
		return super.onOptionsItemSelected(item);
	}

	private void createQueue() {

		pcbReadyQueue1=new LinkedList<PCB>();
		if(scheduleAlg==MFQ){
			pcbReadyQueue2=new LinkedList<PCB>();
			pcbReadyQueue3=new LinkedList<PCB>();
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements OnClickListener{
		
		private ListView lvJobInfo;
		

		private Button btnShowLv;
		
		private PCB pcb1;
		private PCB pcb2;
		private PCB pcb3;
		private PCB pcb4;
		private PCB pcb5;
		


		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			
			return rootView;
		}
		
		@Override
		public void onStart() {
			super.onStart();
			initData();
			getJCB();
			MyAdapter adapter=new MyAdapter();
			lvJobInfo.setAdapter(adapter);
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
		private void initData() {
			llpcRQ2=(LinearLayout) getActivity().findViewById(R.id.llpcRQ2);
			llpcRQ3=(LinearLayout) getActivity().findViewById(R.id.llpcRQ3);
			txtProcessReadyQueue1=(TextView) getActivity().findViewById(R.id.txtReadyQueue1);
			txtProcessReadyQueue2=(TextView) getActivity().findViewById(R.id.txtReadyQueue2);
			txtProcessReadyQueue3=(TextView) getActivity().findViewById(R.id.txtReadyQueue3);
			btnPcb1=(Button) getActivity().findViewById(R.id.btnJob1);
			btnPcb2=(Button) getActivity().findViewById(R.id.btnJob2);
			btnPcb3=(Button) getActivity().findViewById(R.id.btnJob3);
			btnPcb4=(Button) getActivity().findViewById(R.id.btnJob4);
			btnPcb5=(Button) getActivity().findViewById(R.id.btnJob5);
			btnShowLv=(Button) getActivity().findViewById(R.id.btnShowLv);
			btnCPU=(Button) getActivity().findViewById(R.id.btnCPU);
			
			pbCPU=(ProgressBar) getActivity().findViewById(R.id.pbCPU);
			pbRR=(ProgressBar) getActivity().findViewById(R.id.pbRR);
			lvJobInfo=(ListView) getActivity().findViewById(R.id.lvJob);
			
			btnPcb1.setOnClickListener(this);
			btnPcb2.setOnClickListener(this);
			btnPcb3.setOnClickListener(this);
			btnPcb4.setOnClickListener(this);
			btnPcb5.setOnClickListener(this);
			btnCPU.setOnClickListener(this);
			btnShowLv.setOnClickListener(this);
			
			if(scheduleAlg==MFQ){
				llpcRQ2.setVisibility(View.VISIBLE);
				llpcRQ3.setVisibility(View.VISIBLE);
			}
			
		}

		private void getJCB() {
			pcb1=new PCB("A", 3, 2,btnPcb1);
			pcb2=new PCB("B", 2, 4,btnPcb2);
			if(scheduleAlg==MFQ){
				pcb3=new PCB("C", 10, 5,btnPcb3);
				pcb4=new PCB("D", 20, 3,btnPcb4);
			}else{
				pcb3=new PCB("C", 4, 5,btnPcb3);
				pcb4=new PCB("D", 10, 3,btnPcb4);
			}
			pcb5=new PCB("E", 1, 1,btnPcb5);
			
		}

		@Override
		public void onClick(View v) {
			if(v.getId()==R.id.btnShowLv){
				if(lvJobInfo.getVisibility()==View.GONE){
					lvJobInfo.setVisibility(View.VISIBLE);
					btnShowLv.setText("隐藏进程信息");
				}else{
					lvJobInfo.setVisibility(View.GONE);
					btnShowLv.setText("显示进程信息");
				}
				return;
			}
			if(!isProgramRun){
				Toast.makeText(getActivity(), "程序未运行！请点击RUN", Toast.LENGTH_LONG).show();
				return;
			}
			pcb1=new PCB("A", 3, 2,btnPcb1);
			pcb2=new PCB("B", 2, 4,btnPcb2);
			if(scheduleAlg==MFQ){
				pcb3=new PCB("C", 10, 5,btnPcb3);
				pcb4=new PCB("D", 20, 3,btnPcb4);
			}else{
				pcb3=new PCB("C", 4, 5,btnPcb3);
				pcb4=new PCB("D", 10, 3,btnPcb4);
			}
			pcb5=new PCB("E", 1, 1,btnPcb5);
			switch (v.getId()) {
			
			case R.id.btnJob1:
			{
				handleBtnClick(pcb1);
				break;
			}
			case R.id.btnJob2:
			{
				handleBtnClick(pcb2);
				break;
			}
			case R.id.btnJob3:
			{
				handleBtnClick(pcb3);
				break;
			}
			case R.id.btnJob4:
			{
				handleBtnClick(pcb4);
				break;
			}
			case R.id.btnJob5:
			{
				handleBtnClick(pcb5);
				break;
			}
			
			default:
				break;
			}
		}
		

		private void handleBtnClick(PCB pcb) {
			pcb.btn.setBackgroundColor(getResources().getColor(R.color.yellow));
			pcb.btn.setClickable(false);
			pcbReadyQueue1.offer(pcb);//进入进程就绪队列
			txtProcessReadyQueue1.setText(getStringProcessReadyQueue(1));
			scheduleProcess(getActivity());
			
		}
		private static void scheduleProcess(Context context) {
			
			if(MainActivity.scheduleAlg==RR){    //轮转法
				PCB pcb=pcbReadyQueue1.peek();
				if(!cpu.isBusy()){   //判断CPU是否正忙
					pcb = pcbReadyQueue1.poll();
					CPUTask cpuTask=new CPUTask(context, pcb);
					cpuTask.execute();
				}
				
			}else if(MainActivity.scheduleAlg==MainActivity.PIORITY){//优先级轮转法
				PCB pcb=pcbReadyQueue1.peek();
				if(!cpu.isBusy()){
					int max=0;
					for(int location=1;location<pcbReadyQueue1.size();location++){
						if(pcb.getPiority()<pcbReadyQueue1.get(location).getPiority()){
							pcb=pcbReadyQueue1.get(location);
							max=location;
						}
					}
					pcb=pcbReadyQueue1.remove(max);
					CPUTask ioTask=new CPUTask(context, pcb);
					ioTask.execute();
					
				}
				
			}else if(MainActivity.scheduleAlg==MainActivity.MFQ){ //多级反馈
				if(!cpu.isBusy()){
					if(!pcbReadyQueue1.isEmpty()&&cpu.queue1continueTimes<=5){
						doIfMRQ(context,pcbReadyQueue1);
					}else if(!pcbReadyQueue2.isEmpty()&&cpu.queue2continueTimes<=5){
						cpu.queue1continueTimes=0;
						doIfMRQ(context,pcbReadyQueue2);
						
					}else if(!pcbReadyQueue3.isEmpty()){
						cpu.queue2continueTimes=0;
						doIfMRQ(context,pcbReadyQueue3);
					}else{
						doIfMRQ(context,pcbReadyQueue1);
					}
				}
				
				
			}
			
		}
		private static void doIfMRQ(Context context,LinkedList<PCB> pcbReadyQueue) {
			PCB pcb=pcbReadyQueue.peek();
			int max=0;
			for(int location=1;location<pcbReadyQueue.size();location++){
				if(pcb.getPiority()<pcbReadyQueue.get(location).getPiority()){
					pcb=pcbReadyQueue.get(location);
					max=location;
				}
			}
			pcb=pcbReadyQueue.remove(max);
			CPUTask ioTask=new CPUTask(context, pcb);
			ioTask.execute();
			
		}
		
		public class MyAdapter extends BaseAdapter{
			private PCB[] pcbs={pcb1,pcb2,pcb3,pcb4,pcb5};
			View view=null;

			@Override
			public int getCount() {
				return PCB_NUMBER+1;
			}

			@Override
			public Object getItem(int arg0) {
				return null;
			}

			@Override
			public long getItemId(int arg0) {
				return 0;
			}

			@Override
			public View getView(int position, View contentView, ViewGroup parent) {
				ViewHolder holder=null;
				if(contentView==null){
					contentView=LayoutInflater
							.from(getActivity())
							.inflate(R.layout.layout_job_info, null);
					holder=new ViewHolder();
					holder.txtJName=(TextView) contentView.findViewById(R.id.txtJobName);
					holder.txtJTime=(TextView) contentView.findViewById(R.id.txtJobTimeNeeded);
					holder.txtJMemory=(TextView) contentView.findViewById(R.id.txtJobPiority);
					contentView.setTag(holder);
				}else{
					holder=(ViewHolder) contentView.getTag();
				}
				if(position==0){    //第一行默认显示
					return contentView;
				}else{
					holder.txtJName.setText(pcbs[position-1].getName()+"");
					holder.txtJMemory.setText(pcbs[position-1].getPiority()+"");
					holder.txtJTime.setText(pcbs[position-1].getTimeNeeded()+"s");
					return contentView;
				}
				
			}
		}
		class ViewHolder{
			private TextView txtJName;
			private TextView txtJTime;
			private TextView txtJMemory;
		}
	}

	public static void alert(Context context,String string){
		Toast.makeText(context, string, Toast.LENGTH_SHORT)
		.show();
		printStrings.add(string);
	}



	public static class CPUTask extends AsyncTask<Integer, Integer, String>{
		PCB pcb;
		Context context;
		public CPUTask(Context context,PCB pcb) {
			this.context=context;
			this.pcb=pcb;
		}

		
		@Override
		protected void onPostExecute(String result) {
			onFinished();
		}

		
		@Override
		protected void onPreExecute() {
			pbCPU.setMax(pcb.getTimeNeeded()*10);
			pbRR.setMax(cpu.getRuntime()*10);
			cpu.setBusy(true);
			pcb.btn.setBackgroundColor(context.getResources().getColor(R.color.green));
			MainActivity.alert(context,"CPU正在运行进程"+pcb.getName()+"……");
			btnCPU.setText(pcb.getName());
		}

		
		@Override
		protected void onProgressUpdate(Integer... values) {
			int value=values[0];
			pbCPU.setProgress(value);
			pbRR.setProgress(value);
		}

		@Override
		protected String doInBackground(Integer... arg0) {
			Operator op=new Operator();
			int i=0;
			int runTime;
			runTime=pcb.getTimeNeeded()<=cpu.getRuntime()?pcb.getTimeNeeded():cpu.getRuntime();
			for(i=1;i<=runTime*10;i++){
				op.operate();
				publishProgress(i);
			}
			return null;
		}
		private void onFinished(){
			boolean isPcbFinished=pcb.getTimeNeeded()<=cpu.getRuntime()?true:false;
			if(isPcbFinished){//需要的时间不大于时间片
				pcb.setFinished(true);
				pcb.btn.setClickable(true);
				pcb.btn.setBackgroundColor(context.getResources().getColor(R.color.gray));
				alert(context, pcb.getRuntimes()+"次进程"
						+pcb.getName()+"完成,这次运行"
						+pcb.getTimeNeeded()+"秒!");
			}else{
				alert(context, "时间片到！进程"+
						pcb.getName()+
						"第"+pcb.getRuntimes()+"次执行"+
						cpu.getRuntime()+"秒！");
				pcb.setTimeNeeded(pcb.getTimeNeeded()-cpu.getRuntime());
				pcb.setRuntimes(pcb.getRuntimes()+1);
				pcb.btn.setBackgroundColor(context.getResources().getColor(R.color.yellow));
				if(scheduleAlg==MFQ){
					switch (pcb.curQueue) {
					case 1:
						cpu.queue1continueTimes++;
						pcbReadyQueue2.offer(pcb);
						break;
						
					case 2:
						cpu.queue1continueTimes=0;
						cpu.queue2continueTimes++;
						pcbReadyQueue3.offer(pcb);
						break;

					default:
						cpu.queue2continueTimes=0;
						pcbReadyQueue3.offer(pcb);
						break;
					}
					pcb.curQueue++;
					
				}else{
					if(scheduleAlg==PIORITY){//如果执行动态优先级算法，时间片到了优先级应该减1
						pcb.setPiority(pcb.getPiority()-1);
					}
					pcbReadyQueue1.offer(pcb);
				}
				
			}
			btnCPU.setText("CPU");
			cpu.setBusy(false);
			if(scheduleAlg==MFQ){
				txtProcessReadyQueue1.setText(getStringProcessReadyQueue(1));
				txtProcessReadyQueue2.setText(getStringProcessReadyQueue(2));
				txtProcessReadyQueue3.setText(getStringProcessReadyQueue(3));
				if(!pcbReadyQueue1.isEmpty()){
					PlaceholderFragment.scheduleProcess(context);
				}else if(!pcbReadyQueue2.isEmpty()){
					PlaceholderFragment.scheduleProcess(context);
				}else if(!pcbReadyQueue3.isEmpty()){
					PlaceholderFragment.scheduleProcess(context);
				}
			}else{
				txtProcessReadyQueue1.setText(getStringProcessReadyQueue(1));
			}
			
			
			if(!pcbReadyQueue1.isEmpty()){
				PlaceholderFragment.scheduleProcess(context);
			}
			
		}
		
		class Operator {
			public void operate(){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		
	}
	public static String getStringProcessReadyQueue(int i){
		Queue<String> qString=new LinkedList<String>();
		if(i==1){
			for(int location=0;location<pcbReadyQueue1.size();location++){
				if(scheduleAlg==RR){
					qString.offer(pcbReadyQueue1.get(location).getName()+
							"("+pcbReadyQueue1.get(location).getTimeNeeded()+")");
				}else{
					qString.offer(pcbReadyQueue1.get(location).getName()+
							"("+pcbReadyQueue1.get(location).getTimeNeeded()+
							","+pcbReadyQueue1.get(location).getPiority()+")");
				}
				
			}
		}else if(i==2){
			for(int location=0;location<pcbReadyQueue2.size();location++){
				qString.offer(pcbReadyQueue2.get(location).getName()+
						"("+pcbReadyQueue2.get(location).getTimeNeeded()+
						","+pcbReadyQueue2.get(location).getPiority()+")");
			}
		}else if(i==3){
			for(int location=0;location<pcbReadyQueue3.size();location++){
				qString.offer(pcbReadyQueue3.get(location).getName()+
						"("+pcbReadyQueue3.get(location).getTimeNeeded()+
						","+pcbReadyQueue3.get(location).getPiority()+")");
			}
		}
		
		return qString.toString();
	}
	

}
