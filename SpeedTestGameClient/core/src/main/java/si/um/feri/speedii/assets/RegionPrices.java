package si.um.feri.speedii.assets;

public enum RegionPrices {
    FAST_ACCESS(80),
    CABINETS(100),
    SERVER_CONTROL(100),
    HUB(100),
    SWITCH(75),
    CELL_TOWER(200),
    SATELLITE_TOWER(100),
    SIGNAL(125);

    private final int price;

    RegionPrices(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }
}
