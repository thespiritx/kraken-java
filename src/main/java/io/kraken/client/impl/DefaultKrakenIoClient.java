/**
 * Copyright (C) 2015 Nekkra UG (oss@kraken.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.kraken.client.impl;

import io.kraken.client.KrakenIoClient;
import io.kraken.client.exception.KrakenIoException;
import io.kraken.client.exception.KrakenIoRequestException;
import io.kraken.client.model.Auth;
import io.kraken.client.model.AuthWrapper;
import io.kraken.client.model.ResponseBody;
import io.kraken.client.model.request.AbstractUploadRequest;
import io.kraken.client.model.request.AbstractUploadSetRequest;
import io.kraken.client.model.request.DirectFileUploadCallbackUrlRequest;
import io.kraken.client.model.request.DirectFileUploadCallbackUrlSetRequest;
import io.kraken.client.model.request.DirectFileUploadRequest;
import io.kraken.client.model.request.DirectFileUploadSetRequest;
import io.kraken.client.model.request.DirectUploadCallbackUrlRequest;
import io.kraken.client.model.request.DirectUploadCallbackUrlSetRequest;
import io.kraken.client.model.request.DirectUploadRequest;
import io.kraken.client.model.request.DirectUploadSetRequest;
import io.kraken.client.model.request.ImageUrlUploadCallbackUrlRequest;
import io.kraken.client.model.request.ImageUrlUploadCallbackUrlSetRequest;
import io.kraken.client.model.request.ImageUrlUploadRequest;
import io.kraken.client.model.request.ImageUrlUploadSetRequest;
import io.kraken.client.model.response.AbstractUploadResponse;
import io.kraken.client.model.response.FailedUploadResponse;
import io.kraken.client.model.response.SuccessfulUploadCallbackUrlResponse;
import io.kraken.client.model.response.SuccessfulUploadCallbackUrlSetResponse;
import io.kraken.client.model.response.SuccessfulUploadResponse;
import io.kraken.client.model.response.SuccessfulUploadSetResponse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author Clinton LaForest
 * @since 1.1.2
 */
public class DefaultKrakenIoClient implements KrakenIoClient {
	
	private static final Logger log = LoggerFactory.getLogger(DefaultKrakenIoClient.class);
	
	private static final String DEFAULT_BASE_URL = "https://api.kraken.io";
    private static final String DIRECT_UPLOAD_ENDPOINT = "{0}/v1/upload";
    private static final String IMAGE_URL_ENDPOINT = "{0}/v1/url";
    private static final String DATA_PART = "data";
    private static final String UPLOAD_PART = "upload";
    private static final String ENCODING_TYPE = "UTF-8";
    
    private static final String FILE_ATTR_NAME = "filename";
    private static final String FILE_ATTR_TYPE = "mimetype";
    
    private static final String FILE_NAME_DEFAULT = "temp.ext";
    private static final String FILE_TYPE_DEFAULT = ContentType.APPLICATION_OCTET_STREAM.getMimeType();
    
    private final ObjectMapper mapper;
    private final String apiKey;
    private final String apiSecret;
    private final String directUploadUrl;
    private final String imageUrl;

    public DefaultKrakenIoClient(String apiKey, String apiSecret) {
        this(apiKey, apiSecret, DEFAULT_BASE_URL);
    }
    
    public DefaultKrakenIoClient(String apiKey, String apiSecret, String baseUrl) {
    	//checkNotNull(apiKey, "apiKey must not be null");
        //checkArgument(!apiKey.isEmpty(), "apiKey must not be empty");
        //checkNotNull(apiSecret, "apiSecret must not be null");
        //checkArgument(!apiSecret.isEmpty(), "apiSecret must not be empty");
        //checkNotNull(baseUrl, "baseUrl must not be null");
        //checkArgument(!baseUrl.isEmpty(), "baseUrl must not be empty");

        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.directUploadUrl = MessageFormat.format(DIRECT_UPLOAD_ENDPOINT, baseUrl);
        this.imageUrl = MessageFormat.format(IMAGE_URL_ENDPOINT, baseUrl);
        this.mapper = createObjectMapper();
    }
    
