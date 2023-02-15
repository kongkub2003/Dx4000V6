package com.emc.edc.emv.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A TLV list, a dataset of TLV objects
 * @author chenwei
 *
 */
public class TLVList {
	private List<TLV> data = new ArrayList<TLV>();
	
	/**
	 * Generate a TLVList object from the TLV binary data, which can contain 0 to n TLV objects
	 * @param data	TLV data, such as {0x9F,0x01,0x02,0x12,0x34,0x80,0x01,0x56}
	 * @return	TLVList
	 */
	public static TLVList fromBinary(byte[] data) {
		TLVList l = new TLVList();
		int offset = 0;
		while(offset < data.length) {
			TLV d = TLV.fromRawData(data, offset);
			l.addTLV(d);
			offset += d.getRawData().length;
		}
		return l;
	}
	
	/**
	 * Generates a TLVList object from the TLV data represented by the HEX string, which can contain 0 to n TLV objects
	 * @param data	TLV data, such as "9F01021234800156"
	 * @return	TLVList
	 */
	public static TLVList fromBinary(String data) {
		return fromBinary(BytesUtil.hexString2Bytes(data));
	}
	
	/**
	 * Returns the number of TLV
	 * @return	quantity
	 */
	public int size() {
		return data.size();
	}

	/**
	 * Converts to standard binary data such as {0x9F, 0x01, 0x02, 0x12, 0x34, 0x80, 0x01, 0x56}
	 * @return	Binary data
	 */
	public byte[] toBinary() {
		byte[][] allData = new byte[data.size()][];
		for(int i=0; i<data.size(); i++) {
			allData[i] = data.get(i).getRawData();
		}
		return BytesUtil.merage(allData);
	}
	
	/**
	 * Check whether the TLV object corresponding to a TAG is included
	 * @param tag	TAG, such as "9F01"
	 * @return	true means contains, false means does not
	 */
	public boolean contains(String tag) {
		return null != getTLV(tag);
	}
	
	/**
	 * Gets the TLV object for the specified TAG
	 * @param tag	TAG，such as "9F01"
	 * @return	TLV
	 */
	public TLV getTLV(String tag) {
		for(TLV d : data) {
			if(d.getTag().equals(tag)) {
				return d;
			}
		}
		return null;
	}
	
	/**
	 * Gets the subset
	 * @param tags	Multiple TAG, such as "9F01", "9F02"
	 * @return A subset of TLV objects
	 */
	public TLVList getTLVs(String...tags) {
		TLVList list = new TLVList();
		for(String tag : tags) {
			TLV data = getTLV(tag);
			if(data != null) {
				list.addTLV(data);
			}
		}
		if(list.size() == 0) {
			return null;
		}
		return list;
	}
	
	/**
	 * Gets the TLV according to the specified location
	 * @param index	Location, from 0
	 * @return	TLV object
	 */
	public TLV getTLV(int index) {
		return data.get(index);
	}
	
	/**
	 * Add a TLV object
	 * @param tlv	TLV object
	 */
	public void addTLV(TLV tlv) {
		if(tlv.isValid()) {
			data.add(tlv);
		}
		else {
			throw new IllegalArgumentException("tlv is not valid!");
		}
	}
	
	/**
	 * The TLV object pointed to by the specific TAG is retained, and all remaining is deleted
	 * @param tags	Specifies the TAG
	 */
	public void retainAll(String... tags) {
		List<String> tagList = Arrays.asList(tags);
		for (int index = 0; index < data.size();) {
			if (!tagList.contains(data.get(index).getTag())) {
				data.remove(index);
			} else {
				index++;
			}
		}
	}
	
	/**
	 * Deletes a TLV object
	 * @param tag	TAG of TLV objects, such as "9F01"
	 */
	public void remove(String tag) {
		for (int i = 0; i < data.size();) {
			if (tag.equals(data.get(i).getTag())) {
				data.remove(i);
			} else {
				i++;
			}
		}
	}
	
	/**
	 * Delete one to more TLV objects
	 * @param tags	TAG of TLV objects, such as "9F01"
	 */
	public void removeAll(String... tags) {
		List<String> tagList = Arrays.asList(tags);
		for (int i = 0; i < data.size();) {
			if (tagList.contains(data.get(i).getTag())) {
				data.remove(i);
			} else {
				i++;
			}
		}
	}
	
	/**
	 * Convert the TLV list to HEX strings sequentially
	 */
	@Override
	public String toString() {
		if (data.isEmpty()) {
			return super.toString();
		}
		return BytesUtil.bytes2HexString(toBinary());
	}
}