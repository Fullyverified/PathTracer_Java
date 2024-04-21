public class Ray {
    private double oX, dirX, normDirX;
    private double oY, dirY, normDirY;
    private double oZ, dirZ, normDirZ;

    public Ray(double oX, double oY, double oZ, double dirX, double dirY, double dirZ)
    {
        this.oX = oX;
        this.oY = oY;
        this.oZ = oZ;
        this.dirX = dirX;
        this.dirY = dirY;
        this.dirZ = dirZ;

    }
}