	@Override
	public SuccessfulUploadResponse directUpload(
			DirectUploadRequest directUploadRequest) {
		Map<String, String> attrs = new HashMap<String,String>();
		return directUpload(directUploadRequest, directUploadRequest.getImage(), attrs);
	}

	@Override
	public SuccessfulUploadSetResponse directUpload(
			DirectUploadSetRequest directUploadSetRequest) {
		Map<String, String> attrs = new HashMap<String,String>();
		return directUpload(directUploadSetRequest, directUploadSetRequest.getImage(), attrs);
	}

	@Override
	public SuccessfulUploadResponse directUpload(
			DirectFileUploadRequest directFileUploadRequest) {
		
		Map<String, String> attrs = new HashMap<String,String>();
		String fn = directFileUploadRequest.getImage().getName();
		Path path = directFileUploadRequest.getImage().toPath();
		attrs.put(FILE_ATTR_NAME, fn);
		try {
			attrs.put(FILE_ATTR_TYPE, Files.probeContentType(path));
		} catch (IOException e1) {
			attrs.put(FILE_ATTR_TYPE, FILE_TYPE_DEFAULT);
			log.error(e1.getLocalizedMessage());
		}
		
		InputStream is = null;
		try {
			is = new FileInputStream(directFileUploadRequest.getImage());
		} catch (FileNotFoundException e) {
			log.error(e.getLocalizedMessage());
		}
		return directUpload(directFileUploadRequest, is, attrs);
	}

	@Override
	public SuccessfulUploadSetResponse directUpload(
			DirectFileUploadSetRequest directFileUploadSetRequest) {
		
		Map<String, String> attrs = new HashMap<String,String>();
		String fn = directFileUploadSetRequest.getImage().getName();
		Path path = directFileUploadSetRequest.getImage().toPath();
		attrs.put(FILE_ATTR_NAME, fn);
		try {
			attrs.put(FILE_ATTR_TYPE, Files.probeContentType(path));
		} catch (IOException e1) {
			attrs.put(FILE_ATTR_TYPE, FILE_TYPE_DEFAULT);
			log.error(e1.getLocalizedMessage());
		}
		
		InputStream is = null;
		try {
			is = new FileInputStream(directFileUploadSetRequest.getImage());
		} catch (FileNotFoundException e) {
			log.error(e.getLocalizedMessage());
		}
		return directUpload(directFileUploadSetRequest, is, attrs);
	}

	@Override
	public SuccessfulUploadResponse imageUrlUpload(
			ImageUrlUploadRequest imageUrlUploadRequest) {
		return handleResponse(handleImageRequest(imageUrlUploadRequest));
	}

	@Override
	public SuccessfulUploadSetResponse imageUrlUpload(
			ImageUrlUploadSetRequest imageUrlUploadSetRequest) {
		return handleSetResponse(handleImageRequest(imageUrlUploadSetRequest));
	}

	@Override
	public SuccessfulUploadCallbackUrlResponse directUpload(
			DirectUploadCallbackUrlRequest directUploadCallbackUrlRequest) {
		Map<String, String> attrs = new HashMap<String,String>();
		return directCallbackUrlUpload(directUploadCallbackUrlRequest, directUploadCallbackUrlRequest.getImage(), attrs);
	}

	@Override
	public SuccessfulUploadCallbackUrlSetResponse directUpload(
			DirectUploadCallbackUrlSetRequest directUploadCallbackUrlSetRequest) {
		Map<String, String> attrs = new HashMap<String,String>();
		return directCallbackUrlUpload(directUploadCallbackUrlSetRequest, directUploadCallbackUrlSetRequest.getImage(), attrs);
	}

