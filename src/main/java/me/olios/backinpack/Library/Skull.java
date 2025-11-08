package me.olios.backinpack.Library;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Skull {

	public static ItemStack createSkull(String url)
	{
		PlayerProfile profile = getProfile(url);

		ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();

		itemMeta.setOwnerProfile(profile);
		itemStack.setItemMeta(itemMeta);

		return itemStack;
    }

	private static PlayerProfile getProfile(String url)
	{
		PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
		PlayerTextures textures = profile.getTextures();

		URL urlObject = null;

		try
		{
			if (url.startsWith("https://") || url.startsWith("http://"))
			{
				urlObject = new URL(url);
			}
			else
			{
				String decodedString = new String(Base64.getDecoder().decode(url));

				String regex = "\"url\":\"(.*?)\"";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(decodedString);

				if (matcher.find())
				{
					String decodedURL = matcher.group(1);

					urlObject = new URL(decodedURL);
				}
			}

		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}

		textures.setSkin(urlObject);
		profile.setTextures(textures);
		return profile;
	}


}
