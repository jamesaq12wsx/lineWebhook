package com.pershing.APIdemo;

import com.pershing.action.MessageAction;
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
		EUR_column.addAction(new MessageAction("我要兌換", "EUR"));
		carousel.addColumn(EUR_column);
		// JPY currency
		Column JPY_column = new Column("日元 JPY");
		JPY_column.setThumbnailImageUrl("https://i.imgur.com/1qE09cD.jpg");
		JPY_column.addAction(new MessageAction("我要兌換", "JPY"));
		carousel.addColumn(JPY_column);
		// CAD currency
		Column CAD_column = new Column("加幣 CAD");
		CAD_column.setThumbnailImageUrl("https://i.imgur.com/6FavWOy.jpg");
		CAD_column.addAction(new MessageAction("我要兌換", "CAD"));
		carousel.addColumn(CAD_column);
		// CNY currency
		Column CNY_column = new Column("人民幣 CNY");
		CNY_column.setThumbnailImageUrl("https://i.imgur.com/Egu251z.jpg");
		CNY_column.addAction(new MessageAction("我要兌換", "CNY"));
		carousel.addColumn(CNY_column);
		// USD currency
		Column USD_column = new Column("美元 USD");
		USD_column.setThumbnailImageUrl("https://i.imgur.com/gmxsIO9.jpg");
		USD_column.addAction(new MessageAction("我要兌換", "USD"));
		carousel.addColumn(USD_column);
		// Construct the final carousel template
		TemplateMessage message = new TemplateMessage("外幣兌換", carousel);
		Util.sendSinglePush(sender, userId, message);
	}
	
}
