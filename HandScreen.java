package com.example.handmod;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class HandScreen extends Screen {
    private boolean editMain = true;

    public HandScreen() {
        super(Text.literal("Настройка рук"));
    }

    private HandConfig.Hand current() {
        return editMain ? HandConfig.get().main : HandConfig.get().off;
    }

    @Override
    protected void init() {
        int w = 160, h = 20, x = 10, y = 30;

        addDrawableChild(ButtonWidget.builder(
                Text.literal(editMain ? "Рука: ОСНОВНАЯ" : "Рука: ВТОРАЯ"),
                b -> { editMain = !editMain; clearAndInit(); }
        ).dimensions(x, y, w, h).build());
        y += 26;

        for (int i = 0; i < HandConfig.NAMES.length; i++) {
            addDrawableChild(new ParamSlider(x, y, w, h, i, current()));
            y += 24;
        }
        y += 4;

        addDrawableChild(ButtonWidget.builder(Text.literal("Сбросить эту руку"), b -> {
            current().reset();
            clearAndInit();
        }).dimensions(x, y, w, h).build());
        y += 24;

        addDrawableChild(ButtonWidget.builder(Text.literal("Готово"), b -> close())
                .dimensions(x, y, w, h).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawTextWithShadow(textRenderer, this.title, 10, 14, 0xFFFFFFFF);
    }

    // без размытия фона, чтобы видеть руку в реальном времени
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void removed() {
        HandConfig.save();
    }

    private static class ParamSlider extends SliderWidget {
        private final int idx;
        private final HandConfig.Hand hand;

        ParamSlider(int x, int y, int w, int h, int idx, HandConfig.Hand hand) {
            super(x, y, w, h, Text.empty(),
                    (hand.v[idx] - HandConfig.MIN[idx]) / (double) (HandConfig.MAX[idx] - HandConfig.MIN[idx]));
            this.idx = idx;
            this.hand = hand;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            if (hand == null) return;
            setMessage(Text.literal(HandConfig.NAMES[idx] + ": " + String.format("%.2f", hand.v[idx])));
        }

        @Override
        protected void applyValue() {
            hand.v[idx] = HandConfig.MIN[idx] + (HandConfig.MAX[idx] - HandConfig.MIN[idx]) * (float) this.value;
        }
    }
}
