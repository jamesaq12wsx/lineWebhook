package com.pershing.APIdemo;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sun.net.httpserver.HttpExchange;

/**
 * A utility class that handles QR code related functionalities
 * 
 * @author ianw3214
 *
 */
public class QRCodeGenerator {

	// The format that QR codes should be generated in
	private static final String FORMAT = "JPEG";
	
	/**
	 * Basic method that generates a QR code based on the input information
	 * 
	 * @param text				The text to be encoded into the QR code
	 * @param width				The width of the resulting QR code
	 * @param height			The height of the resulting QR code
	 * @param filePath			The file to store the resulting QR code in
	 * @throws WriterException	
	 * @throws IOException
	 */
	public final static void generateQRCodeImage(String text, int width, int height, String filePath) throws WriterException, IOException {
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
		
		Path path = FileSystems.getDefault().getPath(filePath);
		MatrixToImageWriter.writeToPath(bitMatrix, FORMAT, path);
	}

	/**
	 * Writes a QR code into a HTTP response of a HTTP request
	 * @param exchange			The HTTP exchange object of the HTTP request
	 * @return					A flag representing whether the operation was successful or not
	 */
	public final static boolean handleQRCodeFromGet(HttpExchange exchange) {
		
		String parameters = exchange.getRequestURI().getQuery();
		// If there is no information to encode then no QR code can be generated
		if (parameters == null) return false;
		
		try {
			// Generate the QR code and write it int othe output stream of the HTTP response
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix bitMatrix = qrCodeWriter.encode(parameters, BarcodeFormat.QR_CODE, 240, 240);
			exchange.getResponseHeaders().add("Content-Type", "image/jpeg");
			exchange.sendResponseHeaders(200, 0);
			OutputStream os = exchange.getResponseBody();
			MatrixToImageWriter.writeToStream(bitMatrix, FORMAT, os);
			os.close();
		} catch (WriterException e) {
			e.printStackTrace();
			return false;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}
