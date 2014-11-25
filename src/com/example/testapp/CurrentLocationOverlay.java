package com.example.testapp;

public class CurrentLocationOverlay {
	private String mLabel;
    private String mIcon;
    private String mDistance;
    private Double mLatitude;
    private Double mLongitude;

    public CurrentLocationOverlay(String label, String icon, String distance, Double latitude, Double longitude)
    {
        this.mLabel = label;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mIcon = icon;
        this.mDistance = distance;
    }

    public String getmLabel()
    {
        return mLabel;
    }

    public void setmLabel(String mLabel)
    {
        this.mLabel = mLabel;
    }

    public String getmIcon()
    {
        return mIcon;
    }

    public void setmIcon(String icon)
    {
        this.mIcon = icon;
    }

    public Double getmLatitude()
    {
        return mLatitude;
    }

    public void setmLatitude(Double mLatitude)
    {
        this.mLatitude = mLatitude;
    }

    public Double getmLongitude()
    {
        return mLongitude;
    }

    public void setmLongitude(Double mLongitude)
    {
        this.mLongitude = mLongitude;
    }

	public String getmDistance() {
		return mDistance;
	}

	public void setmDistance(String mDistance) {
		this.mDistance = mDistance;
	}
}

