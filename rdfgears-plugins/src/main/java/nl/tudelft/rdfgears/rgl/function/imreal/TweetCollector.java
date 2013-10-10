package nl.tudelft.rdfgears.rgl.function.imreal;

/*
 * #%L
 * RDFGears
 * %%
 * Copyright (C) 2013 WIS group at the TU Delft (http://www.wis.ewi.tudelft.nl/)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.io.BufferedReader;
import java.io.File;

import nl.tudelft.rdfgears.engine.Config;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import nl.tudelft.rdfgears.engine.Config;
import nl.tudelft.rdfgears.engine.Engine;
import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.type.BagType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.function.SimplyTypedRGLFunction;
import nl.tudelft.rdfgears.rgl.function.imreal.vocabulary.USEM;
import nl.tudelft.rdfgears.rgl.function.imreal.vocabulary.WI;
import nl.tudelft.rdfgears.rgl.function.imreal.vocabulary.WO;
import nl.tudelft.rdfgears.util.row.ValueRow;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDF;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * A class that either retrieves tweets from file or from the Twitter stream
 * (ideally not file based but DB based). The filename is the twitter user name.
 * 
 * if includeRetweets==true, retweets are included, otherwise they are ignored
 * 
 * maxHoursAllowedOld indicates how old in hours the stored data is allowed to be before it is overwritten.
 * 
 * @author Claudia
 * 
 */
public class TweetCollector 
{
	
	private static final String TWITTER_DATA_FOLDER = Config.getWritableDir()+"twitterData";
	private static DocumentBuilder docBuilder;
	
	static
	{
		try
		{
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static HashMap<String,String> getTweetTextWithDateAsKey(String twitterUsername, boolean includeRetweets, int maxHoursAllowedOld)
	{
		try
		{
			File twitterDataFolder = new File(TWITTER_DATA_FOLDER);
			if(!twitterDataFolder.exists())
				twitterDataFolder.mkdirs();
			
			File f = new File(TWITTER_DATA_FOLDER+"/"+twitterUsername);
			int hours = -1;
			if(f.exists()==true)
			{
				long lastModified = f.lastModified();
				
				long diff = System.currentTimeMillis()-lastModified;
				
				int seconds = (int)(diff/1000L);
				int minutes = seconds/60;
				hours = minutes/60;
				
				
				//if the file is nearly empty, retrieve it again anyway
				if(f.length()<100)
				{
					System.err.println("(Nearly) empty file found, retrieving again ....");
					hours=-1;
				}
			}
			
			/*
			 * if we do not have data yet (or it is too old), retrieve it and store it in a folder
			 */
			if(hours>maxHoursAllowedOld || hours<0)
			{
				String getTweetsURL = "https://api.twitter.com/1/statuses/user_timeline.xml?include_entities=false&include_rts=true&screen_name="+ twitterUsername + "&count=200";
					
				//TODO: only overwrite the original file if we actually manage to get hold of something from Twitter ..
				BufferedWriter bw = new BufferedWriter(new FileWriter(f.toString()));
				URL url = new URL(getTweetsURL);
				Engine.getLogger().debug("In TweetCollector, retrieving live tweets for " + url.toString());
				Engine.getLogger().debug("hours computed: "+hours+", maxHoursAllowedOld: "+maxHoursAllowedOld);

				BufferedReader in = new BufferedReader(new InputStreamReader(
						url.openStream()));

				String inputLine;
				while ((inputLine = in.readLine()) != null) 
				{
					bw.write(inputLine);
					bw.newLine();
				}
				in.close();
				bw.close();
			}
			
			return getTweetTextWithDateAsKeyFromFile(f,includeRetweets);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return new HashMap<String, String>();
	}
	
	
	private static String getTagValue(String tag, Element el) 
	{
	    NodeList list = el.getElementsByTagName(tag).item(0).getChildNodes();

	    Node val = (Node) list.item(0);
        return val.getNodeValue();
	}

	
	/*
	 * method returns up to the last 200 tweets, ignoring RTs
	 * key: created_at
	 * value: tweet text
	 */
	private static HashMap<String,String> getTweetTextWithDateAsKeyFromFile(File f, boolean includeRetweets)
	{
		HashMap<String, String> tweetMap = new HashMap<String, String>();
		
		try
		{
			Document d = docBuilder.parse(new FileInputStream(f));
			NodeList statuses = d.getElementsByTagName("status");
			
			for(int i=0; i<statuses.getLength(); i++)
			{
				Node status = statuses.item(i);
	            if (status.getNodeType() == Node.ELEMENT_NODE) 
	            {
	                Element el = (Element) status;
	
	                String creationDate = getTagValue("created_at",el);
	                String text = getTagValue("text",el);
	                
	                //is it a retweet?
	                NodeList retweetList = el.getElementsByTagName("retweeted_status");
	                if(retweetList.getLength()>0)
	                {
	                	if(includeRetweets==false)
	                		text="";
	                	else
	                	{
	                		Node retweet = retweetList.item(0);
	                		text = getTagValue("text",(Element)retweet);
	                	}
	                }

	                if(text.equals("")==false)
	                	tweetMap.put(creationDate,text);
	            }
			}
			System.err.println("Number of status tweets: "+statuses.getLength());
			System.err.println("Number of tweets serviced: "+tweetMap.size()+" (includeRetweets="+includeRetweets+")");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return tweetMap;
	}	

}
