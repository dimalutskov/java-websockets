package com.baeldung.websocket;

public class UserMovement {

    private float mCurX;
    private float mCurY;
    private int mRequiredAngle;
    private float mSpeed;

    // Adjustments
    long mFullRotationTime; // Time to change angle on 360 degrees
    float mSpeedChangeValue; // Max speed value which can be accelerated/decelerated value in 1 second

    float mLastPassedDistance;
    float mTotalPassedDistance;

    // Keeps location values in float for calculating
    float mCurrentAngle;
    float mCurrentSpeed;

    long mLastStepTime;

    void setAngle(int angle) {
        mRequiredAngle = angle;
        if (mFullRotationTime == 0) {
            // Change angle instantly otherwise it will be changed according to mChangeAngleTime
            mCurrentAngle = angle;
        }
    }

    void step(long time) {
        if (mLastStepTime == 0 || mSpeed == 0) {
            mLastStepTime = time;
            return;
        }

        calculateCurrentAngle(time);
        calculateCurrentSpeed(time);

        float angle = mCurrentAngle - 90;
        mLastPassedDistance = mCurrentSpeed * ((time - mLastStepTime) / 1000.0f);

        float resultDX = (float) (mLastPassedDistance * Math.cos(Math.toRadians(angle)));
        float resultDY = (float) (mLastPassedDistance * Math.sin(Math.toRadians(angle)));

        mCurX += resultDX;
        mCurY += resultDY;

        mTotalPassedDistance += mLastPassedDistance;
    }

    private void calculateCurrentSpeed(long time) {
        if (mSpeed == mCurrentSpeed) return;

        if (mSpeedChangeValue <= 0) {
            mCurrentSpeed = mSpeed;
        } else {
            long passedTime = time - mLastStepTime;
            float speedChanges = mSpeedChangeValue * (passedTime / 1000.0f);
            if (mSpeed < mCurrentSpeed) {
                mCurrentSpeed -= speedChanges;
                if (mCurrentSpeed < mSpeed) mCurrentSpeed = mSpeed;
            } else {
                mCurrentSpeed += speedChanges;
                if (mCurrentSpeed > mSpeed) mCurrentSpeed = mSpeed;
            }
        }
    }

    private void calculateCurrentAngle(long time) {
        if (mRequiredAngle == mCurrentAngle) return;

        if (mFullRotationTime == 0) {
            mCurrentAngle = mRequiredAngle;
        } else {
            // Calculate angle changes
            float angleDiff = mRequiredAngle - mCurrentAngle;
            if (Math.abs(angleDiff) > 180) {
                angleDiff = 180 - angleDiff;
            }
            long passedTime = time - mLastStepTime;
            float changeAngleTime = (Math.abs(angleDiff) / 360.0f) * mFullRotationTime;
            float progress = passedTime / changeAngleTime;
            if (progress > 1) progress = 1;
            float angleChanges = angleDiff * progress;

            mCurrentAngle = MathUtils.validateAngle(mCurrentAngle + angleChanges);;
        }
    }

}