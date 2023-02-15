package com.emc.edc.emv.util;

/**
 * Created by caizl on 2020/6/17.
 */
public class BmpUtil {

    /**********************************************************
     * getBmpGreybytes
     * 	Extract 8 bits of grayscale data from the bmp file.
     * 	Currently only 1-bit or 8-bit bmp graphs are supported.
     *
     * parameter：
     * 	imageData -- bmp file data stream
     *
     * The return value：
     * 	GreyBmpData.ret=
     * 				0-success
     *  			1-Malformation
     *   			2-This bit is not supported
     *    			3-The image size is incorrect
     *     			4-The image size is incorrect
     * */
    public static GreyBmpData getGreyBmpData(byte[] imageData) {
        GreyBmpData greyBmpData = new GreyBmpData();
        int bmpSize = 0;// The size of the entire bmp file, including the head
        int bfOffBits = 0;// The starting address of the pixel data
        int biSizeImage = 0;// The size of the image data
        short biBitCount = 0;// Number of bits in the image，1,4,8,16,24,32

        greyBmpData.data = null;
        greyBmpData.width = 0;
        greyBmpData.height = 0;
        greyBmpData.ret = 0;

        if ((imageData[0] != 'B') || (imageData[1] != 'M')) {
            greyBmpData.ret = 1;//Malformation
            return greyBmpData;
        }

        bmpSize = toInt_littleEndian(imageData, 2);
        bfOffBits = toInt_littleEndian(imageData, 0xa);
        greyBmpData.width = toInt_littleEndian(imageData, 0x12);
        //If the image is high, if it is a positive number, the image is inverted, if it is a negative number, the image is positive. Most are inverted.
        greyBmpData.height = toInt_littleEndian(imageData, 0x16);
        biBitCount = toShort_littleEndian(imageData, 0x1c);
        biSizeImage = toInt_littleEndian(imageData, 0x22);

        if ((biBitCount != 1) && (biBitCount != 8)) {
            greyBmpData.ret = 2;
            return greyBmpData;
        }

        if (biBitCount == 8) {
            int bitPerPix = 8;
            int bytePerLine = (greyBmpData.width * bitPerPix + 31) / 32 * 4;
            greyBmpData.width = bytePerLine;

            biSizeImage = greyBmpData.width * Math.abs(greyBmpData.height);
            if (biSizeImage != (imageData.length - bfOffBits)) {
                greyBmpData.ret = 3;
                return greyBmpData;
            }
            greyBmpData.data = new byte[biSizeImage];
            if (greyBmpData.height < 0) {
                //The image is forward-facing
                for (int i = 0; i < biSizeImage; i++) {
                    greyBmpData.data[i] = imageData[bfOffBits + i];
                }
            } else {
                //The image is inverted
                int pos = 0;
                for (int i = greyBmpData.height - 1; i >= 0; i--) {
                    for (int j = 0; j < bytePerLine; j++) {
                        greyBmpData.data[pos++] = imageData[bfOffBits + i * bytePerLine + j];
                    }
                }
            }
        } else if (biBitCount == 1) {
            int bitPerPix = 1;
            int bytePerLine = (greyBmpData.width * bitPerPix + 31) / 32 * 4;
            greyBmpData.width = bytePerLine * 8;

            biSizeImage = greyBmpData.width * Math.abs(greyBmpData.height);
            if ((biSizeImage / 8) != (imageData.length - bfOffBits)) {
                greyBmpData.ret = 4;
                return greyBmpData;
            }
            greyBmpData.data = new byte[biSizeImage];
            if (greyBmpData.height < 0) {
                for (int i = 0; i < biSizeImage / 8; i++) {
                    //bit0 is byte7, bit1 is byte6，... bit7 is byte0
                    greyBmpData.data[i * 8 + 7] = (byte) (((imageData[bfOffBits + i] >> 0) & 0x1) == 0 ? 0 : 255);
                    greyBmpData.data[i * 8 + 6] = (byte) (((imageData[bfOffBits + i] >> 1) & 0x1) == 0 ? 0 : 255);
                    greyBmpData.data[i * 8 + 5] = (byte) (((imageData[bfOffBits + i] >> 2) & 0x1) == 0 ? 0 : 255);
                    greyBmpData.data[i * 8 + 4] = (byte) (((imageData[bfOffBits + i] >> 3) & 0x1) == 0 ? 0 : 255);
                    greyBmpData.data[i * 8 + 3] = (byte) (((imageData[bfOffBits + i] >> 4) & 0x1) == 0 ? 0 : 255);
                    greyBmpData.data[i * 8 + 2] = (byte) (((imageData[bfOffBits + i] >> 5) & 0x1) == 0 ? 0 : 255);
                    greyBmpData.data[i * 8 + 1] = (byte) (((imageData[bfOffBits + i] >> 6) & 0x1) == 0 ? 0 : 255);
                    greyBmpData.data[i * 8 + 0] = (byte) (((imageData[bfOffBits + i] >> 7) & 0x1) == 0 ? 0 : 255);
                }
            } else {
                int pos = 0;
                for (int i = greyBmpData.height - 1; i >= 0; i--) {
                    for (int j = 0; j < bytePerLine; j++) {
                        greyBmpData.data[pos * 8 + 7] = (byte) (((imageData[bfOffBits + i * bytePerLine + j] >> 0) & 0x1) == 0 ? 0 : 255);
                        greyBmpData.data[pos * 8 + 6] = (byte) (((imageData[bfOffBits + i * bytePerLine + j] >> 1) & 0x1) == 0 ? 0 : 255);
                        greyBmpData.data[pos * 8 + 5] = (byte) (((imageData[bfOffBits + i * bytePerLine + j] >> 2) & 0x1) == 0 ? 0 : 255);
                        greyBmpData.data[pos * 8 + 4] = (byte) (((imageData[bfOffBits + i * bytePerLine + j] >> 3) & 0x1) == 0 ? 0 : 255);
                        greyBmpData.data[pos * 8 + 3] = (byte) (((imageData[bfOffBits + i * bytePerLine + j] >> 4) & 0x1) == 0 ? 0 : 255);
                        greyBmpData.data[pos * 8 + 2] = (byte) (((imageData[bfOffBits + i * bytePerLine + j] >> 5) & 0x1) == 0 ? 0 : 255);
                        greyBmpData.data[pos * 8 + 1] = (byte) (((imageData[bfOffBits + i * bytePerLine + j] >> 6) & 0x1) == 0 ? 0 : 255);
                        greyBmpData.data[pos * 8 + 0] = (byte) (((imageData[bfOffBits + i * bytePerLine + j] >> 7) & 0x1) == 0 ? 0 : 255);
                        pos++;
                    }

                }
            }
        }

        greyBmpData.ret = 0;
        return greyBmpData;
    }


    public static class GreyBmpData {
        public int ret = -1;
        public byte[] data;
        public int width;
        public int height;
    }

    public static int toInt_littleEndian(byte[] b, int pos) {
        int ret = 0;
        ret = b[pos] & 0xFF;
        ret |= (b[pos+1]<<8) & 0xFF00;
        ret |= (b[pos+2]<<16) & 0xFF0000;
        ret |= (b[pos+3]<<24) & 0xFF000000;
        return ret;
    }

    public static short toShort_littleEndian(byte[] b, int pos) {
        int d = 0;
        short ret;

        d = b[pos] & 0xFF;
        d |= (b[pos+1]<<8) & 0xFF00;

        ret = (short)(d&0xffff);
        return ret;
    }

}
