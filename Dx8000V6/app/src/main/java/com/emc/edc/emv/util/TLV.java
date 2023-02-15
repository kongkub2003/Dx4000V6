package com.emc.edc.emv.util;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * TLV objects
 * @author chenwei
 *
 */
public class TLV {
	private byte[] data;
	private String tag;
	private int length = -1;
	private byte[] value;
	public TLV() {
		
	}
	
	/**
	 * Extract valid numeric values from the raw data and generate a TLV object
	 * @param tlData		Data containing TL，For example, {0x9F, 0x01, 0x02} means Tag:9F01, Len:02
	 * @param tlOffset		tl Where valid data is in tlData, such as "tlData = {0x00, 0x00, 0x9F, 0x01, 0x02}, tl=2"
	 * @param vData			value data, such as {0x12, 0x34}
	 * @param vOffset		The position of valid data for value in vData, such as "vData = {0x00, 0x00, 0x00, 0x12, 0x34}, vOffset=3"
	 * @return	TLV
	 */
	public static TLV fromRawData(byte[] tlData, int tlOffset, byte[] vData, int vOffset) {
		int tLen = getTLength(tlData, tlOffset);
		int lLen = getLLength(tlData, tlOffset+tLen);
		int vLen = calcValueLength(tlData, tlOffset+tLen, lLen);
		
		TLV d = new TLV();
		d.data = BytesUtil.merage(BytesUtil.subBytes(tlData, tlOffset, tLen+lLen), BytesUtil.subBytes(vData, vOffset, vLen));
		d.getTag();
		d.getLength();
		d.getBytesValue();
		
		return d;
	}
	
	/**
	 * Only T and V are provided to generate a TLV object
	 * @param tagName	TAG, such as "9F01"
	 * @param value		The complete VALUE data, such as {0x12, 0x34}, contains the length itself
	 * @return	TLV
	 */
	public static TLV fromData(String tagName, byte[] value) {
		byte[] tag = BytesUtil.hexString2Bytes(tagName);
		TLV d = new TLV();
		d.data = BytesUtil.merage(tag, makeLengthData(value.length), value);
		d.tag = tagName;
		d.length = value.length;
		d.value = value;
		return d;
	}
	
	/**
	 * Generates a TLV object from the TLV buffer
	 * @param data		Contains data in TLV format
	 * @param offset	The offset of the location of the TAG
	 * @return	TLV
	 */
	public static TLV fromRawData(byte[] data, int offset) {
		int len = getDataLength(data, offset);
		TLV d = new TLV();
		d.data = BytesUtil.subBytes(data, offset, len);
		d.getTag();
		d.getLength();
		d.getBytesValue();
		return d;
	}
	
	/**
	 * Get the TAG, such as "9F01"
	 * @return	The HEX string represents the TAG
	 */
	public String getTag() {
		if (tag != null) {
			return tag;
		}
		int tLen = getTLength(data, 0);
		return tag = BytesUtil.bytes2HexString(BytesUtil.subBytes(data, 0, tLen));
	}
	
	/**
	 * Gets the Value length, such as Value={0x12, 0x34} returns 2
	 * @return Value byte data length
	 */
	public int getLength() {
		if (length > -1) {
			return length;
		}
		int offset = getTLength(data, 0);
		int l = getLLength(data, offset);
		if (l == 1) {
			return data[offset] & 0xff;
		}

		int afterLen = 0;
		for (int i = 1; i < l; i++) {
			afterLen <<= 8;
			afterLen |= (data[offset + i]) & 0xff;
		}
		return length = afterLen;
	}
	
	/**
	 * Gets the binary data length of T and L, such as 9F010201234 returns 3 (9F0102)
	 * @return The length of T and L
	 */
	public int getTLLength() {
		if (data == null) {
			return -1;
		}
		return data.length - getBytesValue().length;
	}
	
	/**
	 * Gets Value, a HEX string converted from BCD. For example Value={0x12, 0x34} returns "1234"
	 * @return The Value represented by the HEX string
	 */
	public String getValue() {
		byte[] temp = getBytesValue();
		return BytesUtil.bytes2HexString(temp == null ? new byte[0] : temp);
	}
	
