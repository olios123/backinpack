//
// CC Creative Commons 2022
// Attribution-NoDerivatives 4.0 International
// Author olios
//

package me.olios.backinpack.Recipes;

import me.olios.backinpack.Data;
import me.olios.backinpack.Library.NBTTags;
import me.olios.backinpack.Managers.BackpacksManager;
import me.olios.backinpack.Managers.FilesManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.*;

public class Backpack {

	static YamlConfiguration cnf = FilesManager.getConfigYml();

	public static ShapedRecipe getRecipe()
	{
		NamespacedKey key = new NamespacedKey(Data.plugin, "backpack");

		ItemStack unassignedBackpack = BackpacksManager.createUnassignedBackpackItem();

		// Add NBT Tag about backpack status
		NBTTags.addNBT(unassignedBackpack, "backinpack.status", "unassigned");

		ShapedRecipe recipe = new ShapedRecipe(key, unassignedBackpack);

		String shape1 = "";
		String shape2 = "";
		String shape3 = "";

		List<Boolean> shapes = new ArrayList<>();
		Map<Integer, Character> symbols = new HashMap<>();
		symbols.put(1, 'a');
		symbols.put(2, 'b');
		symbols.put(3, 'c');
		symbols.put(4, 'd');
		symbols.put(5, 'e');
		symbols.put(6, 'f');
		symbols.put(7, 'g');
		symbols.put(8, 'h');
		symbols.put(9, 'i');

		for (int i = 1; i <= 9; i++)
		{
			if (cnf.get("crafting." + i) == null) shapes.add(false);
			else shapes.add(true);
		}

		int pos = 1;
		for (boolean bool : shapes.subList(0, 3))
		{
			if (bool) shape1 += symbols.get(pos++);
			else
			{
				shape1 += " ";
				pos++;
			}
		}

		for (boolean bool : shapes.subList(3, 6))
		{
			if (bool) shape2 += symbols.get(pos++);
			else
			{
				shape2 += " ";
				pos++;
			}
		}

		for (boolean bool : shapes.subList(6, 9))
		{
			if (bool) shape3 += symbols.get(pos++);
			else
			{
				shape3 += " ";
				pos++;
			}
		}

		recipe.shape(shape1, shape2, shape3);
		/*
		* Each letter represents your crafting position
		* Empty space means no item, e.g. "L  " or " L "
		* Each letter is one item
		*
		* ------------------------
		* |   1   |   2   |  3   |
		* ------------------------
		* |   4   |   5   |  6   |
		* ------------------------
		* |   7   |   8   |  9   |
		* ------------------------
		*
		* */

		// Setting items
		int index = 1;
		for (boolean bool : shapes)
		{
			if (bool) recipe.setIngredient(symbols.get(index), Material.getMaterial(cnf.getString("crafting." + index++)));
			else index++;
		}

		return recipe;
	}
}
