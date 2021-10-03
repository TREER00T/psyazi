package ir.treeroot.psyazi.model;

import android.content.Context;

public class Message {

    Context Context;
    String message, Format, timezone, from, to, groupByTime;

    public Message(Context context, String message, String format, String timezone, String groupByTime) {

        this.Context = context;
        this.message = message;
        this.Format = format;
        this.timezone = timezone;
        this.groupByTime = groupByTime;
    }


    public String getGroupByTime() {
        return groupByTime;
    }

    public void setGroupByTime(String groupByTime) {
        this.groupByTime = groupByTime;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }


    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }


    public void setMessage(String message) {
        this.message = message;
    }

    public Context getContext() {
        return Context;
    }

    public String getMessage() {
        return message;
    }

    public String getFormat() {
        return Format;
    }

    public String getTimeZone() {
        return timezone;
    }


}
