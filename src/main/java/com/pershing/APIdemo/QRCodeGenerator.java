package com.pershing.APIdemo;

import java.io.IOException;
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
		System.out.println("GET REQUEST QUERY: " + parameters);
		return true;
	}
	
}
