package android.hardware.barcode;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Message;

public class ScannerHandler extends Handler {

    private final int duration = 1; // seconds

    private final int sampleRate = 2000;

    private final int numSamples = duration * sampleRate;

    private final double sample[] = new double[numSamples];

    private final double freqOfTone = 1600; // hz

    private final byte generatedSnd[] = new byte[2 * numSamples];

    private BarcodeScannerListener barcodeScannerListener;

    @Override
    public void handleMessage(final Message msg) {
	switch (msg.what) {

	    case Scanner.BARCODE_READ: {
		// show the read barcode
		final String barcode = (String) msg.obj;
		if (barcodeScannerListener != null) {
		    barcodeScannerListener.onScan(barcode);
		}
		// play sound
		play();
		break;
	    }
	    case Scanner.BARCODE_NOREAD: {

		break;
	    }

	    default:
		break;
	}
    }

    private void play() {
	final Thread thread = new Thread(new Runnable() {
	    @Override
	    public void run() {
		genTone();
		playSound();

	    }
	});
	thread.start();

    }

    void genTone() {
	// fill out the array
	for (int i = 0; i < numSamples; ++i) {
	    sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone));
	}

	// convert to 16 bit pcm sound array
	// assumes the sample buffer is normalised.
	int idx = 0;
	for (final double dVal : sample) {
	    final short val = (short) (dVal * 32767);
	    generatedSnd[idx++] = (byte) (val & 0x00ff);
	    generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
	}

    }

    void playSound() {

	final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
	        numSamples, AudioTrack.MODE_STATIC);
	audioTrack.write(generatedSnd, 0, numSamples);
	audioTrack.play();
	try {
	    Thread.sleep(30);
	} catch (final InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	audioTrack.release();

    }

    public void setBarcodeScannerListener(final BarcodeScannerListener barcodeScannerListener) {
	this.barcodeScannerListener = barcodeScannerListener;
    }

}
