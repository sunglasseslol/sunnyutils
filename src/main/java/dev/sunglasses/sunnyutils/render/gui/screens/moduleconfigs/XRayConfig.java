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
        ModuleManager mm = ModuleManager.get();

        GridLayout grid = new GridLayout();
        grid.defaultCellSetting()
                .padding(4)
                .alignHorizontallyCenter()
                .alignVerticallyMiddle();

        GridLayout.RowHelper rowHelper = grid.createRowHelper(2);

        List<ButtonData> buttons = List.of(
//                new ButtonData(mm.getModule(XRay.class).getName(), () -> new BlockWhitelistScreen(this))
                new ButtonData(mm.getModule(XRay.class).getName(), () -> null)
        );

        for (ButtonData data : buttons) {
            Button btn = Gui.openScreenButton(0, 0, data.name, data.screenSupplier);
            rowHelper.addChild(btn);
        }

        grid.arrangeElements();
        grid.setX(this.width / 5 - grid.getWidth() / 2);
        grid.setY(100);

        grid.visitWidgets(this::addRenderableWidget);
        this.addRenderableWidget(drawBackButton(this.minecraft));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.minecraft == null) return;
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}

//class BlockWhitelistScreen extends ConfigScreen {
//    private Screen parent;
//    private BlockList blockList;
//    private EditBox searchBox;
//    private Button addButton;
//    private Button removeButton;
//
//    private final Set<Block> whitelistedBlocks = new HashSet<>();
//
//    public BlockWhitelistScreen(Screen parent) {
//        super(new XRayConfig(), "BlockWhitelist");
//        this.parent = parent;
//    }
//
//    @Override
//    protected void init() {
//        if (this.minecraft == null) return;
//
//        this.searchBox = new EditBox(
//                this.font,
//                this.width / 2 - 100,
//                20,
//                200,
//                20,
//                Component.literal("Search blocks...")
//        );
//        this.searchBox.setHint(Component.literal("minecraft:diamond_ore"));
//        this.searchBox.setResponder(text -> {
//            if (blockList != null) {
//                blockList.updateFilter(text);
//            }
//        });
//        this.addRenderableWidget(this.searchBox);
//
//        this.blockList = new BlockList(
//                this.minecraft,
//                this.width,
//                this.height - 96,
//                50,
//                36
//        );
//        this.addRenderableWidget(this.blockList);
//
//        this.addButton = Button.builder(
//                Component.literal("Add Selected Block"),
//                button -> addSelectedBlock()
//        ).bounds(this.width / 2 - 154, this.height - 52, 150, 20).build();
//        this.addRenderableWidget(this.addButton);
//
//        this.removeButton = Button.builder(
//                Component.literal("Remove Selected"),
//                button -> removeSelectedBlock()
//        ).bounds(this.width / 2 + 4, this.height - 52, 150, 20).build();
//        this.addRenderableWidget(this.removeButton);
//
//        this.addRenderableWidget(Button.builder(
//                Component.literal("Done"),
//                button -> {
//                    saveWhitelist();
//                    this.onClose();
//                }
//        ).bounds(this.width / 2 - 100, this.height - 28, 200, 20).build());
//    }
//
//    @Override
//    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
//        this.renderMenuBackground(guiGraphics);
//
//        this.blockList.render(guiGraphics, mouseX, mouseY, partialTick);
//
//        guiGraphics.drawCenteredString(this.font, "BlockWhitelist", this.width / 2, 8, 0xFFFFFF);
//
//        guiGraphics.drawString(this.font, "Whitelisted: " + whitelistedBlocks.size(),
//                10, this.height - 52, 0xAAAAAA);
//
//        super.render(guiGraphics, mouseX, mouseY, partialTick);
//    }
//
//    private void addSelectedBlock() {
//        BlockList.BlockEntry selected = this.blockList.getSelected();
//        if (selected != null) {
//            whitelistedBlocks.add(selected.block);
//            this.blockList.updateWhitelist();
//        }
//    }
//
//    private void removeSelectedBlock() {
//        BlockList.BlockEntry selected = this.blockList.getSelected();
//        if (selected != null) {
//            whitelistedBlocks.remove(selected.block);
//            this.blockList.updateWhitelist();
//        }
//    }
//
//    private void saveWhitelist() {
//        // Save to XRay module
//    }
//
//    @Override
//    public void onClose() {
//        this.minecraft.setScreen(parent);
//    }
//
//    /**
//     * Custom scrollable list of blocks
//     */
//    class BlockList extends ObjectSelectionList<BlockList.BlockEntry> {
//        private String filterText = "";
//        private final List<BlockEntry> allEntries = new ArrayList<>();
//
//        public BlockList(net.minecraft.client.Minecraft mc, int width, int height, int y, int itemHeight) {
//            super(mc, width, height, y, itemHeight);
//
//            for (Block block : BuiltInRegistries.BLOCK) {
//                if (block == Blocks.AIR) continue;
//                allEntries.add(new BlockEntry(block));
//            }
//
//            updateFilter("");
//        }
//
//        public void updateFilter(String text) {
//            this.filterText = text.toLowerCase();
//            this.clearEntries();
//
//            for (BlockEntry entry : allEntries) {
//                String blockId = BuiltInRegistries.BLOCK.getKey(entry.block).toString();
//                if (blockId.toLowerCase().contains(filterText)) {
//                    this.addEntry(entry);
//                }
//            }
//        }
//
//        public void updateWhitelist() {
//            updateFilter(filterText);
//        }
//
//        @Override
//        public int getRowWidth() {
//            return 300;
//        }
//
//        /**
//         * Individual block entry in the list
//         */
//        class BlockEntry extends ObjectSelectionList.Entry<BlockEntry> {
//            final Block block;
//
//            public BlockEntry(Block block) {
//                this.block = block;
//            }
//
//            @Override
//            public void renderContent(GuiGraphics guiGraphics, int index, int top, int left,
//                                      boolean isHovered, float partialTick) {
//                ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
//                String blockName = blockId.toString();
//
//                int color = whitelistedBlocks.contains(block) ? 0x00FF00 : 0xFFFFFF;
//
//                // FIXED: Use BlockWhitelistScreen.this.font instead of XRayConfig.this.font
//                guiGraphics.drawString(
//                        BlockWhitelistScreen.this.font,
//                        blockName,
//                        left + 5,
//                        top + 2,
//                        color
//                );
//
//                if (whitelistedBlocks.contains(block)) {
//                    guiGraphics.drawString(
//                            BlockWhitelistScreen.this.font,
//                            "✓",
//                            left + 200,
//                            top + 2,
//                            0x00FF00
//                    );
//                }
//            }
//
//            @Override
//            public Component getNarration() {
//                return Component.literal(BuiltInRegistries.BLOCK.getKey(block).toString());
//            }
//
//            @Override
//            public boolean mouseClicked(double mouseX, double mouseY, int button) {
//                BlockList.this.setSelected(this);
//                return true;
//            }
//        }
//    }
//}