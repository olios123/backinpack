package me.olios.backinpack.Library;

public class GUIPosition {

    public static int getPosition(int x, int y)
    {
        return (y - 1) * 9 + (x - 1);
    }

}
