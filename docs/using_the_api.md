# Using The API

## The WebHookHandler

The WebHookHandler is the center of the API. It takes incoming events and parses the raw data into various classes so that their properties can be easily accessed.

The WebHookHandler is constructed with 3 properties, the **channelSecret**, **channelAccessToken** and a **MessageSender**. The channelSecret and channelAccessToken properties are just the string values taken from the LINE@ channel page.

## The MessageSender

A MessageSender is an interface for sending push/reply messages. The reason the interface is separate from the WebHookHandler is to allow custom implementations of message sending. For example, you might want to push all message requests to a central server before sending it to the LINE Messaging API, or perhaps you want to log each sent message. Using an interface allows the user to customize the functionality of the message sending functions to acheive different user goals. The interface itself only has two methods:

```java
public interface MessageSender {

    public Response sendReply(String channelAccessToken, String token, List<Message> replyMessages);
    public Response sendPush(String channelAccessToken, String userId, List<Message> pushMessages);

}
```

<div style="page-break-after: always;"></div>

## Inheriting the WebHookHandler

With the API, custom bots are created via inheritance to the WebHookHandler class. There is a base handle function for each event type, which can be overriden to allow the user to write their own handlers. Bellow is a sample implementation of a custom handler function which echos text messages from LINE users that message the bot:

```java
@Override
protected void handleMessageEvent(MessageEvent event) {
    String replyToken = event.replyToken();
    // find the userId
    String lineId = event.source().getId();
    // parse the message and handle appropriately
    Message message = event.message();
    if (message.type() == MessageType.TEXT) {
        TextMessage textMessage = (TextMessage)message;
        String text = textMessage.getText();
        sendSingleTextReply(replyToken, text);
    }
}
```

## Setting up the WebHookHandler

The LINE Messaging API sends its events to a WebHook, which is usually a server that accepts HTTPS POST requests. The API does not have a built in HTTPS server, but instead has specifications on how it should take in data from HTTPS POST requests. Every interaction goes through the **handleWebHookEvent** function, and the function signature looks like so:
> public final boolean handleWebHookEvent(String headerSignature, String body);

The **headerSignature** is a request header with the key **X-Line-Signature**, and it is used to verify the message source. The validation is taken care of by the WebHookHandler and does not need to be done by the user of the API. The body should be a big chunk of JSON data passed into the function as a String.

Thus, to hook up the WebHookHandler to your bot server, simply call the central function from your POST request receiver with the corresponding fields set properly.

## Documentation

Most classes and methods in the API have docs written for them, and more complicated code have comments written alongside as well.

There is a demo written in the project as well, and it uses most functionalities of the API; Thus, it can server as a useful reference.