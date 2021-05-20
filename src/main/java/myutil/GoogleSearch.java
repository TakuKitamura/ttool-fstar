/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * 
 * ludovic.apvrille AT enst.fr
 * 
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 * 
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */

package myutil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * GoogleSearch crawling from google and google Scholar with keywords Creation:
 * 11/03/2015
 * 
 * @version 1.0 11/03/2015
 * @author Huy TRUONG
 */
public class GoogleSearch {

    public static final String charset = "UTF-8";
    public static final String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0";// "Eurecom";
    public static final String google = "http://www.google.com/search?hl=en";
    public static final String googleScholar = "http://scholar.google.com/scholar?ht=en";

    public static final String ENCODING_ERROR = "encodeding_error";
    public static final String IOEx = "IOExeption";

    public static final ArrayList<GoogleSearch> getGoogleResult(String search, String num) {
        ArrayList<GoogleSearch> r = new ArrayList<GoogleSearch>();

        String title = "";
        String url = "";
        String desc = "";
        GoogleSearch gs;

        try {
            String keyword = "&q=" + URLEncoder.encode(search, charset);
            String number = "&num=" + URLEncoder.encode(num, charset);
            String googleurl = google + number + keyword;

            Document doc = Jsoup.connect(googleurl).userAgent(userAgent).get();

            // get list of search result, each result begin with tag <li class="g">
            Elements articles = doc.select("li.g");

            if (articles.size() != 0) {
                for (Element l : articles) {
                    gs = new GoogleSearch();

                    // convert an article to a html in order to using parser again.
                    String htmlArticle = l.toString();
                    Document docArticle = Jsoup.parse(htmlArticle, charset);

                    // get first tag <a href=....>

                    Elements ahrefElement = docArticle.select("a");
                    if (ahrefElement.size() != 0) {
                        // get value of tag.
                        title = ahrefElement.first().text();
                        // get value of attribute href
                        url = ahrefElement.first().attr("href");
                        // string in href has form "/url?q=http://www.... --> remove prefix.
                        url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");
                    }

                    // get description begin with tag <span class="st">
                    Elements descelement = docArticle.select("span.st");
                    if (descelement.size() != 0)
                        desc = descelement.first().text();

                    // TraceManager.addDev("Title: --> "+title);
                    // TraceManager.addDev("url: --> "+url);
                    // TraceManager.addDev("Decription: --> "+desc);

                    gs.setTitle(title);
                    gs.setUrl(url);
                    gs.setDesc(desc);

                    r.add(gs);
                }
            }
            return r;
        } catch (NullPointerException e) {
            return null;
        } catch (UnsupportedEncodingException e) {
            gs = new GoogleSearch();
            gs.setTitle(ENCODING_ERROR);
            r.add(gs);
            return r;
        } catch (IOException e) {
            gs = new GoogleSearch();
            gs.setTitle(IOEx);
            r.add(gs);
            return r;
        }
    }

    public static final ArrayList<GoogleSearch> getGoogleScholarResult(String search, String num) {
        ArrayList<GoogleSearch> r = new ArrayList<GoogleSearch>();
        String title = "";
        String url = "";
        String desc = "";
        String authors = "";
        String citedNumber = "";
        String citedLinks = "";
        String related = "";

        GoogleSearch gs;
        try {
            String keyword = "&q=" + URLEncoder.encode(search, charset);
            String number = "&num=" + URLEncoder.encode(num, charset);
            String googleScholarurl = googleScholar + number + keyword;
            Document doc = Jsoup.connect(googleScholarurl).userAgent(userAgent).get();

            // get list of search result, each result begin with tag <li class="g">
            Element error = doc.select("span.gs_red").first();
            if (error != null) {
                if (error.text().contains("Did you mean:")) {
                    String newrequest = "http://scholar.google.com" + doc.select("a.gs_pda").first().attr("href");
                    doc = Jsoup.connect(newrequest).userAgent(userAgent).get();
                }
            }

            Elements articles = doc.select("div.gs_ri");

            if (articles.size() != 0) {
                for (Element l : articles) {
                    gs = new GoogleSearch();
                    //
                    // convert an article to a html in order to using parser again.
                    String htmlArticle = l.toString();

                    Document docArticle = Jsoup.parse(htmlArticle, charset);
                    //
                    // //get first tag <a href=....>

                    Elements ahrefElement = docArticle.select("h3.gs_rt > a");
                    if (ahrefElement.size() != 0) {
                        title = ahrefElement.first().text();
                        // get value of attribute href
                        url = ahrefElement.attr("href");
                    }

                    Elements descElement = docArticle.select("div.gs_rs");
                    if (descElement.size() != 0) {
                        desc = descElement.first().text();
                    }

                    Elements authorElement = docArticle.select("div.gs_a");
                    if (authorElement.size() != 0)
                        authors = authorElement.first().text();

                    Elements inforElements = docArticle.select("div.gs_fl > a ");
                    if (inforElements.size() != 0) {
                        for (Element a : inforElements) {
                            String href = a.attr("href");
                            if (href.contains("cites")) {
                                citedNumber = a.text();
                                citedLinks = "http://scholar.google.com" + href;
                            }
                            if (href.contains("related")) {
                                related = "http://scholar.google.com" + href;
                            }
                        }
                    }
                    gs.authors = authors;
                    gs.title = title;
                    gs.url = url;
                    gs.desc = desc;
                    gs.citedLinks = citedLinks;
                    gs.citedNumber = citedNumber;

                    r.add(gs);
                }
            }
            return r;
        } catch (NullPointerException e) {
            return null;
        } catch (UnsupportedEncodingException e) {
            gs = new GoogleSearch();
            gs.setTitle(ENCODING_ERROR);
            r.add(gs);
            return r;
        } catch (IOException e) {
            gs = new GoogleSearch();
            gs.setTitle(IOEx);
            r.add(gs);
            return r;
        }
    }

    private String title;
    private String url;
    private String desc;
    private String authors;
    private String citedNumber;
    private String citedLinks;
    private String related;

    public GoogleSearch() {
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
