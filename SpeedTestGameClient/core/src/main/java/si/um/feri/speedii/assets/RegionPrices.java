package si.um.feri.speedii.assets;

public enum RegionPrices {
    FAST_ACCESS(50),
    CABINETS(80),
    SERVER_CONTROL(70),
    HUB(100),
    SWITCH(40),
    CELL_TOWER(35),
    SATELLITE_TOWER(55),
    SIGNAL(120);

    private final int price;

    RegionPrices(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }
}
