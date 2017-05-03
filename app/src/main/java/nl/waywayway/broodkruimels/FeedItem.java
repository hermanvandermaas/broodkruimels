package nl.waywayway.broodkruimels;

public class FeedItem
{

    private String title;
	private String link;
    private String pubdate;
    private String creator;
    private String content;
    private String mediacontent;	
    private int mediawidth;
    private int mediaheight;
    private String mediamedium;
	private String mediatype;
	private int imgwidth;
	private int imgheight;

	public void setLink(String link)
	{
		this.link = link;
	}

	public String getLink()
	{
		return link;
	}

	public void setImgheight(int imgheight)
	{
		this.imgheight = imgheight;
	}

	public int getImgheight()
	{
		return imgheight;
	}

	public void setImgwidth(int imgwidth)
	{
		this.imgwidth = imgwidth;
	}

	public int getImgwidth()
	{
		return imgwidth;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getTitle()
	{
		return title;
	}

	public void setPubdate(String pubdate)
	{
		this.pubdate = pubdate;
	}

	public String getPubdate()
	{
		return pubdate;
	}

	public void setCreator(String creator)
	{
		this.creator = creator;
	}

	public String getCreator()
	{
		return creator;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public String getContent()
	{
		return content;
	}

	public void setMediacontent(String mediacontent)
	{
		this.mediacontent = mediacontent;
	}

	public String getMediacontent()
	{
		return mediacontent;
	}

	public void setMediawidth(int mediawidth)
	{
		this.mediawidth = mediawidth;
	}

	public int getMediawidth()
	{
		return mediawidth;
	}

	public void setMediaheight(int mediaheight)
	{
		this.mediaheight = mediaheight;
	}

	public int getMediaheight()
	{
		return mediaheight;
	}

	public void setMediamedium(String mediamedium)
	{
		this.mediamedium = mediamedium;
	}

	public String getMediamedium()
	{
		return mediamedium;
	}

	public void setMediatype(String mediatype)
	{
		this.mediatype = mediatype;
	}

	public String getMediatype()
	{
		return mediatype;
	}
}
