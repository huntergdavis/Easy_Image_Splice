package com.hunterdavis.easyimagesplice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class EasyImageSplice extends Activity {

	ArrayAdapter<String> m_adapterForLeft;
	ArrayAdapter<String> m_adapterForRight;
	ArrayAdapter<String> m_adapterForHorizontal;
	int SELECT_LEFT_PICTURE = 20;
	int SELECT_RIGHT_PICTURE = 21;
	Uri selectedImageUri1 = null;
	Uri selectedImageUri2 = null;
	Bitmap scaled1 = null;
	Bitmap scaled2 = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// image listeners
		// photo on click listener
		ImageView imageOne = (ImageView) findViewById(R.id.ImageViewOne);
		ImageView imageTwo = (ImageView) findViewById(R.id.ImageViewTwo);

		imageOne.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

			}
		});

		imageTwo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			}
		});

		// let's start with some colors
		genColor(imageOne, Color.GRAY);
		genColor(imageTwo, Color.WHITE);

		// fill up both the percentage spinners
		// set an adapter for our spinner
		m_adapterForLeft = new ArrayAdapter<String>(getBaseContext(),
				android.R.layout.simple_spinner_item);
		m_adapterForLeft
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner spinner = (Spinner) findViewById(R.id.leftpercentage);
		spinner.setAdapter(m_adapterForLeft);

		m_adapterForRight = new ArrayAdapter<String>(getBaseContext(),
				android.R.layout.simple_spinner_item);
		m_adapterForRight
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner = (Spinner) findViewById(R.id.rightpercentage);
		spinner.setAdapter(m_adapterForRight);

		for (int i = 0; i < 100; i++) {
			m_adapterForLeft.add(String.valueOf(i + 1) + "%");
			m_adapterForRight.add(String.valueOf(i + 1) + "%");

		}

		spinner.setSelection(49);
		spinner = (Spinner) findViewById(R.id.leftpercentage);
		spinner.setSelection(49);

		m_adapterForHorizontal = new ArrayAdapter<String>(getBaseContext(),
				android.R.layout.simple_spinner_item);
		m_adapterForHorizontal
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner = (Spinner) findViewById(R.id.horizontalorvertical);
		spinner.setAdapter(m_adapterForHorizontal);
		m_adapterForHorizontal.add("Horizontal");
		m_adapterForHorizontal.add("Vertical");

		// set onclick listener for left image
		// Create an anonymous implementation of OnClickListener
		OnClickListener photoButtonListner = new OnClickListener() {
			public void onClick(View v) {
				// do something when the button is clicked

				// in onCreate or any event where your want the user to
				// select a file
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(
						Intent.createChooser(intent, "Select Photo"),
						SELECT_LEFT_PICTURE);
			}

		};

		ImageView imageButton = (ImageView) findViewById(R.id.ImageViewOne);
		imageButton.setOnClickListener(photoButtonListner);

		// Create an anonymous implementation of OnClickListener
		OnClickListener phototwoButtonListner = new OnClickListener() {
			public void onClick(View v) {
				// do something when the button is clicked

				// in onCreate or any event where your want the user to
				// select a file
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(
						Intent.createChooser(intent, "Select Photo"),
						SELECT_RIGHT_PICTURE);
			}

		};

		imageButton = (ImageView) findViewById(R.id.ImageViewTwo);
		imageButton.setOnClickListener(phototwoButtonListner);

		// set our save button listener
		// Create an anonymous implementation of OnClickListener
		OnClickListener saveButtonListner = new OnClickListener() {
			public void onClick(View v) {
				// do something when the button is clicked
				if ((selectedImageUri1 != null) && (selectedImageUri2 != null)) {
					saveDoubleImage(v.getContext(), true);
				} else {
					Toast.makeText(v.getContext(), "Please Select 2 Images ",
							Toast.LENGTH_LONG).show();
				}

			}
		};

		Button saveButton = (Button) findViewById(R.id.savebutton);
		saveButton.setOnClickListener(saveButtonListner);

		// set our preview button listener
		// Create an anonymous implementation of OnClickListener
		OnClickListener previewButtonListner = new OnClickListener() {
			public void onClick(View v) {
				// do something when the button is clicked
				if ((selectedImageUri1 != null) && (selectedImageUri2 != null)) {
					saveDoubleImage(v.getContext(), false);
				} else {
					Toast.makeText(v.getContext(), "Please Select 2 Images ",
							Toast.LENGTH_LONG).show();
				} 

			}
		};

		Button previewButton = (Button) findViewById(R.id.previewbutton);
		previewButton.setOnClickListener(previewButtonListner);

		// Look up the AdView as a resource and load a request.
		AdView adView = (AdView) this.findViewById(R.id.adView);
		adView.loadAd(new AdRequest());

	}

	// catch the left and right image
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_LEFT_PICTURE) {
				selectedImageUri1 = data.getData();

				// grab a handle to the image
				ImageView imgPreView = (ImageView) findViewById(R.id.ImageViewOne);
				scaleURIAndDisplay(getBaseContext(), selectedImageUri1,
						imgPreView);

			} else if (requestCode == SELECT_RIGHT_PICTURE) {
				selectedImageUri2 = data.getData();

				// grab a handle to the image
				ImageView imgPreView = (ImageView) findViewById(R.id.ImageViewTwo);
				scaleURIAndDisplay(getBaseContext(), selectedImageUri2,
						imgPreView);

			}

		}
	}

	public Boolean genColor(ImageView imgview, int Color) {

		// create a width*height long int array and populate it with random 1 or
		// 0
		// final Random myRandom = new Random();
		int rgbSize = 100 * 100;
		int[] rgbValues = new int[rgbSize];
		for (int i = 0; i < rgbSize; i++) {
			rgbValues[i] = Color;
		}

		// create a width*height bitmap
		BitmapFactory.Options staticOptions = new BitmapFactory.Options();
		staticOptions.inSampleSize = 2;
		Bitmap staticBitmap = Bitmap.createBitmap(rgbValues, 100, 100,
				Bitmap.Config.RGB_565);

		// set the imageview to the static
		imgview.setImageBitmap(staticBitmap);

		return true;

	}

	public void scaleURIAndDisplay(Context context, Uri uri, ImageView imgview) {
		InputStream photoStream;
		try {
			photoStream = context.getContentResolver().openInputStream(uri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;
		Bitmap photoBitmap;

		photoBitmap = BitmapFactory.decodeStream(photoStream, null, options);
		imgview.setImageBitmap(photoBitmap);
		if (photoBitmap == null) {
			return;
		}

		Bitmap scaled = Bitmap.createScaledBitmap(photoBitmap, 100, 100, true);
		photoBitmap.recycle();
		imgview.setImageBitmap(scaled);
		return;
	}

	public Bitmap scaleURIAndReturnBitmap(Context context, Uri uri) {
		InputStream photoStream;
		try {
			photoStream = context.getContentResolver().openInputStream(uri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		;
		Bitmap photoBitmap;

		photoBitmap = BitmapFactory.decodeStream(photoStream, null, options);
		if (photoBitmap == null) {
			return null;
		}

		return Bitmap.createScaledBitmap(photoBitmap, 300, 400, true);

	}

	public Boolean saveDoubleImage(Context context, Boolean saveImageOut) {

		int totalWidth = 0;
		int totalHeight = 0;
		int splitval = 0;
		Boolean horizontal = true;
		Spinner spinner = (Spinner) findViewById(R.id.horizontalorvertical);
		int sel = spinner.getSelectedItemPosition();
		spinner = (Spinner) findViewById(R.id.leftpercentage);
		int leftPer = spinner.getSelectedItemPosition() + 1;
		spinner = (Spinner) findViewById(R.id.rightpercentage);
		int rightPer = spinner.getSelectedItemPosition() + 1;

		if (sel == 1) {
			horizontal = false;
		}
		if (horizontal == true) {
			// TODO if horizontal calculate left number of pixels
			int leftPixels = leftPer * 3;
			int rightPixels = rightPer * 3;
			totalWidth = (int) (leftPixels + rightPixels);
			totalHeight = 400;
			splitval = (int) leftPixels;

		} else {
			int leftPixels = leftPer * 4;
			int rightPixels = rightPer * 4;
			totalHeight = (int) (leftPixels + rightPixels);
			totalWidth = 300;
			splitval = (int) leftPixels;
		}

		if ((splitval == 0) || (totalHeight == 0) || (totalWidth == 0)) {
			return false;
		}

		// create scaled1 and 2
		scaled1 = scaleURIAndReturnBitmap(context, selectedImageUri1);
		scaled2 = scaleURIAndReturnBitmap(context, selectedImageUri2);

		int rgbSize = totalWidth * totalHeight;
		int[] rgbValues = new int[rgbSize];
		for (int i = 0; i < rgbSize; i++) {
			rgbValues[i] = calculatePixelValue(i, totalWidth, totalHeight,
					splitval, horizontal, leftPer, rightPer);
		}

		// create a width*height bitmap
		BitmapFactory.Options staticOptions = new BitmapFactory.Options();
		// staticOptions.inSampleSize = 2;
		Bitmap staticBitmap = Bitmap.createBitmap(rgbValues, totalWidth,
				totalHeight, Bitmap.Config.RGB_565);

		// Display our preview image
		ImageView imagePreView = (ImageView) findViewById(R.id.ImagePreview);
		imagePreView.setImageBitmap(staticBitmap);

		if (saveImageOut == false) {
			return true;
		}

		// now save out the file holmes!
		OutputStream outStream = null;
		String newFileName = null;
		String extStorageDirectory = Environment.getExternalStorageDirectory()
				.toString();

		String[] projection = { MediaStore.Images.ImageColumns.DISPLAY_NAME /* col1 */};
		Cursor c = context.getContentResolver().query(selectedImageUri1,
				projection, null, null, null);
		if (c != null && c.moveToFirst()) {
			String oldFileName = c.getString(0);
			int dotpos = oldFileName.lastIndexOf(".");
			if (dotpos > -1) {
				newFileName = oldFileName.substring(0, dotpos) + "-";
			}
		}

		c = context.getContentResolver().query(selectedImageUri2, projection,
				null, null, null);
		if (c != null && c.moveToFirst()) {
			String oldFileName = c.getString(0);
			int dotpos = oldFileName.lastIndexOf(".");
			if (dotpos > -1) {
				newFileName += oldFileName.substring(0, dotpos)
						+ "-spliced.png";
			}
		}

		if (newFileName != null) {
			File file = new File(extStorageDirectory, newFileName);
			try {
				outStream = new FileOutputStream(file);
				staticBitmap
						.compress(Bitmap.CompressFormat.PNG, 100, outStream);
				try {
					outStream.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
				try {
					outStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}

				Toast.makeText(context, "Saved " + newFileName,
						Toast.LENGTH_LONG).show();
				new SingleMediaScanner(context, file);

			} catch (FileNotFoundException e) {
				// do something if errors out?
				return false;
			}
		}
		return true;

	}

	public int calculatePixelValue(int location, int totalwidth,
			int totalheight, int splitval, Boolean horizontal, int leftPercent,
			int rightPercent) {

		int xLocation = (int) location % (totalwidth);
		int yLocation = (int) Math.floor(location / (totalwidth));

		if (horizontal == true) {
			// get our x and y location
			if (xLocation >= splitval) {

				// calculate our right side start pixel
				int startRightPixel = 300 - (rightPercent * 3);

				int xAdjustedStart = xLocation - splitval;
				int xAdjustedLocation = startRightPixel + xAdjustedStart;
				return scaled2.getPixel(xAdjustedLocation, yLocation);
			} else {
				return scaled1.getPixel(xLocation, yLocation);

			}
		} else {
			if (yLocation < splitval) {
				return scaled1.getPixel(xLocation, yLocation);
			} else {
				int startBottomPixel = 400 - (rightPercent * 4);
				int yStartLocation = yLocation - splitval;
				int yAdjustedLocation = startBottomPixel + yStartLocation;
				return scaled2.getPixel(xLocation, yAdjustedLocation);
			}
		}

	}

}