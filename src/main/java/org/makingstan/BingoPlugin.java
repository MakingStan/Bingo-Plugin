package org.makingstan;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Provides;
import javax.inject.Inject;

import com.google.protobuf.Api;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashMap;

@Slf4j
@PluginDescriptor(
	name = "Bingo"
)
public class BingoPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private BingoConfig config;

	@Inject
	public ClientToolbar clientToolbar;

	private NavigationButton navButton;
	private BingoPanel panel;
	private JsonObject groupInfoJson;

	private ApiSender sender;
	protected long accountHash = -1;

	BufferedImage panelIcon = ImageUtil.loadImageResource(getClass(), "/BingoIcon.png");
	@Override
	protected void startUp()
	{
		sender = new ApiSender("http://localhost:5038/");

		if(GameState.LOGGED_IN == client.getGameState())
		{
			this.accountHash = client.getAccountHash();
		}

		this.panel = injector.getInstance(BingoPanel.class);

		navButton = NavigationButton.builder()
				.tooltip("Bingo Plugin")
				.icon(panelIcon)
				.priority(3)
				.panel(panel)
				.build();
		clientToolbar.addNavigation(navButton);

		updateGroupInfo();
	}

	@Override
	protected void shutDown()
	{
		clientToolbar.removeNavigation(navButton);
	}

	public void updateGroupInfo()
	{
		String groupInfo = sender.sendGetRequest("api/groupinfo/"+accountHash);
		groupInfoJson = new JsonParser().parse(groupInfo).getAsJsonObject();
	}

	@Subscribe
	public void onNpcLootReceived(final NpcLootReceived npcLootReceived)
	{
		Collection<ItemStack> items = npcLootReceived.getItems();

		for(ItemStack receivedItems: items) {
			for(JsonElement el: groupInfoJson.getAsJsonArray("items"))
			{
				System.out.println(el.getAsJsonObject().get("id"));
				if(el.getAsJsonObject().get("id").getAsInt() == receivedItems.getId())
				{
					HashMap<String, Object> body = new HashMap<>();
					body.put("playerId", accountHash);
					body.put("itemId", receivedItems.getId());
					sender.sendPostRequest("api/postdrop", body);
				}
			}

		}
	}


	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			this.accountHash = client.getAccountHash();
		}
	}

	@Provides
	BingoConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BingoConfig.class);
	}
}
