package crawlAndSearch;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class RepImg {

	 Map<File, Float> RepImg = new HashMap<File, Float>();
	 Map<File, Float> LogoImg = new HashMap<File, Float>();
	 Map<File, Float> BannerImg = new HashMap<File, Float>();
	Map<File, Float> AdvertImg = new HashMap<File, Float>();
	Map<File, Float> IconImg = new HashMap<File, Float>();
	
	
	 Map.Entry<File,Float> RepImgpath = null;
	 Map.Entry<File,Float> BannerImgpath = null;
	 Map.Entry<File,Float> LogoImgpath = null;
	 Map.Entry<File,Float> AdvertImgpath = null;
	 Map.Entry<File,Float> IconImgpath = null;
	
	File FinalPath= null; 
	public int imagePerURL=0;
	

	public int getImagePerURL() {
		return imagePerURL;
	}

	public void setImagePerURL(int imagePerURL) {
		this.imagePerURL = imagePerURL;
	}

	public RepImg() 
	{
    }

	//this part of code will search for the images present folder and categorize according to certain conditions
    public File GetImage(File folder) throws Exception
    {
    	int image_per_url=0;
        String Extention = null;
       for (File file : folder.listFiles()) 
        {
    	   if (file.exists())
    	   {
    		   image_per_url++;
        	String FileName=file.toString();
	        int check = FileName.lastIndexOf('.');
	        if (check > 0 && FileName!=""&& file!=null) 
	        {
	            Extention = FileName.substring(check+1);
        	if(Extention.equals("jpg")||Extention.equals("svg")||Extention.equals("png")||Extention.equals("gif"))
             {
        		CategoriseImg(file);
             }
	        
	        }
    	   }
        }
		
       setImagePerURL(image_per_url);
        getIconImg();
        getBannerImg();
        getLogoImg();
        getRepImg();
        getAdvertImg();
        FinalPath=getrepresentativeimage();
        return FinalPath;
        }
        
    // will Categorise the image as logo,banner,icon,advertisement and representative
    public void CategoriseImg(File file) throws Exception
    {
    	 try {
			BufferedImage img = ImageIO.read(file);
			float score=0;
			float height=0;
			float width=0;
			float ratio=0;
			float imgsize = 0;
	    	float maxratio=0;
	    	float minratio=0;
			if(img.getHeight()!=0)
			height=img.getHeight();
			if(img.getWidth()!=0)
			width = img.getWidth();
	    	String path= file.getAbsolutePath();
	    	String extention = path.substring(path.lastIndexOf(".") + 1);
	    	String urlFolderName = Paths.get(file.toString()).toString();
	    	Path p = Paths.get(urlFolderName);
	        
	    	String FileName=p.getFileName().toString();
	    	FileName = FileName.substring(0, FileName.lastIndexOf('.'));
	    	imgsize = height*width;
	    	maxratio=Math.max(height, width);
	    	minratio=Math.min(height, width);
	    	if(maxratio!=0){
	    	ratio=(maxratio/minratio);
	    	}
	    	
	    	
	    	//for checking the rules 
	    	if(ratio<=1.8){
	    		score=score+1;
	    	}
	    	if(imgsize>10000)
	    	{
	    		score=score+1;
	    	}
	    	if(extention=="jpg")
	    	{
	    		score=score+1;
	    	}
	    	if(extention=="svg")
	    	{
	    		score=(float) (score+0.5);
	    	}
	    	if(extention=="png")
	    	{
	    		score=(float) (score+0.5);
	    	}
	    	if(extention=="gif")
	    	{
	    		score=(float) (score+0.5);
	    	}
	    	
	    	String logo="logo";
	    	String advertisement="free, adserver,now, buy, join,click, affiliate,adv, hits, counter";
	    	String icon="background, bg,spirit, templates";
	    	String banner="banner, header,footer, button";
	    	
	    	
	    	if(FileName.contains(logo))
	    	{
	    		LogoImg.put(file, score);
	    	}
	    	else if(FileName.contains(advertisement))
	    	{
	    		AdvertImg.put(file, score);
	    	}
	    	else if(FileName.contains(icon)||width<100||height<100)
	    	{
	    		IconImg.put(file, score);
	    	}
	    	else if(FileName.contains(banner)||ratio>1.8)
	    	{
	    		BannerImg.put(file, score);
	    	}
	    	else
	    	{
	    		RepImg.put(file, score);
	    	}
	    		
	    	
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	 
    }
    
    public void getRepImg() throws Exception
    {
    	
        for(Map.Entry<File,Float> entry : RepImg.entrySet()) 
        {
            if (RepImgpath == null || entry.getValue() > RepImgpath.getValue()) {
                RepImgpath = entry;
            }
        }
        
	}
    
    public void getBannerImg() throws Exception
    {
    	
        for(Map.Entry<File,Float> entry : BannerImg.entrySet()) 
        {
            if (BannerImgpath == null || entry.getValue() > BannerImgpath.getValue()) {
                BannerImgpath = entry;
            }
        }
        
	}
    
    public void getLogoImg() throws Exception
    {
    	
        for(Map.Entry<File,Float> entry : LogoImg.entrySet()) 
        {
            if (LogoImgpath == null || entry.getValue() > LogoImgpath.getValue()) {
                LogoImgpath = entry;
            }
        }
        
	}
    
    public void getAdvertImg() throws Exception
    {
    	
        for(Map.Entry<File,Float> entry : AdvertImg.entrySet()) 
        {
            if (AdvertImgpath == null || entry.getValue() > AdvertImgpath.getValue()) {
                AdvertImgpath = entry;
            }
        }
        
	}
    
    public void getIconImg() throws Exception
    {
    	
        for(Map.Entry<File,Float> entry : IconImg.entrySet()) 
        {
            if (IconImgpath == null || entry.getValue() > IconImgpath.getValue()) {
                IconImgpath = entry;
            }
        }
        
	}
    
    public File getrepresentativeimage() throws Exception
    {
    	if(RepImgpath!=null)
    	{
    		FinalPath =RepImgpath.getKey();
    	}
    	
    	else if(BannerImgpath!=null)
    	{
    		FinalPath =BannerImgpath.getKey();
    	}
    	else if(LogoImgpath!=null)
    	{
    		FinalPath =LogoImgpath.getKey();
    	}
    	else if(AdvertImgpath!=null)
    	{
    		FinalPath =AdvertImgpath.getKey();
    	}
    	else if(IconImgpath!=null)
    	{
    		FinalPath =IconImgpath.getKey();
    	}
    	
    	
    	return FinalPath;
    	
    }
    
    
    	public File ReprensatativeImage(String title,String contextPath) throws Exception
    	{ 
    		try {
    	String Folder_Title=null;
        RepImg RepresentativeImg = new RepImg();
		Path crawlerPath = Paths.get(contextPath,"pics","TempCrawl");
        String splChrs = "-/@#$%^&_+=():.| " ;
		title = title.replaceAll(splChrs, "");
		title = title.replaceAll("[\\-\\+\\.\\^:,|\"\\?$]", "");
       
		
		Folder_Title = Paths.get(crawlerPath.toString(),title).toString();
		File FolderPathfile=new File(Folder_Title);
		if(FolderPathfile.exists())
		{
			FinalPath=RepresentativeImg.GetImage(FolderPathfile);
		}		
    	}
    		catch(NullPointerException ne){
    			ne.printStackTrace();
    			System.err.println("Null Pointer Exception");
    		}
    		return FinalPath;
    }
}