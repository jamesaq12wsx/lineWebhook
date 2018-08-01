package com.pershing.APIdemo;

import com.pershing.action.PostbackAction;
import com.pershing.message.TemplateMessage;
import com.pershing.sender.MessageSender;
import com.pershing.template.CarouselTemplate;
import com.pershing.template.Column;
import com.pershing.util.Util;

public class DemoUtils {

	// HARD CODED TEMPORARY UTILITY FUNCTION
	public static void sendCurrencyExchangeCarousel(String userId, MessageSender sender) {
		CarouselTemplate carousel = new CarouselTemplate();
		// EUR currency
		Column EUR_column = new Column("歐元 EUR");
		EUR_column.setThumbnailImageUrl("https://i.imgur.com/GObxdWf.jpg");
		EUR_column.addAction(new PostbackAction("我要兌換", "action=exchange&data=EUR", "\u200B" + "兌換歐元"));
		carousel.addColumn(EUR_column);
		// JPY currency
		Column JPY_column = new Column("日元 JPY");
		JPY_column.setThumbnailImageUrl("https://i.imgur.com/1qE09cD.jpg");
		JPY_column.addAction(new PostbackAction("我要兌換", "action=exchange&data=JPY", "\u200B" + "兌換日元"));
		carousel.addColumn(JPY_column);
		// CAD currency
		Column CAD_column = new Column("加幣 CAD");
		CAD_column.setThumbnailImageUrl("https://i.imgur.com/6FavWOy.jpg");
		CAD_column.addAction(new PostbackAction("我要兌換", "action=exchange&data=CAD","\u200B" + "兌換加幣"));
		carousel.addColumn(CAD_column);
		// CNY currency
		Column CNY_column = new Column("人民幣 CNY");
		CNY_column.setThumbnailImageUrl("https://i.imgur.com/Egu251z.jpg");
		CNY_column.addAction(new PostbackAction("我要兌換", "action=exchange&data=CNY", "\u200B" + "兌換人民幣"));
		carousel.addColumn(CNY_column);
		// USD currency
		Column USD_column = new Column("美元 USD");
		USD_column.setThumbnailImageUrl("https://i.imgur.com/gmxsIO9.jpg");
		USD_column.addAction(new PostbackAction("我要兌換", "action=exchange&data=USD", "\u200B" + "兌換美元"));
		carousel.addColumn(USD_column);
		// Construct the final carousel template
		TemplateMessage message = new TemplateMessage("外幣兌換", carousel);
		Util.sendSinglePush(sender, userId, message);
	}
	
}