	/**
	 * Gets the first byte of value data
	 * @return The first byte of value data
	 */
	public byte getByteValue() {
		return getBytesValue()[0];
	}
	
	/**
	 * Gets Value, a GBK-encoded string
	 * @return Converted Value
	 */
	public String getGBKValue() {
		try {
			return new String(getBytesValue(), "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Gets Value and converts Value to a numeric string, e.g. Value={0x31, 0x32} returns "12". If Value contains non-numeric characters or decimals, throw {@link NumberFormatException}
	 * @return Converted Value
	 */
	public String getNumberValue() {
		String num = getValue();
		if(num == null) {
			return null;
		}
		return String.valueOf(Long.parseLong(num));
	}
	
	/**
	 * Get Value and convert Value to ASC code
	 * @return Converted Value
	 */
	public byte[] getNumberASCValue() {
		try {
			String result = getNumberValue();
			if(result == null) {
				return null;
			}
			return result.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}
	
	/**
	 * Gets the VALUE after BCD compression, such as Value={0x31, 0x32}, returns 0x12
	 * @return Converted Value
	 */
	public byte[] getBCDValue() {
		String result = getGBKValue();
		if(result == null) {
			return null;
		}
		return BytesUtil.hexString2Bytes(result);
	}
	
	/**
	 * Get raw TLV data such as {0x9F, 0x01, 0x02, 0x12, 0x34}
	 * @return	Raw TLV format data
	 */
	public byte[] getRawData() {
		return data;
	}
	
	/**
	 * Gets the original Value byte array
	 * @return	Original Value
	 */
	public byte[] getBytesValue() {
		if(value != null) {
			return value;
		}
		int l = getLength();
		return value = BytesUtil.subBytes(data, data.length - l, l);
	}
	
	/**
	 * Whether the data is legitimate
	 * @return	If the data is not legitimate, false is returned, otherwise true is returned
	 */
	public boolean isValid() {
		return data != null;
	}
	
	private static int getTLength(byte[] data, int offset) {
		if ((data[offset] & 0x1F) == 0x1F) {
			return parseTLength(data, ++offset, 2);
		}
		return 1;
	}

	private static int parseTLength(byte[] data, int nextOffset, int clen) {
		if ((data[nextOffset] & 0x80) == 0x80) {
			clen = parseTLength(data, ++nextOffset, ++clen);
		}
		return clen;
	}

	private static int getLLength(byte[] data, int offset) {
		if ((data[offset] & 0x80) == 0) {
			return 1;
		}
		return (data[offset] & 0x7F) + 1;
	}
	
	private static int getDataLength(byte[] data, int offset) {
		int tLen = getTLength(data, offset);
		int lLen = getLLength(data, offset+tLen);
		int vLen = calcValueLength(data, offset+tLen, lLen);
		return tLen + lLen + vLen;
	}
	
	private static int calcValueLength(byte[] l, int offset, int lLen) {
		if (lLen == 1) {
			return l[offset] & 0xff;
		}
		
		int vLen = 0;
		for(int i=1; i<lLen; i++) {
			vLen <<= 8;
			vLen |= (l[offset+i])&0xff;
		}
		return vLen;
	}

	private static byte[] makeLengthData(int len) {
		if (len > 127) {
			byte[] tempLen = BytesUtil.intToBytesByLow(len);
			int start = 0;
			for (int i = 0; i < tempLen.length; i++) {
				if (tempLen[i] != 0x00) {
					start = i;
					break;
				}
			}

			byte[] lenData = BytesUtil.subBytes(tempLen, start, -1);
			lenData = BytesUtil.merage(new byte[] {(byte) (0x80 | lenData.length)}, lenData);

			Log.e("test", "lenData: " + BytesUtil.bytes2HexString(lenData));
			return lenData;
		} else {
			return new byte[] { (byte) len };
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		
		if(!(obj instanceof TLV)) {
			return false;
		}
		
		if(data == null || ((TLV)obj).data == null) {
			return false;
		}
		
		return Arrays.equals(data, ((TLV)obj).data);
	}
	
	@Override
	public String toString() {
		if(data == null) {
			return super.toString();
		}
		return BytesUtil.bytes2HexString(data);
	}
}