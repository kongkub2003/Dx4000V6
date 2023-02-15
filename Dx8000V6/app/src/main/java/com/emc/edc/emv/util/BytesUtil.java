package com.emc.edc.emv.util;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Byte Array tool
 * @author chenwei
 *
 */
public class BytesUtil {
	private static final String EMPTY_STRING = "";
	private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

	/** Character set constant definition */
	private static final String CHARSET_ISO8859_1 = "ISO-8859-1";
	private static final String CHARSET_GBK = "GBK";
	private static final String CHARSET_GB2312 = "GB2312";
	private static final String CHARSET_UTF8 = "UTF-8";
	
	private BytesUtil() {}

	/**
	 * asc to  int
	 * @param sum For example: 0x33 to 3
	 * @return
	 */
	public static int ascToInt(byte sum) {
		return Integer.valueOf((char)sum + "");
	}

	/**
	 * Binary data to hexadecimal representation of the string
	 * @param data
	 * @return Conversion results
	 */
	public static String bytes2HexString(byte[] data) {
		if (isNullEmpty(data)) {
			return EMPTY_STRING;
		}
		StringBuilder buffer = new StringBuilder();
		for (byte b : data) {
			String hex = Integer.toHexString(b & 0xff);
			if (hex.length() == 1) {
				buffer.append('0');
			}
			buffer.append(hex);
		}
		return buffer.toString().toUpperCase();
	}

	/**
	 * Converts the ascii byte array into a displayable string
	 * @param asciiData
	 * @return Conversion results
	 */
	public static String toString(byte[] asciiData) {
		if (isNullEmpty(asciiData)) {
			return EMPTY_STRING;
		}
		try {
			int length = 0;
			for (int i = 0; i < asciiData.length; ++i) {
				if (asciiData[i] == 0) {
					length = i;
					break;
				}
			}
			return new String(asciiData, 0, length, CHARSET_GB2312);
		} catch (Exception e) {
			return EMPTY_STRING;
		}
	}

	/**
	 * A hexadecimal representation of string-to-binary data
	 * @param data
	 * @return Conversion results
	 */
	public static byte[] hexString2Bytes(String data) {
		if (isNullEmpty(data)) {
			return EMPTY_BYTE_ARRAY;
		}

		byte[] result = new byte[(data.length() + 1) / 2];
		if ((data.length() & 1) == 1) {
			data += "0";
		}
		for (int i = 0; i < result.length; i++) {
			result[i] = (byte) (hex2byte(data.charAt(i * 2 + 1)) | (hex2byte(data.charAt(i * 2)) << 4));
		}
		return result;
    }
	
	/**
	 * Hexadecimal characters are converted to binary
	 * @param hex
	 * @return Conversion results
	 */
	public static byte hex2byte(char hex){
		if(hex <= 'f' && hex >= 'a') {
			return (byte) (hex - 'a' + 10);
		}
		
		if(hex <= 'F' && hex >= 'A') {
			return (byte) (hex - 'A' + 10);
		}
		
		if(hex <= '9' && hex >= '0') {
			return (byte) (hex - '0');
		}
		
		return 0;
	}

	/**
	 * Get the subarray
	 * @param data		Data
	 * @param offset	Offset position. 0~data.length
	 * @param len		length. A negative number represents the maximum length of the normal range.
	 * @return	byte[]
	 */
	public static byte[] subBytes(byte[] data, int offset, int len){
		if (isNullEmpty(data)) {
			return null;
		}

		if(offset < 0 || data.length <= offset) {
			return null;
		}
		
		if(len < 0 || data.length < offset + len) {
			len = data.length - offset;
		}
		
		byte[] ret = new byte[len];
		
		System.arraycopy(data, offset, ret, 0, len);
		return ret;
	}
	
	/**
	 * Combine multiple pieces of data
	 * @param data	An array of data, which can be passed on any of them
	 * @return		Merged data
	 */
	public static byte[] merage(byte[]... data) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			for (byte[] d : data) {
				if (d == null) {
					throw new IllegalArgumentException("");
				}
				buffer.write(d);
			}
			return buffer.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				buffer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	/**
	 * Converts a byte array to an integer in big-endian mode
	 * @param bytes	
	 * @return	Conversion results
	 */
	public static int bytesToInt(byte[] bytes) {
		if (isNullEmpty(bytes)) {
			return 0;
		}

		if(bytes.length > 4) {
			return -1;
		}
		
		int lastIndex = bytes.length - 1;
		int result = 0;
		for(int i=0; i<bytes.length; i++) {
			result |= (bytes[i] & 0xFF) << ((lastIndex-i)<<3);
		}
		
		return result;
	}
	
	/**
	 * Converts a byte array to an integer in the small-endian pattern
	 * @param bytes	
	 * @return	Conversion results
	 */
	public static int littleEndianBytesToInt(byte[] bytes) {
		if (isNullEmpty(bytes)) {
			return 0;
		}

		if(bytes.length > 4) {
			return -1;
		}
		
		int result = 0;
		for(int i=0; i<bytes.length; i++) {
			result |= (bytes[i] & 0xFF) << (i<<3);
		}
		
		return result;
	}
	
	/**
	 * Converts integers to 4-byte arrays in little-endian mode
	 * @param intValue
	 * @return	Conversion results
	 */
	public static byte[] intToBytesByLow(int intValue) {
		byte[] bytes = new byte[4];
		for(int i=0; i<bytes.length; i++) {
			bytes[i] = (byte) ((intValue >> ((3-i)<<3)) & 0xFF);
		}
		return bytes;
	}