	@Override
	public SuccessfulUploadCallbackUrlResponse directUpload(
			DirectFileUploadCallbackUrlRequest directFileUploadCallbackUrlRequest) {
		
		Map<String, String> attrs = new HashMap<String,String>();
		String fn = directFileUploadCallbackUrlRequest.getImage().getName();
		Path path = directFileUploadCallbackUrlRequest.getImage().toPath();
		attrs.put(FILE_ATTR_NAME, fn);
		try {
			attrs.put(FILE_ATTR_TYPE, Files.probeContentType(path));
		} catch (IOException e1) {
			log.error(e1.getLocalizedMessage());
		}
		
		InputStream is = null;
		try {
			is = new FileInputStream(directFileUploadCallbackUrlRequest.getImage());
		} catch (FileNotFoundException e) {
			log.error(e.getLocalizedMessage());
		}
		return directCallbackUrlUpload(directFileUploadCallbackUrlRequest, is, attrs);
	}

	@Override
	public SuccessfulUploadCallbackUrlSetResponse directUpload(
			DirectFileUploadCallbackUrlSetRequest directFileUploadCallbackUrlSetRequest) {
		
		Map<String, String> attrs = new HashMap<String,String>();
		String fn = directFileUploadCallbackUrlSetRequest.getImage().getName();
		Path path = directFileUploadCallbackUrlSetRequest.getImage().toPath();
		attrs.put(FILE_ATTR_NAME, fn);
		try {
			attrs.put(FILE_ATTR_TYPE, Files.probeContentType(path));
		} catch (IOException e1) {
			log.error(e1.getLocalizedMessage());
		}
		
		InputStream is = null;
		try {
			is = new FileInputStream(directFileUploadCallbackUrlSetRequest.getImage());
		} catch (FileNotFoundException e) {
			log.error(e.getLocalizedMessage());
		}
		return directCallbackUrlUpload(directFileUploadCallbackUrlSetRequest, is, attrs);
	}

	@Override
	public SuccessfulUploadCallbackUrlResponse imageUrlUpload(
			ImageUrlUploadCallbackUrlRequest imageUrlUploadCallbackUrlRequest) {
		return handleCallbackUrlResponse(handleImageRequest(imageUrlUploadCallbackUrlRequest));
	}

	@Override
	public SuccessfulUploadCallbackUrlSetResponse imageUrlUpload(
			ImageUrlUploadCallbackUrlSetRequest imageUrlUploadCallbackUrlSetRequest) {
		return handleCallbackUrlSetResponse(handleImageRequest(imageUrlUploadCallbackUrlSetRequest));
	}
	
	/* private initialization methods */
	private CloseableHttpClient createClient(){
		CloseableHttpClient httpclient = HttpClients.createDefault();
		return httpclient;
	}
	private void closeClient(CloseableHttpClient client){
		try {
			client.close();
		} catch (IOException e) {
			log.error("IOException : Couldn't close client");
		}
	}
	private ResponseBody getContent(HttpResponse response) throws UnsupportedOperationException, IOException{
		ResponseBody output = new ResponseBody();
		
		int code = response.getStatusLine().getStatusCode();
		output.setCode(code);
		
		InputStream body = response.getEntity().getContent();
		//log.debug(response.getEntity().toString());
		//log.debug(body.toString());
		String bodyAsString = IOUtils.toString(body, ENCODING_TYPE);
		output.setBody(bodyAsString);
		
		return output;
	}
	private ObjectMapper createObjectMapper() {
		final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        objectMapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        objectMapper.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);

