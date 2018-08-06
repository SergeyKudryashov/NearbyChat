package com.ss.nearby.model;

public class Endpoint {

    private String mId;
    private String mName;

    public Endpoint(String id, String name) {
        mId = id;
        mName = name;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        for (int i = 0; i < mId.length(); i++) {
            result = result * prime + mId.charAt(i);
        }

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        Endpoint other = (Endpoint) obj;

        return mId == null ? other.mId == null : mId.equals(other.mId);
    }
}