	/**
	 * Converts integers to a 4-byte array sorted by big end
	 * Big endian: Low addresses hold highly significant bytes
	 * For example：0x12345678 corresponds to byte[]
	 *        		   Low address bit     High address bit
	 * Big-endian：    12  34        	   56   78
	 * Little-endian： 78  56        	   34   12
	 */
	public static byte[] intToBytesByHigh(int value) {
		byte[] src = new byte[4];
		src[3] =  (byte) ((value>>24) & 0xFF);
		src[2] =  (byte) ((value>>16) & 0xFF);
		src[1] =  (byte) ((value>>8) & 0xFF);
		src[0] =  (byte) (value & 0xFF);
		return src;
	}

	/**
	 * BCD is converted to ASCII
	 * @param bcd BCD byte array
	 * @return ASCII string
	 */
	public static String bcd2Ascii(final byte[] bcd) {
		if (isNullEmpty(bcd)) {
			return EMPTY_STRING;
		}

		StringBuilder sb = new StringBuilder(bcd.length << 1);
		for (byte ch : bcd) {
			byte half = (byte) (ch >> 4);
			sb.append((char) (half + ((half > 9) ? ('A' - 10) : '0')));
			half = (byte) (ch & 0x0f);
			sb.append((char) (half + ((half > 9) ? ('A' - 10) : '0')));
		}
		return sb.toString();
	}

	/**
	 * ASCII strings are compressed into BCD format
	 * @param ascii ASCII string
	 * @return A compressed BCD byte array
	 */
	public static byte[] ascii2Bcd(String ascii) {
		if (isNullEmpty(ascii)) {
			return EMPTY_BYTE_ARRAY;
		}

		if ((ascii.length() & 0x01) == 1) {
			ascii = "0" + ascii;
		}
		byte[] asc = ascii.getBytes();
		byte[] bcd = new byte[ascii.length() >> 1];
		for (int i = 0; i < bcd.length; i++) {
			bcd[i] = (byte) (hex2byte((char) asc[2 * i]) << 4 | hex2byte((char) asc[2 * i + 1]));
		}
		return bcd;
	}
	
	/**
	 * The string is converted to a byte array
	 * @param data  String
	 * @param charsetName Character set name
	 * @return Byte array
	 */
	public static byte[] toBytes(String data, String charsetName) {
		if (isNullEmpty(data)) {
			return EMPTY_BYTE_ARRAY;
		}

		try {
			return data.getBytes(charsetName);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	/**
	 * The string is converted to ISO8859-1 byte representation
	 * @param data String
	 * @return Byte array
	 */
	public static byte[] toBytes(String data) {
		return toBytes(data, CHARSET_ISO8859_1);
	}

	/**
	 * The string is converted to GBK byte representation
	 * @param data String
	 * @return Byte array
	 */
	public static byte[] toGBK(String data) {
		return toBytes(data, CHARSET_GBK);
	}

	/**
	 * The string is converted to GB2312 byte representation
	 * @param data String
	 * @return Byte array
	 */
	public static byte[] toGB2312(String data) {
		return toBytes(data, CHARSET_GB2312);
	}

	/**
	 * The string is converted to UTF-8 byte representation
	 * @param data String
	 * @return Byte array
	 */
	public static byte[] toUtf8(String data) {
		return toBytes(data, CHARSET_UTF8);
	}

	/**
	 * Byte arrays are converted to strings
	 * @param data  Byte array
	 * @param charsetName Character set name
	 * @return String
	 */
	public static String fromBytes(byte[] data, String charsetName) {
		try {
			return new String(data, charsetName);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	/**
	 * ISO8859-1 byte arrays are converted to strings
	 * @param data Byte array
	 * @return String
	 */
	public static String fromBytes(byte[] data) {
		return fromBytes(data, CHARSET_ISO8859_1);
	}

	/**
	 * GBK byte arrays are converted to strings
	 * @param data Byte array
	 * @return String
	 */
	public static String fromGBK(byte[] data) {
		return fromBytes(data, CHARSET_GBK);
	}

	/**
	 * GB2312 byte arrays are converted to strings
	 * @param data Byte array
	 * @return String
	 */
	public static String fromGB2312(byte[] data) {
		return fromBytes(data, CHARSET_GB2312);
	}

	/**
	 * UTF-8 byte arrays are converted to strings
	 * @param data Byte array
	 * @return String
	 */
	public static String fromUtf8(byte[] data) {
		return fromBytes(data, CHARSET_UTF8);
	}

	public static boolean isAllNullEmpty(String... strings) {
		for (String str : strings) {
			if (!isNullEmpty(str)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isAllNullEmpty(byte[]... arrays) {
		for (byte[] array : arrays) {
			if (!isNullEmpty(array)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNullEmpty(String str) {
		return TextUtils.isEmpty(str);
	}

	public static boolean isNullEmpty(byte[] array) {
		return (array == null) || (array.length == 0);
	}

	public static boolean checkPartEquals(byte[] data1, byte[] data2) {
		if (data1 == null || data2 == null) {
			return false;
		}

		if (data2.length == data1.length) {
			return Arrays.equals(data2, data1);
		} else {
			int cmpLen = Math.min(data1.length, data2.length);
			if (data2.length < data1.length) {
				return Arrays.equals(data2, Arrays.copyOfRange(data1, 0, cmpLen));
			} else {
				return Arrays.equals(data1, Arrays.copyOfRange(data2, 0, cmpLen));
			}
		}
	}
}
