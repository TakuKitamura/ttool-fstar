package myutil;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.jsoup.nodes.Document;
import myutil.TraceManager;

public class GoogleSearch {
	
	public static final String charset = "UTF-8";
	public static final String userAgent = "Eurecom";
	public static final String google = "http://www.google.com/search?hl=en&q=";
	public static final String googleScholar="http://scholar.google.com/scholar?ht=en&q=";
	
	
	public static final ArrayList<GoogleSearch> getGoogleResult(String search) throws UnsupportedEncodingException, IOException{
		ArrayList<GoogleSearch> r = new ArrayList<GoogleSearch>();
		
		String title="" ;
		String url ="" ;
		String desc="";
		GoogleSearch gs;
		
		Document doc = Jsoup.connect(google + URLEncoder.encode(search, charset)).userAgent(userAgent).get();
		
		//System.out.println(doc.toString());
		
//		Element error = doc.select("span.gs_red").first();
//		if (error.text().contains("Did you mean:")){
//			String newrequest = "http://scholar.google.com" + doc.select("a.gs_pda").first().attr("href");
//			doc = Jsoup.connect(newrequest).userAgent(userAgent).get();
//		}	
		
		//get list of search result, each result begin with tag <li class="g">
		Elements articles = doc.select("li.g");
		//System.out.println(articles.toString());
		
		
		if (articles.size()!=0){
			for (Element l : articles){
				gs = new GoogleSearch();
				
				//convert an article to a html in order to using parser again.
				String htmlArticle = l.toString();
				Document docArticle = Jsoup.parse(htmlArticle);
				
				//get first tag <a href=....>
				
				Elements ahrefElement = docArticle.select("a");
				if (ahrefElement.size()!=0){
					//get value of tag.
					title = ahrefElement.first().text();
					//get value of attribute href
					url= ahrefElement.first().attr("href");
					//string in href has form "/url?q=http://www.... --> remove prefix.  
					url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");
				}
          
				 
				//get description begin with tag <span class="st">
				Elements descelement = docArticle.select("span.st");
				if (descelement.size()!=0)
					desc = descelement.first().text();
				
				//TraceManager.addDev("Title: --> "+title);
				//TraceManager.addDev("url: --> "+url);
				//TraceManager.addDev("Decription: --> "+desc);
				
				 gs.setTitle(title);
				 gs.setUrl(url);
				 gs.setDesc(desc);

				 r.add(gs);
			}
		}
		return r;
	}
	
	
	public static final ArrayList<GoogleSearch> getGoogleScholarResult(String search) throws UnsupportedEncodingException, IOException{
		try{
		ArrayList<GoogleSearch> r = new ArrayList<GoogleSearch>();
		String title="";
		String url="";
		String desc="";
		String authors="";
		String citedNumber="";
        String citedLinks="";
        String related="";
        
        GoogleSearch gs;
		
		//SSystem.out.println("call this");
		Document doc = Jsoup.connect(googleScholar + URLEncoder.encode(search, charset)).userAgent(userAgent).get();
		
		//System.out.println(doc.toString());
		//get list of search result, each result begin with tag <li class="g">
		Element error = doc.select("span.gs_red").first();
		if (error.text().contains("Did you mean:")){
			String newrequest = "http://scholar.google.com" + doc.select("a.gs_pda").first().attr("href");
			doc = Jsoup.connect(newrequest).userAgent(userAgent).get();
		}	
		
		Elements articles = doc.select("div.gs_ri");
		
		//System.out.println("=================================");
		//System.out.println(articles.toString());
		
			if(articles.size()!=0){
			for (Element l : articles){
				gs = new GoogleSearch();
	//			
				//convert an article to a html in order to using parser again.
				String htmlArticle = l.toString();
			
				Document docArticle = Jsoup.parse(htmlArticle);
	//			
	//			//get first tag <a href=....>
				
				Elements ahrefElement = docArticle.select("h3.gs_rt > a");
				if (ahrefElement.size()!=0){
					 title= ahrefElement.first().text();
					//get value of attribute href
					 url = ahrefElement.attr("href"); 
				}
			
	
				Elements descElement = docArticle.select("div.gs_rs");
				if (descElement.size()!=0){
					desc = descElement.first().text();
				}
	           
				
	            Elements authorElement = docArticle.select("div.gs_a");
	            if (authorElement.size()!=0)
	            	authors = authorElement.first().text();
	            
	          
	            Elements inforElements = docArticle.select("div.gs_fl > a ");
	            if(inforElements.size()!=0)
	            {
	            	for (Element a : inforElements){
	                	String href = a.attr("href");
	                	if (href.contains("cites")){
	                		 citedNumber = a.text();
	                		 citedLinks = "http://scholar.google.com" +href;
	                	}
	                	if (href.contains("related")){
	                		related = "http://scholar.google.com" +href;
	                	}
	                }
	            }
            
            
	            //TraceManager.addDev("title-->"+title);
	            //TraceManager.addDev("url-->"+url);
	            //TraceManager.addDev("desc-->"+desc);
	            //TraceManager.addDev("author-->"+authors);
	            //TraceManager.addDev("cited number-->"+citedNumber);
	            //TraceManager.addDev("cited link-->"+citedLinks);
	            //TraceManager.addDev("related link-->"+related);
	            
	            gs.authors=authors;
	            gs.title=title;
	            gs.url=url;
	            gs.desc=desc;
	            gs.citedLinks=citedLinks;
	            gs.citedNumber=citedNumber;
				
	            r.add(gs);
			}
		}
		return r;
		}catch (NullPointerException e) {
			return null;
		}
	}
	
	
	private String title;
	private String url;
	private String desc;
	private String authors;
	private String citedNumber;
	private String citedLinks;
	private String related;
	
	
	
	public GoogleSearch(){
	}

	public String getTitle() {
		return title;
	}


	public String getUrl() {
		return url;
	}

	public String getDesc() {
		return desc;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public String getAuthors() {
		return authors;
	}


	public void setAuthors(String authors) {
		this.authors = authors;
	}


	public String getCitedNumber() {
		return citedNumber;
	}


	public void setCitedNumber(String citedNumber) {
		this.citedNumber = citedNumber;
	}


	public String getCitedLinks() {
		return citedLinks;
	}


	public void setCitedLinks(String citedLinks) {
		this.citedLinks = citedLinks;
	}
}
