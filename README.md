# junk
Just some random stuff that I do for fun sometimes. This is just small things that usually don't do too much.

# FixHandshakeRSS.java
This is a program that takes a String url of a rss feed and returns the title and startdate of each event. It deletes the first title field since this is not an event. Because Handshake provides both the title of the event and date information in the title field of the rss feed, that field is used to get the needed information. The title field is split into titles and String dates which are then converted to longs representing unix timestamps with millisecond accuracy.
Example: FixHandshakeRSS.fixThis("https://auburn.joinhandshake.com/external_feeds/18121/public.rss?token=Ylf_yi4c2ZhdXpRRXY3QREKBlXgYajmtezFiXs6YanbJUpBUa7QODA")
returns:
<title>Co-op + Internship Interest Meeting (Virtual) Week 1</title>
<startdate>1691960400000</startdate>
<title>Co-op + Internship Interest Meeting (In-Person) Week 1</title>
<startdate>1692205200000</startdate>
...
as of 07/20/2023.
