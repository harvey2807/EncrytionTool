package model;

public interface Cipher {
    public int genKey();

    public void loadKey();

    public void encrypt();

    public void decrypt();
}
