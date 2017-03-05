package karov.shemi.oz;

import android.content.ContentResolver;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import javax.net.ssl.HttpsURLConnection;

public class Downloader {
	public static final Charset UTF8 = Charset.forName("UTF-8");
 
    public static String downloadPostObject(String[] url) {
    	String result ="";
        try {
        	URL myurl = new URL(url[0]);
        	HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
        	conn.setReadTimeout(10000);
        	conn.setConnectTimeout(15000);
        	conn.setRequestMethod("POST");
        	conn.setDoInput(true);
        	conn.setDoOutput(true);
        	if(url.length>1){
        	Uri.Builder builder = new Uri.Builder();
        	 for(int i=1;i<url.length;i=i+2){
        		 builder .appendQueryParameter(url[i], url[i+1]);
             }
        	 String query = builder.build().getEncodedQuery();
        	 OutputStream os = conn.getOutputStream();
        	 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        	 writer.write(query);
        	 writer.flush();
        	 writer.close();
        	 os.close();
        	}
        	 //conn.connect();
        	
        	 int responseCode=conn.getResponseCode();

             if (responseCode == HttpsURLConnection.HTTP_OK) {
                 String line;
                 BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                 while ((line=br.readLine()) != null) {
                     result+=line;
                 }
             }
             else {
                 result="";

             }
         } catch (Exception e) {
             e.printStackTrace();
         }

        return result;
        }
    public static String downloadPostObject(String[] url,String sourceFileUri) {
        String fileName = sourceFileUri;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        DataOutputStream dos=null;
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);
    	FileInputStream fileInputStream = null;
    	String result ="";
        try {
        	fileInputStream = new FileInputStream(sourceFile);
        	URL myurl = new URL(url[0]);
        	HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
        	conn.setReadTimeout(10000);
        	conn.setConnectTimeout(15000);
        	conn.setRequestMethod("POST");
        	conn.setDoInput(true);
        	conn.setDoOutput(true);
        	conn.setUseCaches(false); // Don't use a Cached Copy
        	conn.setRequestProperty("Connection", "Keep-Alive");
        	conn.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);
        	conn.setRequestProperty("uploaded_file", fileName);
        	dos = new DataOutputStream(conn.getOutputStream());
        	dos.writeBytes(twoHyphens + boundary + lineEnd);
        	dos.writeBytes("Content-Disposition: form-data; name=\"name\"" + lineEnd); 
        	dos.writeBytes(lineEnd);
        	dos.writeBytes("cv file"); // mobile_no is String variable
        	dos.writeBytes(lineEnd);
        	dos.writeBytes(twoHyphens + boundary + lineEnd);
	
        	for(int i=1;i<url.length;i=i+2){
        		byte[] buf = ("Content-Disposition: form-data; name=\""+url[i]+"\"" + lineEnd+ lineEnd).getBytes("UTF-8");
        		dos.write(buf, 0, buf.length);
//        		dos.writeBytes("Content-Disposition: form-data; name=\""+url[i]+"\"" + lineEnd); 
//        		dos.writeBytes(lineEnd);
        		buf = (url[i+1] + lineEnd+twoHyphens + boundary + lineEnd).getBytes("UTF-8");
        		dos.write(buf, 0, buf.length);
//        		
//             	dos.writeBytes(url[i+1]); // mobile_no is String variable
//             	dos.writeBytes(lineEnd);
//            	dos.writeBytes(twoHyphens + boundary + lineEnd);
    	
        	}
        	byte[] buf = ("Content-Disposition: form-data; name=\"file\"; filename=\""+sourceFileUri+"\""+ lineEnd+" Content-Type: image/png" + lineEnd+ lineEnd).getBytes("UTF-8");
    		dos.write(buf, 0, buf.length);
//    		
//        	dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""+sourceFileUri+"\""+ lineEnd+" Content-Type: image/png" + lineEnd); 
//        	dos.writeBytes(lineEnd);
        	
