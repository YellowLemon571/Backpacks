# Backpacks
This simple plugin adds backpacks to your server with a crafting recipe and adjustable item properties.

Plenty of extra features will be added to the future!

# Config
`backpackMaterial` - The material name of the backpack. Not case sensitive.
`backpackName` - The name of the backpack. Color coding is supported.
`backpackLore` - The lore of the backpack. Color coding is supported.
`backpackRecipe.shape` - The "shape" of the backpack's crafting recipe, separated into the top, middle, and bottom rows. One character is used per material, and each unique character is defined by backpackRecipe.materials.
`backpackRecipe.materials.*` - The definitions for each unique recipe material. One entry MUST be made for EACH different material. The key of each entry will be a single character, and the value will be the material name (not case sensitive).
Backpack inventories are stored as base64 strings in the backpacks.yml file.

# Sub-Commands
`give <username>` - Gives a backpack to a specified player.
`name <name>` : Changes the backpack's name.
`lore <lore>` : Changes the backpack's lore.
`material <material>` : Changes the backpack's material.
`recipe` : Opens a UI to set your own recipe for the backpack.

# Permissions
`backpacks.give` - Allows players to use the /givebackpack command.
`backpacks.modify` - Allows modification of the backpack's configuration in-game
`backpacks.use` - Allows usage of the backpacks.
`backpacks.craft` - Allows crafting of backpacks.

# Crafting Recipe
This is the backpack's default crafting recipe. Recipes can be changed either in the config or in-game with the /backpackrecipe command.

![image](https://proxy.spigotmc.org/0dc189d509c8aa2eb20ef4e009f76d2985075a06?url=https%3A%2F%2Fi.imgur.com%2FtknSTcO.png "Default Crafting Recipe")
