package byow.Core;

import java.io.Serializable;

public class Light extends Coordinate implements Serializable {
    private boolean lightOn;

    public Light(int x, int y, boolean onOff) {
        super(x, y);
        this.lightOn = onOff;
    }

    public boolean getonOff() {
        return lightOn;
    }

    public void switchOff() {
        this.lightOn = false;
    }

    public void switchOn() {
        this.lightOn = true;
    }
}