        	// create a buffer of maximum size
        	bytesAvailable = fileInputStream.available();
        	bufferSize = Math.min(bytesAvailable, maxBufferSize);
        	buffer = new byte[bufferSize];
        	// read file and write it into form...
        	bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        	while (bytesRead > 0){
        		dos.write(buffer, 0, bufferSize);
        		bytesAvailable = fileInputStream.available();
        		bufferSize = Math.min(bytesAvailable, maxBufferSize);
        		bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        	}

        	// send multipart form data necesssary after file data...
        	dos.writeBytes(lineEnd);
        	dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


        	int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    result+=line;
                }
            }
            else {
                result="";

            }
            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
     // close the streams //
       
       return result;
    }
    

public static String downloadPostObject(String[] url,Uri sourceFileUri,ContentResolver cr,String fileName) {
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    DataOutputStream dos=null;
    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;
    int maxBufferSize = 1 * 1024 * 1024;
	InputStream fileInputStream = null;
	String result ="";
	try{
		fileInputStream = cr.openInputStream(sourceFileUri);
    	URL myurl = new URL(url[0]);
    	HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
    	conn.setReadTimeout(10000);
    	conn.setConnectTimeout(15000);
    	conn.setRequestMethod("POST");
    	conn.setDoInput(true);
    	conn.setDoOutput(true);
    	conn.setUseCaches(false); // Don't use a Cached Copy
    	conn.setRequestProperty("Connection", "Keep-Alive");
    	conn.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);
    	conn.setRequestProperty("uploaded_file", fileName);
    	dos = new DataOutputStream(conn.getOutputStream());
    	dos.writeBytes(twoHyphens + boundary + lineEnd);
    	dos.writeBytes("Content-Disposition: form-data; name=\"name\"" + lineEnd); 
    	dos.writeBytes(lineEnd);
    	dos.writeBytes("cv file"); // mobile_no is String variable
    	dos.writeBytes(lineEnd);
    	dos.writeBytes(twoHyphens + boundary + lineEnd);

    	for(int i=1;i<url.length;i=i+2){
    		byte[] buf = ("Content-Disposition: form-data; name=\""+url[i]+"\"" + lineEnd+ lineEnd).getBytes("UTF-8");
    		dos.write(buf, 0, buf.length);
//    		dos.writeBytes("Content-Disposition: form-data; name=\""+url[i]+"\"" + lineEnd); 
//    		dos.writeBytes(lineEnd);
    		buf = (url[i+1] + lineEnd+twoHyphens + boundary + lineEnd).getBytes("UTF-8");
    		dos.write(buf, 0, buf.length);
//    		
//         	dos.writeBytes(url[i+1]); // mobile_no is String variable
//         	dos.writeBytes(lineEnd);
//        	dos.writeBytes(twoHyphens + boundary + lineEnd);
	
    	}
    	byte[] buf = ("Content-Disposition: form-data; name=\"file\"; filename=\""+fileName+"\""+ lineEnd+" Content-Type: image/png" + lineEnd+ lineEnd).getBytes("UTF-8");
		dos.write(buf, 0, buf.length);
//		
//    	dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""+sourceFileUri+"\""+ lineEnd+" Content-Type: image/png" + lineEnd); 
//    	dos.writeBytes(lineEnd);
    	
    	// create a buffer of maximum size
    	bytesAvailable = fileInputStream.available();
    	bufferSize = Math.min(bytesAvailable, maxBufferSize);
    	buffer = new byte[bufferSize];
    	// read file and write it into form...
    	bytesRead = fileInputStream.read(buffer, 0, bufferSize);

    	while (bytesRead > 0){
    		dos.write(buffer, 0, bufferSize);
    		bytesAvailable = fileInputStream.available();
    		bufferSize = Math.min(bytesAvailable, maxBufferSize);
    		bytesRead = fileInputStream.read(buffer, 0, bufferSize);
    	}

    	// send multipart form data necesssary after file data...
    	dos.writeBytes(lineEnd);
    	dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


    	int responseCode=conn.getResponseCode();

        if (responseCode == HttpsURLConnection.HTTP_OK) {
            String line;
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line=br.readLine()) != null) {
                result+=line;
            }
        }
        else {
            result="";

        }
        fileInputStream.close();
        dos.flush();
        dos.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
 // close the streams //
   
   return result;
}
}

