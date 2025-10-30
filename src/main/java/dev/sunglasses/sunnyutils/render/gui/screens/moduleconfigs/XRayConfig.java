package dev.sunglasses.sunnyutils.render.gui.screens.moduleconfigs;

import dev.sunglasses.sunnyutils.modules.utilities.XRay;
import dev.sunglasses.sunnyutils.render.gui.Gui;
import dev.sunglasses.sunnyutils.render.gui.screens.ConfigScreen;
import dev.sunglasses.sunnyutils.utils.ButtonData;
import dev.sunglasses.sunnyutils.utils.ScreenButtonData;
import net.minecraft.client.OptionInstance;
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
    // Settings sliders
    private OptionInstance<Integer> scanDelayOption;
    private OptionInstance<Integer> chunkRadiusOption;
    private OptionInstance<Integer> renderDistanceOption;

    public XRayConfig() {
        super(new ConfigScreen(null, "Configuration"), "XRay");
    }

    @Override
    protected void init() {
        if (this.minecraft == null) return;

        // Calculate panel positions
        int leftPanelX = 20;
        int rightPanelX = this.width / 2 + 20;
        int startY = 50;
        int panelWidth = (this.width / 2) - 40;

        // ===== LEFT PANEL: Sub-menus using GridLayout =====
        GridLayout leftGrid = new GridLayout();
        leftGrid.defaultCellSetting()
                .padding(2)
                .alignHorizontallyCenter();

        GridLayout.RowHelper leftRows = leftGrid.createRowHelper(1); // 1 column

        List<ScreenButtonData> screenButtons = List.of(
                new ScreenButtonData("BlockList", () -> new BlockWhitelistScreen(this))
        );

        for(ScreenButtonData data : screenButtons) {
            Button btn = Gui.openScreenButton(0, 0, data.name, data.screenSupplier);
            leftRows.addChild(btn);
        }

        // Position and add left grid
        leftGrid.arrangeElements();
        leftGrid.setX(leftPanelX);
        leftGrid.setY(startY);
        leftGrid.visitWidgets(this::addRenderableWidget);

        // ===== RIGHT PANEL: Settings using GridLayout =====
        GridLayout rightGrid = new GridLayout();
        rightGrid.defaultCellSetting()
                .padding(2)
                .alignHorizontallyCenter();

        GridLayout.RowHelper rightRows = rightGrid.createRowHelper(1); // 1 column

        // Create slider options
        this.scanDelayOption = new OptionInstance<>(
                "xray.scanDelay",
                OptionInstance.noTooltip(),
                (component, value) -> Component.literal("Scan Delay: " + value + " ticks (" + String.format("%.1f", value / 20.0) + "s)"),
                new OptionInstance.IntRange(10, 100),
                XRay.getScanDelay(),
                (newValue) -> XRay.setScanDelay(newValue)
        );

        this.chunkRadiusOption = new OptionInstance<>(
                "xray.chunkRadius",
                OptionInstance.noTooltip(),
                (component, value) -> Component.literal("Chunk Radius: " + value),
                new OptionInstance.IntRange(1, 8),
                XRay.getChunkRadius(),
                (newValue) -> XRay.setChunkRadius(newValue)
        );

        this.renderDistanceOption = new OptionInstance<>(
                "xray.renderDistance",
                OptionInstance.noTooltip(),
                (component, value) -> Component.literal("Render Distance: " + value),
                new OptionInstance.IntRange(16, 128),
                XRay.getRenderDistance(),
                (newValue) -> XRay.setRenderDistance(newValue)
        );

        // Add sliders to grid
        rightRows.addChild(scanDelayOption.createButton(
                this.minecraft.options,
                0, 0,
                panelWidth
        ));

        rightRows.addChild(chunkRadiusOption.createButton(
                this.minecraft.options,
                0, 0,
                panelWidth
        ));

        rightRows.addChild(renderDistanceOption.createButton(
                this.minecraft.options,
                0, 0,
                panelWidth
        ));

        // Add toggle buttons using Gui.createButton
        rightRows.addChild(Gui.createButton(
                0, 0, panelWidth, 20,
                "Show Chests: ON",
                button -> {
                    // TODO: Toggle chests
                    button.setMessage(Component.literal("Show Chests: OFF"));
                }
        ));

        rightRows.addChild(Gui.createButton(
                0, 0, panelWidth, 20,
                "Show Spawners: ON",
                button -> {
                    // TODO: Toggle spawners
                    button.setMessage(Component.literal("Show Spawners: OFF"));
                }
        ));

        // Position and add right grid
        rightGrid.arrangeElements();
        rightGrid.setX(rightPanelX);
        rightGrid.setY(startY);
        rightGrid.visitWidgets(this::addRenderableWidget);

        // ===== BOTTOM: Back Button =====
        this.addRenderableWidget(drawBackButton(this.minecraft));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.minecraft == null) return;

        // Render background
        this.renderMenuBackground(graphics);

        // Draw main title at top
        graphics.drawCenteredString(this.font, this.getTitle(), this.width / 2, 15, 0xFFFFFF);

        // Draw vertical divider line
        int dividerX = this.width / 2;
        graphics.fill(dividerX - 1, 40, dividerX + 1, this.height - 40, 0xFF404040);

        // Draw panel titles
        graphics.drawCenteredString(this.font, "Sub-Menus", this.width / 4, 35, 0xFFFF55);
        graphics.drawCenteredString(this.font, "Settings", dividerX + this.width / 4, 35, 0xFFFF55);

        // Draw setting descriptions
        int rightPanelX = this.width / 2 + 20;
        int descX = rightPanelX + 5;

        // Only draw descriptions if there's space
        if (this.width > 600) {
            graphics.drawString(this.font, "How often to scan", descX, 73, 0x808080);
            graphics.drawString(this.font, "Area to scan", descX, 98, 0x808080);
            graphics.drawString(this.font, "Max render distance", descX, 123, 0x808080);
        }

        // Render all widgets
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
