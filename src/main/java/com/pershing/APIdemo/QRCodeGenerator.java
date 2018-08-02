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

public class QRCodeGenerator {

	private static final String FORMAT = "JPEG";
	
	public final static void generateQRCodeImage(String text, int width, int height, String filePath) throws WriterException, IOException {
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
		
		Path path = FileSystems.getDefault().getPath(filePath);
		MatrixToImageWriter.writeToPath(bitMatrix, FORMAT, path);
	}

	public final static boolean handleQRCodeFromGet(HttpExchange exchange) {
		String parameters = exchange.getRequestURI().getQuery();
		if (parameters == null) parameters = "";
		System.out.println("GET REQUEST QUERY: " + parameters);
		
		try {
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix bitMatrix = qrCodeWriter.encode(parameters, BarcodeFormat.QR_CODE, 240, 240);
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
