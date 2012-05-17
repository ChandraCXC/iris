/**
 * Copyright (C) 2012 Smithsonian Astrophysical Observatory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cfa.vo.interop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author olaurino
 */
public class EncodeDoubleArray {

	public static final int WORDSIZE = 8;

	private static double[] byteToDouble(byte[] data) throws IOException {

		int len = data.length;

		if (len % WORDSIZE != 0) {
			throw new IOException("Array length is not divisible by wordsize");
		}

		int size = len / WORDSIZE;
		double[] result = new double[size];

		DataInputStream inputStream = new DataInputStream(
				new ByteArrayInputStream(data));

		try {

			int ii = 0;
			while (inputStream.available() > 0) {
				result[ii] = inputStream.readDouble();
				ii++;
			}

		} catch (EOFException e) {
			throw new IOException(
					"Unable to read from dataInputStream, found EOF");

		} catch (IOException e) {
			throw new IOException(
					"Unable to read from dataInputStream, IO error");
		}

		return result;

	}

	private static byte[] doubleToByte(double[] data) throws IOException {

		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream outputStream = new DataOutputStream(byteStream);

		try {

			for (int ii = 0; ii < data.length; ii++)
				outputStream.writeDouble(data[ii]);

		} catch (EOFException e) {
			throw new IOException(
					"Unable to read from dataInputStream, found EOF");

		} catch (IOException e) {
			throw new IOException(
					"Unable to read from dataInputStream, IO error");
		}

		byte[] result = byteStream.toByteArray();

		return result;
	}

	public static String encodeBase64(double[] data, boolean swapByteOrder)
			throws IOException {

		byte[] decodedData = doubleToByte(data);

		if (swapByteOrder) {
			ByteBuffer buf = ByteBuffer.wrap(decodedData);
			buf = buf.order(ByteOrder.LITTLE_ENDIAN);
			buf.get(decodedData);
		}

		Base64 codec = new Base64();
		byte[] encodedData = codec.encode(decodedData);

		String result = new String(encodedData);

		return result;
	}

	public static double[] decodeBase64(String dataString, boolean swapByteOrder)
			throws IOException {

		byte[] encodedData = dataString.getBytes();

		Base64 codec = new Base64();
		byte[] decodedData = codec.decode(encodedData);

		if (swapByteOrder) {
			ByteBuffer buf = ByteBuffer.wrap(decodedData);
			buf = buf.order(ByteOrder.LITTLE_ENDIAN);
			buf.get(decodedData);
		}

		double[] result = byteToDouble(decodedData);

		return result;
	}
}