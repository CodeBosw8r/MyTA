# MyTA

MyTA is a Mail Transfer Agent (MTA) with an HTTP API. It takes mail messages in a simple JSON format, then composes valid Mime multipart email messages, optionally signs a DKIM header and delivers the email to one or more local SMTP servers. Because the expensive work is done asynchronously and using multiple workers, MyTA can speed up sending bulk email by a lot. 

# Installing and running

Download the latest WAR file and deploy it in your Servlet Container like Tomcat or Jetty.

# Configuration

The following environment variables can be set:

| name                | description                                            | default              |
| ------------------- | ------------------------------------------------------ | -------------------- |
| NUM_WORKERS         | the number of threads processing incoming messages     | 2                    |
| INCOMING_QUEUE_SIZE | the internal incoming message queue size               | 1000                 |
| RELAY_HOST          | the host or hosts (comma separated) to deliver mail to | localhost            |
| CONFIG_FILE         | config file (only needed for DKIM settings)            | /etc/myta/config.ini |

# DKIM signing

For DKIM signing, a configuration file is needed. The default location is /etc/myta/config.ini (can be changed by setting CONFIG_FILE environment parameter). 

An example configuration for DKIM signing (the 'dkim-mapping' header is required):

```ini
[dkim-mapping]
info@example.com = example.com,someselector,/path/to/some/key.pem
*@example.com = example.com,wildcard,/path/to/wildcard/key.pem
```

The key can either be a full email address or ``'*@example.com'`` to match all email addresses on that domain. The order does not matter, full email addresses are checked first.

The value consists of 3 parts, separated by commas:
- the domain
- the selector
- the path to the key file

# Sending mail

Example PHP code:

```php
$myTAUrl = 'http://localhost:8080/MyTA/postMessage';

$msg = array();

$msg['from'] = 'Firstname Lastname <info@example.com>';
$msg['to'] = 'Mr Recipient <recipient@example.com>';
$msg['subject'] = $schedule->getsubject();
$msg['textBody'] = 'Plaintext body';
$msg['htmlBody'] = '<html>This is the <b>HTML</b> body</html>';

$requestBody = json_encode($msg);

@file_get_contents($myTAUrl, null, stream_context_create(array(
    'http' => array(
    'method' => 'POST',
    'header' => array('Content-Type: application/json'."\r\n" . 'Content-Length: ' . strlen($requestBody) . "\r\n"),
    'content' => $requestBody))));
```

Email addresses can either be a plain email address ``'info@example.com'`` or one with a name and the email address in brackets ``'Firstname Lastname <info@example.com>'``. 

Every message requires at least one 'to' and 'from' address, a subject and either a text (or html) body. 

Here is a simple example with the least required fields:

```json
{
    "from": "info@example.com",
    "to": "example@example.com",
    "subject": "Example Subject",
    "textBody": "Simple text body"
}
```

Here is a full example of a valid json message request body, including extra recipients, extra headers and a bounce address:

```json
{
    "from": "from@example.com",
    "recipients": [
        {
            "type": "to",
            "email": "Firstname Lastname <to@example.com>"
        },
        {
            "type": "cc",
            "email": "cc@example.com"
        },
        {
            "type": "bcc",
            "email": "bcc@example.com"
        }
    ],
    "subject": "Test subject",
    "htmlBody": "This is the <b>html</b> body",
    "textBody": "This is the text body",
    "returnPath": "bounces@example.com",
    "replyTo": [
        "Firstname Lastname <replyto@example.com>",
        "reply2@example.com"
    ],
    "extraHeaders": [
        {
            "name": "Date",
            "value": "Sun, 02 May 2021 17:09:48 GMT"
        },
        {
            "name": "List-Unsubscribe",
            "value": "<mailto:unsubscribe+address-d543fd7@mailing.example.com?subject=unsubscribe>"
        },
        {
            "name": "Message-ID",
            "value": "<somemessageid@example.com>"
        }
    ]
}
```

# Performance tuning

When sending messages in bulk, MyTA will use multiple workers to generate Mime messages and deliver them at a local relay server. Once the queue reaches its maximum, incoming message requests will block until an item is freed from the queue. When this happens, you might need more workers to have more computing power to process the items in the queue. Since each worker spends some time communicating with an SMTP server, you might configure MyTA to use more workers than you have CPU cores. Use just as many to prevent the queue from filling up. To spread the outgoing mail over multiple SMTP servers, configure more than 1 server using the RELAY_HOST environment parameter.

# Monitoring

You can monitor MyTA status by requesting /MyTA/status?output=nvp to generate a plaintext status page with name/value pairs like this:

```text
status:ok numWorkers:6 incomingQueueSize:0 incomingQueueMaxSize:1000 mailsSent:19985 freeMemory:146750424 totalMemory:158334976 maxMemory:1836580864 uptime:17161
```
