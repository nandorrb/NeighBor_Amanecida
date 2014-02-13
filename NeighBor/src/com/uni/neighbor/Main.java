package com.uni.neighbor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter.FilterListener;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.Toast;

import com.wenchao.jsql.JSQLite;

public class Main extends Activity {
	// TODO
	// Give preference to appear to products on offer
	// Impelement a Way to search the barcode Scanner

	ListView displayList;
	Context context;
	DBHelper dbHelper;
	String jsonData;
	SQLiteDatabase db;
	String contents;
	String feedUrl = "http://gdata.youtube.com/feeds/api/users/twistedequations/uploads?v=2&alt=jsonc&start-index=1&max-results=10";
	// String feedUrl;
	Cursor cursor;
	// test String JSON object
	String jsonString = "{ \"PRODUCTS\" :"
			+ "[{\"_id\":1,\"DEFINITION\":\"coca cola\",\"PRICE\":2,\"BRAND\":\"The cocaCola company\",\"OFFER\":0.6},{\"_id\":2,\"DEFINITION\":\"inka cola\",\"PRICE\":1,\"BRAND\":\"The cocaCola company\",\"OFFER\":0},{\"_id\":3,\"DEFINITION\":\"laptop\",\"PRICE\":500,\"BRAND\":\"Toshiba\",\"OFFER\":0.7},{\"_id\":4,\"DEFINITION\":\"robot\",\"PRICE\":800,\"BRAND\":\"Honda\",\"OFFER\":0}]"
			+ "}";
	ClientCursorAdapter adapter;
	Cursor oldCursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		savedata(jsonString);
		setContentView(R.layout.main);

		context = this;
		displayList = (ListView) findViewById(R.id.videoList);
		setAdapter();
		// To Download the DB
		// DownloadJson loaderTask = new DownloadJson();
		// loaderTask.execute();
		final EditText edt = (EditText) findViewById(R.id.SearchBar);
		Button button = (Button) findViewById(R.id.bt);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				String cc = edt.getText().toString();
				filterList(cc);
				//displayList.setAdapter(adapter);
				
			}
		});
		
		
		
	}

	private void setAdapter() {
		cursor = db.rawQuery("SELECT * FROM PRODUCTS", null); // cursor query
		adapter = new ClientCursorAdapter(this, R.layout.video_list_item,
				cursor, 0);
		// this.setListAdapter(adapter);

		displayList.setAdapter(adapter);
	}

	private void savedata(String resultData) {
		// TODO Auto-generated method stub
		// Preparing SQLite Database
		dbHelper = new DBHelper(this);
		db = dbHelper.getWritableDatabase();

		// db.execSQL("DROP TABLE IF EXISTS " + "PRODUCTS");
		// //initialize JSQLite Object

		JSQLite jsql;
		try {
			jsql = new JSQLite(resultData, db);
			// persist to database
			jsql.persist();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.scann:
			try {
				scann();
				feedUrl = contents;
			} finally {
				contents = " ";
			}
			return true;
		case R.id.BarcodeSearch:
			try {
				// scann();

			} finally {

			}
			return true;
		case R.id.action_settings:
			// showHelp();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void scann() {
		// TODO Auto-generated method stub
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		startActivityForResult(intent, 0);

	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				contents = intent.getStringExtra("SCAN_RESULT");
				feedUrl = contents;
				// Result format
				// String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				Log.i("Barcode Result", contents);

				// Handle successful scan
			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
				Log.i("Barcode Result", "Result canceled");

			}
		}
	}

	private void filterList(CharSequence constraint) {

		oldCursor = adapter.getCursor();
		adapter.setFilterQueryProvider(filterQueryProvider);
		adapter.getFilter().filter(constraint, new FilterListener() {
			@SuppressWarnings("deprecation")
			public void onFilterComplete(int count) {
				// assuming your activity manages the Cursor
				// (which is a recommended way)
				stopManagingCursor(oldCursor);
				final Cursor newCursor = adapter.getCursor();
				startManagingCursor(newCursor);
				// safely close the oldCursor
				// adapter.changeCursor(newCursor);
				if (oldCursor != null && !oldCursor.isClosed()) {
					
					oldCursor.close();
				}
				
			}
		});
	}

	private FilterQueryProvider filterQueryProvider = new FilterQueryProvider() {
		public Cursor runQuery(CharSequence constraint) {
			// assuming you have your custom DBHelper instance
			// ready to execute the DB request
			try {
				return dbHelper.getListCursor((String) constraint);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	};

	public class DownloadJson extends AsyncTask<Void, Void, Void> {

		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(context);
			dialog.setTitle("Loading Videos");
			dialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			HttpClient client = new DefaultHttpClient();
			HttpGet getRequest = new HttpGet(feedUrl);

			try {
				HttpResponse responce = client.execute(getRequest);
				StatusLine statusLine = responce.getStatusLine();
				int statusCode = statusLine.getStatusCode();

				if (statusCode != 200) {
					return null;
				}

				InputStream jsonStream = responce.getEntity().getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(jsonStream));
				StringBuilder builder = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				// jsonData is the JSON string
				jsonData = builder.toString();
				// Guarda la Información
				// JSONObject json = new JSONObject(jsonData);
				// JSONObject data = json.getJSONObject("data");
				// JSONArray items = data.getJSONArray("items");
				//
				// for (int i = 0; i < items.length(); i++) {
				// JSONObject video = items.getJSONObject(i);
				// videoArrayList.add(video.getString("title"));
				// }

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			dialog.dismiss();
			// videoAdapter.notifyDataSetChanged();
			Toast.makeText(getApplicationContext(), jsonData, Toast.LENGTH_LONG)
					.show();
			super.onPostExecute(result);
		}
	}
}
