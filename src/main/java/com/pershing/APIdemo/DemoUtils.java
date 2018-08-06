package com.pershing.APIdemo;

import java.util.ArrayList;
import java.util.List;

import com.pershing.action.PostbackAction;
import com.pershing.action.URIAction;
import com.pershing.message.Message;
import com.pershing.message.TemplateMessage;
import com.pershing.mockAPI.MockAPI;
import com.pershing.sender.MessageSender;
import com.pershing.template.ButtonsTemplate;
import com.pershing.template.CarouselTemplate;
import com.pershing.template.Column;
import com.pershing.util.Util;

public class DemoUtils {

	// HARD CODED TEMPORARY UTILITY FUNCTION
	public static void sendCurrencyExchangeCarousel(String userId, MessageSender sender) {
		// Construct the image carousel
		CarouselTemplate carousel = new CarouselTemplate();
		// EUR currency
		float EUR = MockAPI.getCurrency("EUR", "TWD");
		Column EUR_column = new Column("歐元 EUR: " + Float.toString(EUR));
		EUR_column.setThumbnailImageUrl("https://i.imgur.com/GObxdWf.jpg");
		EUR_column.addAction(new PostbackAction("我要兌換", "action=exchange&data=EUR", "\u200B" + "兌換歐元"));
		carousel.addColumn(EUR_column);
		// JPY currency
		float JPY = MockAPI.getCurrency("JPY", "TWD");
		Column JPY_column = new Column("日元 JPY: " + Float.toString(JPY));
		JPY_column.setThumbnailImageUrl("https://i.imgur.com/1qE09cD.jpg");
		JPY_column.addAction(new PostbackAction("我要兌換", "action=exchange&data=JPY", "\u200B" + "兌換日元"));
		carousel.addColumn(JPY_column);
		// CAD currency
		float CAD = MockAPI.getCurrency("CAD", "TWD");
		Column CAD_column = new Column("加幣 CAD: " + Float.toString(CAD));
		CAD_column.setThumbnailImageUrl("https://i.imgur.com/6FavWOy.jpg");
		CAD_column.addAction(new PostbackAction("我要兌換", "action=exchange&data=CAD","\u200B" + "兌換加幣"));
		carousel.addColumn(CAD_column);
		// CNY currency
		float CNY = MockAPI.getCurrency("CNY", "TWD");
		Column CNY_column = new Column("人民幣 CNY: " + Float.toString(CNY));
		CNY_column.setThumbnailImageUrl("https://i.imgur.com/Egu251z.jpg");
		CNY_column.addAction(new PostbackAction("我要兌換", "action=exchange&data=CNY", "\u200B" + "兌換人民幣"));
		carousel.addColumn(CNY_column);
		// USD currency
		float USD = MockAPI.getCurrency("USD", "TWD");
		Column USD_column = new Column("美元 USD: " + Float.toString(USD));
		USD_column.setThumbnailImageUrl("https://i.imgur.com/gmxsIO9.jpg");
		USD_column.addAction(new PostbackAction("我要兌換", "action=exchange&data=USD", "\u200B" + "兌換美元"));
		carousel.addColumn(USD_column);
		// Construct the final carousel template
		TemplateMessage carouselMessage = new TemplateMessage("外幣兌換", carousel);
		
		// Construct a button to open a link to PWA
		ButtonsTemplate link = new ButtonsTemplate.ButtonsTemplateBuilder("其他匯率").build();
		link.addAction(new URIAction("其他匯率", "https://pwa-web-page.herokuapp.com/"));
		TemplateMessage buttonsMessage = new TemplateMessage("其他匯率", link);
		
		// Construct the list of messages to send
		List<Message> messages = new ArrayList<Message>();
		messages.add(carouselMessage);
		messages.add(buttonsMessage);
		
		sender.sendPush(userId, messages, "");
	}
	
}
