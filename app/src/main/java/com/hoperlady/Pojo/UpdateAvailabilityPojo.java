package com.hoperlady.Pojo;

public class UpdateAvailabilityPojo {

    private int countPosition = 0;
    private boolean slotPosition = false;
    private RegistrastionAvailabilityChildPojo pojo;
    private MyProfileAvailabilityShowPojo myProfileAvailabilityShowPojo;

    public MyProfileAvailabilityShowPojo getMyProfileAvailabilityPojo() {
        return myProfileAvailabilityShowPojo;
    }

    public void setMyProfileAvailabilityPojo(MyProfileAvailabilityShowPojo myProfileAvailabilityPojo) {
        this.myProfileAvailabilityShowPojo = myProfileAvailabilityPojo;
    }



    public RegistrastionAvailabilityChildPojo getPojo() {
        return pojo;
    }

    public void setPojo(RegistrastionAvailabilityChildPojo pojo) {
        this.pojo = pojo;
    }

    public int getCountPosition() {
        return countPosition;
    }

    public void setCountPosition(int countPosition) {
        this.countPosition = countPosition;
    }

    public boolean isSlotPosition() {
        return slotPosition;
    }

    public void setSlotPosition(boolean slotPosition) {
        this.slotPosition = slotPosition;
    }
}
