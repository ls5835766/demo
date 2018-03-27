package cn.wch.ch34xuartdriver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.*;
import java.nio.ByteBuffer;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.util.Log;
import android.widget.Toast;

public class CH34xUARTDriver {

	private static final String TAG = "CH34xAndroidDriver";
	private UsbManager mUsbmanager;
	private PendingIntent mPendingIntent;
	private UsbDevice mUsbDevice;
	private UsbInterface mInterface;
	private UsbEndpoint mCtrlPoint;
	private UsbEndpoint mBulkInPoint;
	private UsbEndpoint mBulkOutPoint;
	private UsbDeviceConnection mDeviceConnection;
	private Context mContext;
	private String mString;
	private Object ReadLock=new Object();
	private Object WriteLock=new Object();
	private boolean BroadcastFlag = false;
	private boolean READ_ENABLE = false;
	private read_thread readThread;

	private byte[] readBuffer; /* circular buffer */
	private byte[] usbdata;
	private int writeIndex;
	private int readIndex;
	private int readcount;
	private int totalBytes;
	private ArrayList<String> DeviceNum = new ArrayList<String>();
	private int DeviceCount;
	private int mBulkPacketSize;
	private final int maxnumbytes = 65536 * 10;

	private int WriteTimeOutMillis;
	private int ReadTimeOutMillis;
	private int DEFAULT_TIMEOUT = 500;
	
	final int requestnum = 20;
	final int bytenum = 32;
	
	UsbRequest[] usbRequest = new UsbRequest[requestnum];
	ByteBuffer[] byteBuffer = new ByteBuffer[requestnum];
	
	private Semaphore sem = new Semaphore(1);

	public CH34xUARTDriver(UsbManager manager, Context context,
			String AppName) {
		super();
		readBuffer = new byte[maxnumbytes];
		usbdata = new byte[8092];
		writeIndex = 0;
		readIndex = 0;
		totalBytes = 0;

		mUsbmanager = manager;
		mContext = context;
		mString = AppName;
		WriteTimeOutMillis = 10000;
		ReadTimeOutMillis = 10000;

		ArrayAddDevice("1a86:7523");
		ArrayAddDevice("1a86:5523");
		ArrayAddDevice("1a86:5512");
	}

	private void ArrayAddDevice(String str) {
		DeviceNum.add(str);
		DeviceCount = DeviceNum.size();
	}

	/**
	 * 设置USB的读写超时
	 * 
	 * @param WriteTimeOut
	 *            写超时,单位Ms
	 * @param ReadTimeOut
	 *            读超时,单位Ms
	 * @return true: 设置成功
	 */
	public boolean SetTimeOut(int WriteTimeOut, int ReadTimeOut) {
		WriteTimeOutMillis = WriteTimeOut;
		ReadTimeOutMillis = ReadTimeOut;
		return true;
	}

	private void OpenUsbDevice(UsbDevice mDevice) {
		Object localObject;
		UsbInterface intf;
		if (mDevice == null)
			return;
		intf = getUsbInterface(mDevice);
		if ((mDevice != null) && (intf != null)) {
			localObject = this.mUsbmanager.openDevice(mDevice);
			if (localObject != null) {
				if (((UsbDeviceConnection) localObject).claimInterface(intf,
						true)) {
					this.mUsbDevice = mDevice;
					this.mDeviceConnection = ((UsbDeviceConnection) localObject);
					this.mInterface = intf;
					if (!enumerateEndPoint(intf))
						return;
					Toast.makeText(mContext, "Device Has Attached to Android",
							Toast.LENGTH_LONG).show();
					if (READ_ENABLE == false) {
						READ_ENABLE = true;
						readThread = new read_thread(mBulkInPoint,
								mDeviceConnection);
						readThread.start();
					}
					return;
				}
			}
		}
	}

