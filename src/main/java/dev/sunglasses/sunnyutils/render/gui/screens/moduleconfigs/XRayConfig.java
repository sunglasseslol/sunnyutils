package dev.sunglasses.sunnyutils.render.gui.screens.moduleconfigs;

import dev.sunglasses.sunnyutils.modules.base.ModuleManager;
import dev.sunglasses.sunnyutils.modules.utilities.XRay;
import dev.sunglasses.sunnyutils.render.gui.Gui;
import dev.sunglasses.sunnyutils.render.gui.screens.ConfigScreen;
import dev.sunglasses.sunnyutils.utils.ButtonData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class XRayConfig extends ConfigScreen {
    public XRayConfig() {
        super(new ConfigScreen(null, "Configuration"), "XRay");
    }

    @Override
    protected void init() {
        if (this.minecraft == null) return;
        //
        ModuleManager mm = ModuleManager.get();

        // --- Create the grid ---
        GridLayout grid = new GridLayout();
        grid.defaultCellSetting()
                .padding(4)
                .alignHorizontallyCenter()
                .alignVerticallyMiddle();

        GridLayout.RowHelper rowHelper = grid.createRowHelper(2);

        // Define your button data
        List<ButtonData> buttons = List.of(
                new ButtonData(mm.getModule(XRay.class).getName(), () -> new BlockWhitelistScreen(this))
        );

        // Add buttons to grid
        for (ButtonData data : buttons) {
            Button btn = Gui.openScreenButton(0, 0, data.name, data.screenSupplier);
            rowHelper.addChild(btn);
        }

        // Add a Back button at the bottom

        // --- Center the grid on the screen ---
        grid.arrangeElements();
        grid.setX(this.width / 5 - grid.getWidth() / 2);
        grid.setY(100);

        // --- Actually add to the screen ---
        grid.visitWidgets(this::addRenderableWidget);

        //
        this.addRenderableWidget(drawBackButton(this.minecraft));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.minecraft == null) return;
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}

class BlockWhitelistScreen extends ConfigScreen {
    private Screen parent;
    private BlockList blockList;
    private EditBox searchBox;
    private Button addButton;
    private Button removeButton;

    // Store whitelisted blocks
    private final Set<Block> whitelistedBlocks = new HashSet<>();

    public BlockWhitelistScreen(Screen parent) {
        super(new XRayConfig(), "BlockWhitelist");
        this.parent = parent;

        // Load current whitelist from XRay module
        // (You'll need to add a getter method to XRay)
    }

    @Override
    protected void init() {
        if (this.minecraft == null) return;

        // Search box at top
        this.searchBox = new EditBox(
                this.font,
                this.width / 2 - 100,
                20,
                200,
                20,
                Component.literal("Search blocks...")
        );
        this.searchBox.setHint(Component.literal("minecraft:diamond_ore"));
        this.searchBox.setResponder(text -> {
            if (blockList != null) {
                blockList.updateFilter(text);
            }
        });
        this.addRenderableWidget(this.searchBox);

        // Block list (scrollable)
        this.blockList = new BlockList(
                this.minecraft,
                this.width,
                this.height - 96, // Height
                50, // Top
                36  // Item height
        );
        this.addRenderableWidget(this.blockList);

        // Add button
        this.addButton = Button.builder(
                Component.literal("Add Selected Block"),
                button -> addSelectedBlock()
        ).bounds(this.width / 2 - 154, this.height - 52, 150, 20).build();
        this.addRenderableWidget(this.addButton);

        // Remove button
        this.removeButton = Button.builder(
                Component.literal("Remove Selected"),
                button -> removeSelectedBlock()
        ).bounds(this.width / 2 + 4, this.height - 52, 150, 20).build();
        this.addRenderableWidget(this.removeButton);

        // Back button
        this.addRenderableWidget(Button.builder(
                Component.literal("Done"),
                button -> {
                    saveWhitelist();
                    this.onClose();
                }
        ).bounds(this.width / 2 - 100, this.height - 28, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderMenuBackground(guiGraphics);

        // Render the list
        this.blockList.render(guiGraphics, mouseX, mouseY, partialTick);

        // Draw title
        guiGraphics.drawCenteredString(this.font, "BlockWhitelist", this.width / 2, 8, 0xFFFFFF);

        // Draw instructions
        guiGraphics.drawString(this.font, "Whitelisted: " + whitelistedBlocks.size(),
                10, this.height - 52, 0xAAAAAA);

        // Render widgets (search box, buttons)
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void addSelectedBlock() {
        BlockList.BlockEntry selected = this.blockList.getSelected();
        if (selected != null) {
            whitelistedBlocks.add(selected.block);
            this.blockList.updateWhitelist();
        }
    }

    private void removeSelectedBlock() {
        BlockList.BlockEntry selected = this.blockList.getSelected();
        if (selected != null) {
            whitelistedBlocks.remove(selected.block);
            this.blockList.updateWhitelist();
        }
    }

    private void saveWhitelist() {
        // Save to XRay module
        // XRay.setWhitelistedBlocks(whitelistedBlocks);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }

    /**
     * Custom scrollable list of blocks
     */
    class BlockList extends ObjectSelectionList<BlockList.BlockEntry> {
        private String filterText = "";
        private final List<BlockEntry> allEntries = new ArrayList<>();

        public BlockList(net.minecraft.client.Minecraft mc, int width, int height, int y, int itemHeight) {
            super(mc, width, height, y, itemHeight);

            // Add all blocks from registry
            for (Block block : BuiltInRegistries.BLOCK) {
                if (block == Blocks.AIR) continue; // Skip air
                allEntries.add(new BlockEntry(block));
            }

            updateFilter("");
        }

        public void updateFilter(String text) {
            this.filterText = text.toLowerCase();
            this.clearEntries();

            for (BlockEntry entry : allEntries) {
                String blockId = BuiltInRegistries.BLOCK.getKey(entry.block).toString();
                if (blockId.toLowerCase().contains(filterText)) {
                    this.addEntry(entry);
                }
            }
        }

        public void updateWhitelist() {
            // Refresh to update colors
            updateFilter(filterText);
        }

        @Override
        public int getRowWidth() {
            return 300;
        }

        /**
         * Individual block entry in the list
         */
        class BlockEntry extends ObjectSelectionList.Entry<BlockEntry> {
            final Block block;

            public BlockEntry(Block block) {
                this.block = block;
            }

            @Override
            public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height,
                               int mouseX, int mouseY, boolean isHovered, float partialTick) {
                ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
                String blockName = blockId.toString();

                // Different color if whitelisted
                int color = whitelistedBlocks.contains(block) ? 0x00FF00 : 0xFFFFFF;

                // Draw block name
                guiGraphics.drawString(
                        BlockWhitelistScreen.this.font,
                        blockName,
                        left + 5,
                        top + 2,
                        color
                );

                // Draw status
                if (whitelistedBlocks.contains(block)) {
                    guiGraphics.drawString(
                            BlockWhitelistScreen.this.font,
                            "✓",
                            left + width - 20,
                            top + 2,
                            0x00FF00
                    );
                }
            }

            @Override
            public Component getNarration() {
                return Component.literal(BuiltInRegistries.BLOCK.getKey(block).toString());
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                BlockList.this.setSelected(this);
                return true;
            }
        }
    }
}
