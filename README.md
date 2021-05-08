# MyTA

MyTA is a Mail Transfer Agent with an HTTP API. It takes mail messages in a simple JSON format, then composes valid Mime multipart email and delivers to a local SMTP server.

Because the expensive work is done asynchronously and using multiple workers, MyTA can speed up sending bulk mail. 

# DKIM

DKIM is not implemented yet. High on the TODO list.