	/**
	 * 打开CH34x设备
	 * 
	 * @param mDevice
	 *            需要打开的CH34x设备
	 */
	public void OpenDevice(UsbDevice mDevice) {
		mPendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(
				mString), 0);
		if (mUsbmanager.hasPermission(mDevice)) {
			OpenUsbDevice(mDevice);
		} else {
			synchronized (mUsbReceiver) {
				mUsbmanager.requestPermission(mDevice, mPendingIntent);
			}
		}
	}
	
	/**
	 * 关闭设备
	 */
	public void CloseDevice() {

		if (this.mDeviceConnection != null) {
			if (this.mInterface != null) {
				this.mDeviceConnection.releaseInterface(this.mInterface);
				this.mInterface = null;
			}

			this.mDeviceConnection.close();
		}

		if (this.mUsbDevice != null) {
			this.mUsbDevice = null;
		}

		if (this.mUsbmanager != null) {
			this.mUsbmanager = null;
		}

		if (READ_ENABLE == true) {
			READ_ENABLE = false;
		}

		if (BroadcastFlag == true) {
			this.mContext.unregisterReceiver(mUsbReceiver);
			BroadcastFlag = false;
		}

	}

	/**
	 * 系统是否支持USB Host功能
	 * 
	 * @return true:系统支持USB Host false:系统不支持USB Host
	 */
	public boolean UsbFeatureSupported() {
		boolean bool = this.mContext.getPackageManager().hasSystemFeature(
				"android.hardware.usb.host");
		return bool;
	}

	/**
	 * 枚举并打开CH34x设备
	 * 
	 * @return true:打开成功 false:打开失败
	 */
	public int ResumeUsbList() {
		
		mUsbmanager = (UsbManager) mContext
				.getSystemService(Context.USB_SERVICE);
		mPendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(
				mString), 0);
		HashMap<String, UsbDevice> deviceList = mUsbmanager.getDeviceList();
		if (deviceList.isEmpty()) {
			Toast.makeText(mContext, "No Device Or Device Not Match",
					Toast.LENGTH_LONG).show();
			return -1;
		}
		Iterator<UsbDevice> localIterator = deviceList.values().iterator();
		while (localIterator.hasNext()) {
			UsbDevice	localUsbDevice = localIterator.next();
			for (int i = 0; i < DeviceCount; ++i) {
				if (String
						.format("%04x:%04x",
								new Object[] {
										Integer.valueOf(localUsbDevice
												.getVendorId()),
										Integer.valueOf(localUsbDevice
												.getProductId()) }).equals(
								DeviceNum.get(i))) {
					IntentFilter filter = new IntentFilter(mString);
					filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
					mContext.registerReceiver(mUsbReceiver, filter);
					BroadcastFlag = true;
					if (mUsbmanager.hasPermission(localUsbDevice)) {
						OpenUsbDevice(localUsbDevice);
					} else {
						/*
						Toast.makeText(mContext, "No Perssion!",
								Toast.LENGTH_LONG).show();
						synchronized (mUsbReceiver) {
							mUsbmanager.requestPermission(localUsbDevice,
									mPendingIntent);
						}
						*/
						return -2;
					}
					return 0;
				} else {
					Log.d(TAG, "String.format not match");
				}
			}
		}
		return -1;
	}

	/**
	 * 枚举并打开CH34x设备
	 * 
	 * @return true:打开成功 false:打开失败
	 */
	public int ResumeUsbPermission() {
		
		mUsbmanager = (UsbManager) mContext
				.getSystemService(Context.USB_SERVICE);
		mPendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(
				mString), 0);
		HashMap<String, UsbDevice> deviceList = mUsbmanager.getDeviceList();
		if (deviceList.isEmpty()) {
			Toast.makeText(mContext, "No Device Or Device Not Match",
					Toast.LENGTH_LONG).show();
			return -1;
		}
		Iterator<UsbDevice> localIterator = deviceList.values().iterator();
		while (localIterator.hasNext()) {
			UsbDevice	localUsbDevice = localIterator.next();
			for (int i = 0; i < DeviceCount; ++i) {
				if (String
						.format("%04x:%04x",
								new Object[] {
										Integer.valueOf(localUsbDevice
												.getVendorId()),
										Integer.valueOf(localUsbDevice
												.getProductId()) }).equals(
								DeviceNum.get(i))) {
					BroadcastFlag = true;
					if (!mUsbmanager.hasPermission(localUsbDevice)) {
						Toast.makeText(mContext, "No Perssion!",
								Toast.LENGTH_LONG).show();
						synchronized (mUsbReceiver) {
							mUsbmanager.requestPermission(localUsbDevice,
									mPendingIntent);
						}
						return -2;
					}
					return 0;
				} else {
					Log.d(TAG, "String.format not match");
				}
			}
		}
		return -1;
	}
	
	/**
	 * 枚举CH34x设备
	 * 
	 * @return 返回枚举到的CH34x设备,如果没有则返回null
	 */
	public UsbDevice EnumerateDevice() {
		mUsbmanager = (UsbManager) mContext
				.getSystemService(Context.USB_SERVICE);
		mPendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(
				mString), 0);
		HashMap<String, UsbDevice> deviceList = mUsbmanager.getDeviceList();
		if (deviceList.isEmpty()) {
			Toast.makeText(mContext, "No Device Or Device Not Match",
					Toast.LENGTH_LONG).show();
			return null;
		}
		Iterator<UsbDevice> localIterator = deviceList.values().iterator();
		while (localIterator.hasNext()) {
			UsbDevice localUsbDevice = localIterator.next();
			for (int i = 0; i < DeviceCount; ++i) {
				if (String
						.format("%04x:%04x",
								new Object[] {
										Integer.valueOf(localUsbDevice
												.getVendorId()),
										Integer.valueOf(localUsbDevice
												.getProductId()) }).equals(
								DeviceNum.get(i))) {
					IntentFilter filter = new IntentFilter(mString);
					filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
					mContext.registerReceiver(mUsbReceiver, filter);
					BroadcastFlag = true;
					return localUsbDevice;

				} else {
					Log.d(TAG, "String.format not match");
				}
			}
		}
		return null;
	}
	
	/**
	 * 返回设备是否连接
	 * 
	 * @return true:设备连接 false:设备未连接
	 */
	public boolean isConnected() {
		return (this.mUsbDevice != null) && (this.mInterface != null)
				&& (this.mDeviceConnection != null);
	}

	/**
	 * 获得连接的设备
	 * 
	 * @return 返回连接的设备
	 */
	protected UsbDevice getUsbDevice() {
		return this.mUsbDevice;
	}
	
	private int Uart_Control_Out_Std(int request, int value, int index) {
		int retval = 0;
		retval = mDeviceConnection.controlTransfer(0x00
				| UsbType.USB_RECIP_DEVICE | UsbType.USB_DIR_OUT, request,
				value, index, null, 0, DEFAULT_TIMEOUT);
		return retval;
	}
	
	private int Uart_Control_Out(int request, int value, int index) {
		int retval = 0;
		retval = mDeviceConnection.controlTransfer(UsbType.USB_TYPE_VENDOR
				| UsbType.USB_RECIP_DEVICE | UsbType.USB_DIR_OUT, request,
				value, index, null, 0, DEFAULT_TIMEOUT);
		return retval;
	}

	private int Uart_Control_In(int request, int value, int index,
			byte[] buffer, int length) {
		int retval = 0;
		retval = mDeviceConnection.controlTransfer(UsbType.USB_TYPE_VENDOR
				| UsbType.USB_RECIP_DEVICE | UsbType.USB_DIR_IN, request,
				value, index, buffer, length, DEFAULT_TIMEOUT);
		return retval;
	}

	private int Uart_Set_Handshake(int control) {
		return Uart_Control_Out(UartCmd.VENDOR_MODEM_OUT, ~control, 0);
	}

	private int Uart_Tiocmset(int set, int clear) {
		int control = 0;
		if ((set & UartModem.TIOCM_RTS) == UartModem.TIOCM_RTS)
			control |= UartIoBits.UART_BIT_RTS;
		if ((set & UartModem.TIOCM_DTR) == UartModem.TIOCM_DTR)
			control |= UartIoBits.UART_BIT_DTR;
		if ((clear & UartModem.TIOCM_RTS) == UartModem.TIOCM_RTS)
			control &= ~UartIoBits.UART_BIT_RTS;
		if ((clear & UartModem.TIOCM_DTR) == UartModem.TIOCM_DTR)
			control &= ~UartIoBits.UART_BIT_DTR;

		return Uart_Set_Handshake(control);
	}

	/**
	 * 初始化串口
	 * 
	 * @return true:串口初始化成功 false:串口初始化失败
	 */
	public boolean UartInit() {
		int ret;
		int size = 8;
		byte[] buffer = new byte[size];
//		Uart_Control_Out_Std(0x05, 0x000a, 0x0000);
//		Uart_Control_Out_Std(0x09, 0x0001, 0x0000);
		Uart_Control_Out(UartCmd.VENDOR_SERIAL_INIT, 0x0000, 0x0000);
		ret = Uart_Control_In(UartCmd.VENDOR_VERSION, 0x0000, 0x0000, buffer, 2);
		if (ret < 0)
			return false;
		Uart_Control_Out(UartCmd.VENDOR_WRITE, 0x1312, 0xD982);
		Uart_Control_Out(UartCmd.VENDOR_WRITE, 0x0f2c, 0x0004);
		ret = Uart_Control_In(UartCmd.VENDOR_READ, 0x2518, 0x0000, buffer, 2);
		if (ret < 0)
			return false;
		Uart_Control_Out(UartCmd.VENDOR_WRITE, 0x2727, 0x0000);
		Uart_Control_Out(UartCmd.VENDOR_MODEM_OUT, 0x00ff, 0x0000);
	
		return true;
	}

	/**
	 * 串口参数设置
	 * 
	 * @param baudRate
	 *            波特率：300，600，1200、2400、4800、9600、19200、38400、57600、115200、
	 *            230400、460800、921600，默认：9600
	 * @param dataBit
	 *            5个数据位、6个数据位、7个数据位、8个数据位，默认：8个数据位。
	 * @param stopBit
	 *            0：1个停止位，1：2个停止位，默认：1个停止位。
	 * @param parity
	 *            0：none，1：add，2：even，3：mark和4：space，默认：none。
	 * @param flowControl
	 *            0：none，1：CTS/RTS，默认：none
	 * @return true:设置成功 false:设置失败
	 */
	public boolean SetConfig(int baudRate, byte dataBit,
			byte stopBit, byte parity, byte flowControl) {
		int value = 0;
		int index = 0;
		char valueHigh = 0, valueLow = 0, indexHigh = 0, indexLow = 0;
		switch (parity) {
		case 0: /* NONE */
			valueHigh = 0x00;
			break;
		case 1: /* ODD */
			valueHigh |= 0x08;
			break;
		case 2: /* Even */
			valueHigh |= 0x18;
			break;
		case 3: /* Mark */
			valueHigh |= 0x28;
			break;
		case 4: /* Space */
			valueHigh |= 0x38;
			break;
		default: /* None */
			valueHigh = 0x00;
			break;
		}

		if (stopBit == 2) {
			valueHigh |= 0x04;
		}

		switch (dataBit) {
		case 5:
			valueHigh |= 0x00;
			break;
		case 6:
			valueHigh |= 0x01;
			break;
		case 7:
			valueHigh |= 0x02;
			break;
		case 8:
			valueHigh |= 0x03;
			break;
		default:
			valueHigh |= 0x03;
			break;
		}

		valueHigh |= 0xc0;
		valueLow = 0x9c;

		value |= valueLow;
		value |= (int) (valueHigh << 8);

		switch (baudRate) {
		case 50:
			indexLow = 0;
			indexHigh = 0x16;
			break;
		case 75:
			indexLow = 0;
			indexHigh = 0x64;
			break;
		case 110:
			indexLow = 0;
			indexHigh = 0x96;
			break;
		case 135:
			indexLow = 0;
			indexHigh = 0xa9;
			break;
		case 150:
			indexLow = 0;
			indexHigh = 0xb2;
			break;
		case 300:
			indexLow = 0;
			indexHigh = 0xd9;
			break;
		case 600:
			indexLow = 1;
			indexHigh = 0x64;
			break;
		case 1200:
			indexLow = 1;
			indexHigh = 0xb2;
			break;
		case 1800:
			indexLow = 1;
			indexHigh = 0xcc;
			break;
		case 2400:
			indexLow = 1;
			indexHigh = 0xd9;
			break;
		case 4800:
			indexLow = 2;
			indexHigh = 0x64;
			break;
		case 9600:
			indexLow = 2;
			indexHigh = 0xb2;
			break;
		case 19200:
			indexLow = 2;
			indexHigh = 0xd9;
			break;
		case 38400:
			indexLow = 3;
			indexHigh = 0x64;
			break;
		case 57600:
			indexLow = 3;
			indexHigh = 0x98;
			break;
		case 115200:
			indexLow = 3;
			indexHigh = 0xcc;
			break;
		case 230400:
			indexLow = 3;
			indexHigh = 0xe6;
			break;
		case 460800:
			indexLow = 3;
			indexHigh = 0xf3;
			break;
		case 500000:
			indexLow = 3;
			indexHigh = 0xf4;
			break;
		case 921600:
			indexLow = 7;
			indexHigh = 0xf3;
			break;
		case 1000000:
			indexLow = 3;
			indexHigh = 0xfa;
			break;
		case 2000000:
			indexLow = 3;
			indexHigh = 0xfd;
			break;
		case 3000000:
			indexLow = 3;
			indexHigh = 0xfe;
			break;
		default: // default baudRate "9600"
			indexLow = 2;
			indexHigh = 0xb2;
			break;
		}

		index |= 0x88 | indexLow;
		index |= (int) (indexHigh << 8);	

		int retval = Uart_Control_Out(UartCmd.VENDOR_SERIAL_INIT, value, index);
		if (flowControl == 1) {
			Uart_Tiocmset(UartModem.TIOCM_DTR | UartModem.TIOCM_RTS, 0x00);
		}
		if (retval >= 0)
			return true;
		else
			return false;
	}

	/**
	 * 串口读数据
	 * 
	 * @param data
	 *            接收的byte[]数组缓存区
	 * @param length
	 *            需要接收的数据长度
	 * @return 返回实际读取到的数据长度
	 */
	public int ReadData(byte[] data, int length) {
		synchronized (ReadLock) {
			int mLen = 0;

			try {
		        sem.acquire();
		    } catch (InterruptedException e) {
		        e.printStackTrace();
		    }
			
			/* should be at least one byte to read */
			if ((length < 1) || (totalBytes == 0)) {
				sem.release();
				return mLen;
			}
/*
			if(length>256)
				length=256;
*/
			/* check for max limit */
			if (length > totalBytes)
				length = totalBytes;

			/* update the number of bytes available */
			totalBytes -= length;

			mLen = length;

			/* copy to the user buffer */
			for (int count = 0; count < length; count++) {
				data[count] = readBuffer[readIndex];
				readIndex++;
				/*
				 * shouldn't read more than what is there in the buffer, so no need
				 * to check the overflow
				 */
				readIndex %= maxnumbytes;
			}
			sem.release();
			
			return mLen;
		}
	}

	/**
	 * 串口写数据
	 * 
	 * @param buf
	 *            需要写入的数据缓冲区
	 * @param length
	 *            需要写的数据字节长度
	 * @return 实际写入的数据长度
	 */
	public int WriteData(byte[] buf, int length) {
		int mLen = 0;
		mLen = WriteData(buf, length, this.WriteTimeOutMillis);
		return mLen;
	}

	/**
	 * 串口写数据
	 * 
	 * @param buf
	 *            需要写入的数据缓冲区
	 * @param length
	 *            需要写入的数据字节长度
	 * @param timeoutMillis
	 *            USB写的超时时间,单位为Ms
	 * @return 返回实际写入的数据长度
	 */
	public int WriteData(byte[] buf, int length, int timeoutMillis) {
		synchronized (WriteLock) {
			int offset = 0;
			int HasWritten = 0;
			int odd_len = length;
			if (this.mBulkOutPoint == null)
				return -1;
			while (offset < length) {
				int mLen = Math.min(odd_len, this.mBulkPacketSize);
				byte[] arrayOfByte = new byte[mLen];
				if (offset == 0) {
					System.arraycopy(buf, 0, arrayOfByte, 0, mLen);
				} else {
					System.arraycopy(buf, offset, arrayOfByte, 0, mLen);
				}
				HasWritten = this.mDeviceConnection.bulkTransfer(
						this.mBulkOutPoint, arrayOfByte, mLen, timeoutMillis);
				if (HasWritten < 0) {
					return -2;
				} else {
					offset += HasWritten;
					odd_len -= HasWritten;
				}
			}
			return offset;
		}
	}

	private boolean enumerateEndPoint(UsbInterface sInterface) {
		if (sInterface == null)
			return false;
		for (int i = 0; i < sInterface.getEndpointCount(); ++i) {
			UsbEndpoint endPoint = sInterface.getEndpoint(i);
			if (endPoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK
					&& endPoint.getMaxPacketSize() == 0x20) {
				if (endPoint.getDirection() == UsbConstants.USB_DIR_IN) {
					mBulkInPoint = endPoint;
				} else {
					mBulkOutPoint = endPoint;
				}
				this.mBulkPacketSize = endPoint.getMaxPacketSize();
			} else if (endPoint.getType() == UsbConstants.USB_ENDPOINT_XFER_CONTROL) {
				mCtrlPoint = endPoint;
			}
		}
		return true;
	}

	private UsbInterface getUsbInterface(UsbDevice paramUsbDevice) {
		if (this.mDeviceConnection != null) {
			if (this.mInterface != null) {
				this.mDeviceConnection.releaseInterface(this.mInterface);
				this.mInterface = null;
			}
			this.mDeviceConnection.close();
			this.mUsbDevice = null;
			this.mInterface = null;
		}
		if (paramUsbDevice == null)
			return null;

		for (int i = 0; i < paramUsbDevice.getInterfaceCount(); i++) {
			UsbInterface intf = paramUsbDevice.getInterface(i);
			if (intf.getInterfaceClass() == 0xff
					&& intf.getInterfaceSubclass() == 0x01
					&& intf.getInterfaceProtocol() == 0x02) {
				return intf;
			}
		}
		return null;
	}

	/*********** USB broadcast receiver *******************************************/
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
				Log.e(TAG, "Step1!\n");
				return;
			}

			if (mString.equals(action)) {
				Log.e(TAG, "Step2!\n");
				synchronized (CH34xUARTDriver.class) {
					UsbDevice localUsbDevice = (UsbDevice) intent
							.getParcelableExtra("device");
					if (intent.getBooleanExtra(
							UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						OpenUsbDevice(localUsbDevice);
					} else {
						Toast.makeText(CH34xUARTDriver.this.mContext,
								"Deny USB Permission", Toast.LENGTH_SHORT)
								.show();
						Log.d(TAG, "permission denied");
					}
				}
			} else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
				Log.e(TAG, "Step3!\n");

				UsbDevice localUsbDevice = (UsbDevice) intent
						.getParcelableExtra("device");
				
				String name = localUsbDevice.getDeviceName();
				
				Log.e(TAG, name);
				
				for (int i = 0; i < DeviceCount; ++i) {
					if (String
							.format("%04x:%04x",
									new Object[] {
											Integer.valueOf(localUsbDevice
													.getVendorId()),
											Integer.valueOf(localUsbDevice
													.getProductId()) }).equals(
									DeviceNum.get(i))) {
						Toast.makeText(CH34xUARTDriver.this.mContext, "Disconnect",
								Toast.LENGTH_SHORT).show();
						CloseDevice();
						System.exit(0);
					}
				}

			} else {
				Log.e(TAG, "......");
			}
		}
	};

	/* usb input data handler */
	private class read_thread extends Thread {

		UsbEndpoint endpoint;
		UsbDeviceConnection mConn;

		read_thread(UsbEndpoint point, UsbDeviceConnection con) {
			endpoint = point;
			mConn = con;
			for (int i = 0; i < requestnum; i++) {
				usbRequest[i] = new UsbRequest();
				usbRequest[i].initialize(mConn, endpoint); 
				byteBuffer[i] = ByteBuffer.allocate(bytenum);
			}
			this.setPriority(Thread.MAX_PRIORITY);
		}

		public void run() {
			
			for (int i = 0; i < requestnum; i++)
				usbRequest[i].queue(byteBuffer[i], bytenum);
			
			while (READ_ENABLE == true) {
				while (totalBytes > (maxnumbytes - 255)) {
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				if (endpoint != null) {
						
//					long startTime = System.nanoTime(); 
				
//					for (int i = 0; i < requestnum; i++)
//						usbRequest[i].queue(byteBuffer[i], bytenum);
					
					for (int i = 0; i < requestnum; i++) {
						if (mConn.requestWait() == usbRequest[i]) {
						    usbdata = byteBuffer[i].array();
						    readcount = byteBuffer[i].position();
						    
						    if (readcount > 0) {
								try {
							        sem.acquire();
							    } catch (InterruptedException e) {
							        e.printStackTrace();
							    }
								totalBytes += readcount;
								for (int count = 0; count < readcount; count++) {
									readBuffer[writeIndex] = usbdata[count];
									writeIndex++;
									writeIndex %= maxnumbytes;
								}
								if (writeIndex >= readIndex)
									totalBytes = writeIndex - readIndex;
								else
									totalBytes = (maxnumbytes - readIndex)
											+ writeIndex;
								sem.release();
							}
		 					else if (readcount < 0)
		 						Log.e(TAG, "read error " + readcount);
						    
						    usbRequest[i].queue(byteBuffer[i], bytenum);
						}
					}
/*			
					for (int i = 0; i < requestnum; i++) {
						if (mConn.requestWait() == usbRequest[i]) {
						    usbdata = byteBuffer[i].array();
						    readcount = byteBuffer[i].position();
						}
					}		
				
//					readcount = mConn.bulkTransfer(endpoint, usbdata, 32, 
//							ReadTimeOutMillis);
					
 					if (readcount > 0) {
	 						
						try {
					        sem.acquire();
					    } catch (InterruptedException e) {
					        e.printStackTrace();
					    }
						
						totalBytes += readcount;
						
						for (int count = 0; count < readcount; count++) {
							readBuffer[writeIndex] = usbdata[count];
							writeIndex++;
							writeIndex %= maxnumbytes;
						}
						
						if (writeIndex >= readIndex)
							totalBytes = writeIndex - readIndex;
						else
							totalBytes = (maxnumbytes - readIndex)
									+ writeIndex;
						
						sem.release();

					}
 					else if (readcount < 0)
 						Log.e(TAG, "read error " + readcount);
	
 					long consumingTime = System.nanoTime() - startTime; //消耗時間
 					Log.e("consuming", "comsumed time: " + consumingTime
 							+ "ns. readcount: " + readcount);
*/				
				}
			}
		}
	}

	private final class UartModem {
		public static final int TIOCM_LE = 0x001;
		public static final int TIOCM_DTR = 0x002;
		public static final int TIOCM_RTS = 0x004;
		public static final int TIOCM_ST = 0x008;
		public static final int TIOCM_SR = 0x010;
		public static final int TIOCM_CTS = 0x020;
		public static final int TIOCM_CAR = 0x040;
		public static final int TIOCM_RNG = 0x080;
		public static final int TIOCM_DSR = 0x100;
		public static final int TIOCM_CD = TIOCM_CAR;
		public static final int TIOCM_RI = TIOCM_RNG;
		public static final int TIOCM_OUT1 = 0x2000;
		public static final int TIOCM_OUT2 = 0x4000;
		public static final int TIOCM_LOOP = 0x8000;
	}

	private final class UsbType {
		public static final int USB_TYPE_VENDOR = (0x02 << 5);
		public static final int USB_RECIP_DEVICE = 0x00;
		public static final int USB_DIR_OUT = 0x00; /* to device */
		public static final int USB_DIR_IN = 0x80; /* to host */
	}

	private final class UartCmd {
		public static final int VENDOR_WRITE_TYPE = 0x40;
		public static final int VENDOR_READ_TYPE = 0xC0;
		public static final int VENDOR_READ = 0x95;
		public static final int VENDOR_WRITE = 0x9A;
		public static final int VENDOR_SERIAL_INIT = 0xA1;
		public static final int VENDOR_MODEM_OUT = 0xA4;
		public static final int VENDOR_VERSION = 0x5F;
	}

	private final class UartState {
		public static final int UART_STATE = 0x00;
		public static final int UART_OVERRUN_ERROR = 0x01;
		public static final int UART_PARITY_ERROR = 0x02;
		public static final int UART_FRAME_ERROR = 0x06;
		public static final int UART_RECV_ERROR = 0x02;
		public static final int UART_STATE_TRANSIENT_MASK = 0x07;
	}

	private final class UartIoBits {
		public static final int UART_BIT_RTS = (1 << 6);
		public static final int UART_BIT_DTR = (1 << 5);
	}
}
