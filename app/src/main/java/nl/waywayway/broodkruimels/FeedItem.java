package nl.waywayway.broodkruimels;

// Model definitie voor gebruik met Gson library

public class FeedItem
{
    private String title;
	private String link;
    private String pubDate;
    private String creator;
	private transient int guid;
	private transient String description;
    private String content;
    private String mediacontent;	
    private int mediawidth;
    private int mediaheight;
    private String mediamedium;
	private String mediatype;
	private transient String mediacopyright;
	private int imgwidth;
	private int imgheight;
	private transient String categories;
	private transient String tags;

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getTitle()
	{
		return title;
	}

	public void setLink(String link)
	{
		this.link = link;
	}

	public String getLink()
	{
		return link;
	}

	public void setPubDate(String pubDate)
	{
		this.pubDate = pubDate;
	}

	public String getPubDate()
	{
		return pubDate;
	}

	public void setCreator(String creator)
	{
		this.creator = creator;
	}

	public String getCreator()
	{
		return creator;
	}

	public void setGuid(int guid)
	{
		this.guid = guid;
	}

	public int getGuid()
	{
		return guid;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getDescription()
	{
		return description;
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

	public void setMediacopyright(String mediacopyright)
	{
		this.mediacopyright = mediacopyright;
	}

	public String getMediacopyright()
	{
		return mediacopyright;
	}

	public void setImgwidth(int imgwidth)
	{
		this.imgwidth = imgwidth;
	}

	public int getImgwidth()
	{
		return imgwidth;
	}

	public void setImgheight(int imgheight)
	{
		this.imgheight = imgheight;
	}

	public int getImgheight()
	{
		return imgheight;
	}

	public void setCategories(String categories)
	{
		this.categories = categories;
	}

	public String getCategories()
	{
		return categories;
	}

	public void setTags(String tags)
	{
		this.tags = tags;
	}

	public String getTags()
	{
		return tags;
	}
}
