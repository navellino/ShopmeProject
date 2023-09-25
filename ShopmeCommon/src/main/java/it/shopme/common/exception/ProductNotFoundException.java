package it.shopme.common.exception;

public class ProductNotFoundException extends Exception{
	
	private static final long serialVersionUID = 3342552091913927811L;

	public ProductNotFoundException(String message) {
		super(message);
	}

}
