package android.hardware.barcode;

import java.io.IOException;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class Scanner {
    static public final int BARCODE_READ = 10;

    static public final int BARCODE_NOREAD = 12;

    static Boolean m_bASYC = false;

    static int m_nCommand = 0;

    static public Handler m_handler = null;

    /**
     * It can scan 1D/2D barcode, If scan can't get anything, will wait for sometime, ourself scanner if wait for 10sec. MOTOROLA is wait
     * for 10sec.
     *
     * @return :return the barcode string, If can't get the barocde will show none string
     */
    static public native String ReadSCAAuto();

    /**
     * It can scan 1D/2D barcode, If scan can't get anything, will wait for sometime, ourself scanner if wait for 10sec. MOTOROLA is wait
     * for 10sec.
     *
     * @param nCode
     *            :can head mark,urself scanner id 0x55,MOTOROLA is 0x00,
     * @return :return the barcode string, If can't get the barocde will show none string
     */
    static public native String ReadSCA(int nCode);

    /**
     * It can scan 1D/2D barcode, If scan can't get anything, will wait for sometime, ourself scanner if wait for 10sec. MOTOROLA is wait
     * for 10sec.
     *
     * @param nCommand
     *            :Scan head mark��ourself scanner id 0x55,MOTOROLA is 0x00
     * @param nCode
     *            :Barcode encoding��GB2312 is 1,UTF is 0
     * @return ��return the barcode string, If can't get the barocde will show none string
     */
    static public native String ReadSCAEx(int nCommand, int nCode);

    /**
     * It can scan 1D/2D barcode, If scan can't get anything, will wait for sometime, ourself scanner if wait for 10sec. MOTOROLA is wait
     * for 10sec.
     *
     * @param nCommand
     *            :Scan head mark��ourself scanner id 0x55,MOTOROLA is 0x00
     * @param buf
     *            : store the barcode information, convert the barcode to the string after readed.
     * @return ��return the barcode string, If can't get the barocde will show none string
     */
    static public native int ReadDataSCA(int nCommand, byte[] buf);

    /**
     * It can scan 1D/2D barcode, If scan can't get anything, will wait for sometime, ourself scanner if wait for 10sec. MOTOROLA is wait
     * for 10sec.
     *
     * @param nCommand
     *            :Scan head mark��ourself scanner id 0x55,MOTOROLA is 0x00
     * @return :return the scaned barcode information
     */
    static public native byte[] ReadData(int nCommand);

    /**
     * Initializes the device
     *
     * @return ��success return 0
     */
    static public native int InitSCA();

    /**
     * Asynchronous scanning, call this function will create a Thread to scan, get the scan message or scan timeout all send message to the
     * recipient through the handle, so, must to create a handle in your accept code, and make m_handler=handle, after scan the barcode
     * success send the message BARCODE_READ, timeout send Message BARCODE_NOREAD
     */
    static public void Read() {
	if (m_bASYC) {
	    return;
	} else {
	    // m_nCommand=nCode;
	    StartASYC();
	}
    }

    /**
     * The Thread of the scan
     */
    static void StartASYC() {
	m_bASYC = true;
	final Thread thread = new Thread(new Runnable() {
	    @Override
	    public void run() {

		if (m_handler != null) {

		    final String str = ReadSCAAuto();
		    final Message msg = new Message();
		    msg.what = str.length() > 0 ? BARCODE_READ : BARCODE_NOREAD;
		    msg.obj = str;

		    m_handler.sendMessage(msg);

		}

		m_bASYC = false;
	    }
	});
	thread.start();
    }

    /**
     *
     * The virtual keyboard message,should send the String to the system according the virtual keyboard message
     *
     * @param str
     *            : the sending String
     */
    static public void SendString(final String str) {
	try {
	    Runtime.getRuntime().exec("input keyevent 66");
	    Runtime.getRuntime().exec("input text " + str);

	} catch (final IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    Log.i("run", e.toString());
	}
    }

    static private void showToast(final String str) {
	Toast.makeText(null, str, Toast.LENGTH_SHORT).show();
    }

    static {
	// System.loadLibrary("Cilico-Scan");
	System.loadLibrary("tiny-tools");
    }

}
