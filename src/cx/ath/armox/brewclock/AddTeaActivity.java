package cx.ath.armox.brewclock;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class AddTeaActivity extends Activity implements OnSeekBarChangeListener {

	protected TextView tvCurrTime;
	protected EditText etTeaName;
	protected SeekBar sbTeaTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addtea);
		
		etTeaName = (EditText) findViewById(R.id.etTeaName);
		sbTeaTime = (SeekBar) findViewById(R.id.sbBrewTime);
		sbTeaTime.setOnSeekBarChangeListener(this);
		tvCurrTime = (TextView) findViewById(R.id.tvCurrTime);		
		
		tvCurrTime.setText(String.valueOf(sbTeaTime.getProgress()+1) + " m");
	}

	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (seekBar == sbTeaTime) {
			tvCurrTime.setText(String.valueOf(progress+1) + " m");
		}
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mi = new MenuInflater(this);
		mi.inflate(R.menu.addtea_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.save_tea:
			if (saveTea()) {
				Toast.makeText(this, 
						getString(R.string.save_tea_success, etTeaName.getText().toString()), 
						Toast.LENGTH_SHORT)
						.show();
			}
			break;
		}
		return true;
	}

	private boolean saveTea() {
		if (validate()) {
			TeaData td = new TeaData(this);
			td.addTea(etTeaName.getText().toString(), sbTeaTime.getProgress()+1);
			td.close();
			return true;
		}
		return false;
	}

	public boolean validate() {
		if (etTeaName.getText().length()==0) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle(R.string.invalid_tea_title);
			dialog.setMessage(R.string.invalid_tea_name);
			dialog.show();
			return false;
		}
		else
			return true;
	}
	
}
