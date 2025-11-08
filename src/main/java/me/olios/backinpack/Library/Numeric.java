//
// CC Creative Commons 2022
// Attribution-NoDerivatives 4.0 International
// Author olios
//

package me.olios.backinpack.Library;

public class Numeric {

	public static boolean check(String msg)
	{
		try
		{
			Integer.parseInt(msg);
			return true;
		}catch(NumberFormatException e)
		{
			return false;
		}
	}

}
