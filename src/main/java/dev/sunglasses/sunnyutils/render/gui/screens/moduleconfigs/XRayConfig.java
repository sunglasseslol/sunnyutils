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
        whitelistedBlocks.addAll(XRay.getWhitelistedBlocks());
        System.out.println("Loaded " + whitelistedBlocks.size() + " blocks into whitelist screen");
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

        // Block list - DON'T add as renderable widget, we'll render it manually
        // This prevents it from capturing clicks meant for buttons
        this.blockList = new BlockList(
                this.minecraft,
                this.width,
                this.height - 80, // height stops before buttons
                45, // Top
                20  // Item height (THIS is what makes each entry 20px tall!)
        );
        // NOTE: NOT calling addRenderableWidget for the list!

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

        // Done button
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
        // Render background
        this.renderMenuBackground(guiGraphics);

        // Draw title BEFORE list
        guiGraphics.drawCenteredString(this.font, "BlockWhitelist", this.width / 2, 8, 0xFFFFFF);

        // Draw instructions
        guiGraphics.drawString(this.font, "Whitelisted: " + whitelistedBlocks.size(),
                10, this.height - 60, 0xAAAAAA);

        // Show how many entries we have
        guiGraphics.drawString(this.font, "Entries: " + this.blockList.children().size(),
                10, this.height - 70, 0xFFFFFF);

        // Render the list manually
        this.blockList.render(guiGraphics, mouseX, mouseY, partialTick);

        // Render all other widgets (buttons, search box) with proper event handling
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Let buttons and widgets handle clicks first (they're on top)
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        // Only if no widget handled it, let the list handle the click
        if (this.blockList.isMouseOver(mouseX, mouseY)) {
            return this.blockList.mouseClicked(mouseX, mouseY, button);
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        // Let the list handle scroll dragging
        if (this.blockList.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        // Let the list handle scrolling
        if (this.blockList.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
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
        XRay.setWhitelistedBlocks(whitelistedBlocks);
        XRay.saveWhitelist();
        System.out.println("Saved whitelist with " + whitelistedBlocks.size() + " blocks");
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

        public BlockList(net.minecraft.client.Minecraft mc, int width, int height, int top, int itemHeight) {
            // ObjectSelectionList constructor: (Minecraft, width, height, top, itemHeight)
            super(mc, width, height, top, itemHeight);

            // Add all blocks from registry
            for (Block block : BuiltInRegistries.BLOCK) {
                if (block == Blocks.AIR) continue;
                allEntries.add(new BlockEntry(block));
            }

            System.out.println("BlockList initialized with " + allEntries.size() + " blocks");
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

            System.out.println("Filter '" + text + "' resulted in " + this.children().size() + " blocks");
        }

        public void updateWhitelist() {
            // Refresh to update colors
            updateFilter(filterText);
        }

        @Override
        public int getRowWidth() {
            return this.width - 50;
        }

        @Override
        public int getRowLeft() {
            return this.getX() + this.width / 2 - getRowWidth() / 2;
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

                // Check if mouse is actually over any button
                boolean isOverButton = false;
                if (BlockWhitelistScreen.this.addButton.isMouseOver(mouseX, mouseY) ||
                        BlockWhitelistScreen.this.removeButton.isMouseOver(mouseX, mouseY)) {
                    isOverButton = true;
                }

                // Also check the Done button
                for (net.minecraft.client.gui.components.Renderable renderable : BlockWhitelistScreen.this.renderables) {
                    if (renderable instanceof Button btn && btn.isMouseOver(mouseX, mouseY)) {
                        isOverButton = true;
                        break;
                    }
                }

                boolean shouldHighlight = isHovered && !isOverButton;

                // Different background if whitelisted or hovered
                if (whitelistedBlocks.contains(block)) {
                    guiGraphics.fill(left, top, left + width, top + height, 0x8000FF00); // Semi-transparent green for whitelisted
                } else if (shouldHighlight) {
                    guiGraphics.fill(left, top, left + width, top + height, 0x80FFFFFF); // Semi-transparent white for hover
                } else {
                    guiGraphics.fill(left, top, left + width, top + height, 0x40000000); // Semi-transparent black background
                }

                // Draw block name - WHITE text
                guiGraphics.drawString(
                        BlockWhitelistScreen.this.font,
                        blockName,
                        left + 5,
                        top + (height - BlockWhitelistScreen.this.font.lineHeight) / 2,
                        0xFFFFFFFF  // WHITE text
                );

                // Draw status checkmark if whitelisted
                if (whitelistedBlocks.contains(block)) {
                    guiGraphics.drawString(
                            BlockWhitelistScreen.this.font,
                            "✓",
                            left + width - 20,
                            top + (height - BlockWhitelistScreen.this.font.lineHeight) / 2,
                            0xFF00FF00  // Bright green checkmark
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