public class Town {

    private int pickup;
    private int dropOff;

    public Town(int pickup, int dropOff) {
        this.pickup = pickup;
        this.dropOff = dropOff;
    }

    public void setPickup(int pickup) {this.pickup = pickup;}
    public void setDropOff(int dropOff) {this.dropOff = dropOff;}

    public int getPickup() {return this.pickup;}
    public int getDropOff() {return this.dropOff;}


}