        return objectMapper;
	}
	
	/* private utility methods */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private AuthWrapper wrapAuth(AbstractUploadRequest abstractUploadRequest) {
        return new AuthWrapper(new Auth(apiKey, apiSecret), abstractUploadRequest);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private AuthWrapper wrapAuth(AbstractUploadSetRequest abstractUploadSetRequest) {
        return new AuthWrapper(new Auth(apiKey, apiSecret), abstractUploadSetRequest);
    }
	
	/* private instance methods */
    
    private SuccessfulUploadResponse directUpload(AbstractUploadRequest abstractUploadRequest, InputStream bodyPart, Map<String,String> attributes) {
    	CloseableHttpClient client = createClient();
    	SuccessfulUploadResponse sur = handleResponse(handleRequest(abstractUploadRequest, bodyPart, attributes, client));
    	closeClient(client);
        return sur;
    }

    private SuccessfulUploadSetResponse directUpload(AbstractUploadSetRequest abstractUploadSetRequest, InputStream bodyPart, Map<String,String> attributes) {
    	CloseableHttpClient client = createClient();
    	SuccessfulUploadSetResponse susr = handleSetResponse(handleRequest(abstractUploadSetRequest, bodyPart, attributes, client));
    	closeClient(client);
        return susr;
    }
    
    private SuccessfulUploadCallbackUrlResponse directCallbackUrlUpload(AbstractUploadRequest abstractUploadRequest, InputStream bodyPart, Map<String,String> attributes) {
    	CloseableHttpClient client = createClient();
    	SuccessfulUploadCallbackUrlResponse sucur = handleCallbackUrlResponse(handleRequest(abstractUploadRequest, bodyPart, attributes, client));
    	closeClient(client);
        return sucur;
    }
    
    private SuccessfulUploadCallbackUrlSetResponse directCallbackUrlUpload(AbstractUploadSetRequest abstractUploadSetRequest, InputStream bodyPart, Map<String,String> attributes) {
    	CloseableHttpClient client = createClient();
    	SuccessfulUploadCallbackUrlSetResponse sucusr = handleCallbackUrlSetResponse(handleRequest(abstractUploadSetRequest, bodyPart, attributes, client));
    	closeClient(client);
        return sucusr;
    }
    
    // *** REQUEST HANDLING *** //
    private HttpResponse handleRequest(AbstractUploadRequest abstractUploadRequest, InputStream bodyPart, Map<String,String> attributes, HttpClient client) {
    	
    	HttpResponse httpResponse = null;
    	HttpPost httpPost = new HttpPost(directUploadUrl);
  
    	String filename = FILE_NAME_DEFAULT;
    	if(attributes.containsKey(FILE_ATTR_NAME) && !attributes.get(FILE_ATTR_NAME).equals("")) {
    		filename = attributes.get(FILE_ATTR_NAME);
    	}
    	String filetype = FILE_TYPE_DEFAULT;
    	if(attributes.containsKey(FILE_ATTR_TYPE) && !attributes.get(FILE_ATTR_TYPE).isEmpty()) {
    		filetype = attributes.get(FILE_ATTR_TYPE);
    	}
    	log.debug(filetype);
    	
    	ContentType fileContentType = ContentType.parse(filetype);
    	log.debug(fileContentType.toString());
    	
    	
    	log.debug("-->");
    	MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    	String dataPart = "";
		try {
			dataPart = mapper.writeValueAsString(wrapAuth(abstractUploadRequest));
		} catch (JsonProcessingException e1) {
			log.error(e1.getLocalizedMessage());
		}
		log.debug(dataPart);
    	builder.addTextBody(DATA_PART, dataPart, ContentType.APPLICATION_JSON);
    	builder.addBinaryBody(UPLOAD_PART, bodyPart, fileContentType, filename);
    	HttpEntity multipart = builder.build();
    	httpPost.setEntity(multipart);
    	
    	log.debug("Attempting execution of client");
    	try {
			httpResponse = client.execute(httpPost);
			log.debug(httpResponse.toString());
		} catch (ClientProtocolException e) {
			log.error(e.getLocalizedMessage());
		} catch (IOException e) {
			log.error(e.getLocalizedMessage());
		}
    	log.debug("Client execution completed");
    	
        return httpResponse;
    }
    
    private HttpResponse handleRequest(AbstractUploadSetRequest abstractUploadSetRequest, InputStream bodyPart, Map<String,String> attributes, HttpClient client) {
    	
    	debugMsg("handleRequest", null);
    	
    	HttpResponse httpResponse = null;
    	HttpPost httpPost = new HttpPost(directUploadUrl);
  
    	String filename = FILE_NAME_DEFAULT;
    	if(attributes.containsKey(FILE_ATTR_NAME) && attributes.get(FILE_ATTR_NAME) != null && !attributes.get(FILE_ATTR_NAME).equals("")) {
    		filename = attributes.get(FILE_ATTR_NAME);
    	}
    	String filetype = FILE_TYPE_DEFAULT;
    	if(attributes.containsKey(FILE_ATTR_TYPE) && attributes.get(FILE_ATTR_TYPE) != null && !attributes.get(FILE_ATTR_TYPE).isEmpty()) {
    		filetype = attributes.get(FILE_ATTR_TYPE);
    	}
    	debugMsg("handleRequest", "FileType : " + filetype);
    	
    	ContentType fileContentType = ContentType.parse(filetype);
    	debugMsg("handleRequest", "ContentType : " + filetype);
    	
    	MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    	debugMsg("handleRequest", "MultipartEntityBuilder created");
    	
    	String dataPart = "";
		try {
			dataPart = mapper.writeValueAsString(wrapAuth(abstractUploadSetRequest));
		} catch (JsonProcessingException e1) {
			log.error(e1.getLocalizedMessage());
		}
		debugMsg("handleRequest", dataPart);
		
    	builder.addTextBody(DATA_PART, dataPart, ContentType.APPLICATION_JSON);
    	builder.addBinaryBody(UPLOAD_PART, bodyPart, fileContentType, filename);
    	
    	HttpEntity multipart = builder.build();
    	debugMsg("handleRequest", "Multipart constructed");
    	
    	httpPost.setEntity(multipart);
    	
    	debugMsg("handleRequest", "Executing client");
    	try {
			httpResponse = client.execute(httpPost);
		} catch (ClientProtocolException e) {
			log.error(e.getLocalizedMessage());
		} catch (IOException e) {
			log.error(e.getLocalizedMessage());
		}
    	debugMsg("handleRequest", "Executing complete");
    	
    	debugMsg("handleRequest", "Returning response");
        return httpResponse;
    }
    
    private HttpResponse handleImageRequest(AbstractUploadRequest abstractUploadRequest) {
    	
    	HttpResponse httpResponse = null;
    	CloseableHttpClient client = createClient();
    	HttpPost httpPost = new HttpPost(imageUrl);
    	
    	String dataPart = "";
		try {
			dataPart = mapper.writeValueAsString(wrapAuth(abstractUploadRequest));
		} catch (JsonProcessingException e1) {
			log.error(e1.getLocalizedMessage());
		}
    	
    	HttpEntity stringPart = new StringEntity(dataPart, ContentType.APPLICATION_JSON);
    	httpPost.setEntity(stringPart);
    	
    	try {
			httpResponse = client.execute(httpPost);
		} catch (ClientProtocolException e) {
			log.error(e.getLocalizedMessage());
		} catch (IOException e) {
			log.error(e.getLocalizedMessage());
		}
        
        return httpResponse;
    }
    
    private HttpResponse handleImageRequest(AbstractUploadSetRequest abstractUploadSetRequest) {
    	
    	HttpResponse httpResponse = null;
    	CloseableHttpClient client = createClient();
    	HttpPost httpPost = new HttpPost(imageUrl);
    	
    	String dataPart = "";
		try {
			dataPart = mapper.writeValueAsString(wrapAuth(abstractUploadSetRequest));
		} catch (JsonProcessingException e1) {
			log.error(e1.getLocalizedMessage());
		}
    	
    	HttpEntity stringPart = new StringEntity(dataPart, ContentType.APPLICATION_JSON);
    	httpPost.setEntity(stringPart);
    	
    	try {
			httpResponse = client.execute(httpPost);
		} catch (ClientProtocolException e) {
			log.error(e.getLocalizedMessage());
		} catch (IOException e) {
			log.error(e.getLocalizedMessage());
		}
        
        return httpResponse;
    }
    
    // *** RESPONSE HANDLING *** //
    private SuccessfulUploadResponse handleResponse(HttpResponse response) {
        try {
        	ResponseBody responseBody = getContent(response);
        	if(responseBody.getCode() >= 300){
        		log.debug("Reason : "+responseBody.getReason());
        	}
        	String bodyAsString = responseBody.getBody();
        	log.debug(bodyAsString);
            final AbstractUploadResponse abstractUploadResponse = mapper.readValue(bodyAsString, AbstractUploadResponse.class);
            abstractUploadResponse.setStatus(response.getStatusLine().getStatusCode());

            if (response.getStatusLine().getStatusCode() == 200) {
                return (SuccessfulUploadResponse) abstractUploadResponse;
            } else {
                throw new KrakenIoRequestException("Kraken.io request failed", (FailedUploadResponse) abstractUploadResponse);
            }
        } catch (KrakenIoRequestException e) {
            throw e;
        } catch (Exception e) {
        	log.error(e.getMessage());
            throw new KrakenIoException("Failed to unmarshall response", e);
        }
    }
    
    private SuccessfulUploadSetResponse handleSetResponse(HttpResponse response) {
        try {
        	ResponseBody responseBody = getContent(response);
        	String bodyAsString = responseBody.getBody();
            final AbstractUploadResponse abstractUploadResponse = mapper.readValue(bodyAsString, AbstractUploadResponse.class);
            abstractUploadResponse.setStatus(response.getStatusLine().getStatusCode());

            if (response.getStatusLine().getStatusCode() == 200) {
                return (SuccessfulUploadSetResponse) abstractUploadResponse;
            } else {
                throw new KrakenIoRequestException("Kraken.io request failed", (FailedUploadResponse) abstractUploadResponse);
            }
        } catch (KrakenIoRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new KrakenIoException("Failed to unmarshall response", e);
        }
    }

    private SuccessfulUploadCallbackUrlResponse handleCallbackUrlResponse(HttpResponse response) {
    	String bodyAsString = null;
    	ResponseBody responseBody;
		try {
			responseBody = getContent(response);
		} catch (UnsupportedOperationException e1) {
			throw new KrakenIoException("UnsupportedOperationException", e1);
		} catch (IOException e1) {
			throw new KrakenIoException("IO Exception", e1);
		}
        bodyAsString = responseBody.getBody();
		log.debug(bodyAsString);
    	try {
            if (response.getStatusLine().getStatusCode() == 200) {
            	return mapper.readValue(bodyAsString, SuccessfulUploadCallbackUrlResponse.class);
            } else {
                final FailedUploadResponse failedUploadResponse = mapper.readValue(bodyAsString, FailedUploadResponse.class);
                failedUploadResponse.setStatus(response.getStatusLine().getStatusCode());
                throw new KrakenIoRequestException("Kraken.io request failed", failedUploadResponse);
            }
        } catch (KrakenIoRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new KrakenIoException("Failed to unmarshall response", e);
        }
    }
    
    private SuccessfulUploadCallbackUrlSetResponse handleCallbackUrlSetResponse(HttpResponse response) {
    	String bodyAsString = null;
		try {
			ResponseBody responseBody = getContent(response);
        	bodyAsString = responseBody.getBody();
		} catch (ParseException e1) {
			throw new KrakenIoException("Could not parse", e1);
		} catch (IOException e1) {
			throw new KrakenIoException("IO Exception", e1);
		}
		log.debug(bodyAsString);
        try {
            if (response.getStatusLine().getStatusCode() == 200) {
            	return mapper.readValue(bodyAsString, SuccessfulUploadCallbackUrlSetResponse.class);
            } else {
                final FailedUploadResponse failedUploadResponse = mapper.readValue(bodyAsString, FailedUploadResponse.class);
                failedUploadResponse.setStatus(response.getStatusLine().getStatusCode());
                throw new KrakenIoRequestException("Kraken.io request failed", failedUploadResponse);
            }
        } catch (KrakenIoRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new KrakenIoException("Failed to unmarshall response", e);
        }
    }
    
    private void debugMsg(String fnc, String msg){
    	String debugLine = "";
    	if(msg == null || msg.isEmpty()){
    		debugLine = fnc + " : START";
    	} else {
    		debugLine = "--> " + msg;
    	}
    	log.debug(debugLine);
    }

}
