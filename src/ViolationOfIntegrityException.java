public class ViolationOfIntegrityException extends Exception
{
    public ViolationOfIntegrityException()
    {
        super("Doslo je do narusavanja integriteta fajla!!");
    }

    public ViolationOfIntegrityException(String message)
    {
        super(message);
    }

}
