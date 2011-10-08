package cx.ath.armox.brewclock;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

class Brewer {
	protected CountDownTimer brewCountDownTimer;
	protected int brewTime = 0;
	protected Boolean isBrewing = false;
	protected int brewCount = 0;
	protected TextView tvBrewTime;

	/**
	 * Called every second while brewing.
	 * You must override it to update a TextView or something else.
	 * 
	 * @param millisUntilFinished
	 *            Milliseconds to the end of brewing.
	 */
	protected void updBrewTime(long millisUntilFinished) {
		return;
	}
	
	public int getBrewTime() {
		return brewTime;
	}

	public Boolean getIsBrewing() {
		return isBrewing;
	}

	public int getBrewCount() {
		return brewCount;
	}

	/**
	 * Set an absolute value for the number of minutes to brew. Has no effect if
	 * a brew is currently running.
	 * 
	 * @param minutes
	 *            The number of minutes to brew.
	 */
	public Boolean setBrewTime(int minutes) {
		if (isBrewing || minutes <= 0 || minutes > 10 ) return false;
		brewTime = minutes;
		return true;
	}

	public int incBrewTime() {
		if (brewTime > 9) return brewTime; 
		return ++brewTime;
	}

	public int decBrewTime() {
		if (brewTime > 1)
			return --brewTime;
		else 
			return brewTime;
	}

	/**
	 * Called at the end of brewing; please override.
	 */
	public void theEnd () {
		return;
	}
	
	public Boolean startBrew() {
		if (isBrewing) {
			return false;
		}
		brewCountDownTimer = new CountDownTimer(brewTime * 60 * 1000, 1000) {
			
			@Override
			public void onTick(long millisUntilFinished) {
				updBrewTime(millisUntilFinished);
			}
			
			@Override
			public void onFinish() {
				brewCount++;
				isBrewing = false;
				theEnd();
			}
		}.start();
		isBrewing = true;
		return true;
	}
}

public class BrewClockActivity extends Activity 
	implements OnClickListener, OnItemSelectedListener {

	protected int defaultBrewTime; 
	protected int currBrewTime = defaultBrewTime = 3;
	protected Button brewIncreaseTime;
	protected Button brewDecreaseTime;
	protected Button startBrew;
	protected TextView brewCountLabel;
	protected TextView brewTimeLabel;
	protected Spinner spinTeas;
	protected ProgressBar pbBrewing;
	
	protected Brewer brw;

	public void onItemSelected(AdapterView<?> spinner, View view, int position,
			long id) {
		if (spinner == spinTeas) {
			Cursor sel = (Cursor) spinner.getSelectedItem();
			currBrewTime = sel.getInt(2);
			brw.setBrewTime(currBrewTime);
			brewTimeLabel.setText(String.valueOf(currBrewTime + "m"));
		}
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// do nothing
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		TeaData td = new TeaData(this);

		brewIncreaseTime = (Button) findViewById(R.id.brew_time_up);
		brewDecreaseTime = (Button) findViewById(R.id.brew_time_down);
		startBrew = (Button) findViewById(R.id.brew_start);
		brewCountLabel = (TextView) findViewById(R.id.brew_count_label);
		brewTimeLabel = (TextView) findViewById(R.id.brew_time);
		spinTeas = (Spinner) findViewById(R.id.tea_spinner);
		pbBrewing = (ProgressBar) findViewById(R.id.pbBrewing);

		brewIncreaseTime.setOnClickListener(this);
		brewDecreaseTime.setOnClickListener(this);
		startBrew.setOnClickListener(this);

		brw = new Brewer() {
			@Override
			public void updBrewTime(long millis) {
				brewTimeLabel.setText(String.valueOf(millis/1000) + "s");
				int prog = (int) (millis/10)/(currBrewTime*60);
				pbBrewing.setProgress(100-prog);
			}
			
			@Override
			public void theEnd() {
				brewCountLabel.setText(String.valueOf(brewCount));
				brewTimeLabel.setText(String.valueOf(brewTime) + "m");
				pbBrewing.setProgress(100);
				Toast.makeText(getApplication(), R.string.brew_up, Toast.LENGTH_LONG).show();
				spinTeas.setClickable(true);
			}
		};

		brw.setBrewTime(defaultBrewTime);
		brewTimeLabel.setText(String.valueOf(defaultBrewTime) + "m");
		brewCountLabel.setText("0");

		if (td.count() == 0) {
			addDefaultTeas(td);
		}

		Cursor cur = td.all(this);
		SimpleCursorAdapter sca = new SimpleCursorAdapter(
				this,
				android.R.layout.simple_spinner_item,
				cur,
				new String[] {TeaData.NAME},
				new int[] {android.R.id.text1}
			);

		sca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinTeas.setAdapter(sca);

		spinTeas.setOnItemSelectedListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_tea:
				Intent intent = new Intent(this, AddTeaActivity.class);
				try {
					startActivity(intent);
				} catch (android.content.ActivityNotFoundException e) {
					Toast.makeText(this, "A bellooo 'un sarà mejo che prima 'a fai st'acctivity?", Toast.LENGTH_SHORT).show();
				}
				return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.main, menu);
		return true;
	}

	private void addDefaultTeas(TeaData tD) {
		tD.addTea("Earl Grey", 3);
		tD.addTea("Lemon Scented", 5);
		tD.addTea("Green Tea", 7);
	}

	public void onClick(View v) {
		if (! brw.isBrewing) {
			switch (v.getId()) {
			case R.id.brew_time_down:
				currBrewTime = brw.decBrewTime();
				brewTimeLabel.setText(String.valueOf(currBrewTime) + "m");
				break;
			case R.id.brew_time_up:
				currBrewTime = brw.incBrewTime();
				brewTimeLabel.setText(String.valueOf(currBrewTime) + "m"); 
				break;
			case R.id.brew_start:
				if (brw.startBrew()) {
					pbBrewing.setProgress(0);
					spinTeas.setClickable(false);
				}
				break;
			}
		}
	}
}