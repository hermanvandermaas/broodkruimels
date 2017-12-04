package nl.waywayway.broodkruimels;

public class MakeImageUrl
{
	private int imgWidth;
	private int imgHeight;
	private int urlWidth;
	private String imageUrl;

	public MakeImageUrl(int imgWidth, int imgHeight, int urlWidth, String imageUrl)
	{
		this.imgWidth = imgWidth;
		this.imgHeight = imgHeight;
		this.urlWidth = urlWidth;
		this.imageUrl = imageUrl;
	}
	
	public String make(Boolean secondTry)
	{
		// Maak juiste URL voor downloaden grote afbeelding
		Boolean sizeKnown = (imgWidth > 0 && imgHeight > 0);
		if (secondTry) sizeKnown = false;
		String orientation = (imgWidth > imgHeight) ? ("landscape") : ("portrait");

		// Maak deel van URL met afmetingen van afbeelding
		Float aspectRatio = (float) imgHeight / imgWidth;
		int urlHeight = Math.round(urlWidth * aspectRatio);
		String urlDimensions = "-" + String.valueOf(urlWidth) + "x" + String.valueOf(urlHeight);

		if (!sizeKnown)
		{
			// Als afmetingen niet bekend, of als
			// dit tweede poging is voor downloaden afbeelding,
			// oorspronkelijke afbeelding downloaden,
			// mogelijk erg grote afbeelding
			urlDimensions = "";
		}

		String regex = "(?i)(.+)(-\\d+x\\d+)(\\.jpg|\\.jpeg|\\.png)";
		imageUrl = imageUrl.replaceAll(regex, "$1" + urlDimensions + "$3");

		/*
		 Log.i("HermLog", "mOrientation: " + mOrientation);
		 Log.i("HermLog", "2e poging: " + secondTry);
		 Log.i("HermLog", "mSizeknown: " + mSizeKnown);
		 Log.i("HermLog", "mUrlWidth: " + mUrlWidth);
		 Log.i("HermLog", "mUrlHeight: " + mUrlHeight);
		 Log.i("HermLog", "mAspectratio: " + mAspectRatio);			
		 Log.i("HermLog", "mImgWidth: " + mImgWidth);
		 Log.i("HermLog", "mImgHeight: " + mImgHeight);
		 Log.i("HermLog", "mImageUrl: " + mImageUrl);
		 */

		return imageUrl;
	}
}
