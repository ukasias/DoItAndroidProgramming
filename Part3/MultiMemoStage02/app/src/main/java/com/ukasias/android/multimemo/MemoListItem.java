package com.ukasias.android.multimemo;

public class MemoListItem {
    // id
    private String mId;

    // 이 아이템이 선택 가능한가.
    private boolean mSelectable;

    // 각종 data
    private String[] mData;

    public MemoListItem(String id, String[] data) {
        mId = id;
        mData = data;
    }

    public MemoListItem(String id, String date, String text,
                        String id_photo, String uri_photo,
                        String id_video, String uri_video,
                        String id_voice, String uri_voice,
                        String id_handwriting, String uri_handwriting) {
        mId = id;
        mData = new String[10];
        mData[0] = date;
        mData[1] = text;
        mData[2] = id_photo;
        mData[3] = uri_photo;
        mData[4] = id_video;
        mData[5] = uri_video;
        mData[6] = id_voice;
        mData[7] = uri_voice;
        mData[8] = id_handwriting;
        mData[9] = uri_handwriting;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public boolean getSelectable() {
        return mSelectable;
    }

    public void setSelectable(boolean selectable) {
        mSelectable = selectable;
    }

    public String[] getData() {
        return mData;
    }

    public String getData(int index) {
        if (mData == null || index >= mData.length) {
            return null;
        }

        return mData[index];
    }

    public void setData(String[] data) {
        mData = data;
    }

    public int compareTo(MemoListItem other) {
        if (mData != null) {
            Object[] otherData = other.getData();
            if (mData.length == otherData.length) {
                for (int i = 0; i < mData.length; i++) {
                    if (!mData[i].equals(otherData[i])) {
                        return -1;
                    }
                }
            }
            else {
                return -1;
            }
        }
        else {
            throw new IllegalArgumentException();
        }

        return 0;
    }
}

