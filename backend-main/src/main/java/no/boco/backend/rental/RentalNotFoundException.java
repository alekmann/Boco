package no.boco.backend.rental;

public class RentalNotFoundException extends RuntimeException{
    public RentalNotFoundException(String msg) {
        super(msg);
    }
}
