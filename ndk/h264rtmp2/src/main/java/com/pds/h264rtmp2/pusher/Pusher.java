package com.pds.h264rtmp2.pusher;

/**
 * Created by david on 2017/10/11.
 */

public  abstract class Pusher {
    boolean isPushing = false;
    public abstract void startPush();

    public abstract void stopPush();

    public abstract void relase();


}
