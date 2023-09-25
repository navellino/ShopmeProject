package it.shopme.common.exception;


public class CategoryNotFoundException extends Exception{

	private static final long serialVersionUID = -6398918681284936736L;

	public CategoryNotFoundException(String message) {
		super(message);
	}
}
