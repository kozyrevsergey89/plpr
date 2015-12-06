package com.tetra.service.parser;

import java.io.Serializable;

import com.tetra.service.rest.Response;

/**
 * Base interface for parser. 
 * @author David Mayboroda (david.ftzi@gmail.com)
 */
public interface IParser {

	/**
	 * Setter for parsing model class type;
	 * Must be instance of {@link Serializable}.
	 * @param {@link Class} clazz. 
	 */
	void setClassType(final Class<?> clazz);
	
	/**
	 * Response instance setter for further parsing. 
	 * @param {@link Response} response 
	 */
	void setResponse(final Response response);
	
	/** @return Response instance.*/
	Response getResponse();
	
	/**
	 * Start parsing method.
	 */
	void parse();
	
}
