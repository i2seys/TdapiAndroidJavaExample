# TdapiAndroidJavaExample
An example of using the TdLib library on an android device (Java)

A simple example of a project using TdLib on android (Java).
With this project you can see how to send messages to any public chat (bot or any telegram account).

Successful use requires:
1) Fill blank lines with parameters at the top of the screen (api id, api hash, phone number, public chat. How to get api_id and api_hash: https://core.telegram.org/api/obtaining_api_id)
2) Click on the button "Create client"
3) Click on the button "Set tdlib parameters"
4) Click on the button "Check database encryprion key"
5) Click on the button "Check authorization state". If you see toast "authorizationStateReady", skip step 6 and 7.
6) If you see toast "authorizationStateWaitPhoneNumber",  then click on the button "Set phone number and code". Otherwise click on the button "Log out" and start from step 1.
7) A message with a code will be sent to the specified number in the telegram. Enter the code and click "Ok".
8) Click on the button "Send test message". As a result, you will send a test message to the above public chat.

TdLib documentation: https://core.telegram.org/tdlib/getting-started
