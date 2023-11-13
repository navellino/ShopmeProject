package it.shopme.common.exception;


public class CustomerNotFoundException extends Exception{

	private static final long serialVersionUID = -6398918681284936736L;

	public CustomerNotFoundException(String message) {
		super(message);
	}
}
