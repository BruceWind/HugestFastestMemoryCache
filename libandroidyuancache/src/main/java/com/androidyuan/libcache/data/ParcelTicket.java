package com.androidyuan.libcache.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.androidyuan.libcache.core.BaseTicket;
import com.androidyuan.libcache.core.BytesTransform;

import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;

public class ParcelTicket extends BaseTicket<Parcelable> {
    private final Class parcelCls;
    private Parcelable parcelable;
    private int size = 0;

    public ParcelTicket(Parcelable p) {
        if (p == null) throw new NullPointerException("parameter: p was null.");
        this.parcelable = p;
        parcelCls = p.getClass();
    }

    @Override
    public ByteBuffer toNativeBuffer() {
        byte[] data = BytesTransform.marshallParcelable(parcelable);
        ByteBuffer temp = ByteBuffer.allocateDirect(data.length);
        temp.limit(data.length);
        size = data.length;
        temp.put(data);
        return temp;
    }

    @Override
    public byte[] toBytes() {
        return BytesTransform.marshallParcelable(parcelable);
    }

    @Override
    public void emptyData() {
        parcelable = null;
    }

    @Override
    public void resume() {
        Parcel parcel = BytesTransform.unmarshallToParcelable(buffer, getSize());
        try {
            Constructor constructor = parcelCls.getDeclaredConstructor(Parcel.class);
            constructor.setAccessible(true);//avoid private constructor.
            parcelable = (Parcelable) constructor.newInstance(parcel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        buffer.clear();
    }

    @Override
    public Parcelable getBean() {
        return parcelable;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void resumeFromDisk(byte[] data) {
        Parcel parcel = BytesTransform.unmarshallToParcelable(data);
        try {
            Constructor constructor = parcelCls.getDeclaredConstructor(Parcel.class);
            constructor.setAccessible(true);//avoid private constructor.
            parcelable = (Parcelable) constructor.newInstance(parcel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
